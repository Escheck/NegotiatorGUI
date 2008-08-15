package negotiator.gui.tournamentvars;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.*;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;

import javax.swing.BoxLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JButton;

import negotiator.Domain;
import negotiator.repository.*;
import negotiator.utility.UtilitySpace;

import javax.swing.JFileChooser;
import java.io.FileFilter;

import java.util.ArrayList;
import negotiator.repository.*;
import negotiator.exceptions.Warning;
import negotiator.gui.agentrepository.AgentRepositoryUI;
import negotiator.gui.domainrepository.DomainRepositoryUI;
import negotiator.gui.tree.TreeFrame;
import negotiator.issue.Objective;
import negotiator.tournament.Tournament;
import negotiator.tournament.VariablesAndValues.*;
import negotiator.repository.*;

class TournamentVarsUI extends JFrame {
	
	Tournament tournament;
	AbstractTableModel dataModel;
	final JTable table;
	Repository domainrepository; // contains all available domains and profiles to pick from.
	Repository agentrepository; // contains all available  agents to pick from.
	
	JButton addvarbutton=new JButton("Add Variable");
	JButton removevarbutton=new JButton("Remove Variable");
	JButton editvarbutton=new JButton("Edit Variable");
	
	public TournamentVarsUI(Tournament t) throws Exception {
		if (t==null) throw new NullPointerException("null tournament");
		
		tournament=t;
		domainrepository=Repository.get_domain_repos();
		agentrepository=Repository.get_agent_repository();
		setTitle("Tournament Editor");
		setLayout(new BorderLayout());

		
		dataModel = new AbstractTableModel() {
			final String columnnames[] = {"Variable","Values"};
			
			public int getColumnCount() { 
				return columnnames.length; 
			}
			
			public int getRowCount() { 
				return tournament.getVariables().size();
			}
			
			public Object getValueAt(int row, int col) {
				TournamentVariable var=tournament.getVariables().get(row);
			  	switch(col)
			  	{
			  	case 0:return var.varToString();
			  	case 1:return var.getValues().toString();
			  	default: new Warning("Illegal column in table "+col);
			  	}
			  	return col;
			}
			public String getColumnName(int column) {
			  	  return columnnames[column];
			}
		};
		table = new JTable(dataModel);
		table.setShowGrid(true);
		JScrollPane scrollpane = new JScrollPane(table);
	 	
	      // CREATE THE BUTTONS
		addvarbutton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				try {addrow();}
				catch (Exception err) { new Warning("add failed:"+err); }
			}
		});
		removevarbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removerow(); }
		});		
		editvarbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editrow(); }
		});
		
		JPanel buttons=new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));

		buttons.add(addvarbutton);
		buttons.add(removevarbutton);
		buttons.add(editvarbutton);
		
		add(buttons,BorderLayout.SOUTH);
		add(scrollpane,BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	
	void editrow() {
		int row=table.getSelectedRow();
		System.out.println("edit row "+row);
	}
	
	/** remove selected row from table */
	void removerow() {
		int row=table.getSelectedRow();
		System.out.println("remove row "+row);
		if (row<0 || row>tournament.getVariables().size()) {
			new Warning("Please select one of the rows in the table.");
			return;
		}
		tournament.getVariables().remove(row);
		dataModel.fireTableRowsDeleted(row, row);
	}
	
		//new AddAgentUI();
	void addrow() throws Exception {
		System.out.println("add row "+table.getSelectedRow());
		
		/*
		AgentRepItem ari=(new AddAgentUI(this)).getAgentRepItem();
		System.out.println("UI returned with "+ari);
		if (ari.getName().length()==0)
			throw new IllegalArgumentException("empty agent name is not allowed");
		if (ari!=null) {
			int row=temp_agent_repos.getItems().size();
			AgentRepItem otheragt=temp_agent_repos.getAgentOfClass(ari.getClassPath());
			if (otheragt!=null)
				throw new IllegalArgumentException("Only one reference to a class is allowed, Agent "+otheragt.getName()+" is already of given class!");
			temp_agent_repos.getItems().add(ari);
			dataModel.fireTableRowsInserted(row, row);
		}
		*/
	}
	
	
	/** make sure first three rows are Profile, AgentA, AgentB */
	void correct_tournament(Tournament t)
	{
		ArrayList<TournamentVariable> vars=t.getVariables();
		correctposition(vars,0,new ProfileVariable());
		correctposition(vars,1,new AgentVariable());
		correctposition(vars,2,new AgentVariable());
	}

	/** check that variable of type given in stub is at expected position.
	 * If not, move the first occurence after *that position* to the given position
	 * Or create new instance of that type if there is none.
	 * @param vars is array of TournamentVariables.
	 * @param pos expected position
	 * @param stub: TournamentVariable of the expected type, which is substituted if no object of required type is in the array at all.
	 */
	void correctposition(ArrayList<TournamentVariable> vars, int expectedpos, TournamentVariable stub) {
		// find the profile variable(s) and its position. Remove multiple occurences.
		TournamentVariable v=null;
		int pos=-1;
		for (int i=expectedpos; i<vars.size(); i++) {
			if (vars.get(i).getClass().equals(stub.getClass())) {
				if (v==null) {
					pos=i; v=vars.get(i);
				} else {
					new Warning("tournament contains multiple "+stub.getClass()+" variables. Removing all but the first one.");
					vars.remove(i);
					i--; // re-check this index
				}
			}
		}
	
		if (pos!=expectedpos) {
			// incorrect profile
			if (v==null) {
				new Warning("tournament has no "+stub.getClass()+" variable. adding a stub");
				vars.add(expectedpos,stub);
			} else {
				new Warning("tournament has "+stub.getClass()+" variable not on expected place. Moving it to correct position.");
				vars.remove(pos);
				vars.add(expectedpos, v);
			}
		}
	}
	
	/** run this for a demo of AgentReposUI */
	public static void main(String[] args) 
	{
		try {
			Tournament t=new Tournament();
			new TournamentVarsUI(t); 
		}
		catch (Exception e) { new Warning("TournamentVarsUI failed to launch: "+e); }
	}
}