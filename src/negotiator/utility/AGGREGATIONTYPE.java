package negotiator.utility;

public enum AGGREGATIONTYPE { 
	
	SUM, MIN, MAX;
	
	public static AGGREGATIONTYPE getAggregationType(String type){
		
		switch (type) {
			case "sum": return SUM; 
			case "min": return MIN; 
			case "max": return MAX; 
			default: return SUM; 		
		}
	}

}
