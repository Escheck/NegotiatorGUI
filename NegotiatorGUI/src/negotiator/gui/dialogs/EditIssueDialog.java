package negotiator.gui.dialogs;

import java.awt.Frame;

import javax.swing.JOptionPane;

import negotiator.gui.dialogs.NewObjectiveDialog.InvalidInputException;
import negotiator.issue.*;
import jtreetable.*;

/**
 * 
 * @author Richard Noorlandt
 *
 */

public class EditIssueDialog extends NewIssueDialog {
	
	//Attributes
	private Issue issue;
	
	//Constructors
	public EditIssueDialog(JTreeTable treeTable, Issue issue) {
		this(null, false, treeTable, issue);
	}
		
	public EditIssueDialog(Frame owner, boolean modal, JTreeTable treeTable, Issue issue) {
		this(owner, modal, "Edit Issue", treeTable, issue);
		this.issue = issue;
	}
	
	public EditIssueDialog(Frame owner, boolean modal, String name, JTreeTable treeTable, Issue issue) {
		super(owner, modal, name, treeTable);
		this.issue = issue;
		setTypePanel(issue);
	}
	
	//Methods
	private void setTypePanel(Issue issue) {
		if (issue instanceof IssueDiscrete) {
			
		}
		else if (issue instanceof IssueInteger) {
			
		}
		else if (issue instanceof IssueReal) {
			
		}
	}
	
	protected Issue constructIssue() {
		String name;
		int number;
		String description;
		
		try {
			name = getObjectiveName();
			number = getObjectiveNumber();
			description = getObjectiveDescription();
		}
		catch (InvalidInputException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			return null;
		}
		
		//LET OP!!!!!!!!!!! HIER KOMT COPY FEEST!
		/*
		String selectedType = (String)issueType.getSelectedItem();
		Issue issue;
		
		if (selectedType == DISCRETE) {
			String[] values;
			try {
				values = getDiscreteValues(); 
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			issue = new IssueDiscrete(name, number, values);
		}
		else if (selectedType == INTEGER) {
			int min;
			int max;
			try {
				min = getIntegerMin();
				max = getIntegerMax();
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			issue = new IssueInteger(name, number, min, max);
		}
		else if (selectedType == REAL) {
			double min;
			double max;
			try {
				min = getRealMin();
				max = getRealMax();
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			issue = new IssueReal(name, number, min, max);
		}
		else {
			JOptionPane.showMessageDialog(this, "Please select an issue type!");
			return null;
		}
		*/
		//En copy feest is vies ;p
		
		return issue;
	}
	
	/*
	 * protected Issue constructIssue() {
		String name;
		int number;
		String description;
		Objective selected; //The Objective that is seleced in the tree, which will be the new Issue's parent.
		try {
			name = getObjectiveName();
			number = getObjectiveNumber();
			description = getObjectiveDescription();
		}
		catch (InvalidInputException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			return null;
		}
		try {
			selected = (Objective) treeTable.getTree().getLastSelectedPathComponent();
			if (selected == null) {
				JOptionPane.showMessageDialog(this, "There is no valid parent selected for this objective.");
				return null;
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "There is no valid parent selected for this objective.");
			return null;
		}
		
		String selectedType = (String)issueType.getSelectedItem();
		Issue issue;
		
		if (selectedType == DISCRETE) {
			String[] values;
			try {
				values = getDiscreteValues(); 
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			issue = new IssueDiscrete(name, number, values);
		}
		else if (selectedType == INTEGER) {
			int min;
			int max;
			try {
				min = getIntegerMin();
				max = getIntegerMax();
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			issue = new IssueInteger(name, number, min, max);
		}
		else if (selectedType == REAL) {
			double min;
			double max;
			try {
				min = getRealMin();
				max = getRealMax();
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			issue = new IssueReal(name, number, min, max);
		}
		else {
			JOptionPane.showMessageDialog(this, "Please select an issue type!");
			return null;
		}
		
		issue.setDescription(description);
		selected.addChild(issue);
		return issue;	
	}
	 */
}
