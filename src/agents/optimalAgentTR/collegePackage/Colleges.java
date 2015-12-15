package agents.optimalAgentTR.collegePackage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Indexed from 0 instead of 1!
 * @author Baarslag
 *
 */
public class Colleges
{
	private ArrayList<College> colleges;
	
	public Colleges()
	{
		colleges = new ArrayList<>();
	}
	
	public Colleges(Colleges or)
	{
		colleges = new ArrayList<College>(or.colleges);
	}
	
	public Colleges(ArrayList<College> colleges)
	{
		this.colleges = colleges;
	}

	public void add(College c)
	{
		colleges.add(c);
	}
	
	public College get(int i)
	{
		return colleges.get(i);
	}
	
	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		for (College c : colleges)
		{
			b.append(c.toString());
			b.append("\n");
		}
		return b.toString();
	}
	
	public String toExcel()
	{
		StringBuilder b = new StringBuilder();
		int i = 0;
		for (College c : colleges)
		{
			b.append(++i);
			b.append("\t");
			b.append(c.toExcel());
			b.append("\n");
		}
		return b.toString();
	}
	
	
	public String toMathematica(Function<Colleges, BigDecimal> f)
	{
		StringBuilder b = new StringBuilder();
		b.append("{");
		int i = 0;
		Colleges subColleges = new Colleges();
		double previousValue = 0;
		for (College c : colleges)
		{
			subColleges.add(c);
			if (++i > 1)
				b.append(", ");
			b.append("{");
//			b.append(c.getU() + ", " + c.getAlpha() + ", " + (++i));
			double subCollegeValue = f.apply(subColleges).doubleValue();
			double marginalImprovement = subCollegeValue - previousValue;
			b.append(c.getU() + ", " + c.getAlpha() + ", " + String.format("%.9f", marginalImprovement));
			b.append("}");
			previousValue = subCollegeValue;			
		}
		b.append("}");
		return b.toString();
	}
	public Set<College> toSet()
	{
		return new HashSet<College>(colleges);
	}
	
	public void sort()
	{
		Collections.sort(colleges);
	}

	public int size()
	{
		return colleges.size();
	}

	public boolean contains(College c)
	{
		return colleges.contains(c);
	}
}
