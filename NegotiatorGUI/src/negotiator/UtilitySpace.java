/*
 * UtilitySpace.java
 *
 * Created on November 6, 2006, 10:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.io.*;
import java.util.Vector;

import negotiator.issue.*;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;

/**
 *
 * @author Dmytro Tykhonov & Koen Hindriks 
 * 
 */

public class UtilitySpace {
	
	// Class fields
    private Domain domain;
    private double weights[];
    private Vector evaluations;
    
    // Constructor
    public UtilitySpace(Domain domain, String fileName) {
        this.domain = domain;
        loadFromFile(fileName);
        if (!checkNormalization())
        	System.out.println("Warning: Weights in "+fileName+" do not add up to 1.");
    }
    
    // Class methods
    private boolean checkNormalization() {
        double lSum=0;
        for(int i=0;i<domain.getNumberOfIssues();i++) {
            lSum += weights[i];
        }
        return (lSum==1);
    }
    
    public final int getNumberOfIssues() {
        return domain.getNumberOfIssues();
    }
    
    public final double getUtility(Bid bid) {
        double utility = 0;
        for(int i=0;i<domain.getNumberOfIssues();i++) {
            
            utility = utility +
            	weights[i]*getEvaluation(i,
            			((DiscreteIssue)getIssue(i)).getValueIndex(((ValueDiscrete)bid.getValue(i)).getValue()));
            System.out.println("Issue:"+i+" "+((ValueDiscrete)bid.getValue(i)).getValue());
        }
        return utility;
    }

    private final void loadFromFile(String fileName) {
        SimpleDOMParser parser = new SimpleDOMParser();
        try {
            evaluations = new Vector();
            BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));                  
            SimpleElement root = parser.parse(file);
            
            // Read indicated number of issues from the xml file.
            String s = root.getAttribute("number_of_issues");
            int nrOfIssues = new Integer(s);
            if (domain.getNumberOfIssues()!=nrOfIssues)
            	System.out.println("Mismatch between indicated number of issues in agent and template file.");
            	// TO DO: Define exception.
            int index, nrOfValues;
            
            // Collect weights from file.
            weights = new double[nrOfIssues];
            Object[] xml_weights = root.getChildByTagName("weight");
            for(int i=0;i<nrOfIssues;i++) {
                index = Integer.valueOf(((SimpleElement)xml_weights[i]).getAttribute("index"));
                weights[index-1] = Double.valueOf(((SimpleElement)xml_weights[i]).getAttribute("value"));
            }
            
            // Collect evaluations for each of the issue values from file.
            // Assumption: Discrete-valued issues.
            Object[] xml_issues = root.getChildByTagName("issue");
            for(int i=0;i<nrOfIssues;i++) {
                index = Integer.valueOf(((SimpleElement)xml_issues[i]).getAttribute("index"));
                Object[] xml_items = ((SimpleElement)xml_issues[i]).getChildByTagName("item");
                nrOfValues = xml_items.length;
                double[] tmp_evaluations = new double[nrOfValues];
                for(int j=0;j<nrOfValues;j++) {
                    index = Integer.valueOf(((SimpleElement)xml_items[j]).getAttribute("index"));
                    //values[index-1] = ((SimpleElement)xml_items[i]).getAttribute("value");
                    tmp_evaluations[index-1] = Double.valueOf(((SimpleElement)xml_items[j]).getAttribute("evaluation"));
                }
                evaluations.add(tmp_evaluations);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    public double getWeight(int issuesIndex) {
        return weights[issuesIndex];
    }
    public final double getEvaluation(int issueIndex, int valueIndex) {
        if (((DiscreteIssue)getIssue(issueIndex)).getValue(valueIndex).equals("unspecified")) return 0;
        else {
            double[] tmp = (double[])(evaluations.get(issueIndex)); 
            return tmp[valueIndex];
        }
    }
    
    public final Issue getIssue(int index) {
        return domain.getIssue(index);
    }
    
    public final Domain getDomain() {
        return domain;
    }
    
}
