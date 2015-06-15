package agents.anac.y2014.TUDelftGroup2;

import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import agents.anac.y2014.TUDelftGroup2.boaframework.acceptanceconditions.other.Group2_AS;
import negotiator.boaframework.agent.BOAagent;
import agents.anac.y2014.TUDelftGroup2.boaframework.offeringstrategy.other.Group2_BS;
import negotiator.boaframework.omstrategy.BestBid;
import negotiator.boaframework.omstrategy.NullStrategy;
import agents.anac.y2014.TUDelftGroup2.boaframework.opponentmodel.Group2_OM;
import negotiator.utility.NonlinearUtilitySpace;

 
public class Group2Agent extends BOAagent
{

    @Override
    public void agentSetup() {
    	OpponentModel om             = new Group2_OM();
        
 
         
        try {
			om.init(negotiationSession,new java.util.HashMap<java.lang.String,java.lang.Double>(1));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        OMStrategy oms                 = new  BestBid(negotiationSession,om);
         
        OfferingStrategy offering      = new  Group2_BS();
        try {
			offering.init(negotiationSession, om, oms, new java.util.HashMap<java.lang.String,java.lang.Double>(1) );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        AcceptanceStrategy ac         = new Group2_AS();
        try {
			ac.init(negotiationSession, offering, om, new java.util.HashMap<java.lang.String,java.lang.Double>(1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        
        setDecoupledComponents(ac, offering, om, oms);       
    }

    @Override
    public String getName() {
        return "GROUP2Agent";
    }
}