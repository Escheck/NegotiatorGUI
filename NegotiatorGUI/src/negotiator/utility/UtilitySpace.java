package negotiator.utility;

import java.io.IOException;
import java.io.Serializable;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.session.TimeLineInfo;
import negotiator.session.Timeline;
import negotiator.xml.SimpleElement;

/**
 * A utility space is a function that maps bids to utilities.
 * 
 * @author W.Pasman 5nov15
 *
 */
public interface UtilitySpace extends Serializable {
	/**
	 * @param bid
	 *            of which we are interested in its utility.
	 * @return Utility of the given bid. This utility is undiscounted: there is
	 *         no time dependent devaluation of the utility.
	 * @throws Exception
	 *             when bid is incomplete or invalid. FIXME make more precise
	 */

	public double getUtility(Bid bid) throws Exception;

	/**
	 * Computes the discounted utility of a bid. The actual implementation is
	 * implementation specific.
	 * 
	 * @param util
	 *            the undiscounted utility as coming from
	 *            {@link #getUtility(Bid)}.
	 * @param time
	 *            a real number in the range [0,1] where 0 is the start of the
	 *            negotiation and 1 the end. See also {@link TimeLineInfo}.
	 */
	public Double discount(double util, double time);

	/**
	 * @return a deep copy of this utility space.
	 */
	public UtilitySpace copy();

	/**
	 * Check if this utility space is ready for negotiation.
	 * 
	 * @param dom
	 *            is the domain in which nego is taking place
	 * @throws Exception
	 *             if utility space is incomplete
	 */
	public void checkReadyForNegotiation(Domain dom) throws Exception;

	/**
	 * Creates an xml representation (in the form of a SimpleElements) of the
	 * utilityspace.
	 * 
	 * @return A representation of this utilityspace or <code>null</code> when
	 *         there was an error.
	 * @throws IOException
	 */
	public SimpleElement toXML() throws IOException;

	/**
	 * Classes must implement equals. Not sure if this does anything.
	 * 
	 * @param obj
	 *            the object to compare with
	 * @return true iff utility spaces are equal.
	 */
	public boolean equals(Object obj);

	/**
	 * @return domain belonging to this preference profile.
	 */
	public Domain getDomain();

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
	public Double getReservationValue();

}
