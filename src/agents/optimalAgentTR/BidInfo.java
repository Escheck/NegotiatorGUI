package agents.optimalAgentTR;

import negotiator.Bid;

public class BidInfo {
	
	private Bid bid;
	private double myUtil;
	private double opponentUtil;
	
	public BidInfo(Bid bid){
		this.setBid(bid);
		setMyUtil(0.0);
		setOpponentUtil(0.0);
	}
	
	public BidInfo(Bid bid, double myUtility, double opponentUtility){
		this.setBid(bid);
		this.setMyUtil(myUtility);
		this.setOpponentUtil(opponentUtility);
	}

	public Bid getBid() {
		return bid;
	}

	public void setBid(Bid bid) {
		this.bid = bid;
	}

	public double getMyUtil() {
		return myUtil;
	}

	public void setMyUtil(double myUtil) {
		this.myUtil = myUtil;
	}

	public double getOpponentUtil() {
		return opponentUtil;
	}

	public void setOpponentUtil(double opponentUtil) {
		this.opponentUtil = opponentUtil;
	}
	
}
