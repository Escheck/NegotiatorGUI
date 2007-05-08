/*
 * EnterBidDialog.java
 *
 * Created on November 16, 2006, 10:18 AM
 */

package negotiator.gui;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.NegotiationTemplate;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.exceptions.BidDoesNotExistInDomainException;
import negotiator.issue.*;

/**
 *
 * @author  dmytro
 */
public class EnterBidDialog extends javax.swing.JDialog {
    private Action selectedAction;
    private Agent agent;
    private Bid opponentBid ;
    /** Creates new form EnterBidDialog */
    public EnterBidDialog(Agent agent, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.agent = agent;
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroupActions = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        lblOpponentAction = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableOpponentBid = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        buttonAccpet = new javax.swing.JRadioButton();
        buttonEnd = new javax.swing.JRadioButton();
        buttonSendOffer = new javax.swing.JRadioButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableMyBid = new javax.swing.JTable();
        buttonSend = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Choose action");
        setMinimumSize(new java.awt.Dimension(480, 320));
        setResizable(false);
        jLabel1.setText("Opponents action:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblOpponentAction.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblOpponentAction.setText("jLabel2");
        getContentPane().add(lblOpponentAction, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        tableOpponentBid.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tableOpponentBid);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 450, 40));

        jLabel2.setText("Your action:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        buttonGroupActions.add(buttonAccpet);
        buttonAccpet.setText("Accept Opponent's Bid");
        buttonAccpet.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonAccpet.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonAccpet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAccpetActionPerformed(evt);
            }
        });

        getContentPane().add(buttonAccpet, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        buttonGroupActions.add(buttonEnd);
        buttonEnd.setText("End Negotiation");
        buttonEnd.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonEnd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        getContentPane().add(buttonEnd, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, -1, -1));

        buttonGroupActions.add(buttonSendOffer);
        buttonSendOffer.setSelected(true);
        buttonSendOffer.setText("Send Offer:");
        buttonSendOffer.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonSendOffer.setMargin(new java.awt.Insets(0, 0, 0, 0));
        getContentPane().add(buttonSendOffer, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, -1, -1));

        tableMyBid.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tableMyBid);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 450, 50));

        buttonSend.setText("Send");
        buttonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSendActionPerformed(evt);
            }
        });

        getContentPane().add(buttonSend, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 250, -1, -1));

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        getContentPane().add(buttonCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 250, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSendActionPerformed
// TODO add your handling code here:
        if(buttonAccpet.isSelected()) {
            selectedAction = new Accept(agent, opponentBid);
        } else
        if(buttonCancel.isSelected()) {
            selectedAction = new EndNegotiation(agent);;
        } else
        if(buttonSendOffer.isSelected()) {
            MyActionTableModel model =  ((MyActionTableModel)tableMyBid.getModel());
            Bid bid;
            try {
                bid =  model.getMyBid();
            } catch (BidDoesNotExistInDomainException e) {
                JOptionPane.showMessageDialog(null, "You selected bid that does not exist.");
                return;
            }
            selectedAction = new Offer(agent,bid);
        }
        setVisible(false);
                
        return;        
    }//GEN-LAST:event_buttonSendActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
// TODO add your handling code here:
        selectedAction = null;
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonAccpetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAccpetActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_buttonAccpetActionPerformed
    
      
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton buttonAccpet;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JRadioButton buttonEnd;
    private javax.swing.ButtonGroup buttonGroupActions;
    private javax.swing.JButton buttonSend;
    private javax.swing.JRadioButton buttonSendOffer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblOpponentAction;
    private javax.swing.JTable tableMyBid;
    private javax.swing.JTable tableOpponentBid;
    // End of variables declaration//GEN-END:variables
    public Action askUserForAction(Action opponentAction, Bid myPreviousBid, NegotiationTemplate nt) {
        opponentBid = null;
        if(opponentAction==null) {
            lblOpponentAction.setText("Opponent did not send any action.");            
        } else
        if(opponentAction instanceof Accept) {
            lblOpponentAction.setText("Opponent accept the following bid:");
            opponentBid = ((Accept)opponentAction).getBid();
        } else
        if(opponentAction instanceof EndNegotiation) {
            lblOpponentAction.setText("Opponent cancels the negotiation.");
        } else
        if(opponentAction instanceof Offer) {
            lblOpponentAction.setText("Opponent proposes the following bid:");
            opponentBid = ((Offer)opponentAction).getBid();
        }            
        tableOpponentBid.setModel(new OpponentActionTableModel(opponentBid, nt));
        MyActionTableModel myActionTableModel = new MyActionTableModel(myPreviousBid,nt);
        tableMyBid.setModel(myActionTableModel);
        for(int i=0;i<nt.getDomain().getNumberOfIssues();i++) {
            myActionTableModel.setUpIssuesColumns(tableMyBid,nt.getDomain().getIssue(i), 
                    tableMyBid.getColumnModel().getColumn(i));
        }
            
        setVisible(true);
        
        return selectedAction;
    }
    class OpponentActionTableModel extends AbstractTableModel {
        private Bid opponentBid;
        private NegotiationTemplate nt;
        public OpponentActionTableModel(Bid opponentBid, NegotiationTemplate nt)  {
            this.opponentBid = opponentBid;
            this.nt = nt;
        }
        public int getColumnCount() {return nt.getDomain().getNumberOfIssues();}

        public int getRowCount() {return 1;}

        public String getColumnName(int col) {
            return nt.getDomain().getIssue(col).getName();
        }
        public Class getColumnClass(int c) {return (new String()).getClass();}        

        public Object getValueAt(int row, int col) {
            Object value = null;
            if(opponentBid!= null) 
//                value = nt.getDomain().getIssue(col).getValue(opponentBid.getValueIndex(col));
            	// Assume discrete-valued bids only.
            	value = opponentBid.getValue(col).getStringValue();
            return value;
        }


       
    }
    class MyActionTableModel extends AbstractTableModel {
        public int[] myBid;
        
        private NegotiationTemplate nt;
        public MyActionTableModel(Bid myPreviousBid, NegotiationTemplate nt)  {
            myBid = new int[nt.getDomain().getNumberOfIssues()];
            if(myPreviousBid!=null)
                for(int i=0;i<nt.getDomain().getNumberOfIssues();i++)
//                    myBid[i] = myPreviousBid.getValueIndex(i);
                	// Assume discrete-value issues only.
                	myBid[i] = ((DiscreteIssue)nt.getDomain().getIssue(i)).getValueIndex(((ValueDiscrete)myPreviousBid.getValue(i)).getValue());
            else for(int i=0;i<nt.getDomain().getNumberOfIssues();i++) myBid[i] = -1;
            this.nt = nt;
        }
        public int getColumnCount() {
            return nt.getDomain().getNumberOfIssues();
        }
        public int getRowCount() {
            return 1;
        }
        public String getColumnName(int col) {
            return nt.getDomain().getIssue(col).getName();
        }
        public Object getValueAt(int row, int col) {
            Object value = null;
            if(myBid[col]>=0)
            	// Assume discrete-valued issues only.
                value = ((DiscreteIssue)nt.getDomain().getIssue(col)).getValue(myBid[col]);
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
        public void setUpIssuesColumns(JTable table, Issue issue, TableColumn issueColumn) {
            //Set up the editor for the sport cells.
            JComboBox comboBox = new JComboBox();
            // Assumption: Discrete-valued issues only
            for (int i=0;i<((DiscreteIssue)issue).getNumberOfValues();i++) {
                comboBox.addItem(((DiscreteIssue)issue).getValue(i));    
            }
            issueColumn.setCellEditor(new DefaultCellEditor(comboBox));
            
            //Set up tool tips for the sport cells.
            DefaultTableCellRenderer renderer =
                    new DefaultTableCellRenderer();
            renderer.setToolTipText("Click for combo box");
            issueColumn.setCellRenderer(renderer);
        }
        public void setValueAt(Object value, int row, int col) {
            myBid[col] = ((DiscreteIssue)nt.getDomain().getIssue(col)).getValueIndex(value.toString());
            fireTableCellUpdated(row, col);
        }
        public Bid getMyBid() throws BidDoesNotExistInDomainException {
            Bid bid = null;
//            try {
//                bid = nt.getDomain().makeBid(myBid);
            	Value[] values = new Value[myBid.length];
            	for (int i=0; i<myBid.length; i++) {
            		values[i] = Value.makeValue(ISSUETYPE.DISCRETE, ((DiscreteIssue)nt.getDomain().getIssue(i)).getValue(myBid[i]));
            	}
            	bid = new Bid(nt.getDomain(), values);
//            } catch (BidDoesNotExistInDomainException e) {
//                throw e;
//            }
            return bid;
        }
       
    }
    
}
