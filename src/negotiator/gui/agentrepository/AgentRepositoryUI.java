package negotiator.gui.agentrepository;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import negotiator.repository.*;
import negotiator.exceptions.Warning;
import negotiator.gui.NegoGUIComponent;

/**
 * A user interface to the agent repository 
 * @author wouter
 *
 */
public class AgentRepositoryUI implements NegoGUIComponent
{
	
	JFrame frame;
	JButton addbutton, removebutton;
	Repository agentrepository;
	AbstractTableModel dataModel;
	final JTable table;
	public AgentRepositoryUI(JTable  pTable) {
		this.table = pTable;
		agentrepository = Repository.get_agent_repository();
		
		initTable();
		table.setModel(dataModel);
		table.setShowVerticalLines(false);
		table.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseReleased(MouseEvent e) {
	            int r = table.rowAtPoint(e.getPoint());
	            if (r >= 0 && r < table.getRowCount()) {
	                table.setRowSelectionInterval(r, r);
	            } else {
	                table.clearSelection();
	            }

	            int rowindex = table.getSelectedRow();
	            if (rowindex < 0)
	                return;
	            if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
	                JPopupMenu popup = createPopupMenu();
	                popup.show(e.getComponent(), e.getX(), e.getY());
	            }
	        }
		});
	}

	private JPopupMenu createPopupMenu() {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem addAgent = new JMenuItem("Add agent");
		JMenuItem removeAgent = new JMenuItem("Remove agent");
		popup.add(addAgent);
		popup.add(removeAgent);
		return popup;
	}
	
	private void initTable() {
		dataModel = new AbstractTableModel() {
			private static final long serialVersionUID = -4985008096999143587L;
			final String columnnames[] = {"Agent Name","Description"};
			
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
			  	  case 0:
			  		  String error = "";
			  		  if (agt.getVersion().equals("ERR")) {
			  			  error = " (LOADING FAILED)";
			  		  }
			  		  return agt.getName() + error;
			  	  case 1:
			  		  return agt.getDescription();
			  	  
			  	  }
			  	  return col;
			}
			public String getColumnName(int column) {
			  	  return columnnames[column];
			}
		};
		
	}
	/** remove selected row from table */
	public void removerow() {
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
	
	public void addAction() {
		// TODO Auto-generated method stub
		try {
			// addrow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void editAction() {
		// TODO Auto-generated method stub
		
	}

	public void removeAction() {
		// TODO Auto-generated method stub
		removerow();
	}
	public void saveAction() {
		// TODO Auto-generated method stub
		
	}
}
