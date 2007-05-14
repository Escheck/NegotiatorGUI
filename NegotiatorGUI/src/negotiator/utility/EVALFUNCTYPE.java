package negotiator.utility;

public enum EVALFUNCTYPE { CONSTANT, LINEAR ;

	public static EVALFUNCTYPE convertToType(String type) {
		if (type.equalsIgnoreCase("linear"))
			return EVALFUNCTYPE.LINEAR;
		else if (type.equalsIgnoreCase("constant"))
			return EVALFUNCTYPE.CONSTANT;
		else return null;
	}

	public static double evalLinear(double x, double coef1, double coef0) {
		return coef1*x+coef0;
	}
	public static double evalLinearRev(double y, double coef1, double coef0) {
		return (y-coef0)/coef1;
	}

}
