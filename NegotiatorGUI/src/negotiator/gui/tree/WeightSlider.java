package negotiator.gui.tree;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import negotiator.issue.*;
import negotiator.utility.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class WeightSlider extends JPanel implements ChangeListener, ItemListener {
	
	//Attributes
	static final int MIN_VALUE = 0;
	static final int MAX_VALUE = 1000;
	
	static final Color BACKGROUND = Color.white;
	
	//private Objective objective;
	//private Evaluator evaluator;
	private NegotiatorTreeTableModel tableModel;
	private JCheckBox lock;
	private JSlider slider;
	private JFormattedTextField valueField;
	private boolean locked = false;
	
	private double weight = 0; // < Might change, should probably be done from within objective, model or evaluator. Ony the question is: which?
	
	
	
	//Constructors
	
	//public WeightSlider(Objective obj) {
	public WeightSlider(NegotiatorTreeTableModel model) {
		//objective = obj;
		//evaluator = eval;
		tableModel = model;
		
		this.setBackground(BACKGROUND);
		this.setLayout(new FlowLayout());
		
		slider = new JSlider(MIN_VALUE, MAX_VALUE);
		slider.setBackground(BACKGROUND);
		slider.setToolTipText("Drag to change the weight");
		slider.addChangeListener(this);
		this.add(slider);
		
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(3);
		valueField = new JFormattedTextField(format);
		valueField.setColumns(4);
		valueField.setToolTipText("Fill in a weight between 0 and 1");
		this.add(valueField);
		
		lock = new JCheckBox();
		lock.setBackground(BACKGROUND);
		lock.setToolTipText("Lock weight");
		lock.addItemListener(this);
		this.add(lock);
		
		updatePreferredSize();
	}
	
	
	//Methods
	/**
	 * Converts an int between MIN_VALUE and MAX_VALUE to a double between 0 and 1. This method is
	 * necessary because JSlider only works on int, and weights are doubles between 0 and 1.
	 * @param value the value to be converted.
	 * @return the value converted to a double.
	 */
	private double convertToDouble(int value) {
		if (value < MIN_VALUE)
			return 0;
		if (value > MAX_VALUE)
			return 1;
		
		return (double)value / (double)(MAX_VALUE - MIN_VALUE);
	}
	
	/**
	 * Converts a double between 0 and 1 to an int between MIN_VALUE and MAX_VALUE. This method is
	 * necessary because JSlider only works on int, and weights are in doubles between 0 and 1.
	 * @param value the value to e converted.
	 * @return the value converted to an int between MIN_VALUE and MAX_VALUE.
	 */
	private int convertToInt(double value) {
		if (value < 0)
			return MIN_VALUE;
		if (value > 1)
			return MAX_VALUE;
		
		return (int)(value*((double)MAX_VALUE - (double)MIN_VALUE)) + MIN_VALUE;
	}
	
	/**
	 * 
	 * @return the weight.
	 */
	public double getWeight() {
		return weight;
	}
	
	/**
	 * 
	 * @param newWeight the new weight.
	 */
	public void setWeight(double newWeight) {
		weight = newWeight;
	}
	
	public void stateChanged(ChangeEvent e) {
		//TODO Remove these silly debug thingies
		if (e.getSource() != slider)
			return;
		//System.out.println("\nJust slide with me!");
		double newWeight = convertToDouble(slider.getValue());
		valueField.setValue(newWeight);
		setWeight(newWeight);
		System.out.println("\n" + getWeight());
	}
	
	/**
	 * Implementation of ItemListener, which is registered on the checkbox. If the checkbox state changes,
	 * the slider and textfield will be locked or unlocked. An unchecked checkbox means that the weight
	 * can be changed.
	 * @param e as defined by the ItemListener interface.
	 */
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			locked = true;
			slider.setEnabled(false);
			valueField.setEnabled(false);
		}
		//Otherwise, it is deselected
		else {
			locked = false;
			slider.setEnabled(true);
			valueField.setEnabled(true);
		}
	}
	
	/**
	 * Calculates and sets this objects preferred size, based on its subcomponents.
	 *
	 */
	protected void updatePreferredSize() {
		int prefHeight = lock.getPreferredSize().height;
		if (slider.getPreferredSize().height > prefHeight)
			prefHeight = slider.getPreferredSize().height;
		if (valueField.getPreferredSize().height > prefHeight)
			prefHeight = valueField.getPreferredSize().height;
		
		int prefWidth = lock.getPreferredSize().width +  slider.getPreferredSize().width + valueField.getPreferredSize().width;
		
		this.setPreferredSize(new Dimension(prefWidth, prefHeight));
	}
	
	//@TODO for testing purpose. Prolly' don't work as supposed to
	//TEST
	
	//public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	//	return this;
	//}
	
	/*
	public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected, int row, int column) {
		return this;
	}*/
	
	//TODO END TESTING
}
