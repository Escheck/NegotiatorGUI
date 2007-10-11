package negotiator.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.*;

import negotiator.gui.dialogs.*;
import negotiator.gui.dialogs.NewObjectiveDialog.InvalidInputException;
import negotiator.gui.tree.*;
import negotiator.issue.*;
import negotiator.utility.*;
import jtreetable.*;

/**
 * 
 * @author Richard Noorlandt
 *
 * This launches a editissue dialog window.
 */

public class EditIssueDialog extends NewIssueDialog {
	
	//Attributes
	private Issue issue;
	
	//Constructors
	public EditIssueDialog(TreeFrame owner, Issue issue) {
		this(owner, false, issue);
	}
		
	public EditIssueDialog(TreeFrame owner, boolean modal, Issue issue) {
		this(owner, modal, "Edit Issue", issue);
		this.issue = issue;
	}
	
	public EditIssueDialog(TreeFrame owner, boolean modal, String name, Issue issue) {
		super(owner, modal, name);
		this.issue = issue;
		setPanelContents(issue);
	}
	
	//Methods
	private void setPanelContents(Issue issue) {
		UtilitySpace utilSpace = ((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace();
		
		nameField.setText(issue.getName());
		numberField.setText("" + issue.getNumber());
		
		/*
		if (utilSpace == null || (utilSpace.getEvaluator(issue.getNumber()) == null))
			weightCheck.setSelected(false);
		else
			weightCheck.setSelected(true);
		*/
		
		if (issue instanceof IssueDiscrete) {
			this.issueType.setSelectedItem(DISCRETE);
			this.issueType.setEnabled(false);
			((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, DISCRETE);
			ArrayList<ValueDiscrete> values = ((IssueDiscrete)issue).getValues();

			String valueString = "";
			for (ValueDiscrete val: values) valueString = valueString + val.getValue() + "\n";
			discreteTextArea.setText(valueString);

			if (utilSpace != null) {
				EvaluatorDiscrete eval = (EvaluatorDiscrete)utilSpace.getEvaluator(issue.getNumber());
				if (eval!=null)
				{
					valueString = "";
					for (ValueDiscrete val: values) 
					{
						Integer util=eval.getValue(val); // get the utility for this value
						//System.out.println("util="+util);
						if (util!=null) valueString=valueString+util;
						valueString=valueString+"\n";
						
					}
					discreteTextEvaluationArea.setText(valueString);
			
				}
			}
		}
		else if (issue instanceof IssueInteger) {
			this.issueType.setSelectedItem(INTEGER);
			this.issueType.setEnabled(false);
			((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, INTEGER);
			integerMinField.setText("" + ((IssueInteger)issue).getLowerBound());
			integerMaxField.setText("" + ((IssueInteger)issue).getUpperBound());
			if (utilSpace != null) {
				EvaluatorInteger eval = (EvaluatorInteger)utilSpace.getEvaluator(issue.getNumber());
				if (eval != null) {
					if(eval.getFuncType() == EVALFUNCTYPE.LINEAR){
						integerLinearField.setText("" + eval.getLinearParam());
					}else if(eval.getFuncType() == EVALFUNCTYPE.CONSTANT){
						integerParameterField.setText("" + eval.getConstantParam());
					}
				}
			}
		}
		else if (issue instanceof IssueReal) {
			this.issueType.setSelectedItem(REAL);
			this.issueType.setEnabled(false);
			((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, REAL);
			realMinField.setText("" + ((IssueReal)issue).getLowerBound());
			realMaxField.setText("" + ((IssueReal)issue).getUpperBound());
			if (utilSpace != null) {
				EvaluatorReal eval = (EvaluatorReal)utilSpace.getEvaluator(issue.getNumber());
				if (eval != null) {
					//realOtherField.setText(eval.); Herbert: what's realOtherField?
					if(eval.getFuncType() == EVALFUNCTYPE.LINEAR){
						realLinearField.setText("" + eval.getLinearParam());
					}else if(eval.getFuncType() == EVALFUNCTYPE.CONSTANT){
						realParameterField.setText("" + eval.getConstantParam());
					}
				}
			}
		}
	}
	
	/**
	 * Overrides getObjectiveNumber from NewObjectiveDialog
	 */
	protected int getObjectiveNumber() throws InvalidInputException {
		try {
			return Integer.parseInt(numberField.getText());
		}
		catch (Exception e) {
			throw new InvalidInputException("Error reading objective number from (hidden) field.");
		}
	}
	
	/**
	 * Overrides actionPerformed from NewIssueDialog.
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == okButton) {
			if (issue == null) 
				return;
			else {
				updateIssue(issue);
				
				//Notify the model that the contents of the treetable have changed
				NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel();
				
				(model.getIssueValuePanel(issue)).displayValues(issue);
				model.treeStructureChanged(this, treeFrame.getTreeTable().getTree().getSelectionPath().getPath());
				
				//if (model.getUtilitySpace() == null) {
				//	model.treeStructureChanged(this, treeFrame.getTreeTable().getTree().getSelectionPath().getPath());
				//}
				//else {
				//	treeFrame.reinitTreeTable(((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getDomain(), ((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace());
				//}
			}
			
			this.dispose();
		}			
		else if (e.getSource() == cancelButton) {
			this.dispose();
		}
	}
}