/*
 * Main.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;


import javax.swing.JFileChooser;
import javax.swing.JPanel;

import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.UtilitySpace;


/**
 *
 * @author W.Pasman nov2007
 * Checks all selected files for conformity with the Party domain. 
 * Checked is that the selected file is ready to run (has all evaluators set)
 * You select them one by one, and if one fails the check the application stops with explanation.
 */
public class CheckPartyUtilityXML extends JPanel
{
	private static final long serialVersionUID = -1982241379549713469L;
	Domain partyDomain=null;
	
	public CheckPartyUtilityXML() throws Exception
	{	
    	boolean finished=false;
    	partyDomain=new Domain("etc/templates/partydomain/party_domain.xml");
    	
    	
    	while (!finished)
    	{
    		JFileChooser fileChooser = new JFileChooser();
    		if( fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
    		{
    			checkFile(fileChooser.getSelectedFile().toString() );
    		}
    		else finished=true;
    	}
	}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	try {     	new CheckPartyUtilityXML(); }
    	catch (Exception e) { 
    			System.out.println("Error checking party utilities:"+e.getMessage()); 
    			//e.printStackTrace();
    	}
    }
    
    /**
     * @author W.Pasman
     * @param filename is the filename  of utility space to be checked
     * @throws Exception if file does not meet domain requirements
     */
    public void checkFile(String filename) throws Exception
    {
    	System.out.println("Checking "+filename);
    	UtilitySpace us=new UtilitySpace(partyDomain,filename);
    	us.checkReadyForNegotiation(partyDomain);
    	System.out.println("Check succesfull!\n\n");
    }
}

