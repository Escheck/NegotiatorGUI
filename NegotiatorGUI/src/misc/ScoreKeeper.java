package misc;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import java.io.Serializable;

public class ScoreKeeper<A> implements Comparator<A>, Serializable
{
	protected Map<A, Integer> m;
	protected int max;
	protected int total;
	
	public ScoreKeeper()
	{
		m = new HashMap<A, Integer>();
		total = 0;
		max = 0;
	}
	
	public ScoreKeeper(ScoreKeeper<A> sk)
	{
		this.m = sk.m;
		this.max = sk.max;
		this.total = sk.total;
	}
	
	public void score(A a)
	{
		Integer freq = m.get(a);
		if (freq == null)
			freq = 0;
		freq++;
		if (freq > max) {
			max = freq;
		}
		total++;
		m.put(a, freq);
	}

	public void score(A a, int weight)
	{
		Integer freq = m.get(a);
		if (freq == null)
			freq = 0;
		int newValue = freq + weight;
		if (newValue > max) {
			max = newValue;
		}
		total += weight;
		m.put(a, newValue);
	}
	
	public int getScore(A a)
	{
		Integer freq = m.get(a);
		if (freq == null)
			freq = 0;
		return freq;
	}
	
	public double getNormalizedScore(A a) {
		Integer score = m.get(a);
		if (score == null) {
			score = 0;
		}
		return ((double) score / (double) max);
	}
	
	public double getRelativeScore(A a) {
		Integer score = m.get(a);
		if (score == null) {
			score = 0;
		}
		return ((double) score / (double) total);
	}

	public int compare(A o1, A o2)
	{
		if (o1 == null || o2 == null)
			throw new NullPointerException();
		if (o1.equals(o2))
			return 0;
		if (getScore(o1) > getScore(o2))
			return -1;
		else if (getScore(o1) < getScore(o2))
            return 1;
        else
            return ((Integer) o1.hashCode()).compareTo(o2.hashCode());
	}
	
	public String toString()
	{
		TreeMap<A, Integer> sorted = getSortedCopy();
		return getElements().size() + " entries, " + getTotal() + " total: " + sorted.toString() + "\n";
	}

	public TreeMap<A, Integer> getSortedCopy()
	{
		TreeMap<A, Integer> sorted = new TreeMap<A, Integer>(this);
		sorted.putAll(m);
		return sorted;
	}

	public int getMaxValue() {
		return max;
	}
	
	public String toString(int n)
	{
		TreeMap<A, Integer> sorted = getSortedCopy();
		String s = getElements().size() + " entries, " + getTotal() + " total. Top " + n + " entries:\n";
		int i = 0;
		for (A a : sorted.keySet())
		{
			s += a.toString() + "=" + sorted.get(a) + "\n";
			if (i++ > n)
				break;
		}
		return s;
	}
	
	public int getTotal()
	{
		int total = 0;
		for (A a : m.keySet())
			total += m.get(a);
		return total;
	}
	
	/**
	 * @return Random A, gewogen naar score
	 * TODO: kan sneller
	 */
	public A getRandom()
	{
		double r = Math.random();
		double t = getTotal();
		double gezochteScore = r * t;	// bv gezochteScore [0, 1) matcht met score 1
		int score = 0;
		for (A a : m.keySet())
		{
			score += m.get(a);
			if (score > gezochteScore)
				return a;
		}
		// KN
		return null;
	}
	
	/**
	 * Pas op, neem weights mee!
	 * @return
	 */
	public Set<A> getElements()
	{
		return m.keySet();
	}
	
	public String toMathematicaListPlot()
	{
		StringBuilder s = new StringBuilder("data={");
		boolean first = true;
		TreeSet<A> sortedKeys = new TreeSet<A>(m.keySet());
		for (A entry : sortedKeys)
		{
			if (first)
				first = false;
			else
				s.append(",");
			
			if (entry instanceof Number)
			{
				s.append("{"+entry+","+m.get(entry)+"}");
			}
			
			if (entry instanceof String)
			{
				s.append("{\""+entry+"\","+m.get(entry)+"}");
			}
		}
		s.append("};\n");
		s.append("ListPlot[data, PlotRange -> All]");
		return s.toString();
	}
}
