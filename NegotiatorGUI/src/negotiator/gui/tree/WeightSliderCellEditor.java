package negotiator.gui.tree;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class WeightSliderCellEditor extends AbstractCellEditor implements TableCellEditor {
	
	//Attributes
	NegotiatorTreeTableModel tableModel;
	
	//Constructors
	
	public WeightSliderCellEditor(NegotiatorTreeTableModel model) {
		super();
		tableModel = model;
	}
	
	//Methods
	public Object getCellEditorValue() {
		//TODO TEST CODE!!
		return new Integer(7);
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		//TODO THIS IS TEST CODE!!!
		return new WeightSlider();
	}
}
