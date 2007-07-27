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
		super(owner, "Create new Objective", modal);
		
		this.treeTable = treeTable;
		
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
		descriptionArea = new JTextArea();
		descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		//Initialize the buttons
		okButton = new JButton("Ok");
		okButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		this.setLayout(new BorderLayout());
		
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
		
		JPanel buttonPanel = new JPanel();
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		this.add(labelPanel, BorderLayout.LINE_START);
		this.add(fieldPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		this.pack();
		this.setVisible(true);
	}
	
	//Methods
	public void commitNewObjective() {
		Objective selected = (Objective) treeTable.getTree().getLastSelectedPathComponent();
		
		//The selected Objective is the parent.
		Objective newObjective = new Objective();
		newObjective.setName(nameField.getText());
		//Number?
		newObjective.setDescription(descriptionArea.getText());
		selected.addChild(newObjective);
		
		//Notify the model that the contents of the treetable have changed.
		NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeTable.getTree().getModel();
		model.treeStructureChanged(this, treeTable.getTree().getSelectionPath().getPath());
		
		System.out.println(selected.toString());
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton)
			commitNewObjective();
		else
			System.out.println("Source in NewObjectiveDialog was not okButton.");
			
	}
	
	
}
