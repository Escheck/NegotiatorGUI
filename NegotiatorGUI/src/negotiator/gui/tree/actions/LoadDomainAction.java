package negotiator.gui.tree.actions;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import negotiator.*;
import negotiator.gui.tree.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class LoadDomainAction extends AbstractAction {

	//Attributes
	private TreeFrame parent;
	private File openedFile;
	private final JFileChooser fileChooser;
	
	//Consturctors
	public LoadDomainAction (TreeFrame parent, JFileChooser fileChooser) {
		super("Open Domain");
		this.parent = parent;
		this.fileChooser = fileChooser;
	}
	

	//Methods
	public void actionPerformed(ActionEvent e) {
		int result = fileChooser.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			openedFile = fileChooser.getSelectedFile();
			loadDomain(openedFile);
		}
	}
	

	private void loadDomain(File file) {
		Domain domain = new Domain(file);
		parent.clearTreeTable(domain);
	}
}
