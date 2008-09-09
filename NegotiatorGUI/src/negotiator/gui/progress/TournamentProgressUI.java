package negotiator.gui.progress;

import java.awt.Container;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import negotiator.Domain;
import negotiator.NegotiationEventListener;
import negotiator.actions.Accept;
import negotiator.actions.EndNegotiation;
import negotiator.events.ActionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationSessionEvent;
import negotiator.tournament.NegotiationSession2;
import negotiator.tournament.VariablesAndValues.AgentParamValue;

@SuppressWarnings("serial")
public class TournamentProgressUI extends JPanel implements NegotiationEventListener{
	private JScrollPane tournamentResults;
	private ProgressUI sessionProgress;
	private JTable resultTable;
	private NegoTableModel resultTableModel; // the table model	
	private NegotiationSession2 negoSession;
	private ArrayList <NegotiationSession2> sessionArray;
	private int session;
	
	public TournamentProgressUI(ProgressUI pUI){
		sessionProgress = pUI;
		negoSession = pUI.session;
		String[] colNames={"Domain1","Domain2","AgentA","AgentB","AgentA params","AgentB params","Rounds","utilA","utilB","Details"};
		resultTableModel = new NegoTableModel (colNames);
		resultTable = new  JTable(resultTableModel);
		
		//add a listener to receive selection events:
	    SelectionListener listener = new SelectionListener(resultTable);
	    resultTable.getSelectionModel().addListSelectionListener(listener);
	    resultTable.getColumnModel().getSelectionModel()
	        .addListSelectionListener(listener);		

		tournamentResults = new JScrollPane(resultTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		Container pane = this;
		pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));
		pane.add(tournamentResults);
		pane.add(sessionProgress);
		setVisible(true);
	}
	
	
	//demo:
	public static void main (String[] args) {
		JFrame f = new JFrame();
		Container p = f.getContentPane();
		TournamentProgressUI tpUI = new TournamentProgressUI(new ProgressUI());
		p.add(tpUI);
		f.pack();
		f.setVisible(true);
	}

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
		
		// add the current session to the array
		sessionArray.add(negoSession);
		
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
