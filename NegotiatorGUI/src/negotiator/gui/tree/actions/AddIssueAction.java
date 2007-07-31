package negotiator.gui.tree.actions;

import java.awt.Frame;
import java.awt.event.*;
import javax.swing.*;
import negotiator.gui.dialogs.*;

import jtreetable.JTreeTable;

/**
*
* @author Richard Noorlandt
* 
*/


public class AddIssueAction extends AbstractAction {
	
	//Attributes
	private JTreeTable treeTable;
	private Frame owner;
	
	//Constructors
	public AddIssueAction(Frame dialogOwner, JTreeTable treeTable) {
		super("Add Issue");
		this.treeTable = treeTable;
		this.owner = dialogOwner;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		NewIssueDialog dialog = new NewIssueDialog(owner, true, treeTable);
	}

}
