package negotiator.utility;

import negotiator.Bid;
import negotiator.xml.SimpleElement;

public interface Evaluator {
	
	// Interface methods

	// The getEvaluation method returns a scalar evaluation for a value in a bid.
	// Providing the complete bid as a paramater to the method allows for issue dependencies. 
	public Object getEvaluation(UtilitySpace uspace, Bid bid, int index);
	
	public EVALUATORTYPE getType();
	
	public void loadFromXML(SimpleElement pRoot);
	
}
