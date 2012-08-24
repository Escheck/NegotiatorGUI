package negotiator.boaframework.agent;

import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.acceptanceconditions.AC_Next;
import negotiator.boaframework.offeringstrategy.other.TimeDependent_Offering;
import negotiator.boaframework.omstrategy.NullStrategy;
import negotiator.boaframework.opponentmodel.ScalableBayesianModel;

/**
 * Simple adapter which can be used to convert a BOA Agent to a normal Agent.
 * This is interesting for example for the ANAC competition in which BOA Agents are
 * currently not (yet) accepted.
 * 
 * @author Alex Dirkzwager
 */
public class SimpleBOAagent extends BOAagent{

	@Override
	public void agentSetup() {
		OpponentModel om 			= new ScalableBayesianModel();
		om.init(negotiationSession);
		OMStrategy oms 				= new NullStrategy(negotiationSession);
		OfferingStrategy offering  	= new TimeDependent_Offering(negotiationSession, om, oms, 0.2, 0, 1, 0); //Boulware agent strategy
		AcceptanceStrategy ac 		= new AC_Next(negotiationSession, offering, 1, 0);
		setDecoupledComponents(ac, offering, om, oms);		
	}

	@Override
	public String getName() {
		return "SimpleBOAagent";
	}
}