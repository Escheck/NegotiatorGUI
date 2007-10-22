/*
 * EnterBidDialog.java
 *
 * Created on November 16, 2006, 10:18 AM
 */

package negotiator.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.Color;
import java.util.Vector;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

import java.util.HashMap;
import java.util.ArrayList;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import javax.swing.table.AbstractTableModel;
import negotiator.Agent;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.utility.UtilitySpace;
import negotiator.actions.*;
import negotiator.actions.Action;
import negotiator.exceptions.BidDoesNotExistInDomainException;
import negotiator.issue.*;
import negotiator.utility.EvaluatorDiscrete;

/**
 *
 * @author  W.Pasman
 */
public class EnterBidDialog2 extends JDialog {
	
	private NegoInfo negoinfo; // the table model	
    private negotiator.actions.Action selectedAction;
    private Agent agent;    
    private JTextArea negotiationMessages=new JTextArea("NO MESSAGES YET");  
    // Wouter: we have some whitespace in the buttons,
    // that makes nicer buttons and also artificially increases the window size.
    private JButton buttonBid=new JButton("       Do Bid       ");
    private JButton buttonSkip=new JButton("Skip Turn");
    private JButton buttonEnd=new JButton("End Negotiation");
    private JButton buttonAccept=new JButton(" Accept Opponent Bid ");
    private JPanel buttonPanel=new JPanel();    
    private JTable BidTable ;
    
    public EnterBidDialog2(Agent agent, java.awt.Frame parent, boolean modal, Domain d,UtilitySpace us) {
        super(parent, modal);
		if (d==null) throw new NullPointerException("null domain?");
        this.agent = agent;
        negoinfo=new NegoInfo(null,null,d, us);
        initThePanel();
    }
    
    
    // quick hack.. we can't refer to the Agent's utilitySpace because
    // the field is protected and there is no getUtilitySpace function either.
    // therefore the Agent has to inform us when utilspace changes.
    public void setUtilitySpace(UtilitySpace us)
    { negoinfo.utilitySpace=us; }
    
    
    
    private void initThePanel() {
    	if (negoinfo==null) throw new NullPointerException("negoinfo is null");
    	Container pane=getContentPane();
        pane.setLayout(new BorderLayout());
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Choose action for agent "+agent.getName());
        //setSize(new java.awt.Dimension(600, 400));
        //setBounds(0,0,640,480);

    // create north field: the message field
       pane.add(negotiationMessages,"North");
        
        
     // create center panel: the bid table
        BidTable = new  JTable(negoinfo);
        //BidTable.setModel(negoinfo); // need a model for column size etc...
       	 // Why doesn't this work???
        BidTable.setGridColor(Color.lightGray);
        String[] values = new String[]{"item1", "item2", "item3"};
        JPanel tablepane=new JPanel(new BorderLayout());
        tablepane.add(BidTable.getTableHeader(), "North");
        tablepane.add(BidTable,"Center");
        pane.add(tablepane,"Center");

        	// create south panel: the buttons:
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(buttonBid);
        buttonPanel.add(buttonSkip);
        buttonPanel.add(buttonEnd);
        buttonPanel.add(buttonAccept);
        pane.add(buttonPanel,"South");
        buttonBid.setSelected(true);

     // set action listeners for the buttons
        buttonBid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBidActionPerformed(evt);
            }
        });
        buttonSkip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSkipActionPerformed(evt);
            }
        });
        buttonEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEndActionPerformed(evt);
            }
        });
        buttonAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAcceptActionPerformed(evt);
            }
        });
        pack(); // pack will do complete layout, getting all cells etc.
    }
    
    
    private Bid getBid()
    {
        Bid bid=null;
        try {
            bid =  negoinfo.getBid();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "There is a problem with your bid: "+e.getMessage());
        }
        return bid;    	
    }

    
    private void buttonBidActionPerformed(java.awt.event.ActionEvent evt) 
    {
    	
        Bid bid=getBid();
        if (bid!=null) { 
        	selectedAction = new Offer(agent,bid);         
        	setVisible(false);
        }
    }

    
    private void buttonSkipActionPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println("cancel performed!");
        selectedAction = null;
        setVisible(false);
    }

    private void buttonAcceptActionPerformed(java.awt.event.ActionEvent evt) {
        Bid bid=getBid();
        if (bid!=null) {
        	System.out.println("Accept performed");
        	selectedAction=new Accept(agent,bid);
        	setVisible(false);
        }
    }
    
    private void buttonEndActionPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println("End Negotiation performed");
        selectedAction=new EndNegotiation(agent);
        setVisible(false);
    }
      
    /** 
     * This is called by UIAgent repeatedly, to ask for next action. 
     * @param opponentAction is action done by opponent
     * @param myPreviousBid 
     * @param nt is negotiation template.
     * @return our next negotionat action.
     */
    			public negotiator.actions.Action 
    askUserForAction(negotiator.actions.Action opponentAction, Bid myPreviousBid) 
    {
        negoinfo.opponentOldBid=null;
        if(opponentAction==null) {
        	negotiationMessages.setText("Opponent did not send any action.");            
        }
        if(opponentAction instanceof Accept) {
        	negotiationMessages.setText("Opponent accepted the following bid:");
        	negoinfo.opponentOldBid = ((Accept)opponentAction).getBid();
        }
        if(opponentAction instanceof EndNegotiation) {
        	negotiationMessages.setText("Opponent cancels the negotiation.");
        }
        if(opponentAction instanceof Offer) {
        	negotiationMessages.setText("Opponent proposes the following bid:");
        	negoinfo.opponentOldBid = ((Offer)opponentAction).getBid();
        }
        negoinfo.setOurBid(myPreviousBid);
        
        BidTable.setDefaultRenderer(BidTable.getColumnClass(0),
        		new MyCellRenderer(negoinfo));
        BidTable.setDefaultEditor(BidTable.getColumnClass(0),new MyCellEditor(negoinfo));

        pack();
        setVisible(true); // this returns only after the panel closes.
        
        return selectedAction;
    }
}   
    

/********************************************************/

/**
 * NegoInfo is the class that contains all the negotiation data,
 * and handles the GUI, updating the JTable. This is the main 
 * interface to the actial JTable.
 * This is usualy called XXXModel but I dont like the 'model' in the name.
 * We implement actionlistener to hear the combo box events that
 * require re-rendering of the total cost and utility field.
 * We are pretty hard-wired for a 3-column table, with column 0 the
 * labels, column 1 the opponent bid and col2 our own bid.
 */
class NegoInfo extends AbstractTableModel implements ActionListener
{
	public Bid ourOldBid;			// Bid is hashmap <issueID,Value>. Our current bid is only in the comboboxes,
									// use getBid().
	public Bid opponentOldBid;
	public Domain domain;
	public UtilitySpace utilitySpace;	// WARNING: this may be null
	public ArrayList<Issue> issues=new ArrayList<Issue>(); 
	// the issues, in row order as in the GUI. Init to empty, to enable 
	// freshly initialized NegoInfo to give useful resuitsl to the GUI.
	public ArrayList<Integer> IDs; //the IDs/numbers of the issues, ordered to row number
	public ArrayList<JComboBox> comboBoxes; // the combo boxes for the second column, ordered to row number
	
	NegoInfo(Bid our,Bid opponent,Domain d, UtilitySpace us) throws NullPointerException
	{
		//Wouter: just discovered that assert does nothing........... 
		if (d==null) throw new NullPointerException("null domain?");
		ourOldBid=our; opponentOldBid=opponent;
		domain=d;
		utilitySpace=us;
		issues=d.getIssues();
		IDs=new ArrayList<Integer>();
		for (int i=0; i<issues.size(); i++) IDs.add(issues.get(i).getNumber());
		makeComboBoxes();
	}
	
	
	public int getColumnCount() 	{		return 3;	}	

	public int getRowCount() 	{		return issues.size()+2;	}

	public boolean isCellEditable(int row, int col)
	{		return (col==2 && row<issues.size());	}

   	private String[] colNames={"Issue","Last Bid of Opponent","Your bid"};
   	public String getColumnName(int col) { return colNames[col]; }
	

   	public void setOurBid(Bid bid)
   	{ 		
   		ourOldBid=bid;
	    if (bid==null)
	    	try { ourOldBid=utilitySpace.getMaxUtilityBid(); }
	    	catch (Exception e) { 
	    		System.out.println("error getting max utility first bid:"+e.getMessage()); 
	    		e.printStackTrace();
	    	}
	    setComboBoxes(ourOldBid);
   	}
	
	void makeComboBoxes()
	{
		comboBoxes=new ArrayList<JComboBox>();
		for (Issue issue:issues)
		{
			if (!(issue instanceof IssueDiscrete))
				System.out.println("Problem: issue "+issue+" is not IssueDiscrete. ");
			ArrayList<ValueDiscrete> values=((IssueDiscrete)issue).getValues();
			comboBoxes.add( new JComboBox(values.toArray()));
			for (JComboBox b:comboBoxes) b.addActionListener(this);
		}
	}
	
	/**
	 * set the initial combo box selections according to ourOldBid
	 * Note, we can only handle Discrete evaluators right now. 
	 */
	void setComboBoxes(Bid bid)
	{
		for (int i=0; i<issues.size(); i++)
			comboBoxes.get(i).setSelectedItem((bid.getValue(issues.get(i).getNumber())));	
	}
	
	
	
	/**
	 * get the currently chosen evaluation value of given row in the table. 
	 * @param bid: which bid (the column in the table are for ourBid and opponentBid)
	 * @return the evaluation of the given row in the bid.
	 * returns null if the bid has no value in that row.
	 * @throws probablly if rownr is out of range 0...issues.size()-1
	 */
	Value getCurrentEval(Bid bid,int rownr)
	{
		if (bid==null) return null;
		Integer ID=IDs.get(rownr); // get ID of the issue in question.
		return bid.getValue(ID); // get the current value for that issue in the bid
	}
	
	
	/**
	 * get a render component
	 * @return the Component that can be rendered to show this cell.
	 */
	public Component getValueAt(int row, int col)
	{
		if (row==issues.size())
		{
			if (col==0) return new JLabel("COST (in your utilspace):");
			if (utilitySpace==null) return new JLabel("No UtilSpace");
			Bid bid;
			if (col==1) bid=opponentOldBid; 
			else  try {bid=getBid(); } 
			catch(Exception e) {bid=null; System.out.println("Internal err with getBid:"+e.getMessage()); };
			String val;
			try	{val=utilitySpace.getCost(bid).toString();	}
			catch (Exception e) { 
				System.out.println("Exception during cost calculation:"+e.getMessage()); 
				//e.printStackTrace();
				val="XXX"; }
			return new JTextArea(val);
		}
		if (row==issues.size()+1)
		{
			if (col==0) return new JLabel("Utility:");
			if (utilitySpace==null) return new JLabel("No UtilSpace");
			Bid bid;
			if (col==1) bid=opponentOldBid; 
			else  try {bid=getBid(); } 
			catch(Exception e) {bid=null; System.out.println("Internal err with getBid:"+e.getMessage()); };
			JProgressBar bar=new JProgressBar(0,100);
			try	{ 
				bar.setValue((int)(0.5+100.0*utilitySpace.getUtility(bid)));	
				bar.setIndeterminate(false);
			}
			catch (Exception e) { 
				System.out.println("Exception during cost calculation:"+e.getMessage()); 
				//e.printStackTrace();
				bar.setIndeterminate(true); }

			return bar;
		}
		switch (col)
		{
		case 0:
			return new JTextArea(issues.get(row).getName());
		case 1:
			Value value= getCurrentEval(opponentOldBid,row);
			if (value==null) return new JTextArea("-");
			return new JTextArea(value.toString());
		case 2:
			return comboBoxes.get(row);
		}
		return null;
	}
	
	
	Bid getBid() throws Exception
	{
		HashMap<Integer, Value> values =new HashMap<Integer, Value> ();
		
		for (int i=0; i<issues.size(); i++)
			values.put(IDs.get(i), (Value)comboBoxes.get(i).getSelectedItem());
		return new Bid(domain,values);
	}

	public void actionPerformed(ActionEvent e) {
		//System.out.println("event d!"+e);
		// update the cost and utility of our own bid.
		fireTableCellUpdated(issues.size(),2);
		fireTableCellUpdated(issues.size()+1,2);
	}

}





/********************************************************************/

class MyCellRenderer implements TableCellRenderer {
	NegoInfo negoinfo;
	
    public MyCellRenderer(NegoInfo n) {	negoinfo=n;  }
 
    	// the default converts everything to string...
    public Component getTableCellRendererComponent(JTable table, Object value,
             boolean isSelected, boolean hasFocus, int row, int column) {
		return negoinfo.getValueAt(row,column);   
	}
}






/********************************************************/


class MyCellEditor extends DefaultCellEditor
{
	NegoInfo negoinfo;
	
    public MyCellEditor(NegoInfo  n)
    {
    	super(new JTextField("vaag")); // Java wants us to call super class, who cares...
		negoinfo=n;
	    setClickCountToStart(1);
    }
	
	public Component getTableCellEditorComponent(JTable table, Object value, 
		boolean isSelected, int row, int column)
	{		return negoinfo.getValueAt(row, column);	}

}



