package negotiator.protocol.auction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import negotiator.Agent;
import negotiator.AgentParam;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.NegotiationOutcome;
import negotiator.analysis.BidSpace;
import negotiator.exceptions.Warning;
import negotiator.protocol.OldProtocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.Tournament;
import negotiator.tournament.TournamentConfiguration;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.AgentValue;
import negotiator.tournament.VariablesAndValues.AgentVariable;
import negotiator.tournament.VariablesAndValues.TournamentValue;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;
import agents.BayesianAgentForAuction;

public class AuctionOldProtocol extends OldProtocol
{

	private static final long serialVersionUID = -5739935989755679374L;
	final protected double ALLOWED_UTILITY_DEVIATION = 0.015; 
	private boolean startingWithA = false;
	public int non_gui_nego_time = 120;
	public int gui_nego_time=60*30; 	// Nego time if a GUI is involved in the nego

	public AuctionOldProtocol(AgentRepItem[] agentRepItems,
                              ProfileRepItem[] profileRepItems,
                              HashMap<AgentParameterVariable, AgentParamValue>[] agentParams,
                              int tSR)
	throws Exception {
		super(agentRepItems, profileRepItems, agentParams, tSR);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NegotiationOutcome getNegotiationOutcome() {
		// TODO Auto-generated method stub
		return null;
	}
	protected void calculateTheoreticalOutcome() {
		try {
			int numberOfSellers = getNumberOfAgents()-1;
			double outcome[] = new double[numberOfSellers ];
			//int i=0;
			for (int i=0;i<numberOfSellers ;i++) {
				UtilitySpace centerUtilitySpace = getAgentUtilitySpaces(0);
				UtilitySpace sellerUtilitySpace = getAgentUtilitySpaces(1+i);
				outcome[i] = Double.NEGATIVE_INFINITY;
				BidIterator iter = new BidIterator(centerUtilitySpace.getDomain());
				while(iter.hasNext()) {
					Bid bid = iter.next();
					if(Math.abs(sellerUtilitySpace.getUtility(bid)-sellerUtilitySpace.getReservationValueUndiscounted())<ALLOWED_UTILITY_DEVIATION) {
						double lTmpExpecteUtility = centerUtilitySpace.getUtility(bid);
						if(lTmpExpecteUtility > outcome[i]) {
							outcome[i]= lTmpExpecteUtility ;
						}
					}									
				}				
			}
			//find the winner
			int winner = 0;			
			double lSecondBest = Double.NEGATIVE_INFINITY;
			for(int i=1;i<outcome.length;i++) {
				if(outcome[i]>outcome[winner]) {
					lSecondBest = outcome[winner];
					winner = i;
				} else if (outcome[i]>lSecondBest) {
					lSecondBest =outcome[i]; 
				}
			}
			//find the final outcome
			UtilitySpace centerUtilitySpace = getAgentUtilitySpaces(0);
			UtilitySpace sellerUtilitySpace = getAgentUtilitySpaces(1+winner);
			double finalOutcome[] = new double[2];
			BidIterator iter = new BidIterator(centerUtilitySpace.getDomain());
			while(iter.hasNext()) {
				Bid bid = iter.next();
				if(Math.abs(centerUtilitySpace.getUtility(bid)-lSecondBest)<ALLOWED_UTILITY_DEVIATION) {
					//double lTmpSim = fSimilarity.getSimilarity(tmpBid, pOppntBid);
					double lTmpExpecteUtility = sellerUtilitySpace.getUtility(bid);
					if(lTmpExpecteUtility > finalOutcome[0]) {
						finalOutcome[0] = lTmpExpecteUtility;
						finalOutcome[1] = centerUtilitySpace.getUtility(bid); 
					}
				}				
			}

			SimpleElement theoreticalOutcome = new SimpleElement("theoretical_outcome");
			theoreticalOutcome.setAttribute("winner", getProfileRepItems(1+winner).getURL().getFile());
			theoreticalOutcome.setAttribute("center_utility", String.valueOf(finalOutcome[1]));
			theoreticalOutcome.setAttribute("seller_utility", String.valueOf(finalOutcome[0]));
			SimpleElement optimalPoints = new SimpleElement("optimal_solution");
			theoreticalOutcome.addChildElement(optimalPoints);

			for(int i=0;i<numberOfSellers;i++) {
				SimpleElement space = new SimpleElement("utility_space");
				optimalPoints.addChildElement(space);
				space.setAttribute("spaceA",getProfileRepItems(0).getURL().getFile() );
				space.setAttribute("spaceB",getProfileRepItems(1+i).getURL().getFile() );
				SimpleElement solution = new SimpleElement("solution");
				optimalPoints.addChildElement(solution);
				solution.setAttribute("type", "Nash");
				BidSpace bidSpace = new BidSpace(getAgentUtilitySpaces(0), getAgentUtilitySpaces(i+1));
				solution.setAttribute("utilityA", String.valueOf(bidSpace.getNash().getUtilityA()));
				solution.setAttribute("utilityB", String.valueOf(bidSpace.getNash().getUtilityB()));
				solution = new SimpleElement("solution");
				optimalPoints.addChildElement(solution);
				solution.setAttribute("type", "Kalai");
				solution.setAttribute("utilityA", String.valueOf(bidSpace.getKalaiSmorodinsky().getUtilityA()));
				solution.setAttribute("utilityB", String.valueOf(bidSpace.getKalaiSmorodinsky().getUtilityB()));
			}
		}		
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try { 
			int numberOfSellers = getNumberOfAgents()-1;
			//calcualte theoretical outcome			
			//run the sessions
			AuctionBilateralAtomicNegoSession[] sessions = new AuctionBilateralAtomicNegoSession[numberOfSellers];
			for (int i=0;i<numberOfSellers;i++) {
				sessions[i] = 
					runNegotiationSession(
							getAgentRepItem(0),
							getAgentRepItem(i+1),
							"Buyer", "Seller", 
							getProfileRepItems(0),
							getProfileRepItems(i+1),
							getAgentUtilitySpaces(0),
							getAgentUtilitySpaces(i+1),
							getAgentParams(0),
							getAgentParams(i+1));
			}
			//determine winner
			double lMaxUtil= Double.NEGATIVE_INFINITY;
			double lSecondPrice = Double.NEGATIVE_INFINITY;
			AuctionBilateralAtomicNegoSession winnerSession = null;
			//				NegotiationSession2 secondBestSession = null;
			int winnerSessionIndex=0, i=0;
			for (AuctionBilateralAtomicNegoSession s: sessions) {
				if(s.getNegotiationOutcome().agentAutility>lMaxUtil) {
					lSecondPrice = lMaxUtil;
					lMaxUtil = s.getNegotiationOutcome().agentAutility;
					//secondBestSession = winnerSession;
					winnerSession = s;
					winnerSessionIndex = i;
				} else if(s.getNegotiationOutcome().agentAutility>lSecondPrice) 
					lSecondPrice = s.getNegotiationOutcome().agentAutility;
				i++;
			}

			HashMap<AgentParameterVariable,AgentParamValue> paramsA = new HashMap<AgentParameterVariable,AgentParamValue> ();
			HashMap<AgentParameterVariable,AgentParamValue> paramsB = new HashMap<AgentParameterVariable,AgentParamValue> ();
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(0.9));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(lSecondPrice));
			//paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(0.6));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(-0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));

			AuctionBilateralAtomicNegoSession secondPhaseSession = 
				runNegotiationSession(
						getAgentRepItem(0), 
						getAgentRepItem(1+winnerSessionIndex), 
						"Buyer", "Seller", 
						getProfileRepItems(0),
						getProfileRepItems(1+winnerSessionIndex),
						getAgentUtilitySpaces(0),
						getAgentUtilitySpaces(1+winnerSessionIndex), 
						paramsA, paramsB); 

		} catch (Exception e) { e.printStackTrace(); new Warning("Fatal error cancelled tournament run:"+e); }
	}


	public static ArrayList<OldProtocol> getTournamentSessions(Tournament tournament) throws Exception {
		return generateAllSessions(tournament);
	}


	protected static AuctionOldProtocol createSession(Tournament tournament,ProfileRepItem profileCenter, ProfileRepItem profileSeller1, ProfileRepItem profileSeller2) throws Exception {

		ArrayList<AgentVariable> agentVars=tournament.getAgentVars();
		if (agentVars.size()!=2) throw new IllegalStateException("Tournament does not contain 2 agent variables");
		ArrayList<TournamentValue> agentAvalues=agentVars.get(0).getValues();
		if (agentAvalues.isEmpty()) 
			throw new IllegalStateException("Agent A does not contain any values!");
		ArrayList<TournamentValue> agentBvalues=agentVars.get(1).getValues();
		if (agentBvalues.isEmpty()) 
			throw new IllegalStateException("Agent B does not contain any values!");

		ProfileRepItem[] profiles= new ProfileRepItem[3];//getProfiles();
		profiles[0] = profileCenter;
		profiles[1] = profileSeller1;
		profiles[2] = profileSeller2;
		AgentRepItem[] agents= new AgentRepItem[3];//getProfiles();
		AgentRepItem agentA=((AgentValue)agentAvalues.get(0)).getValue();
		agents[0] = agentA;
		agents[1] = agentA;
		agents[2] = agentA;
		//prepare parameters
		HashMap<AgentParameterVariable,AgentParamValue> paramsA = new HashMap<AgentParameterVariable,AgentParamValue>();
		HashMap<AgentParameterVariable,AgentParamValue> paramsB = new HashMap<AgentParameterVariable,AgentParamValue>();
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(2.1));
		//paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(reservationValue));
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(-0.9));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(2.1));
		//paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(reservationValue));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(-0.9));
		@SuppressWarnings("unchecked") // Google creating generic array types in Java
		HashMap<AgentParameterVariable, AgentParamValue>[] params = new HashMap[3]; 
		params[0] = paramsA;
		params[1] = paramsB;
		params[2] = paramsB;
		AuctionOldProtocol session = new AuctionOldProtocol(agents,  profiles,	params, 1);
		return session;

	}
	private static ArrayList<OldProtocol> generateAllSessions(Tournament tournament) {
		ArrayList<OldProtocol> allSessions = null;
		try {
			allSessions = new ArrayList<OldProtocol>();
			//sessionIndex = 0;
			DomainRepItem domain = new DomainRepItem(new URL("file:etc/templates/SON/son_domain.xml"));
			//center profiles
			//ProfileRepItem center1 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_1.xml"),domain);
			//ProfileRepItem center2 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_2.xml"),domain);
			//ProfileRepItem center3 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_3.xml"),domain);
			//ProfileRepItem center4 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_4.xml"),domain);
			//ProfileRepItem center5 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_5.xml"),domain);
			//ProfileRepItem center6 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_6.xml"),domain);
			//ProfileRepItem center7 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_7.xml"),domain);
			ProfileRepItem center8 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_8.xml"),domain);
			//ProfileRepItem center9 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_9.xml"),domain);
			ProfileRepItem center10 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_10.xml"),domain);
			//ProfileRepItem center11 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_11.xml"),domain);
			//ProfileRepItem center12 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_12.xml"),domain);
			//seller profiles
			//ProfileRepItem seller1 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_1.xml"),domain);
			//ProfileRepItem seller2 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_2.xml"),domain);
			ProfileRepItem seller3 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_3.xml"),domain);
			//ProfileRepItem seller4 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_4.xml"),domain);
			//ProfileRepItem seller5 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_5.xml"),domain);
			ProfileRepItem seller6 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_6.xml"),domain);
			//ProfileRepItem seller7 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_7.xml"),domain);
			//ProfileRepItem seller8 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_8.xml"),domain);
			//ProfileRepItem seller9 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_9.xml"),domain);
			ProfileRepItem seller10 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_10.xml"),domain);
			ProfileRepItem seller11 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_11.xml"),domain);
			//ProfileRepItem seller12 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_12.xml"),domain);

			//allSessions.add(createSession(center5, seller4, seller1, reservationValue));
			//allSessions.add(createSession(center3, seller6, seller5, reservationValue));
			//allSessions.add(createSession(center4, seller5, seller2));
			//allSessions.add(createSession(center8, seller2, seller11));
			//allSessions.add(createSession(center4, seller9, seller6));
			//allSessions.add(createSession(center12, seller2, seller10));
			//allSessions.add(createSession(center1, seller2, seller8));
			//allSessions.add(createSession(tournament,center10, seller7, seller9));
			//allSessions.add(createSession(tournament,center7, seller11, seller9));
			allSessions.add(createSession(tournament,center8, seller11, seller10));
			allSessions.add(createSession(tournament,center10, seller3, seller6));
			//allSessions.add(createSession(tournament,center7, seller4, seller7));
			//allSessions.add(createSession(tournament,center10, seller11, seller8));
			//allSessions.add(createSession(center11, seller5, seller11));
			//allSessions.add(createSession(center6, seller7, seller3));
			//allSessions.add(createSession(center6, seller10, seller5));
			//allSessions.add(createSession(center8, seller6, seller3));
			//allSessions.add(createSession(center2, seller5, seller1));
			//allSessions.add(createSession(center3, seller5, seller4));
			//allSessions.add(createSession(center10, seller5, seller2));
			//allSessions.add(createSession(center1, seller3, seller6));
			//allSessions.add(createSession(center3, seller5, seller4));
			//allSessions.add(createSession(center8, seller10, seller6));
			//allSessions.add(createSession(center4, seller12, seller3));
			//allSessions.add(createSession(center3, seller2, seller11));
			//allSessions.add(createSession(center6, seller2, seller5));
			//allSessions.add(createSession(center10, seller2, seller6));
			//allSessions.add(createSession(center12, seller8, seller12));
			//allSessions.add(createSession(center9, seller6, seller2));
			//allSessions.add(createSession(center7, seller7, seller11));
			//allSessions.add(createSession(center2, seller1, seller5));
			//allSessions.add(createSession(center10, seller8, seller10));
			//allSessions.add(createSession(center11, seller8, seller7));
			//allSessions.add(createSession(center8, seller7, seller10));
			//allSessions.add(createSession(center2, seller7, seller12));
			//allSessions.add(createSession(center10, seller12, seller7));
			//allSessions.add(createSession(center7, seller7, seller11));
			//allSessions.add(createSession(center2, seller1, seller5));
			//allSessions.add(createSession(center10, seller8, seller10));
			//allSessions.add(createSession(center11, seller8, seller7));
			//allSessions.add(createSession(center8, seller7, seller10));
			//allSessions.add(createSession(center2, seller7, seller12));
			//allSessions.add(createSession(center7, seller12, seller11));
			//allSessions.add(createSession(center12, seller8, seller10));
			//allSessions.add(createSession(center2, seller5, seller4));
			//allSessions.add(createSession(center3, seller10, seller12));
			//allSessions.add(createSession(center1, seller5, seller3));
			//allSessions.add(createSession(center11, seller7, seller10));
			//allSessions.add(createSession(center1, seller8, seller12));
			//allSessions.add(createSession(center10, seller4, seller6));
			//allSessions.add(createSession(center7, seller4, seller6));
			//allSessions.add(createSession(center2, seller3, seller6));
			//allSessions.add(createSession(center5, seller10, seller11));
			//allSessions.add(createSession(center12, seller7, seller10));
			//allSessions.add(createSession(center6, seller9, seller11));
			//allSessions.add(createSession(center4, seller10, seller7));
			//allSessions.add(createSession(center3, seller5, seller2));
			//allSessions.add(createSession(center7, seller2, seller4));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allSessions;
	}
	/** do test run of negotiation session.
	 * There may be multiple test runs of a single session, for isntance to take the average score.
	 * returns the result in the global field "outcome"
	 * @param nr is the sessionTestNumber
	 * @throws Exception
	 * 
	 */
	protected AuctionBilateralAtomicNegoSession runNegotiationSession(
			Agent agentA, 
			Agent agentB, 
			AgentRepItem agentARepItem, 
			AgentRepItem agentBRepItem, 			
			String agentAname, 
			String agentBname, 
			ProfileRepItem profileRepItemA,
			ProfileRepItem profileRepItemB,
			UtilitySpace spaceA, 
			UtilitySpace spaceB,
			HashMap<AgentParameterVariable, AgentParamValue> agentAparams,
			HashMap<AgentParameterVariable, AgentParamValue> agentBparams)  throws Exception
 
	{
		int sessionTestNumber=1;
		if(tournamentRunner!= null) tournamentRunner.fireNegotiationSessionEvent(this);
		//NegotiationSession nego = new NegotiationSession(agentA, agentB, nt, sessionNumber, sessionTotalNumber,agentAStarts,actionEventListener,this);
		//SessionRunner sessionrunner=new SessionRunner(this);

		String startingAgent=agentAname;
		if ( (!startingWithA) && new Random().nextInt(2)==1) { 
			startingAgent = agentBname;
		}

		AuctionBilateralAtomicNegoSession sessionrunner = 
			new AuctionBilateralAtomicNegoSession(
					this, 
					agentA, 
					agentB, 
					agentAname,
					agentBname,
					spaceA, 
					spaceB, 
					agentAparams,
					agentBparams,
					"Buyer",
					3600);
		fireBilateralAtomicNegotiationSessionEvent(sessionrunner,  profileRepItemA, profileRepItemB, agentARepItem, agentBRepItem, "", "");
		if(TournamentConfiguration.getBooleanOption("protocolMode", false)) {
			sessionrunner.run();
		} else {
			int totalTime;
			if(agentA.isUIAgent()||agentB.isUIAgent()) totalTime = non_gui_nego_time;
			else totalTime = gui_nego_time;
			negoThread = new Thread(sessionrunner);
			System.out.println("nego start. "+System.currentTimeMillis()/1000);
			negoThread.start();
			try {
				synchronized (this) {
					System.out.println("waiting NEGO_TIMEOUT="+totalTime*1000);
					// wait will unblock early if negotiation is finished in time.
					wait(totalTime*1000);
				}
			} catch (InterruptedException ie) { new Warning("wait cancelled:",ie); }
		}

		stopNegotiation();

		if(sessionrunner.no==null) {
			sessionrunner.JudgeTimeout();
		}

		NegotiationOutcome outcome=sessionrunner.no;
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("outcomes.xml",true));
			out.write(""+outcome.toXML());
			out.close();
		} catch (Exception e) {
			new Warning("Exception during writing s:"+e);
			e.printStackTrace();
		}
		return sessionrunner;
	
	}
	protected AuctionBilateralAtomicNegoSession runNegotiationSession(
			AgentRepItem agentARepItem, 
			AgentRepItem agentBRepItem, 
			String agentAname, 
			String agentBname, 
			ProfileRepItem profileRepItemA,
			ProfileRepItem profileRepItemB,
			UtilitySpace spaceA, 
			UtilitySpace spaceB,
			HashMap<AgentParameterVariable, AgentParamValue> agentAparams,
			HashMap<AgentParameterVariable, AgentParamValue> agentBparams)  throws Exception
			{
		java.lang.ClassLoader loaderA = ClassLoader.getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
		Agent agentA = (Agent)(loaderA.loadClass(agentARepItem.getClassPath()).newInstance());
		agentA.setName(agentAname);

		java.lang.ClassLoader loaderB = ClassLoader.getSystemClassLoader();
		Agent agentB = (Agent)(loaderB.loadClass(agentBRepItem.getClassPath()).newInstance());
		agentB.setName(agentBname);
		
		return runNegotiationSession(agentA, agentB, agentARepItem, agentBRepItem, agentAname, agentBname, profileRepItemA, profileRepItemB, spaceA, spaceB, agentAparams, agentBparams);

	}
}