/*
 * NegotiatorException.java
 *
 * Created on November 17, 2006, 3:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.exceptions;

/**
 *
 * @author dmytro
 * This is a generic class of nogotiation errors.
 */
public class NegotiatorException extends Exception{
    
    /** Creates a new instance of NegotiatorException */
	// Wouter: I think we dont need a constructor, 
	// the constructor of Exception is good enough.
    public NegotiatorException(String message) 
    {
    	super(message);
    }
    
}
