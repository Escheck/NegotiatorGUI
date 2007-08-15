package negotiator.gui.tree.actions;

import javax.swing.*;
import java.awt.event.*;

import negotiator.Domain;
import negotiator.gui.tree.*;
import negotiator.utility.UtilitySpace;

/**
*
* @author Richard Noorlandt
* 
*/

public class NewDomainAction extends AbstractAction {

	//Attributes
	private TreeFrame parent;
	
	//Constructors
	public NewDomainAction(TreeFrame parent) {
		super("New Domain");
		this.parent = parent;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		
	}
}
