/*
 * TournamentProgressUI2.java
 *
 * Created on 9 september 2008, 12:13
 */

package negotiator.gui.progress;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import negotiator.NegotiationEventListener;
import negotiator.actions.Accept;
import negotiator.actions.EndNegotiation;
import negotiator.events.ActionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationSessionEvent;
import negotiator.gui.progress.TournamentProgressUI.SelectionListener;
import negotiator.tournament.NegotiationSession2;
import negotiator.tournament.VariablesAndValues.AgentParamValue;

/**
 *
 * @author  Dima
 */
public class TournamentProgressUI2 extends javax.swing.JPanel implements NegotiationEventListener{
	private NegoTableModel resultTableModel; // the table model	
	private NegotiationSession2 negoSession;
	private int session;
	private ProgressUI2 sessionProgress;
    /** Creates new form TournamentProgressUI2 */
    public TournamentProgressUI2(ProgressUI2 pUI) {
    	jPanel1 = pUI;
        initComponents(); 
		sessionProgress = pUI;
		negoSession = pUI.session;
		String[] colNames={"Domain1","Domain2","AgentA","AgentB","AgentA params","AgentB params","Rounds","utilA","utilB","Details"};
		resultTableModel = new NegoTableModel (colNames);
		resultTable.setModel(resultTableModel);
		//add a listener to receive selection events:
	    SelectionListener listener = new SelectionListener(resultTable);
	    resultTable.getSelectionModel().addListSelectionListener(listener);
	    resultTable.getColumnModel().getSelectionModel()
	        .addListSelectionListener(listener);		
		//pnlSession.add(sessionProgress);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        pnlTournamentOverView = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        pnlSession = new javax.swing.JPanel();
       // jPanel1 = new javax.swing.JPanel();

        setName("Form"); // NOI18N

        jSplitPane1.setDividerSize(3);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(negotiator.gui.NegoGUIApp.class).getContext().getResourceMap(TournamentProgressUI2.class);
        pnlTournamentOverView.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("pnlTournamentOverView.border.title"))); // NOI18N
        pnlTournamentOverView.setName("pnlTournamentOverView"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        resultTable.setModel(new javax.swing.table.DefaultTableModel(
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
        resultTable.setName("resultTable"); // NOI18N
        jScrollPane1.setViewportView(resultTable);

        org.jdesktop.layout.GroupLayout pnlTournamentOverViewLayout = new org.jdesktop.layout.GroupLayout(pnlTournamentOverView);
        pnlTournamentOverView.setLayout(pnlTournamentOverViewLayout);
        pnlTournamentOverViewLayout.setHorizontalGroup(
            pnlTournamentOverViewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
        );
        pnlTournamentOverViewLayout.setVerticalGroup(
            pnlTournamentOverViewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        jSplitPane1.setTopComponent(pnlTournamentOverView);

        pnlSession.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("pnlSession.border.title"))); // NOI18N
        pnlSession.setName("pnlSession"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

//        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
//        jPanel1.setLayout(jPanel1Layout);
//        jPanel1Layout.setHorizontalGroup(
//            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//            .add(0, 390, Short.MAX_VALUE)
//        );
//        jPanel1Layout.setVerticalGroup(
//            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//            .add(0, 221, Short.MAX_VALUE)
//        );

        org.jdesktop.layout.GroupLayout pnlSessionLayout = new org.jdesktop.layout.GroupLayout(pnlSession);
        pnlSession.setLayout(pnlSessionLayout);
        pnlSessionLayout.setHorizontalGroup(
            pnlSessionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlSessionLayout.setVerticalGroup(
            pnlSessionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(pnlSession);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel pnlSession;
    private javax.swing.JPanel pnlTournamentOverView;
    private javax.swing.JTable resultTable;
    // End of variables declaration//GEN-END:variables
	public void handleActionEvent(ActionEvent evt) {
		System.out.println("Caught event "+evt+ "in TournamentProgressUI");	
		if ((evt.getAct() instanceof EndNegotiation)| (evt.getAct()instanceof Accept)){
			System.out.println("end or accept --> fill table");
			resultTable.getModel().setValueAt(sessionProgress.round,session-1,6);//rounds
			resultTable.getModel().setValueAt(evt.getNormalizedUtilityA(),session-1,7);//util a
			resultTable.getModel().setValueAt(evt.getNormalizedUtilityB(),session-1,8);//util b
			resultTable.getModel().setValueAt("",session-1,9);//details???
		}
	}

	public void handleLogMessageEvent(LogMessageEvent evt) {
		System.out.println("Caught event "+evt+ "in TournamentProgressUI");	
	}


	public void handeNegotiationSessionEvent(NegotiationSessionEvent evt) {
		System.out.println("Caught event "+evt+ "in TournamentProgressUI");	
		session+=1;
		if(session>resultTable.getModel().getRowCount()){
			resultTableModel.addRow();
		}
		//fill the table
		String agentAParams="";String agentBParams="";
		negoSession = evt.getSession();
		sessionProgress.session = negoSession;
		negoSession.addNegotiationEventListener(sessionProgress);
		int i=0;
		if(!(negoSession.getAgentAparams()==null)) {
			for(AgentParamValue p: negoSession.getAgentAparams()) 
				{agentAParams+=p; i++;}
			resultTable.getModel().setValueAt(agentAParams,session-1,4);//agent a param
		}
		i=0;
		if(!(negoSession.getAgentBparams()==null)) {
			for(AgentParamValue p: negoSession.getAgentBparams())
			{agentBParams+=p; i++;};
			resultTable.getModel().setValueAt(agentBParams,session-1,5);//agent a param
		}
		resultTable.getModel().setValueAt(negoSession.getProfileArep(),session-1,0);//profile 1
		resultTable.getModel().setValueAt(negoSession.getProfileBrep(),session-1,1);//profile 2
		resultTable.getModel().setValueAt(negoSession.getAgentAStrategyName(),session-1,2);//agent a
		resultTable.getModel().setValueAt(negoSession.getAgentBStrategyName(),session-1,3);//agent b
	    
		//clear the ProgressGUI
		System.out.println("resetting the GUI after NegotiationSessionEvent.");
		sessionProgress.resetGUI();
		sessionProgress.setNegotiationSession(negoSession);
	}
	public class SelectionListener implements ListSelectionListener {
        JTable table;
    
        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        SelectionListener(JTable table) {
            this.table = table;
        }
        public void valueChanged(ListSelectionEvent e) {
            // If cell selection is enabled, both row and column change events are fired
            if (e.getSource() == table.getSelectionModel()
                  && table.getRowSelectionAllowed()) {
                // Column selection changed
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
                System.out.println("selection event happened 1;"+first+" "+last);
            } else if (e.getSource() == table.getColumnModel().getSelectionModel()
                   && table.getColumnSelectionAllowed() ){
                // Row selection changed
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
                System.out.println("selection event happened 2;"+first+" "+last);
            }
    
            if (e.getValueIsAdjusting()) {
                // The mouse button has not yet been released
            }
        }
    }
}
