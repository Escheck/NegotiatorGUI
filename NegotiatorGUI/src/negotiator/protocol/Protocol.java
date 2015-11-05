package negotiator.protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import negotiator.Agent;
import negotiator.Domain;
import negotiator.NegotiationEventListener;
import negotiator.NegotiationOutcome;
import negotiator.actions.Action;
import negotiator.events.ActionEvent;
import negotiator.events.BilateralAtomicNegotiationSessionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.exceptions.Warning;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.Repository;
import negotiator.tournament.Tournament;
import negotiator.tournament.TournamentRunner;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.xml.SimpleElement;

/**
 * Abstract class for the manager of protocols. Implement start() to define the
 * protocol.
 */
public abstract class Protocol implements Runnable, Serializable {
	private static final long serialVersionUID = -7994042965683386295L;
	protected Thread negoThread = null;
	protected TournamentRunner tournamentRunner;
	/**
	 * stopNegotiation indicates that the session has now ended. it is checked
	 * after every call to the agent, and if it happens to be true, session is
	 * immediately returned without any updates to the results list. This is
	 * because killing the thread in many cases will return Agent.getActions()
	 * but with a stale action. By setting stopNegotiation to true before
	 * killing, the agent will still immediately return.
	 */
	public boolean stopNegotiation = false;
	public boolean threadFinished = false;
	private AgentRepItem[] agentRepItems;
	private ProfileRepItem[] profileRepItems;
	private String[] agentNames;
	private HashMap<AgentParameterVariable, AgentParamValue>[] agentParams;

	/** -- **/
	protected Domain domain;
	private AbstractUtilitySpace[] agentUtilitySpaces;

	ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();

	private SimpleElement fRoot;

	public abstract String getName();

	public abstract NegotiationOutcome getNegotiationOutcome();

	protected int sessionNr = -1;
	protected int totalSessions;

	public static ArrayList<Protocol> getTournamentSessions(
			Tournament tournament) throws Exception {
		throw new Exception("This protocol cannot be used in a tournament");
	}

	public final void startSession() {
		Thread protocolThread = new Thread(this);
		protocolThread.start();
	}

	public Protocol(AgentRepItem[] agentRepItems,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams,
			int totalMatches) throws Exception {
		this.agentRepItems = agentRepItems.clone();
		this.profileRepItems = profileRepItems.clone();
		this.totalSessions = totalMatches;
		if (agentParams != null) {
			this.agentParams = agentParams.clone();
		} else {
			this.agentParams = new HashMap[agentRepItems.length];
		}
		loadAgentsUtilitySpaces();
	}

	protected void loadAgentsUtilitySpaces() throws Exception {
		if (domain == null)
			// domain = new
			// Domain(profileRepItems[0].getDomain().getURL().getFile());
			domain = Repository.get_domain_repos().getDomain(
					profileRepItems[0].getDomain());
		// TODO: read the agent names
		agentNames = new String[profileRepItems.length];
		agentNames[0] = "Agent A";
		agentNames[1] = "Agent B";

		// load the utility space
		agentUtilitySpaces = new AdditiveUtilitySpace[profileRepItems.length];
		for (int i = 0; i < profileRepItems.length; i++) {
			ProfileRepItem profile = profileRepItems[i];
			agentUtilitySpaces[i] = Repository.get_domain_repos()
					.getUtilitySpace(domain, profile);
		}
		return;

	}

	public void addNegotiationEventListener(NegotiationEventListener listener) {
		if (!actionEventListener.contains(listener))
			actionEventListener.add(listener);
	}

	public ArrayList<NegotiationEventListener> getNegotiationEventListeners() {
		return (ArrayList<NegotiationEventListener>) (actionEventListener
				.clone());
	}

	public void removeNegotiationEventListener(NegotiationEventListener listener) {
		if (!actionEventListener.contains(listener))
			actionEventListener.remove(listener);
	}

	public synchronized void fireNegotiationActionEvent(Agent actorP,
			Action actP, int roundP, long elapsed, double time, double utilA,
			double utilB, double utilADiscount, double utilBDiscount,
			String remarks, boolean finalActionEvent) {
		for (NegotiationEventListener listener : actionEventListener) {
			listener.handleActionEvent(new ActionEvent(this, actorP, actP,
					roundP, elapsed, time, utilA, utilB, utilADiscount,
					utilBDiscount, remarks, finalActionEvent));
		}
	}

	public synchronized void fireBilateralAtomicNegotiationSessionEvent(
			BilateralAtomicNegotiationSession session, ProfileRepItem profileA,
			ProfileRepItem profileB, AgentRepItem agentA, AgentRepItem agentB,
			String agenAName, String agentBName) {
		for (NegotiationEventListener listener : actionEventListener) {
			listener.handleBlateralAtomicNegotiationSessionEvent(new BilateralAtomicNegotiationSessionEvent(
					this, session, profileA, profileB, agentA, agentB,
					agenAName, agentBName));
		}
	}

	public synchronized void fireLogMessage(String source, String log) {
		for (NegotiationEventListener listener : actionEventListener) {
			listener.handleLogMessageEvent(new LogMessageEvent(this, source,
					log));
		}
	}

	public void setTournamentRunner(TournamentRunner runner) {
		tournamentRunner = runner;
	}

	public Domain getDomain() {
		return domain;
	}

	public AgentRepItem getAgentRepItem(int index) {
		return agentRepItems[index];
	}

	public ProfileRepItem getProfileRepItems(int index) {
		return profileRepItems[index];
	}

	public String getAgentName(int index) {
		return agentNames[index];
	}

	public HashMap<AgentParameterVariable, AgentParamValue> getAgentParams(
			int index) {
		return agentParams[index];
	}

	public AbstractUtilitySpace getAgentUtilitySpaces(int index) {
		return agentUtilitySpaces[index];
	}

	public int getNumberOfAgents() {
		return agentRepItems.length;
	}

	public void stopNegotiation() {
		if (negoThread != null && negoThread.isAlive()) {
			try {
				stopNegotiation = true; // see comments in sessionrunner..
				negoThread.interrupt();
				// we call cleanup of agent from separate thread, preventing any
				// sabotage on kill.
				// Thread cleanup=new Thread() {public void run() {
				// sessionrunner.currentAgent.cleanUp(); } };
				// cleanup.start();
				// TODO call this from separate thread.
				// negoThread.stop(); // kill the stuff
				// Wouter: this will throw a ThreadDeath Error into the nego
				// thread
				// The nego thread will catch this and exit immediately.
				// Maybe it should not even try to catch that.
			} catch (Exception e) {
				new Warning("problem stopping the nego", e);
			}
		}
		return;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(agentNames);
		result = prime * result + Arrays.hashCode(agentParams);
		result = prime * result + Arrays.hashCode(agentRepItems);
		result = prime * result + Arrays.hashCode(agentUtilitySpaces);
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + Arrays.hashCode(profileRepItems);
		return result;
	}

	@Override
	public String toString() {
		return Arrays.toString(agentRepItems) + " on "
				+ Arrays.toString(profileRepItems);
	}

	public int getSessionNumber() {
		return sessionNr;
	}

	public int getTotalSessions() {
		return totalSessions;
	}
}