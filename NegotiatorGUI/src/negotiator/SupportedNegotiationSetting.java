package negotiator;

import negotiator.utility.UTILITYSPACETYPE;

/**
 * Indicates what negotiation settings are supported by an agent.
 */
public class SupportedNegotiationSetting
{
	/** LINEAR means: only supports linear domains, NONLINEAR means it supports any utility space (both linear and non-linear) */
	private UTILITYSPACETYPE utilityspaceType;
	
	public SupportedNegotiationSetting()
	{
	}
	
	public static SupportedNegotiationSetting getLinearUtilitySpaceInstance()
	{
		SupportedNegotiationSetting s = new SupportedNegotiationSetting();
		s.setUtilityspaceType(UTILITYSPACETYPE.LINEAR);
		return s;
	}
	
	public static SupportedNegotiationSetting getDefault()
	{
		return new SupportedNegotiationSetting();
	}
		
	boolean supportsOnlyLinearUtilitySpaces()
	{
		return utilityspaceType == UTILITYSPACETYPE.LINEAR;
	}

	public UTILITYSPACETYPE getUtilityspaceType()
	{
		return utilityspaceType;
	}

	public void setUtilityspaceType(UTILITYSPACETYPE utilityspaceType)
	{
		this.utilityspaceType = utilityspaceType;
	}
	
	
	
	
}
