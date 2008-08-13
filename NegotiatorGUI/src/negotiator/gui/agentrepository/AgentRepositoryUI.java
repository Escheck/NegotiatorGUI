package negotiator.gui.agentrepository;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JButton;
import negotiator.repository.*;
import java.util.ArrayList;
import negotiator.repository.*;
import negotiator.exceptions.Warning;

/**
 * A user interface to the agent repository 
 * @author wouter
 *
 */
public class AgentRepositoryUI extends JFrame
{
	
	JButton addbutton, removebutton;
	Repository temp_agent_repos=new Repository(); // TODO locate this somewhere better
	AbstractTableModel dataModel;
	final JTable table;
	
	
	
	public AgentRepositoryUI()
	{
		init_temp_repository();
		
		setTitle("Agent Repository");
		setLayout(new BorderLayout());
		
		dataModel = new AbstractTableModel() {
			final String columnnames[] = {"Agent Name","Filename (full path)","Version","Description"};
			
			public int getColumnCount() { 
				return columnnames.length; 
			}
			public int getRowCount() { 
				return temp_agent_repos.getItems().size();
			}
			public Object getValueAt(int row, int col) { 
			  	  AgentRepItem agt=(AgentRepItem)temp_agent_repos.getItems().get(row);
			  	  switch(col)
			  	  {
			  	  case 0:return agt.getName();
			  	  case 1: return agt.getPath();
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
				addrow(); 
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
		show();
	}
	
	/** remove selected row from table */
	void removerow() {
		int row=table.getSelectedRow();
		System.out.println("remove row "+row);
		if (row<0 || row>temp_agent_repos.getItems().size()) {
			new Warning("Please select one of the rows in the table.");
			return;
		}
		temp_agent_repos.getItems().remove(row);
		dataModel.fireTableRowsDeleted(row, row);
	}
	
	void addrow() {
		System.out.println("add row "+table.getSelectedRow());	
		new AddAgentUI();
	}
	
	
	void init_temp_repository()
	{
		ArrayList<RepItem> items=temp_agent_repos.getItems();
		items.add(new 	AgentRepItem("aap", "/Volumes/aap.class", "apy negotiator"));
		items.add(new 	AgentRepItem("beer", "/Volumes/beer.class", "beary negotiator"));
		items.add(new 	AgentRepItem("BayesianAgent", "agents.BayesianAgent", "simple agent"));
	}
	
	/** run this for a demo of AgentReposUI */
	public static void main(String[] args) 
	{
	 new AgentRepositoryUI();
	}
}
