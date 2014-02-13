package negotiator.gui.boaframework;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import negotiator.boaframework.BOAagentInfo;
import negotiator.boaframework.BOAcomponent;
import negotiator.boaframework.ComponentsEnum;

import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 * @author Mark Hendrikx
 * @author W.Pasman refactored and modified 13feb2014 #883
 */
public class BOAagentsFrame extends JDialog {

	private static final long serialVersionUID = -8031426652298029936L;

	// AGENTS LIST
	private JLabel boaAgentsLabel;
	private JScrollPane agentsListSP;
	private JList agentsList;
	private DefaultListModel agentsModel;

	// BUTTONS
	private JButton addAgentButton;
	private JButton editAgentButton;
	private JButton saveButton;

	private ArrayList<BOAagentInfo> result;

	public BOAagentsFrame(Frame frame) {
		super(frame, "Select BOA agents", true);
		this.setLocation(frame.getLocation().x + frame.getWidth() / 4,
				frame.getLocation().y + frame.getHeight() / 4);
	}

	public ArrayList<BOAagentInfo> getResult(
			ArrayList<BOAagentInfo> BOAagentList) {
		initFrameUI();
		initAgentsListUI();
		initButtons();
		initControls();

		for (BOAagentInfo agent : BOAagentList) {
			agentsModel.addElement(agent);
		}

		pack();
		setVisible(true);
		return result;
	}

	private void initFrameUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		// allow deeper dialogs to appear on top of this dialog.
		setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
		setMaximumSize(new Dimension(1010, 440));
		setMinimumSize(new Dimension(1010, 440));
		setPreferredSize(new Dimension(1010, 440));
		setResizable(false);
		setTitle("Select BOA agents");
		getContentPane().setLayout(new AbsoluteLayout());
	}

	private void initAgentsListUI() {
		boaAgentsLabel = new JLabel();
		boaAgentsLabel.setText("BOA Agents");
		boaAgentsLabel.setFont(new Font("Tahoma", 1, 13));
		getContentPane().add(boaAgentsLabel,
				new AbsoluteConstraints(10, 50, -1, -1));

		agentsList = new JList();
		agentsModel = new DefaultListModel();
		agentsList.setModel(agentsModel);
		agentsListSP = new JScrollPane();
		agentsListSP.setViewportView(agentsList);
		agentsList.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
					Object[] values = agentsList.getSelectedValues();
					for (int i = 0; i < values.length; i++) {
						agentsModel.removeElement(values[i]);
					}
				}
			}
		});

		getContentPane().add(agentsListSP,
				new AbsoluteConstraints(10, 75, 980, 290));
	}

	private void initButtons() {
		addAgentButton = new JButton();
		addAgentButton.setText("Add agent(s)");
		getContentPane().add(addAgentButton,
				new AbsoluteConstraints(10, 10, 107, -1));

		editAgentButton = new JButton();
		editAgentButton.setText("Edit agent");
		getContentPane().add(editAgentButton,
				new AbsoluteConstraints(120, 10, 107, -1));

		saveButton = new JButton();
		saveButton.setText("Save agents");
		getContentPane().add(saveButton,
				new AbsoluteConstraints(10, 375, 105, -1));
	}

	private void initControls() {
		addAgentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Set<Set<BOAcomponent>> res = new BOAAgentUI(null).getResult();
				if (res != null) {
					insertNewAgents(res);
				}
			}
		});

		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<BOAagentInfo> agents = new ArrayList<BOAagentInfo>();
				for (int i = 0; i < agentsModel.getSize(); i++) {
					agents.add((BOAagentInfo) agentsModel.getElementAt(i));
				}
				result = agents;
				dispose();
			}
		});

		editAgentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BOAagentInfo s = (BOAagentInfo) agentsList.getSelectedValue();
				if (s == null) {
					JOptionPane.showMessageDialog(null,
							"Please select an agent to edit.",
							"Edit notification", 1);
				} else {
					Set<Set<BOAcomponent>> res = new BOAAgentUI(s).getResult();
					if (res != null) {
						agentsModel.removeElement(s);
						insertNewAgents(res);
					}

				}
			}
		});
	}

	private void insertNewAgents(Set<Set<BOAcomponent>> result) {

		Iterator<Set<BOAcomponent>> strategyIterator = result.iterator();
		while (strategyIterator.hasNext()) {
			Set<BOAcomponent> fullStrat = strategyIterator.next();
			Iterator<BOAcomponent> strat = fullStrat.iterator();
			BOAcomponent os = null, as = null, om = null, oms = null;
			while (strat.hasNext()) {
				BOAcomponent strategy = strat.next();
				if (strategy.getType() == ComponentsEnum.BIDDINGSTRATEGY) {
					os = strategy;
				} else if (strategy.getType() == ComponentsEnum.ACCEPTANCESTRATEGY) {
					as = strategy;
				} else if (strategy.getType() == ComponentsEnum.OPPONENTMODEL) {
					om = strategy;
				} else if (strategy.getType() == ComponentsEnum.OMSTRATEGY) {
					oms = strategy;
				}
			}
			BOAagentInfo agent = new BOAagentInfo(os, as, om, oms);
			agentsModel.addElement(agent);
		}
	}

}