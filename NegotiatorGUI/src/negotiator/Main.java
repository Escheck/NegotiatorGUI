/*
 * Main.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.util.TimeZone;
import java.util.Calendar;

import negotiator.gui.MainFrame;
import negotiator.gui.chart.Chart;



/**
 *
 * @author dmytro
 */
public class Main {
    
    /** Creates a new instance of Main */
    public static Logger logger;
    public static String[] args;
    public static NegotiationManager nm;
    public static boolean batchMode ;
    public static boolean fDebug;
    public static MainFrame mf;
    public static Chart fChart = null;
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        batchMode = false;
        fDebug = false;
        checkArguments(args);
        Main.args = args;
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mf = new MainFrame(Main.args);
                logger = new Logger(mf.getOutputArea());
                if(batchMode) mf.getButtonStart().doClick();
                mf.setVisible(true);
                
            }
        });
    }
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
    
}
