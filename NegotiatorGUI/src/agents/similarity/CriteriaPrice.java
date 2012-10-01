/**
 * 
 */
package agents.similarity;

import java.util.HashMap;

import negotiator.Bid;
import negotiator.issue.ValueReal;
import negotiator.utility.EVALFUNCTYPE;
import negotiator.xml.SimpleElement;

/**
 * @author Dmytro Tykhonov
 *
 */
public class CriteriaPrice implements Criteria {

	// Class fields
	double lowerBound;
	double upperBound;
	double maxMargin = -1;
	int fIssueIndex;
	EVALFUNCTYPE type;
	HashMap<Integer, Double> fParam;	
	/**
	 * 
	 */
	public CriteriaPrice(int pIssueIndex) {
		// TODO Auto-generated constructor stub
		fIssueIndex = pIssueIndex;
	}

	/* (non-Javadoc)
	 * @see negotiator.agents.similarity.Criteria#getValue(negotiator.Bid)
	 */
	public double getValue(Bid pBid) {

		double utility;
		double value = 0;
		try {
			value = ((ValueReal)pBid.getValue(fIssueIndex)).getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch(this.type) {
		case LINEAR:
			utility = EVALFUNCTYPE.evalLinear(value, this.fParam.get(1), this.fParam.get(0));
			if (utility<0)
				utility = 0;
			else if (utility > 1)
				utility = 1;
			return utility;
		case CONSTANT:
			return this.fParam.get(0);
		case FARATIN:
			//TODO: DT: Check min and max assumtions. Currently set to 1 and 0
			utility = EVALFUNCTYPE.evalFaratin(value, 1, 0, this.fParam.get(1), this.fParam.get(0));
			if (utility<0)
				utility = 0;
			else if (utility > 1)
				utility = 1;
			return utility;
			
		default:
			return -1.0;
		}
	}	
	/* (non-Javadoc)
	 * @see negotiator.agents.similarity.Criteria#loadFromXML(negotiator.xml.SimpleElement)
	 */
	public void loadFromXML(SimpleElement pRoot) {
		Object[] xml_item = ((SimpleElement)pRoot).getChildByTagName("range");
		this.lowerBound = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
		this.upperBound = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("upperbound"));
		Object[] xml_items = ((SimpleElement)pRoot).getChildByTagName("evaluator");
		String ftype = ((SimpleElement)xml_items[0]).getAttribute("ftype");
		if (ftype!=null)
			this.type = EVALFUNCTYPE.convertToType(ftype);
		// TODO: define exception.
		switch(this.type) {
		case LINEAR:
			this.fParam.put(1, Double.valueOf(((SimpleElement)xml_items[0]).getAttribute("parameter1")));
		case CONSTANT:
			this.fParam.put(0, Double.valueOf(((SimpleElement)xml_items[0]).getAttribute("parameter0")));
		}
	}

}
