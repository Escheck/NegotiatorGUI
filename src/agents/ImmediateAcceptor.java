package agents;

import negotiator.Bid;

/**
 * This agent does not concede, but will accept anything equal to or above the reservation value.
 * For undiscounted domain only.
 */
public class ImmediateAcceptor extends TimeDependentAgent
{
	@Override
	public double getE()
	{
		return 0;
	}
	
	@Override
	public String getName()
	{
		return "Immediate Acceptor";
	}
	
	@Override
	public boolean isAcceptable(Bid plannedBid)
	{
		Bid opponentLastBid = getOpponentLastBid();
		if(getUtility(opponentLastBid) >= utilitySpace.getReservationValue())
			return true;
		return false;
	}
}
