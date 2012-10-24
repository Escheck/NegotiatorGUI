
package negotiator;

import negotiator.xml.SimpleElement;

/**
 * 
 * @author W.Pasman
 * 
 */

public interface XMLable
{
	/**
	 * @return XML representation of the given object.
	 */
	public SimpleElement toXML();
}
