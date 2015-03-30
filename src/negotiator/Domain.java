package negotiator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;

import negotiator.issue.ISSUETYPE;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Objective;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;

/**
 * Representation of the outcome space of a scenario.
 *
 * @author Dmytro Tykhonov & Koen Hindriks
 */
public class Domain implements Serializable 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8729366996052137300L;
	private Objective fObjectivesRoot;
    private String name;
    private SimpleElement root;
    
    /**
     * Creates an empty domain.
     */
    public Domain()
    {
    	fObjectivesRoot = null;
    	name="";
    }
    
    /**
     * @return XML-representation of this domain.
     */
    public SimpleElement getXMLRoot() 
    {
    	return root;
    }
    
    /**
     * Creates a domain given an XML-representation of the domain.
     * @param root XML-representation of the domain.
     */
    public Domain(SimpleElement root)
    {
    	this.root = root;
    	loadTreeFromXML(root);
    }
    
    /**
     * Creates a domain given the path to a file with an XML-representation.
     * @param filename
     * @throws Exception
     */
    public Domain(String filename) throws Exception
    {    	
    	this(new File(filename));
    	name = filename;
    }
    
    /**
     * read a domain from a file.
     * @param filename
     * @throws Exception if 
     */
    public Domain(File filename) throws Exception
    {
    	name = filename.getAbsolutePath();
    	SimpleDOMParser parser = new SimpleDOMParser();
//    		System.out.println("Opening: " + filename.getAbsolutePath());
    		BufferedReader file = new BufferedReader(new FileReader(filename));                  
    		root = parser.parse(file);
    		
    		SimpleElement xml_utility_space;
    		try { 
    			xml_utility_space = 
    				(SimpleElement)(root.getChildByTagName("utility_space")[0]); 
    		} 
    		catch (Exception err) 
    		{ throw new Exception("Can't read from "+filename+", incorrect format of file"); }
    		loadTreeFromXML(xml_utility_space);
    }
    
    /**
     * Returns an issue with a given index. Considers issues in the domain tree as a plain array (uses getIssues method to generate the array).
     * 
     * @param index of the issue.
     * @return issue with the given index.
     */
    public final Objective getIssue(int index) {
    	return getIssues().get(index);
    }
    
   
     /**
      * @param ID (number) of the objective
      * @return the objective with given ID
      */
    public final Objective getObjective(int ID){
    	return fObjectivesRoot.getObjective(ID); 
    }
    
    /**
     * @return the highest level objctive.
     */
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
    
    /**
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
        	if (type==null) { // No value type specified.
//        		new Warning("Type not specified in template file of " + name + " at " + index);
            	issueType = ISSUETYPE.DISCRETE;
        	}
        	else if (type.equals(vtype)) {
            	// Both "type" as well as "vtype" attribute, but consistent.
            		issueType = ISSUETYPE.convertToType(type);
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
            int nrOfItems, minI, maxI;
            double minR, maxR;
            String[] values;
            String[] desc;
            switch(issueType) {
            case DISCRETE:
            	// Collect discrete values for discrete-valued issue from xml template            	
            	xml_items = childIssues.getChildByTagName("item");
                nrOfItems = xml_items.length;
                
                values = new String[nrOfItems];
                desc = new String[nrOfItems];
                for(int k=0;k<nrOfItems;k++) {
                	// TODO: check range of indexes.
                    values[k] = ((SimpleElement)xml_items[k]).getAttribute("value");
                    desc[k]=((SimpleElement)xml_items[k]).getAttribute("description");
                }
                child = new IssueDiscrete(name, index, values, desc,currentParent);
            	break;
            case INTEGER:
            	// Collect range bounds for integer-valued issue from xml template
            	xml_item = childIssues.getChildByTagName("range");
            	minI = Integer.valueOf(childIssues.getAttribute("lowerbound"));
            	maxI = Integer.valueOf(childIssues.getAttribute("upperbound"));
            	child = new IssueInteger(name, index, minI, maxI, currentParent);
            	break;
            case REAL:
            	// Collect range bounds for integer-valued issue from xml template
            	xml_item = childIssues.getChildByTagName("range");
            	minR = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
            	maxR = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("upperbound"));
            	child = new IssueReal(name, index, minR, maxR);
            	break;
            default: // By default, createFrom discrete-valued issue
            	// Collect discrete values for discrete-valued issue from xml template
            	xml_items = childIssues.getChildByTagName("item");
                nrOfItems = xml_items.length;
                values = new String[nrOfItems];
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
    
	/** KH 070511: Moved to here since it is generic method that can be made available to all agents.
	 * Wouter: NOTE, it is NOT checked whether the bid has a utility>0.
	 * @return a random bid
	 */
    public final Bid getRandomBid() {
    	return getRandomBid(new Random());
    }
    
	/** KH 070511: Moved to here since it is generic method that can be made available to all agents.
	 * Wouter: NOTE, it is NOT checked whether the bid has a utility>0.
	 * @param r random variable
	 * @return a random bid
	 */
	public final Bid getRandomBid(Random r)
	{
       //Value[] values = new Value[this.getNumberOfIssues()];
		HashMap<Integer, Value> values = new HashMap<Integer, Value>();
		
       int lNrOfOptions, lOptionIndex;
       
       // For each issue, compute a random value to return in bid.
       for (Issue lIssue: getIssues()) {
			switch(lIssue.getType()) {
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
	            lNrOfOptions =lIssueDiscrete.getNumberOfValues();
	            lOptionIndex = Double.valueOf(r.nextDouble()*(lNrOfOptions)).intValue();
	            if (lOptionIndex >= lNrOfOptions)
	            	lOptionIndex= lNrOfOptions-1;
				//values[i]= lIssueDiscrete.getValue(lOptionIndex);
	            values.put(lIssue.getNumber(), lIssueDiscrete.getValue(lOptionIndex));
				break;
			case INTEGER:
		        lNrOfOptions = ((IssueInteger)lIssue).getUpperBound()-((IssueInteger)lIssue).getLowerBound()+1;
		        lOptionIndex = Double.valueOf(r.nextDouble()*(lNrOfOptions)).intValue();
	            if (lOptionIndex >= lNrOfOptions)
	            	lOptionIndex= lNrOfOptions-1;
	            //values[i]= new ValueInteger(((IssueInteger)lIssue).getLowerBound()+lOptionIndex);
	            values.put(lIssue.getNumber(), new ValueInteger(((IssueInteger)lIssue).getLowerBound()+lOptionIndex));
		        break;
			case REAL:
				IssueReal lIssueReal =(IssueReal)lIssue;
				lNrOfOptions =lIssueReal.getNumberOfDiscretizationSteps();
				double lOneStep = (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())/lNrOfOptions;
	            lOptionIndex = Double.valueOf(r.nextDouble()*(lNrOfOptions)).intValue();
	            if (lOptionIndex >= lNrOfOptions)
	            	lOptionIndex= lNrOfOptions-1;
				//values[i]= new ValueReal(lIssueReal.getLowerBound()+lOneStep*lOptionIndex);
	            values.put(lIssue.getNumber(), new ValueReal(lIssueReal.getLowerBound()+lOneStep*lOptionIndex));
				break;
			}
		}
       try { 
        return new Bid(this,values);
       }
       catch (Exception e) { System.out.println("problem getrandombid:"+e.getMessage()) ; }
       return null;
	}
	
	/**
	 * Creates an XML representation of this domain.
	 * @return the SimpleElements representation of this Domain or <code>null</code> when there was an error.
	 */
	public SimpleElement toXML(){
		SimpleElement root = new SimpleElement("negotiation_template");
		SimpleElement utilRoot = new SimpleElement("utility_space");
		//set attributes for this domain
		utilRoot.setAttribute("number_of_issues", "" + fObjectivesRoot.getChildCount());
		utilRoot.addChildElement(fObjectivesRoot.toXML());
		root.addChildElement(utilRoot);
		return root;
	}
	
	/**
	 * @return all objectives (note, issues are also objectives!) in the domain
	 */
	public ArrayList<Objective> getObjectives()
	{
		Enumeration<Objective> objectives=fObjectivesRoot.getPreorderEnumeration();
		ArrayList<Objective> objectivelist=new ArrayList<Objective>();
		while (objectives.hasMoreElements()) objectivelist.add(objectives.nextElement());
		return objectivelist;
	}
	
	/**
	 * Returns all issues as an arraylist.
	 * Note that it is wise cache the issues, as this implementation is quite
	 * computationally expensive.
	 * 
	 * @return arraylist of all issues in the domain.
	 */
	public ArrayList<Issue> getIssues()
	{
		Enumeration<Objective> issues=fObjectivesRoot.getPreorderIssueEnumeration();
		ArrayList<Issue> issuelist=new ArrayList<Issue>();
		while (issues.hasMoreElements()) issuelist.add((Issue)issues.nextElement());
		return issuelist;
	}
	
	/**
	 * get number of all possible bids. Does not care of constraints. 
	 * 
	 * Not finished!!!
	 * 
	 * @return long number of all possible bids in the domain. 
	 */
	public long getNumberOfPossibleBids() {
		long lNumberOfPossibleBids = (long)1;
		ArrayList<Issue> lIssues = getIssues();
		for(Issue lIssue : lIssues) {
			switch(lIssue.getType()) {
			case DISCRETE:
				lNumberOfPossibleBids = lNumberOfPossibleBids * ((IssueDiscrete)lIssue).getNumberOfValues();
				break;
			case REAL:
				lNumberOfPossibleBids = lNumberOfPossibleBids * ((IssueReal)lIssue).getNumberOfDiscretizationSteps();
				break;
				//TODO: Finish getNumberOfPossibleBids() for Integer, Real issues
			}
		}
		return lNumberOfPossibleBids;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fObjectivesRoot == null) ? 0 : fObjectivesRoot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Domain other = (Domain) obj;
		if (fObjectivesRoot == null) {
			if (other.fObjectivesRoot != null)
				return false;
		} else if (!fObjectivesRoot.equals(other.fObjectivesRoot))
			return false;
		return true;
	}

	/**
	 * @return name of the given domain.
	 */
	public String getName() {
		return name;
	}
}
