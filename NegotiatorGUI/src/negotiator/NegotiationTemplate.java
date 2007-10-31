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
public class NegotiationTemplate {
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
    private Analysis fAnalysis;
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
	
	public NegotiationTemplate(String fileName, String agentAUtilitySpaceFileName, String agentBUtilitySpaceFileName,
			Integer totTime) 
	{
		fFileName = fileName;
		this.agentAUtilitySpaceFileName = agentAUtilitySpaceFileName;
		this.agentBUtilitySpaceFileName = agentBUtilitySpaceFileName;      	
		loadFromFile(fileName);
		totalTime=totTime;
	}
	/**
	 * @param fileName
	 */
	private void loadFromFile(String fileName) {
		SimpleDOMParser parser = new SimpleDOMParser();
		try {
			BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));                  
			fRoot = parser.parse(file);
			/*            if (root.getAttribute("negotiation_type").equals("FDP"))this.negotiationType = FAIR_DEVISION_PROBLEM;
            else thisnegotiationType = CONVENTIONAL_NEGOTIATION;*/
			SimpleElement xml_utility_space = (SimpleElement)(fRoot.getChildByTagName("utility_space")[0]);
			domain = new Domain(xml_utility_space);
			loadAgentsUtilitySpaces();			
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
	
					fAnalysis = Analysis.getInstance(this);
					//fRoot.addChildElement(fAnalysis.getXMLRoot());
					//  save the analysis to the cache
					fAnalysis.saveToCache();
				}
				
			}//if
			if(fAnalysis!=null) showAnalysis();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * Show the analysis window. Couples the analysis object with the chart window 
	 * 
	 * @throws Exception
	 */
	protected void showAnalysis() throws Exception
	{
		Chart lChart = new Chart();		
/*		if((!fAnalysis.isCompleteSpaceBuilt())&&fAnalysis.getTotalNumberOfBids()<100000) 
			fAnalysis.buildCompleteOutcomeSpace();*/
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
	protected void loadAgentsUtilitySpaces() {
		//load the utility space
		fAgentAUtilitySpace = new UtilitySpace(getDomain(), agentAUtilitySpaceFileName);
		fAgentBUtilitySpace = new UtilitySpace(getDomain(), agentBUtilitySpaceFileName);
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
    public Analysis getAnalysis() {
    	return fAnalysis;
    }

}
