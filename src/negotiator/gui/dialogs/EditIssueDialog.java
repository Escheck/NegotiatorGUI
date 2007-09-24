package negotiator.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.*;

import negotiator.gui.dialogs.*;
import negotiator.gui.tree.*;
import negotiator.issue.*;
import negotiator.utility.*;
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
		setPanelContents(issue);
	}
	
	//Methods
	private void setPanelContents(Issue issue) {
		UtilitySpace utilSpace = ((NegotiatorTreeTableModel)treeTable.getTree().getModel()).getUtilitySpace();
		
		nameField.setText(issue.getName());
		numberField.setText("" + issue.getNumber());
		
		if (utilSpace != null || (utilSpace.getEvaluator(issue.getNumber()) != null))
			weightCheck.setSelected(false);
		else
			weightCheck.setSelected(true);
		
		if (issue instanceof IssueDiscrete) {
			this.issueType.setSelectedItem(DISCRETE);
			this.issueType.setEnabled(false);
			((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, DISCRETE);
			Enumeration<ValueDiscrete> values = ((IssueDiscrete)issue).getValues();
			String valueString = "";
			ValueDiscrete val;
			
			while (values.hasMoreElements()) {
				val = values.nextElement();
				valueString = valueString + val.getValue() + "\n";
			}
			discreteTextArea.setText(valueString);
			
			if (utilSpace != null) {
				EvaluatorDiscrete eval = (EvaluatorDiscrete)utilSpace.getEvaluator(issue.getNumber());
				//Let's reuse some variables
				values = ((IssueDiscrete)issue).getValues();
				valueString = "";
				while (values.hasMoreElements() && values != null) {
					val = values.nextElement();
					try{
					valueString = valueString + eval.getEvaluation(val) + "\n";
					}catch(Exception e){
						//do nothing, an exception is thrown whenever there  isn't an evaluator yet.
					}
				}
				discreteTextEvaluationArea.setText(valueString);
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
					//integerOtherField.setText(eval.); Herbert: what's integerOtherField?
					integerLinearField.setText("" + eval.getLinearParam());
					integerParameterField.setText("" + eval.getConstantParam());
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
					realLinearField.setText("" + eval.getLinearParam());
					realParameterField.setText("" + eval.getConstantParam());
				}
			}
		}
	}
	
	/**
	 * Overrides actionPerformed from NewIssueDialog.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			if (issue == null) 
				return;
			updateIssue(issue);
			//Notify the model that the contents of the treetable have changed.
			NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeTable.getTree().getModel();
			model.treeStructureChanged(this, treeTable.getTree().getSelectionPath().getPath());
			(model.getIssueValuePanel(issue)).displayValues(issue);
			this.dispose();
		}			
		else if (e.getSource() == cancelButton) {
			this.dispose();
		}
	}
}