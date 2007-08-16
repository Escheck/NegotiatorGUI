package negotiator.gui.tree.actions;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import negotiator.gui.tree.*;
import negotiator.gui.dialogs.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class SaveDomainAction extends AbstractAction {
	
	//Attributes
	private TreeFrame parent;
	private File openedFile;
	private final JFileChooser fileChooser;
	
	//Constructors
	public SaveDomainAction (TreeFrame parent, JFileChooser fileChooser) {
		super("Save Domain");
		this.parent = parent;
		this.fileChooser = fileChooser;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		/*int result = fileChooser.showSaveDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			openedFile = fileChooser.getSelectedFile();
			saveDomain(openedFile);
		}*/
		new SaveDomainDialog(parent, true, parent.getTreeTable(), fileChooser);
	}
	
	private void saveDomain(File file) {
		System.out.println(file);
	}
}