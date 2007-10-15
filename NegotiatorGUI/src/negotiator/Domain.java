/*
 * Domain.java
 *
 * Created on November 16, 2006, 12:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import negotiator.issue.*;
import negotiator.xml.SimpleElement;
import negotiator.xml.SimpleDOMParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.ArrayList;
/**
 *
 * @author Dmytro Tykhonov & Koen Hindriks
 * 
 */

public class Domain {
	
	// Class fields
    //private int fNumberOfIssues; // idem: removed.
    //private Issue fIssues[]; Wouter: What's this?? Dmytro: this is OLD. delete please.
    private Objective fObjectivesRoot;
    
    // Constructor
    public Domain(){
    	fObjectivesRoot = null;
    	//fNumberOfIssues=0;
    }
    
    public Domain(SimpleElement root) {
    	loadTreeFromXML(root);
    }
    
    public Domain(String filename) {
    	this(new File(filename));
    }
    
    public Domain(File filename){
    	SimpleDOMParser parser = new SimpleDOMParser();
    	try{
    		BufferedReader file = new BufferedReader(new FileReader(filename));                  
    		SimpleElement root = parser.parse(file);
    		SimpleElement xml_utility_space = (SimpleElement)(root.getChildByTagName("utility_space")[0]);
    		loadTreeFromXML(xml_utility_space);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    // Class methods
    /*
    public final int getNumberOfIssues() {
        return fNumberOfIssues;
    }
    */
    
    public final Objective getIssue(int index) {
        return fObjectivesRoot.getChildAt(index);
    }
    
    public final Objective getObjective(int index){
    	return fObjectivesRoot.getObjective(index); 
    }
    
    public final Objective getObjectivesRoot(){
    	return fObjectivesRoot; //TODO hdevos this could be done in a more elegant way. To discuss with Richard.
    }   

    /**
     * Sets a new domain root.
     * @param ob The new root Objective
     */
    public final void setObjectivesRoot(Objective ob){
    	fObjectivesRoot = ob;
    }
    
    //added by Herbert
    /**
     * 
     * @param pRoot The SimpleElement that contains the root of the Objective tree.
     */
    private final void loadTreeFromXML(SimpleElement pRoot){
    	//SimpleElement root contains a LinkedList with SimpleElements.
    	/*
    	 * Structure of the file:
    	 * 
    	 * pRoot contains information about how many items there exist in the utilityspace.
    	 * The first SimpleElement under pRoot contains the root objective of the tree, with a number of objective
    	 * as tagnames.
    	 * 
    	 * 
    	 */    	
    	
    	//Get the number of issues:
 
//causes error. the []s seem to cause a classcastexception.    	SimpleElement[] root = (SimpleElement[])(pRoot.getChildByTagName("objective")); //Get the actual root Objective.
    	SimpleElement root = (SimpleElement)(pRoot.getChildByTagName("objective")[0]); //Get the actual root Objective. 
    	int rootIndex = Integer.valueOf(root.getAttribute("index"));
        Objective objAlmostRoot = new Objective();
        objAlmostRoot.setNumber(rootIndex);
        String name = root.getAttribute("name");
        if(name != null)
        	objAlmostRoot.setName(name);
        else
        	objAlmostRoot.setName("root"); //just in case.
        //set objAlmostRoot attributes based on pRoot
        
        fObjectivesRoot = buildTreeRecursive(root, objAlmostRoot);
    	
        
    } 
    //added by Herbert
    /**
     * 
     * @param currentLevelRoot The current SimpleElement containing the information for the Objective on this level.
     * @param currentParent parent of the current level of this branch of the tree.
     * @return The current parent of this level of the tree, with the children attached.
     */
    
    private final Objective buildTreeRecursive(SimpleElement currentLevelRoot, Objective currentParent){
 /*   	String s = currentLevelRoot.getAttribute("number_of_children");
    	Integer sint = new Integer(s);
    	int sintint = sint; //Check of this cast works!
 */   	
    	Object[] currentLevelObjectives = currentLevelRoot.getChildByTagName("objective");
    	Object[] currentLevelIssues = currentLevelRoot.getChildByTagName("issue");
    	for(int i =0; i < currentLevelObjectives.length; i++){
       			SimpleElement childObjectives = (SimpleElement)currentLevelObjectives[i];
       			int obj_index = Integer.valueOf(childObjectives.getAttribute("index"));
    			Objective child = new Objective(currentParent);
    			child.setNumber(obj_index);
    			//Set child attributes based on childObjectives.
    			child.setName(childObjectives.getAttribute("name"));
   // 			child.setDescription(childObjectives.getAttribute("description"));
   /* 			Double weight = new Double(childObjectives.getAttribute("weight")); //TODO check if weigth is the same things as value!
    			child.setWeight(weight.doubleValue()); 
    */			
    			currentParent.addChild(buildTreeRecursive(childObjectives, child));
   
    		
    	}
    	
    	for(int j = 0; j < currentLevelIssues.length; j++){
    		Issue child = null;
    		
    		SimpleElement childIssues = (SimpleElement)currentLevelIssues[j];
    		//check type of issue
    		String name = childIssues.getAttribute("name");
    		int index = Integer.parseInt(childIssues.getAttribute("index"));
    		
//    		 Collect issue value type from XML file.
            String type = childIssues.getAttribute("type");
            String vtype = childIssues.getAttribute("vtype");
            ISSUETYPE issueType;
            if (type.equals(vtype)) {
            	if (type==null) { // No value type specified.
            		System.out.println("Type not specified in template file.");
                	issueType = ISSUETYPE.DISCRETE;
            	} else { // Both "type" as well as "vtype" attribute, but consistent.
            		issueType = ISSUETYPE.convertToType(type);
            	}
            } else if (vtype!=null && type==null) {
            	issueType = ISSUETYPE.convertToType(vtype);
            } else if (type!=null && vtype==null) { // Used label "type" instead of label "vtype".
            	issueType = ISSUETYPE.convertToType(type);
            } else {
            	System.out.println("Conflicting value types specified for issue in template file.");
            	// TODO: Define exception.
            	// For now: use "type" label.
            	issueType = ISSUETYPE.convertToType(type);
            }
            
            
//          Collect values and/or corresponding parameters for issue type.
            Object[] xml_items;
            Object[] xml_item;
            int nrOfItems, minI, maxI, item_index;
            double minR, maxR;
            String[] values;
            Issue issue;
            switch(issueType) {
            case DISCRETE:
            	// Collect discrete values for discrete-valued issue from xml template
            	
            	xml_items = childIssues.getChildByTagName("item");
                nrOfItems = xml_items.length;
                
                values = new String[nrOfItems];
                for(int k=0;k<nrOfItems;k++) {
                	// TODO: check range of indexes.
                    item_index = Integer.valueOf(((SimpleElement)xml_items[k]).getAttribute("index"));
                    values[k] = ((SimpleElement)xml_items[k]).getAttribute("value");
                }
                child = new IssueDiscrete(name, index, values, currentParent);
            	break;
            case INTEGER:
            	// Collect range bounds for integer-valued issue from xml template
            	xml_item = childIssues.getChildByTagName("range");
            	minI = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
            	maxI = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("upperbound"));
            	child = new IssueInteger(name, index, minI, maxI, currentParent);
            	break;
            case REAL:
            	// Collect range bounds for integer-valued issue from xml template
            	xml_item = childIssues.getChildByTagName("range");
            	minR = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
            	maxR = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("upperbound"));
            	child = new IssueReal(name, index, minR, maxR);
            	break;
 // Issue values cannot be of type "price" anymore... TODO: Remove when everything works.
 //           case PRICE:
 //           	// Collect range bounds for integer-valued issue from xml template
 //           	xml_item = childIssues.getChildByTagName("range");
 //           	minR = Integer.valueOf(((SimpleElement)xml_item).getAttribute("lowerbound"));
 //           	maxR = Integer.valueOf(((SimpleElement)xml_item).getAttribute("upperbound"));
 //           	issue = new IssuePrice(name, index, minR, maxR);
 //           	break;
            default: // By default, create discrete-valued issue
            	// Collect discrete values for discrete-valued issue from xml template
            	xml_items = childIssues.getChildByTagName("item");
                nrOfItems = xml_items.length;
                values = new String[nrOfItems];
                for(int k=0;k<nrOfItems;k++) {
                    item_index = Integer.valueOf(((SimpleElement)xml_items[j]).getAttribute("index"));
                    values[k] = ((SimpleElement)xml_items[j]).getAttribute("value");
                }
            	child = new IssueDiscrete(name, index, values, currentParent);
            	break;
            }
    		    		
 //Descriptions?   		child.setDescription(childIssues.getAttribute("description"));
    /*		Double weight = new Double(childIssues.getAttribute("weight"));
    		child.setWeight(weight.doubleValue());
    */		child.setNumber(index);
            try{
            	currentParent.addChild(child);
            }catch(Exception e){
            	System.out.println("child is NULL");
            	e.printStackTrace();
            	
            }
       	}
    	
    	return currentParent;
    }
    
	// KH 070511: Moved to here since it is generic method that can be made available to all agents.
	public final Bid getRandomBid() {
       //Value[] values = new Value[this.getNumberOfIssues()];
		HashMap<Integer, Value> values = new HashMap<Integer, Value>();
		
       Objective lIssue;
       int lNrOfOptions, lOptionIndex;

       // For each issue, compute a random value to return in bid.
       for(int i=0;i<this.getNumberOfIssues();i++) {
	        lIssue = this.getIssue(i);        	
			switch(lIssue.getType()) {
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
	            lNrOfOptions =lIssueDiscrete.getNumberOfValues();
	            lOptionIndex = Double.valueOf(java.lang.Math.random()*(lNrOfOptions)).intValue();
	            if (lOptionIndex >= lNrOfOptions)
	            	lOptionIndex= lNrOfOptions-1;
				//values[i]= lIssueDiscrete.getValue(lOptionIndex);
	            values.put(new Integer(i), lIssueDiscrete.getValue(lOptionIndex));
				break;
			case INTEGER:
		        lNrOfOptions = ((IssueInteger)lIssue).getUpperBound()-((IssueInteger)lIssue).getLowerBound()+1;
		        lOptionIndex = Double.valueOf(java.lang.Math.random()*(lNrOfOptions)).intValue();
	            if (lOptionIndex >= lNrOfOptions)
	            	lOptionIndex= lNrOfOptions-1;
	            //values[i]= new ValueInteger(((IssueInteger)lIssue).getLowerBound()+lOptionIndex);
	            values.put(new Integer(i), new ValueInteger(((IssueInteger)lIssue).getLowerBound()+lOptionIndex));
		        break;
			case REAL:
				IssueReal lIssueReal =(IssueReal)lIssue;
				lNrOfOptions =lIssueReal.getNumberOfDiscretizationSteps();
				double lOneStep = (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())/lNrOfOptions;
	            lOptionIndex = Double.valueOf(java.lang.Math.random()*(lNrOfOptions)).intValue();
	            if (lOptionIndex >= lNrOfOptions)
	            	lOptionIndex= lNrOfOptions-1;
				//values[i]= new ValueReal(lIssueReal.getLowerBound()+lOneStep*lOptionIndex);
	            values.put(new Integer(i), new ValueReal(lIssueReal.getLowerBound()+lOneStep*lOptionIndex));
				break;
			}
		}
        return new Bid(this,values);
	}
	
	/**
	 * Creates an XML representation of this domain.
	 * @return the SimpleElements representation of this Domain or <code>null</code> when there was an error.
	 */
	public SimpleElement toXML(){
		SimpleElement root = new SimpleElement("utility_space");
		//set attributes for this domain
		root.setAttribute("number_of_issues", ""+0); //unknown right now
		root.addChildElement(fObjectivesRoot.toXML());
		return root;
	}
	
	/**
	 * get all issues as an arraylist.
	 * @author W.Pasman
	 * @return arraylist of all issues in the domain.
	 */
	public ArrayList<Issue> getIssues()
	{
		Enumeration<Objective> issues=fObjectivesRoot.getPreorderIssueEnumeration();
		ArrayList<Issue> issuelist=new ArrayList<Issue>();
		while (issues.hasMoreElements()) issuelist.add((Issue)issues.nextElement());
		return issuelist;
	}
	
}
