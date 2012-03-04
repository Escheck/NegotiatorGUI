package negotiator.qualitymeasures;

/**
 * This class stores the information relating to the outcome of a negotiation.
 * 
 * @author Mark Hendrikx
 */
public class OutcomeInfo {
	
	private double timeOfAgreement;
	private int bids;
	private String domain;
	private boolean agreement;
	private boolean startedA;
	private String agentNameA;
	private String utilProfA;
	private double discountedUtilityA;
	private String agentNameB;
	private String utilProfB;
	private double discountedUtilityB;
	private double nashDistance;
	private double paretoDistance;
	private double kalaiDistance;
	private double unfortunateMovesA;
	private double unfortunateMovesB;
	
	public double getTimeOfAgreement() {
		return timeOfAgreement;
	}
	
	public OutcomeInfo() { }
	
	public void setTimeOfAgreement(double timeOfAgreement) {
		this.timeOfAgreement = timeOfAgreement;
	}
	
	public int getBids() {
		return bids;
	}
	
	public void setBids(int bids) {
		this.bids = bids;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public boolean isAgreement() {
		return agreement;
	}
	
	public void setAgreement(boolean agreement) {
		this.agreement = agreement;
	}
	
	public boolean isStartedA() {
		return startedA;
	}
	
	public void setStartedA(boolean startedA) {
		this.startedA = startedA;
	}
	
	public String getAgentNameA() {
		return agentNameA;
	}
	
	public void setAgentNameA(String agentNameA) {
		this.agentNameA = agentNameA;
	}
	
	public String getUtilProfA() {
		return utilProfA;
	}
	
	public void setUtilProfA(String utilProfA) {
		this.utilProfA = utilProfA;
	}
	
	public double getDiscountedUtilityA() {
		return discountedUtilityA;
	}
	
	public void setDiscountedUtilityA(double discountedUtilityA) {
		this.discountedUtilityA = discountedUtilityA;
	}
	
	public String getAgentNameB() {
		return agentNameB;
	}
	
	public void setAgentNameB(String agentNameB) {
		this.agentNameB = agentNameB;
	}
	
	public String getUtilProfB() {
		return utilProfB;
	}
	
	public void setUtilProfB(String utilProfB) {
		this.utilProfB = utilProfB;
	}
	
	public double getDiscountedUtilityB() {
		return discountedUtilityB;
	}
	
	public void setDiscountedUtilityB(double discountedUtilityB) {
		this.discountedUtilityB = discountedUtilityB;
	}
	
	public double getNashDistance() {
		return nashDistance;
	}
	
	public void setNashDistance(double nashDistance) {
		this.nashDistance = nashDistance;
	}
	
	public double getParetoDistance() {
		return paretoDistance;
	}
	
	public void setParetoDistance(double paretoDistance) {
		this.paretoDistance = paretoDistance;
	}
	
	public double getKalaiDistance() {
		return kalaiDistance;
	}
	
	public void setKalaiDistance(double kalaiDistance) {
		this.kalaiDistance = kalaiDistance;
	}
	
	public double getUnfortunateMovesA() {
		return unfortunateMovesA;
	}

	public void setUnfortunateMovesA(double unfortunateMovesA) {
		this.unfortunateMovesA = unfortunateMovesA;
	}

	public double getUnfortunateMovesB() {
		return unfortunateMovesB;
	}

	public void setUnfortunateMovesB(double unfortunateMovesB) {
		this.unfortunateMovesB = unfortunateMovesB;
	}

	public String toString() {
		String result = 
				"Time of agreement: " + timeOfAgreement + "\n" +
				"Total bids: " + bids + "\n" +
				"Domain: " + domain + "\n" +
				"Agreement reached: " + agreement + "\n" +
				"Agent A started first: " + startedA + "\n" +
				"Agent A name: " + agentNameA + "\n" +
				"Preference profile A: " + utilProfA + "\n" +
				"Final utility A: " + discountedUtilityA + "\n" +
				"Agent B name: " + agentNameB + "\n" +
				"Preference profile B: " + utilProfB + "\n" +
				"Final utility B: " + discountedUtilityB;
		return result;
	}
}
