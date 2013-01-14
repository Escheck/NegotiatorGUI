package negotiator.gui.boaframework;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.sun.msv.datatype.xsd.Comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import negotiator.boaframework.repository.BOAagentRepository;
import negotiator.boaframework.repository.BOArepItem;

/**
 * A user interface to the agent repository 
 * @author Wouter Pasman, Mark Hendrikx
 */
public class BOARepositoryUI {

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

	private void referenceComponents() {
		
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
	}

	private void initTable() {
		dataModel = new AbstractTableModel() {
			private static final long serialVersionUID = -4985008096999143587L;
			final String columnnames[] = {"Type","Name"};
			
			public int getColumnCount() { 
				return columnnames.length; 
			}
			public int getRowCount() { 
				return boaRepository.getItemsCount();
			}
			
			public Object getValueAt(int row, int col) { 
			  	  BOArepItem boaComponent = (BOArepItem) items.get(row);
			  	  switch(col) {
				  	  case 0:
				  		  return boaComponent.getType();
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
	}
}