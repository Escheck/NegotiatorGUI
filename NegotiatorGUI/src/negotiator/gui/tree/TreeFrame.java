package negotiator.gui.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import jtreetable.JTreeTable;
import negotiator.Domain;
import negotiator.gui.tree.actions.AddIssueAction;
import negotiator.gui.tree.actions.EditAction;
import negotiator.issue.Issue;
import negotiator.issue.Objective;
import negotiator.repository.DomainRepItem;
import negotiator.utility.UtilitySpace;

/**
 * Frame from a domain.
 * 
 * @author Wouter Pasman, Mark Hendrikx
 */
public class TreeFrame extends JPanel {

	private static final long serialVersionUID = 9072786889017106286L;
	//Attributes
	private static final Color UNSELECTED = Color.WHITE;
	private static final Color HIGHLIGHT = Color.YELLOW;
	private JTreeTable treeTable;
	private NegotiatorTreeTableModel model;
	private AddIssueAction addIssueAct;
	private EditAction editAct;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private DomainRepItem fDomainRepItem;
	private JPopupMenu treePopupMenu;
	
	//Constructors
	public TreeFrame(Domain domain) {
		this(new NegotiatorTreeTableModel(domain));
	}
	
	public TreeFrame(Domain domain, UtilitySpace utilitySpace) {
		this(new NegotiatorTreeTableModel(domain, utilitySpace));
	}

	public TreeFrame(NegotiatorTreeTableModel treeModel) {
		super();
		init(treeModel, null);
	}
	
	public void clearTreeTable(Domain domain, UtilitySpace utilitySpace) {
		init(new NegotiatorTreeTableModel(domain, utilitySpace), this.getSize());
	}
	
	private void init(NegotiatorTreeTableModel treeModel, Dimension size) {
		model = treeModel;
		setLayout(new BorderLayout());

		//Initialize the table
		initTable(model);
		treeTable.addMouseListener(new TreePopupListener());
		treeTable.getSelectionModel().addListSelectionListener(new TreeSelectionListener());		
		initActions();
		
		//Initialize the Menu
		initMenus();
		initPopupMenus();
		
		JButton saveButton = new JButton("Save changes");
		Icon icon = new ImageIcon(getClass().getResource("../resources/save.png"));
		saveButton.setPreferredSize(new Dimension(180, 60));
		saveButton.setIcon(icon);
		saveButton.setFont(saveButton.getFont().deriveFont(18.0f ));
		JPanel simplePanel = new JPanel();
		simplePanel.add(saveButton);
		
		
		add(simplePanel, BorderLayout.SOUTH);
		
		if (size != null)
			this.setSize(size);
		
	}
	
	private void initTable(NegotiatorTreeTableModel model) {
		treeTable = new JTreeTable(model);
		treeTable.setPreferredSize(new Dimension(1024, 300));
		treeTable.setPreferredScrollableViewportSize(new Dimension(1024, 300));
		treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		treeTable.setRowSelectionAllowed(true);
		treeTable.setColumnSelectionAllowed(false);
		treeTable.setCellSelectionEnabled(true);
		
		TableColumnModel colModel = treeTable.getColumnModel();
		if (treeTable.getColumnCount()>3) colModel.getColumn(3).setMinWidth(220); //Wouter: make it likely that Weight column is shown completely.

		DefaultTableCellRenderer labelRenderer = new JLabelCellRenderer();
		treeTable.setDefaultRenderer(JLabel.class, labelRenderer);
		treeTable.setDefaultRenderer(JTextField.class, labelRenderer);
		
		IssueValueCellEditor valueEditor = new IssueValueCellEditor(model);
		treeTable.setDefaultRenderer(IssueValuePanel.class, valueEditor);
		treeTable.setDefaultEditor(IssueValuePanel.class, valueEditor);
		
		WeightSliderCellEditor cellEditor = new WeightSliderCellEditor(model);
		treeTable.setDefaultRenderer(WeightSlider.class, cellEditor);
		treeTable.setDefaultEditor(WeightSlider.class, cellEditor);
		treeTable.setRowHeight(24);
		
		JScrollPane treePane = new JScrollPane(treeTable);
		treePane.setBackground(treeTable.getBackground());
		add(treePane, BorderLayout.CENTER);
	}
	
	/**
	 * Recreates the Actions. Note that it doesn't reinitialise the Buttons that are dependent on it!
	 * The caller is responsible for this.
	 */
	private void initActions() {
		//Create Actions
		addIssueAct = new AddIssueAction(this, treeTable);
		editAct = new EditAction(this);
		
		//Disable the actions, since no selection is made yet
		addIssueAct.setEnabled(false);
		editAct.setEnabled(false);
	}
	
	private void initMenus() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		menuBar.add(fileMenu);
		menuBar.add(editMenu);

		fileMenu.addSeparator();
		fileMenu.addSeparator();
		fileMenu.addSeparator();
		editMenu.add(addIssueAct);
		editMenu.addSeparator();
		editMenu.add(editAct);
	}
	
	private void initPopupMenus() {
		treePopupMenu = new JPopupMenu();
		treePopupMenu.add(addIssueAct);
		treePopupMenu.addSeparator();
		treePopupMenu.add(editAct);
	}
	
	public JTreeTable getTreeTable() {
		return treeTable;
	}
	
	public NegotiatorTreeTableModel getNegotiatorTreeTableModel() {
		return model;
	}

	protected void updateHighlights(Objective selected) {
		Objective parent = null;
		if (selected != null) {
			parent = selected.getParent();
		}
		Enumeration<Objective> treeEnum = ((Objective)model.getRoot()).getPreorderEnumeration();
		while (treeEnum.hasMoreElements()) {
			Objective obj = treeEnum.nextElement();
			if (selected == null || parent == null) {
				setRowBackground(obj, UNSELECTED);
			}
			else if (parent.isParent(obj)) {
				setRowBackground(obj, HIGHLIGHT);
			}
			else {
				setRowBackground(obj, UNSELECTED);
			}
		}
	}
	
	protected void setRowBackground(Objective node, Color color) {
		model.getNameField(node).setBackground(color);
		model.getTypeField(node).setBackground(color);
		model.getNumberField(node).setBackground(color);
		model.getIssueValuePanel(node).setBackground(color);
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
			if (e.isPopupTrigger()) {
				Point point = new Point(e.getX(), e.getY());
				int rowIndex = treeTable.rowAtPoint(point);
				if (rowIndex != -1) {
					treeTable.setRowSelectionInterval(rowIndex, rowIndex);
				}				
				treePopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		} 
	}
	
	class TreeSelectionListener implements ListSelectionListener {
		
		//Methods
		public void valueChanged(ListSelectionEvent e) {
			Object selected = treeTable.getTree().getLastSelectedPathComponent();
			
			if (selected instanceof Issue) {
				addIssueAct.setEnabled(false);
				editAct.setEnabled(true);
			}
			else if (selected instanceof Objective) {
				addIssueAct.setEnabled(true);
				editAct.setEnabled(true);
			}
			
			updateHighlights((Objective)selected);
			treeTable.repaint();
		}
		
	}

	public DomainRepItem getDomainRepItem() {
		return fDomainRepItem;
	}
}