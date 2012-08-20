package negotiator.qualitymeasures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import negotiator.exceptions.Warning;
import negotiator.xml.OrderedSimpleElement;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Class which calculates statistics from the measures derived from the outcomes log.
 * First the outcomes file is parsed and the results of all matches are stored.
 * Following, averages and standard deviations are calculated per agent/opponent model/acceptance condition.
 * Finally, the results are saved in a separate xml file.
 * 
 * Using the main a tournament measures log can be created afterwards.
 * 
 * @author Mark Hendrikx, Alex Dirkzwager
 */
public class TournamentMeasures {

	/**
	 * Class which parses a normal outcomes log and stores all the information as
	 * OutcomeInfo objects.
	 */
	static class ResultsParser extends DefaultHandler {

		OutcomeInfo outcome = null;
		ArrayList<OutcomeInfo> outcomes = new ArrayList<OutcomeInfo>();
		HashSet<String> agents = new HashSet<String>();

		public void startElement(String nsURI, String strippedName,
				String tagName, Attributes attributes) throws SAXException {
			
			if (!processTournamentBasedQM(nsURI, strippedName, tagName, attributes)) {
				if (!processUtilityBasedQM(nsURI, strippedName, tagName, attributes)) {
					processTrajectoryBasedQM(nsURI, strippedName, tagName, attributes);
				}
			}
		}

		private boolean processTournamentBasedQM(String nsURI, String strippedName,
				String tagName, Attributes attributes) {
			boolean found = false;
			if (tagName.equals("NegotiationOutcome")) {
				outcome = new OutcomeInfo();
				outcome.setTimeOfAgreement(Double.parseDouble(attributes.getValue("timeOfAgreement")));
				outcome.setBids(Integer.parseInt(attributes.getValue("bids")));
				outcome.setDomain(attributes.getValue("domain"));
				outcome.setAgreement(attributes.getValue("lastAction").contains("Accept"));
				outcome.setStartedA(attributes.getValue("startingAgent").equals("A"));
				found = true;
			} else if (tagName.equals("resultsOfAgent") && attributes.getValue("agent").equals("A")) {
				outcome.setAgentNameA(attributes.getValue("agentName"));
				agents.add(outcome.getAgentNameA());
				outcome.setUtilProfA(attributes.getValue("utilspace"));
				outcome.setUtilityA(Double.parseDouble(attributes.getValue("normalizedUtility")));
				outcome.setDiscountedUtilityA(Double.parseDouble(attributes.getValue("discountedUtility")));
				found = true;
			} else if (tagName.equals("resultsOfAgent") && attributes.getValue("agent").equals("B")) {
				outcome.setAgentNameB(attributes.getValue("agentName"));
				agents.add(outcome.getAgentNameB());
				outcome.setUtilProfB(attributes.getValue("utilspace"));
				outcome.setUtilityB(Double.parseDouble(attributes.getValue("normalizedUtility")));
				outcome.setDiscountedUtilityB(Double.parseDouble(attributes.getValue("discountedUtility")));
				found = true;
			}
			return found;
		}
		
		private boolean processUtilityBasedQM(String nsURI, String strippedName,
				String tagName, Attributes attributes) {
			boolean found = false;
			if (tagName.equals("utility_based_quality_measures")) {
				outcome.setNashDistance(Double.parseDouble(attributes.getValue("nash_distance")));
				outcome.setParetoDistance(Double.parseDouble(attributes.getValue("pareto_distance")));
				outcome.setKalaiDistance(Double.parseDouble(attributes.getValue("kalai_distance")));
				outcome.setSocialWelfare(Double.parseDouble(attributes.getValue("social_welfare")));
				found = true;
			}
			return found;
		}
		
		private boolean processTrajectoryBasedQM(String nsURI,
				String strippedName, String tagName, Attributes attributes) {
			boolean found = false;
			if (tagName.equals("trajectory") && attributes.getValue("agent").equals("A")) {
				outcome.setSilentMovesA(Double.parseDouble(attributes.getValue("silent_moves")));
				outcome.setSelfishMovesA(Double.parseDouble(attributes.getValue("selfish_moves")));
				outcome.setFortunateMovesA(Double.parseDouble(attributes.getValue("fortunate_moves")));
				outcome.setUnfortunateMovesA(Double.parseDouble(attributes.getValue("unfortunate_moves")));
				outcome.setNiceMovesA(Double.parseDouble(attributes.getValue("nice_moves")));
				outcome.setConcessionMovesA(Double.parseDouble(attributes.getValue("concession_moves")));
				outcome.setExplorationA(Double.parseDouble(attributes.getValue("exploration_rate")));
				outcome.setJointExploration(Double.parseDouble(attributes.getValue("joint_exploration_rate")));
			} else if (tagName.equals("trajectory") && attributes.getValue("agent").equals("B")) {
				outcome.setSilentMovesB(Double.parseDouble(attributes.getValue("silent_moves")));
				outcome.setSelfishMovesB(Double.parseDouble(attributes.getValue("selfish_moves")));
				outcome.setFortunateMovesB(Double.parseDouble(attributes.getValue("fortunate_moves")));
				outcome.setUnfortunateMovesB(Double.parseDouble(attributes.getValue("unfortunate_moves")));
				outcome.setNiceMovesB(Double.parseDouble(attributes.getValue("nice_moves")));
				outcome.setConcessionMovesB(Double.parseDouble(attributes.getValue("concession_moves")));
				outcome.setExplorationB(Double.parseDouble(attributes.getValue("exploration_rate")));
				outcome.setJointExploration(Double.parseDouble(attributes.getValue("joint_exploration_rate")));
			}
			return found;
		}
		
		public void endElement(String nsURI, String strippedName,
				String tagName) throws SAXException {
			if (tagName.equals("NegotiationOutcome")) {
				outcomes.add(outcome);
			}
		}
		
		/**
		 * Converts an arraylist of outcomes to a seperate arraylist per run.
		 * This method comes in handy when standard deviations need to be calculated.
		 * 
		 * @return ArrayList of runs with their outcomes
		 */
		public ArrayList<ArrayList<OutcomeInfo>> getOutcomesAsRuns() {
			ArrayList<ArrayList<OutcomeInfo>> runs = new ArrayList<ArrayList<OutcomeInfo>>();
			runs.add(new ArrayList<OutcomeInfo>());
			
			for (int i = 0; i < outcomes.size(); i++) {
				boolean added = false;
				
				// check if the outcome can be added to an existing run
				for (int a = 0; a < runs.size(); a++) {
					if (!outcomeInArray(runs.get(a), outcomes.get(i))) {
						runs.get(a).add(outcomes.get(i));
						added = true;
						break;
					}
				}
				
				// if the outcome was already found in every run, create a new run
				if (!added) {
					ArrayList<OutcomeInfo> newList = new ArrayList<OutcomeInfo>();
					newList.add(outcomes.get(i));
					runs.add(newList);
				}
			}
			return runs;
		}
		
		/**
		 * Check if an outcome is found in an array of outcomes.
		 */
		private boolean outcomeInArray(ArrayList<OutcomeInfo> outcomes, OutcomeInfo outcome) {
			boolean found = false;
			for (int i = 0; i < outcomes.size(); i++) {
				if (outcomes.get(i).getAgentNameA().equals(outcome.getAgentNameA()) &&
						outcomes.get(i).getAgentNameB().equals(outcome.getAgentNameB()) &&
						outcomes.get(i).getUtilProfA().equals(outcome.getUtilProfA()) &&
						outcomes.get(i).getUtilProfB().equals(outcome.getUtilProfB())) {
					found = true;
					break;
				}
				if (outcomes.get(i).getAgentNameA().equals(outcome.getAgentNameB()) &&
						outcomes.get(i).getAgentNameB().equals(outcome.getAgentNameA()) &&
						outcomes.get(i).getUtilProfA().equals(outcome.getUtilProfB()) &&
						outcomes.get(i).getUtilProfB().equals(outcome.getUtilProfA())) {
					found = true;
					break;
				}
			}
			
			return found;
		}
		
		public ArrayList<OutcomeInfo> getOutcomes() {
			return outcomes;
		}

		public HashSet<String> getAgents() {
			return agents;
		}
	}

	/**
	 * Can be optionally used to create a tournament results log afterwards.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String in = "d:/Media Knowledge Engineering/Opponent Models 2/Data/Exp1-rounds-pareto.xml";
			String out = "d:/Media Knowledge Engineering/Opponent Models 2/Data/Result.xml";
			
			process(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method parses the file, calculates the results of all measures,
	 * and stores the results in a file.
	 * 
	 * @param log input
	 * @param tournamentLog output
	 * @throws Exception
	 */
	public static void process(String log, String tournamentLog) throws Exception {
		XMLReader xr = XMLReaderFactory.createXMLReader();
		ResultsParser handler = new ResultsParser();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		xr.parse(log);
		OrderedSimpleElement results = calculateMeasures(handler.getOutcomes(), handler.getOutcomesAsRuns(), handler.getAgents());
		writeXMLtoFile(results, tournamentLog);
	}
	
	/**
	 * Write the result given to an XML file with the given path.
	 * 
	 * @param results
	 * @param logPath
	 */
	private static void writeXMLtoFile(OrderedSimpleElement results, String logPath) {
		try {
			File tournamentLog = new File(logPath);
			if (tournamentLog.exists()) {
				tournamentLog.delete();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(tournamentLog, true));
			out.write("" + results);
			out.close();
		} catch (Exception e) {
			new Warning("Exception during writing s:" + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates all quality measures and return an XML-object with the results.
	 * 
	 * @param outcomes stored as a single array (duplicates runs, but easier to write specific methods)
	 * @param runs with outcomes
	 * @param agents
	 * @return XML-object with results of calculated measures.
	 */
public static OrderedSimpleElement calculateMeasures(ArrayList<OutcomeInfo> outcomes, ArrayList<ArrayList<OutcomeInfo>> runs, HashSet<String> agents) {
	OrderedSimpleElement tournamentQualityMeasures = new OrderedSimpleElement("tournament_quality_measures");
		for (Iterator<String> agentsIter = agents.iterator(); agentsIter.hasNext(); ) {
			String agentName = agentsIter.next();
			OrderedSimpleElement agentElement = new OrderedSimpleElement("NegotiationOutcome");
			agentElement.setAttribute("Agent", agentName);
			
			OrderedSimpleElement tournamentQM = new OrderedSimpleElement("TournamentQM");
			agentElement.addChildElement(tournamentQM);
			

			if(agentName.contains("bs")) {
				OrderedSimpleElement decoupledElementID = new OrderedSimpleElement("DecoupledElementID");
				agentElement.addChildElement(decoupledElementID);

				decoupledElementID.setAttribute("offering_strategy", getOfferingStrategyName(agentName));
				decoupledElementID.setAttribute("acceptance_strategy", getAcceptanceStrategyName(agentName));
				decoupledElementID.setAttribute("opponent_model", getOpponentModelName(agentName));
			}
			
			
			tournamentQM.setAttribute("average_time_of_agreement", getAverageTimeOfAgreement(outcomes, agentName) + "");
			tournamentQM.setAttribute("std_time_of_agreement", getStandardDeviationOfTimeOfAgreement(runs, outcomes, agentName) + "");
			tournamentQM.setAttribute("average_time_of_end_of_negotiation", getAverageEndOfNegotiation(outcomes, agentName) + "");
			tournamentQM.setAttribute("std_time_of_end_of_negotiation", getStandardDeviationOfEndOfNegotiation(runs, agentName) + "");
			tournamentQM.setAttribute("average_rounds", getAverageRounds(outcomes, agentName) + "");
			tournamentQM.setAttribute("std_rounds", getStandardDeviationOfTotalRounds(runs, agentName) + "");
			tournamentQM.setAttribute("percentage_of_agreement", getPercentageOfAgreement(outcomes, agentName) + "");
			tournamentQM.setAttribute("average_util", getAverageUtility(outcomes, agentName, false) + "");
			tournamentQM.setAttribute("std_util", getStandardDeviationOfUtility(runs, agentName) + "");
			tournamentQM.setAttribute("average_util_of_agreements", getAverageUtility(outcomes, agentName, true) + "");		
			tournamentQM.setAttribute("average_discounted_util", getAverageDiscountedUtility(outcomes, agentName, false) + "");
			tournamentQM.setAttribute("std_discounted_util", getStandardDeviationOfDiscountedUtility(runs, agentName) + "");
			tournamentQM.setAttribute("average_discounted_util_of_agreements", getAverageDiscountedUtility(outcomes, agentName, true) + "");
			
			OrderedSimpleElement utilityBasedQM = new OrderedSimpleElement("UtilityBasedQM");
			agentElement.addChildElement(utilityBasedQM);
			utilityBasedQM.setAttribute("average_nash_distance", getAverageNashDistance(outcomes, agentName, false) + "");
			utilityBasedQM.setAttribute("average_nash_distance_of_agreements", getAverageNashDistance(outcomes, agentName, true) + "");
			utilityBasedQM.setAttribute("average_pareto_distance", getAverageParetoDistance(outcomes, agentName, false) + "");
			utilityBasedQM.setAttribute("average_pareto_distance_of_agreements", getAverageParetoDistance(outcomes, agentName, true) + "");
			utilityBasedQM.setAttribute("average_kalai_distance", getAverageKalaiDistance(outcomes, agentName, false) + "");
			utilityBasedQM.setAttribute("average_kalai_distance_of_agreements", getAverageKalaiDistance(outcomes, agentName, true) + "");
			utilityBasedQM.setAttribute("average_social_welfare", getAverageSocialWelfare(outcomes, agentName, false) + "");
			utilityBasedQM.setAttribute("average_social_welfare_of_agreements", getAverageSocialWelfare(outcomes, agentName, true) + "");
			
			OrderedSimpleElement trajectorAnalysisQM = new OrderedSimpleElement("trajectorAnalysisQM");
			agentElement.addChildElement(trajectorAnalysisQM);
			
			trajectorAnalysisQM.setAttribute("average_exploration", getAverageExploration(outcomes, agentName) + "");
			trajectorAnalysisQM.setAttribute("average_joint_exploration", getAverageJointExploration(outcomes, agentName) + "");

			// discard invalid trajectories
			ArrayList<OutcomeInfo> newOutcomes = discardInvalidTrajectories(outcomes);

			trajectorAnalysisQM.setAttribute("average_unfortunate_moves", getAveragePercentageOfUnfortunateMoves(newOutcomes, agentName) + "");
			trajectorAnalysisQM.setAttribute("average_fortunate_moves", getAveragePercentageOfFortunateMoves(newOutcomes, agentName) + "");
			trajectorAnalysisQM.setAttribute("average_nice_moves", getAveragePercentageOfNiceMoves(newOutcomes, agentName) + "");
			trajectorAnalysisQM.setAttribute("average_selfish_moves", getAveragePercentageOfSelfishMoves(newOutcomes, agentName) + "");
			trajectorAnalysisQM.setAttribute("average_concession_moves", getAveragePercentageOfConcessionMoves(newOutcomes, agentName) + "");
			trajectorAnalysisQM.setAttribute("average_silent_moves", getAveragePercentageOfSilentMoves(newOutcomes, agentName) + "");
			
			tournamentQualityMeasures.addChildElement(agentElement);
	    }
		return tournamentQualityMeasures;
	}

	/**
	 * Unfortunately, it sometimes occurs that a party made less than two moves in a negotiation.
	 * In this case, trajactory analysis is impossible and the data should be discarded.
	 * 
	 * @param outcomes
	 * @return
	 */
	private static ArrayList<OutcomeInfo> discardInvalidTrajectories(ArrayList<OutcomeInfo> outcomes) {
		ArrayList<OutcomeInfo> outcomesToRemove = new ArrayList<OutcomeInfo>();
		ArrayList<OutcomeInfo> newOutcomes = new ArrayList<OutcomeInfo>(outcomes);
		
		for (OutcomeInfo outcome : newOutcomes) {
			if (outcome.getSelfishMovesA() == 0 && outcome.getNiceMovesA() == 0 &&
					outcome.getFortunateMovesA() == 0 && outcome.getUnfortunateMovesA() == 0 &&
					outcome.getConcessionMovesA() == 0 && outcome.getSilentMovesA() == 0) {
				outcomesToRemove.add(outcome);
			}
		}
		newOutcomes.removeAll(outcomesToRemove);
		return newOutcomes;
	}

	/**
	 * Calculates the standard deviation of the utility of a run.
	 * 
	 * @param runs
	 * @param agentName
	 */
	private static double getStandardDeviationOfUtility(ArrayList<ArrayList<OutcomeInfo>> runs, String agentName) {
		double sumOfAverages = 0;
		double squaredSumOfDeviations = 0;
		double[] results = new double[runs.size()];

		if (runs.size() > 1) {
			for (int i = 0; i < runs.size(); i++) {
				double averageOfRun = getAverageUtility(runs.get(i), agentName, false);
				
				sumOfAverages += averageOfRun;
				results[i] = averageOfRun;
			}
			double averageOfRuns = sumOfAverages / runs.size();
			for (int i = 0; i < runs.size(); i++) {
				squaredSumOfDeviations += Math.pow((results[i] - averageOfRuns), 2);
			}
			// n-1 due to Bessel's correction
			double variance = squaredSumOfDeviations / (runs.size() - 1);
			return Math.sqrt(variance);
		}
		return 0;
	}
	
	/**
	 * Calculates the standard deviation of the discounted utility of a run.
	 * 
	 * @param runs
	 * @param agentName
	 */
	private static double getStandardDeviationOfDiscountedUtility(ArrayList<ArrayList<OutcomeInfo>> runs, String agentName) {
		double sumOfAverages = 0;
		double squaredSumOfDeviations = 0;
		double[] results = new double[runs.size()];
		
		if (runs.size() > 1) {
			for (int i = 0; i < runs.size(); i++) {
				double averageOfRun = getAverageDiscountedUtility(runs.get(i), agentName, false);
				
				sumOfAverages += averageOfRun;
				results[i] = averageOfRun;
			}
			double averageOfRuns = sumOfAverages / runs.size();
			for (int i = 0; i < runs.size(); i++) {
				squaredSumOfDeviations += Math.pow((results[i] - averageOfRuns), 2);
			}
			// n-1 due to Bessel's correction
			double variance = squaredSumOfDeviations / (runs.size() - 1);
			return Math.sqrt(variance);
		}
		return 0;
	}
	
	/**
	 * Calculates the standard deviation of the amount of rounds.
	 * Also uses outcomes because of isAgreement (average of averages != average of all)
	 * 
	 * @param runs
	 * @param outcomes
	 * @param agentName
	 */
	private static double getStandardDeviationOfTotalRounds(ArrayList<ArrayList<OutcomeInfo>> runs, String agentName) {
		double sumOfAverages = 0;
		double squaredSumOfDeviations = 0;
		double[] results = new double[runs.size()];
		
		if (runs.size() > 1) {
			for (int i = 0; i < runs.size(); i++) {
				double averageOfRun = getAverageRounds(runs.get(i), agentName);
				sumOfAverages += averageOfRun;
				results[i] = averageOfRun;
			}
			double averageOfRuns = sumOfAverages / runs.size();
			for (int i = 0; i < runs.size(); i++) {
				squaredSumOfDeviations += Math.pow((results[i] - averageOfRuns), 2);
			}
			// n-1 due to Bessel's correction
			double variance = squaredSumOfDeviations / (runs.size() - 1);
			return Math.sqrt(variance);
		}
		return 0;
	}

	/**
	 * Calculates the standard deviation of the time of agreement.
	 * Also uses outcomes because of isAgreement (average of averages != average of all)
	 * @param runs
	 * @param outcomes
	 * @param agentName
	 */
	private static double getStandardDeviationOfTimeOfAgreement(ArrayList<ArrayList<OutcomeInfo>> runs, ArrayList<OutcomeInfo> outcomes, String agentName) {
		double squaredSumOfDeviations = 0;
		double[] results = new double[runs.size()];
		
		if (runs.size() > 1) {
			for (int i = 0; i < runs.size(); i++) {
				double averageOfRun = getAverageTimeOfAgreement(runs.get(i), agentName);
				results[i] = averageOfRun;
			}
			double averageOfRuns = getAverageTimeOfAgreement(outcomes, agentName);
			for (int i = 0; i < runs.size(); i++) {
				squaredSumOfDeviations += Math.pow((results[i] - averageOfRuns), 2);
			}
			// n-1 due to Bessel's correction
			double variance = squaredSumOfDeviations / (runs.size() - 1);
			return Math.sqrt(variance);
		}
		return 0;
	}
	
	/**
	 * Calculates the standard deviation of the time of end of negotiation.
	 * 
	 * @param runs
	 * @param agentName
	 * @return
	 */
	private static double getStandardDeviationOfEndOfNegotiation(ArrayList<ArrayList<OutcomeInfo>> runs, String agentName) {
		double sumOfAverages = 0;
		double squaredSumOfDeviations = 0;
		double[] results = new double[runs.size()];
		
		if (runs.size() > 1) {
			for (int i = 0; i < runs.size(); i++) {
				double averageOfRun = getAverageEndOfNegotiation(runs.get(i), agentName);
				
				sumOfAverages += averageOfRun;
				results[i] = averageOfRun;
			}
			double averageOfRuns = sumOfAverages / runs.size();
			for (int i = 0; i < runs.size(); i++) {
				squaredSumOfDeviations += Math.pow((results[i] - averageOfRuns), 2);
			}
			// n-1 due to Bessel's correction
			double variance = squaredSumOfDeviations / (runs.size() - 1);
			return Math.sqrt(variance);
		}
		return 0;
	}
	
	/**
	 * Calculates the average non-discounted utility an agent.
	 * Optionally, matches without agreement are ignored.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @param onlyAgreements
	 * @return average utility
	 */
	private static double getAverageUtility(ArrayList<OutcomeInfo> outcomes, 
											String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double utility = 0;

		for (OutcomeInfo outcome : outcomes) {
			boolean found = false;
			if (outcome.getAgentNameA().equals(agentName)) {
				utility += outcome.getUtilityA();
				found = true;
			} else if (outcome.getAgentNameB().equals(agentName)) {
				utility += outcome.getUtilityB();
				found = true;
			}
			if (found && (outcome.isAgreement() || !onlyAgreements)) {
				totalSessions++;
			}
		}
		return utility / (double)totalSessions;
	}

	/**
	 * Calculates the average discounted utility an agent.
	 * Optionally, matches without agreement are ignored.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @param onlyAgreements
	 * @return average utility
	 */
	private static double getAverageDiscountedUtility(ArrayList<OutcomeInfo> outcomes,
			String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double utility = 0;

		for (OutcomeInfo outcome : outcomes) {
			boolean found = false;
			if (outcome.getAgentNameA().equals(agentName)) {
				utility += outcome.getDiscountedUtilityA();
				found = true;
			} else if (outcome.getAgentNameB().equals(agentName)) {
				utility += outcome.getDiscountedUtilityB();
				found = true;
			}
			if (found && (outcome.isAgreement() || !onlyAgreements)) {
				totalSessions++;
			}
		}
		return utility / (double) totalSessions;
	}
	
	/**
	 * Calculates the percentage of agreement of an agent.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return percentage of agreement
	 */
	private static double getPercentageOfAgreement(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		int agreement = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				if (outcome.isAgreement()) {
					agreement++;
				}
			}
		}
		return (double)agreement / (double)totalSessions * 100;
	}
	
	/**
	 * Calculates the average time of agreement of an agent.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @param onlyAgreements
	 * @return average time of agreement
	 */
	private static double getAverageTimeOfAgreement(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double timeOfAgreement = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				if (outcome.isAgreement()) {
					totalSessions++;
					timeOfAgreement += outcome.getTimeOfAgreement();
				}
			}
		}
		return (double)timeOfAgreement / (double)totalSessions;
	}
	
	/**
	 * Calculates the average time of the end of the negotiation of an agent.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @param onlyAgreements
	 * @return average time of end of negotiation
	 */
	private static double getAverageEndOfNegotiation(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double timeOfAgreement = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				timeOfAgreement += outcome.getTimeOfAgreement();
				totalSessions++;
			}
		}
		return (double)timeOfAgreement / totalSessions;
	}
	
	/**
	 * Calculates the average amount of rounds.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return average amount of rounds
	 */
	private static double getAverageRounds(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		int totalBids = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalBids += outcome.getBids();
			}
		}
		return (double)totalBids / (double)totalSessions;
	}
	
	/**
	 * Returns the average individual exploration rate (percentage of possible unique bids
	 * offered by self).
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return average individual exploration rate
	 */
	private static double getAverageExploration(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalExploration = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName)) {
				totalSessions++;
				totalExploration += outcome.getExplorationA();
			} else {
				if (outcome.getAgentNameB().equals(agentName)) {
					totalSessions++;
					totalExploration += outcome.getExplorationB();
				}
			}
		}
		return (double)totalExploration / (double)totalSessions;
	}
	
	/**
	 * Returns the average joint exploration rate (percentage of possible unique bids
	 * offered by both parties).
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return average individual exploration rate
	 */
	private static double getAverageJointExploration(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalJointExploration = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalJointExploration += outcome.getJointExploration();
			}
		}
		return (double)totalJointExploration / (double)totalSessions;
	}
	
	/**
	 * Calculates the average Nash distance of an agreement.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @param onlyAgreements ignore outcomes which result in non-agreement
	 * @return average Nash distance
	 */
	private static double getAverageNashDistance(ArrayList<OutcomeInfo> outcomes, String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double totalNash = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				if (!onlyAgreements || outcome.isAgreement()) {
					totalSessions++;
					totalNash += outcome.getNashDistance();
				}
			}
		}
		return totalNash / totalSessions;
	}
	
	/**
	 * Calculates the average social welfare of an agreement.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @param onlyAgreements ignore outcomes which result in non-agreement
	 * @return average socialwelfare distance
	 */
	private static double getAverageSocialWelfare(ArrayList<OutcomeInfo> outcomes, String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double totalSocialWelfare = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				if (!onlyAgreements || outcome.isAgreement()) {
					totalSessions++;
					totalSocialWelfare += outcome.getSocialWelfare();
				}
			}
		}
		return totalSocialWelfare / totalSessions;
	}
	
	/**
	 * Calculates the average Pareto distance of an agreement.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return average Pareto distance
	 */
	private static double getAverageParetoDistance(ArrayList<OutcomeInfo> outcomes, String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double totalPareto = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				if (!onlyAgreements || outcome.isAgreement()) {
					totalSessions++;
					totalPareto += outcome.getParetoDistance();
				}
			}
		}
		return totalPareto / totalSessions;
	}
	
	/**
	 * Calculates the average Kalai distance of an agreement.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return average Kalai distance
	 */
	private static double getAverageKalaiDistance(ArrayList<OutcomeInfo> outcomes, String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double totalKalai = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				if (!onlyAgreements || outcome.isAgreement()) {
					totalSessions++;
					totalKalai += outcome.getKalaiDistance();
				}
			}
		}
		return totalKalai / totalSessions;
	}
	
	/**
	 * Calculates the average percentage of unfortunate moves.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return percentage of unfortunate moves
	 */
	private static double getAveragePercentageOfUnfortunateMoves(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalUnfortunateMovesPerc = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName)) {
				totalSessions++;
				totalUnfortunateMovesPerc += outcome.getUnfortunateMovesA();
			} else if (outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalUnfortunateMovesPerc += outcome.getUnfortunateMovesB();
			}
		}

		return totalUnfortunateMovesPerc / totalSessions;
	}
	
	/**
	 * Calculates the average percentage of fortunate moves.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return percentage of fortunate moves
	 */
	private static double getAveragePercentageOfFortunateMoves(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalFortunateMovesPerc = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName)) {
				totalSessions++;
				totalFortunateMovesPerc += outcome.getFortunateMovesA();
			} else if (outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalFortunateMovesPerc += outcome.getFortunateMovesB();
			}
		}
		return totalFortunateMovesPerc / totalSessions;
	}
	
	/**
	 * Calculates the average percentage of nice moves.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return percentage of nice moves
	 */
	private static double getAveragePercentageOfNiceMoves(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalNiceMovesPerc = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName)) {
				totalSessions++;
				totalNiceMovesPerc += outcome.getNiceMovesA();
			} else if (outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalNiceMovesPerc += outcome.getNiceMovesB();
			}
		}
		return totalNiceMovesPerc / totalSessions;
	}
	
	/**
	 * Calculates the average percentage of silent moves.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return percentage of silent moves
	 */
	private static double getAveragePercentageOfSilentMoves(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalSilentMovesPerc = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName)) {
				totalSessions++;
				totalSilentMovesPerc += outcome.getSilentMovesA();
			} else if (outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalSilentMovesPerc += outcome.getSilentMovesB();
			}
		}
		return totalSilentMovesPerc / totalSessions;
	}
	
	/**
	 * Calculates the average percentage of concession moves.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return percentage of concession moves
	 */
	private static double getAveragePercentageOfConcessionMoves(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalConcessionMovesPerc = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName)) {
				totalSessions++;
				totalConcessionMovesPerc += outcome.getConcessionMovesA();
			} else if (outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalConcessionMovesPerc += outcome.getConcessionMovesB();
			}
		}
		return totalConcessionMovesPerc / totalSessions;
	}
	
	/**
	 * Calculates the average percentage of selfish moves.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return percentage of selfish moves
	 */
	private static double getAveragePercentageOfSelfishMoves(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalSelfishMovesPerc = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName)) {
				totalSessions++;
				totalSelfishMovesPerc += outcome.getSelfishMovesA();
			} else if (outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalSelfishMovesPerc += outcome.getSelfishMovesB();
			}
		}
		return totalSelfishMovesPerc / totalSessions;
	}
	
	/**
	 * Method used for getting the name of an offering strategy component
	 * of a decoupled agent.
	 * 
	 * @param agentName
	 * @return name of bidding strategy
	 */
	private static String getOfferingStrategyName(String agentName) {
		int left = agentName.indexOf("bs:");
		int right = agentName.indexOf("as:");

		String agentOfferingStrategyName = agentName.substring(left + 3, right);
		agentOfferingStrategyName = agentOfferingStrategyName.trim();

		return agentOfferingStrategyName;
	}
	
	/**
	 * Method used for getting the name of an acceptance strategy component
	 * of a decoupled agent.
	 * 
	 * @param agentName
	 * @return name of acceptance strategy
	 */
	private static String getAcceptanceStrategyName(String agentName) {
		int left = agentName.indexOf("as:");
		int right = agentName.indexOf("om:");

		String agentAcceptanceStrategyName = agentName.substring(left + 3, right);
		agentAcceptanceStrategyName = agentAcceptanceStrategyName.trim();
		
		return agentAcceptanceStrategyName;
	}
	
	/**
	 * Method used for getting the name of an opponent model component
	 * of a decoupled agent.
	 * 
	 * @param agentName
	 * @return name of opponent model
	 */
	private static String getOpponentModelName(String agentName) {
		int left = agentName.indexOf("om:");
		int right = agentName.indexOf("oms:");

		String agentOpponentModelName = agentName.substring(left + 3, right);
		agentOpponentModelName = agentOpponentModelName.trim();
		
		return agentOpponentModelName;
	}
}