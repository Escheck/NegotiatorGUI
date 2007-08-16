package negotiator.gui.tree.actions;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;

import negotiator.gui.tree.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class SaveUtilitySpaceAction extends AbstractAction {
	
	//Attributes
	private TreeFrame parent;
	private File openedFile;
	private final JFileChooser fileChooser;
	
	//Constructors
	public SaveUtilitySpaceAction (TreeFrame parent, JFileChooser fileChooser) {
		super("Save UtilitySpace");
		this.parent = parent;
		this.fileChooser = fileChooser;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		int result = fileChooser.showSaveDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			openedFile = fileChooser.getSelectedFile();
			saveUtilitySpace(openedFile);
		}
	}
	
	private void saveUtilitySpace(File file) {
		parent.getNegotiatorTreeTableModel().getUtilitySpace().toXML().saveToFile(file.getPath());
	}
}