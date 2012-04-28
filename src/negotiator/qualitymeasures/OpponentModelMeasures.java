package negotiator.qualitymeasures;

import java.util.ArrayList;

import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.boaframework.OpponentModel;
import negotiator.utility.UtilitySpace;
import negotiator.xml.OrderedSimpleElement;

/**
 * This class specifies a set of opponent model measures used to measure the performance
 * of an opponent model during a negotiation. Note that the measures are computationally
 * heavy and computed during the negotiation. This entails that it is recommended to use
 * the time-independent rounds protocol.
 * 
 * @author Mark Hendrikx
 */
public class OpponentModelMeasures {

	private UtilitySpace ownUS;
	private UtilitySpace opponentUS;
	private BidSpace realBS;
	private BidPoint realKalai;
	
	public OpponentModelMeasures(UtilitySpace ownSpace, UtilitySpace opponentModelUS) {
		this.ownUS = ownSpace;
		this.opponentUS = opponentModelUS;
		try {
			realBS = new BidSpace(ownUS, opponentUS);
			realKalai = realBS.getKalaiSmorodinsky();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public double calculatePearsonDistUtil(OpponentModel opponentModel) {
		UtilitySpace estimatedSpace = opponentModel.getOpponentUtilitySpace();
		double pearsonDistUtil	= UtilspaceTools.getPearsonDistanceOfBids(estimatedSpace, opponentUS);
		return pearsonDistUtil;
	}
	
	public double calculateKalaiDiff(UtilitySpace ownUS, OpponentModel opponentModel) {
		UtilitySpace estimatedSpace = opponentModel.getOpponentUtilitySpace();
		BidSpace estimatedBS;
		BidPoint estimatedKalai = null;
		try {
			
			estimatedBS = new BidSpace(ownUS, estimatedSpace, true);
			estimatedKalai = estimatedBS.getKalaiSmorodinsky();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Math.abs(realKalai.utilityB - estimatedKalai.utilityB);
	}
	
	private double distanceBetweenTwoPoints(BidPoint a, BidPoint b) {
		return distanceBetweenTwoPoints(a.utilityA, a.utilityB, b.utilityA, b.utilityB);
	}
	
	private double distanceBetweenTwoPoints(double ax, double ay, double bx, double by) {
		return (Math.pow((ax - bx), 2) + Math.pow((ay - by), 2));
	}
}
