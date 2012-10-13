package negotiator.gui.agentrepository;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import negotiator.Global;
import negotiator.gui.DirectoryRestrictedFileSystemView;
import negotiator.gui.GenericFileFilter;
import negotiator.repository.AgentRepItem;
import negotiator.repository.Repository;

/**
 * A user interface to the agent repository 
 * @author Wouter Pasman, Mark Hendrikx
 */
public class AgentRepositoryUI
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
		
		JMenuItem addAgent = new JMenuItem("Add new agent");
		addAgent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAction();
             }
         });
		
		JMenuItem removeAgent = new JMenuItem("Remove agent");
		removeAgent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               removeAction();
            }
        });
		
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
			  	  switch(col) {
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
	
	public void addAction() {
		// Get the root of Genius
		String root = getRoot();
		
		// Restrict file picker to root and subdirectories.
		// Ok, you can escape if you put in a path as directory. We catch this later on.
		FileSystemView fsv = new DirectoryRestrictedFileSystemView(new File(root));
		JFileChooser fc = new JFileChooser(fsv.getHomeDirectory(), fsv);
		
		// Filter such that only directories and .class files are shown.
		FileFilter filter = new GenericFileFilter("class", "Java class files (.class)");
		fc.setFileFilter(filter);
		
		// Open the file picker
		int returnVal = fc.showOpenDialog(null);
		
		// If file selected
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            // Catch people who tried to escape our directory
            if (!file.getPath().startsWith(root)) {
            	JOptionPane.showMessageDialog(null, "Only agents in the root or a subdirectory of the root are allowed.", "Agent import error", 0);
            } else {
            	// Get the relative path of the agent class file
	            String relativePath = file.getPath().substring(root.length(), file.getPath().length() - 6);
	            
	            // Convert path to agent path as in XML file
	            relativePath = relativePath.replace(File.separatorChar + "", ".");
	            
	            // Load the agent and save it in the XML
	            AgentRepItem rep = new AgentRepItem(file.getName().substring(0, file.getName().length() - 6), relativePath, "");
	            agentrepository.getItems().add(rep);
	            agentrepository.save();
	            dataModel.fireTableDataChanged();
            }
        }
	}

	private String getRoot() {
		boolean inJar = false;

		try
		{
		  CodeSource cs = Global.class.getProtectionDomain().getCodeSource();
		  inJar = cs.getLocation().toURI().getPath().endsWith(".jar");
		}
		catch (Exception e)
		{
		e.printStackTrace();
		}
		
		String root = null;
		try {
			root = new java.io.File(".").getCanonicalPath() + File.separator;
			if (!inJar) { // it returns the root of the project, but we should be in .bin
				root += "bin" + File.separator;
			} // else if Jar it returns the root of the directory
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return root;
	}

	public void removeAction() {
		int row=table.getSelectedRow();
		agentrepository.getItems().remove(row);
		dataModel.fireTableRowsDeleted(row, row);
		agentrepository.save();
	}
}