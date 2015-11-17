package negotiator.utility;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.session.TimeLineInfo;
import negotiator.session.Timeline;

/**
 * Some default functionality to support implementation of concrete utility
 * spaces.
 * 
 * @author W.Pasman 5nov15
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractUtilitySpace implements UtilitySpace {

	/**
	 * FIXME remove code that should not really be here, concerning discount
	 * factor, reservation value etc.
	 */
	private Domain domain;
	protected String fileName;
	private double discountFactor = 1;
	private Double fReservationValue = null;

	/**
	 * sets domain and tries to load the file into XML root.
	 * 
	 * @param dom
	 * @param file
	 *            the file to load
	 */
	public AbstractUtilitySpace(Domain dom) {
		domain = dom;
	}

	@Override
	public Domain getDomain() {
		return domain;
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
	 * accept a negotiated agreement. Also sometimes referred to as the walk
	 * away point.
	 * <p>
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
	public double getReservationValueWithDiscount(TimeLineInfo timeline) {
		return getReservationValueWithDiscount(timeline.getTime());
	}

	/**
	 * @return true if the domain features discounts.
	 */
	public boolean isDiscounted() {
		return discountFactor < 1.0;
	}

	/**
	 * @return Discount factor of this preference profile.
	 */
	public final double getDiscountFactor() {
		return discountFactor;
	}

	/**
	 * @return filename of this preference profile.
	 */
	public String getFileName() {
		return fileName;
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
	public double getUtilityWithDiscount(Bid bid, TimeLineInfo timeline) {
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
	 * Specific implementation for discount, based on a discount factor.
	 * Computes:
	 * 
	 * discountedUtil = util * Math.pow(discount, time).
	 * 
	 * Checks for bounds on the discount factor and time.
	 */
	@Override
	public Double discount(double util, double time) {
		return discount(util, time, discountFactor);
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
	private double discount(double util, double time, double discountFactor) {
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
	 * Throws an exception is the the space type is not linear. This method is
	 * necessary because some parts of this class assume we work with weights
	 * and evaluators.
	 */
	protected void checkForLinearSpaceType() {
		if (this instanceof AdditiveUtilitySpace
				|| this instanceof ConstraintUtilitySpace)
			return;

		throw new IllegalStateException(
				"This method is to be used for linear utility spaces only. This space is "
						+ this.getClass());
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
		BidIterator bidit = new BidIterator(getDomain());

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
	 * @return type of the space. Note, you can just as well use instanceof
	 */
	public UTILITYSPACETYPE getType() {
		if (this instanceof AdditiveUtilitySpace) {
			return UTILITYSPACETYPE.LINEAR;
		}
		if (this instanceof NonlinearUtilitySpace) {
			return UTILITYSPACETYPE.NONLINEAR;
		}
		if (this instanceof ConstraintUtilitySpace) {
			return UTILITYSPACETYPE.CONSTRAINT;
		}

		return null;
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
		BidIterator bidit = new BidIterator(getDomain());

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

}
