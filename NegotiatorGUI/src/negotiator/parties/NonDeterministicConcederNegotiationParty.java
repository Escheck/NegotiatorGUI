package negotiator.parties;

import java.util.List;
import java.util.Random;

import misc.Range;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.bidding.BidDetails;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

public class NonDeterministicConcederNegotiationParty extends
		AbstractTimeDependentNegotiationParty {

	public static final double DELTA = 0.05;
	protected Random random;

	@Override
	public void init(AbstractUtilitySpace utilitySpace, Deadline deadlines,
			TimeLineInfo timeline, long randomSeed, AgentID id) {
		super.init(utilitySpace, deadlines, timeline, randomSeed, id);
		random = new Random();
	}

	@Override
	protected Bid getNextBid() {
		final List<BidDetails> candidates = getCandidates(getTargetUtility(),
				DELTA);
		final BidDetails chosen = getRandomElement(candidates);
		return chosen.getBid();
	}

	protected List<BidDetails> getCandidates(double target, double delta) {
		return outcomeSpace.getBidsinRange(new Range(target - delta, target
				+ delta));
	}

	protected <T> T getRandomElement(List<T> list) {
		return list.get(random.nextInt(list.size()));
	}

	@Override
	public double getE() {
		return 2;
	}
}
