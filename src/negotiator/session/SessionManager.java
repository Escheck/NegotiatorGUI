package negotiator.session;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.DiscreteTimeline;
import negotiator.MultipartyNegotiationEventListener;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.logging.CsvLogger;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.protocol.MediatorProtocol;
import negotiator.protocol.MultilateralProtocol;

/**
 * The {@link SessionManager} is responsible for enforcing the
 * {@link MultilateralProtocol} during the {@link Session}. This is the entry
 * point for the negotiation algorithm. The protocol and session parameters are
 * passed on from the GUI.
 *
 * @author David Festen
 * 
 */
public class SessionManager implements Runnable {
	// maximum utility history to keep for each session (absolute maximum is
	// int.max)
	public static final int MAX_UTIL_HISTORY = 100000; // 100K

	private final Session session;

	private final MultilateralProtocol protocol;

	// participating parties
	private final List<NegotiationPartyInternal> parties;

	private SessionEventHandler events;

	// holds a history of all the utilities for the agents. Used for plotting
	// purposes
	private List<List<Double[]>> agentUtilsDiscounted;

	// utility of most recent agreements. Only holds sane information in case of
	// agreement.
	private double[][] agreementUtilitiesDiscounted;

	// needed for reference (for indexing the parties)
	List<NegotiationPartyInternal> agents;

	private ExecutorWithTimeout executor;

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
	public SessionManager(List<NegotiationPartyInternal> parties,
			MultilateralProtocol protocol, Session session,
			ExecutorWithTimeout exec) {
		this.session = session;
		this.protocol = protocol;
		this.parties = parties;
		this.events = new SessionEventHandler(this);
		this.agentUtilsDiscounted = new ArrayList<List<Double[]>>();
		this.executor = exec;

		protocol.setExecutor(exec);

		// needed for reference (for indexing the parties)
		agents = MediatorProtocol.getNonMediators(parties);

		// add a util list for each non-mediator agent
		for (int i = 0; i < agents.size(); i++)
			agentUtilsDiscounted.add(new LinkedList<Double[]>());

		agreementUtilitiesDiscounted = new double[2][agents.size()];
	}

	/**
	 * Run and wait for completion. Can be used from a thread. Throws from the
	 * underlying call are redirected into a {@link RuntimeException}.
	 */
	public void run() {
		try {
			runAndWait();
		} catch (Exception e) {
			throw new RuntimeException("Run failed:" + e.getMessage(), e);
		}
	}

	/**
	 * Runs the negotiation session and wait for it to complete.
	 * 
	 * TODO David: This method has become huge, needs to be refactored.
	 */
	public void runAndWait() throws InvalidActionError, InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException {
		System.gc();
		// pre session protocol call
		protocol.beforeSession(session, parties);

		// announce our session to the sessionLogger
		events.logSession(session, null);
		events.logMessage("Starting negotiation session.");

		// start timers
		session.startTimer();

		// initialize last agreement (which is null at start)
		// Bid lastAgreement = null;

		// execute main loop (using the protocol's round structure)
		do {

			// generate new round
			Round round = protocol.getRoundStructure(parties, session);

			// add round to session
			session.startNewRound(round);

			if (checkDeadlineReached())
				break;
			events.logMessage("Round %d", session.getRoundNumber());
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
				NegotiationPartyInternal party = turn.getParty();
				Action action = requestAction(party, turn.getValidActions());
				turn.setAction(action);
				updateListeners(party, action);

				// get current party's ids
				int partyId = parties.indexOf(party);

				events.logMessage("  Turn %d: %-13s %s", turnNumber, party,
						action);

				// update history list if this was an offer
				if (action instanceof Offer) {

					Bid currentAgreement = protocol.getCurrentAgreement(
							session, parties);

					for (NegotiationPartyInternal agent : agents) {
						int agentId = agents.indexOf(agent);
						Double[] entry = {
								session.getRoundNumber()
										+ session.getTurnNumber() / 10d,
								agent.getUtilityWithDiscount(((Offer) action)
										.getBid()) };
						if (agentUtilsDiscounted.get(0).size() < MAX_UTIL_HISTORY)
							agentUtilsDiscounted.get(agentId).add(entry);

						// if (currentAgreement == null || lastAgreement == null
						// || !currentAgreement.equals(lastAgreement)) {
						// agreementUtilitiesDiscounted[0][agentId] = entry[0];
						// agreementUtilitiesDiscounted[1][agentId] = entry[1];
						// }
					}
					// if (currentAgreement == null || lastAgreement == null
					// || !currentAgreement.equals(lastAgreement))
					// lastAgreement = currentAgreement;

					events.offered(parties, ((Offer) action).getBid(),
							currentAgreement != null, session);

				}

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
			events.logMessage("No agreement found.");
		else {
			events.logMessage("Found an agreement: %s", agreement);
			agreementUtilitiesDiscounted = new double[2][agents.size()];
			for (NegotiationPartyInternal agent : agents) {
				int agentId = agents.indexOf(agent);
				double when = findLastIndexOfBid(agreement, session);
				Double[] entry = {
						Double.isNaN(when) ? session.getRoundNumber() : when,
						agent.getUtilityWithDiscount(agreement) };
				agreementUtilitiesDiscounted[0][agentId] = entry[0];
				agreementUtilitiesDiscounted[1][agentId] = entry[1];
			}

		}
		double runTime = session.getRuntimeInSeconds();
		events.logMessage("Finished negotiation session in %.3fs", runTime);
		try {
			events.logSession(session, agreement);
			events.logMessage(CsvLogger.logSingleSession(session, protocol,
					agents, runTime));
		} catch (Exception e) {
			events.logMessage("Error: could not log session details");
		}
	}

	private boolean checkDeadlineReached() {
		// look at the time, if this is over time, remove last round and count
		// previous round
		// as most recent round
		if (session.isDeadlineReached()) {
			System.out.println("Deadline reached. " + session.getDeadlines());
			session.getRounds().remove(session.getRounds().size() - 1);
			if (session.getDeadlines().getType() == DeadlineType.TIME) {
				double runTimeInSeconds = (Integer) session.getDeadlines()
						.getValue();
				session.setRuntimeInSeconds(runTimeInSeconds);
			}
			events.logMessage("Deadline reached: %s", session.getDeadlines());
			return true;
		}
		return false;
	}

	/**
	 * Request an {@link Action} from the
	 * {@link negotiator.parties.NegotiationParty} given a list of valid actions
	 * and apply it according to
	 * {@link MultilateralProtocol#applyAction(Action, Session)}
	 *
	 * @param party
	 *            The party to request an action of
	 * @param validActions
	 *            the actions the party can choose
	 * @return the chosen action-
	 * @throws TimeoutException
	 */
	private Action requestAction(final NegotiationPartyInternal party,
			final List<Class<? extends Action>> validActions)
			throws InvalidActionError, InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException {

		Action action;
		try {
			action = executor.execute(party.getPartyId().toString(),
					new Callable<Action>() {
						@Override
						public Action call() throws Exception {
							// NegotiationParty still has sloppy type checking.
							ArrayList<Class<? extends Action>> actions = new ArrayList<Class<? extends Action>>();
							actions.addAll(validActions);
							return party.getParty().chooseAction(actions);
						}
					});
		} catch (TimeoutException e) {
			String msg = String.format(
					"Negotiating party %s timed out in chooseAction() method.",
					party.getPartyId());
			events.logMessage(msg);
			throw new NegotiationPartyTimeoutException(party, msg, e);
		}

		// if its not a valid action, throw an error
		if (action == null || !validActions.contains(action.getClass())) {
			throw new InvalidActionError(party);
		}

		// execute action according to protocol
		protocol.applyAction(action, session);

		// return the chosen action
		return action;
	}

	/**
	 * Update all parties in the listeners map with the new action. Has to be
	 * here since interface does not deal with implementation details (which is
	 * correct)
	 *
	 * @param actionOwner
	 *            The Party that initiated the action
	 * @param action
	 *            The action it did.
	 */
	private void updateListeners(final NegotiationPartyInternal actionOwner,
			final Action action) throws NegotiationPartyTimeoutException,
			ExecutionException, InterruptedException {
		Map<NegotiationPartyInternal, List<NegotiationPartyInternal>> listeners = protocol
				.getActionListeners(parties);

		// Sadly not even the listener object was created, so don't bother
		if (listeners == null)
			return;

		// if anyone is listening, notify any and all observers
		if (listeners.get(actionOwner) != null)
			for (final NegotiationPartyInternal observer : listeners
					.get(actionOwner)) {
				try {
					executor.execute(actionOwner.getPartyId().toString(),
							new Callable<Object>() {
								@Override
								public Object call() {
									observer.getParty().receiveMessage(
											actionOwner.getPartyId(), action);
									return null;
								}
							});
				} catch (TimeoutException e) {
					String msg = String
							.format("Negotiating party %s timed out in receiveMessage() method.",
									observer.getPartyId());
					events.logMessage(msg);
					throw new NegotiationPartyTimeoutException(observer, msg, e);
				}
			}
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
		events.addListener(eventListener);
	}

	/**
	 * Get the history of the (discounted) utilities for all agents so far.
	 * Outer list is a list of agent id's and utilities inner list is a list of
	 * double[2] where the first double identifies the round and the second
	 * double identifies the util.
	 *
	 * Example (History of two agents with two rounds): list[0][0] = {1, 0.90}
	 * list[0][1] = {2, 0.60} list[1][0] = {1, 0.70} list[1][1] = {2, 0.80} ^ ^
	 * ^ ^ agent ___| | | | round id ___| | | round number _____| | round
	 * utility _________|
	 *
	 * @return Utility history
	 */
	public List<List<Double[]>> getAgentUtilsDiscounted() {
		return agentUtilsDiscounted;
	}

	/**
	 * Returns round number and (discounted) utility value of agreement if any.
	 * If no agreement this function is undefined.
	 *
	 * result[0][0] = 2.1 <-- round number result[0][1] = 2.1 <-- round number
	 * result[1][0] = 0.40 <-- utility result[1][1] = 0.80 <-- utility
	 *
	 * @return The round number and utility of agreement if any.
	 */
	public double[][] getAgreementUtilitiesDiscounted() {
		return agreementUtilitiesDiscounted;
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

}
