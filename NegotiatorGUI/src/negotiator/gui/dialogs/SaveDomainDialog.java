package negotiator.gui.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import jtreetable.JTreeTable;

/**
 * 
 * @author Richard Noorlandt
 *
 */

public class SaveDomainDialog extends JDialog implements ActionListener {
	
	//Attributes
	
	//Constructors
	public SaveDomainDialog(Frame owner, boolean modal, JTreeTable treeTable, String name) {
		super(owner, name, modal);
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		
	}
}
