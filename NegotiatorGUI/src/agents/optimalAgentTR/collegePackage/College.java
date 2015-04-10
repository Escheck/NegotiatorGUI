package agents.optimalAgentTR.collegePackage;

public class College implements Comparable<College>
{

	/** Student's utility of attending */
	private double u;

	/** Admission chance */
	private double alpha;
	
	/** Index to cross-reference if needed */
	private int index;

	public College(double u, double alpha)
	{
		super();
		this.u = u;
		this.alpha = alpha;
	}

	public College(double u, double alpha, int index)
	{
		super();
		this.u = u;
		this.alpha = alpha;
		this.index = index;
	}

	public double getU() 
	{
		return u;
	}

	public double getAlpha() 
	{
		return alpha;
	}
	
	public int getIndex()
	{
		return index;
	}

	public double getZ()
	{
		return u * alpha;
	}

	@Override
	public int compareTo(College o)
	{
		return Double.compare(o.u, this.u);
	}
	
	@Override
	public String toString()
	{
		return "<u = " + String.format("%.2f",u) + ", alpha = " + String.format("%.2f",alpha) + ">";
	}
	
	public String toExcel()
	{
		return u + "\t" + alpha;
	}
}
