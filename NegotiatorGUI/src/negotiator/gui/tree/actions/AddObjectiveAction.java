package negotiator.gui.tree.actions;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import negotiator.issue.*;
import negotiator.gui.dialogs.*;

/**
*
* @author Richard Noorlandt
* 
*/


public class AddObjectiveAction extends AbstractAction {
	
	//Attributes
	Objective parent;
	
	//Constructors
	public AddObjectiveAction(Frame dialogOwner) {
		super("Add Objective");
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		NewObjectiveDialog dialog = new NewObjectiveDialog();
	}

}
