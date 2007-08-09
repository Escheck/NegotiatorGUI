package negotiator.gui.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import jtreetable.JTreeTable;

import negotiator.gui.dialogs.NewObjectiveDialog.InvalidInputException;
import negotiator.gui.tree.NegotiatorTreeTableModel;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class NewIssueDialog extends NewObjectiveDialog implements ItemListener {
	//Variables
	private static final String DISCRETE = "Discrete";
	private static final String INTEGER = "Integer";
	private static final String REAL = "Real";
	
	private JComboBox issueType;
	private String[] issueTypes;// = {DISCRETE, INTEGER, REAL}; <- for some weird reason this doesn't work
	private JPanel issuePropertyCards;
	private JPanel issuePropertyPanel;
	private JPanel discretePanel;
	private JPanel integerPanel;
	private JPanel realPanel;
	
	private JTextArea discreteTextArea;
	
	private JTextField integerMinField;
	private JTextField integerMaxField;
	
	private JTextField realMinField;
	private JTextField realMaxField;
	
	//Constructors
	public NewIssueDialog(JTreeTable treeTable) {
		this(null, false, treeTable);
	}
		
	public NewIssueDialog(Frame owner, boolean modal, JTreeTable treeTable) {
		super(owner, modal, treeTable, "Create new Issue");
	}
	
	//Methods
	protected void initPanels() {
		super.initPanels();
		
		this.add(constructIssuePropertyPanel(), BorderLayout.CENTER);
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
		integerMinField = new JTextField(15);
		integerMaxField = new JTextField(15);
		realMinField = new JTextField(15);
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
		
		return issuePropertyPanel;
	}
	
	private JPanel constructDiscretePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel("Edit the discrete values below. Use one line for each value.");
		panel.add(label);
		panel.add(new JScrollPane(discreteTextArea));
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
		JPanel max = new JPanel();
		max.setAlignmentX(Component.LEFT_ALIGNMENT);
		max.add(new JLabel("Max: "));
		max.add(integerMaxField);
		panel.add(max);
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
		JPanel max = new JPanel();
		max.add(new JLabel("Max: "));
		max.add(realMaxField);
		panel.add(max);
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
	
	protected int getIntegerMin() throws InvalidInputException {
		return Integer.parseInt(integerMinField.getText());
	}
	
	protected int getIntegerMax() throws InvalidInputException {
		return Integer.parseInt(integerMaxField.getText());
	}
	
	protected double getRealMin() throws InvalidInputException {
		return Double.parseDouble(realMinField.getText());
	}
	
	protected double getRealMax() throws InvalidInputException {
		return Double.parseDouble(realMaxField.getText());
	}
	 
	protected Issue constructIssue() {
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
				NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeTable.getTree().getModel();
				model.treeStructureChanged(this, treeTable.getTree().getSelectionPath().getPath());
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
