/*
 * EnterBidDialog.java
 *
 * Created on November 16, 2006, 10:18 AM
 */

package negotiator.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.awt.Container;


import negotiator.Agent;
import negotiator.Bid;
import negotiator.NegotiationTemplate;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.exceptions.BidDoesNotExistInDomainException;
import negotiator.issue.*;
//import negotiator.issue.Issue;

/**
 *
 * @author  W.Pasman
 */
public class EnterBidDialog2 extends JDialog {
	
    private Action selectedAction;
    private Agent agent;
    private Bid opponentBid ;
    

    private JTextArea negotiationMessages=new JTextArea();
    
    private JButton buttonBid=new JButton("Do Bid");
    private JButton buttonSkip=new JButton("Skip Turn");
    private JButton buttonEnd=new JButton("End Nego");
    private JButton buttonAccept=new JButton("Accept");
    private JPanel buttonPanel=new JPanel();
    
    private JLabel CostLabel=new JLabel("Cost");
    private JLabel UtilityLabel=new JLabel("Utility");
    
    
    private JTable BidTable; // table showing the bids, adjustable
    
    
    
    public EnterBidDialog2(Agent agent, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.agent = agent;
        initComponents();
    }
    
    private void initComponents() {
    	
    	Container pane=getContentPane();
        pane.setLayout(new BorderLayout());
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Choose action for agent "+agent.getName());
        //setMinimumSize(new java.awt.Dimension(480, 320));

        	// todo: create north field: the message field
        
        	// create center panel: the bid table
        BidTable = new javax.swing.JTable();
        BidTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null}
                },
                new String [] {
                    "Title 1", "Title 2", "Title 3", "Title 4"
                }
            ));
        pane.add(BidTable,"Center");

        	// create south panel: the buttons:
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(buttonBid);
        buttonPanel.add(buttonSkip);
        buttonPanel.add(buttonEnd);
        buttonPanel.add(buttonAccept);
        buttonAccept.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
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
        pack();
    }
    
    
    
    
    private void buttonBidActionPerformed(java.awt.event.ActionEvent evt) 
    {

        MyActionTableModel model =  ((MyActionTableModel)BidTable.getModel());
        Bid bid;
        try {
            bid =  model.getMyBid();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "There is a problem with your bid: "+e.getMessage());
            return;
        }
        selectedAction = new Offer(agent,bid);
        setVisible(false);
                
        return;        
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println("cancel performed!");
        selectedAction = null;
        setVisible(false);
    }

    private void buttonAcceptActionPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println("Accept performed");
    }
    
      
    public Action askUserForAction(Action opponentAction, Bid myPreviousBid, NegotiationTemplate nt) {
        opponentBid = null;
        if(opponentAction==null) {
        	negotiationMessages.setText("Opponent did not send any action.");            
        }
        if(opponentAction instanceof Accept) {
        	negotiationMessages.setText("Opponent accepted the following bid:");
            opponentBid = ((Accept)opponentAction).getBid();
        }
        if(opponentAction instanceof EndNegotiation) {
        	negotiationMessages.setText("Opponent cancels the negotiation.");
        }
        if(opponentAction instanceof Offer) {
        	negotiationMessages.setText("Opponent proposes the following bid:");
            opponentBid = ((Offer)opponentAction).getBid();
        }
        
        // load the bidding info into the table
        BidTable.setModel(new NegoTableModel(myPreviousBid,opponentBid,nt));
        /*
        tableOpponentBid.setModel(new OpponentActionTableModel(opponentBid, nt));
        MyActionTableModel myActionTableModel = new MyActionTableModel(myPreviousBid,nt);
        tableMyBid.setModel(myActionTableModel);
        ArrayList<Issue> issues=nt.getDomain().getIssues();
        for(int i=0;i<issues.size();i++)
           myActionTableModel.setUpIssuesColumn(tableMyBid,issues.get(i), 
                   tableMyBid.getColumnModel().getColumn(i));
        */
        
        setVisible(true);
        
        return selectedAction;
    }
        
        
    /** 
     * The NegoTableModel is a table showing the current bidding info.
     * @author W.Pasman
     *
     */
    class NegoTableModel extends AbstractTableModel
    {
    	private Bid ourBid;
    	private Bid opponentBid;
        private NegotiationTemplate negotemplate;
        private ArrayList<Issue> issues; // the domain issues.
        
        public NegoTableModel(Bid ourBidP, Bid opponentBidP, NegotiationTemplate ntP)  {
        	ourBid=ourBidP;
            opponentBid = opponentBidP;
            negotemplate = ntP;
            issues=negotemplate.getDomain().getIssues();
        }
        
        // last two rowsx are for "cost" and "utility"
        public int getRowCount() {return issues.size()+2; }
        public int getColumnCount() {return 3; }

        public String getColumnName(int col) {
        	String[] columnNames={"","Last bid of opponent","Set your current bid"};
        	return columnNames[col];
        }
        
        public Class getColumnClass(int c) {return (new String()).getClass();}        
        public Object getValueAt(int row, int col) {
        	switch (col)
        	{
        	case 0:
                // we need to implement the rownames oruselves, there is no getRowName() function for JTable.
        		if (row<issues.size()) return issues.get(row).getName();
        		if (row==issues.size()) return "Cost";
        		return "Utility";
        	case 1:  return getValueFromBid(opponentBid,row);
        	case 2: return getValueFromBid(ourBid,row);
        	default: return "ERROR";
        	}
        }   
        
        /**
         * get appropriate row value for this bid.
         * @param bid
         * @param row
         * @return
         */
        String	getValueFromBid(Bid bid, int row)
        {
    		if (row<issues.size()) return issues.get(row).getName();
            if(bid[row]>=0)
            	// Assume discrete-valued issues only.
                return ((IssueDiscrete)negotemplate.getDomain().getIssue(row)).getValue(bid[row]);
            else
                return "";
        }
        
        /**
         * extract a bid from the GUI
         * @return the Bid
         * @throws Exception
         */
        public Bid getMyBid() throws Exception {
            Bid bid = null;
        	HashMap<Integer, Value> values = new HashMap<Integer,Value>();
        	ArrayList<Issue> issues=negoTemplate.getDomain().getIssues();
        	for (int i=0; i<myBid.length; i++) {
        		Issue iss=issues.get(i);
        		values.put(new Integer(iss.getNumber()), 
        				(((IssueDiscrete)negoTemplate.getDomain().getIssue(i)).getValue(myBid[i])));
        	}
        	bid = new Bid(negoTemplate.getDomain(), values);
            return bid;
        }

    }
        
    
    
    
    
    class OpponentActionTableModel extends AbstractTableModel {
        private Bid opponentBid;
        private NegotiationTemplate nt;
        public OpponentActionTableModel(Bid opponentBid, NegotiationTemplate nt)  {
            this.opponentBid = opponentBid;
            this.nt = nt;
        }
        public int getColumnCount() {return nt.getDomain().getIssues().size(); }

        public int getRowCount() {return 1;}

        public String getColumnName(int col) {
            return nt.getDomain().getIssue(col).getName();
        }
        public Class getColumnClass(int c) {return (new String()).getClass();}        

        public Object getValueAt(int row, int col) {
            Object value = null;
            ArrayList<Issue> issues=nt.getDomain().getIssues();
            if(opponentBid!= null) 
//                value = nt.getDomain().getIssue(col).getValue(opponentBid.getValueIndex(col));
            	// Assume discrete-valued bids only.
            	value = opponentBid.getValue(issues.get(col).getNumber()).toString();
            return value;
        }   
    }
    
    
    
    
    class MyActionTableModel extends AbstractTableModel {
        public int[] myBid; // array of 
        
        private NegotiationTemplate negoTemplate;
        public MyActionTableModel(Bid myPreviousBid, NegotiationTemplate nt)  {
            negoTemplate = nt;
        	int nissues=getColumnCount() ;
        	myBid = new int[nissues];
        	ArrayList<Issue> issues=nt.getDomain().getIssues();
            if(myPreviousBid!=null)
                for(int i=0;i<nt.getDomain().getIssues().size();i++)
                {
                	int issuenr=issues.get(i).getNumber();
                	myBid[i] = ((IssueDiscrete)issues.get(i)).
                		getValueIndex(((ValueDiscrete)myPreviousBid.getValue(issuenr)).getValue());

                }
            else for(int i=0;i<nissues;i++) myBid[i] = -1;
        }
        
        public int getColumnCount() {
            return negoTemplate.getDomain().getIssues().size();
        }
        public int getRowCount() {
            return 1;
        }
        public String getColumnName(int col) {
            return negoTemplate.getDomain().getIssue(col).getName();
        }
        public Object getValueAt(int row, int col) {
            Object value = null;
            if(myBid[col]>=0)
            	// Assume discrete-valued issues only.
                value = ((IssueDiscrete)negoTemplate.getDomain().getIssue(col)).getValue(myBid[col]);
            else
                value = new String("");
            return value;
        }
        public Class getColumnClass(int c) {
            return (new String()).getClass();
        }
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            return true;
        }
        public void setUpIssuesColumn(JTable table, Issue issue, TableColumn issueColumn) {
            //Set up the editor for the sport cells.
            JComboBox comboBox = new JComboBox();
            // Assumption: Discrete-valued issues only
            for (int i=0;i<((IssueDiscrete)issue).getNumberOfValues();i++) {
                comboBox.addItem(((IssueDiscrete)issue).getValue(i));    
            }
            issueColumn.setCellEditor(new DefaultCellEditor(comboBox));
            
            //Set up tool tips for the sport cells.
            DefaultTableCellRenderer renderer =
                    new DefaultTableCellRenderer();
            renderer.setToolTipText("Click for combo box");
            issueColumn.setCellRenderer(renderer);
        }
        public void setValueAt(Object value, int row, int col) {
            myBid[col] = ((IssueDiscrete)negoTemplate.getDomain().getIssue(col)).getValueIndex(value.toString());
            fireTableCellUpdated(row, col);
        }
        
        /**
         * extract a bid from the GUI
         * @return the Bid
         * @throws Exception
         */
        public Bid getMyBid() throws Exception {
            Bid bid = null;
        	HashMap<Integer, Value> values = new HashMap<Integer,Value>();
        	ArrayList<Issue> issues=negoTemplate.getDomain().getIssues();
        	for (int i=0; i<myBid.length; i++) {
        		Issue iss=issues.get(i);
        		values.put(new Integer(iss.getNumber()), 
        				(((IssueDiscrete)negoTemplate.getDomain().getIssue(i)).getValue(myBid[i])));
        	}
        	bid = new Bid(negoTemplate.getDomain(), values);
            return bid;
        }
       
    }
    
}
