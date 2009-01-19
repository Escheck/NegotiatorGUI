package negotiator.protocol.auction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import agents.BayesianAgentForAuction;

import negotiator.Agent;
import negotiator.AgentParam;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.NegotiationOutcome;
import negotiator.analysis.BidSpace;
import negotiator.exceptions.Warning;
import negotiator.protocol.Protocol;
import negotiator.protocol.alternatingoffers.AlternatingOffersBilateralAtomicNegoSession;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.Tournament;
import negotiator.tournament.VariablesAndValues.*;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

public class AuctionProtocol extends Protocol {
	final private double ALLOWED_UTILITY_DEVIATION = 0.015; 

	public AuctionProtocol(AgentRepItem[] agentRepItems,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams)
			throws Exception {
		super(agentRepItems, profileRepItems, agentParams);
		// TODO Auto-generated constructor stub
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

	@Override
	public void startSession() {
		// TODO Auto-generated method stub
		
	}

	public void run() {
		try { 
			int numberOfSellers = getNumberOfAgents()-1;
			//calcualte theoretical outcome			
			double outcome[] = new double[numberOfSellers ];
			//int i=0;
			for (int i=0;i<numberOfSellers ;i++) {
				UtilitySpace centerUtilitySpace = getAgentUtilitySpaces(0);
				UtilitySpace sellerUtilitySpace = getAgentUtilitySpaces(1+i);
				outcome[i] = Double.NEGATIVE_INFINITY;
				BidIterator iter = new BidIterator(centerUtilitySpace.getDomain());
				while(iter.hasNext()) {
					Bid bid = iter.next();
					if(Math.abs(sellerUtilitySpace.getUtility(bid)-sellerUtilitySpace.getReservationValue())<ALLOWED_UTILITY_DEVIATION) {
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
				space.setAttribute("spaceA",sessions.get(i).getAgentAUtilitySpaceFileName() );
				space.setAttribute("spaceB",sessions.get(i).getAgentBUtilitySpaceFileName());
				SimpleElement solution = new SimpleElement("solution");
				optimalPoints.addChildElement(solution);
				solution.setAttribute("type", "Nash");
				solution.setAttribute("utilityA", String.valueOf(sessions.get(i).getBidSpace().getNash().utilityA));
				solution.setAttribute("utilityB", String.valueOf(sessions.get(i).getBidSpace().getNash().utilityB));
				solution = new SimpleElement("solution");
				optimalPoints.addChildElement(solution);
				solution.setAttribute("type", "Kalai");
				solution.setAttribute("utilityA", String.valueOf(sessions.get(i).getBidSpace().getKalaiSmorodinsky().utilityA));
				solution.setAttribute("utilityB", String.valueOf(sessions.get(i).getBidSpace().getKalaiSmorodinsky().utilityB));
			}
			//run the sessions
			for (int i=0;i<numberOfSellers;i++) {
				//if (the_event_listener!=null) s.actionEventListener=the_event_listener;
				AuctionBilateralAtomicNegoSession session = new AuctionBilateralAtomicNegoSession(
						)
				for (NegotiationEventListener list: negotiationEventListeners) s.addNegotiationEventListener(list);
				fireNegotiationSessionEvent(s);
				s.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
			}
			//determine winner
			double lMaxUtil= Double.NEGATIVE_INFINITY;
			double lSecondPrice = Double.NEGATIVE_INFINITY;
			AuctionBilateralAtomicNegoSession winnerSession = null;
//				NegotiationSession2 secondBestSession = null;
			for (AlternatingOffersNegotiationSession s: sessions) {
				if(s.getSessionRunner().getNegotiationOutcome().agentAutility>lMaxUtil) {
					lSecondPrice = lMaxUtil;
					lMaxUtil = s.getSessionRunner().getNegotiationOutcome().agentAutility;
					//secondBestSession = winnerSession;
					winnerSession = s;
				} else if(s.getSessionRunner().getNegotiationOutcome().agentAutility>lSecondPrice) 
					lSecondPrice = s.getSessionRunner().getNegotiationOutcome().agentAutility;
			}
			HashMap<AgentParameterVariable,AgentParamValue>  paramsA=new HashMap<AgentParameterVariable,AgentParamValue> ();
			HashMap<AgentParameterVariable,AgentParamValue>  paramsB=new HashMap<AgentParameterVariable,AgentParamValue> ();
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(0.9));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(lSecondPrice));
			//paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(0.6));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(-0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
			AuctionBilateralAtomicNegoSession secondPhaseSession = new AuctionBilateralAtomicNegoSession(
						winnerSession.agentArep,
						winnerSession.agentBrep,
						winnerSession.getProfileArep(),
						winnerSession.getProfileBrep(),
						winnerSession.getAgentAname(),
						winnerSession.getAgentBname(),
						paramsA,
						paramsB,
						100,
						1,
						true, 1200, 1200, 1
				);
				secondPhaseSession.setAdditional(theoreticalOutcome);
				for (NegotiationEventListener list: negotiationEventListeners) 
					secondPhaseSession.addNegotiationEventListener(list);
				fireBilateralAtomicNegotiationSessionEvent(secondPhaseSession, profileA, profileB, agentA, agentB);
				secondPhaseSession.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
	   		
		} catch (Exception e) { e.printStackTrace(); new Warning("Fatail error cancelled tournament run:"+e); }
	}
	private static ArrayList<AuctionProtocol> allSessions;
	
	public static ArrayList<Protocol> getTournamentSessions(Tournament tournament) throws Exception {
		if(allSessions==null) generateAllSessions(tournament);
		if(sessionIndex<allSessions.size()) {
			ArrayList<AuctionBilateralAtomicNegoSession> result = allSessions.get(sessionIndex);
			sessionIndex++;
			return result;
		}
		else return null;
	}


	protected static AuctionProtocol createSession(Tournament tournament,ProfileRepItem profileCenter, ProfileRepItem profileSeller1, ProfileRepItem profileSeller2) throws Exception {

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
		HashMap<AgentParameterVariable, AgentParamValue>[] params = new HashMap[3]; 
		params[0] = paramsA;
		params[1] = paramsB;
		params[2] = paramsB;
		AuctionProtocol session = new  AuctionProtocol (agents,  profiles,	params) ;
		return session;

	}
	private static void generateAllSessions(Tournament tournament) {
		try {
			allSessions = new ArrayList<AuctionProtocol>();
			//sessionIndex = 0;
			DomainRepItem domain = new DomainRepItem(new URL("file:etc/templates/SON/son_domain.xml"));
			//center profiles
			ProfileRepItem center1 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_1.xml"),domain);
			ProfileRepItem center2 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_2.xml"),domain);
			ProfileRepItem center3 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_3.xml"),domain);
			ProfileRepItem center4 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_4.xml"),domain);
			ProfileRepItem center5 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_5.xml"),domain);
			ProfileRepItem center6 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_6.xml"),domain);
			ProfileRepItem center7 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_7.xml"),domain);
			ProfileRepItem center8 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_8.xml"),domain);
			ProfileRepItem center9 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_9.xml"),domain);
			ProfileRepItem center10 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_10.xml"),domain);
			ProfileRepItem center11 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_11.xml"),domain);
			ProfileRepItem center12 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_12.xml"),domain);
			//seller profiles
			ProfileRepItem seller1 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_1.xml"),domain);
			ProfileRepItem seller2 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_2.xml"),domain);
			ProfileRepItem seller3 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_3.xml"),domain);
			ProfileRepItem seller4 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_4.xml"),domain);
			ProfileRepItem seller5 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_5.xml"),domain);
			ProfileRepItem seller6 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_6.xml"),domain);
			ProfileRepItem seller7 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_7.xml"),domain);
			ProfileRepItem seller8 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_8.xml"),domain);
			ProfileRepItem seller9 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_9.xml"),domain);
			ProfileRepItem seller10 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_10.xml"),domain);
			ProfileRepItem seller11 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_11.xml"),domain);
			ProfileRepItem seller12 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_12.xml"),domain);
			
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
			// TODO: handle exception
			e.printStackTrace();
		}

	}
    /** do test run of negotiation session.
     * There may be multiple test runs of a single session, for isntance to take the average score.
     * returns the result in the global field "outcome"
     * @param nr is the sessionTestNumber
     * @throws Exception
     * 
     */
    protected void runNegotiationSession(int nr)  throws Exception
    {
    	java.lang.ClassLoader loaderA = ClassLoader.getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
    	agentA = (Agent)(loaderA.loadClass(getAgentARep().getClassPath()).newInstance());
   		agentA.setName(getAgentAname());

   		java.lang.ClassLoader loaderB =ClassLoader.getSystemClassLoader();
    	agentB = (Agent)(loaderB.loadClass(getAgentBRep().getClassPath()).newInstance());
    	agentB.setName(getAgentBname());
    	
    	sessionTestNumber=nr;
    	if(tournamentRunner!= null) tournamentRunner.fireNegotiationSessionEvent(this);
        //NegotiationSession nego = new NegotiationSession(agentA, agentB, nt, sessionNumber, sessionTotalNumber,agentAStarts,actionEventListener,this);
    	//SessionRunner sessionrunner=new SessionRunner(this);
    	startingAgent=getAgentAname();
    	if ( (!startingWithA) && new Random().nextInt(2)==1) { 
    		startingAgent=getAgentBname();
    	}
    	AuctionBilateralAtomicNegoSession sessionrunner;
    	sessionrunner=new AuctionBilateralAtomicNegoSession(this, 
    							agentA, 
    							agentB, 
    							getAgentAname(),
    							getAgentBname(),
    							getAgentAUtilitySpace(), 
    							getAgentBUtilitySpace(), 
    							getAgentAparams(),
    							getAgentBparams(),
    							startingAgent,
    							non_gui_nego_time);
    	fireBilateralAtomicNegotiationSessionEvent(sessionrunner,  getProfileArep(), getProfileBrep(),getAgentARep(), getAgentBRep());
    	if(Global.fDebug) {
    		sessionrunner.run();
        } else {
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
        	//System.out.println("nego finished. "+System.currentTimeMillis()/1000);
        	//synchronized (this) { try { wait(1000); } catch (Exception e) { System.out.println("2nd wait gets exception:"+e);} }
        
    	stopNegotiation();
    		
        // add path to the analysis chart
        // TODO Wouter: I removed this, not the job of a negotiationsession. We have no nt here anyway.
        //if (nt.getBidSpace()!=null)
        //	nt.addNegotiationPaths(sessionNumber, nego.getAgentABids(), nego.getAgentBBids());
        	
    	if(sessionrunner.no==null) {
    		sessionrunner.JudgeTimeout();
    	}
    		outcome=sessionrunner.no;
    		//sf.addNegotiationOutcome(outcome);        // add new result to the outcome list.
    		if(fAdditional!=null) { 
    			if(outcome.additional==null) {
    				outcome.additional = new SimpleElement("additional");
    			
    			}
    			outcome.additional.addChildElement(fAdditional);
    		}
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("outcomes.xml",true));
            out.write(""+outcome.toXML());
            out.close();
        } catch (Exception e) {
        	new Warning("Exception during writing s:"+e);
        	e.printStackTrace();
        }
        
    }
    


}
