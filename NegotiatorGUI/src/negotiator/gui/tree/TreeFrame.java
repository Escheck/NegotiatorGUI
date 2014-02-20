package negotiator.gui.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import jtreetable.JTreeTable;
import negotiator.Domain;
import negotiator.gui.dialogs.EditIssueDialog;
import negotiator.gui.dialogs.NewIssueDialog;
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
	// Attributes
	private static final Color UNSELECTED = Color.WHITE;
	private static final Color HIGHLIGHT = Color.YELLOW;
	private JTreeTable treeTable;
	private NegotiatorTreeTableModel model;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private DomainRepItem fDomainRepItem;
	// as we can't use this in a listener
	private TreeFrame thisFrame;
	private boolean hasNoProfiles;
	private JTextField discount;
	private JTextField reservationValue;

	// Constructors
	public TreeFrame(Domain domain, boolean hasNoProfiles) {
		this(new NegotiatorTreeTableModel(domain), hasNoProfiles);
	}

	public TreeFrame(Domain domain, UtilitySpace utilitySpace) {
		this(new NegotiatorTreeTableModel(domain, utilitySpace), false);
	}

	public TreeFrame(NegotiatorTreeTableModel treeModel, boolean hasNoProfiles) {
		super();
		this.hasNoProfiles = hasNoProfiles;
		init(treeModel, null);
	}

	public void clearTreeTable(Domain domain, UtilitySpace utilitySpace) {
		init(new NegotiatorTreeTableModel(domain, utilitySpace), this.getSize());
	}

	public boolean isDomain() {
		return model.getUtilitySpace() == null;
	}

	private void init(NegotiatorTreeTableModel treeModel, Dimension size) {
		thisFrame = this;
		model = treeModel;
		setLayout(new BorderLayout());
		// Initialize the table
		initTable(model);
		treeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {

				if (e.getClickCount() == 2) {
					Object selected = treeTable.getTree()
							.getLastSelectedPathComponent();

					if (selected instanceof Issue) {
						if (hasNoProfiles || !isDomain()) {
							new EditIssueDialog(thisFrame, (Issue) selected);
						} else {
							JOptionPane
									.showMessageDialog(
											null,
											"You may only edit the issues when there are no preference profiles.",
											"Edit error", 0);
						}
					}
				}
			}
		});
		treeTable.setRowHeight(40);
		// Initialize the Menu
		initMenus();
		JPanel simplePanel = new JPanel();

		JButton saveButton = new JButton("Save changes");
		Icon icon = new ImageIcon(getClass().getClassLoader().getResource(
				"negotiator/gui/resources/save.png"));
		saveButton.setPreferredSize(new Dimension(180, 60));
		saveButton.setIcon(icon);
		saveButton.setFont(saveButton.getFont().deriveFont(18.0f));
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (model.getUtilitySpace() != null) {
					double newDiscount = 1.0;
					try {
						newDiscount = Double.parseDouble(discount.getText());
						if (newDiscount < 0 || newDiscount > 1) {
							JOptionPane.showMessageDialog(null,
									"The discount value is not valid.");
							return;
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null,
								"The discount value is not valid.");
						return;
					}
					double newRV = 1.0;
					try {
						newRV = Double.parseDouble(reservationValue.getText());
						if (newRV < 0 || newRV > 1) {
							JOptionPane.showMessageDialog(null,
									"The reservation value is not valid.");
							return;
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null,
								"The reservation value is not valid.");
						return;
					}
					model.getUtilitySpace().setDiscount(newDiscount);
					model.getUtilitySpace().setReservationValue(newRV);
					try {
						model.getUtilitySpace()
								.toXML()
								.saveToFile(
										model.getUtilitySpace().getFileName());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else { // this is a domain
					model.getDomain().toXML()
							.saveToFile(model.getDomain().getName());
				}
			}
		});

		if (hasNoProfiles) {

			JButton addIssue = new JButton("Add issue");
			Icon icon2 = new ImageIcon(getClass().getClassLoader().getResource(
					"negotiator/gui/resources/edit_add-32.png"));
			addIssue.setPreferredSize(new Dimension(180, 60));
			addIssue.setIcon(icon2);
			addIssue.setFont(addIssue.getFont().deriveFont(18.0f));
			addIssue.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					new NewIssueDialog(thisFrame);
				}
			});
			simplePanel.add(addIssue);

			JButton removeIssue = new JButton("Remove issue");
			Icon icon3 = new ImageIcon(getClass().getClassLoader().getResource(
					"negotiator/gui/resources/edit_remove-32.png"));
			removeIssue.setPreferredSize(new Dimension(180, 60));
			removeIssue.setIcon(icon3);
			removeIssue.setFont(removeIssue.getFont().deriveFont(18.0f));
			removeIssue.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					Object selected = treeTable.getTree()
							.getLastSelectedPathComponent();

					if (selected instanceof Issue) {
						((Issue) selected).removeFromParent();
						// correct numbering
						for (int i = 0; i < model.getDomain().getIssues()
								.size(); i++) {
							model.getDomain().getIssues().get(i)
									.setNumber(i + 1); // + 1 for root
						}
						treeTable.updateUI();
					}
				}
			});
			simplePanel.add(removeIssue);
		}
		simplePanel.add(saveButton);

		if (model.getUtilitySpace() != null) {
			simplePanel.add(new JLabel("Discount: "));
			discount = new JTextField(model.getUtilitySpace()
					.getDiscountFactor() + "", 5);
			simplePanel.add(discount);

			simplePanel.add(new JLabel("Reservation value: "));
			reservationValue = new JTextField(model.getUtilitySpace()
					.getReservationValueUndiscounted() + "", 5);
			simplePanel.add(reservationValue);
		}

		add(simplePanel, BorderLayout.SOUTH);

		if (size != null)
			this.setSize(size);

	}

	public boolean hasNoProfiles() {
		return hasNoProfiles;
	}

	private void initTable(NegotiatorTreeTableModel model) {
		treeTable = new JTreeTable(model);
		treeTable.setPreferredSize(new Dimension(1024, 600));
		treeTable.setPreferredScrollableViewportSize(new Dimension(1024, 300));
		treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		treeTable.setRowSelectionAllowed(true);
		treeTable.setColumnSelectionAllowed(false);
		treeTable.setCellSelectionEnabled(true);

		TableColumnModel colModel = treeTable.getColumnModel();
		if (treeTable.getColumnCount() > 3)
			colModel.getColumn(3).setMinWidth(220); // Wouter: make it likely
													// that Weight column is
													// shown completely.

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

	private void initMenus() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
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
		Enumeration<Objective> treeEnum = ((Objective) model.getRoot())
				.getPreorderEnumeration();
		while (treeEnum.hasMoreElements()) {
			Objective obj = treeEnum.nextElement();
			if (selected == null || parent == null) {
				setRowBackground(obj, UNSELECTED);
			} else if (parent.isParent(obj)) {
				setRowBackground(obj, HIGHLIGHT);
			} else {
				setRowBackground(obj, UNSELECTED);
			}
		}
	}

	public Objective getRoot() {
		return (Objective) model.getRoot();
	}

	protected void setRowBackground(Objective node, Color color) {
		model.getNameField(node).setBackground(color);
		model.getTypeField(node).setBackground(color);
		model.getNumberField(node).setBackground(color);
		model.getIssueValuePanel(node).setBackground(color);
	}

	public DomainRepItem getDomainRepItem() {
		return fDomainRepItem;
	}
}