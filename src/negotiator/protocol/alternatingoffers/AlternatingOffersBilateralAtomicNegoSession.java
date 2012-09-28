package negotiator.protocol.alternatingoffers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.ContinuousTimeline;
import negotiator.DiscreteTimeline;
import negotiator.Global;
import negotiator.NegotiationOutcome;
import negotiator.PausableContinuousTimeline;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidPointTime;
import negotiator.analysis.BidSpace;
import negotiator.analysis.BidSpaceCache;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.OutcomeTuple;
import negotiator.boaframework.acceptanceconditions.other.Multi_AcceptanceCondition;
import negotiator.boaframework.agent.BOAagent;
import negotiator.boaframework.opponentmodel.NoModel;
import negotiator.exceptions.Warning;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.protocol.Protocol;
import negotiator.qualitymeasures.OpponentModelMeasures;
import negotiator.qualitymeasures.logmanipulation.OutcomeInfo;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

/**
 * This is an updated version which has shared deadlines for both agents,
 * implemented with {@link Timeline}.
 */
public class AlternatingOffersBilateralAtomicNegoSession extends
		BilateralAtomicNegotiationSession {
	/**
	 * stopNegotiation indicates that the session has now ended. it is checked
	 * after every call to the agent, and if it happens to be true, session is
	 * immediately returned without any updates to the results list. This is
	 * because killing the thread in many cases will return Agent.getAction()
	 * but with a stale action. By setting stopNegotiation to true before
	 * killing, the agent will still immediately return.
	 */
	public boolean stopNegotiation = false;
	public NegotiationOutcome no;
	private boolean agentAtookAction = false;
	private boolean agentBtookAction = false;
	protected String startingAgent;
	protected boolean startingWithA = true;
	/* time/deadline */
	protected Date startTime;
	protected long startTimeMillies; // idem.
	/** Default setting is 3min. This is the number of ms. */
	protected Integer totalTime = 1000 * 180;
	protected int sessionTotalNumber = 1;
	protected Protocol protocol;

	public Agent currentAgent = null; // agent currently bidding.
	private Timeline timeline;
	private boolean showGUI = true;

	public ArrayList<NegotiationOutcome> MACoutcomes = new ArrayList<NegotiationOutcome>();
	private boolean agentAWithMultiAC = false;
	private boolean agentBWithMultiAC = false;
	private ArrayList<ArrayList<OutcomeTuple>> completeList = new ArrayList<ArrayList<OutcomeTuple>>();
	/**
	 * Uses to keep track of the results of quantifying the accuracy of an
	 * opponent model during a negotiation
	 */
	private OpponentModelMeasures omMeasures;

	/** load the runtime objects to start negotiation */
	public AlternatingOffersBilateralAtomicNegoSession(Protocol protocol,
			Agent agentA, Agent agentB, String agentAname, String agentBname,
			UtilitySpace spaceA, UtilitySpace spaceB,
			HashMap<AgentParameterVariable, AgentParamValue> agentAparams,
			HashMap<AgentParameterVariable, AgentParamValue> agentBparams,
			String startingAgent) throws Exception {

		super(protocol, agentA, agentB, agentAname, agentBname, spaceA, spaceB,
				agentAparams, agentBparams);
		this.protocol = protocol;
		this.startingAgent = startingAgent;
		
		if (protocol.getConfiguration() != null && protocol.getConfiguration().containsKey("disableGUI") &&
				protocol.getConfiguration().get("disableGUI") == 1) {
			showGUI = false;
		}
	}

	/**
	 * a parent thread will call this via the Thread.run() function. Then it
	 * will start a timer to handle the time-out of the negotiation. At the end
	 * of this run, we will notify the parent so that he does not keep waiting
	 * for the time-out.
	 */
	public void run() {

		startTime = new Date();
		startTimeMillies = System.currentTimeMillis();

		try {
			double agentAUtility, agentBUtility;

			// DEFAULT: time-based protocol
			if (protocol.getConfiguration() != null
					&& protocol.getConfiguration().containsKey("protocolMode")
					&& protocol.getConfiguration().get("protocolMode") == 1) {
				timeline = new DiscreteTimeline(protocol.getConfiguration()
						.get("deadline"));
			} else {
				if (protocol.getConfiguration() != null && 
						protocol.getConfiguration().containsKey("deadline")) {
					totalTime = protocol.getConfiguration().get("deadline");
				}
				if (Global.PAUSABLE_TIMELINE) {
					timeline = new PausableContinuousTimeline(totalTime);
				} else {
					timeline = new ContinuousTimeline(totalTime);
				}
			}

			// note, we clone the utility spaces for security reasons, so that
			// the agent
			// can not damage them.
			agentA.internalInit(sessionNumber, sessionTotalNumber, startTime,
					totalTime, timeline, new UtilitySpace(spaceA), agentAparams);
			agentA.init();
			agentB.internalInit(sessionNumber, sessionTotalNumber, startTime,
					totalTime, timeline, new UtilitySpace(spaceB), agentBparams);
			agentB.init();

			stopNegotiation = false;
			lastAction = null;

			if (startingAgent.equals(agentAname))
				currentAgent = agentA;
			else
				currentAgent = agentB;

			System.out.println("starting with agent " + currentAgent.getName());
			// Main.log("Agent " + currentAgent.getName() + " begins");
			if (showGUI) {
				fireLogMessage("Nego", "Agent " + currentAgent.getName()
						+ " begins");
			}
			// DEFAULT: disable trace logging
			if (protocol.getConfiguration() != null
					&& protocol.getConfiguration().containsKey(
							"logNegotiationTrace")
					&& protocol.getConfiguration().get("logNegotiationTrace") == 1) {
				omMeasures = new OpponentModelMeasures(spaceA, spaceB);
			}
			checkForMAC();

			while (!stopNegotiation) {
				// timeline.printTime();
				double time = timeline.getTime();

				try {
					// inform agent about last action of his opponent
					currentAgent.ReceiveMessage(lastAction);
					String deadlineReachedMsg = "Deadline reached while waiting for ["
							+ currentAgent + "]";

					if (timeline.isDeadlineReached()) {
						// if there is a MAC being used
						if (hasMAC()) {
							if (agentAWithMultiAC) {
								AcceptanceStrategy AC = ((BOAagent) agentA)
										.getAcceptanceStrategy();
								((Multi_AcceptanceCondition) AC)
										.remainingACDeadline();
							}
							if (agentBWithMultiAC) {
								AcceptanceStrategy AC = ((BOAagent) agentB)
										.getAcceptanceStrategy();
								((Multi_AcceptanceCondition) AC)
										.remainingACDeadline();
							}
							createMACOutcomes(time);
						} else {
							badOutcome(time, deadlineReachedMsg);
						}
					}
					if (stopNegotiation)
						return;
					// get next action of the agent that has its turn now
					lastAction = currentAgent.chooseAction();
					if (timeline.isDeadlineReached()) {
						if (hasMAC()) {
							if (agentAWithMultiAC) {
								AcceptanceStrategy AC = ((BOAagent) agentA)
										.getAcceptanceStrategy();
								((Multi_AcceptanceCondition) AC)
										.remainingACDeadline();
							}
							if (agentBWithMultiAC) {
								AcceptanceStrategy AC = ((BOAagent) agentB)
										.getAcceptanceStrategy();
								((Multi_AcceptanceCondition) AC)
										.remainingACDeadline();
							}
							createMACOutcomes(time);
						} else {
							badOutcome(time, deadlineReachedMsg);
						}
					}

					if (stopNegotiation)
						return;

					if (lastAction instanceof EndNegotiation) {
						System.out.println("EndNegotiation was called");
						stopNegotiation = true;
						if (hasMAC()) {
							createMACOutcomes(time);
						} else {
							badOutcome(
									time,
									"Agent ["
											+ currentAgent.getName()
											+ "] sent EndNegotiation, so the negotiation ended without agreement");
						}
						checkAgentActivity(currentAgent);
					} else if (lastAction instanceof Offer) {
						// Main.log("Agent " + currentAgent.getName() +
						// " sent the following offer:");
						if (showGUI) {
							fireLogMessage("Nego",
									"Agent " + currentAgent.getName()
											+ " sent the following offer:");
						}
						lastBid = ((Offer) lastAction).getBid();
						if (lastBid == null) {
							badOutcome(
									time,
									"Agent ["
											+ currentAgent.getName()
											+ "] sent an offer with null in it, so the negotiation ended without agreement");
							return;
						}
						// Main.log(lastAction.toString());
						double utilA = agentA.utilitySpace.getUtility(lastBid);
						double utilB = agentB.utilitySpace.getUtility(lastBid);
						if (showGUI) {
							fireLogMessage("Nego", lastAction.toString());

							// Main.log("Utility of " + agentA.getName() +": " +
							// utilA);
							fireLogMessage("Nego", "Utility of " + agentA.getName()
									+ ": " + utilA);
							// Main.log("Utility of " + agentB.getName() +": " +
							// utilB);
							fireLogMessage("Nego", "Utility of " + agentB.getName()
									+ ": " + utilB);
						}
							// save last results

						BidPointTime p = null;
						p = new BidPointTime(lastBid,
								spaceA.getUtility(lastBid),
								spaceB.getUtility(lastBid), time);

						if (!timeline.isDeadlineReached()) {
							// if agent A just made an offer
							if (currentAgent.equals(agentA)) {
								fAgentABids.add(p);
								processOnlineData();
							} else {
								fAgentBBids.add(p);
							}
						}

						double agentAUtilityDisc = spaceA
								.getUtilityWithDiscount(lastBid, time);
						double agentBUtilityDisc = spaceB
								.getUtilityWithDiscount(lastBid, time);

						if (showGUI) {
							fireNegotiationActionEvent(currentAgent, lastAction,
									sessionNumber, System.currentTimeMillis()
											- startTimeMillies, time, utilA, utilB,
									agentAUtilityDisc, agentBUtilityDisc, "bid by "
											+ currentAgent.getName(), false);
						}
						// System.out.println(sessionNumber);
						checkAgentActivity(currentAgent);
					} else if (lastAction instanceof Accept) {
						String acceptedBy = (currentAgent.equals(agentA)) ? "agentA"
								: "agentB";
						if (agentAWithMultiAC
								&& acceptedBy.equalsIgnoreCase("agentB")) {
							AcceptanceStrategy AC = ((BOAagent) agentA)
									.getAcceptanceStrategy();
							((Multi_AcceptanceCondition) AC).remainingACAccept(
									lastBid, time, fAgentABids, fAgentBBids,
									acceptedBy);
						} else if (agentBWithMultiAC
								&& acceptedBy.equalsIgnoreCase("agentA")) {
							AcceptanceStrategy AC = ((BOAagent) agentB)
									.getAcceptanceStrategy();
							((Multi_AcceptanceCondition) AC).remainingACAccept(
									lastBid, time, fAgentABids, fAgentBBids,
									acceptedBy);
						}
						createMACOutcomes(time);

						if (!hasMAC()) {
							createOutcome(lastBid, time, false, null,
									acceptedBy);
						}
					} else { // lastAction instanceof unknown action, e.g. null.
						throw new Exception("unknown action by agent "
								+ currentAgent.getName());
					}

				} catch (Exception e) {
					new Warning("Caught exception:", e, true, 2);
					stopNegotiation = true;
					new Warning("Protocol error by Agent"
							+ currentAgent.getName(), e, true, 3);
					// Global.log("Protocol error by Agent " +
					// currentAgent.getName() +":"+e.getMessage());
					if (lastBid == null)
						agentAUtility = agentBUtility = 1.;
					else {
						agentAUtility = agentBUtility = 0.;
						// handle both getUtility calls apart, if one crashes
						// the other should not be affected.
						try {
							agentAUtility = spaceA.getUtility(lastBid);
						} catch (Exception e1) {
						}
						try {
							agentBUtility = spaceB.getUtility(lastBid);
						} catch (Exception e1) {
						}
					}
					if (currentAgent == agentA)
						agentAUtility = 0.;
					else
						agentBUtility = 0.;
					try {
						BidPoint lastbidPoint = new BidPoint(lastBid,
								agentAUtility, agentBUtility);
						BidPoint nash = bidSpace.getNash();
						double distanceToNash = lastbidPoint.getDistance(nash);

						// checkForMAC();
						if (!hasMAC()) {
							newOutcome(
									currentAgent,
									agentAUtility,
									agentBUtility,
									0,
									0,
									"Caught exception. Agent ["
											+ currentAgent.getName()
											+ "] sent " + lastAction
											+ ". Details: " + e.toString(),
									time, distanceToNash, "");
						} else {
							System.out.println("Error thrown: with MAC agent");
							for (ArrayList<OutcomeTuple> outcomeTupleList : completeList)
								for (OutcomeTuple outcomeTuple : outcomeTupleList) {
									newOutcome(
											currentAgent,
											agentAUtility,
											agentBUtility,
											0,
											0,
											"Caught exception. Agent ["
													+ currentAgent.getName()
													+ "] sent " + lastAction
													+ ". Details: "
													+ e.toString(), time,
											distanceToNash, "");
									changeNameofAC(agentAWithMultiAC,
											agentBWithMultiAC, outcomeTuple);
									MACoutcomes.add(no);
								}
						}

						System.err.println("Emergency outcome: "
								+ agentAUtility + ", " + agentBUtility);
					} catch (Exception err) {
						err.printStackTrace();
						new Warning(
								"exception raised during exception handling: "
										+ err);
					}
					// don't compute the max utility, we're in exception which
					// is already bad enough.
				}
				// swap to other agent
				if (currentAgent.equals(agentA))
					currentAgent = agentB;
				else
					currentAgent = agentA;
			}

			// nego finished by Accept or illegal lastAction.
			// notify parent that we're ready.
			synchronized (protocol) {
				protocol.notify();
			}

			/*
			 * Wouter: WE CAN NOT DO MORE PROCESSING HERE!!!!! Maybe even
			 * catching the ThreadDeath error is wrong. If we do more
			 * processing, we risk getting a ThreadDeath exception causing
			 * Eclipse to pop up a dialog bringing us into the debugger.
			 */

		} catch (Error e) {
			if (e instanceof ThreadDeath) {
				System.out.println("Nego was timed out");
				// Main.logger.add("Negotiation was timed out. Both parties get util=0");
				// if this happens, the caller will adjust utilities.
			}

		}

	}

	/**
	 * Log the negotiation trace during the negotiation
	 * 
	 * @throws Exception
	 */
	private void processOnlineData() {
		// DEFAULT: disable trace logging
		if (protocol.getConfiguration() != null
				&& protocol.getConfiguration().containsKey(
						"logNegotiationTrace")
				&& protocol.getConfiguration().get("logNegotiationTrace") == 1) {
			if (Global.PAUSABLE_TIMELINE) {
				try {
					timeline.pause();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fAgentBBids.size() > 0) {
				Bid lastOpponentBid = fAgentBBids.get(fAgentBBids.size() - 1).bid;
				if (lastOpponentBid != null) {
					omMeasuresResults.addTimePoint(timeline.getTime());
					omMeasuresResults.addBidIndex(omMeasures
							.getOpponentBidIndex(lastOpponentBid));
				}
			}
			if (Global.PAUSABLE_TIMELINE) {
				try {
					timeline.resume();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Checks if one of the agents MAC has
	 */
	private boolean checkForMAC() {
		if (hasMAC()) {
			return true;
		} else {
			if (agentA instanceof BOAagent
					&& ((BOAagent) agentA).getAcceptanceStrategy().isMAC()) {
				agentAWithMultiAC = true;
				// System.out.println("agentAWithMultiAC: " +
				// agentAWithMultiAC);
			}

			if (agentB instanceof BOAagent
					&& ((BOAagent) agentB).getAcceptanceStrategy().isMAC()) {
				agentBWithMultiAC = true;
				// System.out.println("agentBWithMultiAC: " +
				// agentBWithMultiAC);
			}
		}

		return hasMAC();
	}

	/** returns true if one of the agents are using a MAC **/
	private boolean hasMAC() {
		if (agentAWithMultiAC || agentBWithMultiAC) {
			return true;
		}

		return false;
	}

	/** gets the MAC outcomeTuples from the agent class that has a MAC **/
	private void updateOutcomeTupleList() {
		if (agentAWithMultiAC) {
			ArrayList<OutcomeTuple> listFromAgentA = ((Multi_AcceptanceCondition) ((BOAagent) agentA)
					.getAcceptanceStrategy()).getOutcomes();
			completeList.add(listFromAgentA);
		}

		if (agentBWithMultiAC) {
			ArrayList<OutcomeTuple> listFromAgentB = ((Multi_AcceptanceCondition) ((BOAagent) agentB)
					.getAcceptanceStrategy()).getOutcomes();
			completeList.add(listFromAgentB);
		}
	}

	/** Creates a bad Outcome, which is an outcome with an error **/
	protected void badOutcome(double time, String logMsg) throws Exception {
		stopNegotiation = true;
		double rvADiscounted = spaceA.getReservationValueWithDiscount(time);
		double rvBDiscounted = spaceB.getReservationValueWithDiscount(time);
		double rvA = spaceA.getReservationValueUndiscounted();
		double rvB = spaceB.getReservationValueUndiscounted();

		BidPoint lastbidPoint = new BidPoint(lastBid, rvA, rvB);
		BidPoint nash = getBidSpace().getNash();
		double distanceToNash = lastbidPoint.getDistance(nash);
		newOutcome(currentAgent, rvA, rvB, rvADiscounted, rvBDiscounted,
				logMsg, time, distanceToNash, "");
	}

	/**
	 * Creates the different outcomes for an agent that is using a MAC Calls
	 * createOutcome which actually creates the outcome to be logged.
	 ***/
	protected void createMACOutcomes(double time) throws Exception {
		updateOutcomeTupleList();
		for (ArrayList<OutcomeTuple> agentList : completeList) {
			for (OutcomeTuple outcomeTuple : agentList) {
				String logMsg = "";
				if (outcomeTuple.getLogMsgType() == "deadline") {
					logMsg = "Deadline reached while waiting for ["
							+ currentAgent + "]";
					badOutcome(time, logMsg);
					changeNameofAC(agentAWithMultiAC, agentBWithMultiAC,
							outcomeTuple);
					MACoutcomes.add(no);
				} else if (outcomeTuple.getLogMsgType() == "breakoff") {
					logMsg = "Agent ["
							+ currentAgent.getName()
							+ "] sent EndNegotiation, so the negotiation ended without agreement";
					badOutcome(time, logMsg);
					changeNameofAC(agentAWithMultiAC, agentBWithMultiAC,
							outcomeTuple);
					MACoutcomes.add(no);
				} else if (outcomeTuple.getLogMsgType() == "accept") {
					createOutcome(outcomeTuple.getLastBid(), time, true,
							outcomeTuple, outcomeTuple.getAcceptedBy());
					continue;
				}
			}
		}
	}

	protected void createBadMACOutcomes() {

	}

	/**
	 * This is the running method of the negotiation thread. It contains the
	 * work flow of the negotiation.
	 */
	protected void checkAgentActivity(Agent agent) {
		if (agent.equals(agentA))
			agentAtookAction = true;
		else
			agentBtookAction = true;

		if (timeline instanceof DiscreteTimeline)
			((DiscreteTimeline) timeline).increment();

	}

	public Agent otherAgent(Agent ag) {
		if (ag == agentA)
			return agentB;
		return agentA;
	}

	/**
	 * Make a new outcome and update table
	 * 
	 * @param distanceToNash
	 */
	private void newOutcome(Agent currentAgent, double utilA, double utilB,
			double utilADiscount, double utilBDiscount, String message,
			double time, double distanceToNash, String acceptedBy)
			throws Exception {
			OutcomeInfo outcomeInfo = new OutcomeInfo(agentA.getName(), agentB.getName(), 
					agentA.getClass().getCanonicalName(), 
					agentB.getClass().getCanonicalName(), 
					utilA, utilB, utilADiscount, utilBDiscount, 
					message, 1.0, 1.0,
					spaceA.getDomain().getName(),
					spaceA.getFileName(), spaceB.getFileName(),
					time, acceptedBy
			);
			
			
		no = new NegotiationOutcome(this, sessionNumber, lastAction, fAgentABids, fAgentBBids,startingWithA,  additionalLog, distanceToNash, outcomeInfo);
		calculateFinalAccuracy(no);
		boolean agreement = (lastAction instanceof Accept);
		processDataForLogging(time, agreement);
		if (showGUI) {
			fireNegotiationActionEvent(currentAgent, lastAction, fAgentABids.size() + fAgentBBids.size(),
					System.currentTimeMillis() - startTimeMillies, time, utilA,
					utilB, utilADiscount, utilBDiscount, message, true);
		}
		if (Global.LOW_MEMORY_MODE) {
			BidSpaceCache.removeBidSpace(spaceA, spaceB);
		}
	}

	/**
	 * Creates a newOutcome for the MAC where the amount of Bids is different
	 * than in the final stage of the negotiation
	 **/
	private void newOutcome(Agent currentAgent, Action lastAction,
			double utilA, double utilB, double utilADiscount,
			double utilBDiscount, String message,
			ArrayList<BidPointTime> agentASize,
			ArrayList<BidPointTime> agentBSize, double time,
			double distanceToNash, String acceptedBy) throws Exception {
		OutcomeInfo outcomeInfo = new OutcomeInfo(agentA.getName(), agentB.getName(), 
				agentA.getClass().getCanonicalName(), 
				agentB.getClass().getCanonicalName(), 
				utilA, utilB, utilADiscount, utilBDiscount, 
				message, 1.0, 1.0,
				spaceA.getDomain().getName(),
				spaceA.getFileName(), spaceB.getFileName(),
				time, acceptedBy
		);
		
		
		no = new NegotiationOutcome(this, sessionNumber, lastAction, (ArrayList<BidPointTime>) agentASize, 	(ArrayList<BidPointTime>) agentBSize,startingWithA,  additionalLog, distanceToNash, outcomeInfo);
		calculateFinalAccuracy(no);
		boolean agreement = (lastAction instanceof Accept);
		processDataForLogging(time, agreement);
		fireNegotiationActionEvent(currentAgent, lastAction, fAgentABids.size() + fAgentBBids.size(),
				System.currentTimeMillis() - startTimeMillies, time, utilA,
				utilB, utilADiscount, utilBDiscount, message, true);
		if (Global.LOW_MEMORY_MODE) {
			BidSpaceCache.removeBidSpace(spaceA, spaceB);
		}
	}

	private void calculateFinalAccuracy(NegotiationOutcome negoOutcome) {
		if (Global.CALCULATE_FINAL_ACCURACY) {
			if (agentA instanceof BOAagent) {
				OpponentModel opponentModel = ((BOAagent) agentA)
						.getOpponentModel();
				if (!(opponentModel instanceof NoModel)) {
					UtilitySpace estimatedOpponentUS = opponentModel
							.getOpponentUtilitySpace();
					BidSpace estimatedBS = null;
					try {
						estimatedBS = new BidSpace(spaceA, estimatedOpponentUS,
								false, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					omMeasuresResults
							.addPearsonCorrelationCoefficientOfBids(omMeasures
									.calculatePearsonCorrelationCoefficientBids(estimatedOpponentUS));
					omMeasuresResults.addRankingDistanceOfBids(omMeasures
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
					omMeasuresResults.addParetoFrontierDistance(omMeasures
							.calculateParetoFrontierDistance(estimatedBS));
					estimatedBS = null;
					estimatedOpponentUS = null;
					System.gc();
					negoOutcome.setNegotiationOutcome(omMeasuresResults);
				}
			}

		}
	}

	private void processDataForLogging(double time, boolean agreement) {
		// DEFAULT: disable trace logging
		if (protocol.getConfiguration() != null
				&& protocol.getConfiguration().containsKey(
						"logNegotiationTrace")
				&& protocol.getConfiguration().get("logNegotiationTrace") == 1) {
			matchDataLogger.addMeasure("time",
					omMeasuresResults.getTimePointList());
			matchDataLogger.addMeasure("bidindices",
					omMeasuresResults.getBidIndices());
			matchDataLogger.writeToFileCompact(time, agreement,
					protocol.getRun());
		}
	}

	/**
	 * This is called whenever the protocol is timed-out. What happens in case
	 * of a time-out is (1) the sessionrunner is killed with a
	 * Thread.interrupt() call from the NegotiationSession2. (2) judgeTimeout()
	 * is called.
	 * 
	 * @author W.Pasman
	 */
	public void JudgeTimeout() {
		System.out.println("Judging time-out.");

		try {
			// checkForMAC();
			double time = 1;
			double rvADiscounted = spaceA.getReservationValueWithDiscount(time);
			double rvBDiscounted = spaceB.getReservationValueWithDiscount(time);
			double rvA = spaceA.getReservationValueUndiscounted();
			double rvB = spaceB.getReservationValueUndiscounted();

			BidPoint lastbidPoint = new BidPoint(lastBid, rvA, rvB);
			BidPoint nash = getBidSpace().getNash();
			double distanceToNash = lastbidPoint.getDistance(nash);
			if (!hasMAC()) {
				newOutcome(currentAgent, rvA, rvB, rvADiscounted,
						rvBDiscounted,
						"JudgeTimeout: negotiation was timed out", time,
						distanceToNash, "");
			} else {
				System.out.println("Judge Timeout: with MAC agent");

				if (agentAWithMultiAC) {
					AcceptanceStrategy AC = ((BOAagent) agentA)
							.getAcceptanceStrategy();
					((Multi_AcceptanceCondition) AC).remainingACJudgeTimeout();
				}
				if (agentBWithMultiAC) {
					AcceptanceStrategy AC = ((BOAagent) agentB)
							.getAcceptanceStrategy();
					((Multi_AcceptanceCondition) AC).remainingACJudgeTimeout();
				}
				updateOutcomeTupleList();
				for (ArrayList<OutcomeTuple> outcomeTupleList : completeList)
					for (OutcomeTuple outcomeTuple : outcomeTupleList) {
						if (outcomeTuple.getLogMsgType().equalsIgnoreCase(
								"Judge Timeout")) {
							newOutcome(currentAgent, rvA, rvB, rvADiscounted,
									rvBDiscounted,
									"JudgeTimeout: negotiation was timed out",
									time, distanceToNash, "");
							changeNameofAC(agentAWithMultiAC,
									agentBWithMultiAC, outcomeTuple);
							// System.out.println("OutcomeTuple: " +
							// outcomeTuple.toString());
							MACoutcomes.add(no);
						}
						createMACOutcomes(time);

					}
			}
		} catch (Exception err) {
			new Warning("error during creation of new outcome:", err, true, 2);
		}
		// don't bother about max utility, both have zero anyway.

	}

	/** Creates an actual outcome object that can be logged **/
	public void createOutcome(Bid lastBid, double time, boolean isMac,
			OutcomeTuple outcomeTuple, String acceptedBy) throws Exception {

		stopNegotiation = true;
		// Accept accept = (Accept)lastAction;
		double agentAUtility;
		double agentBUtility;
		double agentAUtilityDisc;
		double agentBUtilityDisc;
		double distanceToNash;

		agentAUtilityDisc = spaceA.getUtilityWithDiscount(lastBid, time);
		agentBUtilityDisc = spaceB.getUtilityWithDiscount(lastBid, time);

		agentAUtility = spaceA.getUtility(lastBid);
		agentBUtility = spaceB.getUtility(lastBid);

		BidPoint lastbidPoint = new BidPoint(lastBid, agentAUtility,
				agentBUtility);
		BidPoint nash = getBidSpace().getNash();
		distanceToNash = lastbidPoint.getDistance(nash);

		if (isMac) {
			ArrayList<BidPointTime> subAgentABids = null;
			ArrayList<BidPointTime> subAgentBBids = null;

			if (outcomeTuple == null) {
				newOutcome(currentAgent, agentAUtility, agentBUtility,
						agentAUtilityDisc, agentBUtilityDisc, null, time,
						distanceToNash, acceptedBy);
				System.out.println("OutcomeTuple is null");
				MACoutcomes.add(no);
			} else {

				if (fAgentABids.size() == outcomeTuple.getAgentASize()
						|| fAgentBBids.size() == outcomeTuple.getAgentBSize()) {
					subAgentABids = fAgentABids;
					subAgentBBids = fAgentBBids;
				} else {
					subAgentABids = new ArrayList<BidPointTime>(
							fAgentABids.subList(0,
									outcomeTuple.getAgentASize() + 1));
					subAgentBBids = new ArrayList<BidPointTime>(
							fAgentBBids.subList(0,
									outcomeTuple.getAgentBSize() + 1));
				}
				newOutcome(currentAgent, new Accept(agentB.getAgentID()),
						agentAUtility, agentBUtility, agentAUtilityDisc,
						agentBUtilityDisc, null, subAgentABids, subAgentBBids,
						outcomeTuple.getTime(), distanceToNash,
						outcomeTuple.getAcceptedBy());
				changeNameofAC(agentAWithMultiAC, agentBWithMultiAC, outcomeTuple);
				MACoutcomes.add(no);
			}

		} else {
			newOutcome(currentAgent, agentAUtility, agentBUtility,
					agentAUtilityDisc, agentBUtilityDisc, null, time,
					distanceToNash, acceptedBy);
		}

	}

	/**
	 * This changes the name of the AC from "Multi Acceptance Criteria" to the
	 * correct AC
	 **/
	private void changeNameofAC(boolean agentAWithMultiAC,
			boolean agentBWithMultiAC, OutcomeTuple tuple) {
		if (agentAWithMultiAC) {
			String newName = no.agentAname.replaceAll(
					"Multi Acceptance Criteria", tuple.getName());
			no.agentAname = newName;
		}
		if (agentBWithMultiAC) {
			String newName = no.agentBname.replaceAll(
					"Multi Acceptance Criteria", tuple.getName());
			no.agentBname = newName;
		}

	}

	public NegotiationOutcome getNegotiationOutcome() {
		return no;
	}

	public String getStartingAgent() {
		return startingAgent;
	}

	public void setStartingWithA(boolean val) {
		startingWithA = val;
	}

	public void setTotalTime(int val) {
		totalTime = val;
	}

	public void setSessionTotalNumber(int val) {
		sessionTotalNumber = val;
	}
}