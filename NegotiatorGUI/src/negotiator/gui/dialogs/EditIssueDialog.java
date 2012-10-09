package negotiator.gui.dialogs;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JOptionPane;
import negotiator.gui.tree.NegotiatorTreeTableModel;
import negotiator.gui.tree.TreeFrame;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.EvaluatorInteger;
import negotiator.utility.EvaluatorReal;
import negotiator.utility.UtilitySpace;
import java.awt.CardLayout;

/**
 * 
 * @author Richard Noorlandt
 *
 * This launches a editissue dialog window.
 * Wouter: this is ugly. The EditIssueDialog also handles editing of evaluators.
 * it gets access to the util space via the treeFrame, the parent of this dialog.
 */
public class EditIssueDialog extends NewIssueDialog {

	private static final long serialVersionUID = 5730169200768833303L;
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
	
	/**
	 * Load the appropriate contents into the right panel.
	 * @param issue
	 */
	private void setPanelContents(Issue issue) {
		UtilitySpace utilSpace = treeFrame.getNegotiatorTreeTableModel().getUtilitySpace();
		
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
			List<ValueDiscrete> values = ((IssueDiscrete)issue).getValues();

			String valueString = "";
			String descString="";
			for (ValueDiscrete val: values)
			{
				valueString = valueString + val.getValue() + "\n";
				String desc=((IssueDiscrete)issue).getDesc(val);
				if (desc!=null) descString=descString+desc;
				descString=descString+"\n";
			}
			discreteTextArea.setText(valueString);	
			
			if (utilSpace != null) {
				EvaluatorDiscrete eval = (EvaluatorDiscrete)utilSpace.getEvaluator(issue.getNumber());
				if (eval!=null)
				{
					 // load the eval values
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
			this.nameField.setEnabled(false);
			((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, INTEGER);
			integerMinField.setText("" + ((IssueInteger)issue).getLowerBound());
			integerMaxField.setText("" + ((IssueInteger)issue).getUpperBound());
			integerMinField.setEnabled(false);
			integerMaxField.setEnabled(false);
			if (utilSpace != null) {
				EvaluatorInteger eval = (EvaluatorInteger)utilSpace.getEvaluator(issue.getNumber());

				if (eval != null) {
					integerUtilityLowestValue.setText("" + eval.getUtilLowestValue());
					integerUtilityHighestValue.setText("" + eval.getUtilHeighestValue());
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
					switch (eval.getFuncType()) {
					case LINEAR:
						realLinearField.setText("" + eval.getLinearParam());						
					case CONSTANT:
						realParameterField.setText("" + eval.getConstantParam());
					default:
						break;
					}
					//realOtherField.setText(eval.); Herbert: what's realOtherField?
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
			if (issue == null) return;
			
			boolean valid = true;
			if (issue instanceof IssueInteger) {
				double utilLIV = Double.parseDouble(integerUtilityLowestValue.getText());
				double utilHIV = Double.parseDouble(integerUtilityHighestValue.getText());
				if (utilLIV < 0.0 || utilLIV > 1.0) {
					valid = false;
					JOptionPane.showConfirmDialog(null, "The utility of the lowest value should be \n" +
														"in the range [0, 1]", "Input",
														JOptionPane.PLAIN_MESSAGE);
				} else if (utilHIV < 0.0 || utilHIV > 1.0) {
					valid = false;
					JOptionPane.showConfirmDialog(null, "The utility of the heighest value should be \n" +
														"in the range [0, 1]", "Input",
														JOptionPane.PLAIN_MESSAGE);
				}
			}
			if (valid){
				updateIssue(issue);
				
				//Notify the model that the contents of the treetable have changed
				NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel();
				
				(model.getIssueValuePanel(issue)).displayValues(issue);
				Object[] path = { model.getRoot() };
				if (treeFrame.getTreeTable().getTree().getSelectionPath() != null) {
					path = treeFrame.getTreeTable().getTree().getSelectionPath().getPath();
				}
				model.treeStructureChanged(this, path);
				
				//if (model.getUtilitySpace() == null) {
				//	model.treeStructureChanged(this, treeFrame.getTreeTable().getTree().getSelectionPath().getPath());
				//}
				//else {
				//	treeFrame.reinitTreeTable(((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getDomain(), ((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace());
					//}
				
				this.dispose();
			}
		}			
		else if (e.getSource() == cancelButton) {
			this.dispose();
		}
	}
}