package negotiator.exceptions;

import java.util.Hashtable;

/** 
 * Warning objects handle warning messages. These objects also count how many times a particular type of message
 * has been issued already.
 * You can ask for a stack dump as well.
 * @author W.Pasman
 */

public class Warning {
	
	// Class fields
	static Hashtable<String,Integer>
		pPreviousMessages = new Hashtable<String,Integer>();   // Hashtable key = warning message, corresponding value = #repetitions.

	// Constructor
	/**
	 * Default warning: Print warning message at most 5 times. Stack trace is not printed.
	 */
	public Warning(String pWarning) {
		makeWarning(pWarning, false, 5);
	}
	
	public Warning(String pWarning, boolean pShowStack, int pSuppressAt) {
		makeWarning(pWarning, pShowStack, pSuppressAt);
	}
	
	// Class methods
	/**
	 * Add warning to static hashtable used to keep track of all warnings issued so far.
	 * Only show warning if message has not appeared more than 'fSuppressAt' times.
	 */
	public void makeWarning(String pWarning, boolean pShowStack, int pSuppressAt) {
		
		Object lWarnings = pPreviousMessages.get(pWarning);

		if (lWarnings==null) {
			pPreviousMessages.put(pWarning, 0);
			lWarnings=0;
		}
		
		int lNrOfWarnings = (Integer)(pPreviousMessages.get(pWarning))+1;
		// Update nr of warning occurrences in hashtable
		pPreviousMessages.put(pWarning, lNrOfWarnings);
		
		if ((Integer)lWarnings > pSuppressAt) return;

		// Print message
		System.out.print("WARNING: "+pWarning);

		Exception e=new Exception();
		StackTraceElement[] elts=e.getStackTrace();
		if (pShowStack && elts.length>=3)
		{
			System.out.println();
				// start stacktrace at 2: 0 and 1 are inside the Warning class and not useful.
			for (int i=2; i<elts.length; i++) System.out.println(elts[i]);
		} else {
			if (elts.length>=2) System.out.print(" at "+elts[2]+"\n");
			else System.out.print(" at empty stack point?\n");
		}
		
		if ((Integer)lWarnings == pSuppressAt)
		{
			System.out.print("New occurrences of this warning will not be shown anymore.\n");
			return; 
		}
	}


}
