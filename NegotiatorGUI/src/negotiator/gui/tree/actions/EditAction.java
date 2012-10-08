package negotiator.gui.tree.actions;

import java.awt.event.*;
import javax.swing.*;
import negotiator.gui.dialogs.*;
import negotiator.gui.tree.*;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class EditAction extends AbstractAction {
	
	private static final long serialVersionUID = -4491726706262184805L;
	//Attributes
	TreeFrame treeFrame;
	//JTreeTable treeTable;
	
	//Constructors
	//public EditAction(JTreeTable treeTable) {
	//	super("Edit");
	//	this.treeTable = treeTable;
	//}
	public EditAction(TreeFrame treeFrame) {
		super("Edit");
		this.treeFrame = treeFrame;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		try {
			Objective obj = (Objective) treeFrame.getTreeTable().getTree().getLastSelectedPathComponent();
			if (obj instanceof Issue) {
				EditIssueDialog dialog = new EditIssueDialog(treeFrame, (Issue)obj);
			} else if (obj instanceof Objective) {
				EditObjectiveDialog dialog = new EditObjectiveDialog(treeFrame, obj);
			}
		}
		catch (Exception except) {
			except.printStackTrace();
		}
	}

}
