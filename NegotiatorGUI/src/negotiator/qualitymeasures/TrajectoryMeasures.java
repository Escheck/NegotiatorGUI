package negotiator.qualitymeasures;

import java.util.ArrayList;
import negotiator.analysis.BidPoint;
import negotiator.xml.SimpleElement;

/**
 * This class is an implementation of the trajectory measures discussed by
 * Hindriks et al. Extend calculateMeasures to add your own measures.
 * 
 * NOTE: currently there is a bug in Genius which results in the last bid
 * not being saved correctly. Therefore it is advised to remove the last
 * before calculating the measures below.
 * 
 * @author Mark Hendrikx and Alexander Dirkzwager
 * @contact m.j.c.hendrikx@student.tudelft.nl
 */
public class TrajectoryMeasures {

	ArrayList<BidPoint> agentABids;
	ArrayList<BidPoint> agentBBids;
	boolean agentAFirst;
	private final double SILENTTHRESHOLD = 0.05;
	//private int silentMoves = 0;

	public TrajectoryMeasures(ArrayList<BidPoint> agentABids,
			ArrayList<BidPoint> agentBBids) {
		this.agentABids = agentABids;
		this.agentBBids = agentBBids;
	}

	/**
	 * Calculates the percentage of unfortunate moves.
	 * @param isAgentA is true if this is agent A
	 * @return percentage of unfortunate moves
	 */
	public double calculatePercentageUnfortunateMoves(boolean isAgentA) {
		ArrayList<BidPoint> bids = agentABids;
		if (!isAgentA) {
			bids = agentBBids;
		}

		if (bids.size() > 1) {
			int unfortunateMoves = 0;
			BidPoint prev = bids.get(0);
			
			for (int i = 1; i < bids.size(); i++) {
				if(!checkSilentMove(bids.get(i), prev)) {
					if (bids.get(i).utilityA < prev.utilityA &&
							bids.get(i).utilityB < prev.utilityB) {
						unfortunateMoves++;
					}
				}
				
				prev = bids.get(i);
			}
			// -1 since we are looking in between bids.
			return (double)unfortunateMoves / ((double)bids.size() - 1);
		} else {
			return 0;
		}
	}
	
	/**
	 * Calculates the percentage of selfish moves.
	 * @param isAgentA is true if this is agent A
	 * @return percentage of selfish moves
	 */
	public double calculatePercentageSelfishMoves(boolean isAgentA) {
		ArrayList<BidPoint> bids = agentABids;
		if (!isAgentA) {
			bids = agentBBids;
		}

		if (bids.size() > 1) {
			int selfishMoves = 0;
			BidPoint prev = bids.get(0);
			
			for (int i = 1; i < bids.size(); i++) {
				if(!checkSilentMove(bids.get(i), prev)) {
					if (bids.get(i).utilityA > prev.utilityA &&
							bids.get(i).utilityB < prev.utilityB) {
						selfishMoves++;
					}
				}
				prev = bids.get(i);
			}
			// -1 since we are looking in between bids.
			return (double)selfishMoves / ((double)bids.size() - 1);
		} else {
			return 0;
		}
	}
	
	/**
	 * Calculates the percentage of concession moves.
	 * @param isAgentA is true if this is agent A
	 * @return percentage of concession moves
	 */
	public double calculatePercentageConcessionMoves(boolean isAgentA) {
		ArrayList<BidPoint> bids = agentABids;
		if (!isAgentA) {
			bids = agentBBids;
		}

		if (bids.size() > 1) {
			int concessionMoves = 0;
			BidPoint prev = bids.get(0);
			
			for (int i = 1; i < bids.size(); i++) {
				if(!checkSilentMove(bids.get(i), prev) && !checkNiceMoves(bids.get(i), prev)) {
					if (bids.get(i).utilityA < prev.utilityA &&
							bids.get(i).utilityB > prev.utilityB) {
						concessionMoves++;
					}
				}
				prev = bids.get(i);
			}
			// -1 since we are looking in between bids.
			return (double)concessionMoves / ((double)bids.size() - 1);
		} else {
			return 0;
		}
	}
	
	
	/**
	 * Calculates the percentage of fortunate moves.
	 * @param isAgentA is true if this is agent A
	 * @return percentage of fortunate moves
	 */
	public double calculatePercentageFortunateMoves(boolean isAgentA) {
		ArrayList<BidPoint> bids = agentABids;
		if (!isAgentA) {
			bids = agentBBids;
		}

		if (bids.size() > 1) {
			int fortunateMoves = 0;
			BidPoint prev = bids.get(0);
			
			for (int i = 1; i < bids.size(); i++) {
				if(!checkSilentMove(bids.get(i), prev) && !checkNiceMoves(bids.get(i), prev)) {
					if (bids.get(i).utilityA > prev.utilityA &&
							bids.get(i).utilityB > prev.utilityB) {
						fortunateMoves++;
					}
				}
				prev = bids.get(i);
			}
			// -1 since we are looking in between bids.
			return (double)fortunateMoves / ((double)bids.size() - 1);
		} else {
			return 0;
		}
	}
	/**
	 * Calculates the percentage of silent moves.
	 * @param isAgentA is true if this is agent A
	 * @return percentage of silent moves
	 */
	public double calculatePercentageSilentMoves(boolean isAgentA) {
		ArrayList<BidPoint> bids = agentABids;
		if (!isAgentA) {
			bids = agentBBids;
		}

		if (bids.size() > 1) {
			int silentMoves = 0;
			BidPoint prev = bids.get(0);
			
			for (int i = 1; i < bids.size(); i++) {
				if(checkSilentMove(bids.get(i), prev)) {
					silentMoves++;
				}
				prev = bids.get(i);
			}
			// -1 since we are looking in between bids.
			return (double)silentMoves / ((double)bids.size() - 1);
		} else {
			return 0;
		}
	}
	
	/**
	 * Calculates the percentage of nice moves.
	 * @param isAgentA is true if this is agent A
	 * @return percentage of nice moves
	 */
	public double calculatePercentageNiceMoves(boolean isAgentA) {
		ArrayList<BidPoint> bids = agentABids;
		if (!isAgentA) {
			bids = agentBBids;
		}

		if (bids.size() > 1) {
			int niceMoves = 0;
			BidPoint prev = bids.get(0);
			
			for (int i = 1; i < bids.size(); i++) {
				if(checkNiceMoves(bids.get(i), prev)) {
					niceMoves++;
				}
				prev = bids.get(i);
			}
			// -1 since we are looking in between bids.
			return (double)niceMoves / ((double)bids.size() - 1);
		} else {
			return 0;
		}
	}
	
	
	
	
	public boolean checkNiceMoves(BidPoint bid, BidPoint prevBid) {
		if(bid.utilityB > prevBid.utilityB && 
				(bid.utilityA > prevBid.utilityA + SILENTTHRESHOLD || bid.utilityA < prevBid.utilityA - SILENTTHRESHOLD)) {
				return true;

		}
			//current bid is between ownPrev +- SILENTTHRESHOLD
			//current Bid is greater than opponent prevBid
		return false;
	}
	
	public boolean checkSilentMove(BidPoint bid, BidPoint prevBid) {
		if (Math.abs(bid.utilityA - prevBid.utilityA) < SILENTTHRESHOLD &&
				Math.abs(bid.utilityB - prevBid.utilityB) < SILENTTHRESHOLD) {
			//silentMoves++;
			return true;
		}	
		return false;
	}
	
	/**
	 * Returns an XML representation of all trajectory based quality measures.
	 * Extend this method with your own metrics.
	 * 
	 * @param utilA utility of agreement for party A
	 * @param utilB utility of agreement for party B
	 * @return XML representation of the quality measures.
	 */
	public SimpleElement calculateMeasures() {
		SimpleElement tjQualityMeasures = new SimpleElement("trajactory_based_quality_measures");
		
		SimpleElement agentA = new SimpleElement("trajectory");
		tjQualityMeasures.addChildElement(agentA);
		agentA.setAttribute("agent", "A");
		agentA.setAttribute("unfortunate_moves", calculatePercentageUnfortunateMoves(true) + "");
		agentA.setAttribute("fortunate_moves", calculatePercentageFortunateMoves(true) + "");
		agentA.setAttribute("nice_moves", calculatePercentageNiceMoves(true) + "");
		agentA.setAttribute("selfish_moves", calculatePercentageSelfishMoves(true) + "");
		agentA.setAttribute("silent_moves", calculatePercentageSilentMoves(true) + "");
		agentA.setAttribute("concession_moves", calculatePercentageConcessionMoves(true) + "");

		
		SimpleElement agentB = new SimpleElement("trajectory");
		tjQualityMeasures.addChildElement(agentB);
		agentB.setAttribute("agent", "B");
		agentB.setAttribute("unfortunate_moves", calculatePercentageUnfortunateMoves(false) + "");
		agentB.setAttribute("fortunate_moves", calculatePercentageFortunateMoves(false) + "");
		agentB.setAttribute("nice_moves", calculatePercentageNiceMoves(false) + "");
		agentB.setAttribute("selfish_moves", calculatePercentageSelfishMoves(false) + "");
		agentB.setAttribute("silent_moves", calculatePercentageSilentMoves(false) + "");
		agentB.setAttribute("concession_moves", calculatePercentageConcessionMoves(false) + "");
		
		return tjQualityMeasures;
	}
}
