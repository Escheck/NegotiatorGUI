package negotiator.gui.tree.actions;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import jtreetable.*;
import negotiator.issue.*;
import negotiator.gui.dialogs.*;

/**
*
* @author Richard Noorlandt
* 
*/


public class AddObjectiveAction extends AbstractAction {
	
	//Attributes
	JTreeTable treeTable;
	Frame owner;
	
	//Constructors
	public AddObjectiveAction(Frame dialogOwner, JTreeTable treeTable) {
		super("Add Objective");
		this.treeTable = treeTable;
		this.owner = dialogOwner;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		NewObjectiveDialog dialog = new NewObjectiveDialog(owner, true, treeTable);
	}

}
