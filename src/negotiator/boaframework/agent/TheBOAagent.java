package negotiator.boaframework.agent;

import misc.Serializer;
import negotiator.boaframework.BOAagentInfo;
import negotiator.boaframework.repository.BOAagentRepository;

/**
 * This class is used to convert a BOA agent created using the GUI to
 * a real agent. The parseStrategyParameters loads the information object,
 * the agentSetup uses the information object to load the agent using
 * reflexion.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 19/12/11
 */
public class TheBOAagent extends BOAagent {

	/** name of the agent */
	String name = "";
	/** information object which stores the decoupled agent */
	BOAagentInfo dagent;
	
	/**
	 * Loads and initializes the decoupled components of the agent.
	 */
	@Override
	public void agentSetup() {
		// load the class names of each object
		String os = dagent.getOfferingStrategy().getClassname();
		String as = dagent.getAcceptanceStrategy().getClassname();
		String om = dagent.getOpponentModel().getClassname();
		String oms = dagent.getOMStrategy().getClassname();
		
		// create the actual objects using reflexion
		offeringStrategy = BOAagentRepository.getInstance().getOfferingStrategy(os);
		acceptConditions = BOAagentRepository.getInstance().getAcceptanceStrategy(as);
		opponentModel = BOAagentRepository.getInstance().getOpponentModel(om);
		omStrategy = BOAagentRepository.getInstance().getOMStrategy(oms);

		// init the components.
		try {
			opponentModel.init(negotiationSession, dagent.getOpponentModel().getParameters());
			opponentModel.setOpponentUtilitySpace(fNegotiation);
			omStrategy.init(negotiationSession, opponentModel, dagent.getOMStrategy().getParameters());
			offeringStrategy.init(negotiationSession, opponentModel, omStrategy, dagent.getOfferingStrategy().getParameters());
			acceptConditions.init(negotiationSession, offeringStrategy, dagent.getAcceptanceStrategy().getParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// remove the reference to the information object such that the garbage collector can remove it.
		dagent = null;
	}

	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Removes the references to all components such that the garbage collector
	 * can remove them.
	 */
	@Override
	public void cleanUp() {
		offeringStrategy = null;
		acceptConditions = null;
		opponentModel = null;
		negotiationSession = null;
		utilitySpace = null;
		dagent = null;
	}
	
	/**
	 * Loads the BOA agent information object created by using the GUI.
	 * The agentSetup method uses this information to load the necessary
	 * components by using reflexion.
	 */
	@Override
	public void parseStrategyParameters(String variables) throws Exception {
		Serializer<BOAagentInfo> serializer = new Serializer<BOAagentInfo>("");
		dagent = serializer.readStringToObject(variables);
		name = dagent.getName();
	}
}
