/*
 * Main.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.net.URL;
import java.util.TimeZone;
import java.util.Calendar;

import negotiator.gui.NegoGUIApp;



/**
 *
 * @author dmytro
 */
public class Global {
    
    public static Logger logger;
    public static String[] args;

    public static boolean batchMode = false;
    public static boolean fDebug = false;
    public static boolean analysisEnabled=true; // set to true to enable the realtime analysis tool.
    public static boolean experimentalSetup=true;//set to true to allow agent to access negotiation environment
     
    public Global() {
    }
    
    /**
     * @param args the command line arguments
     */
    
    private static void checkArguments(String[] args){
    	for(int i=0;i<args.length;i++) {
    		if(args[i].equals("-d")) fDebug = true;    		
    	}
    }
    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        java.text.SimpleDateFormat sdf = 
              new java.text.SimpleDateFormat(DATE_FORMAT);
        /*
        ** on some JDK, the default TimeZone is wrong
        ** we must set the TimeZone manually!!!
        **     sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        */
        sdf.setTimeZone(TimeZone.getDefault());          
          
        return sdf.format(cal.getTime());
    }

	public static boolean isDebug() {
		return fDebug;
	}

	public static String getLocalDirName()
	{
		String localDirName;
	
		//	Use that name to get a URL to the directory we are executing in
		java.net.URL myURL = NegoGUIApp.class.getResource(NegoGUIApp.getClassName());
		//Open a URL to the our .class file
	
		//Clean up the URL and make a String with absolute path name
		localDirName = myURL.getPath(); //Strip path to URL object out
		localDirName = myURL.getPath().replaceAll("%20", " "); //change %20 chars to spaces
	
		//Get the current execution directory
		localDirName =
			localDirName.substring(0,localDirName.lastIndexOf("/")); //clean off the file name
	
		return localDirName;
	}
}
