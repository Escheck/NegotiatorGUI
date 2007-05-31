package negotiator.gui.tree;

import javax.swing.*;
import javax.swing.table.*;
import jtreetable.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class TreePanel extends JPanel {
	
	//Attributes
	JTreeTable treeTable;
	NegotiatorTreeTableModel model;
	
	
	//Constructors
	
	public TreePanel(NegotiatorTreeTableModel treeModel) {
		super();
		
		model = treeModel;
		
		treeTable = new JTreeTable(model);
		//TODO THIS IS FOR TESTING
		WeightSlider slider = treeModel.getWeightSlider();
		treeTable.setDefaultRenderer(WeightSlider.class, slider);
		treeTable.setDefaultEditor(WeightSlider.class, new WeightSliderCellEditor(model));
		//treeTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(slider));
		//TODO END OF TESTING
		JScrollPane treePane = new JScrollPane(treeTable);
		this.add(treePane);
		
		//Initialise the Panel with buttons.
		JPanel controls = new JPanel();
		controls.add(new JButton("Add"));
		controls.add(new JButton("Delete"));
		controls.add(new JButton("Move"));
		
		controls.add(new WeightSlider());
		this.add(controls);
	}
	
	//Methods
	
	public JTreeTable getTreeTable() {
		return treeTable;
	}

}
