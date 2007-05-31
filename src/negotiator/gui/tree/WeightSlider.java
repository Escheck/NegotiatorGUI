package negotiator.gui.tree;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class WeightSlider extends JPanel implements TableCellRenderer, TableCellEditor {
	
	//Attributes
	static final int MIN_VALUE = 0;
	static final int MAX_VALUE = 1000;
	
	Objective objective;
	JCheckBox lock;
	JSlider slider;
	JFormattedTextField valueField;
	
	
	
	//Constructors
	public WeightSlider() {
		//TODO This is a stub for testing. Weightsliders should know their objectives / issues.
		this(null);
	}
	
	public WeightSlider(Objective obj) {
		objective = obj;
		
		this.setLayout(new FlowLayout());
		
		lock = new JCheckBox();
		this.add(lock);
		
		slider = new JSlider(MIN_VALUE, MAX_VALUE);
		this.add(slider);
		
		valueField = new JFormattedTextField();
		this.add(valueField);
	}
	
	
	//Methods
	
	//@TODO for testing purpose. Prolly' don't work as supposed to
	//TEST
	public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return this;
	}
	
	public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected, int row, int column) {
		return this;
	}
	
	//TODO END TESTING
}
