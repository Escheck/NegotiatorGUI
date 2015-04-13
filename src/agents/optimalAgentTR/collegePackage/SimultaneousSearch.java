package agents.optimalAgentTR.collegePackage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;


public class SimultaneousSearch
{
	/** Computes f(portfolio). Changes portfolio by sorting it! */
	private Function<Colleges, BigDecimal> f = portfolio -> {
		// sort by ith best ranked college
		portfolio.sort();
		BigDecimal grossPayoff = new BigDecimal(0.0);
		String computation = "";
		for (int i = 0; i < portfolio.size(); i++)
		{
			BigDecimal rho = new BigDecimal(1);
			for (int l = 0; l <= i-1; l++)
				rho = rho.multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(portfolio.get(l).getAlpha())));
			
			
			double alphai = portfolio.get(i).getAlpha();
			double ui = portfolio.get(i).getU();
			BigDecimal zi = BigDecimal.valueOf(ui).multiply(BigDecimal.valueOf(alphai));
			grossPayoff = grossPayoff.add(zi.multiply(rho));
			
			computation += " + " + zi + " * " + rho ;
		}
		computation += " = " + grossPayoff;
//		System.out.println(computation);
			
		return grossPayoff;
	};
	
	Colleges allColleges = new Colleges();
	
	private Function<Integer, BigDecimal> c; 
	
	public SimultaneousSearch()
	{
//		paperExample();
//		splitThePie();
//		uniformDomain();
		ItexCypress();
		
//		setNoCost();
		setDeadlineCost(5);
//		setSizeCost(0.01);
	}
	
	
	/**
	 * For external use. Uses no costs.
	 * @param allColleges
	 */
	public SimultaneousSearch(Colleges allColleges)
	{
		super();
		this.allColleges = allColleges;
		setNoCost();
	}



	private void setNoCost()
	{
		c = k -> new BigDecimal(0);
	}
	
	public void setDeadlineCost(int D)
	{
		c = k -> 
		{
			if (k <= D)
				return new BigDecimal(0);
			else
				return null; 				// this codes infinity.					
		};
		return;
	}
	
	private void setSizeCost(double C)
	{
		c = k -> BigDecimal.valueOf(C).multiply(new BigDecimal(k));		// k * C
	}

	private void paperExample()
	{
		College c1 = new College(1, 0.1);
		College c2 = new College(0.5, 0.9);
		College c3 = new College(0.48, 1);

		// order should not matter
		allColleges.add(c1);
		allColleges.add(c3);
		allColleges.add(c2);
	}
	
	private void splitThePie()
	{
		
		// College c1 = new College(0.1, 0.9);
		// College c2 = new College(0.2, 0.8);
		// etc.
		
		int N = 99;
		for (int i = 1; i <= N; i++)
		{
			double u = i / (N + 1.0);
			College c = new College(u, 1 - u);
			allColleges.add(c);
		}
	}
	
	private void uniformDomain()
	{
		
		// College c1 = new College(0.1, 0.9);
		// College c2 = new College(0.2, 0.8);
		// etc.
		
		int N = 99;
		for (int i = 1; i <= N; i++)
		{
			College c = new College(Math.random(), Math.random());
			allColleges.add(c);
		}
	}
	
	private void ItexCypress()
	{
		ItexCypress itexCypress = new ItexCypress();
		allColleges = itexCypress.colleges;
	}

	public static void main(String[] args)
	{
		SimultaneousSearch ss = new SimultaneousSearch();
		
//		ss.expectedPayoffOfAllColleges();
		
//		ss.bruteForceStar();
		
		ss.computeSigmaStar();
	}
		

	private void expectedPayoffOfAllColleges()
	{
		BigDecimal expectedValue = f.apply(allColleges);
//		System.out.println(allColleges + " -> " + expectedValue);		
	}

	public Colleges computeSigma(int N)
	{
		Colleges portfolio = new Colleges();
		
		int n;
		for (n=1; n <= N; n++)
		{
		// Step 1
		int besti = findBestMarginalImprovement(portfolio);
		
		// Step 2
		if (besti == -1)	// this codes f(Y_{n-1} + i) - f(Y_{n-1}) < c(n) - c(n-1)
			break;
		
		// Step 3
		portfolio.add(allColleges.get(besti));
		}
		
//		portfolio.sort();
		System.out.println("Sigma_" + n + " (out of " + N  + ") = \n" + portfolio.toMathematica(f));
		
		return portfolio;		
	}
	
	public Colleges computeSigmaStar()
	{
		return computeSigma(allColleges.size());
	}
	
	/**
	 * Finds the best subset of any size
	 */
	public Colleges bruteForceStar()
	{
		return bruteForce(-1);
	}
	
	/**
	 * Finds the best subset of size == N by generating and checking all of them
	 */
	public Colleges bruteForce(int N)
	{
		Set<Set<College>> collegePowerset = powerSet(allColleges.toSet());
		BigDecimal bestExpectedValue = new BigDecimal(-1);
		Colleges bestSubset = null;
		for (Set<College> subset : collegePowerset)
		{
			if (N != -1 && subset.size() != N)
				continue;
			Colleges tryPortfolio = new Colleges(new ArrayList<>(subset));
			BigDecimal expectedValue = f.apply(tryPortfolio);
//			System.out.println(tryPortfolio + " -> " + expectedValue);
			if (expectedValue.compareTo(bestExpectedValue) > 0)
			{
				bestExpectedValue = expectedValue;
				bestSubset = tryPortfolio;
			}
		}
		System.out.println("Brute force best subset: {\n" + bestSubset + "}. Exp. value: " + bestExpectedValue);
		return bestSubset;
	}
	
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	    	sets.add(new HashSet<T>());
	    	return sets;
	    }
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	    	Set<T> newSet = new HashSet<T>();
	    	newSet.add(head);
	    	newSet.addAll(set);
	    	sets.add(newSet);
	    	sets.add(set);
	    }		
	    return sets;
	}

	/**
	 * Finds the index of the best college to add to the portfolio.
	 * @return -1 when the net marginal benefit turns negative.
	 */
	private int findBestMarginalImprovement(Colleges portfolio)
	{
		int besti = -1;
		BigDecimal bestExpectedValue = new BigDecimal(-1);
		for (int i = 0; i < allColleges.size(); i++)
		{
			College c = allColleges.get(i);
			
			if (portfolio.contains(c))			// skip everything in Y_{n-1}
				continue;
			
			Colleges tryPortfolio = new Colleges(portfolio);
			tryPortfolio.add(c);
			BigDecimal expectedValue = f.apply(tryPortfolio);
//			System.out.println(tryPortfolio + " -> " + expectedValue);
			if (expectedValue.compareTo(bestExpectedValue) > 0)
			{
				bestExpectedValue = expectedValue;
				besti = i;
			}
		}
		System.out.println("Best addition: #" + besti + ": " + allColleges.get(besti) + ". Exp. value: " + String.format("%.9f",bestExpectedValue));
		
		// Step 2
		int n = portfolio.size() + 1; 							// the n from the outer loop
		Colleges currentPortfolio = new Colleges(portfolio);	// Make a copy because f changes it
		BigDecimal currentPayoff = f.apply(currentPortfolio);	// f(Y_{n-1})
		BigDecimal marginalImprovement = bestExpectedValue.subtract(currentPayoff); // f(Yn-1 + in) - f(Yn-1)
		BigDecimal cn = c.apply(n);
		BigDecimal cprev = c.apply(n-1);
		BigDecimal marginalCost;
		boolean stop;
		if (cn == null)		// c(n) = infinity
		{
			stop = true;
			System.out.println("Marginal improvement: " + String.format("%.3f",marginalImprovement)
					+ ". Marginal cost: oo, so I will not add it and stop.");
		}
		else
		{
			marginalCost = cn.subtract(cprev); 	// c(n) - c(n-1)
			stop = marginalImprovement.compareTo(marginalCost) < 0;
			System.out.println("Marginal improvement: " + String.format("%.3f",marginalImprovement) 
					+ ". Marginal cost: " + marginalCost
					+ (stop ? ". So stopping." : ". So continuing."));
		}
		
		if (stop)
			return -1;
		
		return besti;
	}
	
	public double bid(int j) 
	{
		if (j == 1)
			return 0.5;
		else
			return 0.5 + 0.5 * Math.pow(bid(j - 1), 2);
	}

}
