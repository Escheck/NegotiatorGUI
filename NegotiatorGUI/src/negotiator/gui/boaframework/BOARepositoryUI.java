package negotiator.gui.boaframework;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import negotiator.boaframework.ComponentsEnum;
import negotiator.boaframework.repository.BOAagentRepository;
import negotiator.boaframework.repository.BOArepItem;
import negotiator.gui.NegoGUIApp;

/**
 * A user interface to the agent repository 
 * @author Wouter Pasman, Mark Hendrikx
 */
public class BOARepositoryUI {

	private static final String ADD_A_COMPONENT = "Add a component";
	private BOAagentRepository boaRepository;
	private AbstractTableModel dataModel;
	private final JTable table;
	private ArrayList<BOArepItem> items;
	
	public BOARepositoryUI(JTable pTable) {
		this.table = pTable;
		boaRepository = BOAagentRepository.getInstance();
		items = new ArrayList<BOArepItem>();
		referenceComponents();
		initTable();
	}

	private JPopupMenu createPopupMenu() {
		JPopupMenu popup = new JPopupMenu();

		JMenuItem addComponent = new JMenuItem("Add new component");
		addComponent.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addAction();
			}
		});

		JMenuItem editComponent = new JMenuItem("Edit component");
		editComponent.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				editAction();
			}
		});
		
		JMenuItem removeComponent = new JMenuItem("Remove component");
		removeComponent.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				removeAction();
			}
		});

		popup.add(addComponent);
		popup.add(editComponent);
		popup.add(removeComponent);
		
		return popup;
	}
	
	private void referenceComponents() {
		items.clear();
		for (Entry<String, BOArepItem> entry : boaRepository.getOfferingStrategiesRepItems().entrySet()) {
			items.add(entry.getValue());
		}

		for (Entry<String, BOArepItem> entry : boaRepository.getAcceptanceStrategiesRepItems().entrySet()) {
			items.add(entry.getValue());
		}

		for (Entry<String, BOArepItem> entry : boaRepository.getOpponentModelsRepItems().entrySet()) {
			items.add(entry.getValue());
		}

		for (Entry<String, BOArepItem> entry : boaRepository.getOMStrategiesRepItems().entrySet()) {
			items.add(entry.getValue());
		}

		Collections.sort(items);
		
		if (items.size() == 0) {
			addTemporaryComponent();
		}
	}

	private void initTable() {
		dataModel = new AbstractTableModel() {
			private static final long serialVersionUID = -4985008096999143587L;
			final String columnnames[] = {"Type","Name"};
			
			public int getColumnCount() { 
				return columnnames.length; 
			}
			
			public int getRowCount() { 
				return items.size();
			}
			
			public Object getValueAt(int row, int col) { 
			  	  BOArepItem boaComponent = (BOArepItem) items.get(row);
			  	  switch(col) {
				  	  case 0:
				  		  return boaComponent.getTypeString();
				  	  case 1:
				  		  return boaComponent.getName();
			  	  }
			  	  return col;
			}
			public String getColumnName(int column) {
			  	  return columnnames[column];
			}
		};
	
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
	            if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
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
	
	public void addAction() {
		// shoud return boolean if added an item.
		// if so, sort items and display again
		BOAComponentEditor loader = new BOAComponentEditor(NegoGUIApp.negoGUIView.getFrame(), "Add BOA component");
		BOArepItem item = loader.getResult(null);
		if (item != null) {
			items.add(item);
			Collections.sort(items);
			table.updateUI();
		}
	}
	public void editAction() {
		BOArepItem item = items.get(table.getSelectedRow());
		BOAComponentEditor loader = new BOAComponentEditor(NegoGUIApp.negoGUIView.getFrame(), "Edit BOA component");
		BOArepItem result = loader.getResult(item);
		if (result != null) {
			items.remove(item);
			items.add(result);
			Collections.sort(items);
			table.updateUI();
		}
	}
	
	public void removeAction() {	
		if (table.getSelectedRow() != -1) {
			BOArepItem removed = items.remove(table.getSelectedRow());
			if (dataModel.getRowCount() == 0) {
				addTemporaryComponent();
			}
			dataModel.fireTableDataChanged();
			if (removed.getType() != ComponentsEnum.UNKNOWN) {
				boaRepository.removeComponent(removed);
			}
		}
	}
	
	private void addTemporaryComponent() {
		if (items.size() == 0) {
			items.add(new BOArepItem(ADD_A_COMPONENT, "", ComponentsEnum.UNKNOWN));
		}
	}
}