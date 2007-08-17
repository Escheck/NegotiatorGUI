package negotiator.gui.dialogs;

import java.awt.*;
import negotiator.issue.*;

import jtreetable.JTreeTable;

/**
 * 
 * @author Richard Noorlandt
 *
 */

public class EditObjectiveDialog extends NewObjectiveDialog {
	
	//Attributes
	Objective objective;
	
	//Constructor
	public EditObjectiveDialog(JTreeTable treeTable, Objective objective) {
		this(null, false, treeTable, objective);
	}
		
	public EditObjectiveDialog(Frame owner, boolean modal, JTreeTable treeTable, Objective objective) {
		this(owner, modal, treeTable, "Edit Objective", objective);
	}
	
	public EditObjectiveDialog(Frame owner, boolean modal, JTreeTable treeTable, String name, Objective objective) {
		super(owner, modal, treeTable, name);
		this.objective = objective;
		setFieldValues();
	}
	
	//Methods
	private void setFieldValues() {
		nameField.setText(objective.getName());
		numberField.setText("" + objective.getNumber());
//		descriptionArea.setText(objective.getDescription());
	}
	
	//overrides from newobjectdialog
	protected Objective constructObjective() {
		String name="";
		int number=0;
		String description="";
		try {
			name = getObjectiveName();
			number = (getObjectiveNumber());
			description = (getObjectiveDescription());
		}
		catch (InvalidInputException e) {
			
		}
		objective.setName(name);
		objective.setNumber(number);
		objective.setDescription(description);
		return objective;
	}
}
