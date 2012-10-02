package negotiator.gui.tree.actions;

import java.awt.event.*;
import javax.swing.*;
import jtreetable.*;
import negotiator.gui.dialogs.*;
import negotiator.gui.tree.*;

/**
*
* @author Richard Noorlandt
* 
*/


public class AddObjectiveAction extends AbstractAction {

	private static final long serialVersionUID = -7860001446232441466L;
	//Attributes
	JTreeTable treeTable;
	TreeFrame owner;
	
	//Constructors
	public AddObjectiveAction(TreeFrame dialogOwner, JTreeTable treeTable) {
		super("Add Objective");
		this.treeTable = treeTable;
		this.owner = dialogOwner;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		NewObjectiveDialog dialog = new NewObjectiveDialog(owner, true);
	}

}
