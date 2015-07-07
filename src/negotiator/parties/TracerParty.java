package negotiator.parties;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.utility.UtilitySpace;

public class TracerParty extends NonDeterministicConcederNegotiationParty {


    public TracerParty(UtilitySpace utilitySpace,
                                    Deadline deadlines, Timeline timeline, long randomSeed) {
        super(utilitySpace, deadlines, timeline, randomSeed);
        partyId = new AgentID("Tracer");

    }

    @Override
    public double getTargetUtility() {
        double t = super.getTargetUtility();
        System.out.println(t);
        return t;
    }
}
