package negotiator.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.Timeline;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Objective;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;

/**
 * The utility space couples all objectives to weights and evaluators. A
 * utilityspace currently is not bound to one agent.
 * 
 * @author D. Tykhonov, K. Hindriks, W. Pasman
 */
public class UtilitySpace implements Serializable {

	// Class fields
	protected Domain domain;
	protected String fileName;
	protected UTILITYSPACETYPE spaceType;
	private double discountFactor = 1;

	private Double fReservationValue = null;
	// Added by Dmytro: I need the XMLRoot for the utility space to load the
	// Similarity functions
	// in the Similarity agent
	protected SimpleElement fXMLRoot;
	private Map<Objective, Evaluator> fEvaluators; // changed to use Objective.
													// TODO check casts.

	/**
	 * Creates an empty utility space.
	 */
	public UtilitySpace() {
		this(new Domain(), new HashMap<Objective, Evaluator>());
	}

	/**
	 * Creates a new utilityspace of the given domain.
	 * 
	 * @param domain
	 *            for which the utilityspace should be specified.
	 */
	public UtilitySpace(Domain domain) {
		this(domain, new HashMap<Objective, Evaluator>());
	}

	public UtilitySpace(Domain domain, Map<Objective, Evaluator> fEvaluators) {
		this.domain = domain;
		this.fEvaluators = fEvaluators;
	}

	/**
	 * Create new default util space for a given domain.
	 * 
	 * @param domain
	 * @param fileName
	 *            to read domain from. Set fileName to "" if no file available,
	 *            in which case default evaluators are loaded..
	 * @throws Exception
	 *             if error occurs, e.g. if domain does not match the util
	 *             space, or file not found.
	 */
	public UtilitySpace(Domain domain, String fileName) throws Exception {
		this.domain = domain;
		this.fileName = fileName;
		fEvaluators = new HashMap<Objective, Evaluator>();
		if (!fileName.equals(""))
			loadTreeFromFile(fileName);
		else { // add evaluator to all objectives
			ArrayList<Objective> objectives = domain.getObjectives();
			for (Objective obj : objectives) {
				Evaluator eval = defaultEvaluator(obj);
				fEvaluators.put(obj, eval);
			}

		}
	}

	/**
	 * Copies the data from another UtilitySpace.
	 * 
	 * @param us
	 *            utility space to be cloned.
	 */
	public UtilitySpace(UtilitySpace us) {
		domain = us.getDomain();
		fileName = us.getFileName();
		fEvaluators = new HashMap<Objective, Evaluator>();
		fReservationValue = us.getReservationValue();
		// and clone the evaluators
		for (Objective obj : domain.getObjectives()) {
			Evaluator e = us.getEvaluator(obj.getNumber());
			if (e != null)
				fEvaluators.put(obj, e.clone());
			// else incomplete. But that is allowed I think.
			// especially, objectives (the non-Issues) won't generally have an
			// evlauator.
		}
		fXMLRoot = us.getXMLRoot();
		discountFactor = us.discountFactor;
	}

	/**
	 * This method returns the type of utility space: either linear or
	 * nonlinear. May also return null, this means linear FAIK.
	 */
	public UTILITYSPACETYPE getType() {

		return this.spaceType;
	}

	/**
	 * createFrom a default evaluator for a given Objective. This function is placed
	 * here, and not in Objective, because the Objectives should not be loaded
	 * with utility space functionality. The price we pay for that is that we
	 * now have an ugly switch inside the code, losing some modularity.
	 * 
	 * @param obj
	 *            the objective to createFrom an evaluator for
	 * @return the default evaluator
	 */
	private Evaluator defaultEvaluator(Objective obj) {
		if (obj.isObjective())
			return new EvaluatorObjective();
		// if not an objective then it must be an issue.
		switch (((Issue) obj).getType()) {
		case DISCRETE:
			return new EvaluatorDiscrete();
		case INTEGER:
			return new EvaluatorInteger();
		case REAL:
			return new EvaluatorReal();
		default:
			System.out.println("INTERNAL ERROR: issue of type "
					+ ((Issue) obj).getType() + "has no default evaluator");
		}
		return null;
	}

	/**
	 * Checks the normalization throughout the tree. Will eventually replace
	 * checkNormalization
	 * 
	 * @return true if the weigths are indeed normalized, false if they aren't.
	 */
	private boolean checkTreeNormalization() {
		return checkTreeNormalizationRecursive(domain.getObjectivesRoot());
	}

	protected void normalizeWeights(Objective currentRoot) {
		double lSum = 0;

		Enumeration<Objective> children = currentRoot.children();

		// Wouter: there is nothing recursive here. This function seems broken
		while (children.hasMoreElements()) {

			Objective tmpObj = children.nextElement();
			lSum += (fEvaluators.get(tmpObj)).getWeight();

		}
		children = currentRoot.children();

		// Wouter: there is nothing recursive here. This function seems broken
		while (children.hasMoreElements()) {

			Objective tmpObj = children.nextElement();
			double weight = (1 - lSum) * (fEvaluators.get(tmpObj)).getWeight(); // RA:
																				// It
																				// should
																				// be
																				// (1/lSum)
			(fEvaluators.get(tmpObj)).setWeight(weight);

		}
	}

	/**
	 * Private helper function to check the normalisation throughout the tree.
	 * 
	 * @param currentRoot
	 *            The current parent node of the subtree we are going to check
	 * @return True if the weights are indeed normalized, false if they aren't.
	 */
	private boolean checkTreeNormalizationRecursive(Objective currentRoot) {
		boolean normalised = true;
		double lSum = 0;

		Enumeration<Objective> children = currentRoot.children();

		// Wouter: there is nothing recursive here. This function seems broken
		while (children.hasMoreElements() && normalised) {

			Objective tmpObj = children.nextElement();
			lSum += (fEvaluators.get(tmpObj)).getWeight();

		}
		// System.out.println("sum="+lSum);
		return (normalised && lSum > .98 && lSum < 1.02);
	}

	/**
	 * Check if this utility space is ready for negotiation.
	 * 
	 * @param dom
	 *            is the domain in which nego is taking place
	 * @throws Exception
	 *             if utility space is incomplete (@see isComplete());
	 */
	public void checkReadyForNegotiation(Domain dom) throws Exception {
		// check if utility spaces are instance of the domain
		// following checks normally succeed, as the domain of the domain space
		// is enforced in the loader.
		if (!(dom.equals(domain)))
			throw new Exception("domain does not match the negotiation domain");
		String err = isComplete();
		if (err != null)
			throw new Exception("utility space '" + fileName
					+ "' is incomplete\n" + err);
	}

	/**
	 * @return XML root of this utilityspace.
	 */
	protected SimpleElement getXMLRoot() {
		return fXMLRoot;
	}

	/**
	 * @return number of issues. This can only be used for linear utility
	 *         functions.
	 */
	public final int getNrOfEvaluators() {
		checkForLinearSpaceType();
		return fEvaluators.size();
	}

	/**
	 * Returns the evaluator of an issue for the given index. This can only be
	 * used for linear utility functions.
	 * 
	 * @param index
	 *            The IDnumber of the Objective or Issue
	 * @return An Evaluator for the Objective or Issue.
	 */
	public final Evaluator getEvaluator(int index) {
//		checkForLinearSpaceType();
		Objective obj = domain.getObjective(index); // Used to be Issue in stead
													// of Objective
		if (obj != null) {
			return fEvaluators.get(obj);
		} else
			return null;
	}

	/**
	 * Throws an exception is the the space type is not linear. This method is
	 * necessary because some parts of this class assume we work with weights
	 * and evaluators.
	 */
	private void checkForLinearSpaceType() {
		if (getType() == null)
			return;

		if (getType() != UTILITYSPACETYPE.LINEAR && getType() != UTILITYSPACETYPE.CONSTRAINT)
			throw new IllegalStateException(
					"This method is to be used for linear utility spaces only. This space is "
							+ getType());
	}

	/**
	 * @param bid
	 *            of which we are interested in its utility.
	 * @return Undiscounted utility of the given bid. Works for any utility
	 *         space.
	 * @throws Exception
	 *             when bid is incomplete or invalid.
	 */
	public double getUtility(Bid bid) throws Exception {
		EVALUATORTYPE type;
		double utility = 0, financialUtility = 0, financialRat = 0;

		Objective root = domain.getObjectivesRoot();
		Enumeration<Objective> issueEnum = root.getPreorderIssueEnumeration();
		while (issueEnum.hasMoreElements()) {
			Objective is = issueEnum.nextElement();
			Evaluator eval = fEvaluators.get(is);
			type = eval.getType();
			switch (type) {
			case DISCRETE:
			case INTEGER:
			case REAL:
				// System.out.println(is + " weight = " + eval.getWeight() +
				// " with value " + bid.getValue(is.getNumber()) + " -> " +
				// getEvaluation(is.getNumber(), bid));
				utility += eval.getWeight()
						* getEvaluation(is.getNumber(), bid);
				break;
			}
		}
		double result = financialRat * financialUtility + (1 - financialRat)
				* utility;
		if (result > 1)
			return 1;
		return result;
	}

	/**
	 * Let d in (0, 1) be the discount factor. (If d <= 0 or d >= 1, we assume
	 * that d = 1.) Let t in [0, 1] be the current time, as defined by the
	 * {@link Timeline}. We compute the <i>discounted</i> utility
	 * discountedUtility as follows:
	 * 
	 * discountedUtility = originalUtility * d^t.
	 * 
	 * For t = 0 the utility remains unchanged, and for t = 1 the original
	 * utility is multiplied by the discount factor. The effect is almost linear
	 * in between. Works with any utility space.
	 * 
	 * @param bid
	 *            of which we are interested in its utility.
	 * @param timeline
	 *            indicating the time passed in the negotiation.
	 * @return discounted utility.
	 */
	public double getUtilityWithDiscount(Bid bid, Timeline timeline) {
		double time = timeline.getTime();
		return getUtilityWithDiscount(bid, time);
	}

	/**
	 * @see #getUtilityWithDiscount(Bid, Timeline)
	 * @param bid
	 *            of which we want to know the utility at the given time.
	 * @param time
	 *            at which we want to know the utility of the bid.
	 * @return discounted utility.
	 */
	public double getUtilityWithDiscount(Bid bid, double time) {
		double util = 0;
		try {
			util = getUtility(bid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		double discountedUtil = discount(util, time);
		// System.out.println(util + " * " + discount + "^" + time + " = " +
		// discountedUtil);
		return discountedUtil;
	}

	/**
	 * Computes:
	 * 
	 * discountedUtil = util * Math.pow(discount, time).
	 * 
	 * Checks for bounds on the discount factor and time.
	 * 
	 * @param util
	 *            undiscounted utility.
	 * @param time
	 *            at which we want to know the discounted utility.
	 * @param discountFactor
	 *            of the preference profile.
	 * @return discounted version of the given utility at the given time.
	 */
	public static double discount(double util, double time,
			double discountFactor) {
		double discount = discountFactor;
		if (time < 0) {
			System.err.println("Warning: time = " + time
					+ " < 0, using time = 0 instead.");
			time = 0;
		}
		if (time > 1) {
			System.err.println("Warning: time = " + time
					+ " > 1, using time = 1 instead.");
			time = 1;
		}

		double discountedUtil = util * Math.pow(discount, time);
		return discountedUtil;
	}

	/**
	 * Computes:
	 * 
	 * discountedUtil = util * Math.pow(discount, time).
	 * 
	 * Checks for bounds on the discount factor and time.
	 */
	protected double discount(double util, double time) {
		return discount(util, time, discountFactor);
	}

	/**
	 * Returns the utility of one issue in the bid. Note that this value is in
	 * the range [0,1] as it is not normalized by the issue weight. Only works
	 * with linear utility spaces.
	 * 
	 * @param pIssueIndex
	 *            of the issue.
	 * @param bid
	 * @return evaluation of the value of the issue of the given bid.
	 * @throws Exception
	 *             if the bid or value is null.
	 */
	public final double getEvaluation(int pIssueIndex, Bid bid)
			throws Exception {
		checkForLinearSpaceType();

		Object lObj = getDomain().getObjective(pIssueIndex);
		Evaluator lEvaluator = fEvaluators.get(lObj);

		return lEvaluator.getEvaluation(this, bid, pIssueIndex);
	}

	/**
	 * Returns the maximum bid in the utility space. This is only supported for
	 * linear utility spaces. Totally revised, brute-force search now.
	 * 
	 * @return a bid with the maximum utility value attainable in this util
	 *         space
	 * @throws Exception
	 *             if there is no bid at all in this util space.
	 */
	public final Bid getMaxUtilityBid() throws Exception {
		checkForLinearSpaceType();
		Bid maxBid = null;
		double maxutil = 0.;
		BidIterator bidit = new BidIterator(domain);

		if (!bidit.hasNext())
			throw new Exception("The domain does not contain any bids!");
		while (bidit.hasNext()) {
			Bid thisBid = bidit.next();
			double thisutil = getUtility(thisBid);
			if (thisutil > maxutil) {
				maxutil = thisutil;
				maxBid = thisBid;
			}
		}
		return maxBid;
	}

	/**
	 * Returns the worst bid in the utility space. This is only supported for
	 * linear utility spaces.
	 * 
	 * @return a bid with the lowest possible utility
	 * @throws Exception
	 *             if there is no bid at all in the util space
	 */
	public Bid getMinUtilityBid() throws Exception {
		checkForLinearSpaceType();
		Bid minBid = null;
		double minUtil = 1.2;
		BidIterator bidit = new BidIterator(domain);

		if (!bidit.hasNext())
			throw new Exception("The domain does not contain any bids!");
		while (bidit.hasNext()) {
			Bid thisBid = bidit.next();
			double thisutil = getUtility(thisBid);
			if (thisutil < minUtil) {
				minUtil = thisutil;
				minBid = thisBid;
			}
		}
		return minBid;
	}

	/**
	 * @param filename
	 *            The name of the xml file to parse.
	 * @throws exception
	 *             if error occurs, e.g. file not found
	 */
	private final boolean loadTreeFromFile(String filename) throws Exception {
		SimpleDOMParser parser = new SimpleDOMParser();
		BufferedReader file = new BufferedReader(new FileReader(new File(
				filename)));
		SimpleElement root = parser.parse(file);
		fXMLRoot = root;
		return loadTreeRecursive(root);
	}

	/**
	 * Loads the weights and issues for the evaluators.
	 * 
	 * @param root
	 *            The current root of the XML structure.
	 */
	private final boolean loadTreeRecursive(SimpleElement currentRoot) {
		// TODO hdevos:
		// We get an Objective or issue from the SimpleElement structure,
		// get it's number of children:
		int nrOfWeights = 0;
		// String what = currentRoot.getTagName();
		/*
		 * if(!what.equals("Objective") || !what.equals("utility_space")){ //are
		 * the only two tags that can have weights
		 * loadTreeRecursive((SimpleElement
		 * )(currentRoot.getChildElements())[0]); //It's the utility_space tag.
		 * Ignore. }
		 */
		// TODO hdevos: find a way of checking the number of issues in the
		// Domain versus the number of issues in the UtilitySpace

		int index;
		// load reservation value
		try {
			if ((currentRoot.getChildByTagName("reservation") != null)
					&& (currentRoot.getChildByTagName("reservation").length > 0)) {
				SimpleElement xml_reservation = (SimpleElement) (currentRoot
						.getChildByTagName("reservation")[0]);
				fReservationValue = Double.valueOf(xml_reservation
						.getAttribute("value"));
			}
		} catch (Exception e) {
			System.out.println("Utility space has no reservation value");
		}
		// load discount factor
		try {
			if ((currentRoot.getChildByTagName("discount_factor") != null)
					&& (currentRoot.getChildByTagName("discount_factor").length > 0)) {
				SimpleElement xml_reservation = (SimpleElement) (currentRoot
						.getChildByTagName("discount_factor")[0]);
				double df = Double.valueOf(xml_reservation
						.getAttribute("value"));
				discountFactor = validateDiscount(df);
			}
		} catch (Exception e) {
			System.out.println("Utility space has no discount factor;");
		}

		Vector<Evaluator> tmpEvaluator = new Vector<Evaluator>(); // tmp vector
																	// with all
																	// Evaluators
																	// at this
																	// level.
																	// Used to
																	// normalize
																	// weigths.
		EVALUATORTYPE evalType;
		String type, etype;
		Evaluator lEvaluator = null;

		// Get the weights of the current children
		Object[] xml_weights = currentRoot.getChildByTagName("weight");
		nrOfWeights = xml_weights.length; // assuming each
		HashMap<Integer, Double> tmpWeights = new HashMap<Integer, Double>();
		// System.out.println("nrOfWeights = " + nrOfWeights);
		for (int i = 0; i < nrOfWeights; i++) {
			index = Integer.valueOf(((SimpleElement) xml_weights[i])
					.getAttribute("index"));
			double dval = Double.valueOf(((SimpleElement) xml_weights[i])
					.getAttribute("value"));
			Integer indInt = new Integer(index);
			Double valueDouble = new Double(dval);
			tmpWeights.put(indInt, valueDouble);
		}

		// Collect evaluations for each of the issue values from file.
		// Assumption: Discrete-valued issues.
		Object[] xml_issues = currentRoot.getChildByTagName("issue");
		Object[] xml_objectives = currentRoot.getChildByTagName("objective");
		Object[] xml_obj_issues = new Object[xml_issues.length
				+ xml_objectives.length];
		int i_ind;
		for (i_ind = 0; i_ind < xml_issues.length; i_ind++) {
			// System.out.println("issues_index: " + i_ind + " vs length:" +
			// xml_issues.length +" to fill something of lenght: "+
			// xml_obj_issues.length);
			xml_obj_issues[i_ind] = xml_issues[i_ind];
		}
		/*
		 * for(int o_ind = i_ind; o_ind < xml_obj_issues.length; o_ind++){
		 * System.out.println("objectives_index: " + o_ind + " vs length:" +
		 * xml_objectives.length +" to fill something of lenght: "+
		 * xml_obj_issues.length); xml_obj_issues[o_ind] =
		 * xml_objectives[o_ind]; }
		 */for (int o_ind = 0; (o_ind + i_ind) < xml_obj_issues.length; o_ind++) {
			// System.out.println("objectives_index: " + o_ind + " vs length:" +
			// xml_objectives.length +" to fill something of lenght: "+
			// xml_obj_issues.length);
			xml_obj_issues[(o_ind + i_ind)] = xml_objectives[o_ind];
		}

		for (int i = 0; i < xml_obj_issues.length; i++) {
			index = Integer.valueOf(((SimpleElement) xml_obj_issues[i])
					.getAttribute("index"));
			type = ((SimpleElement) xml_obj_issues[i]).getAttribute("type");
			etype = ((SimpleElement) xml_obj_issues[i]).getAttribute("etype");
			if (type == null) { // No value type specified.
				// new
				// Warning("Evaluator type not specified in utility template file.");
				// TODO: Define exception.
				evalType = EVALUATORTYPE.DISCRETE;
			} else if (type.equals(etype)) {
				evalType = EVALUATORTYPE.convertToType(type);
			} else if (type != null && etype == null) { // Used label "type"
														// instead of label
														// "vtype".
				evalType = EVALUATORTYPE.convertToType(type);
			} else {
				System.out
						.println("Conflicting value types specified for evaluators in utility template file.");
				// TODO: Define exception.
				// For now: use "type" label.
				evalType = EVALUATORTYPE.convertToType(type);
			}
			if (tmpWeights.get(index) != null) {
				switch (evalType) {
				case DISCRETE:
					lEvaluator = new EvaluatorDiscrete();
					break;
				case INTEGER:
					lEvaluator = new EvaluatorInteger();
					break;
				case REAL:
					lEvaluator = new EvaluatorReal();
					break;
				case OBJECTIVE:
					lEvaluator = new EvaluatorObjective();
					break;
				}
				lEvaluator.loadFromXML((SimpleElement) (xml_obj_issues[i]));
				// TODO: put lEvaluator to an array (done?)
				// evaluations.add(tmp_evaluations);

				try {
					fEvaluators
							.put(getDomain().getObjective(index), lEvaluator); // Here
																				// we
																				// get
																				// the
																				// Objective
																				// or
																				// Issue.
				} catch (Exception e) {
					System.out.println("Domain-utilityspace mismatch");
					e.printStackTrace();
					return false;
				}
			}
			try {
				if (nrOfWeights != 0) {
					Integer indexInt = new Integer(index);
					// System.out.println("Hashcode here is: " +
					// indexInt.hashCode());
					double tmpdwt = tmpWeights.get(indexInt).doubleValue();
					Objective tmpob = getDomain().getObjective(index);
					fEvaluators.get(tmpob).setWeight(tmpdwt);
					// fEvaluators.get(getDomain().getObjective(index)).setWeight(tmpWeights.get(index).doubleValue());
					// System.out.println("set weight to " + tmpdwt);
				}
			} catch (Exception e) {
				System.out
						.println("Evaluator-weight mismatch or no weight for this issue or objective.");
			}
			tmpEvaluator.add(lEvaluator); // for normalisation purposes.
		}

		// Recurse over all children:
		boolean returnval = false;
		Object[] objArray = currentRoot.getChildElements();
		for (int i = 0; i < objArray.length; i++)
			returnval = loadTreeRecursive((SimpleElement) objArray[i]);
		return returnval;
	}

	protected double validateDiscount(double df) {
		if (df < 0 || df > 1) {
			System.err.println("Warning: discount factor = " + df
					+ " was discarded.");
		}

		if (df <= 0 || df > 1) {
			df = 1;
		}
		return df;
	}

	/**
	 * 
	 * @param issueID
	 *            The Issue or Objective to get the weight from
	 * @return The weight, or -1 if the objective doesn't exist. Only works with
	 *         linear utility spaces.
	 */
	public double getWeight(int issueID) {
		checkForLinearSpaceType();
		// TODO geeft -1.0 terug als de weight of de eveluator niet bestaat.
		Objective ob = domain.getObjective(issueID);
		if (ob != null) {
			// System.out.println("Obje index "+ issueID +" != null");
			Evaluator ev = fEvaluators.get(ob);
			if (ev != null) {
				// System.out.println("Weight " + issueID + " should be " +
				// ev.getWeight());
				return ev.getWeight();
			}
		} else
			System.out.println("Obje " + issueID + " == null");
		return 0.0; // fallthrough.
	}

	/**
	 * Method used to set the weight of the given objective. Only works with
	 * linear utility spaces.
	 * 
	 * @param objective
	 *            of which the weights must be set.
	 * @param weight
	 *            to which the weight of the objective must be set.
	 * @return the new weight of the issue after normalization.
	 */
	public double setWeight(Objective objective, double weight) {
		checkForLinearSpaceType();
		try {
			Evaluator ev = fEvaluators.get(objective);
			double oldWt = ev.getWeight();
			if (!ev.weightLocked()) {
				ev.setWeight(weight); // set weight
			}
			this.normalizeChildren(objective.getParent());
			if (this.checkTreeNormalization()) {
				return fEvaluators.get(objective).getWeight();
			} else {
				ev.setWeight(oldWt); // set the old weight back.
				return fEvaluators.get(objective).getWeight();
			}
		} catch (NullPointerException npe) {
			return -1;
		}
	}

	/**
	 * @deprecated Use getObjective
	 * 
	 * @param index
	 *            The index of the issue to
	 * @return the indexed objective or issue
	 */
	public final Objective getIssue(int index) {
		return domain.getIssue(index);
	}

	/**
	 * @return domain belonging to this preference profile.
	 */
	public final Domain getDomain() {
		return domain;
	}

	/**
	 * Sets an <Objective, evaluator> pair. Replaces old evaluator for
	 * objective. Only works with linear utility spaces.
	 * 
	 * @param obj
	 *            The Objective to attach an Evaluator to.
	 * @param ev
	 *            The Evaluator to attach.
	 * @return The given evaluator.
	 */
	public final Evaluator addEvaluator(Objective obj, Evaluator ev) {
		checkForLinearSpaceType();
		fEvaluators.put(obj, ev); // replaces old value for that object-key if
									// key already existed.
		return ev;
	}

	/**
	 * @return The set with all pairs of evaluators and objectives in this
	 *         utilityspace. Only works with linear utility spaces.
	 */
	public final Set<Map.Entry<Objective, Evaluator>> getEvaluators() {
		checkForLinearSpaceType();
		return fEvaluators.entrySet();
	}

	/**
	 * Place a lock on the weight of an objective or issue. Only works with
	 * linear utility spaces.
	 * 
	 * @param obj
	 *            The objective or issue that is about to have it's weight
	 *            locked.
	 * @return <code>true</code> if successful, <code>false</code> If the
	 *         objective doesn't have an evaluator yet.
	 */
	public final boolean lock(Objective obj) {
		checkForLinearSpaceType();
		try {
			fEvaluators.get(obj).lockWeight();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Clear a lock on the weight of an objective or issue. Only works with
	 * linear utility spaces.
	 * 
	 * @param obj
	 *            The objective or issue that is having it's lock cleared.
	 * @return <code>true</code> If the lock is cleared, <code>false</code> if
	 *         the objective or issue doesn't have an evaluator yet.
	 */
	public final boolean unlock(Objective obj) {
		checkForLinearSpaceType();
		try {
			fEvaluators.get(obj).unlockWeight();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Normalizes the weights of objectives of the given objective so that they
	 * sum up to one. Only works with linear utility spaces.
	 * 
	 * @param obj
	 *            of which the weights must be normalized.
	 * @return all evaluators using getEvaluators().
	 */
	public final Set<Map.Entry<Objective, Evaluator>> normalizeChildren(
			Objective obj) {
		checkForLinearSpaceType();
		Enumeration<Objective> childs = obj.children();
		double RENORMALCORR = 0.05; // we add this to all weight sliders to
									// solve the slider-stuck-at-0 problem.
		double weightSum = 0;
		double lockedWeightSum = 0;
		int freeCount = 0;
		int lockedCount = 0;
		while (childs.hasMoreElements()) {
			Objective tmpObj = childs.nextElement();
			try {
				if (!fEvaluators.get(tmpObj).weightLocked()) {
					weightSum += fEvaluators.get(tmpObj).getWeight();
					freeCount++;
				} else {
					lockedWeightSum += fEvaluators.get(tmpObj).getWeight();
					lockedCount++;
				}
			} catch (Exception e) {

				// do nothing, we can encounter Objectives/issues without
				// Evaluators.
			}
		}
		// System.out.println("freeCount + lockedCount = " + freeCount + " + " +
		// lockedCount);
		if (freeCount + lockedCount == 1) {
			Enumeration<Objective> singleChild = obj.children();
			while (singleChild.hasMoreElements()) {
				Objective tmpObj = singleChild.nextElement();
				fEvaluators.get(tmpObj).setWeight(1.0);
			}
		}

		// Wouter: cleaned up the test...
		// if(/*weightSum + lockedWeightSum != 1.0 && */(lockedCount +1) <
		// (freeCount + lockedCount) /*&& weightSum + lockedWeightSum != 0.0*/
		// ){ //that second bit to ensure that there is no problem with
		if (freeCount > 1) {
			Enumeration<Objective> normalChilds = obj.children();
			while (normalChilds.hasMoreElements()) {
				Objective tmpObj = normalChilds.nextElement();
				double diff = (lockedWeightSum + weightSum) - 1.0;
				// because of RENORMALCORR, total weight will get larger.
				double correctedWeightSum = weightSum + RENORMALCORR
						* freeCount;
				try {

					if (!fEvaluators.get(tmpObj).weightLocked()) {
						double currentWeight = fEvaluators.get(tmpObj)
								.getWeight();
						double newWeight = currentWeight
								- (diff * (currentWeight + RENORMALCORR) / correctedWeightSum);
						if (newWeight < 0) {
							newWeight = 0; // FIXME hdv: could this become 0?
											// Unsure of that.
						}
						fEvaluators.get(tmpObj).setWeight(newWeight);
						// System.out.println("new Weight of " +
						// tmpObj.getName() + " is " + newWeight);
					}
				} catch (Exception e) {
					// do nothing, we can encounter Objectives/issues without
					// Evaluators.
				}

			}

		}

		return getEvaluators();
	}

	/**
	 * Creates an xml representation (in the form of a SimpleElements) of the
	 * utilityspace.
	 * 
	 * @return A representation of this utilityspace or <code>null</code> when
	 *         there was an error.
	 * @throws IOException
	 */
	public SimpleElement toXML() throws IOException {
		SimpleElement root = (domain.getObjectivesRoot()).toXML(); // convert
																	// the
																	// domain.
		root = toXMLrecurse(root);
		SimpleElement rootWrapper = new SimpleElement("utility_space");
		rootWrapper.addChildElement(root);

		SimpleElement discountFactor = new SimpleElement("discount_factor");
		discountFactor.setAttribute("value", getDiscountFactor() + "");
		rootWrapper.addChildElement(discountFactor);

		SimpleElement reservationValue = new SimpleElement("reservation");
		reservationValue.setAttribute("value",
				getReservationValueUndiscounted() + "");
		rootWrapper.addChildElement(reservationValue);

		return rootWrapper;
	}

	/**
	 * Wouter: I assume this adds the utilities (weights) from this utility
	 * space to a given domain. It modifies the currentLevel so the return value
	 * is superfluous.
	 * 
	 * @param currentLevel
	 *            is pointer to a XML tree describing the domain.
	 * @return XML tree with the weights. NOTE: currentLevel is modified anyway.
	 */
	private SimpleElement toXMLrecurse(SimpleElement currentLevel) {
		// go through all tags.

		// receiveMessage the objective fields.
		Object[] Objectives = currentLevel.getChildByTagName("objective");
		// Object[] childWeights = currentLevel.getChildByTagName("weight");
		// Wou;ter: again, domain has no weights.

		for (int objInd = 0; objInd < Objectives.length; objInd++) {
			SimpleElement currentChild = (SimpleElement) Objectives[objInd];
			int childIndex = Integer
					.valueOf(currentChild.getAttribute("index"));
			try {
				Evaluator ev = fEvaluators.get(domain.getObjective(childIndex));
				// Wouter: nasty, they dont check whether object actually has
				// weight.
				// they account on an exception being thrown in dthat case....
				SimpleElement currentChildWeight = new SimpleElement("weight");
				currentChildWeight.setAttribute("index", "" + childIndex);
				currentChildWeight.setAttribute("value", "" + ev.getWeight());
				currentLevel.addChildElement(currentChildWeight);
			} catch (Exception e) {
				// do nothing, not every node has an evaluator.
			}
			currentChild = toXMLrecurse(currentChild);
		}

		// receiveMessage the issue fields.
		Object[] Issues = currentLevel.getChildByTagName("issue");
		// Object[] IssueWeights = currentLevel.getChildByTagName("weight");
		// Wouter: huh, domain has no weights!!!

		for (int issInd = 0; issInd < Issues.length; issInd++) {
			SimpleElement issueL = (SimpleElement) Issues[issInd];

			// set the weight
			int childIndex = Integer.valueOf(issueL.getAttribute("index"));
			Objective tmpEvObj = domain.getObjective(childIndex);
			try {

				Evaluator ev = fEvaluators.get(tmpEvObj);

				SimpleElement currentChildWeight = new SimpleElement("weight");
				currentChildWeight.setAttribute("index", "" + childIndex);
				currentChildWeight.setAttribute("value", "" + ev.getWeight());
				currentLevel.addChildElement(currentChildWeight);

				String evtype_str = issueL.getAttribute("etype");
				EVALUATORTYPE evtype = EVALUATORTYPE.convertToType(evtype_str);
				switch (evtype) {
				case DISCRETE:
					// fill this issue with the relevant weights to items.
					Object[] items = issueL.getChildByTagName("item");
					for (int itemInd = 0; itemInd < items.length; itemInd++) {
						// SimpleElement tmpItem = (SimpleElement)
						// items[itemInd];
						IssueDiscrete theIssue = (IssueDiscrete) domain
								.getObjective(childIndex);

						EvaluatorDiscrete dev = (EvaluatorDiscrete) ev;
						Integer eval = dev.getValue(theIssue.getValue(itemInd));
						((SimpleElement) items[itemInd]).setAttribute(
								"evaluation", "" + eval);
					}
					break;
				case INTEGER:
					EvaluatorInteger iev = (EvaluatorInteger) ev;
					issueL.setAttribute("lowerbound", "" + iev.getLowerBound());
					issueL.setAttribute("upperbound", "" + iev.getUpperBound());

					SimpleElement thisIntEval = new SimpleElement("evaluator");
					thisIntEval.setAttribute("ftype", "linear");
					thisIntEval.setAttribute("slope", "" + iev.getSlope());
					thisIntEval.setAttribute("offset", "" + iev.getOffset());
					issueL.addChildElement(thisIntEval);
					break;
				case REAL:
					EvaluatorReal rev = (EvaluatorReal) ev;
					SimpleElement thisRealEval = new SimpleElement("evaluator");
					EVALFUNCTYPE revtype = rev.getFuncType();
					if (revtype == EVALFUNCTYPE.LINEAR) {
						thisRealEval.setAttribute("ftype", "linear");
						thisRealEval.setAttribute("parameter1",
								"" + rev.getLinearParam());
					} else if (revtype == EVALFUNCTYPE.CONSTANT) {
						thisRealEval.setAttribute("ftype", "constant");
						thisRealEval.setAttribute("parameter0",
								"" + rev.getConstantParam());
					}
					issueL.addChildElement(thisRealEval);
					// TODO hdv the same thing as above vor the "evaluator" tag.
					break;
				}
			} catch (Exception e) {
				// do nothing, it could be that this objective/issue doesn't
				// have an evaluator yet.
			}

		}

		return currentLevel;
	}

	/**
	 * Wouter: this function *should* check that the domainSubtreeP is a subtree
	 * of the utilSubtreeP, and that all leaf nodes are complete. However
	 * currently we only check that all the leaf nodes are complete,
	 * 
	 * @return null if util space is complete, else returns string containging
	 *         explanation why not.
	 */
	private String isComplete() {
		// We don't have the domain template here anymore.
		// so we can only check that all fields are filled.
		ArrayList<Issue> issues = domain.getIssues();
		if (issues == null)
			return "Utility space is not complete, in fact it is empty!";
		String mess;
		for (Issue issue : issues) {
			Evaluator ev = getEvaluator(issue.getNumber());
			if (ev == null)
				return "issue " + issue.getName() + " has no evaluator";
			mess = (ev.isComplete(issue));
			if (mess != null)
				return mess;
		}
		return null;
	}

	/**
	 * @param newRV
	 *            new reservation value.
	 */
	public void setReservationValue(double newRV) {
		fReservationValue = newRV;
	}

	/**
	 * @param newDiscount
	 *            new discount factor.
	 */
	public void setDiscount(double newDiscount) {
		discountFactor = validateDiscount(newDiscount);
	}

	/**
	 * The reservation value is the least favourable point at which one will
	 * accept a negotiated agreement. Also sometimes referred to as the �walk
	 * away� point.
	 * 
	 * This is value remains constant during the negotiation. However, by
	 * default, the reservation value descreases with time. To obtain the
	 * discounted version of the reservation value, use
	 * {@link #getReservationValueWithDiscount(Timeline)}.
	 * 
	 * @return undiscounted reservation value of the preference profile (may be
	 *         null).
	 */
	public Double getReservationValue() {
		return getReservationValueUndiscounted();
	}

	/**
	 * Equivalent to {@link #getReservationValue()}, but always returns a double
	 * value. When the original reservation value is <b>null</b> it returns the
	 * default value 0.
	 * 
	 * @return undiscounted reservation value of the preference profile (never
	 *         null).
	 * @see #getReservationValue()
	 */
	public double getReservationValueUndiscounted() {
		if (fReservationValue == null)
			return 0;
		return fReservationValue;
	}

	/**
	 * The discounted version of {@link #getReservationValue()}.
	 * 
	 * @param time
	 *            at which we want to know the utility of the reservation value.
	 * @return discounted reservation value.
	 */
	public double getReservationValueWithDiscount(double time) {
		Double rv = getReservationValue();
		if (rv == null || rv == 0)
			return 0;

		return discount(rv, time);
	}

	/**
	 * The discounted version of {@link #getReservationValue()}.
	 * 
	 * @param timeline
	 *            specifying the current time in the negotiation.
	 * @return discounted reservation value.
	 */
	public double getReservationValueWithDiscount(Timeline timeline) {
		return getReservationValueWithDiscount(timeline.getTime());
	}

	/**
	 * @return filename of this preference profile.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return true if the domain features discounts.
	 */
	public boolean isDiscounted() {
		return discountFactor < 1.0;
	}

	public String toString() {
		String result = "";
		for (Entry<Objective, Evaluator> entry : fEvaluators.entrySet()) {
			result += ("Issue weight " + entry.getValue().getWeight() + "\n");
			result += ("Values " + entry.getKey().getName() + ": "
					+ entry.getValue().toString() + "\n");
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UtilitySpace))
			return false;
		UtilitySpace obj2 = (UtilitySpace) obj;
		// check domains
		if (!domain.equals(obj2.getDomain()))
			return false;
		// check evaluators
		for (Entry<Objective, Evaluator> entry : fEvaluators.entrySet()) {
			Evaluator eval2 = obj2.getEvaluator(entry.getKey().getNumber());
			if (!entry.getValue().equals(eval2)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return Discount factor of this preference profile.
	 */
	public final double getDiscountFactor() {
		return discountFactor;
	}
}
