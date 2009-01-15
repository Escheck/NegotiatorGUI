package negotiator.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;

import negotiator.Domain;
import negotiator.Global;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;

public class NegotiationSession {
	
	private AgentRepItem[] agentRepItems;	
    private ProfileRepItem[] profileRepItems;    
    private String[] agentNames;
    private HashMap<AgentParameterVariable,AgentParamValue>[]  agentParams;
    
    /** -- **/
    protected Domain domain;
    private UtilitySpace[] agentUtilitySpaces;
    

    private SimpleElement fRoot;
	private String fFileName;    

    
    public NegotiationSession(AgentRepItem[] agentRepItems, ProfileRepItem[] profileRepItems, HashMap<AgentParameterVariable,AgentParamValue>[] agentParams) throws Exception{
    	this.agentRepItems = agentRepItems.clone();
    	this.profileRepItems = profileRepItems.clone();
    	this.agentParams = agentParams.clone();    	
    	loadAgentsUtilitySpaces();
    }
	protected void loadAgentsUtilitySpaces() throws Exception
	{
		//load the utility space
		for(int i=0;i<profileRepItems.length;i++) {
			ProfileRepItem profile = profileRepItems[i];
			agentUtilitySpaces[i] = new UtilitySpace(domain, profile.getURL().getFile());
			//System.out.println("utility space statistics for "+"Agent "+agentAUtilitySpaceFileName);
			//fAgentAUtilitySpace.showStatistics();
		}
		return;

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
		if (Global.analysisEnabled && !Global.batchMode)
		{
			if(fRoot.getChildByTagName("analysis").length>0) {
				//fAnalysis = new Analysis(this, (SimpleElement)(fRoot.getChildByTagName("analysis")[0]));
			} else {
				//propose to build an analysis
/*				Object[] options = {"Yes","No"};                  
				int n = JOptionPane.showOptionDialog(null,
						"You have no analysis available for this template. Do you want build it?",
						"No Analysis",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,
						options,
						options);
				if(n==0) {*/
					//bidSpace=new BidSpace(fAgentAUtilitySpace,fAgentBUtilitySpace);
					//fAnalysis = Analysis.getInstance(this);
					//  save the analysis to the cache
					//fAnalysis.saveToCache();
				//}
				
			}//if
		}
		//if(fAnalysis!=null) showAnalysis();	
		//if (bidSpace!=null) showAnalysis();
	}

    void check() throws Exception {
    	//if (!(getProfileArep().getDomain().equals(getProfileBrep().getDomain())))
    		//throw new IllegalArgumentException("profiles "+getProfileArep()+" and "+getProfileBrep()+" have a different domain.");
    }
    
	public AgentRepItem getAgentRepItem(int index) {
		return agentRepItems[index];
	}
    public ProfileRepItem getProfileRepItems(int index) {
    	return profileRepItems[index];
    }
    public String getAgentName(int index) {
    	return agentNames[index];
    }
    public HashMap<AgentParameterVariable,AgentParamValue> getAgentParams(int index) {
    	return agentParams[index];
    }
    
    public  UtilitySpace getAgentUtilitySpaces(int index) {
    	return agentUtilitySpaces[index];
    }

}
