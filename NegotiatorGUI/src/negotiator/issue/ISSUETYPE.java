package negotiator.issue;

public enum ISSUETYPE {	UNKNOWN, DISCRETE, INTEGER, REAL, OBJECTIVE;

	public static ISSUETYPE convertToType(String typeString) {

		// If typeString is null for some reason (i.e. not spceified in the XML template
		// then we assume that we have DISCRETE type
		if(typeString==null) return ISSUETYPE.DISCRETE;
		else if (typeString.equalsIgnoreCase("integer"))
			return ISSUETYPE.INTEGER;
		else if (typeString.equalsIgnoreCase("real"))
			return ISSUETYPE.REAL;
		else if (typeString.equalsIgnoreCase("discrete"))
			return ISSUETYPE.DISCRETE;
		else {
			// Type specified incorrectly!
			System.out.println("Type specified incorrectly.");
			// For now return DISCRETE type.
			return ISSUETYPE.DISCRETE;
			// TODO: Define corresponding exception.
		}
	}	
}