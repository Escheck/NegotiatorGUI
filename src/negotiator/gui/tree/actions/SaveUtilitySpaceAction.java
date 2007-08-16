package negotiator.gui.tree.actions;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class SaveUtilitySpaceAction extends AbstractAction {
	
	//Attributes
	private JFrame parent;
	private File openedFile;
	private final JFileChooser fileChooser;
	
	//Constructors
	public SaveUtilitySpaceAction (JFrame parent, JFileChooser fileChooser) {
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
		System.out.println(file);
	}
}