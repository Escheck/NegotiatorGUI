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
import java.util.Vector;
import java.util.Map;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.issue.*;
import negotiator.xml.*;

/**
 *
 * @author Dmytro Tykhonov & Koen Hindriks 
 * 
 */

public class UtilitySpace {
	
	// Class fields
    private Domain domain;
    private HashMap<Integer, Double> weights[];
    // TODO: make this arraylist? WHY was this a Vector type? Can you explain this to me Dmytro?
//    private Map<Issue,Evaluator> fEvaluators;
    private Map<Objective, Evaluator> fEvaluators; //changed to use Objective. TODO check casts.
    // Constructor
    public UtilitySpace(Domain domain, String fileName) {
        this.domain = domain;
        loadTreeFromFile(fileName);
    }
    
    // Class methods
    
    /**
     * @depricated Use checkTreeNormalization
     */
    private boolean checkNormalization() {
        double lSum=0;
        for(int i=0;i<domain.getNumberOfIssues();i++) {
            lSum += weights[i];
        }
        return (lSum==1);
    }
    /**
     * Checks the normalization throughout the tree. Will eventually replace checkNormalization 
     * @return true if the weigths are indeed normalized, false if they aren't. 
     */
    private boolean checkTreeNormalization(){
    	return checkTreeNormalizationRecursive(domain.getObjectivesRoot());
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
    	
    	while(children.hasMoreElements() && normalised){
    		
    		Objective tmpObj = children.nextElement();
    		lSum += (fEvaluators.get(tmpObj)).getWeight();
    		
    	}
    	
    	return (normalised && lSum==1);
    }
    
    public final int getNrOfEvaluators() {
    	return fEvaluators.size();
    }
    
    /**
     * Returns an evaluator for an Objective or Issue
     * @param index The index of the Objective or Issue
     * @return An Evaluator for the Objective or Issue.
     */
    public final Evaluator getEvaluator(int index) {
 /*   	Issue issue = domain.getIssue(index);
    	return fEvaluators.get(issue);
 */   	
    	Objective obj = domain.getObjective(index); //Used to be Issue in stead of Objective
    	return fEvaluators.get(obj);
    }

// Utility space should not return domain-related information, should it?
//    public final int getNumberOfIssues() {
//        return domain.getNumberOfIssues();
//    }
    
    public final double getUtility(Bid bid) {
    	EVALUATORTYPE type;
        double utility = 0, financialUtility = 0, financialRat = 0;
        
        for(int i=0;i<domain.getNumberOfIssues();i++) {
        	type = getEvaluator(i).getType();
        	switch(type) {
        	case DISCRETE:
        	case INTEGER:
        	case REAL:
        		utility += weights[i]*getEvaluation(i,bid);
        		break;
        	case PRICE:
        		financialUtility = getEvaluation(i,bid);
        		financialRat = ((EvaluatorPrice)getEvaluator(i)).rationalityfactor;
        		break;
        	}
        }
        return financialRat*financialUtility+(1-financialRat)*utility;
    }
    
    public final double getEvaluation(int pIssueIndex, Bid bid) {
    	ISSUETYPE vtype;
    	vtype = bid.getValue(pIssueIndex).getType();
    	
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
    
    // KH 070511: Moved getMaxBid method to UtilitySpace class since it should be available to all agents.
    // Method returns (a) bid which has maximum utility in this utility space.
	public final Bid getMaxUtilityBid() {
		int nrOfIssues = domain.getNumberOfIssues();
//		Value[] values = new Value[nrOfIssues];	//TODO hdv: Do something about these values. See proposal of 11-6-7
		HashMap<Integer, Value> values = new HashMap<Integer, Value>();
//		Value[] maxValues = new Value[nrOfIssues]; //TODO hdv: Do something about these values. See proposal of 11-6-7.
		HashMap<Integer, Value> maxValues = new HashMap<Integer, Value>();
		Bid lBid, newBid;
		Issue issue;
		ISSUETYPE type;
		int lMax;
		double u1,u2;

		// QUESTION: Wouldn't it be better to allign issue indexes in template with ranges used in arrays.
		// Now we use a range from 0 to nrOfIssue-1, which is different from indexes in templates.
		
		// Construct initial bid.
		for (int i=0; i<nrOfIssues; i++) {
			issue = domain.getIssue(i);
			type = issue.getType();
			switch(type) {
			case DISCRETE:
//				values[i] = ((IssueDiscrete)issue).getValue(0);
				values.put(new Integer(i), ((IssueDiscrete)issue).getValue(0));
				break;
			case INTEGER:
//				values[i] = new ValueInteger(((IssueInteger)issue).getLowerBound());
				values.put(new Integer(i), new ValueInteger(((IssueInteger)issue).getLowerBound()));
//				maxValues[i] = new ValueInteger(((IssueInteger)issue).getUpperBound());
				maxValues.put(new Integer(i), new ValueInteger(((IssueInteger)issue).getUpperBound()));
				break;
			case REAL:
//				values[i] = new ValueReal(((IssueReal)issue).getLowerBound());
				values.put(new Integer(i), new ValueReal(((IssueReal)issue).getLowerBound()));
//				maxValues[i] = new ValueReal(((IssueReal)issue).getUpperBound());
				maxValues.put(new Integer(i), new ValueReal(((IssueReal)issue).getUpperBound()));
				break;
			}
		}
		
		// ASSUMPTION: issues are (almost) independent, and utility de/in-creases linear for non-discrete issues.
		for (int i = 0; i < nrOfIssues; i++) {
			//lBid = initialBid;
			issue = domain.getIssue(i);
			type = issue.getType();
			switch(type) {
			case DISCRETE:
				lMax = 0;
				u1 = ((EvaluatorDiscrete)this.getEvaluator(i)).getEvaluation(((IssueDiscrete)issue).getValue(0));
				for (int j = 1; j < ((IssueDiscrete)issue).getNumberOfValues(); j++) {
					// Assume issue independence here.
					u2 = ((EvaluatorDiscrete)this.getEvaluator(i)).getEvaluation(((IssueDiscrete)issue).getValue(j));
					if (u1<u2) {
						lMax =j;
						u1 = u2;
					}
				}
//				values[i] = new ValueDiscrete(((IssueDiscrete)issue).getValue(lMax).getValue());
				values.put(new Integer(i), new ValueDiscrete(((IssueDiscrete)issue).getValue(lMax).getValue()) );
//				maxValues[i] = new ValueDiscrete(((IssueDiscrete)issue).getValue(lMax).getValue());
				maxValues.put(new Integer(i), new ValueDiscrete(((IssueDiscrete)issue).getValue(lMax).getValue()));
				
				break;
			case INTEGER:
				// TODO: Add code.
				break;
			case REAL:
				// assume indep & linear
				
//				values[i] = new ValueReal(((IssueReal)issue).getLowerBound());
				values.put(new Integer(i), new ValueReal(((IssueReal)issue).getLowerBound()));
//				maxValues[i] = new ValueReal(((IssueReal)issue).getUpperBound());
				maxValues.put(new Integer(i), new ValueReal(((IssueReal)issue).getUpperBound()));
				lBid = new Bid(domain,values);	//TODO hdv: Are these the only 3 places where the constructor of Bid is called?
				newBid = new Bid(domain,maxValues);
				if (this.getUtility(lBid)>this.getUtility(newBid))
//					maxValues[i] = new ValueReal(((ValueReal)values.get(new Integer(i))).getValue());
					maxValues.put(new Integer(i), new ValueReal(((ValueReal)values.get(new Integer(i))).getValue()));
				break;
			}
		}
		return new Bid(domain, maxValues);
	}

	
	//added by Herbert
	/**
	 * @param filename The name of the xml file to parse.
	 */
	private final void loadTreeFromFile(String filename){
    //	double weightsSum = 0;
    //	EVALUATORTYPE evalType;
    //	String type, etype;
       // Evaluator lEvaluator=null;
     //   int nrOfIssues=0, indexEvalPrice=-1;
        
        SimpleDOMParser parser = new SimpleDOMParser();
        try{
        	fEvaluators = new HashMap<Objective, Evaluator>();
            BufferedReader file = new BufferedReader(new FileReader(new File(filename)));                  
            SimpleElement root = parser.parse(file);
            loadTreeRecursive(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
   	}
	/**
	 * Loads the weights and issues for the evaluators.
	 * @param root The current root of the Objective tree.
	 */
	private final void loadTreeRecursive(SimpleElement currentRoot){
		//TODO hdevos:
        //We get an Objective or issue from the SimpleElement structure,
        //get it's number of children:
		int nrOfWeights = 0;
		String what = currentRoot.getTagName();
		if(!what.equals("Objective") || what.equals("utility_space")){ //are the only two tags that can have weights
			loadTreeRecursive((SimpleElement)(currentRoot.getChildElements())[0]); //It's the utility_space tag. Ignore.
		}
		//TODO hdevos: find a way of checking the number of issues in the Domain versus the number of issues in the UtilitySpace
		
		int index;
		double weightsSum = 0;
		
		Vector<Evaluator> tmpEvaluator = new Vector<Evaluator>(); //tmp vector with all Evaluators at this level. Used to normalize weigths.
		EVALUATORTYPE evalType;
    	String type, etype;
        Evaluator lEvaluator=null;
        int indexEvalPrice=-1;
        
        //Get the weights of the current children
		Object[] xml_weights = currentRoot.getChildByTagName("weight");
		nrOfWeights = xml_weights.length; //assuming each 
		double tmpWeights[] = new double[nrOfWeights];
        for(int i = 0; i < nrOfWeights; i++){
        	index = Integer.valueOf(((SimpleElement)xml_weights[i]).getAttribute("index"));
        	tmpWeights[index-1] = Double.valueOf(((SimpleElement)xml_weights[i]).getAttribute("value"));
            weightsSum += tmpWeights[index-1]; // For normalization purposes on this level. See below.
        }
        
//      Collect evaluations for each of the issue values from file.
        // Assumption: Discrete-valued issues.
        Object[] xml_issues = currentRoot.getChildByTagName("issue");
//        boolean issueWithCost = false;
//        double[] cost;
        for(int i=0;i<nrOfWeights;i++) {
            index = Integer.valueOf(((SimpleElement)xml_issues[i]).getAttribute("index"));
            type = ((SimpleElement)xml_issues[i]).getAttribute("type");
            etype = ((SimpleElement)xml_issues[i]).getAttribute("etype");
            if (type==etype) {
            	if (type==null) { // No value type specified.
            		System.out.println("Evaluator type not specified in utility template file.");
            		// TODO: Define exception.
                	evalType = EVALUATORTYPE.DISCRETE;
            	} else { // Both "type" as well as "vtype" attribute, but consistent.
            		evalType = EVALUATORTYPE.convertToType(type);
            	}
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
            	//set weights here.
            	break;
            }
            lEvaluator.loadFromXML((SimpleElement)(xml_issues[i]));
            // TODO: put lEvaluator to an array (done?)
            //evaluations.add(tmp_evaluations);
            //TODO: hdevos: add weigths to the evaluators.
            fEvaluators.put(getDomain().getObjective(index),lEvaluator); //Here we get the Objective or Issue.
            
            tmpEvaluator.add(lEvaluator); //for normalisation purposes.
        }
        //Normalize weights if sum of weights exceeds 1.
        // Do not include weight for price evaluator! This weight represents "financial rationality factor".
        // TODO: Always normalize weights to 1??
        if (indexEvalPrice!=-1) {
        	weightsSum -= tmpWeights[indexEvalPrice];
        }
        if (weightsSum>1) { // Only normalize if sum of weights exceeds 1.
        	for (int i=0;i<nrOfWeights;i++) {
        		if (i!=indexEvalPrice) {
        			tmpEvaluator.elementAt(i).setWeight(tmpEvaluator.elementAt(i).getWeight()/weightsSum); //redo this bit!
        		}
        	}
        }
        
        
        //Recurse over all children:
        Object[] objArray = currentRoot.getChildElements();
        for(int i = 0; i < objArray.length ; i++ )
        loadTreeRecursive((SimpleElement)objArray[i]);             
        
        
        //Checking if the UtilitySpace of this agent and the Domain match.
		
	}
	
    private final void loadFromFile(String fileName) {
    	double weightsSum = 0;
    	EVALUATORTYPE evalType;
    	String type, etype;
        Evaluator lEvaluator=null;
        int nrOfIssues=0, indexEvalPrice=-1, nrOfLevelWeights = 0;
    	
        SimpleDOMParser parser = new SimpleDOMParser();
        try {
            fEvaluators = new HashMap<Objective, Evaluator>();
            BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));                  
            SimpleElement root = parser.parse(file);
            
            // Read indicated number of issues from the xml file.
            String s = root.getAttribute("number_of_issues"); 
            nrOfIssues = new Integer(s);
            if (domain.getNumberOfIssues()!=nrOfIssues)
            	System.out.println("Mismatch between indicated number of issues in agent and template file.");
            	// TODO: Define exception?
            int index;
            
            // Collect weights from file.
            // TODO: Normalize weights if they add up to >1. 
            
            Object[] xml_weights = root.getChildByTagName("weight");
            nrOfLevelWeights = xml_weights.length;
            weights = new double[nrOfLevelWeights];
            for(int i=0;i<nrOfLevelWeights;i++) {
                index = Integer.valueOf(((SimpleElement)xml_weights[i]).getAttribute("index"));
                weights[index-1] = Double.valueOf(((SimpleElement)xml_weights[i]).getAttribute("value"));
                weightsSum += weights[index-1]; // For normalization purposes. See below.
            }
            
            // Collect evaluations for each of the issue values from file.
            // Assumption: Discrete-valued issues.
            Object[] xml_issues = root.getChildByTagName("issue");
//            boolean issueWithCost = false;
//            double[] cost;
            nrOfIssues = xml_issues.length;
            for(int i=0;i<nrOfIssues;i++) {
                index = Integer.valueOf(((SimpleElement)xml_issues[i]).getAttribute("index"));
                type = ((SimpleElement)xml_issues[i]).getAttribute("type");
                etype = ((SimpleElement)xml_issues[i]).getAttribute("etype");
                if (type==etype) {
                	if (type==null) { // No value type specified.
                		System.out.println("Evaluator type not specified in utility template file.");
                		// TODO: Define exception.
                    	evalType = EVALUATORTYPE.DISCRETE;
                	} else { // Both "type" as well as "vtype" attribute, but consistent.
                		evalType = EVALUATORTYPE.convertToType(type);
                	}
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
                }
                lEvaluator.loadFromXML((SimpleElement)(xml_issues[i]));
                // TODO: put lEvaluator to an array (done?)
                //evaluations.add(tmp_evaluations);
                fEvaluators.put(getDomain().getIssue(index-1),lEvaluator);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Normalize weights if sum of weights exceeds 1.
        // Do not include weight for price evaluator! This weight represents "financial rationality factor".
        // TODO: Always normalize weights to 1??
        if (indexEvalPrice!=-1) {
        	weightsSum -= weights[indexEvalPrice];
        }
        if (weightsSum>1) { // Only normalize if sum of weights exceeds 1.
        	for (int i=0;i<nrOfIssues;i++) {
        		if (i!=indexEvalPrice) {
        			weights[i] = weights[i]/weightsSum;
        		}
        	}
        }
    }
   
    public double getWeight(int issuesIndex) {
        //return weights[issuesIndex]; //old
    	return fEvaluators.get(domain.getObjective(issuesIndex)).getWeight();
    }
    
    /**
     * @depricated Use getObjective
     * 
     * @param index The index of the issue to 
     * @return the indexed issue
     */
    public final Issue getIssue(int index) {
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
    
}
