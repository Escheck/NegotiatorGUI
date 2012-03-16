package negotiator.qualitymeasures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import negotiator.exceptions.Warning;
import negotiator.xml.SimpleElement;
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
 * NOTE: some methods, such as Kalai and Nash distance, should be extended such that
 * matches without outcome can be ignored.
 * 
 * @author Mark Hendrikx, Alex Dirkzwager
 */
public class TournamentMeasures {
	
	/**
	 * Class which parses a normal outcomes log and stores all the information in objects.
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
				outcome.setTimeOfAgreement(Double.parseDouble(attributes.getValue(1)));
				outcome.setBids(Integer.parseInt(attributes.getValue(2)));
				outcome.setDomain(attributes.getValue(3));
				outcome.setAgreement(attributes.getValue(4).contains("Accept"));
				outcome.setStartedA(attributes.getValue(5).equals("A"));
				found = true;
			} else if (tagName.equals("resultsOfAgent") && attributes.getValue(0).equals("A")) {
				outcome.setAgentNameA(attributes.getValue(1));
				agents.add(attributes.getValue(1));
				outcome.setUtilProfA(attributes.getValue(3));
				outcome.setUtilityA(Double.parseDouble(attributes.getValue(10)));
				outcome.setDiscountedUtilityA(Double.parseDouble(attributes.getValue(8)));
				found = true;
			} else if (tagName.equals("resultsOfAgent") && attributes.getValue(0).equals("B")) {
				outcome.setAgentNameB(attributes.getValue(1));
				agents.add(attributes.getValue(1));
				outcome.setUtilProfB(attributes.getValue(3));
				outcome.setUtilityB(Double.parseDouble(attributes.getValue(10)));
				outcome.setDiscountedUtilityB(Double.parseDouble(attributes.getValue(8)));
				found = true;
			}
			return found;
		}
		
		private boolean processUtilityBasedQM(String nsURI, String strippedName,
				String tagName, Attributes attributes) {
			boolean found = false;
			if (tagName.equals("utility_based_quality_measures")) {
				outcome.setNashDistance(Double.parseDouble(attributes.getValue(0)));
				outcome.setParetoDistance(Double.parseDouble(attributes.getValue(1)));
				outcome.setKalaiDistance(Double.parseDouble(attributes.getValue(2)));
				found = true;
			}
			return found;
		}
		
		private boolean processTrajectoryBasedQM(String nsURI,
				String strippedName, String tagName, Attributes attributes) {
			boolean found = false;
			if (tagName.equals("trajectory") && attributes.getValue(1).equals("A")) {
				outcome.setUnfortunateMovesA(Double.parseDouble(attributes.getValue(0)));
			} else if (tagName.equals("trajectory") && attributes.getValue(1).equals("B")) {
				outcome.setUnfortunateMovesB(Double.parseDouble(attributes.getValue(0)));
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
				for (int a = 0; a < runs.size(); a++) {
					if (!outcomeInArray(runs.get(a), outcomes.get(i))) {
						runs.get(a).add(outcomes.get(i));
						added = true;
						break;
					}
				}
				if (!added) {
					ArrayList<OutcomeInfo> newList = new ArrayList<OutcomeInfo>();
					newList.add(outcomes.get(i));
					runs.add(newList);
				}
			}
			return runs;
		}
		
		/**
		 * Calculate how many runs were done. Note that all runs have to be checked
		 * as it is not guaranteed that there is an order in the data.
		 * 
		 * @return amount of runs
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
			String in = "c:/Path/To/Normal/Log/log.xml";
			String out = "c:/Path/To/Output/Filename.xml";
			process(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main method of this class. Parses the file, calculates the results of all measures,
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
		SimpleElement results = calculateMeasures(handler.getOutcomes(), handler.getOutcomesAsRuns(), handler.getAgents());
		writeXMLtoFile(results, tournamentLog);
	}
	
	/**
	 * Write the result given to and XML file with the given path.
	 * @param results
	 * @param logPath
	 */
	private static void writeXMLtoFile(SimpleElement results, String logPath) {
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
public static SimpleElement calculateMeasures(ArrayList<OutcomeInfo> outcomes, ArrayList<ArrayList<OutcomeInfo>> runs, HashSet<String> agents) {
		SimpleElement tournamentQualityMeasures = new SimpleElement("tournament_quality_measures");
		System.out.println("RUNS: " + runs.size());
		System.out.println("OUTCOMES: " + outcomes.size());
		for (int i = 0; i < runs.size(); i++) {
			System.out.println("run " + i + "  " + runs.get(i).size());
		}
		for (Iterator<String> agentsIter = agents.iterator(); agentsIter.hasNext(); ) {
			String agentName = agentsIter.next();
			SimpleElement agentElement = new SimpleElement("NegotiationOutcome");
			agentElement.setAttribute("Agent", agentName);
			
			SimpleElement tournamentQM = new SimpleElement("TournamentQM");
			agentElement.addChildElement(tournamentQM);
			

			if(agentName.contains("bs")) {
				SimpleElement decoupledElementID = new SimpleElement("DecoupledElementID");
				agentElement.addChildElement(decoupledElementID);

				decoupledElementID.setAttribute("offering_strategy", getOfferingStrategyName(agentName));
				decoupledElementID.setAttribute("acceptance_strategy", getAcceptanceStrategyName(agentName));
				decoupledElementID.setAttribute("opponent_model", getOpponentModelName(agentName));
			}

			tournamentQM.setAttribute("percentage_of_agreement", getPercentageOfAgreement(outcomes, agentName) + "%");
			tournamentQM.setAttribute("average_rounds", getAverageRounds(outcomes, agentName) + "");
			tournamentQM.setAttribute("average_time_of_agreement", getAverageTimeOfAgreement(outcomes, agentName) + "");
			tournamentQM.setAttribute("average_util_of_agreements", getAverageUtility(outcomes, agentName, true) + "");
			tournamentQM.setAttribute("average_util", getAverageUtility(outcomes, agentName, false) + "");
			tournamentQM.setAttribute("average_discounted_util_of_agreements", getAverageDiscountedUtility(outcomes, agentName, true) + "");
			tournamentQM.setAttribute("average_discounted_util", getAverageDiscountedUtility(outcomes, agentName, false) + "");
			
			SimpleElement utilityBasedQM = new SimpleElement("UtilityBasedQM");
			agentElement.addChildElement(utilityBasedQM);
			utilityBasedQM.setAttribute("average_nash_distance", getAverageNashDistance(outcomes, agentName) + "");
			utilityBasedQM.setAttribute("average_pareto_distance", getAverageParetoDistance(outcomes, agentName) + "");
			utilityBasedQM.setAttribute("average_kalai_distance", getAverageKalaiDistance(outcomes, agentName) + "");
			
			SimpleElement runBasedQM = new SimpleElement("RunBasedQM");
			agentElement.addChildElement(runBasedQM);
			getStandardDeviationOfTimeOfAgreement(runs, outcomes, agentName);
			getStandardDeviationOfDiscountedUtility(runs, agentName);
			getStandardDeviationOfTotalRounds(runs, agentName);
			
			SimpleElement prefOpponentModelQM = new SimpleElement("PrefOpponentModelQM");
			agentElement.addChildElement(prefOpponentModelQM);
			prefOpponentModelQM.setAttribute("average_correlation_nash",  "");
			prefOpponentModelQM.setAttribute("average_correlation_kalai",  "");
			prefOpponentModelQM.setAttribute("average_correlation_pareto_bids",  "");
			prefOpponentModelQM.setAttribute("average_correlation_all_bids",  "");
			
			
			
			SimpleElement trajectorAnalysisQM = new SimpleElement("trajectorAnalysisQM");
			agentElement.addChildElement(trajectorAnalysisQM);
			trajectorAnalysisQM.setAttribute("percentage_of_unfortunate_moves", getAveragePercentageOfUnfortunateMoves(outcomes, agentName) + "");
				
			tournamentQualityMeasures.addChildElement(agentElement);
	    }
		return tournamentQualityMeasures;
	}

	/**
	 * Calculates the standard deviation of the utility of a run.
	 * @param runs
	 * @param agentName
	 */
	private static void getStandardDeviationOfDiscountedUtility(ArrayList<ArrayList<OutcomeInfo>> runs, String agentName) {
		double sumOfAverages = 0;
		double squaredSumOfDeviations = 0;
		double[] results = new double[runs.size()];
		for (int i = 0; i < runs.size(); i++) {
			double averageOfRun = getAverageDiscountedUtility(runs.get(i), agentName, false);
			System.out.println(averageOfRun);
			sumOfAverages += averageOfRun;
			results[i] = averageOfRun;
		}
		double averageOfRuns = sumOfAverages / runs.size();
		for (int i = 0; i < runs.size(); i++) {
			squaredSumOfDeviations += Math.pow((results[i] - averageOfRuns), 2);
		}
		// n-1 due to Bessel's correction
		double variance = squaredSumOfDeviations / (runs.size() - 1);
	}
	
	/**
	 * Calculates the standard deviation of the time of agreement.
	 * Also uses outcomes because of isAgreement (average of averages != average of all)
	 * @param runs
	 * @param outcomes
	 * @param agentName
	 */
	private static void getStandardDeviationOfTotalRounds(ArrayList<ArrayList<OutcomeInfo>> runs, String agentName) {
		double sumOfAverages = 0;
		double squaredSumOfDeviations = 0;
		double[] results = new double[runs.size()];
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
	}

	/**
	 * Also uses outcomes because of isAgreement (average of averages != average of all)
	 * @param runs
	 * @param outcomes
	 * @param agentName
	 */
	private static void getStandardDeviationOfTimeOfAgreement(ArrayList<ArrayList<OutcomeInfo>> runs, ArrayList<OutcomeInfo> outcomes, String agentName) {
		double squaredSumOfDeviations = 0;
		double[] results = new double[runs.size()];
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
	 * Calculates the average Nash distance of an agreement.
	 * @param outcomes
	 * @param agentName
	 * @return average Nash distance
	 */
	private static double getAverageNashDistance(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalNash = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalNash += outcome.getNashDistance();
			}
		}
		return totalNash / totalSessions;
	}
	
	/**
	 * Calculates the average Pareto distance of an agreement.
	 * @param outcomes
	 * @param agentName
	 * @return average Pareto distance
	 */
	private static double getAverageParetoDistance(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalPareto = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalPareto += outcome.getParetoDistance();
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
	private static double getAverageKalaiDistance(ArrayList<OutcomeInfo> outcomes, String agentName) {
		int totalSessions = 0;
		double totalKalai = 0;
		for (OutcomeInfo outcome : outcomes) {
			if (outcome.getAgentNameA().equals(agentName) || outcome.getAgentNameB().equals(agentName)) {
				totalSessions++;
				totalKalai += outcome.getKalaiDistance();
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