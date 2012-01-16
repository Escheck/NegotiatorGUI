package negotiator.decoupledframework;

import java.util.ArrayList;
import java.util.HashMap;
/*
import negotiator.decoupledframework.acceptanceconditions.*;
import negotiator.decoupledframework.offeringstrategy.*;
import negotiator.decoupledframework.opponentmodel.*;
import negotiator.decoupledframework.omstrategy.*;
*/

/**
 * Simple class used to load the repository of decoupled agent components.
 * 
 * Please report bugs to author.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 16-01-12
 */
public class DecoupledAgentRepository {
	private static DecoupledAgentRepository ref;
	private HashMap<String, OfferingStrategy> offeringStrategies = new HashMap<String, OfferingStrategy>();
	private HashMap<String, AcceptanceStrategy> acceptanceStrategies = new HashMap<String, AcceptanceStrategy>();
	private HashMap<String, OpponentModel> opponentModels = new HashMap<String, OpponentModel>();
	private HashMap<String, OMStrategy> omStrategies = new HashMap<String, OMStrategy>();
	private HashMap<String, OutcomeSpace> outcomeSpaces = new HashMap<String, OutcomeSpace>();
	
	private DecoupledAgentRepository() {
		loadDecoupledAgentRepository();
	}
	
	public static DecoupledAgentRepository getInstance() {
	    if (ref == null)
	        ref = new DecoupledAgentRepository();		
	    return ref;
	  }
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void loadDecoupledAgentRepository() {
		loadOfferingStrategies();
		loadAcceptanceStrategies();
		loadOpponentModels();
		loadOMStrategies();
		loadOutcomeSpaces();
	}

	private void loadOfferingStrategies() {
		
		//ANAC2010
		/*
		offeringStrategies.put("AgentFSEGA", new AgentFSEGA_Offering());
		offeringStrategies.put("AgentSmith", new AgentSmith_Offering());
		offeringStrategies.put("Nozomi", new Nozomi_Offering());
		offeringStrategies.put("Agent K", new AgentK_Offering());
		offeringStrategies.put("Yushu", new Yushu_Offering());
		offeringStrategies.put("IAMCrazyHaggler", new IAMCrazyHaggler_Offering());
		*/
		
		//ANAC2011
		/*
		offeringStrategies.put("BRAMAgent", new BRAMAgent_Offering());
		offeringStrategies.put("HardHeaded", new HardHeaded_Offering());
		offeringStrategies.put("Nice-Tit-For-Tat", new NiceTitForTat_Offering());
		offeringStrategies.put("TheNegotiator", new TheNegotiator_Offering()); // test OM implementation (not sure about array indices)
		*/
		
		//Other OfferingStrategies
		/*
		offeringStrategies.put("Offer decreasing utility", new ChoosingAllBids());
		offeringStrategies.put("TimeDependent (concession: e)", new TimeDependent_Offering());
		offeringStrategies.put("RandomWalker", new Random_Offering());
		*/
	}
	
	private void loadAcceptanceStrategies() {
		/*
		//ANAC2010
		acceptanceStrategies.put("AgentSmith", new AC_AgentSmith());
		acceptanceStrategies.put("AgentFSEGA", new AC_AgentFSEGA());
		acceptanceStrategies.put("Nozomi", new AC_Nozomi());
		acceptanceStrategies.put("Agent K", new AC_AgentK());
		acceptanceStrategies.put("Yushu", new AC_Yushu());
		acceptanceStrategies.put("IAMcrazyHaggler", new AC_IAMcrazyHaggler());

		//ANAC2011
		acceptanceStrategies.put("NiceTitForTat", new AC_NiceTitForTat());
		acceptanceStrategies.put("TheNegotiator", new AC_TheNegotiator()); 
		acceptanceStrategies.put("HardHeaded", new AC_HardHeaded()); 
		acceptanceStrategies.put("BRAMAgent", new AC_BRAMAgent());

		//Other AC		
		acceptanceStrategies.put("False", new AC_False());
		acceptanceStrategies.put("True", new AC_True());
		acceptanceStrategies.put("Previous (alpha: a, beta: b)", new AC_Previous());
		acceptanceStrategies.put("Time (constant: c)", new AC_Time());
		acceptanceStrategies.put("Gap (constant: c)", new AC_Gap());
		acceptanceStrategies.put("Constant (constant: c)", new AC_Const());
		acceptanceStrategies.put("Next (alpha: a, beta: b)", new AC_Next());
		*/
		//AC_Combi variants
		/*
		acceptanceStrategies.put("Combi", new AC_Combi());
		acceptanceStrategies.put("CombiAvg", new AC_CombiAvg());
		acceptanceStrategies.put("CombiBestAvg", new AC_CombiBestAvg());
		acceptanceStrategies.put("CombiMax", new AC_CombiMax());
		acceptanceStrategies.put("CombiMaxInWindow", new AC_CombiMaxInWindow());
		acceptanceStrategies.put("CombiProb", new AC_CombiProb());
		*/
		
		// acceptanceStrategies.put("Multi_AC", new Multi_AcceptanceCondition());
		//acceptanceStrategies.put("Multi-Acceptance Conditions", new AC_MultiConditions());
	}
	
	private void loadOpponentModels() {
		/*
		opponentModels.put("Null", new NullModel());
		opponentModels.put("Frequency (learncoef: l, vweight: v)", new FrequencyModel());
		opponentModels.put("Bayesian (onlyusebesthypo: m)", new BayesianModel());
		opponentModels.put("Bayesian scalable", new BayesianModelScalable());
		opponentModels.put("Perfect", new PerfectModel());
		opponentModels.put("FSEGA Bayesian", new FSEGABayesianModel());
		opponentModels.put("Smith Frequency", new SmithFrequencyModel());
		*/
	}
	
	private void loadOMStrategies() {
		/*
		omStrategies.put("Offer best n (count: n)", new OfferBestN());
		omStrategies.put("Default", new NullStrategy());
		*/
	}

	private void loadOutcomeSpaces() {
		outcomeSpaces.put("Sorted outcomespace", new SortedOutcomeSpace());
		outcomeSpaces.put("Unsorted outcomespace", new OutcomeSpace());
		
		/*
		//ANAC2010
		outcomeSpaces.put("AgentFSEGA", new SortedOutcomeSpace());
		outcomeSpaces.put("AgentSmith", new SortedOutcomeSpace());
		outcomeSpaces.put("Nozomi", new SortedOutcomeSpace());
		outcomeSpaces.put("Agent K", null);
		outcomeSpaces.put("Yushu", new SortedOutcomeSpace());
		outcomeSpaces.put("IAMCrazyHaggler", null);
				
		//ANAC2011
		outcomeSpaces.put("BRAMAgent", new SortedOutcomeSpace());
		outcomeSpaces.put("HardHeaded", new OutcomeSpace());
		outcomeSpaces.put("Nice-Tit-For-Tat", new SortedOutcomeSpace());
		outcomeSpaces.put("TheNegotiator", new SortedOutcomeSpace());
				
		//Other OfferingStrategies
		outcomeSpaces.put("Offer decreasing utility", new OutcomeSpace());
		outcomeSpaces.put("TimeDependent (concession: e)", new OutcomeSpace());
		outcomeSpaces.put("RandomWalker", new OutcomeSpace());
		*/
	}
	
	public ArrayList<String> getOfferingStrategies() {
		return new ArrayList<String>(offeringStrategies.keySet());
	}
	
	public ArrayList<String>getAcceptanceStrategies() {
		return new ArrayList<String>(acceptanceStrategies.keySet());
	}
	
	public ArrayList<String> getOpponentModels() {
		return new ArrayList<String>(opponentModels.keySet());
	}
	
	public ArrayList<String> getOMStrategies() {
		return new ArrayList<String>(omStrategies.keySet());
	}
	
	public ArrayList<String> getOutcomeSpaces() {
		return new ArrayList<String>(outcomeSpaces.keySet());
	}
	
	public OfferingStrategy getOfferingStrategy(String key) {
		return offeringStrategies.get(key);
	}
	
	public AcceptanceStrategy getAcceptanceStrategy(String key) {
		return acceptanceStrategies.get(key);
	}
	
	public OpponentModel getOpponentModel(String key) {
		return opponentModels.get(key);
	}
	
	public OMStrategy getOMStrategy(String key) {
		return omStrategies.get(key);
	}
	
	public OutcomeSpace getOutcomeSpace(String key) {
		return outcomeSpaces.get(key);
	}
}