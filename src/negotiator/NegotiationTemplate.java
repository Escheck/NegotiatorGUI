/*
 * NegotiationTemplate.java
 *
 * Created on November 16, 2006, 11:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import negotiator.analysis.Analysis;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.gui.MainFrame;
import negotiator.gui.chart.Chart;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;

/**
 *
 * @author Dmytro Tykhonov
 * 
 * Wouter: this field holds the data needed for a negotiatin.
 * I'm still not sure whether totalTime should be here,
 * because the exact purpose of this template was not described.
 */
public class NegotiationTemplateOLD
{
/*    public static final int FAIR_DEVISION_PROBLEM = 1;
    public static final int CONVENTIONAL_NEGOTIATION = 2;
    private int negotiationType;*/
    private Domain domain;
    private String agentAUtilitySpaceFileName;
    private String agentBUtilitySpaceFileName;
    private UtilitySpace fAgentAUtilitySpace;
    private UtilitySpace fAgentBUtilitySpace;
    private SimpleElement fRoot;
	private String fFileName;    
    //private Analysis fAnalysis;
	private BidSpace bidSpace=null;
    private int totalTime; // total available time for nego, in seconds.

    /** Creates a new instance of NegotiationTemplate 
     * @param negotiationType
     * @param domain
     * @param agentAUtilitySpaceFileName
     * @param agentBUtilitySpaceFileName
     */
    
    /*
	public NegotiationTemplate(int negotiationType, Domain domain, Integer totTime,
			String agentAUtilitySpaceFileName, String agentBUtilitySpaceFileName) {
		this.agentAUtilitySpaceFileName = agentAUtilitySpaceFileName;
		this.agentBUtilitySpaceFileName = agentBUtilitySpaceFileName;               
//		this.negotiationType = negotiationType;
		this.domain = domain;
		totalTime=totTime;
	}
	*/
	
    /**
     * @param fileName I think this is the domain file name.
     */
	public NegotiationTemplate(String fileName, 
			String agentAUtilitySpaceFileName, String agentBUtilitySpaceFileName,
			Integer totTime) 
	throws Exception
	{
		fFileName = fileName;
		this.agentAUtilitySpaceFileName = agentAUtilitySpaceFileName;
		this.agentBUtilitySpaceFileName = agentBUtilitySpaceFileName;      	
		loadFromFile(fileName);
		totalTime=totTime;
	}
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
				int n = JOptionPane.showOptionDialog(Main.mf,
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
		lChart.show();
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
	public static void loadParamsFromFile (String fileName, MainFrame mf) throws Exception
	{
		SimpleDOMParser parser = new SimpleDOMParser();
		try {
			BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));		
			SimpleElement root = parser.parse(file);
/*            if (root.getAttribute("negotiation_type").equals("FDP"))this.negotiationType = FAIR_DEVISION_PROBLEM;
            else this.negotiationType = CONVENTIONAL_NEGOTIATION;*/
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

}
