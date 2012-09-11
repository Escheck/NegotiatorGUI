package negotiator.qualitymeasures;

/**
 * Simple object used to the information of a negotiation outcome.
 * 
 * @author Mark Hendrikx and Alex Dirkzwager
 */
public class OutcomeInfo {
	
	private double timeOfAgreement;
	private int bids;
	private String domain;
	private boolean agreement;
	private boolean startedA;
	private String agentNameA;
	private String utilProfA;
	private double utilityA;
	private double discountedUtilityA;
	private String agentNameB;
	private String utilProfB;
	private double utilityB;
	private double discountedUtilityB;
	private double nashDistance;
	private double paretoDistance;
	private double kalaiDistance;
	private double unfortunateMovesA;
	private double unfortunateMovesB;
	private double silentMovesA;
	private double silentMovesB;
	private double niceMovesA;
	private double niceMovesB;
	private double fortunateMovesA;
	private double fortunateMovesB;
	private double selfishMovesA;
	private double selfishMovesB;
	private double concessionMovesA;
	private double concessionMovesB;
	private double explorationA;
	private double explorationB;
	private double jointExploration;
	private double socialWelfare;
	private int runNumber;
	private double ACbestTheoreticalA;
	private double ACbestDiscountedTheoreticalA;
	public double getACbestTheoreticalA() {
		return ACbestTheoreticalA;
	}

	public void setACbestTheoreticalA(double aCbestTheoreticalA) {
		ACbestTheoreticalA = aCbestTheoreticalA;
	}

	public double getACbestDiscountedTheoreticalA() {
		return ACbestDiscountedTheoreticalA;
	}

	public void setACbestDiscountedTheoreticalA(double aCbestDiscountedTheoreticalA) {
		ACbestDiscountedTheoreticalA = aCbestDiscountedTheoreticalA;
	}

	public double getACbestTheoreticalB() {
		return ACbestTheoreticalB;
	}

	public void setACbestTheoreticalB(double aCbestTheoreticalB) {
		ACbestTheoreticalB = aCbestTheoreticalB;
	}

	public double getACbestDiscountedTheoreticalB() {
		return ACbestDiscountedTheoreticalB;
	}

	public void setACbestDiscountedTheoreticalB(double aCbestDiscountedTheoreticalB) {
		ACbestDiscountedTheoreticalB = aCbestDiscountedTheoreticalB;
	}

	private double ACbestTheoreticalB;
	private double ACbestDiscountedTheoreticalB;
	private String BiddingStrategyA;
	private String AcceptanceStrategyA;
	private String OpponentModelA;
	private String BiddingStrategyB;
	private String AcceptanceStrategyB;
	private String OpponentModelB;
	
	public String getBiddingStrategyA() {
		return BiddingStrategyA;
	}

	public void setBiddingStrategyA(String biddingStrategyA) {
		BiddingStrategyA = biddingStrategyA;
	}

	public String getAcceptanceStrategyA() {
		return AcceptanceStrategyA;
	}

	public void setAcceptanceStrategyA(String acceptanceStrategyA) {
		AcceptanceStrategyA = acceptanceStrategyA;
	}

	public String getOpponentModelA() {
		return OpponentModelA;
	}

	public void setOpponentModelA(String opponentModelA) {
		OpponentModelA = opponentModelA;
	}

	public String getBiddingStrategyB() {
		return BiddingStrategyB;
	}

	public void setBiddingStrategyB(String biddingStrategyB) {
		BiddingStrategyB = biddingStrategyB;
	}

	public String getAcceptanceStrategyB() {
		return AcceptanceStrategyB;
	}

	public void setAcceptanceStrategyB(String acceptanceStrategyB) {
		AcceptanceStrategyB = acceptanceStrategyB;
	}

	public String getOpponentModelB() {
		return OpponentModelB;
	}

	public void setOpponentModelB(String opponentModelB) {
		OpponentModelB = opponentModelB;
	}

	public int getRunNumber() {
		return runNumber;
	}

	public void setRunNumber(int runNumber) {
		this.runNumber = runNumber;
	}

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
	
	public double getUtilityA() {
		return utilityA;
	}
	
	public void setUtilityA(double utilityA) {
		this.utilityA = utilityA;
	}
	
	public double getUtilityB() {
		return utilityB;
	}
	
	public void setUtilityB(double utilityB) {
		this.utilityB = utilityB;
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

	
	public double getSilentMovesA() {
		return silentMovesA;
	}

	public void setSilentMovesA(double silentMovesA) {
		this.silentMovesA = silentMovesA;
	}

	public double getSilentMovesB() {
		return silentMovesB;
	}

	public void setSilentMovesB(double silentMovesB) {
		this.silentMovesB = silentMovesB;
	}

	public double getNiceMovesA() {
		return niceMovesA;
	}

	public void setNiceMovesA(double niceMovesA) {
		this.niceMovesA = niceMovesA;
	}

	public double getNiceMovesB() {
		return niceMovesB;
	}

	public void setNiceMovesB(double niceMovesB) {
		this.niceMovesB = niceMovesB;
	}

	public double getFortunateMovesA() {
		return fortunateMovesA;
	}

	public void setFortunateMovesA(double fortunateMovesA) {
		this.fortunateMovesA = fortunateMovesA;
	}

	public double getFortunateMovesB() {
		return fortunateMovesB;
	}

	public void setFortunateMovesB(double fortunateMovesB) {
		this.fortunateMovesB = fortunateMovesB;
	}

	public double getSelfishMovesA() {
		return selfishMovesA;
	}

	public void setSelfishMovesA(double selfishMovesA) {
		this.selfishMovesA = selfishMovesA;
	}

	public double getSelfishMovesB() {
		return selfishMovesB;
	}

	public void setSelfishMovesB(double selfishMovesB) {
		this.selfishMovesB = selfishMovesB;
	}

	public double getConcessionMovesA() {
		return concessionMovesA;
	}

	public void setConcessionMovesA(double concessionMovesA) {
		this.concessionMovesA = concessionMovesA;
	}

	public double getConcessionMovesB() {
		return concessionMovesB;
	}

	public void setConcessionMovesB(double concessionMovesB) {
		this.concessionMovesB = concessionMovesB;
	}

	public double getExplorationA() {
		return explorationA;
	}

	public void setExplorationA(double explorationA) {
		this.explorationA = explorationA;
	}

	public double getExplorationB() {
		return explorationB;
	}

	public void setExplorationB(double explorationB) {
		this.explorationB = explorationB;
	}

	public double getJointExploration() {
		return jointExploration;
	}

	public void setJointExploration(double jointExploration) {
		this.jointExploration = jointExploration;
	}

	public double getSocialWelfare() {
		return socialWelfare;
	}

	public void setSocialWelfare(double socialWelfare) {
		this.socialWelfare = socialWelfare;
	}

	@Override
	public String toString() {
		return "OutcomeInfo [timeOfAgreement=" + timeOfAgreement + ", bids="
				+ bids + ", domain=" + domain + ", agreement=" + agreement
				+ ", startedA=" + startedA + ", agentNameA=" + agentNameA
				+ ", utilProfA=" + utilProfA + ", utilityA=" + utilityA
				+ ", discountedUtilityA=" + discountedUtilityA
				+ ", agentNameB=" + agentNameB + ", utilProfB=" + utilProfB
				+ ", utilityB=" + utilityB + ", discountedUtilityB="
				+ discountedUtilityB + ", nashDistance=" + nashDistance
				+ ", paretoDistance=" + paretoDistance + ", kalaiDistance="
				+ kalaiDistance + ", unfortunateMovesA=" + unfortunateMovesA
				+ ", unfortunateMovesB=" + unfortunateMovesB
				+ ", silentMovesA=" + silentMovesA + ", silentMovesB="
				+ silentMovesB + ", niceMovesA=" + niceMovesA + ", niceMovesB="
				+ niceMovesB + ", fortunateMovesA=" + fortunateMovesA
				+ ", fortunateMovesB=" + fortunateMovesB + ", selfishMovesA="
				+ selfishMovesA + ", selfishMovesB=" + selfishMovesB
				+ ", concessionMovesA=" + concessionMovesA
				+ ", concessionMovesB=" + concessionMovesB + ", explorationA="
				+ explorationA + ", explorationB=" + explorationB
				+ ", jointExploration=" + jointExploration + ", socialWelfare="
				+ socialWelfare + "]";
	}
}