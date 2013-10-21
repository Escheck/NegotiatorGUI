package negotiator.qualitymeasures.logmanipulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import negotiator.qualitymeasures.logmanipulation.NegotiationLogParser.ResultsParser;
import negotiator.xml.OrderedSimpleElement;

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
	 * Can be optionally used to create a tournament results log afterwards.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String in = "c:/Users/Mark/workspace/Alex branch/Genius/BOA2013 experiment/Agents/SD/CUHKAgent.xml";
			String out = "c:/Users/Mark/workspace/Alex branch/Genius/BOA2013 experiment/Agents/SD/CUHKAgent STD.xml";
			
			
			ResultsParser resultsParser = NegotiationLogParser.parseLog(in);

			OrderedSimpleElement results = calculateMeasures(resultsParser.getOutcomes(), resultsParser.getOutcomesAsRuns(), resultsParser.getAgents());

			NegotiationLogParser.writeXMLtoFile(results, out);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runTournamentMeasures(String in, String out) throws Exception{
		ResultsParser resultsParser = NegotiationLogParser.parseLog(in);
		OrderedSimpleElement results = calculateMeasures(resultsParser.getOutcomes(), resultsParser.getOutcomesAsRuns(), resultsParser.getAgents());
		NegotiationLogParser.writeXMLtoFile(results, out);
	}
	
	
	/**
	 * Calculates all quality measures and return an XML-object with the results.
	 * 
	 * @param outcomes stored as a single array (duplicates runs, but easier to write specific methods)
	 * @param runs with outcomes
	 * @param agents
	 * @return XML-object with results of calculated measures.
	 */
	public static OrderedSimpleElement calculateMeasures(ArrayList<OutcomeInfoDerived> outcomes, ArrayList<ArrayList<OutcomeInfoDerived>> runs, HashSet<String> agents) {
		OrderedSimpleElement tournamentQualityMeasures = new OrderedSimpleElement("tournament_quality_measures");
		for (Iterator<String> agentsIter = agents.iterator(); agentsIter.hasNext(); ) {
			String agentName = agentsIter.next();
			OrderedSimpleElement agentElement = new OrderedSimpleElement("NegotiationOutcome");
			agentElement.setAttribute("Agent", agentName);
		
			OrderedSimpleElement tournamentQM = new OrderedSimpleElement("TournamentQM");
			agentElement.addChildElement(tournamentQM);
			
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
			trajectorAnalysisQM.setAttribute("perc_pareto_bids", getAveragePercentageParetoBids(outcomes, agentName) + "");

			// discard invalid trajectories
			ArrayList<OutcomeInfoDerived> newOutcomes = discardInvalidTrajectories(outcomes);

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
	private static ArrayList<OutcomeInfoDerived> discardInvalidTrajectories(ArrayList<OutcomeInfoDerived> outcomes) {
		ArrayList<OutcomeInfoDerived> outcomesToRemove = new ArrayList<OutcomeInfoDerived>();
		ArrayList<OutcomeInfoDerived> newOutcomes = new ArrayList<OutcomeInfoDerived>(outcomes);
		
		for (OutcomeInfoDerived outcome : newOutcomes) {
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
	private static double getStandardDeviationOfUtility(ArrayList<ArrayList<OutcomeInfoDerived>> runs, String agentName) {
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
	private static double getStandardDeviationOfDiscountedUtility(ArrayList<ArrayList<OutcomeInfoDerived>> runs, String agentName) {
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
	private static double getStandardDeviationOfTotalRounds(ArrayList<ArrayList<OutcomeInfoDerived>> runs, String agentName) {
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
	private static double getStandardDeviationOfTimeOfAgreement(ArrayList<ArrayList<OutcomeInfoDerived>> runs, ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
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
	private static double getStandardDeviationOfEndOfNegotiation(ArrayList<ArrayList<OutcomeInfoDerived>> runs, String agentName) {
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
	private static double getAverageUtility(ArrayList<OutcomeInfoDerived> outcomes, 
											String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double utility = 0;

		for (OutcomeInfoDerived outcome : outcomes) {
			// if outcome is an agreement, or we do not care about it
			if ((outcome.isAgreement() || !onlyAgreements)) {
				if (outcome.getAgentAname().equals(agentName)) {
					utility += outcome.getAgentAutility();
					totalSessions++;
				} else if (outcome.getAgentBname().equals(agentName)) {
					utility += outcome.getAgentButility();
					totalSessions++;
				}
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
	private static double getAverageDiscountedUtility(ArrayList<OutcomeInfoDerived> outcomes,
			String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double utility = 0;

		for (OutcomeInfoDerived outcome : outcomes) {
			// if outcome is an agreement, or we do not care about it
			if (outcome.isAgreement() || !onlyAgreements) {
				if (outcome.getAgentAname().equals(agentName)) {
					utility += outcome.getAgentAutilityDiscount();
					totalSessions++;
				} else if (outcome.getAgentBname().equals(agentName)) {
					utility += outcome.getAgentButilityDiscount();
					totalSessions++;
				}
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
	private static double getPercentageOfAgreement(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		int agreement = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName) || outcome.getAgentBname().equals(agentName)) {
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
	private static double getAverageTimeOfAgreement(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double timeOfAgreement = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName) || outcome.getAgentBname().equals(agentName)) {
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
	private static double getAverageEndOfNegotiation(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double timeOfAgreement = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName) || outcome.getAgentBname().equals(agentName)) {
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
	private static double getAverageRounds(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		int totalBids = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName) || outcome.getAgentBname().equals(agentName)) {
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
	private static double getAverageExploration(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double totalExploration = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName)) {
				totalSessions++;
				totalExploration += outcome.getExplorationA();
			} else {
				if (outcome.getAgentBname().equals(agentName)) {
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
	private static double getAverageJointExploration(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double totalJointExploration = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName) || outcome.getAgentBname().equals(agentName)) {
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
	private static double getAverageNashDistance(ArrayList<OutcomeInfoDerived> outcomes, String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double totalNash = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName) || outcome.getAgentBname().equals(agentName)) {
				if (!onlyAgreements || outcome.isAgreement()) {
					totalSessions++;
					totalNash += outcome.getNashDistanceA();
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
	private static double getAverageSocialWelfare(ArrayList<OutcomeInfoDerived> outcomes, String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double totalSocialWelfare = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName) || outcome.getAgentBname().equals(agentName)) {
				if (!onlyAgreements || outcome.isAgreement()) {
					totalSessions++;
					totalSocialWelfare += outcome.getSocialWelfareA();
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
	private static double getAverageParetoDistance(ArrayList<OutcomeInfoDerived> outcomes, String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double totalPareto = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName) || outcome.getAgentBname().equals(agentName)) {
				if (!onlyAgreements || outcome.isAgreement()) {
					totalSessions++;
					totalPareto += outcome.getParetoDistanceA();
				}
			}
		}
		return totalPareto / totalSessions;
	}
	
	/**
	 * Returns the average percentage of bids offered by the opponent which
	 * were Pareto optimal.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return average percentage of Pareto bids
	 */
	private static double getAveragePercentageParetoBids(
			ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double totalPercParetoBids = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName)) {
				totalSessions++;
				totalPercParetoBids += outcome.getPercParetoBidsA();
			} else {
				if (outcome.getAgentBname().equals(agentName)) {
					totalSessions++;
					totalPercParetoBids += outcome.getPercParetoBidsB();
				}
			}
		}
		return (double)totalPercParetoBids / (double)totalSessions;
	}
	
	/**
	 * Calculates the average Kalai distance of an agreement.
	 * 
	 * @param outcomes
	 * @param agentName
	 * @return average Kalai distance
	 */
	private static double getAverageKalaiDistance(ArrayList<OutcomeInfoDerived> outcomes, String agentName, boolean onlyAgreements) {
		int totalSessions = 0;
		double totalKalai = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName) || outcome.getAgentBname().equals(agentName)) {
				if (!onlyAgreements || outcome.isAgreement()) {
					totalSessions++;
					totalKalai += outcome.getKalaiDistanceA();
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
	private static double getAveragePercentageOfUnfortunateMoves(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double totalUnfortunateMovesPerc = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName)) {
				totalSessions++;
				totalUnfortunateMovesPerc += outcome.getUnfortunateMovesA();
			} else if (outcome.getAgentBname().equals(agentName)) {
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
	private static double getAveragePercentageOfFortunateMoves(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double totalFortunateMovesPerc = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName)) {
				totalSessions++;
				totalFortunateMovesPerc += outcome.getFortunateMovesA();
			} else if (outcome.getAgentBname().equals(agentName)) {
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
	private static double getAveragePercentageOfNiceMoves(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double totalNiceMovesPerc = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName)) {
				totalSessions++;
				totalNiceMovesPerc += outcome.getNiceMovesA();
			} else if (outcome.getAgentBname().equals(agentName)) {
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
	private static double getAveragePercentageOfSilentMoves(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double totalSilentMovesPerc = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName)) {
				totalSessions++;
				totalSilentMovesPerc += outcome.getSilentMovesA();
			} else if (outcome.getAgentBname().equals(agentName)) {
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
	private static double getAveragePercentageOfConcessionMoves(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double totalConcessionMovesPerc = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName)) {
				totalSessions++;
				totalConcessionMovesPerc += outcome.getConcessionMovesA();
			} else if (outcome.getAgentBname().equals(agentName)) {
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
	private static double getAveragePercentageOfSelfishMoves(ArrayList<OutcomeInfoDerived> outcomes, String agentName) {
		int totalSessions = 0;
		double totalSelfishMovesPerc = 0;
		for (OutcomeInfoDerived outcome : outcomes) {
			if (outcome.getAgentAname().equals(agentName)) {
				totalSessions++;
				totalSelfishMovesPerc += outcome.getSelfishMovesA();
			} else if (outcome.getAgentBname().equals(agentName)) {
				totalSessions++;
				totalSelfishMovesPerc += outcome.getSelfishMovesB();
			}
		}
		return totalSelfishMovesPerc / totalSessions;
	}
}