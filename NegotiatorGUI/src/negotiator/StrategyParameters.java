package negotiator;

import java.util.HashMap;

/**
 * Simple class which stores the parameters given to a negotiation
 * strategy, for example an concession factor.
 * 
 * @author Mark Hendrikx
 */
public class StrategyParameters {

	protected HashMap<String, String> strategyParam;
	
	public StrategyParameters() {
		strategyParam = new HashMap<String, String>();
	}

	public void addVariable(String name, String value) {
		strategyParam.put(name, value);
	}
	
	public String getValueAsString(String name) {
		return strategyParam.get(name);
	}
	
	public double getValueAsDouble(String name) {
		return Double.parseDouble(strategyParam.get(name));
	}
}