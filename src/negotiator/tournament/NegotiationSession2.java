package negotiator.tournament;

import negotiator.Domain;

import negotiator.Main;
import negotiator.NegotiationOutcome; 

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import negotiator.actions.*;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;


import java.util.Random;

import javax.swing.JOptionPane;

import negotiator.*;
import negotiator.exceptions.Warning;

import negotiator.gui.SessionFrame;
import negotiator.gui.chart.Chart;
import negotiator.repository.*;
import negotiator.tournament.VariablesAndValues.*;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;



/**
 *
 * NegotiationSession is both the storage place for a negotiation session 
 * and the run-time object / thread doing a session.
 * Actually the SessionRunner does most of handling the nego session, here we have only code
 * for enforcing the time deadline.
 * 
 * @author W.Pasman. Lots of old code from NegotiationSession and NegotmationManager
 * 
 */
public class NegotiationSession2 implements Runnable {
	AgentRepItem agentArep;
	AgentRepItem agentBrep;
    private ProfileRepItem profileArep;
    private ProfileRepItem profileBrep;
    private String agentAname;
    private String agentBname;
    private ArrayList<AgentParamValue> agentAparams=new ArrayList<AgentParamValue> ();
    private ArrayList<AgentParamValue> agentBparams=new ArrayList<AgentParamValue> ();
    int sessionNumber;
    int sessionTotalNumber;
    boolean startingWithA=true;
    NegotiationEventListener actionEventListener;
	String startingAgent; // agentAname or agnetBname
	Integer totalTime; // will be set only AFTER running the session, because it depends on whether agent isUIAgent() or not
	NegotiationOutcome outcome;

    public static int NON_GUI_NEGO_TIME = 120;
    public static int GUI_NEGO_TIME=60*30; 	// Nego time if a GUI is involved in the nego


    /** fields copied from the NegotiationTemplate class */
    
    private Domain domain;
    private String agentAUtilitySpaceFileName;
    private String agentBUtilitySpaceFileName;
    private UtilitySpace fAgentAUtilitySpace;
    private UtilitySpace fAgentBUtilitySpace;
    private SimpleElement fRoot;
	private String fFileName;    
    //private Analysis fAnalysis;
	private BidSpace bidSpace=null;
    //private int totalTime; // total available time for nego, in seconds.
    
	
	SessionRunner sessionrunner;
    /** END OF fields copied from the NegotiationTemplate class */
    
    
    /** 
     * Creates a new instance of Negotiation 
     * @param agtA AgentRepItem (agent descriptor) for agent A.
     * @param agtB idem agent B.
     * @param profA ProfileRep Item (the profile descriptor) for agent A.
     * @param profB idem agent B.
     * @param nameA the run-name for agent A. This is not the class name!
     * @param nameB idem agent B.
     * @param agtApar parameters for Agent A. null is equivalent to empty parameters list.
     * @param agtBpar idem for agent B.
     * @param sessionnr
     * @param totalsessions
     * @param forceStartA true to force start with agent A. with false, start agent is chosen randomly.
     * @param ael is the callback point for bidding events. null means you won't be given call backs.
     * @throws Exception
     */
    public NegotiationSession2(AgentRepItem agtA, AgentRepItem agtB, ProfileRepItem profA, ProfileRepItem profB,
    		String nameA, String nameB,ArrayList<AgentParamValue> agtApar,ArrayList<AgentParamValue> agtBpar,
    		int sessionnr, int totalsessions,boolean forceStartA,
    		NegotiationEventListener ael) throws Exception {
    	agentArep=agtA;
    	agentBrep=agtB;
    	setProfileArep(profA);
    	setProfileBrep(profB);
    	setAgentAname(nameA);
    	setAgentBname(nameB);
    	if (agtApar!=null) setAgentAparams(agtApar);
    	if (agtBpar!=null) setAgentBparams(agtBpar);
    	sessionNumber=sessionnr;
    	sessionTotalNumber=totalsessions;
    	startingWithA=forceStartA;
    	actionEventListener=ael;
    	startingAgent=getAgentAname();
    	if ( (!startingWithA) && new Random().nextInt(2)==1) { 
    		startingAgent=getAgentBname();
    	}
   		fFileName = getProfileArep().getDomain().getURL().getFile();
		this.agentAUtilitySpaceFileName = getProfileArep().getURL().getFile();
		this.agentBUtilitySpaceFileName = getProfileBrep().getURL().getFile();      	
		loadFromFile(fFileName);
    	
    	check();
    }
   
    
    void check() throws Exception {
    	if (!(getProfileArep().getDomain().equals(getProfileBrep().getDomain())))
    		throw new IllegalArgumentException("profiles "+getProfileArep()+" and "+getProfileBrep()+" have a different domain.");
    }

    /***************** RUN A NEGO SESSION. code below comes from NegotiationManager ****************************/
    private Thread negoThread = null;
    //SessionFrame sf; // this will show the outcomes. Not really a job for NegoSession, TODO remove this,
 
     /**
      * Warning. You can call run() directly (instead of using Thread.start() )
      * but be aware that run() will not return until the nego session
      * has completed. That means that your interface will lock up until the session is complete.
      * And if the negosession uses modal interfaces, this will lock up swing, because modal
      * interfaces will not launch until the other swing interfaces have handled their events.
      * (at least this is my current understanding, Wouter, 22aug08).
      * See "java dialog deadlock" on the web...
      */
    public void run() {
    	try { 
    		startNegotiation();
    		// only sleep if batch mode????
    		Thread.sleep(1000); // 1 second delay before next nego starts. Used to be 5, is it needed anyway?
    		// Wouter: huh?? removed this           System.exit(0);
        } catch (Exception e) { new Warning("Problem starting negotiation:"+e); }
    }

    public void startNegotiation() throws Exception {
        //sf = new SessionFrame(agentAname, agentBname);
        //sf.setVisible(true);
       // Main.log("Starting negotiations...");
        for(int i=0;i<sessionTotalNumber;i++) {
            Main.log("Starting session " + String.valueOf(i+1));
            runNegotiationSession(i+1);
        }
    }
    
    
    protected void runNegotiationSession(int nr)  throws Exception
    {
    	sessionNumber=nr;
        //NegotiationSession nego = new NegotiationSession(agentA, agentB, nt, sessionNumber, sessionTotalNumber,agentAStarts,actionEventListener,this);
    	//SessionRunner sessionrunner=new SessionRunner(this);
    	sessionrunner=new SessionRunner(this);
    	totalTime=sessionrunner.totTime;
    	if(Main.fDebug) {
    		sessionrunner.run();
        } else {
        	negoThread = new Thread(sessionrunner);
            System.out.println("nego start. "+System.currentTimeMillis()/1000);
            negoThread.start();
        	try {
        		synchronized (this) {
        			System.out.println("waiting NEGO_TIMEOUT="+totalTime);
        			 // wait will unblock early if negotiation is finished in time.
    				wait(totalTime*1000);
        		}
        	} catch (InterruptedException ie) {
        		System.out.println("wait cancelled:"+ie.getMessage()); ie.printStackTrace();}
        	}
        	//System.out.println("nego finished. "+System.currentTimeMillis()/1000);
        	//synchronized (this) { try { wait(1000); } catch (Exception e) { System.out.println("2nd wait gets exception:"+e);} }
        
        	if (negoThread!=null&&negoThread.isAlive()) {
        		try {
        			negoThread.stop(); // kill the stuff
        			 // Wouter: this will throw a ThreadDeath Error into the nego thread
        			 // The nego thread will catch this and exit immediately.
        			 // Maybe it should not even try to catch that.
        		} catch (Exception e) {
        			System.out.println("problem stopping the nego:"+e.getMessage());
        			e.printStackTrace();
        		
        		}
        }
        // add path to the analysis chart
        // TODO Wouter: I removed this, not the job of a negotiationsession. We have no nt here anyway.
        //if (nt.getBidSpace()!=null)
        //	nt.addNegotiationPaths(sessionNumber, nego.getAgentABids(), nego.getAgentBBids());
        	
    	if(sessionrunner.no==null) {
    		try {
    		sessionrunner.newOutcome(null, 0, 0, new EndNegotiation(null), "nego result was null(aborted)");
    		} catch (Exception err) { new Warning("error during creation of new outcome:"+err); }
    		// don't bother about max utility, both have zero anyway.
    	}
    		outcome=sessionrunner.no;
    		//sf.addNegotiationOutcome(outcome);        // add new result to the outcome list. 
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("outcomes.xml",true));
            out.write(""+outcome.toXML());
            out.close();
        } catch (Exception e) {
        	new Warning("Exception during writing outcomes:"+e);
        }
        
    }
    public void stopNegotiation() {
        if (negoThread.isAlive()) {
            try {
//                negoThread.interrupt();
                negoThread.stop();
            } catch (Exception e) {
            }
        }
        return;
    }
    
    public String toString() {
    	return "NegotiationSession["+agentArep.getName()+" versus "+agentBrep.getName()+"]";
    }
 
    
    /* methods copied from the NegotiationTemplate class */
    
    
	/**
	 * @param fileName Wouter: I think this is the domain.xml file.
	 */
	private void loadFromFile(String fileName)  throws Exception
	{
		SimpleDOMParser parser = new SimpleDOMParser();
		BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));                  
		fRoot = parser.parse(file);
		/*            if (root.getAttribute("negotiation_type").equals("FDP"))this.negotiationType = FAIR_DEVISION_PROBLEM;
        else thisnegotiationType = CONVENTIONAL_NEGOTIATION;*/
		SimpleElement xml_utility_space = (SimpleElement)(fRoot.getChildByTagName("utility_space")[0]);
		domain = new Domain(xml_utility_space);
		loadAgentsUtilitySpaces();
		if (Main.analysisEnabled && !Main.batchMode)
		{
			if(fRoot.getChildByTagName("analysis").length>0) {
				//fAnalysis = new Analysis(this, (SimpleElement)(fRoot.getChildByTagName("analysis")[0]));
			} else {
				//propose to build an analysis
				Object[] options = {"Yes","No"};                  
				int n = JOptionPane.showOptionDialog(null,
						"You have no analysis available for this template. Do you want build it?",
						"No Analysis",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,
						options,
						options);
				if(n==0) {
					bidSpace=new BidSpace(fAgentAUtilitySpace,fAgentBUtilitySpace);
					//fAnalysis = Analysis.getInstance(this);
					//  save the analysis to the cache
					//fAnalysis.saveToCache();
				}
				
			}//if
		}
		//if(fAnalysis!=null) showAnalysis();	
		if (bidSpace!=null) showAnalysis();
	}

	
	
	/**
	 * 
	 * Show the analysis window. Couples the analysis object with the chart window 
	 * Wouter: old vesion by Dmytro.
	 * @throws Exception
	 */
	/*
	protected void showAnalysisOld() throws Exception
	{
		Chart lChart = new Chart();		
		//if((!fAnalysis.isCompleteSpaceBuilt())&&fAnalysis.getTotalNumberOfBids()<100000) 
		//	fAnalysis.buildCompleteOutcomeSpace();
		if(fAnalysis.isCompleteSpaceBuilt()) {			
			double[][] lAllBids = new double[fAnalysis.getTotalNumberOfBids()][2];
			for(int i=0;i<fAnalysis.getTotalNumberOfBids();i++) {
				lAllBids[i][0]= fAgentAUtilitySpace.getUtility(fAnalysis.getBidFromCompleteSpace(i));
				lAllBids[i][1]= fAgentBUtilitySpace.getUtility(fAnalysis.getBidFromCompleteSpace(i));
			}
			lChart.addCurve("All Outcomes", lAllBids);		
	
		}
		double[][] lParetoPoints = new double[fAnalysis.getParetoCount()][2];
		for(int i=0;i<fAnalysis.getParetoCount();i++) {
			lParetoPoints[i][0]= fAgentAUtilitySpace.getUtility(fAnalysis.getParetoBid(i));
			lParetoPoints[i][1]= fAgentBUtilitySpace.getUtility(fAnalysis.getParetoBid(i));
		}
		lChart.addCurve("Pareto frontier", lParetoPoints);		
		double[][] lNash = new double[1][2];
		lNash[0][0]= fAgentAUtilitySpace.getUtility(fAnalysis.getNashProduct());
		lNash[0][1]= fAgentBUtilitySpace.getUtility(fAnalysis.getNashProduct());		
		lChart.addCurve("Nash product", lNash);
		double[][] lKalaiSmorodinsky = new double[1][2];
		lKalaiSmorodinsky[0][0]= fAgentAUtilitySpace.getUtility(fAnalysis.getKalaiSmorodinsky());
		lKalaiSmorodinsky[0][1]= fAgentBUtilitySpace.getUtility(fAnalysis.getKalaiSmorodinsky());		
		lChart.addCurve("Kalai-Smorodinsky", lKalaiSmorodinsky);
		Main.fChart = lChart;
		lChart.show();
	}
	*/
	
	/**
	 * 
	 * Show the analysis window. Couples the analysis object with the chart window 
	 * Wouter: this version uses the BidSpace instead of the Analysis.
	 * 
	 * @throws Exception
	 */
	protected void showAnalysis() throws Exception
	{
		int i;
		if (bidSpace==null) throw new NullPointerException("bidspace=null, cant show analysis");
		Chart lChart = new Chart();

		i=0;
		double[][] lAllBids = new double[bidSpace.bidPoints.size()][2];
		for(BidPoint p: bidSpace.bidPoints) 
		  { lAllBids[i][0]= p.utilityA; lAllBids[i][1]= p.utilityB; i++;}
		lChart.addCurve("All Outcomes", lAllBids);		
	
		i=0;
		double[][] lParetoPoints=new double[bidSpace.getParetoFrontier().size()][2];
		for (BidPoint p:bidSpace.getParetoFrontier())
		  { lParetoPoints[i][0]= p.utilityA; lParetoPoints[i][1]= p.utilityB; i++;}
		lChart.addCurve("Pareto frontier", lParetoPoints);		

		BidPoint nash=bidSpace.getNash();
		double[][] lNash = new double[1][2];
		lNash[0][0]= nash.utilityA;	lNash[0][1]= nash.utilityB;	
		lChart.addCurve("Nash product", lNash);
		
		
		double[][] lKalaiSmorodinsky = new double[1][2];
		BidPoint kalai=bidSpace.getKalaiSmorodinsky();

		lKalaiSmorodinsky[0][0]= kalai.utilityA; lKalaiSmorodinsky[0][1]=kalai.utilityB;		
		lChart.addCurve("Kalai-Smorodinsky", lKalaiSmorodinsky);
		Main.fChart = lChart;
		//lChart.show();
	}
	
	
	/**
	 * 
	 * Call this method to draw the negotiation paths on the chart with analysis.
	 * Wouter: moved to here from Analysis. 
	 * @param pAgentABids
	 * @param pAgentBBids
	 */
	public void addNegotiationPaths(int sessionNumber, ArrayList<BidPoint> pAgentABids, ArrayList<BidPoint> pAgentBBids) 
	{
        double[][] lAgentAUtilities = new double[pAgentABids.size()][2];
        double[][] lAgentBUtilities = new double[pAgentBBids.size()][2];        
        try
        {
        	int i=0;
        	for (BidPoint p:pAgentABids)
        	{
	        	lAgentAUtilities [i][0] = p.utilityA;
	        	lAgentAUtilities [i][1] = p.utilityB;
	        	i++;
        	}
        	i=0;
        	for (BidPoint p:pAgentBBids)
        	{
	        	lAgentBUtilities [i][0] = p.utilityA;
	        	lAgentBUtilities [i][1] = p.utilityB;
	        	i++;
        	}
	        
	        if (Main.fChart==null) throw new Exception("fChart=null, can not add curve.");
	        Main.fChart.addCurve("Negotiation path of Agent A ("+String.valueOf(sessionNumber)+")", lAgentAUtilities);
	        Main.fChart.addCurve("Negotiation path of Agent B ("+String.valueOf(sessionNumber)+")", lAgentBUtilities);
	        Main.fChart.show();
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
		
	}
	
	//alinas code
	public double[][] getNegotiationPathA(){
		System.out.println("fAgentABids "+sessionrunner.fAgentABids.size());
		double[][] lAgentAUtilities = new double[2][sessionrunner.fAgentABids.size()];
		try
        {
			int i=0;
	    	for (BidPoint p:sessionrunner.fAgentABids)
	    	{
	        	lAgentAUtilities [0][i] = p.utilityA;
	        	lAgentAUtilities [1][i] = p.utilityB;
	        	i++;
	    	}
        } catch (Exception e) {
			e.printStackTrace();
        	return null;
		}
    	
		return lAgentAUtilities; 
	}
	public double[][] getNegotiationPathB(){
		System.out.println("fAgentBBids "+sessionrunner.fAgentBBids.size());
		double[][] lAgentBUtilities = new double [2][sessionrunner.fAgentBBids.size()];  
		try{
			int i=0;
	    	for (BidPoint p:sessionrunner.fAgentBBids)
	    	{
	        	lAgentBUtilities [0][i] = p.utilityA;
	        	lAgentBUtilities [1][i] = p.utilityB;
	        	i++;
	    	}
	 	} catch (Exception e) {
		   	e.printStackTrace();
		   	return null;
		}
		return lAgentBUtilities;
	}
	
	protected void loadAgentsUtilitySpaces() throws Exception
	{
		//load the utility space
		fAgentAUtilitySpace = new UtilitySpace(getDomain(), agentAUtilitySpaceFileName);
		System.out.println("utility space statistics for "+"Agent "+agentAUtilitySpaceFileName);
		fAgentAUtilitySpace.showStatistics();
		fAgentBUtilitySpace = new UtilitySpace(getDomain(), agentBUtilitySpaceFileName);
		System.out.println("utility space statistics for "+"Agent "+agentBUtilitySpaceFileName);
		fAgentBUtilitySpace.showStatistics();
		return;

	}
	
	/**
	 * 
	 * @param fileName
	 * @param mf points to the MainFrame GUI that currently also holds the application data (...)
	 * @throws Exception if there are problems reading the file.
	 */
	/*public static void loadParamsFromFile (String fileName, MainFrame mf) throws Exception
	{
		SimpleDOMParser parser = new SimpleDOMParser();
		try {
			BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));		
			SimpleElement root = parser.parse(file);

            mf.setNemberOfSessions(root.getAttribute("number_of_sessions"));
            SimpleElement xml_agentA = (SimpleElement)(root.getChildByTagName("agent")[0]);
            mf.setAgentAName(xml_agentA.getAttribute("name"));
            mf.setAgentAClassName(xml_agentA.getAttribute("class"));
            mf.setAgentAUtilitySpace((new File(fileName)).getParentFile().toString()+"/"+  xml_agentA.getAttribute("utility_space"));
            SimpleElement xml_agentB = (SimpleElement)(root.getChildByTagName("agent")[1]);
            mf.setAgentBName(xml_agentB.getAttribute("name"));
            mf.setAgentBClassName(xml_agentB.getAttribute("class"));
            mf.setAgentBUtilitySpace((new File(fileName)).getParentFile().toString()+"/"+  xml_agentB.getAttribute("utility_space"));
        } catch (Exception e) {
            throw new IOException("Problem loading parameters from "+fileName+": "+e.getMessage());
        }
    }
    */
    
    public Domain getDomain() {
        return domain;
    }
    
    public String getAgentBUtilitySpaceFileName() {
        return agentBUtilitySpaceFileName;
    }
    
    public String getAgentAUtilitySpaceFileName() {
        return agentAUtilitySpaceFileName;
    }
    

	public UtilitySpace getAgentAUtilitySpace() {
		return fAgentAUtilitySpace;
	}

	public UtilitySpace getAgentBUtilitySpace() {
		return fAgentBUtilitySpace;
	}
	
	 
    public SimpleElement domainToXML(){
    	return domain.toXML(); 		
    }

     /**
      * @return total available time for entire nego, in seconds.
      */
    public Integer getTotalTime() { return totalTime; }
    public BidSpace getBidSpace() { return bidSpace; }


	public void setAgentAname(String agentAname) {
		this.agentAname = agentAname;
	}


	public String getAgentAname() {
		return agentAname;
	}


	public void setAgentBname(String agentBname) {
		this.agentBname = agentBname;
	}


	public String getAgentBname() {
		return agentBname;
	}


	public void setAgentAparams(ArrayList<AgentParamValue> agentAparams) {
		this.agentAparams = agentAparams;
	}


	public ArrayList<AgentParamValue> getAgentAparams() {
		return agentAparams;
	}


	public void setAgentBparams(ArrayList<AgentParamValue> agentBparams) {
		this.agentBparams = agentBparams;
	}


	public ArrayList<AgentParamValue> getAgentBparams() {
		return agentBparams;
	}


	public void setProfileArep(ProfileRepItem profileArep) {
		this.profileArep = profileArep;
	}


	public ProfileRepItem getProfileArep() {
		return profileArep;
	}


	public void setProfileBrep(ProfileRepItem profileBrep) {
		this.profileBrep = profileBrep;
	}


	public ProfileRepItem getProfileBrep() {
		return profileBrep;
	}


}
