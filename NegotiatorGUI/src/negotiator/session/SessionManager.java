package negotiator.session;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.DiscreteTimeline;
import negotiator.MultipartyNegotiationEventListener;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.logging.CsvLogger;
import negotiator.logging.SessionLogger;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MediatorProtocol;
import negotiator.protocol.Protocol;

/**
 * The {@link SessionManager} is responsible for enforcing the
 * {@link negotiator.protocol.Protocol} during the {@link Session}. This is the
 * entry point for the negotiation algorithm. The protocol and session
 * parameters are passed on from the GUI.
 *
 * @author David Festen
 */
public class SessionManager {
	// maximum utility history to keep for each session (absolute maximum is
	// int.max)
	public static final int MAX_UTIL_HISTORY = 100000; // 100K

	// Holds the session instance
	private final Session session;

	// Holds the protocol instance
	private final Protocol protocol;

	// Holds a list of participating parties
	private final List<NegotiationParty> parties;

	// Holds parties that are listening to other parties.
	// Key represent the even broadcasting parties, value is a list parties that
	// are listening to
	// the events that are send.
	private final Map<NegotiationParty, List<NegotiationParty>> listeners;

	// Command line interface sessionLogger used to log messages to the cli part
	// of the gui in a single
	// negotiation session
	private SessionLogger sessionLogger;

	// holds a history of all the utilities for the agents. Used for logging
	// purposes
	private List<List<Double[]>> agentUtils;

	// utility of most recent agreements. Only holds sane information in case of
	// agreement.
	private double[][] agreementUtilities;

	// needed for reference (for indexing the parties)
	List<NegotiationParty> agents;

	// Used to silence and restore console output for agents
	private PrintStream orgOut = System.out;
	private PrintStream orgErr = System.err;

	/**
	 * Initializes a new instance of the {@see SessionManager} object. After
	 * initialization this {@see SessionManager} can be {@see #run()}.
	 *
	 * @param parties
	 *            The parties to use in this session (including agents and
	 *            optionally mediators)
	 * @param protocol
	 *            The protocol to use for this session.
	 * @param session
	 *            A session object containing preset information (can also be a
	 *            new instance)
	 */
	public SessionManager(List<NegotiationParty> parties, Protocol protocol,
			Session session) {
		this.session = session;
		this.protocol = protocol;
		this.parties = parties;
		this.listeners = protocol.getActionListeners(parties);
		this.sessionLogger = new SessionLogger(this);
		this.agentUtils = new ArrayList<List<Double[]>>();

		// needed for reference (for indexing the parties)
		agents = MediatorProtocol.getNonMediators(parties);

		// add a util list for each non-mediator agent
		for (int i = 0; i < agents.size(); i++)
			agentUtils.add(new LinkedList<Double[]>());

		agreementUtilities = new double[2][agents.size()];
	}

	/**
	 * Start the negotiation session TODO David: This method has become huge,
	 * needs to be refactored.
	 */
	public void run() throws InvalidActionError, InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException {
		// pre session protocol call
		protocol.beforeSession(session, parties);

		// announce our session to the sessionLogger
		sessionLogger.logSession(session, null);
		sessionLogger.logMessage("Starting negotiation session.");

		// start timers
		session.startTimer();

		// initialize last agreement (which is null at start)
		Bid lastAgreement = null;

		// execute main loop (using the protocol's round structure)
		do {

			// generate new round
			Round round = protocol.getRoundStructure(parties, session);

			// add round to session
			session.startNewRound(round);

			if (checkDeadlineReached())
				break;
			sessionLogger.logMessage("Round %d", session.getRoundNumber());
			int turnNumber = 0;

			// Let each party do an action
			for (Turn turn : round.getTurns()) {
				if (checkDeadlineReached())
					break;
				// for each party, set the round-based timeline again (to avoid
				// tempering)
				if (session.getTimeline() instanceof DiscreteTimeline) {
					((DiscreteTimeline) session.getTimeline())
							.setcRound(session.getRoundNumber());
				}

				turnNumber++;
				NegotiationParty party = turn.getParty();
				Action action = requestAction(party, turn.getValidActions());
				turn.setAction(action);
				updateListeners(party, action);

				// get current party's ids
				int partyId = parties.indexOf(party);

				// update history list if this was an offer
				if (action instanceof Offer) {

					Bid currentAgreement = protocol.getCurrentAgreement(
							session, parties);

					for (NegotiationParty agent : agents) {
						int agentId = agents.indexOf(agent);
						Double[] entry = {
								session.getRoundNumber()
										+ session.getTurnNumber() / 10d,
								agent.getUtility(((Offer) action).getBid()) };
						if (agentUtils.get(0).size() < MAX_UTIL_HISTORY)
							agentUtils.get(agentId).add(entry);

						if (currentAgreement == null || lastAgreement == null
								|| !currentAgreement.equals(lastAgreement)) {
							agreementUtilities[0][agentId] = entry[0];
							agreementUtilities[1][agentId] = entry[1];
						}
					}
					if (currentAgreement == null || lastAgreement == null
							|| !currentAgreement.equals(lastAgreement))
						lastAgreement = currentAgreement;
				}

				// log messages
				sessionLogger.logMessage("  Turn %d: %-13s %s", turnNumber,
						party, action);
				sessionLogger.logBid(session, parties,
						protocol.getCurrentAgreement(session, parties) != null);

				// Do not start new turn in current round if protocol is
				// finished at this point
				if (protocol.isFinished(session, parties)) {
					break;
				}
			}
			if (checkDeadlineReached())
				break;

		}
		// run main loop while protocol does not say it is finished
		while (!protocol.isFinished(session, parties)
				&& !checkDeadlineReached());

		// stop timers if running
		if (session.isTimerRunning())
			session.stopTimer();

		// post session protocol call
		protocol.afterSession(session, parties);

		// log result messages;
		Bid agreement = protocol.getCurrentAgreement(session, parties);
		if (agreement == null)
			sessionLogger.logMessage("No agreement found.");
		else {
			sessionLogger.logMessage("Found an agreement: %s", agreement);
			agreementUtilities = new double[2][agents.size()];
			for (NegotiationParty agent : agents) {
				int agentId = agents.indexOf(agent);
				double when = findLastIndexOfBid(agreement, session);
				Double[] entry = {
						Double.isNaN(when) ? session.getRoundNumber() : when,
						agent.getUtility(agreement) };
				agreementUtilities[0][agentId] = entry[0];
				agreementUtilities[1][agentId] = entry[1];
			}

		}
		double runTime = session.getRuntimeInSeconds();
		sessionLogger.logMessage("Finished negotiation session in %.3fs",
				runTime);
		try {
			sessionLogger.logSession(session, agreement);
			sessionLogger.logMessage(CsvLogger.logSingleSession(session,
					protocol, agents, runTime));
		} catch (Exception e) {
			sessionLogger.logMessage("Error: could not log session details");
		}
	}

	private boolean checkDeadlineReached() {
		// look at the time, if this is over time, remove last round and count
		// previous round
		// as most recent round
		if (session.isDeadlineReached()) {
			System.out.println("Deadline reached. " + session.getDeadlines());
			session.getRounds().remove(session.getRounds().size() - 1);
			if (session.getDeadlines().containsKey(DeadlineType.TIME)) {
				double runTimeInSeconds = (Integer) session.getDeadlines().get(
						DeadlineType.TIME);
				session.setRuntimeInSeconds(runTimeInSeconds);
			}
			sessionLogger.logMessage("Deadline reached: %s",
					session.getDeadlines());
			return true;
		}
		return false;
	}

	/**
	 * Request an {@link Action} from the
	 * {@link negotiator.parties.NegotiationParty} given a list of valid actions
	 * and apply it according to
	 * {@link negotiator.protocol.Protocol#applyAction(Action, Session)}
	 *
	 * @param party
	 *            The party to request an action of
	 * @param validActions
	 *            the actions the party can choose
	 * @return the chosen action-
	 */
	private Action requestAction(final NegotiationParty party,
			final ArrayList<Class> validActions) throws InvalidActionError,
			InterruptedException, ExecutionException,
			NegotiationPartyTimeoutException {

		// choose action
		Action action = protocol.getTimeOutInSeconds(session) <= 0 ? party
				.chooseAction(validActions) : chooseActionWithTimeOut(party,
				validActions);

		// Check if action is valid for this protocol
		boolean isValid = validActions.contains(action.getClass());

		// if its not, throw an error
		if (!isValid)
			throw new InvalidActionError(party);

		// execute action according to protocol
		protocol.applyAction(action, session);

		// return the chosen action
		return action;
	}

	/**
	 * Update all parties in the listeners map with the new action
	 *
	 * @param actionOwner
	 *            The Party that initiated the action
	 * @param action
	 *            The action it did.
	 */
	private void updateListeners(NegotiationParty actionOwner, Action action) {
		// Sadly not even the listener object was created, so don't bother
		if (listeners == null)
			return;

		// if anyone is listening, notify any and all observers
		if (listeners.get(actionOwner) != null)
			for (NegotiationParty observers : listeners.get(actionOwner))
				observers.receiveMessage(actionOwner, action);
	}

	/**
	 * Adds a listener to the logging events. currently used to attach the cli
	 * listener
	 *
	 * @param eventListener
	 *            The instance listening to logging events
	 */
	public void addLoggingListener(
			MultipartyNegotiationEventListener eventListener) {
		sessionLogger.addListener(eventListener);
	}

	/**
	 * Get the history of the utilities for all agents so far. Outer list is a
	 * list of agent id's and utilities inner list is a list of double[2] where
	 * the first double identifies the round and the second double identifies
	 * the util.
	 *
	 * Example (History of two agents with two rounds): list[0][0] = {1, 0.90}
	 * list[0][1] = {2, 0.60} list[1][0] = {1, 0.70} list[1][1] = {2, 0.80} ^ ^
	 * ^ ^ agent ___| | | | round id ___| | | round number _____| | round
	 * utility _________|
	 *
	 * @return Utility history
	 */
	public List<List<Double[]>> getAgentUtils() {
		return agentUtils;
	}

	/**
	 * Returns round number and utility value of agreement if any. If no
	 * agreement this function is undefined.
	 *
	 * result[0][0] = 2.1 <-- round number result[0][1] = 2.1 <-- round number
	 * result[1][0] = 0.40 <-- utility result[1][1] = 0.80 <-- utility
	 *
	 * @return The round number and utility of agreement if any.
	 */
	public double[][] getAgreementUtilities() {
		return agreementUtilities;
	}

	// Finds the last index of the bid in the session or NaN if not found.
	private static double findLastIndexOfBid(Bid needle, Session session) {
		for (int roundIndex = session.getRounds().size() - 1; roundIndex >= 0; roundIndex--) {
			Round round = session.getRounds().get(roundIndex);
			for (int actionIndex = round.getActions().size() - 1; actionIndex >= 0; actionIndex--) {
				Action action = round.getActions().get(actionIndex);
				if (action instanceof Offer
						&& ((Offer) action).getBid().equals(needle))
					return (roundIndex + 1) + (actionIndex + 1) / 10d;
			}
		}
		return Double.NaN;
	}

	private Action chooseActionWithTimeOut(final NegotiationParty party,
			final ArrayList<Class> validActions) throws InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		int timeLeft = Integer.MAX_VALUE;
		if (session.getDeadlines().containsKey(DeadlineType.TIME)) {
			timeLeft = (int) (((Integer) session.getDeadlines().get(
					DeadlineType.TIME)
					* (1 - session.getTimeline().getTime()) + 1));
		}

		int timeOut = Math.min(protocol.getTimeOutInSeconds(session), timeLeft);
		if (session.getDeadlines().containsKey(DeadlineType.TIME)) {
			timeOut = Math
					.min(timeOut,
							(Integer) session.getDeadlines().get(
									DeadlineType.TIME) + 1);
		}
		Future<Action> future = executor.submit(new Callable<Action>() {
			public Action call() throws Exception {
				return party.chooseAction(validActions);
			}
		});
		try {
			return future.get(timeOut, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			String msg = String.format(
					"Negotiating party %s timed out. {TIMEOUT=%ds}",
					party.getPartyId(), timeOut);
			sessionLogger.logMessage(msg);
			throw new NegotiationPartyTimeoutException(party, e);
		} finally {
			executor.shutdownNow();
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
