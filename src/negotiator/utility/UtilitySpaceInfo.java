package negotiator.utility;

/**
 * Provides information extraction from a {@link UtilitySpace}.
 * 
 * @author W.Pasman 21jul15
 *
 */
public interface UtilitySpaceInfo {

	/**
	 * @return true iff the domain features discounts.
	 */
	boolean isDiscounted();

	/**
	 * The reservation value is the least favourable point at which one will
	 * accept a negotiated agreement. Also sometimes referred to as the walk
	 * away point.
	 * 
	 * This is value remains constant during the negotiation. But see also
	 * {@link #discount(double, double)}.
	 * 
	 * @return undiscounted reservation value of the preference profile (may be
	 *         null).
	 */
	Double getReservationValue();

	/**
	 * The utility may decrease depending on the current negotiation time.
	 * Generally, utilities decrease as the time advances.
	 * 
	 * @param util
	 *            the undiscounted utility in range [0,1]
	 * @param time
	 *            number in range [0,1] that determines the discount.
	 * 
	 * @return discounted utility
	 */
	Double discount(double util, double time);

}
