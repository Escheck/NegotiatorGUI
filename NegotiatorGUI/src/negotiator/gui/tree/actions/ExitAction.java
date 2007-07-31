package negotiator.gui.tree.actions;

import javax.swing.*;
import java.awt.event.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class ExitAction extends AbstractAction {
	
	//Attributes
	private JFrame frame;
	
	//Constructors
	public ExitAction(JFrame frame) {
		super("Exit");
		this.frame = frame;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		frame.dispose();
	}
}
