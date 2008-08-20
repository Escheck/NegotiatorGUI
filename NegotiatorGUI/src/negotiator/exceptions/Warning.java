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
		makeWarning(pWarning, new Exception(),false,5);
	}
	
	/** if you set showstack to true, stack dump will be made for location where WARNING occurs. 
	 * Note that this is not useful if you are converting an exception into a warning. In that case,
	 * you better use Warning(warning, Exception) */
	public Warning(String pWarning, boolean pShowStack, int pSuppressAt) {
		makeWarning(pWarning, new Exception(), pShowStack,pSuppressAt);
	}
	
	public Warning(String pWarning, Exception err) {
		makeWarning(pWarning,err,false,5);
	}
	
	public Warning(String pWarning, Exception err, boolean pShowStack,int pSuppressAt) {
		makeWarning(pWarning,err,pShowStack,pSuppressAt);
	}
	
	// Class methods
	/**
	 * Add warning to static hashtable used to keep track of all warnings issued so far.
	 * Only show warning if message has not appeared more than 'fSuppressAt' times.
	 * @param e is exception that caused the problem. Use null to avoid stack dump. 
	 */
	public void makeWarning(String pWarning, Exception e, boolean pDumpStack,int pSuppressAt) {
		
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
		System.out.print("WARNING: "+pWarning+", "+e);

		StackTraceElement[] elts=e.getStackTrace();
		if (pDumpStack && elts.length>=3)
		{
			System.out.println();
				// start stacktrace at 2: 0 and 1 are inside the Warning class and not useful.
			for (int i=0; i<elts.length; i++) System.out.println(elts[i]);
		} else {
			if (elts.length>0) System.out.print(" at "+elts[0]+"\n");
			else System.out.print(" at empty stack point?\n");
		}
		
		if ((Integer)lWarnings == pSuppressAt)
		{
			System.out.print("New occurrences of this warning will not be shown anymore.\n");
			return; 
		}
	}


}
