package negotiator.session;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import negotiator.Timeline;
import negotiator.config.MultilateralTournamentConfiguration;
import negotiator.exceptions.AnalysisException;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.logging.CsvLogger;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MediatorProtocol;
import negotiator.protocol.Protocol;

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
	private MultilateralTournamentConfiguration configuration;

	/**
	 * Logger for the results
	 */
	private CsvLogger logger;

	/**
	 * Used to silence and restore console output for agents
	 */
	PrintStream orgOut = System.out;
	PrintStream orgErr = System.err;

	/**
	 * Initializes a new instance of the
	 * {@link negotiator.session.TournamentManager} class. The tournament
	 * manager uses the provided configuration to find which sessions to run and
	 * how many collections of these sessions (tournaments) to run.
	 *
	 * @param config
	 *            The configuration to use for this Tournament
	 */
	public TournamentManager(MultilateralTournamentConfiguration config) {
		configuration = config;
	}

	/**
	 * Runnable implementation for thread
	 */
	@Override
	public void run() {
		long start = System.nanoTime();
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
		System.out.println("run took time  " + (end - start) / 1000000000.
				+ "s");
	}

	/**
	 * Run the complete tournament the number of times provided by the
	 * configuration file
	 *
	 * @throws Exception
	 */
	public void runTournament() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		logger = new CsvLogger(String.format("logs/Log-Tournament-%s.csv",
				dateFormat.format(new Date())));

		// Initialize this session to zero (used for logging only)
		boolean printedHeader = false;

		for (int tournamentNumber = 0; tournamentNumber < configuration
				.getNumTournaments(); tournamentNumber++) {
			int sessionNumber = 0;

			// Generator for parties (don't generate them up-front as these can
			// be many)
			Iterable<List<NegotiationParty>> generator = configuration
					.getPartiesGenerator();

			useConsoleOut(false);
			// enumerate all parties
			for (List<NegotiationParty> partyList : generator) {

				useConsoleOut(true);

				// if could not create parties. skip this session
				if (partyList == null) {
					System.out
							.println("Error on initializing one or more of the agent");
					continue;
				}

				List<NegotiationParty> agentList = MediatorProtocol
						.getNonMediators(partyList);

				if (!printedHeader) {
					logger.logLine(CsvLogger.getDefaultHeader(agentList));
					printedHeader = true;
				}

				System.out.println(String.format(
						"Running tournament %d/%d, session %d ",
						tournamentNumber + 1,
						configuration.getNumTournaments(), ++sessionNumber));
				logger.log(sessionNumber);
				boolean sessionOk = runSingleSession(partyList);
				if (!sessionOk) {
					logger.log("ERROR");
					for (int i = 0; i < 11; i++)
						logger.log("");
					for (NegotiationParty agent : agentList)
						logger.log(agent.getPartyId().toString());
					logger.logLine();
				}
				System.out.println(sessionOk ? "Session done."
						: "Session exited.");
				System.out.println("");

				useConsoleOut(false);
			}
			useConsoleOut(true);

		}
		System.out.println("All tournament sessions are done");
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
	public boolean runSingleSession(List<NegotiationParty> parties)
			throws Exception {
		try {

			Protocol protocol = configuration.getProtocol();
			Session session = configuration.getSession().copy();
			ExecutorWithTimeout executor = new ExecutorWithTimeout(
					1000 * session.getDeadlines().getTimeOrDefaultTimeout());

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
				logger.logLine(CsvLogger.logDefaultSession(session, protocol,
						agentList, runTime));
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
			String msg = String.format(
					"Agent %s timed out, skipping current session.", e
							.getInstigator().getPartyId());
			System.err.println(msg);
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
}
