package negotiator.gui.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import jtreetable.JTreeTable;

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
		
		//Initialize the panels.
		discretePanel = new JPanel();
		discretePanel.add(new JLabel("Wat moet hier komen?"));
		
		integerPanel = new JPanel();
		integerPanel.add(new JLabel("En hier?"));
		
		realPanel = new JPanel();
		realPanel.add(new JLabel("Nog een panel :-)"));
		
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
	
	public void itemStateChanged(ItemEvent e) {
		((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, (String)e.getItem());
	}
}
