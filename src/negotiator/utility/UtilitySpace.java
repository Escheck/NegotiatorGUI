/*
 * UtilitySpace.java
 *
 * Created on November 6, 2006, 10:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.utility;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map;
import java.util.Map.Entry;

import negotiator.utility.EVALFUNCTYPE;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.issue.*;
import negotiator.xml.*;
import negotiator.exceptions.Warning;
import negotiator.BidIterator;

/**
 *
 * 
 * Wouter: the utility space couples all objectives to weights and evaluators.
 * A utilityspace currently is not bound to one agent.
 * I can see some security issues with that...
 * 
 * Wouter: this class is final to prevent users (students) to override the getUtility function
 * with their own version of UtilitySpace
 * 
 * Wouter 15nov: un-done the final, students may hack what they want, but they work with a copy anyway.
 * 
 * @author Dmytro Tykhonov & Koen Hindriks 
 */

public class UtilitySpace {
	
	public enum CHECK_CONSTRAINTS {DO_CHECK, DO_NOT_CHECK};
	public static CHECK_CONSTRAINTS fCheckConstraints = CHECK_CONSTRAINTS.DO_CHECK;
	// Class fields
    protected Domain domain;
    //Added by Dmytro: I need the XMLRoot for the utility space to load the Similarity functions
    // in the Similarity agent
    private SimpleElement fXMLRoot;
    public SimpleElement getXMLRoot() { return fXMLRoot;}
    private Double fReservationValue = null;
    
    private double discountFactor = 0;
    private Map<Objective, Evaluator> fEvaluators; //changed to use Objective. TODO check casts.
    private String fileName;

    
    
    /**
     * Creates an empty utility space.
     */
    public UtilitySpace(){
    	this.domain = new Domain();
    	fEvaluators = new HashMap<Objective, Evaluator>();
    }
    
    /**
     * Create new default util space for a given domain.
     * @param domain
     * @param fileName to read domain from. 
     * Set fileName to "" if no file available, in which case default evaluators are loaded..
     * @throws if error occurs, e.g. if domain does not match the util space, or file not found.
     */
    public UtilitySpace(Domain domain, String fileName) throws Exception
    {
        this.domain = domain;
        this.fileName = fileName;
    	fEvaluators = new HashMap<Objective, Evaluator>();
        if(!fileName.equals(""))
        	loadTreeFromFile(fileName);
        else
        { // add evaluator to all objectives
        	ArrayList<Objective> objectives=domain.getObjectives();        	
        	for (Objective obj:objectives) {
        		Evaluator eval =  DefaultEvaluator(obj);
        		fEvaluators.put(obj, eval);
        		if(eval instanceof EvaluatorDiscrete) {
        			EvaluatorDiscrete evalDisc = (EvaluatorDiscrete)eval;
        			IssueDiscrete issue = (IssueDiscrete)obj;        			

        			for(Value val: issue.getValues()) {
        				ValueDiscrete valDisc = (ValueDiscrete)val;
        				evalDisc.setCost(valDisc, issue.getCost(valDisc));
        			}
        		}
        	}
        	
        }
    }
    
    /** @return a clone of another utility space */
    public UtilitySpace(UtilitySpace us)
    {
    	domain=us.getDomain();
    	fileName = us.getFileName();
    	fEvaluators = new HashMap<Objective, Evaluator>();
    	fReservationValue = us.getReservationValue(); 
    	// and clone the evaluators
    	for (Objective obj:domain.getObjectives())
    	{
    		Evaluator e=us.getEvaluator(obj.getNumber());
    		if (e!=null) fEvaluators.put(obj, e.clone());
    		// else incomplete. But that is allowed I think.
    		// especially, objectives (the non-Issues) won't generally have an evlauator.
    	}
    	fXMLRoot = us.getXMLRoot();
    }
    
    /**
     * create a default evaluator for a given Objective.
     * This function is placed here, and not in Objective, because
     * the Objectives should not be loaded with utility space functionality.
     * The price we pay for that is that we now have an ugly switch inside the code,
     * losing some modularity.
     * @param obj the objective to create an evaluator for
     * @return the defualt evaluator
     * @author W.Pasman
     */
    public Evaluator DefaultEvaluator(Objective obj)
    {
    	if (obj.isObjective()) return new EvaluatorObjective();
    	//if not an objective then it must be an issue.
    	switch (((Issue)obj).getType())
    	{
    	case DISCRETE: return new EvaluatorDiscrete();
    	case INTEGER: return new EvaluatorInteger();
    	case REAL: return new EvaluatorReal();
    	default: System.out.println("INTERNAL ERROR: issue of type "+((Issue)obj).getType()+
    			"has no default evaluator");
    	}
    	return null;
    }

        
    
    /**
     * Checks the normalization throughout the tree. Will eventually replace checkNormalization 
     * @return true if the weigths are indeed normalized, false if they aren't. 
     */
    private boolean checkTreeNormalization(){
    	return checkTreeNormalizationRecursive(domain.getObjectivesRoot());
    }
    protected void normalizeWeights(Objective currentRoot) {
    	double lSum = 0;
    	
    	Enumeration<Objective> children = currentRoot.children();
    	
    	// Wouter: there is nothing recursive here. This function seems broken
    	while(children.hasMoreElements()){
    		
    		Objective tmpObj = children.nextElement();
    		lSum += (fEvaluators.get(tmpObj)).getWeight();
    		
    	}
    	children = currentRoot.children();
    	
    	// Wouter: there is nothing recursive here. This function seems broken
    	while(children.hasMoreElements()){
    		
    		Objective tmpObj = children.nextElement();
    		double weight = (1-lSum)*(fEvaluators.get(tmpObj)).getWeight();
    		(fEvaluators.get(tmpObj)).setWeight(weight);
    		
    	}
    }
    /**
     * Private helper function to check the normalisation throughout the tree.
     * @param currentRoot The current parent node of the subtree we are going to check
     * @return True if the weights are indeed normalized, false if they aren't.
     */
    private boolean checkTreeNormalizationRecursive(Objective currentRoot ){
    	boolean normalised = true;
    	double lSum = 0;
    	
    	Enumeration<Objective> children = currentRoot.children();
    	
    	// Wouter: there is nothing recursive here. This function seems broken
    	while(children.hasMoreElements() && normalised){
    		
    		Objective tmpObj = children.nextElement();
    		lSum += (fEvaluators.get(tmpObj)).getWeight();
    		
    	}
    	//System.out.println("sum="+lSum);
    	return (normalised && lSum>.98 && lSum<1.02);
    }
    
    /**
     * @author W.Pasman
     * check if this utility space is ready for negotiation.
     * @param d is the domain in which nego is taking place
     * throws if problem occurs.
     */
    public void checkReadyForNegotiation(String agentName, Domain dom) throws Exception
    {
        // check if utility spaces are instance of the domain
        // following checks normally succeed, as the domain of the domain space is enforced in the loader.
        if (!(dom.equals(domain)))
        	throw new Exception("domain of agent "+agentName+"does not match the negotiation domain");
        String err=IsComplete();
        if (err!=null) throw new Exception("utility space '"+ fileName +"' of agent "+agentName+" is incomplete\n"+err);
        
        // TODO 
         if (!checkTreeNormalization()) {
        	 
        	 //throw new Exception("utility space of agent "+agentName+" is not normalized \n(the issue weights do not sum to 1)");
         }
         
    }
    
    /**Wouter: I think this should not be used anymore*/
    public final int getNrOfEvaluators() {
    	return fEvaluators.size();
    }
    
    /**
     * @param index The IDnumber of the Objective or Issue
     * @return An Evaluator for the Objective or Issue.
     */
    public final Evaluator getEvaluator(int index) {
 /*   	Issue issue = domain.getIssue(index);
    	return fEvaluators.get(issue);
 */   	
    	Objective obj = domain.getObjective(index); //Used to be Issue in stead of Objective
    	if(obj != null){
    		return fEvaluators.get(obj);
    	}else return null;
    }

// Utility space should not return domain-related information, should it?
//    public final int getNumberOfIssues() {
//        return domain.getNumberOfIssues();
//    }
    
    /**
     * update 23oct. If a hard constraint is violated, the utility should be 0.
     * @param bid=???? seems 
     */
    public double getUtility(Bid bid) throws Exception
    {
    	EVALUATORTYPE type;
        double utility = 0, financialUtility = 0, financialRat = 0;
        if(fCheckConstraints == CHECK_CONSTRAINTS.DO_CHECK)
        	if (constraintsViolated(bid)) return 0.;
        
        Objective root = domain.getObjectivesRoot();
        Enumeration<Objective> issueEnum = root.getPreorderIssueEnumeration();
        while(issueEnum.hasMoreElements()){
        	Objective is = issueEnum.nextElement();
        	Evaluator eval = fEvaluators.get(is);
        	type = eval.getType();
        	switch(type) {
        	case DISCRETE:
        	case INTEGER:
        	case REAL:
        		utility += eval.getWeight()*getEvaluation(is.getNumber(),bid);
        		break;
        	case PRICE:
        		financialUtility = getEvaluation(is.getNumber(),bid);
        		financialRat = ((EvaluatorPrice)eval).rationalityfactor;
        		break;
        	}
        }
        return financialRat*financialUtility+(1-financialRat)*utility;
    }
    
    /**
     * @param bid
     * @param timeAfterStart
     * @param deadline
     * @return
     * @throws Exception
     */
    public double getUtilityWithDiscount(Bid bid, long timeAfterStart, long deadline) throws Exception
    {
    	long timeAfteStartNormalized = 0; 
    	if(timeAfterStart>deadline) {
    		timeAfteStartNormalized =  (long) 1.;
    	} else {
    		timeAfteStartNormalized = timeAfterStart/deadline;
    	}
    	double utility = getUtility(bid) * Math.exp(- discountFactor * timeAfteStartNormalized);
    	return utility;
    }
    

    
    /**
     * @author W.Pasman
     * CHeck that the constraints are not violated.
     * This is an ad-hoc solution, we need structural support 
     * for constraints. Soft, hard constraints, a constraint space etc.
     * @param bid the bid to be checked
     * @return true if the bid violates constraint, else false.
     */
    public boolean constraintsViolated(Bid bid)
    {
    	Double cost=0.;
    	try { cost=getCost(bid); } catch (Exception e) 
    	{ 
    		System.out.println("can not compute cost:"+e.getMessage()+"- assuming constraints violated");
    		return true; 
    	}
    	return cost>1200.;
    }
    
    /**
     * gets the utility of one issue in the bid.
     * @param pIssueIndex
     * @param bid
     * @return
     * @throws Exception
     */
    public final double getEvaluation(int pIssueIndex, Bid bid) throws Exception {
    	ISSUETYPE vtype;
    	Value tmpval = bid.getValue(pIssueIndex);
    	//assert (tmpval!=null); //Wouter: I dont think this does anything.
    	vtype = tmpval.getType();
    	
  /* hdevos: used to be this: 	
   		Issue lIssue = getDomain().getIssue(pIssueIndex);
    	Evaluator lEvaluator = fEvaluators.get(lIssue);
   */

    	Objective lObj = getDomain().getObjective(pIssueIndex);
    	Evaluator lEvaluator = fEvaluators.get(lObj);
    	EVALUATORTYPE etype = lEvaluator.getType();
    	
    	switch(etype) {
    	case DISCRETE:
    		return ((EvaluatorDiscrete)lEvaluator).getEvaluation(this,bid,pIssueIndex);
    	case INTEGER:
    		return ((EvaluatorInteger)lEvaluator).getEvaluation(this,bid,pIssueIndex);
    	case REAL:
    		return ((EvaluatorReal)lEvaluator).getEvaluation(this,bid,pIssueIndex);
    	case PRICE:
    		return ((EvaluatorPrice)lEvaluator).getEvaluation(this,bid,pIssueIndex);
    	case OBJECTIVE: 
    		return ((EvaluatorObjective)lEvaluator).getEvaluation(this,bid,pIssueIndex);
    	default:
    		return -1;
    	
    	}
    
    }
    

    /**
     * Totally revised, brute-force search now.
     * @return a bid with the maximum utility value attainable in this util space
     * @throws Exception if there is no bid at all in this util space.
     * @author W.Pasman
     */
	public final Bid getMaxUtilityBid() throws Exception
	{
		Bid maxBid=null; double maxutil=0.;
		BidIterator bidit=new BidIterator(domain);

		if (bidit.hasNext()) maxBid=bidit.next();
		else throw new Exception("The domain does not contain any bids!");
		while (bidit.hasNext())
		{
			Bid thisBid=bidit.next();
			double thisutil=getUtility(thisBid);
			if (thisutil>maxutil) { maxutil=thisutil; maxBid=thisBid;  }
		}
		return maxBid;
	}

	
	/**
	 * @author Herbert. Modified Wouter.
	 * @param filename The name of the xml file to parse.
	 * @throws exception if error occurs, e.g. file not found
	 */
	private final boolean loadTreeFromFile(String filename) throws Exception
	{        
        SimpleDOMParser parser = new SimpleDOMParser();
        BufferedReader file = new BufferedReader(new FileReader(new File(filename)));                  
        SimpleElement root = parser.parse(file);
        fXMLRoot = root;
        return loadTreeRecursive(root);
   	}
	
	
	/**
	 * @author hdevos
	 * Loads the weights and issues for the evaluators.
	 * @param root The current root of the XML structure.
	 */
	private final boolean loadTreeRecursive(SimpleElement currentRoot){
		//TODO hdevos:
        //We get an Objective or issue from the SimpleElement structure,
        //get it's number of children:
		int nrOfWeights = 0;
		String what = currentRoot.getTagName();
/*		if(!what.equals("Objective") || !what.equals("utility_space")){ //are the only two tags that can have weights
			loadTreeRecursive((SimpleElement)(currentRoot.getChildElements())[0]); //It's the utility_space tag. Ignore.
		}
*/		
		//TODO hdevos: find a way of checking the number of issues in the Domain versus the number of issues in the UtilitySpace
		
		int index;
		double weightsSum = 0;
		//load reservation value
		try {
			if((currentRoot.getChildByTagName("reservation")!=null)&&(currentRoot.getChildByTagName("reservation").length>0)){
				SimpleElement xml_reservation = (SimpleElement)(currentRoot.getChildByTagName("reservation")[0]);
				fReservationValue = Double.valueOf(xml_reservation.getAttribute("value"));
			}
		} catch (Exception e) {
			System.out.println("Utility space has no reservation value");
		}
		//load discount factor
		try {
			if((currentRoot.getChildByTagName("discount_factor")!=null)&&(currentRoot.getChildByTagName("discount_factor").length>0)){
				SimpleElement xml_reservation = (SimpleElement)(currentRoot.getChildByTagName("discount_factor")[0]);
				discountFactor = Double.valueOf(xml_reservation.getAttribute("value"));
			}
		} catch (Exception e) {
			System.out.println("Utility space has no discount factor;");
		}
		
			
			
		Vector<Evaluator> tmpEvaluator = new Vector<Evaluator>(); //tmp vector with all Evaluators at this level. Used to normalize weigths.
		EVALUATORTYPE evalType;
    	String type, etype;
        Evaluator lEvaluator=null;
        int indexEvalPrice=-1;
        
        //Get the weights of the current children
		Object[] xml_weights = currentRoot.getChildByTagName("weight");
		nrOfWeights = xml_weights.length; //assuming each 
		HashMap<Integer, Double> tmpWeights = new HashMap<Integer, Double>();
		//System.out.println("nrOfWeights = " + nrOfWeights);
        for(int i = 0; i < nrOfWeights; i++){
        	index = Integer.valueOf(((SimpleElement)xml_weights[i]).getAttribute("index"));
        	double dval = Double.valueOf( ((SimpleElement)xml_weights[i]).getAttribute("value"));
        	Integer indInt = new Integer(index);
        	Double valueDouble = new Double(dval);
        	tmpWeights.put(indInt, valueDouble);
            weightsSum += tmpWeights.get(index); // For normalization purposes on this level. See below.
        }
        
//      Collect evaluations for each of the issue values from file.
        // Assumption: Discrete-valued issues.
        Object[] xml_issues = currentRoot.getChildByTagName("issue");
        Object[] xml_objectives = currentRoot.getChildByTagName("objective");
        Object[] xml_obj_issues = new Object[xml_issues.length + xml_objectives.length];
        int i_ind;
        for(i_ind = 0; i_ind < xml_issues.length; i_ind++){
        	//System.out.println("issues_index: " + i_ind + " vs length:" + xml_issues.length +" to fill something of lenght: "+ xml_obj_issues.length);
        	xml_obj_issues[i_ind] = xml_issues[i_ind];
        }
 /*     for(int o_ind = i_ind; o_ind < xml_obj_issues.length; o_ind++){ 
        	System.out.println("objectives_index: " + o_ind + " vs length:" + xml_objectives.length +" to fill something of lenght: "+ xml_obj_issues.length);
        	xml_obj_issues[o_ind] = xml_objectives[o_ind];
        }
 */     for(int o_ind = 0; (o_ind + i_ind) < xml_obj_issues.length; o_ind++){ 
 			//System.out.println("objectives_index: " + o_ind + " vs length:" + xml_objectives.length +" to fill something of lenght: "+ xml_obj_issues.length);
 			xml_obj_issues[(o_ind + i_ind) ] = xml_objectives[o_ind];
 		}  
//        boolean issueWithCost = false;
//        double[] cost;
        for(int i=0;i<xml_obj_issues.length;i++) {
            index = Integer.valueOf(((SimpleElement)xml_obj_issues[i]).getAttribute("index"));
            type = ((SimpleElement)xml_obj_issues[i]).getAttribute("type");
            etype = ((SimpleElement)xml_obj_issues[i]).getAttribute("etype");
            if (type==null) { // No value type specified.
        		new Warning("Evaluator type not specified in utility template file.");
        		// TODO: Define exception.
            	evalType = EVALUATORTYPE.DISCRETE;
        	}
            else if (type.equals(etype)) {
            		evalType = EVALUATORTYPE.convertToType(type);
            } else if (etype!=null && type==null) {
            	evalType = EVALUATORTYPE.convertToType(etype);
            } else if (type!=null && etype==null) { // Used label "type" instead of label "vtype".
            	evalType = EVALUATORTYPE.convertToType(type);
            } else {
            	System.out.println("Conflicting value types specified for evaluators in utility template file.");
            	// TODO: Define exception.
            	// For now: use "type" label.
            	evalType = EVALUATORTYPE.convertToType(type);
            }
            if(tmpWeights.get(index) != null){
            	switch(evalType) {
            	case DISCRETE:
            		lEvaluator = new EvaluatorDiscrete();
            		break;
            	case INTEGER:
            		lEvaluator = new EvaluatorInteger();
            		break;
            	case REAL:
            		lEvaluator = new EvaluatorReal();
            		break;
            	case PRICE:
            		if (indexEvalPrice>-1)
            			System.out.println("Multiple price evaluators in utility template file!");
            		// TODO: Define exception.
            		indexEvalPrice = index-1;
            		lEvaluator = new EvaluatorPrice();
            		break;
            	case OBJECTIVE:
            		lEvaluator = new EvaluatorObjective();		
            		break;
            	}
            	lEvaluator.loadFromXML((SimpleElement)(xml_obj_issues[i]));
            	// TODO: put lEvaluator to an array (done?)
            	//evaluations.add(tmp_evaluations);
            	
            	try{
            		fEvaluators.put(getDomain().getObjective(index),lEvaluator); //Here we get the Objective or Issue.
            	}catch(Exception e){
            		System.out.println("Domain-utilityspace mismatch");
            		e.printStackTrace();
            		return false;
            	}
            }
            try{
            	if(nrOfWeights != 0){
            		Integer indexInt = new Integer(index);
            		//System.out.println("Hashcode here is: " + indexInt.hashCode());
            		double tmpdwt = tmpWeights.get(indexInt).doubleValue();
            		Objective tmpob = getDomain().getObjective(index);
            		fEvaluators.get(tmpob).setWeight(tmpdwt);
            		//fEvaluators.get(getDomain().getObjective(index)).setWeight(tmpWeights.get(index).doubleValue());
            		//System.out.println("set weight to " + tmpdwt);
            	}
            }catch(Exception e){
            	System.out.println("Evaluator-weight mismatch or no weight for this issue or objective.");
            }
            tmpEvaluator.add(lEvaluator); //for normalisation purposes.
        }
        //Normalize weights if sum of weights exceeds 1.
        // Do not include weight for price evaluator! This weight represents "financial rationality factor".
        // TODO: Always normalize weights to 1??
        if (indexEvalPrice!=-1) {
        	weightsSum -= tmpWeights.get(indexEvalPrice); //FIXME? hdv: -1 is an invalid index. So.. what gives? Why is it -1 in the original program?
        }
       /* if (weightsSum>1.0) { // Only normalize if sum of weights exceeds 1.
        	for (int i=0;i<nrOfWeights;i++) {
        		if (i!=indexEvalPrice) {
        			tmpEvaluator.elementAt(i).setWeight(tmpEvaluator.elementAt(i).getWeight()/weightsSum); 
        		}
        	}
        }
       */ 
        
        //Recurse over all children:
        boolean returnval = false;
        Object[] objArray = currentRoot.getChildElements();
        for(int i = 0; i < objArray.length ; i++ )
        	returnval = loadTreeRecursive((SimpleElement)objArray[i]);
        return returnval;
	}
	

	/**
	 * 
	 * @param issueID The Issue or Objective to get the weight from
	 * @return The weight, or -1 if the objective doesn't exist.
	 */
	public double getWeight(int issueID) {
        //return weights[issuesIndex]; //old
    	//TODO geeft -1.0 terug als de weight of de eveluator niet bestaat.
		Objective ob = domain.getObjective(issueID);
		if(ob != null){
		//	System.out.println("Obje index "+ issueID +" != null");
			Evaluator ev = fEvaluators.get(ob);
			if(ev != null){
				//System.out.println("Weight " + issueID + " should be " + ev.getWeight());
				return ev.getWeight();
			}
		}
		else
			System.out.println("Obje "+ issueID +" == null");
		return 0.0; //fallthrough.
    }
    public double setWeightSimple(Objective tmpObj, double wt){
    	try{
    		Evaluator ev = fEvaluators.get(tmpObj);
   			ev.setWeight(wt); //set weight
    	}catch(NullPointerException npe){
    		return -1;
    	}
    	return wt;
    }
    
    
	
    public double setWeight(Objective tmpObj, double wt){
    	try{
    		Evaluator ev = fEvaluators.get(tmpObj);
    		double oldWt = ev.getWeight();
    		if(!ev.weightLocked()){
    			ev.setWeight(wt); //set weight
    		}
    		this.normalizeChildren(tmpObj.getParent());
    		if(this.checkTreeNormalization()){
    			return fEvaluators.get(tmpObj).getWeight();
    		}else{
    			ev.setWeight(oldWt); //set the old weight back.
    			return fEvaluators.get(tmpObj).getWeight();
    		}
    	}catch(NullPointerException npe){
    		return -1;
    	}
    	
    }
    
    /**
     * @depricated Use getObjective
     * 
     * @param index The index of the issue to 
     * @return the indexed objective or issue
     */
    public final Objective getIssue(int index) {
        return domain.getIssue(index);
    }
    
    /**
     * Returns the Objective or Issue at that index
     * @param index The index of the Objective or Issue.
     * @return An Objective or Issue.
     */
    public final Objective getObjective(int index){
    	return domain.getObjective(index);
    }
    
    public final Domain getDomain() {
        return domain;
    }
    
    /**
     * Adds an evaluator to an objective or issue
     * @param obj The Objective or Issue to attach an Evaluator to.
     * @return The new Evaluator.
     */
    public final Evaluator addEvaluator(Objective obj){
    	Evaluator ev = null;
    	ISSUETYPE etype = obj.getType();
    	switch(etype){
    	case INTEGER:
    		ev = new EvaluatorInteger();
    		break;
    	case REAL:
    		ev = new EvaluatorReal();
    		break;
    	case DISCRETE:
    		ev = new EvaluatorDiscrete();
    		break;
    	case OBJECTIVE:
    		ev = new EvaluatorObjective();
    		break;
    /*	case PRICE:
    		ev = new EvaluatorPrice();
    		break;
    */		
    	}
    	fEvaluators.put(obj, ev);
    	return ev;
    }
    
    /**
     * Sets an <Objective, evaluator> pair. Replaces old evaluator for objective
     * @param obj The Objective to attach an Evaluator to.
     * @param ev The Evaluator to attach.
     * @return the given evaluator Wouter: what's the use of the return value???
     */
    public final Evaluator addEvaluator(Objective obj, Evaluator ev){
    	fEvaluators.put(obj, ev); // replaces old value for that object-key if key already existed.
    	return ev;
    }
    
    /**
     * @return The set with all pairs of evaluators and objectives in this utilityspace.
     */
    public final Set<Map.Entry<Objective, Evaluator> >getEvaluators(){
    	return fEvaluators.entrySet(); 
    }
    
    /**
     * Place a lock on the weight of an objective or issue.
     * @param obj The objective or issue that is about to have it's weight locked.
     * @return <code>true</code> if succesfull, <code>false</code> If the objective doesn't have an evaluator yet.
     */
    public final boolean lock(Objective obj){
    	try{
    		fEvaluators.get(obj).lockWeight();
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    /**
     * Clear a lock on the weight of an objective or issue.
     * @param obj The objective or issue that is having it's lock cleared.
     * @return <code>true</code> If the lock is cleared, <code>false</code> if the objective or issue doesn't have an evaluator yet.
     */
    public final boolean unlock(Objective obj){
    	try{
    		fEvaluators.get(obj).unlockWeight();
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;    	
    }
    
    public final Set<Map.Entry<Objective,Evaluator>> normalizeChildren(Objective obj){
    	Enumeration<Objective> childs = obj.children();
    	double RENORMALCORR=0.05; // we add this to all weight sliders to solve the slider-stuck-at-0 problem.
    	double weightSum = 0;
    	double lockedWeightSum = 0;
    	int freeCount = 0;
    	int lockedCount = 0;
    	while(childs.hasMoreElements()){
    		Objective tmpObj = childs.nextElement();
    		try{
    			if(!fEvaluators.get(tmpObj).weightLocked()){
    			weightSum += fEvaluators.get(tmpObj).getWeight();
    			freeCount++;
    			}else{
    				lockedWeightSum += fEvaluators.get(tmpObj).getWeight();
    				lockedCount++;
    			}
    		}catch(Exception e){
    			
    			//do nothing, we can encounter Objectives/issues without Evaluators.
    		}
    	}
    	System.out.println("freeCount + lockedCount = " + freeCount + " + " + lockedCount);
    	if(freeCount + lockedCount == 1){
    		System.out.println("At least the IF works...");
    		Enumeration<Objective> singleChild = obj.children();
    		while(singleChild.hasMoreElements()) {
    			Objective tmpObj = singleChild.nextElement();
    			fEvaluators.get(tmpObj).setWeight(1.0);
    		}
    	}
    	
    	//Wouter: cleaned up the test...
    	//if(/*weightSum + lockedWeightSum != 1.0 && */(lockedCount +1) < (freeCount + lockedCount) /*&& weightSum + lockedWeightSum != 0.0*/ ){ //that second bit to ensure that there is no problem with
    	if( freeCount >1){
    		Enumeration<Objective> normalChilds = obj.children();
    		while(normalChilds.hasMoreElements()){
    			Objective tmpObj = normalChilds.nextElement();
    			double diff = (lockedWeightSum + weightSum) - 1.0 ;
    			 // because of RENORMALCORR, total weight will get larger.
    			double correctedWeightSum=weightSum+RENORMALCORR*freeCount;
    			try{
    				
    					if(!fEvaluators.get(tmpObj).weightLocked()){
    						double currentWeight = fEvaluators.get(tmpObj).getWeight();
    						double newWeight = currentWeight -(diff* (currentWeight+RENORMALCORR)/correctedWeightSum);
    						if(newWeight < 0){
    							newWeight = 0; //FIXME hdv: could this become 0? Unsure of that.
    						}
    						fEvaluators.get(tmpObj).setWeight(newWeight);
    						System.out.println("new Weight of " + tmpObj.getName() + " is " + newWeight);
    					}
    			}catch(Exception e){
    					// do nothing, we can encounter Objectives/issues without Evaluators.
    			}
    			
    		}
    		
    	}
    	
    	return getEvaluators();
    }
    
     public final Set<Map.Entry<Objective,Evaluator> > modifyWeight(Objective obj, double wt)
     {
    	 if(fEvaluators.get(obj).weightLocked() || wt > 1.0){
    		 return getEvaluators();
    	 }else{
    		 fEvaluators.get(obj).setWeight(wt);
    		 return normalizeChildren(obj.getParent());
    	 }
     }

     public boolean removeEvaluator(Objective obj){
    	 try{
    		 fEvaluators.remove(obj);
    		 
    	 }catch(Exception e){
    		 return false;
    	 }
    	 return true;
     }
     
    /**
     * Creates an xml representation (in the form of a SimpleElements) of the utilityspace.
     * @return A representation of this utilityspace or <code>null</code> when there was an error.
     */ 
    public SimpleElement toXML(){
    	SimpleElement root = (domain.getObjectivesRoot()).toXML(); // convert the domain. 
    	root = toXMLrecurse(root);
    	SimpleElement rootWrapper = new SimpleElement("utility_space"); 
    	//can't really say overhere how many issues there are inhere.
    	// Wouter: huh??? Just count them??
    	rootWrapper.addChildElement(root);
    	return rootWrapper;//but how to get the correct values in place?
    }
    
    /**
     * Wouter: I assume this adds the utilities (weights and cost) from this utility space
     * to a given domain. It modifies the currentLevel so the return value is superfluous.
     * @param currentLevel is pointer to a XML tree describing the domain.
     * @return XML tree with the weights and cost set. NOTE: currentLevel is modified anyway.
     */
    private SimpleElement toXMLrecurse(SimpleElement currentLevel){
    	//go through all tags.

    	// update the objective fields.
    	Object[] Objectives = currentLevel.getChildByTagName("objective");
    	//Object[] childWeights = currentLevel.getChildByTagName("weight");
    	// Wou;ter: again, domain has no weights.
    	
    	for(int objInd=0; objInd<Objectives.length;objInd++){
    		SimpleElement currentChild = (SimpleElement)Objectives[objInd];
    		int childIndex = Integer.valueOf(currentChild.getAttribute("index"));
    		try{
    			Evaluator ev = fEvaluators.get(domain.getObjective(childIndex));
    			// Wouter: nasty, they dont check whether object actually has weight.
    			// they account on an exception being thrown in dthat case....
				SimpleElement currentChildWeight = new SimpleElement("weight");
				currentChildWeight.setAttribute("index", ""+childIndex);
				currentChildWeight.setAttribute("value", ""+ev.getWeight());
				currentLevel.addChildElement(currentChildWeight);	
    		}catch(Exception e){
    			//do nothing, not every node has an evaluator. 
    		}	
    		currentChild = toXMLrecurse(currentChild);
    	}
    	
    	// update the issue fields.
    	Object[] Issues = currentLevel.getChildByTagName("issue");
    	//Object[] IssueWeights = currentLevel.getChildByTagName("weight"); 
    	// Wouter: huh, domain has no weights!!!
    	
    	for(int issInd=0; issInd<Issues.length; issInd++){
    		SimpleElement issueL = (SimpleElement) Issues[issInd];
    		
    		//set the weight
    		int childIndex = Integer.valueOf(issueL.getAttribute("index"));
    		Objective tmpEvObj = domain.getObjective(childIndex);
    		try{
    			
    			Evaluator ev = fEvaluators.get(tmpEvObj);
    			
    			SimpleElement currentChildWeight = new SimpleElement("weight");
				currentChildWeight.setAttribute("index", ""+childIndex);
				currentChildWeight.setAttribute("value", ""+ev.getWeight());
				currentLevel.addChildElement(currentChildWeight);
				
    			String evtype_str = issueL.getAttribute("etype");
    			EVALUATORTYPE evtype = EVALUATORTYPE.convertToType(evtype_str);
    			switch(evtype){
    			case DISCRETE:
    				//fill this issue with the relevant weights to items.
    				Object[] items = issueL.getChildByTagName("item");
    				for(int itemInd = 0; itemInd < items.length; itemInd++){
    					//SimpleElement tmpItem = (SimpleElement) items[itemInd];
    					IssueDiscrete theIssue = (IssueDiscrete)domain.getObjective(childIndex);
    				
    					EvaluatorDiscrete dev = (EvaluatorDiscrete) ev;
    					Integer eval = dev.getValue(theIssue.getValue(itemInd));
    					((SimpleElement)items[itemInd]).setAttribute("evaluation", ""+eval);
    					
    					Double cost = dev.getCost(theIssue.getValue(itemInd));
    					if (cost!=null) ((SimpleElement)items[itemInd]).setAttribute("cost", ""+cost);
    					
    					//String desc = dev.getDesc(theIssue.getValue(itemInd));
    					//if (desc!=null) tmpItem.setAttribute("description", ""+desc);
    				}
    				break;
    			case INTEGER:
    				Object[] Ranges = issueL.getChildByTagName("range");
    				SimpleElement thisRange = (SimpleElement)Ranges[0];
    				EvaluatorInteger iev = (EvaluatorInteger) ev;
    				thisRange.setAttribute("lowerbound", ""+iev.getLowerBound());
    				thisRange.setAttribute("upperbound", ""+iev.getUpperBound());
    				SimpleElement thisIntEval = new SimpleElement("evaluator");
    				EVALFUNCTYPE ievtype = iev.getFuncType();
    				if(ievtype == EVALFUNCTYPE.LINEAR){
    					thisIntEval.setAttribute("ftype", "linear");
    					thisIntEval.setAttribute("parameter1", ""+iev.getLinearParam());
    				}else if(ievtype == EVALFUNCTYPE.CONSTANT){
    					thisIntEval.setAttribute("ftype", "constant");
    					thisIntEval.setAttribute("parameter0", ""+iev.getConstantParam());
    				}
    				issueL.addChildElement(thisIntEval);
    				//TODO hdv We need an new simpleElement here that contains the evaluator and it's ftype. 
    				break;
    			case REAL:
    				Object[] RealRanges = issueL.getChildByTagName("range");
    				EvaluatorReal rev = (EvaluatorReal) ev;
    				SimpleElement thisRealEval = new SimpleElement("evaluator");
    				EVALFUNCTYPE revtype = rev.getFuncType();
    				if(revtype == EVALFUNCTYPE.LINEAR){
    					thisRealEval.setAttribute("ftype", "linear");
    					thisRealEval.setAttribute("parameter1", ""+rev.getLinearParam());
    				}else if(revtype == EVALFUNCTYPE.CONSTANT){
    					thisRealEval.setAttribute("ftype", "constant");
    					thisRealEval.setAttribute("parameter0", ""+rev.getConstantParam());
    				}
    				issueL.addChildElement(thisRealEval);    				
    				//TODO hdv the same thing as above vor the "evaluator" tag.
    				break;
    			}
    		}catch(Exception e){
    			//do nothing, it could be that this objective/issue doesn't have an evaluator yet.
    		}	
    		
    	}
    	
    	return currentLevel;
    }
    
    /**
      * Wouter: this function *should* check that the domainSubtreeP is a subtree of the utilSubtreeP, 
     * and that all leaf nodes are complete.
     * However currently we only check that all the leaf nodes are complete,
    * @author W.Pasman
     * @return null if util space is complete, else returns string containging explanation why not.
     */
    public String IsComplete() 
	// Oh damn, problem, we don't have the domain template here anymore.
    // so how can we check domain compativility?
    // only we can check that all fields are filled.........
    { 
    	ArrayList<Issue> issues=domain.getIssues();
    	if (issues==null) return "Utility space is not complete, in fact it is empty!";
    	String mess;
    	for (Issue issue:issues) 
    	{
    		Evaluator ev=getEvaluator(issue.getNumber());
    		if (ev==null) return "issue "+issue.getName()+" has no evaluator";
    		mess= (ev.isComplete(issue));
    		if (mess!=null) return mess;
    	}
    	return null;
    }


    /**
     * as we don't have the domain tree we can't do the check as we hoped to do .
     * @param utilSubtreeP
     * @param domainSubtreeP
     * @return  Stringg containing explanation why not a subtree, or null.
     * @author W.Pasman
    String IsSubtreeAndComplete(Objective utilSubtreeP,Objective domainSubtreeP)
    {
    	if (utilSubtreeP.isLeaf() && domainSubtreeP.isLeaf())
    	{
    		// check the evaluator at the utilSubtree, whether it agrees with the domain description and is complete
    		// if it is a leaf, it is an Issue and there should be an evaluator.
    		// note, the non-leaf nodes do not need an evaluator.
    		blabla
    	}
    	else
    	{
    			// check all objectives in the domain. 
    		for (Objective domSpaceObj:domainSubtreeP.getChildren())
    		{
    			 // get child from utilSubtreeP that has same ID. These should match.
    			 // we do this because order of children may differ.
    			Objective matchingUtilSpaceObj=utilSubtreeP.getChildWithID(domSpaceObj.getNumber());
    			if (matchingUtilSpaceObj==null)
    				return "The utility space has no objective matching domainspace object "+domSpaceObj.getName();
    			String checksubtrees=IsSubtreeAndComplete(matchingUtilSpaceObj,domSpaceObj);
    			if (checksubtrees!=null) return checksubtrees;
    		}
    		 // strictly it don't matter if there is more in the util space under this node, but 
    		 // lets give a warning...
    		if (utilSubtreeP.getChildren().size()<domainSubtreeP.getChildren().size())
    			//Wouter: need to make this a messagebox or so, how did that work??
    			System.out.println("WARNING: utility space has more objectives than the domain space under the node"+
    					domainSubtreeP.getName());
    	}
    	return null;
    }
     */

    /**
     * compute the cost of the given bid. 
     * There is also getCost in Evaluator but it currently only works for EvaluatorDiscrete.
     * Need more clarity on how to deal with this.
     * For instance, one could argue that the evaluator for the root object
     * should be able to compute cost of the entire bid.
     * @throws if cost can not be computed for some reason.
     * @return computed cost
     * @author W.Pasman
     */
    public Double getCost(Bid bid) throws Exception
    {
    	Double totalCost=0.0;
    	Double costofissue;
    	for (Issue issue: domain.getIssues())
    	{
    		int ID=issue.getNumber();
    		try {costofissue=getEvaluator(ID).getCost(this, bid, ID); }
    		catch (Exception e) { 
    			new Warning("getcost:"+e.getMessage()+". using 0",false,1);
    			costofissue=0.; 
    		}
    		totalCost += costofissue;;
    	}
    	return totalCost;
    }
    
    public void showStatistics()
    {
    	for (Objective obj: fEvaluators.keySet())
    	{
    		System.out.print("Objective "+obj.getName()+" ");
    		fEvaluators.get(obj).showStatistics();
    	}
    }
    public Double getReservationValue() {
    	return fReservationValue;
    }

    public String getFileName() {
    	return fileName;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
/*		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result
				+ ((fEvaluators == null) ? 0 : fEvaluators.hashCode());
		result = prime
				* result
				+ ((fReservationValue == null) ? 0 : fReservationValue
						.hashCode());
						*/
		return result;
	}

/*	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UtilitySpace other = (UtilitySpace) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (fEvaluators == null) {
			if (other.fEvaluators != null)
				return false;
		} else if (!fEvaluators.equals(other.fEvaluators))
			return false;
		if (fReservationValue == null) {
			if (other.fReservationValue != null)
				return false;
		} else if (!fReservationValue.equals(other.fReservationValue))
			return false;
		return true;
	}
*/	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UtilitySpace)) return false;
		UtilitySpace obj2 = (UtilitySpace)obj;
		//cehck domains
		if(!domain.equals(obj2.getDomain())) return false;
		//check evaluators
		for(Entry<Objective, Evaluator> entry : fEvaluators.entrySet()) {
			Evaluator eval2 = obj2.getEvaluator(entry.getKey().getNumber());
			if(!entry.getValue().equals(eval2)) return false;
		}
		return true;
	}

	public final double getDiscountFactor() {
		return discountFactor;
	}

}
