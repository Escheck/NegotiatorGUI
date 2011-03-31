package agents;

public class TimeDependentAgentBoulware extends TimeDependentAgent
{
	@Override
	public double getE()
	{
		return 0.2;
	}
	
	@Override
	public String getName()
	{
		return "Boulware";
	}
}
