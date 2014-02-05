package negotiator.gui.agentrepository;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;

import negotiator.gui.GenericFileFilter;
import negotiator.repository.AgentRepItem;
import negotiator.repository.Repository;

/**
 * A user interface to the agent repository
 * 
 * @author Wouter Pasman, Mark Hendrikx
 */
public class AgentRepositoryUI {
	private static final String ADD_AN_AGENT = "Add an agent";
	private Repository agentrepository;
	private AbstractTableModel dataModel;
	private final JTable table;

	public AgentRepositoryUI(JTable pTable) {
		this.table = pTable;
		agentrepository = Repository.get_agent_repository();

		initTable();
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
			final String columnnames[] = { "Agent Name", "Description" };

			public int getColumnCount() {
				return columnnames.length;
			}

			public int getRowCount() {
				return agentrepository.getItems().size();
			}

			public Object getValueAt(int row, int col) {
				AgentRepItem agt = (AgentRepItem) agentrepository.getItems()
						.get(row);
				switch (col) {
				case 0:
					String error = "";
					if (agt.getVersion().equals("ERR")
							&& !agt.getName().equals(ADD_AN_AGENT)) {
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

		if (agentrepository.getItems().size() == 0) {
			addTemporaryAgent();
			agentrepository.save();
		}

		table.setModel(dataModel);
		table.setShowVerticalLines(false);
		table.addMouseListener(new MouseAdapter() {

			// if Windows
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseCode(e);
			}

			// if Linux
			public void mousePressed(MouseEvent e) {
				mouseCode(e);
			}

			private void mouseCode(MouseEvent e) {
				int r = table.rowAtPoint(e.getPoint());
				if (r >= 0 && r < table.getRowCount()) {
					table.setRowSelectionInterval(r, r);
				} else {
					table.clearSelection();
				}

				int rowindex = table.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					JPopupMenu popup = createPopupMenu();
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		table.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
					removeAction();
				}
			}
		});
	}

	/**
	 * Add new agent to repository. The agent is expected to be a .class file
	 */
	public void addAction() {

		// Restrict file picker to root and subdirectories.
		// Ok, you can escape if you put in a path as directory. We catch this
		// later on.
		//
		// FileSystemView fsv = new DirectoryRestrictedFileSystemView(new
		// File(root));
		// JFileChooser fc = new JFileChooser(fsv.getHomeDirectory(), fsv);
		// Lifted the restriction. #856
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));

		// Filter such that only directories and .class files are shown.
		FileFilter filter = new GenericFileFilter("class",
				"Java class files (.class)");
		fc.setFileFilter(filter);

		// Open the file picker
		int returnVal = fc.showOpenDialog(null);

		// If file selected
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				addToRepo(new AgentRepItem(fc.getSelectedFile()));
			} catch (ClassNotFoundException e) {
				showLoadError("No class found at " + fc, e);
			} catch (InstantiationException e) {
				// happens when object instantiated is interface or abstract
				showLoadError(
						"Class cannot be instantiated. Reasons may be that there is no constructor without arguments, "
								+ "or the class is abstract or an interface.",
						e);
			} catch (IllegalAccessException e) {
				showLoadError("Missing constructor without arguments", e);
			} catch (NoClassDefFoundError e) {
				showLoadError("Errors in loaded class.", e);
			} catch (ClassCastException e) {
				showLoadError(
						"The loaded class seems to be of the wrong type. ", e);
			} catch (IllegalArgumentException e) {
				showLoadError("The given file can not be used.", e);
			} catch (IOException e) {
				showLoadError("The file can not be read.", e);
			}
		}

	}

	/*
	 * show error while loading agent file. Also show the detail message.
	 */
	private void showLoadError(String text, Throwable e) {
		String message = e.getMessage();
		if (message == null) {
			message = "";
		}

		JOptionPane.showMessageDialog(null, text + "\n" + message,
				"Load error", 0);

	}

	/**
	 * 
	 * @param file
	 *            absolute file path, eg
	 *            /Volumes/documents/NegoWorkspace3/NegotiatorGUI
	 *            /bin/agents/BayesianAgent.class TODO I think this should be
	 *            path to the root directory for the class path, eg
	 *            '/Volumes/documents/NegoWorkspace3/NegotiatorGUI/bin'
	 * @param className
	 *            the class path name, eg "agents.BayesianAgent"
	 */
	private void addToRepo(File file, String className) {
		// Remove "Add agents" if there were no agents first
		int row = table.getSelectedRow();
		if (agentrepository.getItems().get(row).getName().equals(ADD_AN_AGENT)) {
			agentrepository.getItems().remove(row);
		}

		// Load the agent and save it in the XML. -6 strips the '.class'.
		AgentRepItem rep = new AgentRepItem(file.getName().substring(0,
				file.getName().length() - 6), className, "");
		agentrepository.getItems().add(rep);
		agentrepository.save();
		dataModel.fireTableDataChanged();

	}

	/**
	 * Add a Agent reference to the repo.
	 * 
	 * @param agentref
	 */
	private void addToRepo(AgentRepItem agentref) {
		// Remove "Add agents" if there were no agents first
		int row = table.getSelectedRow();
		if (agentrepository.getItems().get(row).getName().equals(ADD_AN_AGENT)) {
			agentrepository.getItems().remove(row);
		}

		agentrepository.getItems().add(agentref);
		agentrepository.save();
		dataModel.fireTableDataChanged();

	}

	public void removeAction() {
		for (int i = 0; i < table.getSelectedRows().length; i++) {
			agentrepository.getItems().remove(table.getSelectedRows()[i]);
		}
		if (dataModel.getRowCount() == 0) {
			addTemporaryAgent();
		}
		dataModel.fireTableDataChanged();
		agentrepository.save();
	}

	private void addTemporaryAgent() {
		if (dataModel.getRowCount() == 0) {
			agentrepository.getItems().add(
					new AgentRepItem(ADD_AN_AGENT, "", ""));
		}
	}
}