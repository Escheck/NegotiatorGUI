package negotiator.parties;

import negotiator.AgentID;
import negotiator.DeadlineType;
import negotiator.Timeline;
import negotiator.utility.UtilitySpace;

import java.util.Map;


public class BoulwareNegotiationParty extends AbstractTimeDependentNegotiationParty {

    public BoulwareNegotiationParty(UtilitySpace utilitySpace, Map<DeadlineType, Object> deadlines, Timeline timeline, long randomSeed) {
        super(utilitySpace, deadlines, timeline, randomSeed);
        partyId = new AgentID(String.format("Boulware#%4s", hashCode()));
    }

    @Override
    public double getE() {
        return 0.2;
    }
}
