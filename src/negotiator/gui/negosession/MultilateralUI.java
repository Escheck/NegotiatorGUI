package negotiator.gui.negosession;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import negotiator.Deadline;
import negotiator.config.Configuration;
import negotiator.gui.NegoGUIApp;
import negotiator.gui.progress.MultiPartyTournamentProgressUI;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.RepItem;
import negotiator.session.TournamentManager;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

/**
 * This is the user interface for the multilateral tournament.
 * <p/>
 * The user interface elements where generated using Intellij IDEA, so if you
 * need to change them, you should probably use the same program (there exists a
 * free Community Edition (CE)).
 * <p/>
 * The configuration of this user interface is stored in the
 * {@link negotiator.config.Configuration} variable, which is also used by the
 * tournament manager to run the tournaments.
 *
 * @author David Festen
 */
@SuppressWarnings("UnusedDeclaration")
public class MultilateralUI extends JPanel {
	private JComboBox cmbProtocol;
	private JSpinner txtNumberOfTournaments;
	private JComboBox cmbMediator;
	private JButton btnStartTournament;
	private JPanel pnlRoot;
	private JPanel pnlMediator;
	private JPanel pnlEnvironment;
	private JLabel lblProtocol;
	private JLabel lblDeadlines;
	private JLabel lblNumberOfTournaments;
	private JLabel lblDeadlinesValue;
	private JButton btnDeadlines;
	private JLabel lblMediator;
	private JButton btnAddAgents;
	private JButton btnClearAgents;
	private JButton btnRemoveAgents;
	private JButton btnAddProfiles;
	private JButton btnRemoveProfiles;
	private JButton btnClearProfiles;
	private JPanel pnlAgentButtons;
	private JPanel pnlProfileButtons;
	private JList lstAgents;
	private JList lstProfiles;
	private JPanel pnlAgentsProfiles;
	private JLabel lblMediatorProfile;
	private JComboBox cmbMediatorProfile;
	private JSpinner txtAgentsPerSession;
	private JLabel lblAgentsPerSession;
	private JCheckBox chkDuplicateAgents;
	private JLabel lblDuplicateAgents;
	private JPanel pnlStartTournament;

	// Holds the configuration for this user interface
	private final Configuration config;

	/**
	 * Creates a new instance of the
	 * {@link negotiator.gui.negosession.MultilateralUI} object. This will
	 * initialize all the values to their respective defaults. Also makes sure
	 * that all action listeners are bind.
	 */
	public MultilateralUI() {
		// basics
		super();
		add(pnlRoot);
		setVisible(true);
		config = new Configuration();

		// set config defaults
		config.setDeadlines(new Deadline(0, 180));
		config.setProtocolItem(ContentProxy.fetchProtocols().get(0));
		config.setNumSessions(1);
		config.setNumAgentsPerSession(2);
		updateRepetitionRequired();

		// set renderers
		RepItemComboRenderer cmbRenderer = new RepItemComboRenderer();
		RepItemListRenderer lstRenderer = new RepItemListRenderer();
		cmbProtocol.setRenderer(cmbRenderer);
		cmbMediator.setRenderer(cmbRenderer);
		lstAgents.setCellRenderer(lstRenderer);
		lstProfiles.setCellRenderer(lstRenderer);
		cmbMediatorProfile.setRenderer(cmbRenderer);

		// set models
		cmbMediatorProfile.setModel(asComboBoxModel(ContentProxy
				.fetchProfiles()));
		cmbProtocol.setModel(asComboBoxModel(ContentProxy.fetchProtocols()));
		config.setProtocolItem((MultiPartyProtocolRepItem) cmbProtocol
				.getSelectedItem());
		lstAgents.setModel(new ListModel<PartyRepItem>(config.getPartyItems()));
		lstProfiles.setModel(new DefaultListModel());

		// set text values
		lblDeadlinesValue.setText(asString(config.getDeadlines()));
		txtNumberOfTournaments.setValue(1);
		txtAgentsPerSession.setValue(2);

		// show/hide mediator panel
		MultiPartyProtocolRepItem item = (MultiPartyProtocolRepItem) cmbProtocol
				.getSelectedItem();
		pnlMediator.setVisible(item.getHasMediator());
		lblMediatorProfile.setVisible(item.getHasMediatorProfile());
		cmbMediatorProfile.setVisible(item.getHasMediatorProfile());

		// set actions
		btnDeadlines.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DeadlineDialog dialog = new DeadlineDialog(MultilateralUI.this,
						config.getDeadlines());
				dialog.pack();
				dialog.setVisible(true);

				config.setDeadlines(dialog.getDeadlines());
				lblDeadlinesValue.setText(asString(config.getDeadlines()));
			}
		});
		cmbMediator.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PartyRepItem item = (PartyRepItem) cmbMediator
						.getSelectedItem();
				config.setMediatorItem(item);
			}
		});
		cmbProtocol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MultiPartyProtocolRepItem item = (MultiPartyProtocolRepItem) cmbProtocol
						.getSelectedItem();
				config.setProtocolItem(item);
				pnlMediator.setVisible(item.getHasMediator());
				lblMediatorProfile.setVisible(item.getHasMediatorProfile());
				cmbMediatorProfile.setVisible(item.getHasMediatorProfile());
				cmbMediator.setModel(asComboBoxModel(ContentProxy
						.fetchMediatorsForProtocol(config.getProtocolItem())));
				if (item.getHasMediator()) {
					config.setMediatorItem((PartyRepItem) cmbMediator
							.getSelectedItem());
				}
				if (item.getHasMediatorProfile()) {
					config.setMediatorProfile((ProfileRepItem) cmbMediatorProfile
							.getSelectedItem());
				}
				config.getPartyItems().clear();
				lstAgents.setModel(new ListModel<PartyRepItem>());
				updateAgentsPerSession();
				updateRepetitionRequired();
			}
		});
		btnAddAgents.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<PartyRepItem> items = ContentProxy
						.fetchPartiesForProtocol(config.getProtocolItem());
				AddFromListDialog dialog = new AddFromListDialog<PartyRepItem>(
						MultilateralUI.this, items);
				dialog.pack();
				dialog.setVisible(true);

				@SuppressWarnings("unchecked")
				List<PartyRepItem> selected = dialog.getSelected();
				config.setPartyItems(selected);
				lstAgents.setModel(new ListModel<PartyRepItem>(config
						.getPartyItems()));
				updateRepetitionRequired();
			}
		});
		btnRemoveAgents.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices = lstAgents.getSelectedIndices();
				for (int i = indices.length - 1; i >= 0; i--) {
					config.getPartyItems().remove(indices[i]);
				}
				lstAgents.setModel(new ListModel<PartyRepItem>(config
						.getPartyItems()));
				updateAgentsPerSession();
				updateRepetitionRequired();
			}
		});
		btnClearAgents.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				config.getPartyItems().clear();
				lstAgents.setModel(new ListModel<PartyRepItem>());
				updateAgentsPerSession();
				updateRepetitionRequired();
			}
		});
		btnAddProfiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<ProfileRepItem> items = ContentProxy.fetchProfiles();
				AddFromListDialog dialog = new AddFromListDialog<ProfileRepItem>(
						MultilateralUI.this, items);
				dialog.pack();
				dialog.setVisible(true);

				@SuppressWarnings("unchecked")
				List<ProfileRepItem> selected = dialog.getSelected();
				config.setPartyProfileItems(selected);

				lstProfiles.setModel(new AbstractListModel() {
					@Override
					public int getSize() {
						return config.getPartyProfileItems().size();
					}

					@Override
					public Object getElementAt(int index) {
						return getShortPath(config.getPartyProfileItems().get(
								index));
					}
				});

				updateAgentsPerSession();
				updateRepetitionRequired();
			}
		});
		btnRemoveProfiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices = lstProfiles.getSelectedIndices();
				for (int i = indices.length - 1; i >= 0; i--) {
					config.getPartyProfileItems().remove(indices[i]);
				}
				lstProfiles.setModel(new ListModel<ProfileRepItem>(config
						.getPartyProfileItems()));
				updateRepetitionRequired();
			}
		});
		btnClearProfiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				config.getPartyProfileItems().clear();
				lstProfiles.setModel(new ListModel<ProfileRepItem>());
				updateRepetitionRequired();
			}
		});
		btnStartTournament.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startNegotiation();
			}
		});
		txtNumberOfTournaments.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				config.setNumSessions((Integer) txtNumberOfTournaments
						.getValue());
			}
		});
		txtAgentsPerSession.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				config.setNumAgentsPerSession((Integer) txtAgentsPerSession
						.getValue());
				updateRepetitionRequired();
			}
		});
		chkDuplicateAgents.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean allowed = chkDuplicateAgents.isSelected();
				config.setRepetitionAllowed(allowed);
			}
		});
	}

	/**
	 * Create a DefaultComboBoxModel from a list of items
	 *
	 * @param items
	 *            items to create combo box out of
	 * @param <T>
	 *            type of the items
	 * @return a DefaultComboBoxModel
	 */
	private static <T> DefaultComboBoxModel asComboBoxModel(List<T> items) {
		return new DefaultComboBoxModel(new Vector<T>(items));
	}

	/**
	 * String representation of a map of things
	 *
	 * @param map
	 *            map of things
	 * @return human readable string
	 */
	private static String asString(Deadline deadline) {
		String time = "time: " + deadline.getTotalTime();
		String round = "round:" + deadline.getTotalRounds();
		if (deadline.isTime()) {
			if (deadline.isRounds()) {
				return time + " or " + round;
			}
			return time;
		}
		if (deadline.isRounds()) {
			return round;
		}
		return "Not set";
	}

	/**
	 * This will shorten the Profile rep items path by displaying only the
	 * rightmost two scopes. for example:
	 * /etc/templates/y2012/Phone/Phone-A-prof1.xml => Phone/Phone-A-prof1.xml.
	 *
	 * @param profileRepItem
	 *            The item to get the short path of
	 * @return the short path string
	 */
	public static String getShortPath(ProfileRepItem profileRepItem) {
		String[] split = profileRepItem.toString().split("/");
		if (split.length < 2)
			return profileRepItem.toString();
		else
			return split[split.length - 2] + "/" + split[split.length - 1];
	}

	// updates whether the repetition is required or not
	private void updateRepetitionRequired() {
		if (config.getPartyItems().size() < config.getNumAgentsPerSession()) {
			chkDuplicateAgents.setEnabled(false);
			chkDuplicateAgents.setSelected(true);
			chkDuplicateAgents.setText("Repetition required");
			config.setRepetitionAllowed(true);
		} else {
			chkDuplicateAgents.setEnabled(true);
			chkDuplicateAgents.setSelected(false);
			chkDuplicateAgents.setText("");
			config.setRepetitionAllowed(false);
		}
	}

	/**
	 * Start a new tournament of negotiation sessions
	 */
	private void startNegotiation() {
		try {

			btnStartTournament.setText("Running...");
			btnStartTournament.setEnabled(false);
			btnStartTournament.repaint();

			MultiPartyTournamentProgressUI progressUI = new MultiPartyTournamentProgressUI();
			NegoGUIApp.negoGUIView.replaceTab("Multi Tour. Progr.", this,
					progressUI);

			final TournamentManager manager = new TournamentManager(
					new Configuration(config));
			manager.start(); // runs the manager thread async
			System.out.println("Negotiation started successfully");

			// Checks manager thread async in a polling fashion
			// NB: Better to do this as a listener.
			(new Thread() {
				@Override
				public void run() {
					while (true) {
						if (!manager.isAlive()) {

							btnStartTournament.setText("Start Tournament");
							btnStartTournament.setEnabled(true);
							btnStartTournament.repaint();
							break;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				}
			}).start();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("System exited with error: " + e.getMessage());
		}
	}

	// /**
	// * Gets the data for the participants table.
	// *
	// * @return the data for each row and column
	// */
	// private Object[][] getParticipantTableData()
	// {
	// List<PartyRepItem> parties = config.getPartyItems();
	// List<ProfileRepItem> profiles = config.getPartyProfileItems();
	// Object[][] data = new Object[parties.size()][3];
	// for (int i = 0; i < parties.size(); i++)
	// {
	// data[i][0] = i + 1;
	// data[i][1] = parties.get(i);
	// data[i][2] = profiles.get(i);
	// }
	// return data;
	// }

	/**
	 * Set the number of agents per session text box to the number of profiles
	 */
	private void updateAgentsPerSession() {
		txtAgentsPerSession.setValue(config.getPartyProfileItems().size());
	}

	{
		// GUI initializer generated by IntelliJ IDEA GUI Designer
		// >>> IMPORTANT!! <<<
		// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT
	 * edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		pnlRoot = new JPanel();
		pnlRoot.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0),
				-1, -1));
		pnlRoot.setInheritsPopupMenu(false);
		pnlRoot.setMaximumSize(new Dimension(1280, 2147483647));
		pnlRoot.setMinimumSize(new Dimension(760, 320));
		pnlRoot.setPreferredSize(new Dimension(760, 640));
		pnlRoot.setBorder(BorderFactory
				.createTitledBorder("Multilateral Negotiation Tournament Setup"));
		pnlMediator = new JPanel();
		pnlMediator.setLayout(new GridLayoutManager(2, 2,
				new Insets(0, 0, 0, 0), -1, -1));
		pnlRoot.add(pnlMediator, new GridConstraints(1, 0, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_WANT_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
				null, 0, false));
		pnlMediator.setBorder(BorderFactory.createTitledBorder("Mediator"));
		lblMediator = new JLabel();
		lblMediator.setText("Mediator Strategy");
		pnlMediator.add(lblMediator, new GridConstraints(0, 0, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1),
				null, 0, false));
		cmbMediator = new JComboBox();
		final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
		defaultComboBoxModel1.addElement("item 1");
		defaultComboBoxModel1.addElement("item 2");
		defaultComboBoxModel1.addElement("item 3");
		defaultComboBoxModel1.addElement("item 4");
		cmbMediator.setModel(defaultComboBoxModel1);
		pnlMediator.add(cmbMediator, new GridConstraints(0, 1, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		lblMediatorProfile = new JLabel();
		lblMediatorProfile.setText("Mediator Profile");
		pnlMediator.add(lblMediatorProfile, new GridConstraints(1, 0, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1),
				null, 0, false));
		cmbMediatorProfile = new JComboBox();
		final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
		defaultComboBoxModel2.addElement("item 1");
		defaultComboBoxModel2.addElement("item 2");
		defaultComboBoxModel2.addElement("item 3");
		defaultComboBoxModel2.addElement("item 4");
		cmbMediatorProfile.setModel(defaultComboBoxModel2);
		pnlMediator.add(cmbMediatorProfile, new GridConstraints(1, 1, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		pnlEnvironment = new JPanel();
		pnlEnvironment.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0,
				0, 0), -1, -1));
		pnlRoot.add(pnlEnvironment, new GridConstraints(0, 0, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
				null, 0, false));
		pnlEnvironment.setBorder(BorderFactory
				.createTitledBorder("Environment"));
		lblProtocol = new JLabel();
		lblProtocol.setText("Protocol");
		pnlEnvironment.add(lblProtocol, new GridConstraints(0, 0, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 16),
				null, 0, false));
		lblDeadlines = new JLabel();
		lblDeadlines.setText("Deadline");
		pnlEnvironment.add(lblDeadlines, new GridConstraints(1, 0, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 16),
				null, 0, false));
		lblNumberOfTournaments = new JLabel();
		lblNumberOfTournaments.setText("Number of Tournaments");
		pnlEnvironment.add(lblNumberOfTournaments, new GridConstraints(2, 0, 1,
				1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 16),
				null, 0, false));
		txtNumberOfTournaments = new JSpinner();
		pnlEnvironment.add(txtNumberOfTournaments, new GridConstraints(2, 1, 1,
				1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_WANT_GROW,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, 24),
				null, 0, false));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0),
				-1, -1));
		pnlEnvironment.add(panel1, new GridConstraints(1, 1, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW, null,
				new Dimension(227, 36), null, 0, false));
		lblDeadlinesValue = new JLabel();
		lblDeadlinesValue.setText("[Time: 300] OR [Rounds: 50]");
		panel1.add(lblDeadlinesValue, new GridConstraints(0, 0, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(180, 16),
				null, 0, false));
		btnDeadlines = new JButton();
		btnDeadlines.setText("…");
		panel1.add(btnDeadlines, new GridConstraints(0, 1, 1, 1,
				GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED, new Dimension(24, 24),
				new Dimension(24, 24), new Dimension(24, 24), 0, false));
		cmbProtocol = new JComboBox();
		final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
		defaultComboBoxModel3.addElement("item 1");
		defaultComboBoxModel3.addElement("item 2");
		defaultComboBoxModel3.addElement("item 3");
		defaultComboBoxModel3.addElement("item 4");
		cmbProtocol.setModel(defaultComboBoxModel3);
		pnlEnvironment.add(cmbProtocol, new GridConstraints(0, 1, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(227, 26),
				null, 0, false));
		lblAgentsPerSession = new JLabel();
		lblAgentsPerSession.setText("Agents per Session");
		pnlEnvironment.add(lblAgentsPerSession, new GridConstraints(3, 0, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 16),
				null, 0, false));
		txtAgentsPerSession = new JSpinner();
		pnlEnvironment.add(txtAgentsPerSession, new GridConstraints(3, 1, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_WANT_GROW,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, 24),
				null, 0, false));
		lblDuplicateAgents = new JLabel();
		lblDuplicateAgents.setText("Agent Repetition Allowed");
		pnlEnvironment.add(lblDuplicateAgents, new GridConstraints(4, 0, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 16),
				null, 0, false));
		chkDuplicateAgents = new JCheckBox();
		chkDuplicateAgents.setText("");
		pnlEnvironment.add(chkDuplicateAgents, new GridConstraints(4, 1, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		pnlAgentsProfiles = new JPanel();
		pnlAgentsProfiles.setLayout(new GridLayoutManager(3, 2, new Insets(0,
				0, 0, 0), -1, -1));
		pnlRoot.add(pnlAgentsProfiles, new GridConstraints(2, 0, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
				null, 0, false));
		pnlAgentsProfiles.setBorder(BorderFactory.createTitledBorder(""));
		pnlAgentButtons = new JPanel();
		pnlAgentButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnlAgentsProfiles.add(pnlAgentButtons, new GridConstraints(2, 0, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
				null, 0, false));
		btnAddAgents = new JButton();
		btnAddAgents.setText("Add");
		pnlAgentButtons.add(btnAddAgents);
		btnRemoveAgents = new JButton();
		btnRemoveAgents.setText("Remove");
		pnlAgentButtons.add(btnRemoveAgents);
		btnClearAgents = new JButton();
		btnClearAgents.setText("Clear");
		pnlAgentButtons.add(btnClearAgents);
		pnlProfileButtons = new JPanel();
		pnlProfileButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnlAgentsProfiles.add(pnlProfileButtons, new GridConstraints(2, 1, 1,
				1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
				null, 0, false));
		btnAddProfiles = new JButton();
		btnAddProfiles.setText("Add");
		pnlProfileButtons.add(btnAddProfiles);
		btnRemoveProfiles = new JButton();
		btnRemoveProfiles.setText("Remove");
		pnlProfileButtons.add(btnRemoveProfiles);
		btnClearProfiles = new JButton();
		btnClearProfiles.setText("Clear");
		pnlProfileButtons.add(btnClearProfiles);
		final JLabel label1 = new JLabel();
		label1.setText("Profiles");
		pnlAgentsProfiles.add(label1, new GridConstraints(0, 1, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Agents");
		pnlAgentsProfiles.add(label2, new GridConstraints(0, 0, 1, 1,
				GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		pnlAgentsProfiles.add(scrollPane1, new GridConstraints(1, 0, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_WANT_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(
						-1, 120), new Dimension(-1, 120), null, 0, false));
		lstAgents = new JList();
		final DefaultListModel defaultListModel1 = new DefaultListModel();
		defaultListModel1.addElement("Agent 1");
		defaultListModel1.addElement("Agent 2");
		defaultListModel1.addElement("Agent 3");
		defaultListModel1.addElement("Agent 4");
		defaultListModel1.addElement("Agent 5");
		defaultListModel1.addElement("Agent 6");
		defaultListModel1.addElement("Agent 7");
		defaultListModel1.addElement("Agent 8");
		defaultListModel1.addElement("Agent 9");
		defaultListModel1.addElement("Agent 10");
		defaultListModel1.addElement("Agent 11");
		defaultListModel1.addElement("Agent 12");
		defaultListModel1.addElement("Agent 13");
		defaultListModel1.addElement("Agent 14");
		lstAgents.setModel(defaultListModel1);
		lstAgents.setSelectionMode(2);
		scrollPane1.setViewportView(lstAgents);
		final JScrollPane scrollPane2 = new JScrollPane();
		pnlAgentsProfiles.add(scrollPane2, new GridConstraints(1, 1, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_WANT_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(
						-1, 120), new Dimension(-1, 120), null, 0, false));
		lstProfiles = new JList();
		final DefaultListModel defaultListModel2 = new DefaultListModel();
		defaultListModel2.addElement("Profile 1");
		defaultListModel2.addElement("Profile 2");
		defaultListModel2.addElement("Profile 3");
		defaultListModel2.addElement("Profile 4");
		defaultListModel2.addElement("Profile 5");
		defaultListModel2.addElement("Profile 6");
		defaultListModel2.addElement("Profile 7");
		defaultListModel2.addElement("Profile 8");
		defaultListModel2.addElement("Profile 9");
		defaultListModel2.addElement("Profile 10");
		defaultListModel2.addElement("Profile 11");
		defaultListModel2.addElement("Profile 12");
		defaultListModel2.addElement("Profile 13");
		defaultListModel2.addElement("Profile 14");
		lstProfiles.setModel(defaultListModel2);
		scrollPane2.setViewportView(lstProfiles);
		pnlStartTournament = new JPanel();
		pnlStartTournament.setLayout(new GridLayoutManager(1, 1, new Insets(0,
				100, 0, 0), -1, -1));
		pnlRoot.add(pnlStartTournament, new GridConstraints(3, 0, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
				null, 0, false));
		btnStartTournament = new JButton();
		btnStartTournament.setText("Start Tournament");
		pnlStartTournament.add(btnStartTournament, new GridConstraints(0, 0, 1,
				1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return pnlRoot;
	}

	/**
	 * Renderer for class-internal usage
	 */
	private class RepItemComboRenderer extends BasicComboBoxRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			// get default value for item
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			// update text with name value
			if (value instanceof ProfileRepItem)
				setText(getShortPath((ProfileRepItem) value));
			else if (value instanceof RepItem)
				setText(((RepItem) value).getName());

			// return label
			return this;
		}
	}

	// private class
	private class RepItemListRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			if (value instanceof ProfileRepItem)
				setText(getShortPath((ProfileRepItem) value));
			else if (value instanceof RepItem)
				setText(((RepItem) value).getName());

			return this;
		}
	}

	//
	// // private class
	// private class ParticipantsTableModel extends DefaultTableModel
	// {
	// ParticipantsTableModel(GuiConfiguration config)
	// {
	// super(getParticipantTableData(), new Object[]{"Id", "Agent", "Profile"});
	// }
	//
	// @Override
	// public Object getValueAt(int row, int column)
	// {
	// Object value = super.getValueAt(row, column);
	// if (value instanceof PartyRepItem)
	// {
	// return ((PartyRepItem) value).getName();
	// }
	// else if (value instanceof ProfileRepItem)
	// {
	// return getShortPath((ProfileRepItem) value);
	// }
	// // default case
	// else return value;
	// }
	// }

	// private class
	private class ListModel<T> extends AbstractListModel {
		final List<T> model;

		ListModel() {
			this.model = new ArrayList<T>();
		}

		ListModel(List<T> model) {
			this.model = model;
		}

		/**
		 * Returns the length of the list.
		 *
		 * @return the length of the list
		 */
		@Override
		public int getSize() {
			return model.size();
		}

		/**
		 * Returns the value at the specified index.
		 *
		 * @param index
		 *            the requested index
		 * @return the value at <code>index</code>
		 */
		@Override
		public T getElementAt(int index) {
			return model.get(index);
		}
	}
}
