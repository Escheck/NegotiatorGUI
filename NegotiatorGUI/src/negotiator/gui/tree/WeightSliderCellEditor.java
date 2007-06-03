package negotiator.gui.tree;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class WeightSliderCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
	
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
		if (value == null)
			System.out.println("value == null in getTableCellEditorComponent");
		else {
			System.out.println("value != null in getTableCellEditorComponent");
			if (value instanceof negotiator.issue.Objective)
				System.out.println("And it is an Objective!");
			else
				System.out.println("It's no Objective :-(");
		}
		//return tableModel.getWeightSlider(value);
		//THIS BELOW WORKS AS EXPECTED
		return (WeightSlider)value;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		//TODO Implement this. This is a stub
		if (value == null)
			System.out.println("value == null in getTableCellRendererComponent");
		else {
			System.out.println("value != null in getTableCellRendererComponent");
			if (value instanceof negotiator.issue.Objective)
				System.out.println("And it is an Objective!");
			else
				System.out.println("It's no Objective :-(");
		}
		System.out.println("It's a: " + value.toString());
		//Ok, dus value komt nu uit colom 2 van het model. Let op, het argument column is dus de kolum uit de representatie! Niet altijd 2 dus. 
		
		System.out.println("col: " + column);
			
		//return tableModel.getWeightSlider(value);
		//THIS BELOW WORKS AS EXPECTED
		return (WeightSlider)value;
	}
}
