package negotiator.gui.agentparameters;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import negotiator.exceptions.Warning;
import negotiator.repository.AgentRepItem;
import negotiator.repository.Repository;
import java.util.ArrayList;
import negotiator.tournament.VariablesAndValues.*;

/** Wouter: obsolete?? */

class AgentParametersUI extends JDialog
{
	ArrayList<TournamentVariable> variables;
	
	/** this call will change contents of vars */
	public AgentParametersUI(ArrayList<TournamentVariable> vars,boolean oneValuePerParameter)
	{
		setTitle("Agent Parameters Editor");
		setLayout(new BorderLayout());
		
		final String columnnames[] = {"Parameter Name",oneValuePerParameter? "Value":"Values (comma separated)"};

		dataModel = new AbstractTableModel() {
			
			public int getColumnCount() { 
				return columnnames.length; 
			}
			public int getRowCount() { 
				return variables.size();
			}
			public Object getValueAt(int row, int col) { 
			  	  TournamentVariable var=variables.get(row);
			  	  switch(col)
			  	  {
			  	  case 0:return agt.getName();
			  	  case 1: return agt.getClassPath();
			  	  case 2: return agt.getVersion();
			  	  case 3: return agt.getDescription();
			  	  
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
		JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		addbutton=new JButton("Add Agent");
		addbutton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				try {addrow();}
				catch (Exception err) { new Warning("add failed:"+err); }
			}
		});
		removebutton=new JButton("Remove Agent");
		removebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removerow(); }
		});
		buttons.add(addbutton);
		buttons.add(removebutton);
		
		add(buttons,BorderLayout.SOUTH);
		add(scrollpane,BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	