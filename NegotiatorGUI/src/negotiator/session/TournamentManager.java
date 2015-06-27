package negotiator.session;

import static java.lang.String.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.Timeline;
import negotiator.config.Configuration;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;
import negotiator.exceptions.AnalysisException;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.exceptions.NegotiatorException;
import negotiator.gui.progress.MultipartyNegoEventLogger;
import negotiator.logging.CsvLogger;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MediatorProtocol;
import negotiator.protocol.MultilateralProtocol;
import negotiator.tournament.TournamentGenerator;

/**
 * Manages the tournament and makes sure that the
 * {@link negotiator.session.SessionManager} are instantiated. It uses the
 * configuration object which is created by the user interface and extracts
 * individual session from configuration object which it wil pass on to the
 * session manager.
 */
public class TournamentManager extends Thread {

	/**
	 * Holds the configuration used by this tournament manager
	 */
	private Configuration configuration;

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
	public TournamentManager(Configuration config) {
		configuration = config;
	}

	/****************** listener support *******************/
	public void addEventListener(MultipartyNegotiationEventListener listener) {
		listeners.add(listener);
	}

	public void removeEventListener(MultipartyNegotiationEventListener listener) {
		listeners.remove(listener);
	}

	public void notifyOfferEvent(MultipartyNegotiationOfferEvent evt) {
		for (MultipartyNegotiationEventListener l : listeners) {
			try {
				l.handleOfferActionEvent(evt);
			} catch (Throwable e) {
				e.printStackTrace(); // we can't do much here if handler fails.
			}
		}
	}

	/**
	 * Notify all listeners of given LogMessageEvent.
	 * 
	 * @param evt
	 *            event to report.
	 */
	public void notifyMessageEvent(LogMessageEvent evt) {
		for (MultipartyNegotiationEventListener l : listeners) {
			try {
				l.handleLogMessageEvent(evt);
			} catch (Throwable e) {
				e.printStackTrace(); // we can't do much here if handler fails.
			}
		}
	}

	/**
	 * notify given string message to listeners.
	 * 
	 * @param message
	 *            the message to send.
	 */
	public void notifyMessageEvent(String message) {
		notifyMessageEvent(new LogMessageEvent(this, "TournamentManager",
				message));
	}

	public void notifyNegotiationEvent(MultipartyNegotiationSessionEvent evt) {
		for (MultipartyNegotiationEventListener l : listeners) {
			try {
				l.handleMultipartyNegotiationEvent(evt);
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

		/**
		 * FIXME in all other cases, the logging is done by the GUI. We may have
		 * to rethink why this is here.
		 */
		MultipartyNegoEventLogger myLogger = new MultipartyNegoEventLogger(
				configuration.getPartyProfileItems().get(0).getDomain()
						.getName(), configuration.getNumAgentsPerSession());
		addEventListener(myLogger);

		for (int tournamentNumber = 0; tournamentNumber < configuration
				.getNumTournaments(); tournamentNumber++) {

			// Generator for parties (don't generate them up-front as these can
			// be many)
			TournamentGenerator generator = configuration.getPartiesGenerator();

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
		int totalSessions = configuration.numSessionsPerTournament();
		while (generator.hasNext()) {

			String errormessage = null; // null=all OK.
			List<NegotiationParty> partyList = null;
			List<NegotiationParty> agentList = null; // null=bad

			sessionNumber++;
			System.out.println(format(
					"Starting tournament %d/%d, session %d/%d",
					tournamentNumber + 1, configuration.getNumTournaments(),
					sessionNumber, totalSessions));

			StringBuilder logline = new StringBuilder("" + sessionNumber);
			ExecutorWithTimeout executor = new ExecutorWithTimeout(
					1000 * configuration.getSession().getDeadlines()
							.getTimeOrDefaultTimeout());

			try {
				partyList = getPartyList(executor, generator);
			} catch (TimeoutException e) {
				System.err.println("failed to construct agent due to timeout:"
						+ e.getMessage());
			}

			if (partyList == null) {
				errormessage = "ERR in init";
				System.out
						.println("Error on initializing one or more of the agent");
			} else {

				agentList = MediatorProtocol.getNonMediators(partyList);
				System.out.println("Running session");
				if (!runSingleSession(partyList, executor)) {
					errormessage = "ERR in session";
				}
			}

			// we need to log anyway, even if there is no agentList.
			if (errormessage != null) {
				logline.append(errormessage);
				for (int i = 0; i < 11; i++)
					logline.append(""); // FIXME what's this doing??
				if (agentList != null) {
					for (NegotiationParty agent : agentList)
						logline.append(agent.getPartyId().toString());
				}
				notifyMessageEvent(logline.toString());
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
	 */
	private List<NegotiationParty> getPartyList(ExecutorWithTimeout executor,
			final TournamentGenerator generator) throws TimeoutException {
		List<NegotiationParty> partyList = null;
		useConsoleOut(false);
		try {
			partyList = executor
					.execute(new Callable<List<NegotiationParty>>() {

						@Override
						public List<NegotiationParty> call()
								throws RepositoryException, NegotiatorException {
							return generator.next();
						}
					});
		} catch (ExecutionException e) {
			useConsoleOut(true);
			Throwable inner = e.getCause();
			System.err.println(inner.getMessage());
			if (inner instanceof RepositoryException) {
				System.err
						.println("fatal: something wrong with the repository");
				e.printStackTrace();
				System.exit(1);
			}
			// otherwise, we fall out and partyList may remain null.
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
	 */
	public boolean runSingleSession(List<NegotiationParty> parties,
			ExecutorWithTimeout executor) {
		try {

			MultilateralProtocol protocol = configuration.getProtocol();
			Session session = configuration.getSession().copy();

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
				List<NegotiationParty> agentList = MediatorProtocol
						.getNonMediators(parties);
				notifyMessageEvent(CsvLogger.getDefaultSessionLog(session,
						protocol, agentList, runTime));

				return true;
			} catch (Error e) {
				throw new AnalysisException(
						"Unknown error in analyses or logging");
			}
		} catch (InvalidActionError invalidActionError) {
			useConsoleOut(true);
			System.err
					.println("Agent performed invalid action, skipping current session.");
			return false;
		} catch (NegotiationPartyTimeoutException e) {
			useConsoleOut(true);
			System.err.println(e.getMessage());
			return false;
		} catch (InterruptedException e) {
			useConsoleOut(true);
			// thrown when trying to cut off long running agents
			return false;
		} catch (ExecutionException e) {
			useConsoleOut(true);
			System.err
					.println("Thread or thread execution interrupted, skipping current session.");
			return false;
		} catch (Exception e) {
			useConsoleOut(true);
			System.err
					.println("Agent caused an unknown exception, skipping current session.");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Silences or restores the console output. This can be useful to suppress
	 * output of foreign code, like submitted agents
	 * 
	 * FIXME redundant code, copy of SessionManager#useConsoleOut.
	 *
	 * @param enable
	 *            Enables console output if set to true or disables it when set
	 *            to false
	 */
	private void useConsoleOut(boolean enable) {

		if (enable) {
			System.setErr(orgErr);
			System.setOut(orgOut);
		} else {
			System.setOut(new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException { /* no-op */
				}
			}));
			System.setErr(new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException { /* no-op */
				}
			}));
		}
	}

	private static final int BILLION = 1000000000;
	private static final int DAY = 86400;
	private static final int HOUR = 3600;
	private static final int MINUTE = 60;

	private String prettyTimeSpan(double nanoTime) {
		int t = (int) Math.floor(nanoTime / BILLION);
		double ms = nanoTime / BILLION - t;
		String prettyTimeSpan = "";

		int days = t / DAY;
		int hours = t % DAY / HOUR;
		int minutes = t % DAY % HOUR / MINUTE;
		int seconds = t % DAY % HOUR % MINUTE;

		if (days == 1)
			prettyTimeSpan += format("%d day, ", days);
		if (days > 1)
			prettyTimeSpan += format("%d days, ", days);
		if (hours == 1)
			prettyTimeSpan += format("%d hour, ", hours);
		if (hours > 1)
			prettyTimeSpan += format("%d hours, ", hours);
		if (minutes == 1)
			prettyTimeSpan += format("%d minute, ", minutes);
		if (minutes > 1)
			prettyTimeSpan += format("%d minutes, ", minutes);
		if (seconds == 1)
			prettyTimeSpan += format("%d second", seconds);
		else
			prettyTimeSpan += format("%d seconds", seconds);

		return prettyTimeSpan;
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
}
