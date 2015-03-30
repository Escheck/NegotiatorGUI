package negotiator.boaframework;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import negotiator.NegotiationResult;

/**
 * Abstract superclass for BOA components. This should have been called
 * BOAcomponent but that class is already in use .
 * 
 * @author W.Pasman 25mar14 #867
 **/
public abstract class BOA {
	/**
	 * Reference to the object which holds all information about the negotiation
	 */

	protected NegotiationSession negotiationSession;

	/**
	 * initializes the BOA
	 * 
	 * @param negotiationSession
	 */
	public void init(NegotiationSession negotiationSession) {
		this.negotiationSession = negotiationSession;
	}

	/**
	 * returns the set of available parameters of this BOA component. Default
	 * implementation returns empty set. If a BOA component has parameters, it
	 * should override this. This can be different from the actual parameters
	 * used at runtime, which is passed through {@link #init(...)} calls to the
	 * components.
	 * 
	 * @return set of parameters of this BOA component
	 */
	public Set<BOAparameter> getParameters() {
		return new HashSet<BOAparameter>();
	}

	/**
	 * Method called at the end of the negotiation. Ideal location to call the
	 * storeData method to receiveMessage the data to be saved.
	 * 
	 * @param result
	 *            of the negotiation.
	 */
	public void endSession(NegotiationResult result) {
	}

	/**
	 * Method used to store data that should be accessible in the next
	 * negotiation session on the same scenario. This method can be called
	 * during the negotiation, but it makes more sense to call it in the
	 * endSession method.
	 * 
	 * @param object
	 *            to be saved by this component.
	 */
	abstract public void storeData(Serializable object);

	/**
	 * Method used to load the saved object, possibly created in a previous
	 * negotiation session. The method returns null when such an object does not
	 * exist yet.
	 * 
	 * @return saved object or null when not available.
	 */
	abstract public Serializable loadData();

}