package negotiator.gui.tree.actions;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import negotiator.*;
import negotiator.gui.tree.*;
import negotiator.utility.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class LoadUtilitySpaceAction extends AbstractAction {

	//Attributes
	private TreeFrame parent;
	private File openedFile;
	private final JFileChooser fileChooser;
	
	//Consturctors
	public LoadUtilitySpaceAction (TreeFrame parent, JFileChooser fileChooser) {
		super("Open Utility Space");
		this.parent = parent;
		this.fileChooser = fileChooser;
		
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		int result = fileChooser.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			openedFile = fileChooser.getSelectedFile();
			loadUtilitySpace(openedFile);
		}
	}
	
	private void loadUtilitySpace(File file) {
		Domain domain = parent.getNegotiatorTreeTableModel().getDomain();
		parent.clearTreeTable(domain, new UtilitySpace(domain, file.getPath()));
	}
}