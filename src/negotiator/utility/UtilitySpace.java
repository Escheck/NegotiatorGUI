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
import java.util.HashMap;
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
    private double weights[];
    // TODO: make this arraylist? WHY was this a Vector type? Can you explain this to me Dmytro?
    private Map<Issue,Evaluator> fEvaluators;
    
    // Constructor
    public UtilitySpace(Domain domain, String fileName) {
        this.domain = domain;
        loadFromFile(fileName);
    }
    
    // Class methods
    private boolean checkNormalization() {
        double lSum=0;
        for(int i=0;i<domain.getNumberOfIssues();i++) {
            lSum += weights[i];
        }
        return (lSum==1);
    }
    
    public final int getNrOfEvaluators() {
    	return fEvaluators.size();
    }
    
    public final Evaluator getEvaluator(int index) {
    	Issue issue = domain.getIssue(index);
    	return fEvaluators.get(issue);
    	/*
    	 * TODO replace with:
    	 * Objective obj = domain.getObjective(index);
    	 * return fEvaluators.get(obj);
    	 */
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
    	
    	Issue lIssue = getDomain().getIssue(pIssueIndex);
    	Evaluator lEvaluator = fEvaluators.get(lIssue);
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
    	default:
    		return -1;
    	}
    }
    
    // KH 070511: Moved getMaxBid method to UtilitySpace class since it should be available to all agents.
    // Method returns (a) bid which has maximum utility in this utility space.
	public final Bid getMaxUtilityBid() {
		int nrOfIssues = domain.getNumberOfIssues();
		Value[] values = new Value[nrOfIssues];
		Value[] maxValues = new Value[nrOfIssues];
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
				values[i] = ((IssueDiscrete)issue).getValue(0);
				break;
			case INTEGER:
				values[i] = new ValueInteger(((IssueInteger)issue).getLowerBound());
				maxValues[i] = new ValueInteger(((IssueInteger)issue).getUpperBound());
				break;
			case REAL:
				values[i] = new ValueReal(((IssueReal)issue).getLowerBound());
				maxValues[i] = new ValueReal(((IssueReal)issue).getUpperBound());
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
				values[i] = new ValueDiscrete(((IssueDiscrete)issue).getValue(lMax).getValue());
				maxValues[i] = new ValueDiscrete(((IssueDiscrete)issue).getValue(lMax).getValue());
				
				break;
			case INTEGER:
				// TODO: Add code.
				break;
			case REAL:
				// assume indep & linear
				
				values[i] = new ValueReal(((IssueReal)issue).getLowerBound());
				maxValues[i] = new ValueReal(((IssueReal)issue).getUpperBound());
				lBid = new Bid(domain,values);
				newBid = new Bid(domain,maxValues);
				if (this.getUtility(lBid)>this.getUtility(newBid))
					maxValues[i] = new ValueReal(((ValueReal)values[i]).getValue());
				break;
			}
		}
		return new Bid(domain, maxValues);
	}

    private final void loadFromFile(String fileName) {
    	double weightsSum = 0;
    	EVALUATORTYPE evalType;
    	String type, etype;
        Evaluator lEvaluator=null;
        int nrOfIssues=0, indexEvalPrice=-1;
    	
        SimpleDOMParser parser = new SimpleDOMParser();
        try {
            fEvaluators = new HashMap<Issue, Evaluator>();
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
            weights = new double[nrOfIssues];
            Object[] xml_weights = root.getChildByTagName("weight");
            for(int i=0;i<nrOfIssues;i++) {
                index = Integer.valueOf(((SimpleElement)xml_weights[i]).getAttribute("index"));
                weights[index-1] = Double.valueOf(((SimpleElement)xml_weights[i]).getAttribute("value"));
                weightsSum += weights[index-1]; // For normalization purposes. See below.
            }
            
            // Collect evaluations for each of the issue values from file.
            // Assumption: Discrete-valued issues.
            Object[] xml_issues = root.getChildByTagName("issue");
//            boolean issueWithCost = false;
//            double[] cost;
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
        return weights[issuesIndex];
    }

    public final Issue getIssue(int index) {
        return domain.getIssue(index);
    }
    
    public final Domain getDomain() {
        return domain;
    }
    
}
