package negotiator.qualitymeasures.logmanipulation;

public class OutcomeInfoDerived extends OutcomeInfo{
	
	private String currentTime;
	private int bids;
	private boolean agreement;
	private int runNr;
	private String startingAgent;
	private String biddingStrategyA;
	private String acceptanceStrategyA;
	private String opponentModelA;
	private String biddingStrategyB;
	private String acceptanceStrategyB;
	private String opponentModelB;
	private String lastAction;
	
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
	private double ACbestTheoreticalA;
	private double ACbestDiscountedTheoreticalA;
	private double ACbestTheoreticalB;
	private double ACbestDiscountedTheoreticalB;
	private double minDemandedUtilityA;
	private double minDemandedUtilityB;
	private double Total_CR_A;
	private double Total_CR_B;
	private double FYUA;
	private double FYUB;
	private double BS_CR_A;
	private double BS_CR_B;
	private double AC_CR_A;
	private double AC_CR_B;
	private double Normalized_AC_CR_A;
	private double Normalized_AC_CR_B;
	private double normalizedUtilityA;
	private double normalizedUtilityB;
	private double percParetoBidsA;
	private double percParetoBidsB;
	
	public String getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}
	public int getBids() {
		return bids;
	}
	public void setBids(int bids) {
		this.bids = bids;
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
	public double getMinDemandedUtilityA() {
		return minDemandedUtilityA;
	}
	public void setMinDemandedUtilityA(double minDemandedUtilityA) {
		this.minDemandedUtilityA = minDemandedUtilityA;
	}
	public double getMinDemandedUtilityB() {
		return minDemandedUtilityB;
	}
	public void setMinDemandedUtilityB(double minDemandedUtilityB) {
		this.minDemandedUtilityB = minDemandedUtilityB;
	}
	public double getTotal_CR_A() {
		return Total_CR_A;
	}
	public void setTotal_CR_A(double total_CR_A) {
		Total_CR_A = total_CR_A;
	}
	public double getTotal_CR_B() {
		return Total_CR_B;
	}
	public void setTotal_CR_B(double total_CR_B) {
		Total_CR_B = total_CR_B;
	}
	public double getBS_CR_A() {
		return BS_CR_A;
	}
	public void setBS_CR_A(double bS_CR_A) {
		BS_CR_A = bS_CR_A;
	}
	public double getBS_CR_B() {
		return BS_CR_B;
	}
	public void setBS_CR_B(double bS_CR_B) {
		BS_CR_B = bS_CR_B;
	}
	public double getAC_CR_A() {
		return AC_CR_A;
	}
	public void setAC_CR_A(double aC_CR_A) {
		AC_CR_A = aC_CR_A;
	}
	public double getAC_CR_B() {
		return AC_CR_B;
	}
	public void setAC_CR_B(double aC_CR_B) {
		AC_CR_B = aC_CR_B;
	}
	public double getNormalized_AC_CR_A() {
		return Normalized_AC_CR_A;
	}
	public void setNormalized_AC_CR_A(double normalized_AC_CR_A) {
		Normalized_AC_CR_A = normalized_AC_CR_A;
	}
	public double getNormalized_AC_CR_B() {
		return Normalized_AC_CR_B;
	}
	public void setNormalized_AC_CR_B(double normalized_AC_CR_B) {
		Normalized_AC_CR_B = normalized_AC_CR_B;
	}
	public double getNormalizedUtilityA() {
		return normalizedUtilityA;
	}
	public void setNormalizedUtilityA(double normalizedUtilityA) {
		this.normalizedUtilityA = normalizedUtilityA;
	}
	public double getNormalizedUtilityB() {
		return normalizedUtilityB;
	}
	public void setNormalizedUtilityB(double normalizedUtilityB) {
		this.normalizedUtilityB = normalizedUtilityB;
	}
	public int getRunNr() {
		return runNr;
	}
	public void setRunNr(int runNr) {
		this.runNr = runNr;
	}
	public boolean isAgreement() {
		return agreement;
	}
	public void setAgreement(boolean agreement) {
		this.agreement = agreement;
	}
	public String getStartingAgent() {
		return startingAgent;
	}
	public void setStartingAgent(String startingAgent) {
		this.startingAgent = startingAgent;
	}
	public String getBiddingStrategyA() {
		return biddingStrategyA;
	}
	public void setBiddingStrategyA(String biddingStrategyA) {
		this.biddingStrategyA = biddingStrategyA;
	}
	public String getAcceptanceStrategyA() {
		return acceptanceStrategyA;
	}
	public void setAcceptanceStrategyA(String acceptanceStrategyA) {
		this.acceptanceStrategyA = acceptanceStrategyA;
	}
	public String getOpponentModelA() {
		return opponentModelA;
	}
	public void setOpponentModelA(String opponentModelA) {
		this.opponentModelA = opponentModelA;
	}
	public String getBiddingStrategyB() {
		return biddingStrategyB;
	}
	public void setBiddingStrategyB(String biddingStrategyB) {
		this.biddingStrategyB = biddingStrategyB;
	}
	public String getAcceptanceStrategyB() {
		return acceptanceStrategyB;
	}
	public void setAcceptanceStrategyB(String acceptanceStrategyB) {
		this.acceptanceStrategyB = acceptanceStrategyB;
	}
	public String getOpponentModelB() {
		return opponentModelB;
	}
	public void setOpponentModelB(String opponentModelB) {
		this.opponentModelB = opponentModelB;
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
	public String getLastAction() {
		return lastAction;
	}
	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}
	public double getFYUA() {
		return FYUA;
	}
	public void setFYUA(double fYUA) {
		FYUA = fYUA;
	}
	public double getFYUB() {
		return FYUB;
	}
	public void setFYUB(double fYUB) {
		FYUB = fYUB;
	}
	
	public double getPercParetoBidsA() {
		return percParetoBidsA;
	}

	public void setPercParetoBidsA(double percParetoBidsA) {
		this.percParetoBidsA = percParetoBidsA;
	}

	public double getPercParetoBidsB() {
		return percParetoBidsB;
	}

	public void setPercParetoBidsB(double percParetoBidsB) {
		this.percParetoBidsB = percParetoBidsB;
	}
}