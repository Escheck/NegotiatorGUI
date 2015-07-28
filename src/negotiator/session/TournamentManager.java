package negotiator.session;

import static java.lang.String.format;
import static misc.ConsoleHelper.useConsoleOut;
import static misc.Time.prettyTimeSpan;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.config.GuiConfiguration;
import negotiator.config.MultilateralTournamentConfiguration;
import negotiator.events.AgreementEvent;
import negotiator.events.NegotiationEvent;
import negotiator.events.SessionFailedEvent;
import negotiator.events.SessionStartedEvent;
import negotiator.events.TournamentEndedEvent;
import negotiator.events.TournamentStartedEvent;
import negotiator.exceptions.AnalysisException;
import negotiator.exceptions.InstantiateException;
import negotiator.exceptions.NegotiatorException;
import negotiator.parties.NegotiationParty;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.protocol.MediatorProtocol;
import negotiator.protocol.MultilateralProtocol;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.tournament.TournamentGenerator;
import negotiator.utility.TournamentIndicesGenerator;

/**
 * Manages a multi-lateral tournament and makes sure that the
 * {@link negotiator.session.SessionManager} are instantiated. It uses the
 * configuration object which is created by the user interface and extracts
 * individual session from configuration object which it wil pass on to the
 * session manager.
 * 
 * <p>
 * Agents in a tournament must be of class {@link NegotiationParty}.
 */
public class TournamentManager extends Thread {

	/**
	 * Holds the configuration used by this tournament manager
	 */
	private MultilateralTournamentConfiguration configuration;

	/**
	 * Used to silence and restore console output for agents
	 */
	PrintStream orgOut = System.out;
	PrintStream orgErr = System.err;

	/**
	 * Used for printing time related console output.
	 */
	long start = System.nanoTime();

	/**
	 * Listeners to events in the tournament.
	 */
	private List<MultipartyNegotiationEventListener> listeners = new ArrayList<MultipartyNegotiationEventListener>();

	/**
	 * Initializes a new instance of the
	 * {@link negotiator.session.TournamentManager} class. The tournament
	 * manager uses the provided configuration to find which sessions to run and
	 * how many collections of these sessions (tournaments) to run.
	 *
	 * @param config
	 *            The configuration to use for this Tournament
	 */
	public TournamentManager(GuiConfiguration config) {
		configuration = config;
	}

	/****************** listener support *******************/
	public void addEventListener(MultipartyNegotiationEventListener listener) {
		listeners.add(listener);
	}

	public void removeEventListener(MultipartyNegotiationEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify all our liteners of a negotiation event that occured.
	 * 
	 * @param evt
	 */
	private void notifyEvent(NegotiationEvent evt) {
		for (MultipartyNegotiationEventListener l : listeners) {
			try {
				l.handleEvent(evt);
			} catch (Throwable e) {
				e.printStackTrace(); // we can't do much here if handler fails.
			}
		}
	}

	/****************** manager *****************************/

	/**
	 * Runnable implementation for thread
	 */
	@Override
	public void run() {
		start = System.nanoTime();
		try {
			this.runTournament();
			System.out.println("Tournament completed");
			System.out.println("------------------");
			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Tournament exited with an error");
			System.out.println("------------------");
			System.out.println("");
		}
		long end = System.nanoTime();
		System.out.println("Run finished in " + prettyTimeSpan(end - start));
	}

	/**
	 * Run the complete tournament the number of times provided by the
	 * configuration file
	 *
	 * @throws Exception
	 */
	public void runTournament() throws Exception {

		for (int tournamentNumber = 0; tournamentNumber < configuration
				.getNumTournaments(); tournamentNumber++) {

			TournamentIndicesGenerator indicesGenerator = new TournamentIndicesGenerator(
					configuration.getNumAgentsPerSession(), configuration
							.getPartyProfileItems().size(),
					configuration.getRepetitionAllowed(), configuration
							.getPartyItems().size());
			TournamentGenerator generator = new TournamentGenerator(
					configuration, indicesGenerator);

			runSessions(tournamentNumber, generator);

		}
		System.out.println("All tournament sessions are done");
	}

	/**
	 * Run all sessions in the given generator.
	 * 
	 * @param tournamentNumber
	 * @param generator
	 */
	private void runSessions(int tournamentNumber,
			final TournamentGenerator generator) {
		int sessionNumber = 0;
		int totalSessions = generator.numSessionsPerTournament();
		notifyEvent(new TournamentStartedEvent(this, tournamentNumber,
				totalSessions));

		while (generator.hasNext()) {

			String errormessage = null; // null=all OK.
			List<NegotiationPartyInternal> partyList = null;
			List<NegotiationPartyInternal> agentList = null; // null=bad

			sessionNumber++;
			notifyEvent(new SessionStartedEvent(this, sessionNumber,
					totalSessions));

			StringBuilder logline = new StringBuilder("" + sessionNumber);
			ExecutorWithTimeout executor = new ExecutorWithTimeout(
					1000 * configuration.getDeadline()
							.getTimeOrDefaultTimeout());
			try {
				partyList = getPartyList(executor, generator);
			} catch (TimeoutException e) {
				System.err.println("failed to construct agent due to timeout:"
						+ e.getMessage());
				notifyEvent(new SessionFailedEvent(this, e,
						"failed to construct agent due to timeout"));
				continue; // do not run any further if we don't have the agents.
			} catch (ExecutionException e) {
				e.printStackTrace();
				notifyEvent(new SessionFailedEvent(this, e,
						"failed to construct agent"));
				continue; // do not run any further if we don't have the agents.
			}

			agentList = MediatorProtocol.getNonMediators(partyList);
			Exception e = runSingleSession(partyList, executor);
			if (e != null) {
				notifyEvent(new SessionFailedEvent(this, e,
						"failure while running session"));
				continue;
			}

			System.out.println(errormessage != null ? "Session done."
					: "Session exited.");
			int nDone = totalSessions * tournamentNumber + sessionNumber;
			int nRemaining = totalSessions * configuration.getNumTournaments()
					- nDone;
			System.out.println(format("approx. %s remaining",
					prettyTimeSpan(estimatedTimeRemaining(nDone, nRemaining))));
			System.out.println("");

		}
		notifyEvent(new TournamentEndedEvent(this));
	}

	/**
	 * Generate the parties involved in the next round of the tournament
	 * generator. Assumes generator.hasNext(). <br>
	 * Checks various error cases and reports accordingly. If repository fails
	 * completely, we call System.exit(). useConsoleOut is called to disable
	 * console output while running agent code. <br>
	 * 
	 * @param executor
	 * @param generator
	 * @return list of parties for next round. May return null if one or more
	 *         agents could not be created.
	 * @throws TimeoutException
	 *             if we run out of time during the construction.
	 * @throws ExecutionException
	 *             if one of the agents does not construct properly
	 */
	private List<NegotiationPartyInternal> getPartyList(
			ExecutorWithTimeout executor, final TournamentGenerator generator)
			throws TimeoutException, ExecutionException {
		List<NegotiationPartyInternal> partyList = null;
		useConsoleOut(false);
		try {
			partyList = executor.execute("manager",
					new Callable<List<NegotiationPartyInternal>>() {

						@Override
						public List<NegotiationPartyInternal> call()
								throws RepositoryException, NegotiatorException {
							return generator.next();
						}
					});
		} finally {
			useConsoleOut(true);
		}
		return partyList;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Run a single session for the given parties (protocol and session are also
	 * used, but extracted from the tournament manager's configuration
	 *
	 * @param parties
	 *            the parties to run the tournament for
	 * @throws Exception
	 *             if some of the required data could not be extracted
	 * 
	 * @return If all goes ok, returns null. Else, Exception that occured during
	 *         execution.
	 */
	public Exception runSingleSession(List<NegotiationPartyInternal> parties,
			ExecutorWithTimeout executor) {
		Exception exception = null;

		try {
			MultilateralProtocol protocol = getProtocol();
			Session session = parties.get(0).getSession();

			// TODO: ** hackery ** we should make sure that session gives
			// timeline to agents, not the other way around.
			Timeline timeline = parties.get(0).getTimeLine();
			session.setTimeline(timeline);
			SessionManager sessionManager = new SessionManager(parties,
					protocol, session, executor);
			useConsoleOut(false);
			sessionManager.run();
			useConsoleOut(true);

			try {
				double runTime = session.getRuntimeInSeconds();
				List<NegotiationPartyInternal> agentList = MediatorProtocol
						.getNonMediators(parties);
				notifyEvent(new AgreementEvent(this, session, protocol,
						agentList, runTime));
			} catch (Error e) {
				exception = new AnalysisException(
						"Unknown error in analyses or logging", e);
			}
		} catch (Exception e) {
			exception = e;
		}

		useConsoleOut(true);
		if (exception != null) {
			exception.printStackTrace();
		}
		return exception;
	}

	/**
	 * Calculate estimated time remaining using extrapolation
	 *
	 * @return estimation of time remaining in nano seconds
	 */
	private double estimatedTimeRemaining(int nSessionsDone,
			int nSessionsRemaining) {
		long now = System.nanoTime() - start;
		double res = nSessionsRemaining * now / (double) nSessionsDone;
		return res;

		// return nSessionsRemaining * (System.nanoTime() / start) /
		// (double)nSessionsDone;
	}

	/**
	 * Create a new instance of the Protocol object from a
	 * {@link MultiPartyProtocolRepItem}
	 *
	 * @param protocolRepItem
	 *            Item to create Protocol out of
	 * @return the created protocol.
	 * @throws InstantiateException
	 *             if failure occurs while constructing the rep item.
	 */
	public MultilateralProtocol getProtocol() throws InstantiateException {

		MultiPartyProtocolRepItem protocolRepItem = configuration
				.getProtocolItem();

		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class protocolClass;
		try {
			protocolClass = loader.loadClass(protocolRepItem.getClassPath());

			@SuppressWarnings("unchecked")
			Constructor protocolConstructor = protocolClass.getConstructor();

			return (MultilateralProtocol) protocolConstructor.newInstance();
		} catch (Exception e) {
			throw new InstantiateException("failed to instantiate "
					+ protocolRepItem, e);
		}

	}

}
