package negotiator.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jtreetable.JTreeTable;

import java.util.Enumeration;

import negotiator.gui.tree.*;
import negotiator.gui.dialogs.NewObjectiveDialog.InvalidInputException;
import negotiator.gui.tree.NegotiatorTreeTableModel;
import negotiator.issue.*;
import negotiator.utility.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class NewIssueDialog extends NewObjectiveDialog implements ItemListener {
	//Variables
	protected static final String DISCRETE = "Discrete";
	protected static final String INTEGER = "Integer";
	protected static final String REAL = "Real";
	
	protected JComboBox issueType;
	protected String[] issueTypes;// = {DISCRETE, INTEGER, REAL}; <- for some weird reason this doesn't work
	protected JPanel issuePropertyCards;
	protected JPanel issuePropertyPanel;
	protected JPanel discretePanel;
	protected JPanel integerPanel;
	protected JPanel realPanel;
	
	protected JTextArea discreteTextArea;
	protected JTextArea discreteTextEvaluationArea;
	
	protected JTextField integerMinField;
	protected JTextField integerOtherField;
	protected JTextField integerLinearField;
	protected JTextField integerParameterField;
	protected JTextField integerMaxField;
	
	protected JTextField realMinField;
	protected JTextField realOtherField;
	protected JTextField realLinearField;
	protected JTextField realParameterField;
	protected JTextField realMaxField;
	
	//Constructors
	//public NewIssueDialog(JTreeTable treeTable) {
	//	this(null, false, treeTable);
	//}
		
	public NewIssueDialog(TreeFrame owner, boolean modal, JTreeTable treeTable) {
		this(owner, modal, "Create new Issue", treeTable);
	}
	
	public NewIssueDialog(TreeFrame owner, boolean modal, String name, JTreeTable treeTable) {
		super(owner, modal, name);
	}
	
	//Methods
	protected void initPanels() {
		super.initPanels();
		
		JPanel tmpIssPropP = constructIssuePropertyPanel();

		this.add(tmpIssPropP, BorderLayout.CENTER);
		
	}
	
	private JPanel constructIssuePropertyPanel() {
		String[] issueTypesTmp = {DISCRETE, INTEGER, REAL};
		issueTypes = issueTypesTmp;
		
		//Initialize the comboBox.
		issueType = new JComboBox(issueTypes);
		issueType.setSelectedIndex(0);
		issueType.addItemListener(this);
		
		//Initialize the input components
		discreteTextArea = new JTextArea(20, 25);
		discreteTextEvaluationArea = new JTextArea(20,25);
		integerMinField = new JTextField(15);
		integerOtherField = new JTextField(15);
		integerLinearField = new JTextField(15);
		integerParameterField = new JTextField(15);
		
		integerMaxField = new JTextField(15);
		realMinField = new JTextField(15);
		realOtherField = new JTextField(15);
		realLinearField = new JTextField(15);
		realParameterField = new JTextField(15);
		realMaxField = new JTextField(15);
		
		//Initialize the panels.
		discretePanel = constructDiscretePanel();
		integerPanel = constructIntegerPanel();
		realPanel = constructRealPanel();
		
		issuePropertyCards = new JPanel();
		issuePropertyCards.setLayout(new CardLayout());
		issuePropertyCards.add(discretePanel, DISCRETE);
		issuePropertyCards.add(integerPanel, INTEGER);
		issuePropertyCards.add(realPanel, REAL);
		
		issuePropertyPanel = new JPanel();
		issuePropertyPanel.setBorder(BorderFactory.createTitledBorder("Issue Properties"));
		issuePropertyPanel.setLayout(new BorderLayout());
		issuePropertyPanel.add(issueType, BorderLayout.PAGE_START);
		issuePropertyPanel.add(issuePropertyCards, BorderLayout.CENTER);
		/*
		if(this.weightCheck.isSelected()){
			weightCheck.setEnabled(false);
		}
		*/
		return issuePropertyPanel;
	}
	
	private JPanel constructDiscretePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		
		JPanel textPanel = new JPanel();
		JPanel evalPanel = new JPanel();
		
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
		JLabel textLabel = new JLabel("Edit the discrete values below. Use one line for each value.");
		textPanel.add(textLabel);
		textPanel.add(new JScrollPane(discreteTextArea));
		panel.add(textPanel);
		
		
		
		evalPanel.setLayout(new BoxLayout(evalPanel, BoxLayout.PAGE_AXIS));
		JLabel evalLabel = new JLabel("Edit the evaluation values below. Use one line for each value.");
		evalPanel.add(evalLabel);
		evalPanel.add(new JScrollPane(discreteTextEvaluationArea));
		panel.add(evalPanel);
		
		if(((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace()==null){
			weightCheck.setEnabled(false);
			weightCheck.setToolTipText("Disabled until there is a Utility Space.");
			discreteTextEvaluationArea.setEnabled(false);
			discreteTextEvaluationArea.setToolTipText("Disabled until there is a Utility Space.");
		}
		
		
		return panel;
	}
	
	private JPanel constructIntegerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel("Give the bounds of the Integer values:");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(label);
		
		JPanel min = new JPanel();
		min.setAlignmentX(Component.LEFT_ALIGNMENT);
		min.add(new JLabel("Min: "));
		min.add(integerMinField);
		panel.add(min);

		JPanel lin = new JPanel();
		lin.setAlignmentX(Component.LEFT_ALIGNMENT);
		lin.add(new JLabel("Linear: "));
		lin.add(integerLinearField);
		panel.add(lin);		

		JPanel par = new JPanel();
		par.setAlignmentX(Component.LEFT_ALIGNMENT);
		par.add(new JLabel("Parameter: "));
		par.add(integerParameterField);
		panel.add(par);	
		
		JPanel max = new JPanel();
		max.setAlignmentX(Component.LEFT_ALIGNMENT);
		max.add(new JLabel("Max: "));
		max.add(integerMaxField);
		panel.add(max);

		if(((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace()==null){
			weightCheck.setEnabled(false);
			weightCheck.setToolTipText("Disabled until there is a Utility Space.");
			integerLinearField.setEnabled(false);
			integerLinearField.setToolTipText("Disabled until there is a Utility Space.");
			integerParameterField.setEnabled(false);
			integerParameterField.setToolTipText("Disabled until there is a Utility Space.");
		}
		
		return panel;
	}
	
	private JPanel constructRealPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel("Give the bounds of the Real values:");
		panel.add(label);
		
		JPanel min = new JPanel();
		min.add(new JLabel("Min: "));
		min.add(realMinField);
		panel.add(min);

		JPanel lin = new JPanel();
		lin.setAlignmentX(Component.LEFT_ALIGNMENT);
		lin.add(new JLabel("Linear: "));
		lin.add(realLinearField);
		panel.add(lin);		

		JPanel par = new JPanel();
		par.setAlignmentX(Component.LEFT_ALIGNMENT);
		par.add(new JLabel("Parameter: "));
		par.add(realParameterField);
		panel.add(par);	
		
		JPanel max = new JPanel();
		max.add(new JLabel("Max: "));
		max.add(realMaxField);
		panel.add(max);

		if(((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace()==null){
			weightCheck.setEnabled(false);
			weightCheck.setToolTipText("Disabled until there is a Utility Space.");
			realLinearField.setEnabled(false);
			realLinearField.setToolTipText("Disabled until there is a Utility Space.");
			realParameterField.setEnabled(false);
			realParameterField.setToolTipText("Disabled until there is a Utility Space.");
		}
		
		return panel;
	}
	
	protected String[] getDiscreteValues() throws InvalidInputException {
		/*int index;
		int lastIndex = 0;
		//String
		while ((index = source.indexOf("\n")) != -1) {
			String value = source.subString(lastIndex, index);
			System.out.println(value);
		}*/
		String[] values = discreteTextArea.getText().split("\n");
		for (int i = 0; i < values.length; i++)
			System.out.println(values[i]);
		return values;
	}
	
	/**
	 * Gets the evaluations for the discrete issue from the input field in this dialog. The input values don't need to be normalized.
	 * @return An nomalized array with the evaluations.
	 * @throws InvalidInputException 
	 */
	protected double[] getDiscreteEvalutions() throws InvalidInputException, ClassCastException {
		String[] evalueStrings = discreteTextEvaluationArea.getText().split("\n");
		double weightSum = 0;
		if(evalueStrings.length != 0){
			double evalues[] = new double[evalueStrings.length];
			for(int i = 0; i<evalueStrings.length; i++){
			if(!evalueStrings.equals("")){
				evalues[i] = Double.valueOf(evalueStrings[i]);
				weightSum += evalues[i];
				System.out.println(""+evalues[i]);
				}
			}
			if(weightSum != 1.0){ //normalize the evaluations.
				for(int norm_i = 0; norm_i < evalues.length; norm_i++){
					evalues[norm_i] = evalues[norm_i]/weightSum;
					
				}
				
			}
			return evalues;
		}else return null;
	}
	
	protected int getIntegerMin() throws InvalidInputException {
		return Integer.parseInt(integerMinField.getText());
	}
	
	protected int getIntegerOther() throws InvalidInputException{
		return Integer.parseInt(integerOtherField.getText());
	}

	protected int getIntegerLinear() throws InvalidInputException {
		return Integer.parseInt(integerLinearField.getText());
	}
	
	protected int getIntegerParameter() throws InvalidInputException {
		return Integer.parseInt(integerParameterField.getText());
	}	
	
	protected int getIntegerMax() throws InvalidInputException {
		return Integer.parseInt(integerMaxField.getText());
	}
	
	protected double getRealMin() throws InvalidInputException {
		return Double.parseDouble(realMinField.getText());
	}
	
	protected double getRealOther()throws InvalidInputException {
		return Double.parseDouble(realOtherField.getText());
	}
	
	protected double getRealLinear() throws InvalidInputException {
		return Double.parseDouble(realLinearField.getText());
	}
	
	protected double getRealParameter() throws InvalidInputException {
		return Double.parseDouble(realParameterField.getText());
	}
	
	protected double getRealMax() throws InvalidInputException {
		return Double.parseDouble(realMaxField.getText());
	}
	
	protected Issue constructIssue() {
		return updateIssue(null);
	}
	
	protected Issue updateIssue(Issue issue) {
		String name;
		int number;
		String description;
		Objective selected = null; //The Objective that is selected in the tree, which will be the new Issue's parent.
		boolean newIssue = (issue == null); //Defines if a new Issue is added, or if an existing Issue is being edited.
		try {
			name = getObjectiveName();
			number = getObjectiveNumber();
			description = getObjectiveDescription();
		}
		catch (InvalidInputException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			return null;
		}
		//If no issue is given to be modified, construct a new one that is the child of the selected Objective.
		if (newIssue) {
			try {
				selected = (Objective) treeFrame.getTreeTable().getTree().getLastSelectedPathComponent();
				if (selected == null) {
					JOptionPane.showMessageDialog(this, "There is no valid parent selected for this objective.");
					return null;
				}
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(this, "There is no valid parent selected for this objective.");
				return null;
			}
		}
		
		String selectedType = (String)issueType.getSelectedItem();
		//Issue issue = null;
		Evaluator evDis = null;
		if (selectedType == DISCRETE) {
			String[] values;
			double[] evalues = null;
			try {
				values = getDiscreteValues(); 
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			try{
				evalues = getDiscreteEvalutions();
				if(evalues != null){
					evDis = new EvaluatorDiscrete();
					evDis.setWeight(0.0);
				}
				
				
			}catch (Exception f){ //Can also be a casting exception.
				if(f instanceof InvalidInputException){
					JOptionPane.showMessageDialog(this, f.getMessage());
				}
			}
			
			if (newIssue) {
				issue = new IssueDiscrete(name, number, values);
			}
			else if (issue instanceof IssueDiscrete) {
				issue.setName(name);
				issue.setNumber(number);
				((IssueDiscrete)issue).clear();
				((IssueDiscrete)issue).addValues(values);
			}
			Enumeration v_enum = ((IssueDiscrete)issue).getValues();
			int eval_ind = 0;
			if(evDis != null){
				((EvaluatorDiscrete)evDis).clear();
			
				while(v_enum.hasMoreElements() && evDis != null && eval_ind < evalues.length){
					((EvaluatorDiscrete)evDis).setEvaluation(((Value)v_enum.nextElement()), evalues[eval_ind]);
					eval_ind++;
				}
			}
			
			UtilitySpace uts = ((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace();
			if(uts != null){
				//uts.getEvaluator(issue).clear();
				uts.addEvaluator(issue, evDis);
			}

		}
		else if (selectedType == INTEGER) {
			int min;
			int linear;
			int parameter;
			int max;
			
			Evaluator evInt = null;
			try {
				min = getIntegerMin();
				max = getIntegerMax();
				
				if(! integerLinearField.getText().equals("")){
					evInt = new EvaluatorInteger();
					evInt.setWeight(0.0);
					((EvaluatorInteger)evInt).setLowerBound(min);
					((EvaluatorInteger)evInt).setUpperBound(max);
					((EvaluatorInteger)evInt).setLinearParam(getIntegerLinear());
				}else if(! integerParameterField.getText().equals("")){
					evInt = new EvaluatorInteger();
					evInt.setWeight(0.0);
					((EvaluatorInteger)evInt).setLowerBound(min);
					((EvaluatorInteger)evInt).setUpperBound(max);
					((EvaluatorInteger)evInt).setConstantParam(getIntegerParameter());
				}
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			if (newIssue) {
				issue = new IssueInteger(name, number, min, max);
			}
			else if (issue instanceof IssueInteger){
				issue.setName(name);
				issue.setNumber(number);
				((IssueInteger)issue).setLowerBound(min);
				((IssueInteger)issue).setUpperBound(max);
			}
			UtilitySpace uts = ((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace();
			if(uts != null){
				uts.addEvaluator(issue, evInt);
			}
		}
		else if (selectedType == REAL) {
			double min;
			double other;
			double max;
			Evaluator evReal = null;
			try {
				min = getRealMin();
				//other = getRealOther();
				max = getRealMax();
				if(! realLinearField.getText().equals("")){
					evReal = new EvaluatorReal();
					evReal.setWeight(0.0);
					((EvaluatorReal)evReal).setLowerBound(min);
					((EvaluatorReal)evReal).setUpperBound(max);
					((EvaluatorReal)evReal).setLinearParam(getRealLinear());
				}else if(! realParameterField.getText().equals("")){
					evReal = new EvaluatorReal();
					evReal.setWeight(0.0);
					((EvaluatorReal)evReal).setLowerBound(min);
					((EvaluatorReal)evReal).setUpperBound(max);
					((EvaluatorReal)evReal).setConstantParam(getRealParameter());
				}
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			if (newIssue) {
				issue = new IssueReal(name, number, min, max);
			}
			else if (issue instanceof IssueReal){
				issue.setName(name);
				issue.setNumber(number);
				((IssueReal)issue).setLowerBound(min);
				((IssueReal)issue).setUpperBound(max);
			}
			UtilitySpace uts = ((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace();
			if(uts != null){
				uts.addEvaluator(issue, evReal);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "Please select an issue type!");
			return null;
		}
		
		issue.setDescription(description);
		if (newIssue) {
			selected.addChild(issue);
		}
		
		return issue;	
	}
	
	/**
	 * Overrides actionPerformed from Objective.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			Issue issue = constructIssue();
			if (issue == null) 
				return;
			else {
				//Notify the model that the contents of the treetable have changed.
				NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel();
				model.treeStructureChanged(this, treeFrame.getTreeTable().getTree().getSelectionPath().getPath());
				this.dispose();
			}
		}			
		else if (e.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, (String)e.getItem());
	}

}
