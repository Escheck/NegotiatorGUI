package negotiator.gui.tree.actions;

import java.awt.event.*;
import javax.swing.*;
import negotiator.gui.dialogs.*;
import jtreetable.JTreeTable;
import negotiator.gui.tree.*;

/**
*
* @author Richard Noorlandt
* 
*/


public class AddIssueAction extends AbstractAction {
	private static final long serialVersionUID = -3180369207410940284L;
	//Attributes
	private TreeFrame owner;
	
	//Constructors
	public AddIssueAction(TreeFrame dialogOwner, JTreeTable treeTable) {
		super("Add Issue");
		this.owner = dialogOwner;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		NewIssueDialog dialog = new NewIssueDialog(owner, true);
	}

}
