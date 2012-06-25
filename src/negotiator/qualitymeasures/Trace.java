package negotiator.qualitymeasures;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.bidding.BidDetails;

public class Trace {
	
	private ArrayList<BidDetails> offeredBids;
	private String agent;
	private String opponent;
	private String agentProfile;
	private String opponentProfile;
	private String domain;
	private double endOfNegotiation;
	private boolean agreement;
	private int runNumber;
	
	public Trace() {
		offeredBids = new ArrayList<BidDetails>();
	}

	public void addBid(Bid bid, double evaluation, double time) {
		offeredBids.add(new BidDetails(bid, evaluation, time));
	}

	public ArrayList<BidDetails> getOfferedBids() {
		return offeredBids;
	}

	public void setOfferedBids(ArrayList<BidDetails> offeredBids) {
		this.offeredBids = offeredBids;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getOpponent() {
		return opponent;
	}

	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	public String getAgentProfile() {
		return agentProfile;
	}

	public void setAgentProfile(String agentProfile) {
		this.agentProfile = agentProfile;
	}

	public String getOpponentProfile() {
		return opponentProfile;
	}

	public void setOpponentProfile(String opponentProfile) {
		this.opponentProfile = opponentProfile;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public double getEndOfNegotiation() {
		return endOfNegotiation;
	}

	public void setEndOfNegotiation(double endOfNegotiation) {
		this.endOfNegotiation = endOfNegotiation;
	}

	public boolean isAgreement() {
		return agreement;
	}

	public void setAgreement(boolean agreement) {
		this.agreement = agreement;
	}

	public int getRunNumber() {
		return runNumber;
	}

	public void setRunNumber(int runNumber) {
		this.runNumber = runNumber;
	}

	@Override
	public String toString() {
		return "Trace [agent=" + agent
				+ ", opponent=" + opponent + ", agentProfile=" + agentProfile
				+ ", opponentProfile=" + opponentProfile + ", domain=" + domain
				+ ", endOfNegotiation=" + endOfNegotiation + ", agreement="
				+ agreement +  ", runNumber=" + runNumber + "]";
	}
}