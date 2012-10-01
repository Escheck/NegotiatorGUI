package negotiator.gui.progress;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import negotiator.NegotiationEventListener;
import negotiator.actions.Accept;
import negotiator.actions.EndNegotiation;
import negotiator.events.ActionEvent;
import negotiator.events.BilateralAtomicNegotiationSessionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationSessionEvent;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

/**
 *
 * @author  Dima
 * Modified by Alex Dirkzwager and Mark Hendrikx to eliminate memory leaks.
 */
public class TournamentProgressUI2 extends javax.swing.JPanel implements NegotiationEventListener{
	private NegoTableModel resultTableModel; // the table model	
	private BilateralAtomicNegotiationSession negoSession;
	private int session;
	private ProgressUI2 sessionProgress;
	/** modified Wouter 4nov08: SesssionDetailsUI contains list of pairs <session number, ProgressUI> */
	private Hashtable<Integer,ProgressUI2> SessionDetailsUI=new Hashtable<Integer,ProgressUI2>();
	
    /** Creates new form TournamentProgressUI2 */
    public TournamentProgressUI2(ProgressUI2 pUI) {
    	jPanel1 = pUI;
        initComponents(); 
		sessionProgress = pUI;
		negoSession = pUI.session;
		String[] colNames={"Prof. A","Prof. B","AgentA","AgentB","AgentA params","AgentB params","Rounds","utilA","utilB","utilA discount","utilB discount", "Time"};
		resultTableModel = new NegoTableModel (colNames);
		resultTable.setModel(resultTableModel);
		// add a listener to receive selection events:
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
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
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
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
    
    /**
     * Tournament overview at the top
     */
	public void handleActionEvent(ActionEvent evt) {
		//System.out.println("Caught event "+evt+ "in TournamentProgressUI");	
		if ((evt.getAct() instanceof EndNegotiation) || (evt.getAct()instanceof Accept) || evt.isFinalActionEvent()){
			//System.out.println("end or accept --> fill table");
			resultTable.getModel().setValueAt(evt.getRound(),session-1,6);//rounds
			resultTable.getModel().setValueAt(evt.getNormalizedUtilityA(),session-1,7);//util a
			resultTable.getModel().setValueAt(evt.getNormalizedUtilityB(),session-1,8);//util b
			resultTable.getModel().setValueAt(evt.getUtilADiscount(),session-1,9);//util a
			resultTable.getModel().setValueAt(evt.getUtilBDsicount(),session-1,10);//util b
			resultTable.getModel().setValueAt(evt.getTime(),session-1,11);//details???
		}
	}

	public void handleLogMessageEvent(LogMessageEvent evt) {
		//System.out.println("Caught event "+evt+ "in TournamentProgressUI");	
	}

	public void handleBlateralAtomicNegotiationSessionEvent(
			BilateralAtomicNegotiationSessionEvent evt) {
		//System.out.println("Caught event "+evt+ "in TournamentProgressUI");	
		session+=1;
		if(session>resultTable.getModel().getRowCount()){
			resultTableModel.addRow();
		}
		//fill the table
		String agentAParams="";String agentBParams="";
		negoSession = evt.getSession();
		sessionProgress.session = negoSession;
		//if(negoSession.sessionTestNumber<1)
		negoSession.addNegotiationEventListener(sessionProgress);
		
		int i=0;
		if(!(negoSession.getAgentAparams()==null)) {
			for(Entry<AgentParameterVariable,AgentParamValue> entry: negoSession.getAgentAparams().entrySet()) 
				{agentAParams+= entry.getKey().varToString() + " " + entry.getValue().toString(); i++;}
			resultTable.getModel().setValueAt(agentAParams,session-1,4);//agent a param
		}
		i=0;
		if(!(negoSession.getAgentBparams()==null)) {
			for(Entry<AgentParameterVariable,AgentParamValue> entry: negoSession.getAgentBparams().entrySet()) 
				{agentBParams+= entry.getKey().varToString() + " " + entry.getValue().toString(); i++;}
			resultTable.getModel().setValueAt(agentBParams,session-1,5);//agent a param
		}
		resultTable.getModel().setValueAt(evt.getProfileA().getName(),session-1,0);//profile 1
		resultTable.getModel().setValueAt(evt.getProfileB().getName(),session-1,1);//profile 2
		resultTable.getModel().setValueAt(evt.getAgentAName(),session-1,2);//agent a
		resultTable.getModel().setValueAt(evt.getAgentBName(),session-1,3);//agent b
	    
		//clear the ProgressGUI
		//System.out.println("resetting the GUI after NegotiationSessionEvent.");
		sessionProgress.resetGUI();
		sessionProgress.setNegotiationSession(negoSession);
		
	}

	public void handeNegotiationSessionEvent(NegotiationSessionEvent evt) {
		
	}
}
