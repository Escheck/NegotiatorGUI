package negotiator.exceptions;

import java.util.Hashtable;

/** Warning objects handle warning messages 
 * This is NOT an exception, but this seemed the best place to put it now.

 * internally they count how many times individual messages have been issued already.
 * You can ask for a stack dump as well.
 * @author W.Pasman
 * */
public class Warning {
	static Hashtable<String,Integer> pPreviousMessages=new Hashtable();
		//key=String with error message, value=#repetitions.
	/** suppress after this many repetitions. 
	 * Note, you can set this to 0 as well, suppressing the warning alltogether.*/
	int fSuppressAt; 
	

	 /** default warning: no stack trace and default suppress at 5 repetitions. */
	public Warning(String pWarning) { MakeWarning(pWarning, false,5); }
	
	 /**
	 * show a warning on the command line
	 * 
	 * @param pWarning the message string to be shown.
	 * @param pShowStack whether a stack dump is to be shown 
	 * @param pSuppressAt stop showing the warning after this #of warnings.
	 */
	public Warning(String pWarning, boolean pShowStack,int pSuppressAt)
	{ MakeWarning( pWarning,  pShowStack, pSuppressAt); }
	
	
	// warning shows message if not yet shown too many times yet.
	public void MakeWarning(String pWarning, boolean pShowStack,int pSuppressAt)
	{
		fSuppressAt=pSuppressAt;
		Object lnumwarns=pPreviousMessages.get(pWarning);
		if (lnumwarns==null) { pPreviousMessages.put(pWarning,0); lnumwarns=0; }
		
		if ((Integer)lnumwarns < fSuppressAt) ShowWarning(pWarning,pShowStack);
	}
	

	
	 /** ShowWarning shows message and increments counter.
	  make sure that message is already in hashtable before call ing this*/
	public void ShowWarning(String pWarning,boolean pShowStack)
	{
		Integer lnumwarns=(Integer)(pPreviousMessages.get(pWarning));
		lnumwarns++;
		pPreviousMessages.put(pWarning, lnumwarns);
		System.out.print("WARNING");
		if (lnumwarns==fSuppressAt) 
			System.out.print("(further warnings will be suppressed)");
		System.out.println(":"+pWarning);
		if (pShowStack)
		{
			Exception e=new Exception();
			StackTraceElement[] elts=e.getStackTrace();
				// start stacktrace at 2: 0 and 1 are inside the Warning class and not useful.
			for (int i=2; i<elts.length; i++) System.out.println(elts[i]);
		}
	}

}
