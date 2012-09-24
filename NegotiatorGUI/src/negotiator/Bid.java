/*
 * Bid.java
 *
 * Created on November 6, 2006, 10:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

// import negotiator.xml.SimpleElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.xml.SimpleElement;

/**
 * 
 * @author Dmytro Tykhonov & Koen Hindriks
 * Wouter: a bid is a set of <idnumber,value> pairs, where idnumber is the unique number of the issue,
 * and value is the picked alternative.
 */

@XmlRootElement
public final class Bid implements XMLable
{

	// Class fields
	Domain fDomain;
	@XmlElement(name="values")
	@XmlJavaTypeAdapter(MyMapAdapter.class)
	private HashMap<Integer, Value> fValues; // Wouter: the bid values  for each IssueID

	public Bid(){fValues = new HashMap<Integer, Value>();}
	/**
	 * create a new bid in a domain. Partially checks the validity of the bid as well
	 * There is only this constructor because we require that ALL values in the domain
	 * get assigned a value.
	 * @param domainP: the domain in which the bid is done
	 * @param bidsP: HashMap, which is a set of pairs <issueID,value>
	 * @throws Exception if the bid is not a legal bid in the domain.
	 */
	public Bid(Domain domainP, HashMap<Integer,Value> bidP) throws Exception
	{
		this.fDomain = domainP; // THIS NEEDS A CHECK!

		// Check if indexes are ok
		// Discussion with Dmytro 16oct 1200: it is possible to do only a partial bid, leaving
		// part of the issues un-set. But each issue being bidded on has to exist in the domain,
		// and this is what we check here.
		// Discussion 16oct 16:03: No, ALL values have to be set.
		// Discussion 19oct: probably there only is this particular constructor because
		// that enables to enforce this.
		//ArrayList<Issue> issues=domainP.getIssues();
/*		for (Issue issue:issues)
			if (bidP.get(new Integer(issue.getNumber()))==null)
				throw new BidDoesNotExistInDomainException(
						"bid for issue '"+issue.getName()+"' (issue #"+issue.getNumber()+") lacks");
		*/
		fValues = bidP;
	}


	/**
	 * @return the picked value for given issue idnumber 
	 */
	public Value getValue(int issueNr) throws Exception
	{
		Value v=fValues.get(issueNr);
		if (v==null) {
			if (fDomain.getIssue(issueNr)==null) 
				throw new Exception("Bid.getValue: issue "+issueNr+" does not exist at all");
			throw new Exception("There is no evaluator for issue "+issueNr);
		}
		return v;
	}

	public void setValue(int issueId, Value pValue) {
		if (fValues.get(issueId).getType() == pValue.getType()) {
			fValues.put(issueId, pValue);
		} /*
			 * TODO Throw an excpetion. else throw new
			 * BidDoesNotExistInDomainException();
			 */
	}
	public void setDomain(Domain domain) {
		fDomain = domain;
	}
	public String toString() {
        String s = "Bid[";
        Set<Entry<Integer, Value>> value_set = fValues.entrySet();
        Iterator value_it = value_set.iterator();
        while(value_it.hasNext()){
        	int ind = ((Entry<Integer, Value>)value_it.next()).getKey();
        	Object tmpobj = fDomain.getObjective(ind); //Objective isn't recognized here, GKW. hdv
        	if(tmpobj != null){
        		String nm = fDomain.getObjective(ind).getName();
        		s += nm + ": " +
        			fValues.get(ind) +", ";
        	}else{
        		System.out.println("objective with index " + ind + " does not exist");
        	}
        		
        }
        s=s+"]";
        return s;
    }

	public boolean equals(Bid pBid) {
		//System.out.println("comparing "+this+" with "+pBid);
		/*
		 * for(int i=0;i<fValues.length;i++) {
		 * if(!fValues[i].equals(pBid.getValue(i))) { return false; } }
		 */
		return fValues.equals(pBid.getValues());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Bid)
			return equals((Bid)obj);
		return false;
	}
	
	/**
	 * Helper function to enable the comparison between two Bids.
	 * Wouter: changed to public for convenience.
	 * @return
	 */
	protected HashMap<Integer, Value> getValues() {
		 // create a clone, to avoid changing of the values.
		return fValues;
	}
	

	// DOES NO LONGER APPLY
	// public String indexesToString() {
	// String result ="";
	// for(int i=0;i<fValues.length;i++) {
	// result += String.valueOf(fValues[i])+";";
	// }
	// return result;
	// }
	// TODO re-do the save/load XML for bids
	public Bid(Domain pDomain, SimpleElement pXMLBid) throws Exception
	{
		fDomain = pDomain;
		fValues = new HashMap<Integer,Value>();
		// fValuesIndexes = new int[pDomain.getNumberOfIssues()];
		Object[] lXMLIssues = (pXMLBid.getChildByTagName("issue"));
		Value lValue = null;
		SimpleElement lXMLItem;
		String lTmp;
		for (int i = 0; i < lXMLIssues.length; i++) {
			String indexStr = ((SimpleElement) lXMLIssues[i])
					.getAttribute("index"); // find the index of this Issue.
			int ind = new Integer(indexStr);
			switch (fDomain.getObjective(ind).getType()) {
			case DISCRETE:
				lXMLItem = (SimpleElement) (((SimpleElement) lXMLIssues[i])
						.getChildByTagName("item"))[0];
				lTmp = lXMLItem.getAttribute("value");
				// fValuesIndexes[Integer.valueOf(((SimpleElement)lXMLIssues[i]).getAttribute("index"))-1]
				// =
				// Integer.valueOf();
				lValue = new ValueDiscrete(lTmp);
				break;
			case INTEGER:
				lXMLItem = (SimpleElement) (((SimpleElement) lXMLIssues[i])
						.getChildByTagName("value"))[0];
				lTmp = lXMLItem.getText();
				lValue = new ValueInteger(Integer.valueOf(lTmp));
				break;

			// case PRICE:
			// lXMLItem =
			// (SimpleElement)(((SimpleElement)lXMLIssues[i]).getChildByTagName("value"))[0];
			// lTmp = lXMLItem.getText();
			// lValue = new ValuePrice(Double.valueOf(lTmp));
			// break;
			case REAL:
				lXMLItem = (SimpleElement) (((SimpleElement) lXMLIssues[i])
						.getChildByTagName("value"))[0];
				lTmp = lXMLItem.getText();
				lValue = new ValueReal(Double.valueOf(lTmp));
				break;
			case OBJECTIVE:
				//TODO something with objectives.
				//for now, do nothing. Objectives do not enter into bids.
				break;
				
			// TODO:COMPLETED: DT implement Bid(Domain, SimpleElement) in Bid
			// for the rest of the issue/value types
			// TODO: DT add bid validation w.r.t. Domain, throw an exception
			// BidDoesNotExist
			}// switch
			fValues.put(ind, lValue);
		}
	}

	public SimpleElement toXML() {

		SimpleElement lXMLBid = new SimpleElement("bid");

		// TODO hdv rewrite this to use the hashmap.

		for(Issue lIssue : fDomain.getIssues()) {

			Value lVal = fValues.get(lIssue.getNumber());
			SimpleElement lXMLIssue = new SimpleElement("issue");
			lXMLIssue.setAttribute("type", lIssue.convertToString());
			lXMLIssue.setAttribute("index", String.valueOf(lIssue.getNumber()));
			lXMLBid.addChildElement(lXMLIssue); SimpleElement lXMLItem=null;		 
			switch(lVal.getType()) { 
			case DISCRETE: 
				ValueDiscrete lDiscVal = (ValueDiscrete)(lVal); 
				lXMLItem = new SimpleElement("item");		  
				lXMLItem.setAttribute("value", lDiscVal.getValue()); 
				break; 
				//TODO:/COMPLETE DT implement toXML method in Bid for the rest of theissue/value types 
			case INTEGER: 
				ValueInteger lIntVal =(ValueInteger)(lVal); 
				lXMLItem = new SimpleElement("value");
				lXMLItem.setText(String.valueOf(lIntVal.getValue())); 
				break; 
			case REAL: 
				ValueReal lRealVal = (ValueReal)(lVal); 
				lXMLItem = new  SimpleElement("value");
				lXMLItem.setText(String.valueOf(lRealVal.getValue())); 
				break; 
				// case PRICE: 
				// ValueReal lPriceVal = (ValueReal)(fValues[i]); 
				// lXMLValue =  new SimpleElement("value"); 
				// lXMLValue.setText(String.valueOf(lPriceVal.getValue())); 
				// break; 
			}//switch
			lXMLIssue.addChildElement(lXMLItem); 
		}
		return lXMLBid;
	}
	// TODO can we save indexes to Strings?
	/*
	 * public String indexesToString() { String result =""; for(int i=0;i<fValuesIndexes.length;i++) {
	 * result += String.valueOf(fValuesIndexes[i])+";"; } return result; }
	 */


	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int code=0;
		for(Entry<Integer, Value> lEntry : fValues.entrySet()) {
			code = code + lEntry.getValue().hashCode();  
		}
		return code;//fValues.hashCode();
	}
	
}
class MyMapAdapter extends XmlAdapter<Temp,Map<Integer,Value>> {

	@Override
	public Temp marshal(Map<Integer, Value> arg0) throws Exception {
		Temp temp = new Temp();
		for(Entry<Integer, Value> entry : arg0.entrySet()){
			temp.entry.add(new Item(entry.getKey(), entry.getValue()));
		}
		return temp;
	}

	@Override
	public Map<Integer, Value> unmarshal(Temp arg0) throws Exception {
		Map<Integer, Value> map = new HashMap<Integer, Value>();
		for(Item item: arg0.entry) {
			map.put(item.key, item.value);
		}
		return map;
	}

}
class Temp {
  @XmlElement(name="issue")  
  public List<Item> entry ;
  
  public Temp(){entry = new ArrayList<Item>();}
   
}
@XmlRootElement
class Item {
	  @XmlAttribute(name="index")
	  public Integer key;
	  	
	  @XmlElement	  
	  public Value value;
	  
	  public Item(){ }
	  public Item(Integer key, Value val) {
		  this.key = key;
		  this.value = val;
	  }
	}


