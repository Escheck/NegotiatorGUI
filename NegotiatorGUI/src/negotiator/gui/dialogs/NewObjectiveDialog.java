package negotiator.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class NewObjectiveDialog extends JDialog {

	//Attributes	
	JButton okButton;
	JButton cancelButton;
	
	JTextField nameField;
	JTextField numberField; //TODO: make this non editable
	//WeightSlider?
	JTextArea descriptionArea;
	
	Objective parent;
	
	//Constructors
	
	public NewObjectiveDialog(Objective parent) {
		this(null, false, parent);
	}
		
	public NewObjectiveDialog(Frame owner, boolean modal, Objective parent) {
		super(owner, "Create new Objective", modal);
		
		//If parent == null, we are dealing with a root node.
		this.parent = parent;
		
		//Initialize the buttons
		okButton = new JButton("Ok");
		cancelButton = new JButton("Cancel");
		
		//Initialize the fields
		nameField = new JTextField();
		numberField = new JTextField();
		descriptionArea = new JTextArea();
		
		this.setLayout(new FlowLayout());
		
		this.add(nameField);
		this.add(numberField);
		this.add(descriptionArea);
		
		this.add(okButton);
		this.add(cancelButton);
		
		this.pack();
		this.setVisible(true);
	}
	
	//Methods
	public void commitNewObjective() {
		Objective newObjective = new Objective(parent);
	}
	
	
}
