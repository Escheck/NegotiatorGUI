package negotiator.gui.negosession;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import negotiator.repository.ProfileRepItem;
import negotiator.repository.RepItem;

/**
 * Dialog to request a list of items of the type T from the user
 * 
 * @author W.Pasman loosely based on a form that was generated from IntelliJ
 *         that showed unreadable and uneditable.
 *
 * @param <T>
 *            the type of items (must extend {@link RepItem}) for in the list.
 */
@SuppressWarnings("serial")
public class AddFromListDialog<T extends RepItem> extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JList lstContent;

	public AddFromListDialog(Component parent, List<T> repItems) {
		init();
		setLocationRelativeTo(parent);
		setModel(repItems);

		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);
		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void setModel(List<T> lst) {
		final List<T> modelList = new ArrayList<T>(lst);
		lstContent.setModel(new AbstractListModel() {
			@Override
			public int getSize() {
				return modelList.size();
			}

			@Override
			public Object getElementAt(int index) {
				return modelList.get(index);
			}

		});
		lstContent.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index,
						isSelected, cellHasFocus);

				if (value instanceof ProfileRepItem)
					setText(MultilateralUI.getShortPath((ProfileRepItem) value));

				else if (value instanceof RepItem)
					setText(((RepItem) value).getName());

				return this;
			}
		});
	}

	public List<T> getSelected() {
		List<Object> selectedObjects = Arrays.asList(lstContent
				.getSelectedValues());
		List<T> selectedRepItems = new ArrayList<T>(selectedObjects.size());

		for (Object selectedObject : selectedObjects) {
			@SuppressWarnings("unchecked")
			boolean add = selectedRepItems.add((T) selectedObject);
		}
		return selectedRepItems;
	}

	private void onOK() {
		dispose();
	}

	private void onCancel() {
		dispose();
	}

	private void init() {
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		final JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		contentPane.add(panel1, BorderLayout.SOUTH);

		// The buttons panel
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout());
		panel1.add(panel2, BorderLayout.CENTER);
		buttonOK = new JButton("OK");
		panel2.add(buttonOK);
		buttonCancel = new JButton("Cancel");
		panel2.add(buttonCancel);

		final JPanel panel3 = new JPanel();
		panel3.setLayout(new BorderLayout());

		contentPane.add(panel3, BorderLayout.CENTER);

		final JScrollPane scrollPane1 = new JScrollPane();
		panel3.add(scrollPane1, BorderLayout.CENTER);
		lstContent = new JList();
		final DefaultListModel defaultListModel1 = new DefaultListModel();
		lstContent.setModel(defaultListModel1);
		scrollPane1.setViewportView(lstContent);
	}

}
