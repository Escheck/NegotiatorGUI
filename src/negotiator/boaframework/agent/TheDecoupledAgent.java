package negotiator.decoupledframework.agent;

import misc.Serializer;
import negotiator.decoupledframework.DecoupledAgentInfo;
import negotiator.decoupledframework.repository.DecoupledAgentRepository;

/**
 * This class is used to convert a serialized decoupled agent (created with the GUI)
 * to a real agent.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 19/12/11
 */
public class TheDecoupledAgent extends DecoupledAgent {

	String name = "";
	DecoupledAgentInfo dagent;
	
	/**
	 * Loads and initializes the decoupled components of the agent.
	 */
	@Override
	public void agentSetup() {
		String os = dagent.getOfferingStrategy().getClassname();
		String as = dagent.getAcceptanceStrategy().getClassname();
		String om = dagent.getOpponentModel().getClassname();
		String oms = dagent.getOMStrategy().getClassname();
		
		// Create clones and reset the clones. The clone is necessary to create a new object.
		// The problem is that we don't know the definite form of the constructor.
		// We could have used reflexion, but that option is a lot slower.
		// For safety we also reset the clone, because it is possible that the object remembers
		// information from its previous round.
		offeringStrategy = DecoupledAgentRepository.getInstance().getOfferingStrategy(os).clone().reset();
		acceptConditions = DecoupledAgentRepository.getInstance().getAcceptanceStrategy(as).clone().reset();
		opponentModel = DecoupledAgentRepository.getInstance().getOpponentModel(om).clone().reset();
		omStrategy = DecoupledAgentRepository.getInstance().getOMStrategy(oms).clone().reset();

		try {
			opponentModel.init(negotiationSession, dagent.getOpponentModel().getParameters());
			opponentModel.setOpponentUtilitySpace(fNegotiation);
			omStrategy.init(negotiationSession, opponentModel, dagent.getOMStrategy().getParameters());
			offeringStrategy.init(negotiationSession, opponentModel, omStrategy, dagent.getOfferingStrategy().getParameters());
			acceptConditions.init(negotiationSession, offeringStrategy, dagent.getAcceptanceStrategy().getParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return name;
	}
	
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
	 * Retrieves the parameters of the agent and converts them to
	 * a decoupled agent.
	 */
	@Override
	public void parseStrategyParameters(String variables) throws Exception {
		Serializer<DecoupledAgentInfo> serializer = new Serializer<DecoupledAgentInfo>("");
		dagent = serializer.readStringToObject(variables);
		name = dagent.getName();
	}
}
