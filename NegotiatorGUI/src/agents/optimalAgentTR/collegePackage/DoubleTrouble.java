package agents.optimalAgentTR.collegePackage;



/** 
 * Bevat allerlei static methodes om met doubles om te gaan
 */
public final class DoubleTrouble
{
	private DoubleTrouble()
	{
		throw new IllegalArgumentException("DoubleTrouble heeft geen instantiaties.");
	}

	
//	public static final String REGEX_NON_NEGATIVE = "\\d{1,10}(?:\\.\\d{1,10})?";
	
	/**  \\d+   :  een cijfer, 1 tot oneindig veel keren
	 *   (?:    :  begin van een non-capturing group
	 *   \\.    :  letterlijke punt
	 *   \\d+   :  een cijfer, 1 tot oneindig veel keren
	 *   )?     :  einde van een non-capturing group en zeg dat deze group 0 of 1 keer plaatsvindt
	 */
	public static final String REGEX_NON_NEGATIVE = "\\d+(?:\\.\\d+)?";

	/** 
	 * Precisie waarmee we werken
	 */
	public static final double EPSILON = 1e-13;
	/** Het dertiende cijfer achter de komma is nog significant, het veertiende niet meer. */
	private static final double SIGNIFICANT_EXPONENT = 1 / EPSILON;

	public static void main(String[] args)
	{
		double x = 0.4 - 0.1;
		System.out.println(x);
		System.out.println(rondAfOpSignificantie(x));
		System.out.println(removeRoundingErrors(x));
	}

	public static boolean isInt(double d)
	{
		return DoubleTrouble.almostEqual(d, Math.round(d));
	}

	public static boolean almostEqual(double d, double e)
	{
		return almostEqual(d, e, EPSILON);
	}

	public static boolean almostEqual(double d, double e, double precision)
	{
		return Math.abs(d - e) < precision;
	}

	public static boolean almostEqual(double [] ds, double [] es)
	{
		if (ds.length != es.length) return false;

		for (int i = 0; i < es.length; i++)
		{
			if (!almostEqual(ds[i], es[i]))
				return false;
		}
		return true;
	}


	public static boolean isAlmostZero(double d)
	{
		return almostEqual(d, 0.0d);
	}

	/**
	 * Bv. 27.8%
	 */
	public static double procenten(double x)
	{
		return Math.round(1000 * x)/10d;
	}

	/**
	 * In gehele procenten, bv. 28%
	 */
	public static int procentenGeheel(double x)
	{
		return (int) Math.round(100 * x);
	}

	public static String procenten(double x, double totaal)
	{
		return procenten(x / totaal) + "%";
	}

	public static String procentenGepad(double x, double totaal)
	{
		return String.format("% 5.1f%%", procenten(x / totaal));
	}

	/**
	 * Maakt van 0.3999999999999999 bv. 0.4, 
	 * maar 3.1415926535 blijft 3.1415926535.
	 */
	public static double removeRoundingErrors(double x)
	{
		double afgerond = rondAfOpSignificantie(x);
		if (almostEqual(afgerond, x))
			return afgerond;
		else
			return x;
	}

	/** 
	 * Rondt de double af op één decimaal nauwkeurig.
	 * Geeft bijv 3.0 terug voor 3.01415
	 */
	public static double rondAf1(double x)
	{
		return Math.round(10 * x)/10d;
	}

	/** 
	 * Rondt de double af op twéé decimalen nauwkeurig.
	 * Geeft bijv 3.01 terug voor 3.01415
	 */
	public static double rondAf2(double x)
	{
		return Math.round(100 * x)/100d;
	}

	public static double rondAf3(double x)
	{
		return Math.round(1000 * x)/1000d;
	}

	public static double rondAf4(double x)
	{
		return Math.round(10000 * x)/10000d;
	}

	public static double rondAf6(double x)
	{
		return Math.round(1000000 * x)/1000000d;
	}

	public static double rondAf(double x)
	{
		return Math.round(100000 * x)/100000d;
	}

	private static double rondAfOpSignificantie(double x)
	{
		return Math.round(SIGNIFICANT_EXPONENT * x)/SIGNIFICANT_EXPONENT;
	}

	/**
	 * Parst een double in doubleString, geeft 'null' bij een fout.
	 * 
	 */
	public static Double parseDouble(String doubleString)
	{
		if (doubleString == null) return null;
		Double d;
		try
		{
			d = Double.parseDouble(doubleString);
		}
		catch (NumberFormatException e)
		{
			d = null;	
		}
		return d;
	}

	/**
	 * Parst een double in doubleString, geeft 'def' bij een fout.
	 * 
	 */
	public static Double parseDouble(String doubleString, Double def)
	{
		if (doubleString == null) return null;
		Double d;
		try
		{
			d = Double.parseDouble(doubleString);
		}
		catch (NumberFormatException e)
		{
			d = def;	
		}
		return d;
	}

	public static int parseInt(String intString, int defaultWaarde)
	{
		if (intString == null) return defaultWaarde;
		try
		{
			return Integer.parseInt(intString);
		}
		catch (NumberFormatException e)
		{
			return defaultWaarde;
		}
	}
	
	/**
	 * Parst een int in intString, geeft <b>null</b> bij een fout.
	 */
	public static Integer parseInt(String intString)
	{
		try
		{
			return Integer.parseInt(intString);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	public static String procenten2(int deel, int geheel)
	{
		return "" + deel + " ("+DoubleTrouble.procenten((double) deel,(double) geheel)+")";
	}

	public static Double parseDouble(CharSequence subSequence)
	{
		return parseDouble(subSequence.toString());
	}
}
