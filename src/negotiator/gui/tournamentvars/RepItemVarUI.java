package negotiator.gui.tournamentvars;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import negotiator.gui.MultiListSelectionModel;
import negotiator.gui.ExtendedListModel;

/**
 * Improved version of the ProfileVarUI and AgentVarUI classes.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 04/12/11
 * @param <B>
 */
public class RepItemVarUI<A> extends JDialog {

	private static final long serialVersionUID = 1L;
	private JButton okButton = new JButton("Ok");
	private JButton clearButton = new JButton("Clear");
	private JButton cancelButton = new JButton("Cancel");
	private JList profileList = new JList();
	private JScrollPane scrollPane = new JScrollPane();
	private ArrayList<A> result;
	private ExtendedListModel<A> model;

	/**
	 * Creates the RepItem Selector.
	 * 
	 * @param frame of the caller
	 */
	public RepItemVarUI(Frame frame, String title) {
		super(frame, title, true);
		this.setLocation(frame.getLocation().x + frame.getWidth() / 2, frame.getLocation().y + frame.getHeight() / 4);
		this.setSize(frame.getSize().width / 3, frame.getSize().height / 2);
	}

	/**
	 * Initialize the GUI components. The GUI code is based on code created by
	 * using the Netbeans GUIbuilder.
	 * 
	 * @return
	 */
	public List<A> getResult(ArrayList<A> items, ArrayList<A> selectedItems) {

		// Set the list model
		model = new ExtendedListModel<A>();
		profileList.setModel(model);
		
		model.setInitialContent(items);
		
		// Set a custom selection model (each click toggles a listitem on/off)
		profileList.setSelectionModel(new MultiListSelectionModel());
		
		// Select previously selected items
		for (A item : selectedItems) {
			profileList.setSelectedValue(item, true);
		}
		
		scrollPane.setViewportView(profileList);
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<A> profiles = new ArrayList<A>();
				for (int item : profileList.getSelectedIndices()) {
					profiles.add((A) model.getElementAt(item));
				}
				result = profiles;
				dispose();
			}
		});

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				profileList.clearSelection();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// Set the layout of the GUI (autogenerated using Netbeans)
		javax.swing.GroupLayout layout = new GroupLayout(getContentPane());
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														scrollPane,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														360, Short.MAX_VALUE)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		okButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		82,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGap(18, 18,
																		18)
																.addComponent(
																		clearButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		82,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGap(18, 18,
																		18)
																.addComponent(
																		cancelButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		82,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
																.addGap(18, 18,
																		18)
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(scrollPane,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										379, Short.MAX_VALUE)
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(okButton)
												.addComponent(clearButton)
												.addComponent(cancelButton))
								.addContainerGap()));
		setVisible(true);
		return result;
	}
}