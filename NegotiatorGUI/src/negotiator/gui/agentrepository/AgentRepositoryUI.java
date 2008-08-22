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
import negotiator.exceptions.Warning;
import java.net.URL;


/**
 * A user interface to the agent repository 
 * @author wouter
 *
 */
public class AgentRepositoryUI extends JFrame
{
	
	JButton addbutton, removebutton;
	Repository agentrepository;
	AbstractTableModel dataModel;
	final JTable table;
	 
	public AgentRepositoryUI() throws Exception
	{
		agentrepository = Repository.get_agent_repository();
		setTitle("Agent Repository");
		setLayout(new BorderLayout());

		dataModel = new AbstractTableModel() {
			final String columnnames[] = {"Agent Name","Filename (full path)","Version","Description"};
			
			public int getColumnCount() { 
				return columnnames.length; 
			}
			public int getRowCount() { 
				return agentrepository.getItems().size();
			}
			public Object getValueAt(int row, int col) { 
			  	  AgentRepItem agt=(AgentRepItem)agentrepository.getItems().get(row);
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
	
	/** remove selected row from table */
	void removerow() {
		int row=table.getSelectedRow();
		System.out.println("remove row "+row);
		if (row<0 || row>agentrepository.getItems().size()) {
			new Warning("Please select one of the rows in the table.");
			return;
		}
		agentrepository.getItems().remove(row);
		dataModel.fireTableRowsDeleted(row, row);
		agentrepository.save();

	}
	
		//new AddAgentUI();
	void addrow() throws Exception {
		System.out.println("add row "+table.getSelectedRow());
		AgentRepItem ari=(new AddAgentUI(this)).getAgentRepItem();
		System.out.println("UI returned with "+ari);
		if (ari.getName().length()==0)
			throw new IllegalArgumentException("empty agent name is not allowed");
		if (ari!=null) {
			int row=agentrepository.getItems().size();
			AgentRepItem otheragt=agentrepository.getAgentOfClass(ari.getClassPath());
			if (otheragt!=null)
				throw new IllegalArgumentException("Only one reference to a class is allowed, Agent "+otheragt.getName()+" is already of given class!");
			agentrepository.getItems().add(ari);
			dataModel.fireTableRowsInserted(row, row);
			agentrepository.save();
		}
	}
	
	

	/** run this for a demo of AgentReposUI */
	public static void main(String[] args) 
	{
		try { new AgentRepositoryUI(); }
		catch (Exception e) { new Warning("launch of AgentRepositoryUI failed: "+e); }
	}
}
