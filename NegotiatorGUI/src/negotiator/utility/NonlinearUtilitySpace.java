package negotiator.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;

public class NonlinearUtilitySpace extends AbstractUtilitySpace {
	private double maxUtilityValue;
	private UtilityFunction nonlinearFunction;
	private ArrayList<InclusiveHyperRectangle> allinclusiveConstraints; // we
																		// keep
																		// all
																		// constraints
																		// for
																		// negotiating
																		// agent's
																		// strategy
	private ArrayList<ExclusiveHyperRectangle> allexclusiveConstraints; // we
																		// keep
																		// all
																		// constraints
																		// for
																		// negotiating
																		// agent's
																		// strategy
	private SimpleElement fXMLRoot;

	// add some parameters for discount factor
	/**
	 * Creates an empty nonlinear utility space.
	 */
	public NonlinearUtilitySpace() {
		super(new Domain());
		this.nonlinearFunction = new UtilityFunction();
		this.allinclusiveConstraints = new ArrayList<InclusiveHyperRectangle>();
		this.allexclusiveConstraints = new ArrayList<ExclusiveHyperRectangle>();
	}

	public NonlinearUtilitySpace(Domain domain) {
		super(domain);
		this.nonlinearFunction = new UtilityFunction();
		this.allinclusiveConstraints = new ArrayList<InclusiveHyperRectangle>();
		this.allexclusiveConstraints = new ArrayList<ExclusiveHyperRectangle>();
	}

	public NonlinearUtilitySpace(Domain domain, String fileName)
			throws Exception {
		super(domain);
		this.nonlinearFunction = new UtilityFunction();
		this.fileName = fileName;
		this.allinclusiveConstraints = new ArrayList<InclusiveHyperRectangle>();
		this.allexclusiveConstraints = new ArrayList<ExclusiveHyperRectangle>();

		if (!fileName.equals("")) {
			SimpleDOMParser parser = new SimpleDOMParser();
			BufferedReader file = new BufferedReader(new FileReader(new File(
					fileName)));
			SimpleElement root = parser.parse(file);
			fXMLRoot = root;
			loadNonlinearSpace(root);

		} else
			throw new IOException();
	}

	private ArrayList<InclusiveHyperRectangle> getAllInclusiveConstraints() {
		return this.allinclusiveConstraints;
	}

	private ArrayList<ExclusiveHyperRectangle> getAllExclusiveConstraints() {
		return this.allexclusiveConstraints;
	}

	/** @return a clone of another utility space */
	public NonlinearUtilitySpace(NonlinearUtilitySpace us) {
		super(us.getDomain());
		fileName = us.getFileName();
		fXMLRoot = us.fXMLRoot;
		maxUtilityValue = ((NonlinearUtilitySpace) us).getMaxUtilityValue();
		nonlinearFunction = ((NonlinearUtilitySpace) us).getNonlinearFunction();
		allinclusiveConstraints = ((NonlinearUtilitySpace) us)
				.getAllInclusiveConstraints();
		allexclusiveConstraints = ((NonlinearUtilitySpace) us)
				.getAllExclusiveConstraints();
		this.setDiscount(((NonlinearUtilitySpace) us).getDiscountFactor());
		this.setReservationValue(((NonlinearUtilitySpace) us)
				.getReservationValue());
	}

	// This method parse xml file and load nonlinear utility space

	private ArrayList<Constraint> loadHyperRectangles(Object[] rectangeElements) {

		ArrayList<Constraint> hyperRectangleConstraints = new ArrayList<Constraint>();

		for (int j = 0; j < rectangeElements.length; j++) {

			HyperRectangle rectangle = null;
			ArrayList<Bound> boundlist = new ArrayList<Bound>();
			Object[] bounds = null;

			if (((SimpleElement) rectangeElements[j])
					.getChildByTagName("INCLUDES").length != 0) {
				rectangle = new InclusiveHyperRectangle();
				allinclusiveConstraints
						.add((InclusiveHyperRectangle) rectangle);
				bounds = ((SimpleElement) rectangeElements[j])
						.getChildByTagName("INCLUDES");
			}

			if (((SimpleElement) rectangeElements[j])
					.getChildByTagName("EXCLUDES").length != 0) {
				rectangle = new ExclusiveHyperRectangle();
				allexclusiveConstraints
						.add((ExclusiveHyperRectangle) rectangle);
				bounds = ((SimpleElement) rectangeElements[j])
						.getChildByTagName("EXCLUDES");
			}

			if ((((SimpleElement) rectangeElements[j])
					.getChildByTagName("INCLUDES").length == 0)
					&& (((SimpleElement) rectangeElements[j])
							.getChildByTagName("EXCLUDES").length == 0)) {
				rectangle = new InclusiveHyperRectangle(true);
			} else {
				for (int k = 0; k < bounds.length; k++) {
					Bound b = new Bound(
							((SimpleElement) bounds[k]).getAttribute("index"),
							((SimpleElement) bounds[k]).getAttribute("min"),
							((SimpleElement) bounds[k]).getAttribute("max"));
					boundlist.add(b);
				}
				rectangle.setBoundList(boundlist);
			}

			rectangle.setUtilityValue(Double
					.parseDouble(((SimpleElement) rectangeElements[j])
							.getAttribute("utility")));
			if (((SimpleElement) rectangeElements[j]).getAttribute("weight") != null)
				rectangle.setWeight(Double
						.parseDouble(((SimpleElement) rectangeElements[j])
								.getAttribute("weight")));

			hyperRectangleConstraints.add(rectangle);
		}
		return hyperRectangleConstraints;

	}

	private UtilityFunction loadUtilityFunction(SimpleElement utility) {

		UtilityFunction currentFunction = new UtilityFunction();
		// set the aggregation type
		currentFunction.setAggregationType(AGGREGATIONTYPE
				.getAggregationType(utility.getAttribute("aggregation")));
		// set the weight if it is specified

		if (utility.getAttribute("weight") != null)
			currentFunction.setWeight(Double.parseDouble(utility
					.getAttribute("weight")));

		// similarly other constraint can be parsed and add to the constraints
		// by adding a addConstraints method
		currentFunction.setConstraints(loadHyperRectangles(utility
				.getChildByTagName("hyperRectangle")));

		// here load inner utility functions !

		Object[] innerFunctions = ((SimpleElement) utility)
				.getChildByTagName("ufun");

		for (int k = 0; k < innerFunctions.length; k++) {
			currentFunction
					.addUtilityFunction(loadUtilityFunction(((SimpleElement) innerFunctions[k])));
		}
		return currentFunction;
	}

	@Override
	public SimpleElement toXML() throws IOException {
		/**
		 * HACK #903. Instead of conversion, we read in the existing XML file
		 * from disk, plug in the reservation value and discount factor and
		 * retuurn that.
		 */
		SimpleElement root;
		SimpleDOMParser parser = new SimpleDOMParser();
		BufferedReader file = new BufferedReader(new FileReader(new File(
				fileName)));
		root = parser.parse(file);

		Object[] reservationelts = root.getChildByTagName("reservation");
		if (reservationelts == null || reservationelts.length == 0
				|| !(reservationelts[0] instanceof SimpleElement)) {
			throw new InvalidPropertiesFormatException(
					"file does not contain reservation value");
		}
		Object[] discountelts = root.getChildByTagName("discount_factor");
		if (discountelts == null || discountelts.length == 0
				|| !(discountelts[0] instanceof SimpleElement)) {
			throw new InvalidPropertiesFormatException(
					"file does not contain discount factor value");
		}

		((SimpleElement) reservationelts[0]).setAttribute("value", ""
				+ getReservationValueUndiscounted());

		((SimpleElement) discountelts[0]).setAttribute("value", ""
				+ getDiscountFactor());

		return root;

	}

	private void loadNonlinearSpace(SimpleElement root) {

		// load reservation value
		try {
			if ((root.getChildByTagName("reservation") != null)
					&& (root.getChildByTagName("reservation").length > 0)) {
				SimpleElement xml_reservation = (SimpleElement) (root
						.getChildByTagName("reservation")[0]);
				this.setReservationValue(Double.valueOf(xml_reservation
						.getAttribute("value")));
				System.out.println("Reservation value: "
						+ this.getReservationValue());
			}
		} catch (Exception e) {
			System.out.println("Utility space has no reservation value");
		}
		// load discount factor
		try {
			if ((root.getChildByTagName("discount_factor") != null)
					&& (root.getChildByTagName("discount_factor").length > 0)) {
				SimpleElement xml_reservation = (SimpleElement) (root
						.getChildByTagName("discount_factor")[0]);
				double df = Double.valueOf(xml_reservation
						.getAttribute("value"));
				this.setDiscount(validateDiscount(df));
				System.out.println("Discount value: "
						+ this.getDiscountFactor());
			}
		} catch (Exception e) {
			System.out.println("Utility space has no discount factor;");
		}

		// load utility
		Object utility = ((SimpleElement) root.getChildElements()[0])
				.getChildByTagName("utility")[0];
		this.setMaxUtilityValue(Double.parseDouble(((SimpleElement) utility)
				.getAttribute("maxutility")));
		this.nonlinearFunction = loadUtilityFunction((SimpleElement) ((SimpleElement) utility)
				.getChildByTagName("ufun")[0]);
	}

	private double getMaxUtilityValue() {
		return maxUtilityValue;
	}

	private void setMaxUtilityValue(double maxUtilityValue) {
		this.maxUtilityValue = maxUtilityValue;
	}

	@Override
	public double getUtility(Bid bid) throws Exception {
		double result = (double) nonlinearFunction.getUtility(bid)
				/ this.maxUtilityValue;

		if (result > 1)
			return 1;
		else
			return result;
	}

	@Override
	public void checkReadyForNegotiation(Domain dom) throws Exception {
	}

	/**
	 * Uses the original equals of {@link Object}.
	 */
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	// RA We make it private for ANAC competition
	private UtilityFunction getNonlinearFunction() {
		return nonlinearFunction;
	}

	// RA: this method needs to be checked - I didn't check whether it works
	// properly
	@Override
	public String toString() {

		String result = "";
		for (InclusiveHyperRectangle rec : this.getAllInclusiveConstraints()) {
			ArrayList<Bound> boundList = rec.getBoundList();
			result += ("Rectangle: \n");
			for (Bound bound : boundList) {
				result += ("Issue with index " + bound.getIssueIndex()
						+ " min:" + bound.getMin() + " max:" + bound.getMax() + " \n");
			}

		}
		return result;
	}

	// The following methods belongs to Katsuhide

	/**
	 * 
	 * @param InclusiveHyperRectangle
	 *            :
	 * @return ArrauList<Bound>
	 */
	private ArrayList<Bound> getIntersectionOfHyperRectangles(
			ArrayList<InclusiveHyperRectangle> hyperRects) {
		ArrayList<Bound> hyperRectBoundlist = hyperRects.get(0).getBoundList();
		for (int index = 1; index < hyperRects.size(); ++index) {
			hyperRectBoundlist = getIntersection(hyperRects.get(index)
					.getBoundList(), hyperRectBoundlist);
		}
		return hyperRectBoundlist;
	}

	/**
	 * Finding the overlap area of HyperRectangle
	 * 
	 * @param ArrayList
	 *            <Bound>
	 * @return ArrauList<Bound>
	 */
	private ArrayList<Bound> getIntersection(ArrayList<Bound> bl1,
			ArrayList<Bound> bl2) {
		ArrayList<Bound> retBoundList = new ArrayList<Bound>();

		int index1 = 0;
		int index2 = 0;

		// When they have the same issue infromation
		do {
			Bound b1 = bl1.get(index1);
			Bound b2 = bl2.get(index2);

			if (b1.getIssueIndex() == b2.getIssueIndex()) {
				Bound intersectionBound = getIntersection(b1, b2);
				retBoundList.add(intersectionBound);
				++index1;
				++index2;
			} else if (b1.getIssueIndex() < b2.getIssueIndex()) {
				retBoundList.add(b1);
				++index1;
			} else {
				retBoundList.add(b2);
				++index2;
			}
		} while (!(bl1.size() == index1 || bl2.size() == index2));

		// Adding the remaining bounds
		if (bl1.size() > index1) {
			for (int i = index1; i < bl1.size(); ++i) {
				retBoundList.add(bl1.get(i));
			}
		}
		if (bl2.size() > index2) {
			for (int i = index2; i < bl2.size(); ++i) {
				retBoundList.add(bl2.get(i));
			}
		}

		return retBoundList;
	}

	/**
	 * Getting the overlap area of the bounds
	 */
	private Bound getIntersection(Bound b1, Bound b2) {
		int max = b1.getMax();
		int min = b1.getMin();
		if (b2.getMax() < max) {
			max = b2.getMax();
		}
		if (min < b2.getMin()) {
			min = b2.getMin();
		}
		return new Bound(b2.getIssueIndex(), min, max);
	}

	@Override
	public UtilitySpace copy() {
		return new NonlinearUtilitySpace(this);
	}
}
