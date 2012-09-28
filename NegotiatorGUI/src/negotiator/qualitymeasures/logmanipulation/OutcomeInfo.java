package negotiator.qualitymeasures.logmanipulation;

/**
 * Simple object used to the information of a negotiation outcome.
 * 
 * @author Mark Hendrikx and Alex Dirkzwager
 */
public class OutcomeInfo {
	
	private String agentAname;
	private	String agentBname;
	private	String agentAclass;
	private	String agentBclass;
	private	double agentAutility;
	private	double agentButility;
	private double agentAutilityDiscount;
	private double agentButilityDiscount;
	private String errorRemarks;
	private double agentAmaxUtil;
	private double agentBmaxUtil;
	private String domainName;
	private	String agentAutilSpaceName;
	private	String agentButilSpaceName;
	private	double timeOfAgreement;
	private String acceptedBy;
	
	public OutcomeInfo() {
		
	}
	
	public OutcomeInfo(String agentAname,
			String agentBname,
			String agentAclass,
			String agentBclass,
			double agentAutility,
			double agentButility,
			double agentAutilityDiscount,
			double agentButilityDiscount,
			String errorRemarks,
			double agentAmaxUtil,
			double agentBmaxUtil,
			String domainName,
			String agentAutilSpaceName,
			String agentButilSpaceName,
			double time,
			String acceptedBy
	)
	{
		this.agentAname = agentAname;
		this.agentBname = agentBname;
		this.agentAclass = agentAclass;
		this.agentBclass = agentBclass;
		this. agentAutility = agentAutility;
		this. agentButility = agentButility;
		this. agentAutilityDiscount = agentAutilityDiscount;
		this. agentButilityDiscount = agentButilityDiscount;
		this.errorRemarks = errorRemarks;
		this. agentAmaxUtil = agentAmaxUtil;
		this. agentBmaxUtil = agentBmaxUtil;
		this.domainName = domainName;
		this.agentAutilSpaceName = agentAutilSpaceName;
		this.agentButilSpaceName =  agentButilSpaceName;
		this. timeOfAgreement = time;
		this.acceptedBy = acceptedBy;
	}
	
	public String getAgentAname() {
		return agentAname;
	}
	public void setAgentAname(String agentAname) {
		this.agentAname = agentAname;
	}
	public String getAgentBname() {
		return agentBname;
	}
	public void setAgentBname(String agentBname) {
		this.agentBname = agentBname;
	}
	public String getAgentAclass() {
		return agentAclass;
	}
	public void setAgentAclass(String agentAclass) {
		this.agentAclass = agentAclass;
	}
	public String getAgentBclass() {
		return agentBclass;
	}
	public void setAgentBclass(String agentBclass) {
		this.agentBclass = agentBclass;
	}
	public Double getAgentAutility() {
		return agentAutility;
	}
	public void setAgentAutility(Double agentAutility) {
		this.agentAutility = agentAutility;
	}
	public double getAgentButility() {
		return agentButility;
	}
	public void setAgentButility(double agentButility) {
		this.agentButility = agentButility;
	}
	public double getAgentAutilityDiscount() {
		return agentAutilityDiscount;
	}
	public void setAgentAutilityDiscount(double agentAutilityDiscount) {
		this.agentAutilityDiscount = agentAutilityDiscount;
	}
	public double getAgentButilityDiscount() {
		return agentButilityDiscount;
	}
	public void setAgentButilityDiscount(double agentButilityDiscount) {
		this.agentButilityDiscount = agentButilityDiscount;
	}
	public String getErrorRemarks() {
		return errorRemarks;
	}
	public void setErrorRemarks(String errorRemarks) {
		this.errorRemarks = errorRemarks;
	}
	public double getAgentAmaxUtil() {
		return agentAmaxUtil;
	}
	public void setAgentAmaxUtil(double agentAmaxUtil) {
		this.agentAmaxUtil = agentAmaxUtil;
	}
	public double getAgentBmaxUtil() {
		return agentBmaxUtil;
	}
	public void setAgentBmaxUtil(double agentBmaxUtil) {
		this.agentBmaxUtil = agentBmaxUtil;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getAgentAutilSpaceName() {
		return agentAutilSpaceName;
	}
	public void setAgentAutilSpaceName(String agentAutilSpaceName) {
		this.agentAutilSpaceName = agentAutilSpaceName;
	}
	public String getAgentButilSpaceName() {
		return agentButilSpaceName;
	}
	public void setAgentButilSpaceName(String agentButilSpaceName) {
		this.agentButilSpaceName = agentButilSpaceName;
	}
	public double getTimeOfAgreement() {
		return timeOfAgreement;
	}
	public void setTimeOfAgreement(double time) {
		this.timeOfAgreement = time;
	}
	public String getAcceptedBy() {
		return acceptedBy;
	}
	public void setAcceptedBy(String acceptedBy) {
		this.acceptedBy = acceptedBy;
	}
		
	
		
}