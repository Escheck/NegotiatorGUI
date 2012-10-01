package negotiator.gui.tree;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.*;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public abstract class IssueValuePanel extends JPanel {
	
	private static final long serialVersionUID = 7098132974856692090L;

	//Attributes
	static final Color BACKGROUND = Color.white;
	
	//Constructors
	public IssueValuePanel(NegotiatorTreeTableModel model, Objective objective) {
		super();
		this.setBackground(BACKGROUND);
		this.setLayout(new FlowLayout());
	}
	
	//Methods
	/*
	 * No specific methods need to be implemented. The subclasses should implement a panel that
	 * visualizes the possible values of an issue in an appropriate way.
	 */
	
	/**
	 * Draws the values of this Issue or Objective
	 * @param The Objective or Issue to display values of.
	 */
	 public abstract void displayValues(Objective node);
	 
}
