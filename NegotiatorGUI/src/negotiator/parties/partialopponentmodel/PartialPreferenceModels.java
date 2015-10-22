package negotiator.parties.partialopponentmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import negotiator.Bid;
import negotiator.Feedback;
import negotiator.issue.Issue;
import negotiator.issue.Value;

public class PartialPreferenceModels {

	private ArrayList<ValuePreferenceGraphMap> preferenceOrderingMap; // a
																		// collection
																		// of
																		// models
																		// for
																		// each
																		// agent
	private ArrayList<Issue> issues;

	public PartialPreferenceModels(Bid firstBid, int numberOfParties) { // maybe
																		// we
																		// can
																		// get
																		// the
																		// bid
																		// directly
																		// and
																		// use
																		// also
																		// domain
																		// knowledge
																		// later...

		preferenceOrderingMap = new ArrayList<ValuePreferenceGraphMap>(
				numberOfParties);
		issues = firstBid.getIssues();
		for (int i = 0; i < numberOfParties; i++)
			preferenceOrderingMap.add(new ValuePreferenceGraphMap(firstBid));
	}

	public void updateIssuePreferenceList(int issueIndex, Value previousValue,
			Value currentValue, ArrayList<Feedback> feedback) {

		for (int i = 0; i < feedback.size(); i++)
			preferenceOrderingMap.get(i).updateValuePreferenceGraph(issueIndex,
					previousValue, currentValue, feedback.get(i));
	}

	public boolean mayImproveMajority(int issueIndex, Value previousValue,
			Value newValue) {

		int count = 0;

		for (ValuePreferenceGraphMap partyPreferenceMap : preferenceOrderingMap) {

			if (partyPreferenceMap.isLessPreferredThan(issueIndex, newValue,
					previousValue))
				count++;
		}

		if (count < ((double) preferenceOrderingMap.size() / 2))
			return true;
		else
			return false;
	}

	public boolean mayImproveAll(int issueIndex, Value previousValue,
			Value newValue) {

		for (ValuePreferenceGraphMap partyPreferenceMap : preferenceOrderingMap) {

			if (partyPreferenceMap.isLessPreferredThan(issueIndex, newValue,
					previousValue))
				return false;
		}
		return false;
	}

	public Value getNashValue(int issueIndex) {

		Value nashValue = null;
		double nashProduct = -1.0;
		double currentProduct;

		if (preferenceOrderingMap.get(0).getAllValues(issueIndex).size() == 1) // if
																				// there
																				// exists
																				// only
																				// one
																				// value,
																				// return
																				// it.
			return preferenceOrderingMap.get(0).getAllValues(issueIndex).get(0);

		for (Value currentValue : preferenceOrderingMap.get(0).getAllValues(
				issueIndex)) {

			currentProduct = 1.0;
			for (int i = 0; i < preferenceOrderingMap.size(); i++) {
				currentProduct *= preferenceOrderingMap.get(i)
						.getEstimatedUtility(issueIndex, currentValue);
			}
			if (currentProduct > nashProduct) {
				nashProduct = currentProduct;
				nashValue = currentValue;
			}
		}

		return nashValue;

	}

	public ArrayList<Value> getNashValues(int issueIndex) {

		ArrayList<Value> nashValues = new ArrayList<Value>();
		double nashProduct = -1;
		double currentProduct;

		if (preferenceOrderingMap.get(0).getAllValues(issueIndex).size() == 1) // if
																				// there
																				// exists
																				// only
																				// one
																				// value,
																				// return
																				// it.
			nashValues.add(preferenceOrderingMap.get(0)
					.getAllValues(issueIndex).get(0));
		else {

			for (Value currentValue : preferenceOrderingMap.get(0)
					.getAllValues(issueIndex)) {

				currentProduct = 1.0;
				for (int i = 0; i < preferenceOrderingMap.size(); i++) {
					currentProduct *= preferenceOrderingMap.get(i)
							.getEstimatedUtility(issueIndex, currentValue);
				}
				if (currentProduct > nashProduct) {
					nashValues.clear();
					nashProduct = currentProduct;
					nashValues.add(currentValue);
				} else if (currentProduct == nashProduct)
					nashValues.add(currentValue);
			}
		}

		return nashValues;

	}

	public double estimateSumUtility(Bid currentBid) throws Exception {

		double utility = 0.0;
		for (int i = 0; i < preferenceOrderingMap.size(); i++) {
			utility += preferenceOrderingMap.get(i).estimateUtility(currentBid);
		}
		return utility;

	}

	public double estimateProductUtility(Bid currentBid) throws Exception {

		double utility = 1;
		for (int i = 0; i < preferenceOrderingMap.size(); i++) {
			utility *= preferenceOrderingMap.get(i).estimateUtility(currentBid);
		}
		return utility;

	}

	// because of the time constraint, I wrote the simple sorting but not
	// efficient (since there are no much nash bids, it will not be a problem)
	public void sortBidsWrtSumUtility(ArrayList<Bid> bidList) throws Exception {

		for (int i = 0; i < bidList.size(); i++) {

			for (int j = i + 1; j < bidList.size(); j++) {

				if (estimateSumUtility(bidList.get(i)) < estimateSumUtility(bidList
						.get(j))) {
					Bid temp = new Bid(bidList.get(i));
					bidList.set(i, bidList.get(j));
					bidList.set(j, temp);
				}

			}
		}
	}

	// because of the time constraint, I wrote the simple sorting but not
	// efficient (since there are no much nash bids, it will not be a problem)
	public void sortBidsWrtProductUtility(ArrayList<Bid> bidList)
			throws Exception {

		for (int i = 0; i < bidList.size(); i++) {

			for (int j = i + 1; j < bidList.size(); j++) {

				if (estimateProductUtility(bidList.get(i)) < estimateProductUtility(bidList
						.get(j))) {
					Bid temp = new Bid(bidList.get(i));
					bidList.set(i, bidList.get(j));
					bidList.set(j, temp);
				}

			}
		}
	}

	public ArrayList<Bid> estimatePossibleNashBids(Bid sampleBid)
			throws Exception {

		ArrayList<Bid> nashBids = new ArrayList<Bid>();
		HashMap<Integer, ArrayList<Value>> nashIssueValues = new HashMap<Integer, ArrayList<Value>>();

		Bid firstBid = new Bid(sampleBid);

		for (Issue currentIssue : issues) {
			nashIssueValues.put(currentIssue.getNumber(),
					getNashValues(currentIssue.getNumber()));
			firstBid = firstBid.putValue(currentIssue.getNumber(),
					nashIssueValues.get(currentIssue.getNumber()).get(0));
		}

		nashBids.add(firstBid);

		int currentIndex;
		for (Issue currentIssue : issues) {
			currentIndex = currentIssue.getNumber();

			for (Value currentValue : nashIssueValues.get(currentIndex)) {

				for (int i = 0; i < nashBids.size(); i++) {
					Bid currentBid = new Bid(nashBids.get(i));
					currentBid = currentBid
							.putValue(currentIndex, currentValue);
					if (!nashBids.contains(currentBid))
						nashBids.add(currentBid);
				}
			}

		}

		sortBidsWrtSumUtility(nashBids);
		return nashBids;
	}

	public ArrayList<Value> getIncomparableValues(int issueIndex,
			Value currentValue) {

		ArrayList<Value> incomparableValues = new ArrayList<Value>();
		Value incomparable;
		for (int i = 0; i < preferenceOrderingMap.size(); i++) {
			incomparable = preferenceOrderingMap.get(i).getIncomparableValue(
					issueIndex, currentValue);
			if (incomparable != null)
				incomparableValues.add(incomparable);
		}

		return incomparableValues;

	}

	public Value getIncomparableValue(int issueIndex, Value currentValue,
			Random random) {

		ArrayList<Value> incomparableValues = getIncomparableValues(issueIndex,
				currentValue);

		if (incomparableValues.size() == 0)
			return null;

		return (incomparableValues
				.get(random.nextInt(incomparableValues.size())));

	}

	public ArrayList<Value> getAllPossibleValues(int issueIndex) {
		return preferenceOrderingMap.get(0).getAllValues(issueIndex);
	}

	public Value getMissingValue(int issueIndex) {
		return preferenceOrderingMap.get(0).getMissingValue(issueIndex);
	}

	public ValuePreferenceGraphMap getValuePreferenceMap(int partyNo) {
		return preferenceOrderingMap.get(partyNo);
	}

	@Override
	public String toString() {

		StringBuffer buffy = new StringBuffer("Partial Preference Model");

		for (int i = 0; i < preferenceOrderingMap.size(); i++) {
			buffy.append("\n For party -" + i + "\n");
			buffy.append(preferenceOrderingMap.get(i).toString());
		}

		return (buffy.toString());

	}

}