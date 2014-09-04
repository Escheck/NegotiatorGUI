package negotiator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
 * 
 * Wouter: a bid is a set of <idnumber,value> pairs, where idnumber is the
 * unique number of the issue, and value is the picked alternative.
 * 
 * @author Dmytro Tykhonov & Koen Hindriks
 */
@XmlRootElement
public final class Bid implements XMLable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7723017380013100614L;
	// Class fields
	Domain fDomain;
	@XmlElement(name = "values")
	@XmlJavaTypeAdapter(MyMapAdapter.class)
	private HashMap<Integer, Value> fValues; // Wouter: the bid values for each
												// IssueID

	/**
	 * Create a new empty bid of which the values still must be set.
	 */
	public Bid() {
		fValues = new HashMap<Integer, Value>();
	}

	/**
	 * create a new bid in a domain. Partially checks the validity of the bid as
	 * well There is only this constructor because we require that ALL values in
	 * the domain get assigned a value.
	 * 
	 * @param domainP
	 *            the domain in which the bid is done
	 * @param bidP
	 *            HashMap, which is a set of pairs <issueID,value>
	 * @throws Exception
	 *             if the bid is not a legal bid in the domain.
	 */
	public Bid(Domain domainP, HashMap<Integer, Value> bidP) throws Exception {
		this.fDomain = domainP; // THIS NEEDS A CHECK!

		// Check if indexes are ok
		// Discussion with Dmytro 16oct 1200: it is possible to do only a
		// partial bid, leaving
		// part of the issues un-set. But each issue being bidded on has to
		// exist in the domain,
		// and this is what we check here.
		// Discussion 16oct 16:03: No, ALL values have to be set.
		// Discussion 19oct: probably there only is this particular constructor
		// because
		// that enables to enforce this.
		// ArrayList<Issue> issues=domainP.getIssues();
		/*
		 * for (Issue issue:issues) if (bidP.get(new
		 * Integer(issue.getNumber()))==null) throw new
		 * BidDoesNotExistInDomainException(
		 * "bid for issue '"+issue.getName()+"' (issue #"
		 * +issue.getNumber()+") lacks");
		 */
		fValues = bidP;
	}

	/**
	 * This method clones the given bid.
	 */
	public Bid(Bid bid) {

		fDomain = bid.fDomain;
		fValues = (HashMap<Integer, Value>) bid.fValues.clone();
	}

	/**
	 * @param issueNr
	 *            number of an issue.
	 * @return the picked value for given issue idnumber
	 * @throws Exception
	 *             if there exist no issue with the given number.
	 */
	public Value getValue(int issueNr) throws Exception {
		Value v = fValues.get(issueNr);
		if (v == null) {
			if (fDomain.getIssue(issueNr) == null)
				throw new Exception("Bid.getValue: issue " + issueNr
						+ " does not exist at all");
			throw new Exception("There is no evaluator for issue " + issueNr);
		}
		return v;
	}

	/**
	 * Set the value of the issue with the given issueID to the given value.
	 * 
	 * @param issueId
	 *            unique ID of an issue.
	 * @param pValue
	 *            value of the issue.
	 */
	public void setValue(int issueId, Value pValue) {
		if (fValues.get(issueId).getType() == pValue.getType()) {
			fValues.put(issueId, pValue);
		}
	}

	public String toString() {
		String s = "Bid[";
		Set<Entry<Integer, Value>> value_set = fValues.entrySet();
		Iterator<Entry<Integer, Value>> value_it = value_set.iterator();
		while (value_it.hasNext()) {
			int ind = ((Entry<Integer, Value>) value_it.next()).getKey();
			Object tmpobj = fDomain.getObjective(ind); // Objective isn't
														// recognized here, GKW.
														// hdv
			if (tmpobj != null) {
				String nm = fDomain.getObjective(ind).getName();
				s += nm + ": " + fValues.get(ind) + ", ";
			} else {
				System.out.println("objective with index " + ind
						+ " does not exist");
			}

		}
		s = s + "]";
		return s;
	}

	/**
	 * @param pBid
	 *            to which this bid must be compared.
	 * @return true if the values of this and the given bid are equal.
	 */
	public boolean equals(Bid pBid) {
		return fValues.equals(pBid.getValues());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Bid)
			return equals((Bid) obj);
		return false;
	}

	/**
	 * Helper function to enable the comparison between two Bids. Wouter:
	 * changed to public for convenience.
	 * 
	 * @return
	 */

	// Reyhan: Changed public (it was "protected")
	public HashMap<Integer, Value> getValues() {
		// create a clone, to avoid changing of the values.
		return fValues;
	}

	// Reyhan: add this method
	public ArrayList<Issue> getIssues() {
		return fDomain.getIssues();
	}

	public SimpleElement toXML() {

		SimpleElement lXMLBid = new SimpleElement("bid");

		// TODO hdv rewrite this to use the hashmap.

		for (Issue lIssue : fDomain.getIssues()) {

			Value lVal = fValues.get(lIssue.getNumber());
			SimpleElement lXMLIssue = new SimpleElement("issue");
			lXMLIssue.setAttribute("type", lIssue.convertToString());
			lXMLIssue.setAttribute("index", String.valueOf(lIssue.getNumber()));
			lXMLBid.addChildElement(lXMLIssue);
			SimpleElement lXMLItem = null;
			switch (lVal.getType()) {
			case DISCRETE:
				ValueDiscrete lDiscVal = (ValueDiscrete) (lVal);
				lXMLItem = new SimpleElement("item");
				lXMLItem.setAttribute("value", lDiscVal.getValue());
				break;
			// TODO:/COMPLETE DT implement toXML method in Bid for the rest of
			// theissue/value types
			case INTEGER:
				ValueInteger lIntVal = (ValueInteger) (lVal);
				lXMLItem = new SimpleElement("value");
				lXMLItem.setText(String.valueOf(lIntVal.getValue()));
				break;
			case REAL:
				ValueReal lRealVal = (ValueReal) (lVal);
				lXMLItem = new SimpleElement("value");
				lXMLItem.setText(String.valueOf(lRealVal.getValue()));
				break;
			}// switch
			lXMLIssue.addChildElement(lXMLItem);
		}
		return lXMLBid;
	}

	// TODO can we save indexes to Strings?
	/*
	 * public String indexesToString() { String result =""; for(int
	 * i=0;i<fValuesIndexes.length;i++) { result +=
	 * String.valueOf(fValuesIndexes[i])+";"; } return result; }
	 */

	@Override
	public int hashCode() {
		int code = 0;
		for (Entry<Integer, Value> lEntry : fValues.entrySet()) {
			code = code + lEntry.getValue().hashCode();
		}
		return code;// fValues.hashCode();
	}

	/**
	 * Calculates similarity with another bid. Similarity is measured by
	 * counting the amount of values that are equal across both bids and then
	 * dividing that by the total amount of values in both bids. A similarity of
	 * 1.0 indicates that the bids are one and the same, and a similarity of 0.0
	 * indicates that they are completely different.
	 * 
	 * @param other
	 *            The other bid to compare with.
	 * @return value between 0 and 1. 1 means totally the same bid, 0 totally
	 *         different.
	 */
	public double getSimilarity(Bid other) {
		double sum = 0.0;
		double total = 0.0;
		Set<Entry<Integer, Value>> value_set_this = fValues.entrySet();
		Iterator<Entry<Integer, Value>> value_it_this = value_set_this
				.iterator();
		Set<Entry<Integer, Value>> value_set_other = other.fValues.entrySet();
		Iterator<Entry<Integer, Value>> value_it_other = value_set_other
				.iterator();
		while (value_it_this.hasNext()) {
			int ind = ((Entry<Integer, Value>) value_it_this.next()).getKey();
			((Entry<Integer, Value>) value_it_other.next()).getKey();
			// Objective isn't recognized here, GKW. hdv
			Object tmpobj = fDomain.getObjective(ind);
			if (tmpobj != null) {
				if (this.fValues.get(ind).equals(other.fValues.get(ind))) {
					sum += 1.0;
				}
				total += 1.0;
			} else {
				System.out.println("objective with index " + ind
						+ " does not exist");
			}

		}
		if (total == 0.0) {
			return 0.0;
		}
		return sum / total;

	}

}

class MyMapAdapter extends XmlAdapter<Temp, Map<Integer, Value>> {

	@Override
	public Temp marshal(Map<Integer, Value> arg0) throws Exception {
		Temp temp = new Temp();
		for (Entry<Integer, Value> entry : arg0.entrySet()) {
			temp.entry.add(new Item(entry.getKey(), entry.getValue()));
		}
		return temp;
	}

	@Override
	public Map<Integer, Value> unmarshal(Temp arg0) throws Exception {
		Map<Integer, Value> map = new HashMap<Integer, Value>();
		for (Item item : arg0.entry) {
			map.put(item.key, item.value);
		}
		return map;
	}

}

class Temp {
	@XmlElement(name = "issue")
	public List<Item> entry;

	public Temp() {
		entry = new ArrayList<Item>();
	}

}

@XmlRootElement
class Item {
	@XmlAttribute(name = "index")
	public Integer key;

	@XmlElement
	public Value value;

	public Item() {
	}

	public Item(Integer key, Value val) {
		this.key = key;
		this.value = val;
	}
}
