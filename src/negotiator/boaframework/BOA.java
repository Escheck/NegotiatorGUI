package negotiator.boaframework;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract superclass for BOA components. This should have been called
 * BOAcomponent but that class is already in use .
 **/
public abstract class BOA {
	/**
	 * returns the set of parameters of this BOA component. Default
	 * implementation is empty set. If a BOA component has parameters, it should
	 * override this.
	 * 
	 * @return set of parameters of this BOA component
	 */
	public Set<BOAparameter> getParameters() {
		return new HashSet<BOAparameter>();
	}
}