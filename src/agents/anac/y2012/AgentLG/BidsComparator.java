package agents.anac.y2012.AgentLG;
import java.util.Comparator;

import negotiator.Bid;
import negotiator.utility.UtilitySpace;


public class BidsComparator  implements Comparator<Bid> {
	
	public BidsComparator(UtilitySpace utilitySpace) {
		super();
		this.utilitySpace = utilitySpace;
	}

	private UtilitySpace  utilitySpace;
	
	@Override
	public int compare(Bid arg0, Bid arg1) {
		try {
			if (utilitySpace.getUtility(arg0) < utilitySpace.getUtility(arg1))
					return 1;
			else if (utilitySpace.getUtility(arg0) == (utilitySpace.getUtility(arg1))) {
				return 0;
			}
		} catch (Exception e) {

		}
		return -1;
	}

}
