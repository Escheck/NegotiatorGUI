package negotiator.parties;

import misc.Range;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.bidding.BidDetails;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

import java.util.List;
import java.util.Random;


public class NonDeterministicConcederNegotiationParty extends
        AbstractTimeDependentNegotiationParty {

    public static final double DELTA = 0.05;
    protected Random random;

    public NonDeterministicConcederNegotiationParty(UtilitySpace utilitySpace,
                                                    Deadline deadlines, Timeline timeline, long randomSeed) {
        super(utilitySpace, deadlines, timeline, randomSeed);
        partyId = new AgentID(String.format("NDConceder#%4s", hashCode()));
        random = new Random();
    }

    @Override
    protected Bid getNextBid() {
        final List<BidDetails> candidates = getCandidates(getTargetUtility(), DELTA);
        final BidDetails chosen = getRandomElement(candidates);
        return chosen.getBid();
    }

    protected List<BidDetails> getCandidates(double target, double delta) {
        return outcomeSpace.getBidsinRange(new Range(target - delta, target + delta));
    }

    protected <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    @Override
    public double getE() {
        return 2;
    }
}
