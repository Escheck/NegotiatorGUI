package negotiator.gui.tree;

import javax.swing.*;
import javax.swing.table.*;
import jtreetable.*;
import negotiator.*;
import negotiator.utility.*;
import negotiator.gui.tree.actions.*;

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
	public TreePanel(Domain domain, UtilitySpace utilitySpace) {
		this(new NegotiatorTreeTableModel(domain, utilitySpace));
	}
	
	public TreePanel(NegotiatorTreeTableModel treeModel) {
		super();
		
		model = treeModel;
		
		//Create Actions
		AddAction addAct = new AddAction();
		AddObjectiveAction addObjectiveAct = new AddObjectiveAction();
		AddIssueAction addIssueAct = new AddIssueAction();
		DeleteAction delAct = new DeleteAction();
		EditAction editAct = new EditAction();
		
		treeTable = new JTreeTable(model);
		//TODO THIS IS FOR TESTING
		//WeightSlider slider = treeModel.getWeightSlider();
		WeightSliderCellEditor cellEditor = new WeightSliderCellEditor(model);
		treeTable.setDefaultRenderer(WeightSlider.class, cellEditor);//new WeightSliderCellRenderer(model));//slider);
		treeTable.setDefaultEditor(WeightSlider.class, cellEditor);//new WeightSliderCellEditor(model));
		treeTable.getColumnModel().getColumn(2).setPreferredWidth(new WeightSlider().getPreferredSize().width);//(new WeightSlider().getSize().width);
		treeTable.setRowHeight(new WeightSlider().getPreferredSize().height);
		//treeTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(slider));
		//TODO END OF TESTING
		JScrollPane treePane = new JScrollPane(treeTable);
		this.add(treePane);
		
		//Initialise the Panel with buttons.
		JPanel controls = new JPanel();
		controls.setBorder(BorderFactory.createTitledBorder("Edit nodes"));
		controls.add(new JButton(addAct));
		controls.add(new JButton(addObjectiveAct));
		controls.add(new JButton(addIssueAct));
		controls.add(new JButton(delAct));
		controls.add(new JButton(editAct));
		this.add(controls);
	}
	
	//Methods
	private void initPopupMenu() {
		
	}
	
	public JTreeTable getTreeTable() {
		return treeTable;
	}
	
	public NegotiatorTreeTableModel getNegotiatorTreeTableModel() {
		return model;
	}

}
