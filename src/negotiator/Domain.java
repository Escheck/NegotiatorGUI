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

/**
 *
 * @author Dmytro Tykhonov & Koen Hindriks
 * 
 */

public class Domain {
	
	// Class fields
    private int fNumberOfIssues;
    private Issue fIssues[];
    
    // Constructor
    public Domain(SimpleElement root) {
        loadFromXML(root);
    }
    
    // Class methods
    public final int getNumberOfIssues() {
        return fNumberOfIssues;
    }
    
    public final Issue getIssue(int index) {
        return fIssues[index];
    }
    
    private final void loadFromXML(SimpleElement pRoot) {
    	
        // Get number of issues from the xml file & create array of issues
        String s = pRoot.getAttribute("number_of_issues");
        fNumberOfIssues = new Integer(s);
        fIssues = new Issue[fNumberOfIssues];
        
        // Get issue parameters and/or values
        Object[] xml_issues =  pRoot.getChildByTagName("issue");
        for(int i=0;i<fNumberOfIssues;i++) {	
        	int index = Integer.valueOf(((SimpleElement)xml_issues[i]).getAttribute("index"));
            String name = ((SimpleElement)xml_issues[i]).getAttribute("name");
            String type = ((SimpleElement)xml_issues[i]).getAttribute("type");
            ISSUETYPE issueType;
            if (type==null) {
            	System.out.println("Type not specified in template file.");
            	issueType = ISSUETYPE.DISCRETE;
            	}
            else
            	issueType = Issue.convertToType(type);
            
            Object[] xml_items;
            Object xml_item;
            int nrOfItems, minI, maxI;
            double minR, maxR;
            String[] values;
            Issue issue;
            switch(issueType) {
            case DISCRETE:
            	// Collect discrete values for discrete-valued issue from xml template
            	xml_items = ((SimpleElement)xml_issues[i]).getChildByTagName("item");
                nrOfItems = xml_items.length;
                values = new String[nrOfItems];
                for(int j=0;j<nrOfItems;j++) {
                    index = Integer.valueOf(((SimpleElement)xml_items[j]).getAttribute("index"));
                    values[index-1] = ((SimpleElement)xml_items[j]).getAttribute("value");
                }
            	issue = new DiscreteIssue(name, index, issueType, values);
            	break;
            case INTEGER:
            	// Collect range bounds for integer-valued issue from xml template
            	xml_item = ((SimpleElement)xml_issues[i]).getChildByTagName("range");
            	minI = Integer.valueOf(((SimpleElement)xml_item).getAttribute("lowerbound"));
            	maxI = Integer.valueOf(((SimpleElement)xml_item).getAttribute("upperbound"));
            	issue = new IntegerIssue(name, index, issueType, minI, maxI);
            	break;
            case REAL:
            	// Collect range bounds for integer-valued issue from xml template
            	xml_item = ((SimpleElement)xml_issues[i]).getChildByTagName("range");
            	minR = Integer.valueOf(((SimpleElement)xml_item).getAttribute("lowerbound"));
            	maxR = Integer.valueOf(((SimpleElement)xml_item).getAttribute("upperbound"));
            	issue = new RealIssue(name, index, issueType, minR, maxR);
            	break;
            case PRICE:
            	// Collect range bounds for integer-valued issue from xml template
            	xml_item = ((SimpleElement)xml_issues[i]).getChildByTagName("range");
            	minR = Integer.valueOf(((SimpleElement)xml_item).getAttribute("lowerbound"));
            	maxR = Integer.valueOf(((SimpleElement)xml_item).getAttribute("upperbound"));
            	issue = new PriceIssue(name, index, issueType, minR, maxR);
            	break;
            default: // By default, create discrete-valued issue
            	// Collect discrete values for discrete-valued issue from xml template
            	xml_items = ((SimpleElement)xml_issues[i]).getChildByTagName("item");
                nrOfItems = xml_items.length;
                values = new String[nrOfItems];
                for(int j=0;j<nrOfItems;j++) {
                    int item_index = Integer.valueOf(((SimpleElement)xml_items[j]).getAttribute("index"));
                    values[item_index-1] = ((SimpleElement)xml_items[j]).getAttribute("value");
                }
            	issue = new DiscreteIssue(name, index, issueType, values);
            	break;
            }
            fIssues[i] = issue;
        }
    }
}
