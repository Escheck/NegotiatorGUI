package negotiator.qualitymeasures;

import java.util.ArrayList;
import java.util.HashMap;

import misc.Pair;
import negotiator.Bid;
import negotiator.bidding.BidDetails;

public class Trace {
	
	private ArrayList<Pair<Integer, BidDetails>> offeredBids;
	private String agent;
	private String opponent;
	private String agentProfile;
	private String opponentProfile;
	private String domain;
	private double endOfNegotiation;
	private boolean agreement;
	private int runNumber;
	private HashMap<Integer, String> legend;
	private HashMap<String, ArrayList<Double>> data;
	
	public Trace() {
		offeredBids = new ArrayList<Pair<Integer, BidDetails>>();
		legend = new HashMap<Integer, String>();
		data = new HashMap<String, ArrayList<Double>>();
	}

	public void addBid(int index, Bid bid, double evaluation, double time) {
		BidDetails bidDetails = new BidDetails(bid, evaluation, time);
		offeredBids.add(new Pair<Integer, BidDetails>(index, bidDetails));
	}
	
	public ArrayList<Pair<Integer, BidDetails>> getOfferedBids() {
		return offeredBids;
	}

	public void setOfferedBids(ArrayList<Pair<Integer, BidDetails>> offeredBids) {
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


	public HashMap<Integer, String> getLegend() {
		return legend;
	}

	public void setLegend(HashMap<Integer, String> legend) {
		this.legend = legend;
		for (String header : legend.values()) {
			if (!header.equals("time") && !header.equals("bidindices")) {
				data.put(header, new ArrayList<Double>());
			}
		}
	}

	public HashMap<String, ArrayList<Double>> getData() {
		return data;
	}

	public void setData(HashMap<String, ArrayList<Double>> data) {
		this.data = data;
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