package negotiator.qualitymeasures.logmanipulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;

import negotiator.exceptions.Warning;
import negotiator.xml.OrderedSimpleElement;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Apparently this class outcome log files. The log data is used for analysis of
 * a tournament. See {@link TournamentMeasures}.
 * 
 * @author Mark Hendrikx, Alex Dirkzwager (probably)
 * 
 */
public class NegotiationLogParser {

	/**
	 * Class which parses a normal outcomes log and stores all the information
	 * as OutcomeInfoDerived objects.
	 */
	static class ResultsParser extends DefaultHandler {

		OutcomeInfoDerived outcome = null;
		ArrayList<OutcomeInfoDerived> outcomes = new ArrayList<OutcomeInfoDerived>();
		HashSet<String> agents = new HashSet<String>();

		public void startElement(String nsURI, String strippedName,
				String tagName, Attributes attributes) throws SAXException {
			processTournamentBasedQM(nsURI, strippedName, tagName, attributes);
			/*
			 * if (!processTournamentBasedQM(nsURI, strippedName, tagName,
			 * attributes)) { if (!processUtilityBasedQM(nsURI, strippedName,
			 * tagName, attributes)) { processTrajectoryBasedQM(nsURI,
			 * strippedName, tagName, attributes); } }
			 */
		}

		private boolean processTournamentBasedQM(String nsURI,
				String strippedName, String tagName, Attributes attributes) {
			boolean found = false;
			if (tagName.equals("NegotiationOutcome")) {
				outcome = new OutcomeInfoDerived();
				outcome.setCurrentTime(attributes.getValue("currentTime"));
				outcome.setTimeOfAgreement(Double.parseDouble(attributes
						.getValue("timeOfAgreement")));
				outcome.setBids(Integer.parseInt(attributes.getValue("bids")));
				outcome.setDomainName(attributes.getValue("domain"));
				outcome.setLastAction(attributes.getValue("lastAction"));
				outcome.setAgreement(attributes.getValue("lastAction")
						.contains("Accept"));
				outcome.setRunNr(Integer.parseInt(attributes
						.getValue("runNumber")));
				outcome.setErrorRemarks(attributes.getValue("errors"));
				outcome.setStartingAgent(attributes.getValue("startingAgent"));
				found = true;
			} else if (tagName.equals("resultsOfAgent")
					&& attributes.getValue("agent").equals("A")) {
				outcome.setAgentAname(attributes.getValue("agentName"));
				agents.add(outcome.getAgentAname());
				outcome.setBiddingStrategyA(attributes
						.getValue("offering_strategy"));
				outcome.setAcceptanceStrategyA(attributes
						.getValue("acceptance_strategy"));
				outcome.setOpponentModelA(attributes.getValue("opponent_model"));
				outcome.setAgentAclass(attributes.getValue("agentClass"));
				outcome.setAgentAutilSpaceName(attributes.getValue("utilspace"));
				outcome.setAgentAutility(Double.parseDouble(attributes
						.getValue("finalUtility")));
				outcome.setAgentAutilityDiscount(Double.parseDouble(attributes
						.getValue("discountedUtility")));
				outcome.setACbestTheoreticalA(Double.parseDouble(attributes
						.getValue("bestAcceptableBid")));
				outcome.setACbestDiscountedTheoreticalA(Double
						.parseDouble(attributes
								.getValue("bestDiscountedAccepableBid")));

				// Check if Competitiveness data is present
				if (attributes.getValue("minDemandedUtility") != null) {
					outcome.setMinDemandedUtilityA(Double
							.parseDouble(attributes
									.getValue("minDemandedUtility")));
					outcome.setFYUA(Double.parseDouble(attributes
							.getValue("FYU")));
					outcome.setTotal_CR_A(Double.parseDouble(attributes
							.getValue("Total_CR")));
					outcome.setBS_CR_A(Double.parseDouble(attributes
							.getValue("BS_CR")));
					outcome.setAC_CR_A(Double.parseDouble(attributes
							.getValue("AC_CR")));
					outcome.setNormalized_AC_CR_A(Double.parseDouble(attributes
							.getValue("Normalized_AC_CR")));
				}

				outcome.setAgentAmaxUtil(Double.parseDouble(attributes
						.getValue("maxUtility")));
				outcome.setNormalizedUtilityA(Double.parseDouble(attributes
						.getValue("normalizedUtility")));

				// Checking if Trajectory data is present
				if (attributes.getValue("silent_moves") != null) {
					outcome.setSilentMovesA(Double.parseDouble(attributes
							.getValue("silent_moves")));
					outcome.setSelfishMovesA(Double.parseDouble(attributes
							.getValue("selfish_moves")));
					outcome.setFortunateMovesA(Double.parseDouble(attributes
							.getValue("fortunate_moves")));
					outcome.setUnfortunateMovesA(Double.parseDouble(attributes
							.getValue("unfortunate_moves")));
					outcome.setNiceMovesA(Double.parseDouble(attributes
							.getValue("nice_moves")));
					outcome.setConcessionMovesA(Double.parseDouble(attributes
							.getValue("concession_moves")));
					outcome.setExplorationA(Double.parseDouble(attributes
							.getValue("exploration_rate")));
					outcome.setJointExploration(Double.parseDouble(attributes
							.getValue("joint_exploration_rate")));
					outcome.setPercParetoBidsA(Double.parseDouble(attributes
							.getValue("perc_pareto_bids")));
				}

				// Checking if Utility Based Quality Measure data is present
				if (attributes.getValue("nash_distance") != null) {
					outcome.setNashDistanceA(Double.parseDouble(attributes
							.getValue("nash_distance")));
					outcome.setParetoDistanceA(Double.parseDouble(attributes
							.getValue("pareto_distance")));
					outcome.setKalaiDistanceA(Double.parseDouble(attributes
							.getValue("kalai_distance")));
					outcome.setSocialWelfareA(Double.parseDouble(attributes
							.getValue("social_welfare")));
				}
				outcome.setAcceptedBy(attributes.getValue("AcceptedBy"));
				found = true;
			} else if (tagName.equals("resultsOfAgent")
					&& attributes.getValue("agent").equals("B")) {
				outcome.setAgentBname(attributes.getValue("agentName"));
				agents.add(outcome.getAgentBname());
				outcome.setBiddingStrategyB(attributes
						.getValue("offering_strategy"));
				outcome.setAcceptanceStrategyB(attributes
						.getValue("acceptance_strategy"));
				outcome.setOpponentModelB(attributes.getValue("opponent_model"));
				outcome.setAgentBclass(attributes.getValue("agentClass"));
				outcome.setAgentButilSpaceName(attributes.getValue("utilspace"));
				outcome.setAgentButility(Double.parseDouble(attributes
						.getValue("finalUtility")));
				outcome.setAgentButilityDiscount(Double.parseDouble(attributes
						.getValue("discountedUtility")));
				outcome.setACbestTheoreticalB(Double.parseDouble(attributes
						.getValue("bestAcceptableBid")));
				outcome.setACbestDiscountedTheoreticalB(Double
						.parseDouble(attributes
								.getValue("bestDiscountedAccepableBid")));

				// Check if Competitiveness data is present
				if (attributes.getValue("minDemandedUtility") != null) {
					outcome.setMinDemandedUtilityB(Double
							.parseDouble(attributes
									.getValue("minDemandedUtility")));
					outcome.setFYUB(Double.parseDouble(attributes
							.getValue("FYU")));
					outcome.setTotal_CR_B(Double.parseDouble(attributes
							.getValue("Total_CR")));
					outcome.setBS_CR_B(Double.parseDouble(attributes
							.getValue("BS_CR")));
					outcome.setAC_CR_B(Double.parseDouble(attributes
							.getValue("AC_CR")));
					outcome.setNormalized_AC_CR_B(Double.parseDouble(attributes
							.getValue("Normalized_AC_CR")));
				}
				outcome.setAgentBmaxUtil(Double.parseDouble(attributes
						.getValue("maxUtility")));
				outcome.setNormalizedUtilityB(Double.parseDouble(attributes
						.getValue("normalizedUtility")));

				// Checking if Trajectory Quality Measure data is present
				if (attributes.getValue("silent_moves") != null) {
					outcome.setSilentMovesB(Double.parseDouble(attributes
							.getValue("silent_moves")));
					outcome.setSelfishMovesB(Double.parseDouble(attributes
							.getValue("selfish_moves")));
					outcome.setFortunateMovesB(Double.parseDouble(attributes
							.getValue("fortunate_moves")));
					outcome.setUnfortunateMovesB(Double.parseDouble(attributes
							.getValue("unfortunate_moves")));
					outcome.setNiceMovesB(Double.parseDouble(attributes
							.getValue("nice_moves")));
					outcome.setConcessionMovesB(Double.parseDouble(attributes
							.getValue("concession_moves")));
					outcome.setExplorationB(Double.parseDouble(attributes
							.getValue("exploration_rate")));
					outcome.setJointExploration(Double.parseDouble(attributes
							.getValue("joint_exploration_rate")));
					outcome.setPercParetoBidsB(Double.parseDouble(attributes
							.getValue("perc_pareto_bids")));
				}

				// Checking if Utility Based Quality Measure data is present
				if (attributes.getValue("nash_distance") != null) {
					outcome.setNashDistanceB(Double.parseDouble(attributes
							.getValue("nash_distance")));
					outcome.setParetoDistanceB(Double.parseDouble(attributes
							.getValue("pareto_distance")));
					outcome.setKalaiDistanceB(Double.parseDouble(attributes
							.getValue("kalai_distance")));
					outcome.setSocialWelfareB(Double.parseDouble(attributes
							.getValue("social_welfare")));
				}
				found = true;
			}
			return found;
		}

		private boolean processUtilityBasedQM(String nsURI,
				String strippedName, String tagName, Attributes attributes) {
			boolean found = false;
			if (tagName.equals("utility_based_quality_measures")) {
				/*
				 * outcome.setNashDistance(Double.parseDouble(attributes.getValue
				 * ("nash_distance")));
				 * outcome.setParetoDistance(Double.parseDouble
				 * (attributes.getValue("pareto_distance")));
				 * outcome.setKalaiDistance
				 * (Double.parseDouble(attributes.getValue("kalai_distance")));
				 * outcome
				 * .setSocialWelfare(Double.parseDouble(attributes.getValue
				 * ("social_welfare")));
				 */
				found = true;
			}
			return found;
		}

		private boolean processTrajectoryBasedQM(String nsURI,
				String strippedName, String tagName, Attributes attributes) {
			boolean found = false;
			if (tagName.equals("trajectory")
					&& attributes.getValue("agent").equals("A")) {
				outcome.setSilentMovesA(Double.parseDouble(attributes
						.getValue("silent_moves")));
				outcome.setSelfishMovesA(Double.parseDouble(attributes
						.getValue("selfish_moves")));
				outcome.setFortunateMovesA(Double.parseDouble(attributes
						.getValue("fortunate_moves")));
				outcome.setUnfortunateMovesA(Double.parseDouble(attributes
						.getValue("unfortunate_moves")));
				outcome.setNiceMovesA(Double.parseDouble(attributes
						.getValue("nice_moves")));
				outcome.setConcessionMovesA(Double.parseDouble(attributes
						.getValue("concession_moves")));
				outcome.setExplorationA(Double.parseDouble(attributes
						.getValue("exploration_rate")));
				outcome.setJointExploration(Double.parseDouble(attributes
						.getValue("joint_exploration_rate")));
				outcome.setPercParetoBidsA(Double.parseDouble(attributes
						.getValue("perc_pareto_bids")));
			} else if (tagName.equals("trajectory")
					&& attributes.getValue("agent").equals("B")) {
				outcome.setSilentMovesB(Double.parseDouble(attributes
						.getValue("silent_moves")));
				outcome.setSelfishMovesB(Double.parseDouble(attributes
						.getValue("selfish_moves")));
				outcome.setFortunateMovesB(Double.parseDouble(attributes
						.getValue("fortunate_moves")));
				outcome.setUnfortunateMovesB(Double.parseDouble(attributes
						.getValue("unfortunate_moves")));
				outcome.setNiceMovesB(Double.parseDouble(attributes
						.getValue("nice_moves")));
				outcome.setConcessionMovesB(Double.parseDouble(attributes
						.getValue("concession_moves")));
				outcome.setExplorationB(Double.parseDouble(attributes
						.getValue("exploration_rate")));
				outcome.setJointExploration(Double.parseDouble(attributes
						.getValue("joint_exploration_rate")));
				outcome.setPercParetoBidsB(Double.parseDouble(attributes
						.getValue("perc_pareto_bids")));
			}
			return found;
		}

		public void endElement(String nsURI, String strippedName, String tagName)
				throws SAXException {
			if (tagName.equals("NegotiationOutcome")) {
				outcomes.add(outcome);
			}
		}

		/**
		 * Converts an arraylist of outcomes to a seperate arraylist per run.
		 * This method comes in handy when standard deviations need to be
		 * calculated.
		 * 
		 * @return ArrayList of runs with their outcomes
		 */
		public ArrayList<ArrayList<OutcomeInfoDerived>> getOutcomesAsRuns() {
			ArrayList<ArrayList<OutcomeInfoDerived>> runs = new ArrayList<ArrayList<OutcomeInfoDerived>>();
			runs.add(new ArrayList<OutcomeInfoDerived>());

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

				// if the outcome was already found in every run, createFrom a new
				// run
				if (!added) {
					ArrayList<OutcomeInfoDerived> newList = new ArrayList<OutcomeInfoDerived>();
					newList.add(outcomes.get(i));
					runs.add(newList);
				}
			}
			return runs;
		}

		/**
		 * Check if an outcome is found in an array of outcomes.
		 */
		private boolean outcomeInArray(ArrayList<OutcomeInfoDerived> outcomes,
				OutcomeInfoDerived outcome) {
			boolean found = false;
			for (int i = 0; i < outcomes.size(); i++) {
				if (outcomes.get(i).getAgentAname()
						.equals(outcome.getAgentAname())
						&& outcomes.get(i).getAgentBname()
								.equals(outcome.getAgentBname())
						&& outcomes.get(i).getAgentAutilSpaceName()
								.equals(outcome.getAgentAutilSpaceName())
						&& outcomes.get(i).getAgentButilSpaceName()
								.equals(outcome.getAgentButilSpaceName())) {
					found = true;
					break;
				}
				if (outcomes.get(i).getAgentAname()
						.equals(outcome.getAgentBname())
						&& outcomes.get(i).getAgentBname()
								.equals(outcome.getAgentAname())
						&& outcomes.get(i).getAgentAutilSpaceName()
								.equals(outcome.getAgentButilSpaceName())
						&& outcomes.get(i).getAgentButilSpaceName()
								.equals(outcome.getAgentAutilSpaceName())) {
					found = true;
					break;
				}
			}

			return found;
		}

		public ArrayList<OutcomeInfoDerived> getOutcomes() {
			return outcomes;
		}

		public HashSet<String> getAgents() {
			return agents;
		}
	}

	public static ResultsParser parseLog(String log) throws Exception {
		XMLReader xr = XMLReaderFactory.createXMLReader();
		ResultsParser handler = new ResultsParser();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		xr.parse(log);
		System.out.println("Parsed " + handler.getOutcomes().size()
				+ " outcome logs");
		return handler;
	}

	/**
	 * Write the result given to an XML file with the given path.
	 * 
	 * @param results
	 * @param logPath
	 */
	static void writeXMLtoFile(OrderedSimpleElement results, String logPath) {
		try {
			File tournamentLog = new File(logPath);
			if (tournamentLog.exists()) {
				tournamentLog.delete();
			}
			System.out.println("Writing XML to file");
			BufferedWriter out = new BufferedWriter(new FileWriter(
					tournamentLog, true));
			out.write("" + results);
			out.flush();
			out.close();
			System.out.println("Finished parsing data");
		} catch (Exception e) {
			new Warning("Exception during writing s:" + e);
			e.printStackTrace();
		}
	}
}
