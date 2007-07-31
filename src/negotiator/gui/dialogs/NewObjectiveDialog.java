package negotiator.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jtreetable.*;
import negotiator.gui.tree.*;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class NewObjectiveDialog extends JDialog implements ActionListener {

	//Attributes	
	JButton okButton;
	JButton cancelButton;
	
	JLabel nameLabel;
	JLabel numberLabel;
	JLabel descriptionLabel;
	
	JTextField nameField;
	JTextField numberField; //TODO: make this non editable
	//WeightSlider?
	JTextArea descriptionArea;
	
	JTreeTable treeTable;
	
	//Constructors
	
	public NewObjectiveDialog(JTreeTable treeTable) {
		this(null, false, treeTable);
	}
		
	public NewObjectiveDialog(Frame owner, boolean modal, JTreeTable treeTable) {
		this(owner, modal, treeTable, "Create new Objective");
	}
	
	public NewObjectiveDialog(Frame owner, boolean modal, JTreeTable treeTable, String name) {
		super(owner, name, modal);
		
		this.treeTable = treeTable;
		
		initPanels();
		
		this.pack();
		this.setVisible(true);
	}
	
	//Methods
	protected void initPanels() {
		this.setLayout(new BorderLayout());
		
		this.add(constructBasicPropertyPanel(), BorderLayout.NORTH);
		this.add(constructButtonPanel(), BorderLayout.SOUTH);
	}
	
	private JPanel constructBasicPropertyPanel() {
		//Initialize the labels
		nameLabel = new JLabel("Name:");
		nameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		numberLabel = new JLabel("Number:");
		numberLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		descriptionLabel = new JLabel("Description:");
		descriptionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		//Initialize the fields
		nameField = new JTextField();
		nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
		numberField = new JTextField();
		numberField.setAlignmentX(Component.LEFT_ALIGNMENT);
		numberField.setEditable(false);
		numberField.setText("" + (((NegotiatorTreeTableModel)treeTable.getTree().getModel()).getHighestObjectiveNr() + 1));
		descriptionArea = new JTextArea();
		descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
		
		labelPanel.add(new JLabel("Name:"));
		labelPanel.add(new JLabel("Number:"));
		labelPanel.add(new JLabel("Description:"));
		
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.PAGE_AXIS));
		
		fieldPanel.add(nameField);
		fieldPanel.add(numberField);
		fieldPanel.add(descriptionArea);
		
		JPanel basicPropertyPanel = new JPanel();
		basicPropertyPanel.setBorder(BorderFactory.createTitledBorder("Basic Properties"));
		basicPropertyPanel.setLayout(new BorderLayout());
		basicPropertyPanel.add(labelPanel, BorderLayout.LINE_START);
		basicPropertyPanel.add(fieldPanel, BorderLayout.CENTER);
		
		return basicPropertyPanel;
	}
	
	/**
	 * Initializes the buttons, and returns a panel containing them.
	 * @return a JPanel with the buttons.
	 */
	private JPanel constructButtonPanel() {
		//Initialize the buttons
		okButton = new JButton("Ok");
		okButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		return buttonPanel;
	}
	
	public void commitNewObjective() {
		Objective selected = (Objective) treeTable.getTree().getLastSelectedPathComponent();
		
		//The selected Objective is the parent.
		Objective newObjective = new Objective();
		newObjective.setName(nameField.getText());
		newObjective.setNumber(Integer.parseInt(numberField.getText()));
		newObjective.setDescription(descriptionArea.getText());
		selected.addChild(newObjective);
		
		//Notify the model that the contents of the treetable have changed.
		NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeTable.getTree().getModel();
		model.treeStructureChanged(this, treeTable.getTree().getSelectionPath().getPath());
		
		this.dispose();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton)
			commitNewObjective();
		else
			this.dispose();
			
	}
	
	
}
