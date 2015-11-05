package negotiator.qualitymeasures;

import java.util.ArrayList;

import negotiator.analysis.BidSpace;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.opponentmodel.OppositeModel;
import negotiator.boaframework.opponentmodel.PerfectModel;
import negotiator.boaframework.opponentmodel.WorstModel;
import negotiator.utility.AdditiveUtilitySpace;

/**
 * This class is used to load a simple trace of the opponent's bids, and feed
 * this trace to an opponent model. Following, the quality of the opponent model
 * is sampled at a given interval. The data is saved in a Excel pivot-table
 * compatible format.
 * 
 * @author Mark Hendrikx
 */
public class TraceProcessor {

	// changing this value effects the TraceMeasures
	public final static double SAMPLE_EVERY_X_TIME = 0.01;
	private static OpponentModel opponentModel;
	private static boolean firstEntry = true;

	public static void main(String[] args) {
		String mainDir = "c:/Users/Mark/workspace/Genius";
		String logPath = "Tracelogs/Experiment 1/Nondeterministic11.csv";
		String outPath = "log/Test.csv";

		TraceLoader loader = new TraceLoader();
		ArrayList<Trace> traces = loader.loadTraces(mainDir, logPath);
		processTraces(outPath, traces, mainDir);
	}

	private static void processTraces(String outPath, ArrayList<Trace> traces,
			String mainDir) {
		for (int a = 0; a < traces.size(); a++) {
			System.out.println("Processing trace " + (a + 1) + "/"
					+ traces.size() + " " + traces.get(a).getOpponentProfile());
			opponentModel = new PerfectModel();
			Trace trace = traces.get(a);
			NegotiationSession negotiationSession = new NegotiationSessionWrapper(
					trace, mainDir);
			OpponentModelMeasuresResults omMeasuresResults = new OpponentModelMeasuresResults();
			OpponentModelMeasures omMeasures = null;
			try {
				opponentModel.init(negotiationSession, null);
				AdditiveUtilitySpace opponentSpace = new AdditiveUtilitySpace(
						negotiationSession.getUtilitySpace().getDomain(),
						mainDir + "/" + trace.getOpponentProfile());
				opponentModel.setOpponentUtilitySpace(opponentSpace);
				omMeasures = new OpponentModelMeasures(
						(AdditiveUtilitySpace) negotiationSession
								.getUtilitySpace(),
						opponentSpace);
			} catch (Exception e) {
				e.printStackTrace();
			}

			int currentSample = 0;
			for (int i = 0; i < trace.getOfferedBids().size(); i++) {
				BidDetails opponentBid = trace.getOfferedBids().get(i)
						.getSecond();
				negotiationSession.getOpponentBidHistory().add(opponentBid);
				opponentModel.updateModel(opponentBid.getBid(),
						opponentBid.getTime());
				if (opponentBid.getTime() >= (currentSample * SAMPLE_EVERY_X_TIME)) {
					try {
						if (currentSample % 5 == 0) {
							System.out.println(currentSample);
						}
						currentSample++;
						omMeasuresResults.addBidIndex(trace.getOfferedBids()
								.get(i).getFirst());
						if (currentSample == 1
								|| !(opponentModel instanceof OppositeModel || opponentModel instanceof WorstModel)) {
							AdditiveUtilitySpace estimatedOpponentUS = (AdditiveUtilitySpace) opponentModel
									.getOpponentUtilitySpace();
							BidSpace estimatedBS = new BidSpace(
									negotiationSession.getUtilitySpace(),
									estimatedOpponentUS, false, true);
							omMeasuresResults
									.addPearsonCorrelationCoefficientOfBids(omMeasures
											.calculatePearsonCorrelationCoefficientBids(estimatedOpponentUS));
							omMeasuresResults
									.addRankingDistanceOfBids(omMeasures
											.calculateRankingDistanceBids(estimatedOpponentUS));
							omMeasuresResults
									.addRankingDistanceOfIssueWeights(omMeasures
											.calculateRankingDistanceWeights(opponentModel));
							omMeasuresResults
									.addAverageDifferenceBetweenBids(omMeasures
											.calculateAvgDiffBetweenBids(opponentModel));
							omMeasuresResults
									.addAverageDifferenceBetweenIssueWeights(omMeasures
											.calculateAvgDiffBetweenIssueWeights(opponentModel));
							omMeasuresResults.addKalaiDistance(omMeasures
									.calculateKalaiDiff(estimatedBS));
							omMeasuresResults.addNashDistance(omMeasures
									.calculateNashDiff(estimatedBS));
							omMeasuresResults
									.addAverageDifferenceOfParetoFrontier(omMeasures
											.calculateAvgDiffParetoBidToEstimate(estimatedOpponentUS));
							omMeasuresResults
									.addPercentageOfCorrectlyEstimatedParetoBids(omMeasures
											.calculatePercCorrectlyEstimatedParetoBids(estimatedBS));
							omMeasuresResults
									.addPercentageOfIncorrectlyEstimatedParetoBids(omMeasures
											.calculatePercIncorrectlyEstimatedParetoBids(estimatedBS));
							omMeasuresResults
									.addParetoFrontierDistance(omMeasures
											.calculateParetoFrontierDistance(estimatedBS));
							estimatedBS = null;
							estimatedOpponentUS = null;
							System.gc();
						} else {
							omMeasuresResults
									.addPearsonCorrelationCoefficientOfBids(omMeasuresResults
											.getPearsonCorrelationCoefficientOfBidsList()
											.get(0));
							omMeasuresResults
									.addRankingDistanceOfBids(omMeasuresResults
											.getRankingDistanceOfBidsList()
											.get(0));
							omMeasuresResults
									.addRankingDistanceOfIssueWeights(omMeasuresResults
											.getRankingDistanceOfIssueWeightsList()
											.get(0));
							omMeasuresResults
									.addAverageDifferenceBetweenBids(omMeasuresResults
											.getAverageDifferenceBetweenBidsList()
											.get(0));
							omMeasuresResults
									.addAverageDifferenceBetweenIssueWeights(omMeasuresResults
											.getAverageDifferenceBetweenIssueWeightsList()
											.get(0));
							omMeasuresResults
									.addKalaiDistance(omMeasuresResults
											.getKalaiDistanceList().get(0));
							omMeasuresResults.addNashDistance(omMeasuresResults
									.getNashDistanceList().get(0));
							omMeasuresResults
									.addAverageDifferenceOfParetoFrontier(omMeasuresResults
											.getAverageDifferenceOfParetoFrontierList()
											.get(0));
							omMeasuresResults
									.addPercentageOfCorrectlyEstimatedParetoBids(omMeasuresResults
											.getPercentageOfCorrectlyEstimatedParetoBidsList()
											.get(0));
							omMeasuresResults
									.addPercentageOfIncorrectlyEstimatedParetoBids(omMeasuresResults
											.getPercentageOfIncorrectlyEstimatedParetoBidsList()
											.get(0));
							omMeasuresResults
									.addParetoFrontierDistance(omMeasuresResults
											.getParetoFrontierDistanceList()
											.get(0));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			processDataForLogging(trace, omMeasuresResults, outPath);
			traces.set(a, null);
		}
		System.out.println("Processed " + traces.size() + " traces");
	}

	private static void processDataForLogging(Trace trace,
			OpponentModelMeasuresResults omMeasuresResults, String outPath) {
		CSVlogger logger = new CSVlogger(outPath, trace);
		logger.addMeasure("time", omMeasuresResults.getTimePointList());
		logger.addMeasure("bidindices", omMeasuresResults.getBidIndices());
		logger.addMeasure("pearson_corr_coef_bids",
				omMeasuresResults.getPearsonCorrelationCoefficientOfBidsList());
		logger.addMeasure("ranking_dist_bids",
				omMeasuresResults.getRankingDistanceOfBidsList());
		logger.addMeasure("ranking_dist_issue_weights",
				omMeasuresResults.getRankingDistanceOfIssueWeightsList());
		logger.addMeasure("avg_difference_between_bids",
				omMeasuresResults.getAverageDifferenceBetweenBidsList());
		logger.addMeasure("avg_difference_between_issue_weights",
				omMeasuresResults.getAverageDifferenceBetweenIssueWeightsList());
		logger.addMeasure("kalai_diff",
				omMeasuresResults.getKalaiDistanceList());
		logger.addMeasure("nash_diff", omMeasuresResults.getNashDistanceList());
		logger.addMeasure("avg_diff_pareto_frontier",
				omMeasuresResults.getAverageDifferenceOfParetoFrontierList());
		logger.addMeasure("perc_correct_pareto_frontier", omMeasuresResults
				.getPercentageOfCorrectlyEstimatedParetoBidsList());
		logger.addMeasure("perc_incorrect_pareto_frontier", omMeasuresResults
				.getPercentageOfIncorrectlyEstimatedParetoBidsList());
		logger.addMeasure("pareto_frontier_distance",
				omMeasuresResults.getParetoFrontierDistanceList());
		logger.writeToFilePivotCompatible(trace.getRunNumber(),
				SAMPLE_EVERY_X_TIME, firstEntry);
		firstEntry = false;
	}
}