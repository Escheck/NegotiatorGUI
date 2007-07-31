package negotiator.gui.tree;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import jtreetable.*;
import negotiator.*;
import negotiator.utility.*;
import negotiator.gui.tree.actions.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class TreeFrame extends JFrame {
	
	//Attributes
	private JTreeTable treeTable;
	private NegotiatorTreeTableModel model;
	
	private AddAction addAct;
	private AddObjectiveAction addObjectiveAct;
	private AddIssueAction addIssueAct;
	private DeleteAction delAct;
	private EditAction editAct;
	private ExitAction exitAct;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	
	private JPopupMenu treePopupMenu;
	
	private JMenuItem addObjectiveMenuItem;
	private JMenuItem addIssueMenuItem;
	private JMenuItem exitMenuItem;
	
	//Constructors
	public TreeFrame(Domain domain) {
		this(new NegotiatorTreeTableModel(domain));
	}
	
	public TreeFrame(Domain domain, UtilitySpace utilitySpace) {
		this(new NegotiatorTreeTableModel(domain, utilitySpace));
	}
	
	public TreeFrame(NegotiatorTreeTableModel treeModel) {
		super();
		
		model = treeModel;
		
		this.getContentPane().setLayout(new BorderLayout());
		
		//Initialize the table
		initTable(model);
		treeTable.addMouseListener(new TreePopupListener());
		
		//Create Actions
		addAct = new AddAction();
		addObjectiveAct = new AddObjectiveAction(this, treeTable);
		addIssueAct = new AddIssueAction(this, treeTable);
		delAct = new DeleteAction();
		editAct = new EditAction();
		exitAct = new ExitAction(this);
		
		//Initialize the Menu
		initMenuItems();
		initMenus();
		initPopupMenus();
		
		//Initialise the Panel with buttons.
		JPanel controls = new JPanel();
		controls.setBorder(BorderFactory.createTitledBorder("Edit nodes"));
		controls.add(new JButton(addAct));
		controls.add(new JButton(addObjectiveAct));
		controls.add(new JButton(addIssueAct));
		controls.add(new JButton(delAct));
		controls.add(new JButton(editAct));
		this.getContentPane().add(controls, BorderLayout.PAGE_END);
		
		//Do nothing on closing, since we might need different behaviour.
		//See negotiator.gui.tree.actions.ExitAction
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.pack();
		this.setVisible(true);
	}
	
	//Methods
	private void initTable(NegotiatorTreeTableModel model) {
		treeTable = new JTreeTable(model);
		treeTable.setPreferredSize(new Dimension(1024, 600));
		treeTable.setPreferredScrollableViewportSize(new Dimension(1024, 600));
		
		WeightSliderCellEditor cellEditor = new WeightSliderCellEditor(model);
		treeTable.setDefaultRenderer(WeightSlider.class, cellEditor);
		treeTable.setDefaultEditor(WeightSlider.class, cellEditor);
		treeTable.getColumnModel().getColumn(4).setPreferredWidth(new WeightSlider(model).getPreferredSize().width);
		treeTable.setRowHeight(new WeightSlider(model).getPreferredSize().height);
		//treeTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(slider));
		
		JScrollPane treePane = new JScrollPane(treeTable);
		treePane.setBackground(treeTable.getBackground());
		this.getContentPane().add(treePane, BorderLayout.CENTER);
	}
	
	private void initMenuItems() {
		addObjectiveMenuItem = new JMenuItem(addObjectiveAct);
		addIssueMenuItem = new JMenuItem(addIssueAct);
		exitMenuItem = new JMenuItem(exitAct);
	}
	
	private void initMenus() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		
		fileMenu.add(new JMenuItem("Test"));
		fileMenu.add(exitAct);
		
		editMenu.add(addObjectiveMenuItem);
		editMenu.add(addIssueMenuItem);
		
		this.setJMenuBar(menuBar);
	}
	
	private void initPopupMenus() {
		treePopupMenu = new JPopupMenu();
		
		treePopupMenu.add(addObjectiveMenuItem);
		treePopupMenu.add(addIssueMenuItem);
	}
	
	public JTreeTable getTreeTable() {
		return treeTable;
	}
	
	public NegotiatorTreeTableModel getNegotiatorTreeTableModel() {
		return model;
	}
	
	class TreePopupListener extends MouseAdapter {
		
		//Methods
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}
		
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		} 
		
		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger())
				treePopupMenu.show(e.getComponent(), e.getX(), e.getY());
		} 
	}

}
