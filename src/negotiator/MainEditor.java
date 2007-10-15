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
import negotiator.gui.tree.TreeFrame;
import negotiator.issue.Objective;


/**
 *
 * @author W.Pasman (quick mod of Main.java 15oct07) 
 */
public class MainEditor {
    
    /** Creates a new instance of Main */
    //public static String[] args;
    //public static NegotiationManager nm;
    //public static boolean batchMode ;
    //public static boolean fDebug;
    public static MainFrame mf;
    public static Chart fChart = null;
    public MainEditor() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    	Domain domain=new Domain();
    	Objective root=new Objective(null,"root");
    	domain.setObjectivesRoot(root);
    	TreeFrame treeFrame = new TreeFrame(domain);
		
		//treeFrame.pack();
		//treeFrame.setVisible(true);

		/*
        batchMode = false;
        fDebug = false;
        checkArguments(args);
        Main.args = args;
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mf = new MainFrame(Main.args);
                if(batchMode) mf.getButtonStart().doClick();
                mf.setVisible(true);
                
            }
        });
        */
    }


    
}
