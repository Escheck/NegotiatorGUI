/*
 *
 * Created on September 3, 2008, 3:36 PM
 */

package negotiator.gui.negosession;

import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.gui.NegoGUIApp;
import negotiator.gui.progress.MultipartyProgressUI;
import negotiator.logging.FileLogger;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.Protocol;
import negotiator.repository.DomainRepItem;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.session.ExecutorWithTimeout;
import negotiator.session.Session;
import negotiator.session.SessionManager;
import negotiator.tournament.TournamentGenerator;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

import org.jdesktop.application.Action;

/**
 *
 * @author Reyhan
 */

public class MultiNegoSessionUI extends javax.swing.JPanel {

	private static final boolean fShowProgressUI = true;
	public static int PROTOCOL_NO = 0;
	private MultiPartyProtocolRepItem selectedMultiPartyProtocolRepItem;
	private ArrayList<PartyRepItem> selectedPartyRepItems;
	private ArrayList<AgentID> selectedPartyIDList;
	private ArrayList<ProfileRepItem> selectedProfileRepItems;
	private ArrayList<HashMap<AgentParameterVariable, AgentParamValue>> selectedPartyParams;
	// Variables declaration - do not modify
	private javax.swing.JButton btnAddMediator;
	private javax.swing.JButton btnAddParty;
	private javax.swing.JButton btnParamsEachParty;
	private javax.swing.JButton btnParamsMediator;
	private javax.swing.JButton btnRemoveParty;
	private javax.swing.JButton btnstartMultiSession;
	private javax.swing.JComboBox cmbMediatorPref;
	private javax.swing.JComboBox cmbMediatorStr;
	private javax.swing.JComboBox cmbPartyStr;
	private javax.swing.JComboBox cmbPrefParty;
	private javax.swing.JComboBox cmbProtocol;
	private javax.swing.JLabel jLabelMedID;
	private javax.swing.JLabel jLabelMedParam;
	private javax.swing.JLabel jLabelMedPref;
	private javax.swing.JLabel jLabelMedStr;
	private javax.swing.JLabel jLabelPartyId;
	private javax.swing.JLabel jLabelPartyParam;
	private javax.swing.JLabel jLabelPartyPref;
	private javax.swing.JLabel jLabelPartyStr;
	private javax.swing.JLabel jLabelProtocol;
	private javax.swing.JLabel jLabelMaxTimeOrRound;
	private javax.swing.JLabel jLabelDeadlineType;
	private javax.swing.JPanel jPanelMain;
	private javax.swing.JPanel jPanelMediator;
	private javax.swing.JPanel jPanelParticipant;
	private javax.swing.JPanel jPanelTimeOut;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTableParty;
	private javax.swing.JTextField txtMaxTimeOrRound;
	private javax.swing.JTextField txtMediatorID;
	private javax.swing.JTextField txtMedParams;
	private javax.swing.JTextField txtDeadlineType;
	private javax.swing.JTextField txtPartyID;
	private javax.swing.JTextField txtPartyParams;

	/** Creates new form MultiNegoSessionUI */
	public MultiNegoSessionUI() {
		PROTOCOL_NO++;
		initComponents();
		try {
			initValues();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void initSelectedVariables() {

		selectedPartyIDList = new ArrayList<AgentID>();
		selectedPartyParams = new ArrayList<HashMap<AgentParameterVariable, AgentParamValue>>();
		selectedPartyRepItems = new ArrayList<PartyRepItem>();
		selectedProfileRepItems = new ArrayList<ProfileRepItem>();
		DefaultTableModel tbm = (DefaultTableModel) jTableParty.getModel();
		int count = jTableParty.getRowCount();

		for (int i = 0; i < count; i++)
			tbm.removeRow(0);

		jTableParty.setModel(tbm);
	}

	private void initValues() throws Exception {

		initSelectedVariables();

		// Parameter part will be active later; for now we hide the component
		// related to the parameters.
		jLabelMedParam.setVisible(false);
		txtMedParams.setVisible(false);
		btnParamsMediator.setVisible(false);

		jLabelPartyParam.setVisible(false);
		txtPartyParams.setVisible(false);
		btnParamsEachParty.setVisible(false);

		btnAddMediator.setVisible(false);
		txtPartyID.setText("Party 1");
		/* ************************************* */

		Repository protocolRep = Repository.getMultiPartyProtocolRepository();
		cmbProtocol.removeAllItems();
		for (RepItem protocol : protocolRep.getItems()) {
			cmbProtocol.addItem(new MultiPartyProtocolComboBoxItem(
					(MultiPartyProtocolRepItem) protocol));
		}

		cmbMediatorStr.removeAllItems();
		cmbPartyStr.removeAllItems();

		Repository party_rep = Repository.get_party_repository();

		selectedMultiPartyProtocolRepItem = ((MultiPartyProtocolComboBoxItem) cmbProtocol
				.getSelectedItem()).multiPartyProtocol;

		for (RepItem prt : party_rep.getItems()) {
			String partyPath = ((PartyRepItem) prt).getProtocolClassPath();
			String protocolPath = selectedMultiPartyProtocolRepItem
					.getClassPath();
			if (!partyPath.equals(protocolPath))
				continue;

			if (((PartyRepItem) prt).getIsMediator())
				cmbMediatorStr
						.addItem(new PartyComboBoxItem((PartyRepItem) prt));
			else
				cmbPartyStr.addItem(new PartyComboBoxItem((PartyRepItem) prt));
		}

		cmbMediatorPref.removeAllItems();
		cmbPrefParty.removeAllItems();

		for (RepItem prof : getProfiles()) {
			cmbMediatorPref.addItem(new ProfileComboBoxItem(
					(ProfileRepItem) prof));
			cmbPrefParty
					.addItem(new ProfileComboBoxItem((ProfileRepItem) prof));
		}

		if (!selectedMultiPartyProtocolRepItem.getHasMediatorProfile()) {
			cmbMediatorPref.setVisible(false);
			jLabelMedPref.setVisible(false);
		}

		if (selectedMultiPartyProtocolRepItem.getHasMediator() == false)
			jPanelMediator.setVisible(false);

		cmbProtocol.addActionListener(new ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent e) {

				System.out.println("Protocol is selected");

				selectedMultiPartyProtocolRepItem = ((MultiPartyProtocolComboBoxItem) cmbProtocol
						.getSelectedItem()).multiPartyProtocol;

				if (selectedMultiPartyProtocolRepItem.getHasMediator() == false)
					jPanelMediator.setVisible(false);
				else
					jPanelMediator.setVisible(true);

				if (!selectedMultiPartyProtocolRepItem.getHasMediatorProfile()) {

					cmbMediatorPref.setVisible(false);
					jLabelMedPref.setVisible(false);
				} else {
					cmbMediatorPref.setVisible(true);
					jLabelMedPref.setVisible(true);
				}

				if (!btnAddMediator.isEnabled()) {
					txtMediatorID.setEnabled(true);
					cmbMediatorStr.setEnabled(true);
					cmbMediatorPref.setEnabled(true);
					// btnAddMediator.setEnabled(true);
				}

				initSelectedVariables();

				cmbPartyStr.removeAllItems();
				cmbMediatorStr.removeAllItems();
				txtPartyID.setText("Party 1");
				cmbPrefParty.setSelectedIndex(0);
				Repository party_rep = Repository.get_party_repository();

				for (RepItem prt : party_rep.getItems()) {
					String partyPath = ((PartyRepItem) prt)
							.getProtocolClassPath();
					String protocolPath = selectedMultiPartyProtocolRepItem
							.getClassPath();
					if (!partyPath.equals(protocolPath))
						continue;

					if (((PartyRepItem) prt).getIsMediator())
						cmbMediatorStr.addItem(new PartyComboBoxItem(
								(PartyRepItem) prt));
					else
						cmbPartyStr.addItem(new PartyComboBoxItem(
								(PartyRepItem) prt));
				}

			}
		});

	}

	private List<NegotiationParty> parties;

	/**
	 * This is a proxy class to bridge this UI to the new SessionManager
	 * structure. It fetches all the data in this class and then generates a new
	 * session manager out of it.
	 *
	 * @return The session manager that can run the tournament
	 * @author David Festen
	 */
	private SessionManager generateSessionManager() throws Exception {
		/*
		 * We need a list of agents, a protocol and a session to start. We'll
		 * generate them in that order, so list of agents (parties), protocol,
		 * session. Any exception we'll throw so that the calling class can show
		 * them on the screen
		 */

		// we need to pre-generate the deadline map instance (we need it
		// multiple times)
		int rounds = 0, time = 0;
		if (txtDeadlineType.getText().toUpperCase().contains("ROUND")) {
			rounds = Integer.parseInt(txtMaxTimeOrRound.getText());
		} else if (txtDeadlineType.getText().toUpperCase().contains("TIME")) {
			time = Integer.parseInt(txtMaxTimeOrRound.getText());
		}

		Deadline deadline = new Deadline(time, rounds);

		// First we generate the session instance
		Session session = new Session(deadline);

		// Second the list of agent
		if (selectedPartyRepItems.get(0).getIsMediator()) {
			PartyRepItem mediator = selectedPartyRepItems.get(0);
			List<PartyRepItem> agents = selectedPartyRepItems.subList(1,
					selectedPartyRepItems.size());

			ProfileRepItem mediatorProfile = null;
			List<ProfileRepItem> agentProfiles = selectedProfileRepItems;

			if (selectedProfileRepItems.get(0).getDomain() != null)
				mediatorProfile = selectedProfileRepItems.get(0);
			agentProfiles = selectedProfileRepItems.subList(1,
					selectedPartyRepItems.size());

			parties = TournamentGenerator.generateSessionParties(agents,
					agentProfiles, selectedPartyIDList, 0, mediator,
					mediatorProfile, session);
		} else {
			parties = TournamentGenerator.generateSessionParties(
					selectedPartyRepItems, selectedProfileRepItems,
					selectedPartyIDList, 0, null, null, session);
		}

		// Finally we generate the protocol instance
		Protocol protocol = TournamentGenerator
				.createFrom(selectedMultiPartyProtocolRepItem);

		// Now that we have them all, we can generate and return the
		// SessionManager instance.
		return new SessionManager(parties, protocol, session,
				new ExecutorWithTimeout(
						1000 * deadline.getTimeOrDefaultTimeout()));
	}

	/** TODO use the parameters. */
	public void start() throws Exception {

		if (jTableParty.getRowCount() < 2) {
			JOptionPane.showMessageDialog(null,
					"There should be at least two negotiating agent !",
					"Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if ((!txtDeadlineType.getText().trim().equals("Time"))
				&& (!txtDeadlineType.getText().trim().equals("Round"))) {
			JOptionPane.showMessageDialog(null,
					"Deadline type can be either Time or Round !", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		int maxRoundOrTime;

		try {
			maxRoundOrTime = Integer.parseInt(txtMaxTimeOrRound.getText()
					.trim());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Max duration should be an integer !", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		for (int i = 0; i < jTableParty.getRowCount(); i++) {

			selectedPartyIDList.add((AgentID) jTableParty.getValueAt(i, 0));
			selectedPartyRepItems.add((PartyRepItem) jTableParty.getValueAt(i,
					1));
			selectedProfileRepItems.add((ProfileRepItem) jTableParty
					.getValueAt(i, 2));
			selectedPartyParams
					.add(new HashMap<AgentParameterVariable, AgentParamValue>());
		}

		// Preparing the table column names for progress GUI
		ArrayList<String> progressInfoList = new ArrayList<String>();

		if (!selectedMultiPartyProtocolRepItem.getHasMediator())
			progressInfoList.add(selectedPartyIDList.get(0).toString());

		for (int i = 1; i < this.selectedPartyIDList.size(); i++) {
			progressInfoList.add(this.selectedPartyIDList.get(i).toString());
		}

		SessionManager sessionManager = generateSessionManager();

		MultipartyProgressUI graphlistener = null;
		if (fShowProgressUI) {
			graphlistener = new MultipartyProgressUI(progressInfoList,
					sessionManager, parties);
			NegoGUIApp.negoGUIView.replaceTab("Progress-" + PROTOCOL_NO, this,
					graphlistener);
			sessionManager.addLoggingListener(graphlistener);
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String fileName = String.format("Log-Session_%s.csv",
				dateFormat.format(new Date()));
		FileLogger fileLogger = new FileLogger(fileName);
		sessionManager.addLoggingListener(fileLogger);

		System.out.println("Negotiation session has started.");
		sessionManager.run();

		/*
		 * // determine the domain DomainRepItem
		 * domain=agentProfiles[0].getDomain(); if
		 * (domain!=agentProfiles[1].getDomain()) throw new
		 * IllegalArgumentException(
		 * "profiles for agent A and B do not have the same domain. Please correct your profiles"
		 * );
		 * 
		 * Check doma�ns are the same !
		 * 
		 * if(fShowProgressUI) {
		 * NegoGUIApp.negoGUIView.replaceTab("Sess."+ns.getSessionNumber
		 * ()+" Prog.", this, graphlistener);
		 * protocol.addNegotiationEventListener(graphlistener); }
		 */
	}

	public ArrayList<ProfileRepItem> getProfiles() throws Exception {
		Repository domainrep = Repository.get_domain_repos();
		ArrayList<ProfileRepItem> profiles = new ArrayList<ProfileRepItem>();
		for (RepItem domain : domainrep.getItems()) {
			if (!(domain instanceof DomainRepItem))
				throw new IllegalStateException(
						"Found a non-DomainRepItem in domain repository:"
								+ domain);
			for (ProfileRepItem profile : ((DomainRepItem) domain)
					.getProfiles())
				profiles.add(profile);
		}
		return profiles;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jPanelMain = new javax.swing.JPanel();
		jLabelProtocol = new javax.swing.JLabel();
		jPanelMediator = new javax.swing.JPanel();
		jLabelMedPref = new javax.swing.JLabel();
		jLabelMedStr = new javax.swing.JLabel();
		jLabelMedParam = new javax.swing.JLabel();
		cmbMediatorPref = new javax.swing.JComboBox();
		txtMedParams = new javax.swing.JTextField();
		btnParamsMediator = new javax.swing.JButton();
		txtMediatorID = new javax.swing.JTextField();
		jLabelMedID = new javax.swing.JLabel();
		cmbMediatorStr = new javax.swing.JComboBox();
		btnAddMediator = new javax.swing.JButton();
		cmbProtocol = new javax.swing.JComboBox();
		btnstartMultiSession = new javax.swing.JButton();
		jPanelParticipant = new javax.swing.JPanel();
		jLabelPartyPref = new javax.swing.JLabel();
		jLabelPartyStr = new javax.swing.JLabel();
		jLabelPartyParam = new javax.swing.JLabel();
		cmbPrefParty = new javax.swing.JComboBox();
		cmbPartyStr = new javax.swing.JComboBox();
		txtPartyParams = new javax.swing.JTextField();
		btnParamsEachParty = new javax.swing.JButton();
		txtPartyID = new javax.swing.JTextField();
		jLabelPartyId = new javax.swing.JLabel();
		btnAddParty = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTableParty = new javax.swing.JTable();
		btnRemoveParty = new javax.swing.JButton();
		jPanelTimeOut = new javax.swing.JPanel();
		jLabelDeadlineType = new javax.swing.JLabel();
		txtDeadlineType = new javax.swing.JTextField();
		jLabelMaxTimeOrRound = new javax.swing.JLabel();
		txtMaxTimeOrRound = new javax.swing.JTextField();

		setName("Form"); // NOI18N

		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
				.getInstance().getContext()
				.getResourceMap(MultiNegoSessionUI.class);
		jPanelMain.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanelMain.border.title"))); // NOI18N
		jPanelMain.setName("jPanelMain"); // NOI18N

		jLabelProtocol.setText(resourceMap.getString("jLabelProtocol.text")); // NOI18N
		jLabelProtocol.setName("jLabelProtocol"); // NOI18N

		jPanelMediator.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanelMediator.border.title"))); // NOI18N
		jPanelMediator.setName("jPanelMediator"); // NOI18N

		jLabelMedPref.setText(resourceMap.getString("jLabelMedPref.text")); // NOI18N
		jLabelMedPref.setName("jLabelMedPref"); // NOI18N

		jLabelMedStr.setText(resourceMap.getString("lblMedStrategy.text")); // NOI18N
		jLabelMedStr.setName("lblMedStrategy"); // NOI18N

		jLabelMedParam.setText(resourceMap.getString("jLabelMedParam.text")); // NOI18N
		jLabelMedParam.setName("jLabelMedParam"); // NOI18N

		cmbMediatorPref.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		javax.swing.ActionMap actionMap = org.jdesktop.application.Application
				.getInstance().getContext()
				.getActionMap(MultiNegoSessionUI.class, this);
		cmbMediatorPref.setName("cmbMedProfile"); // NOI18N

		txtMedParams.setEditable(false);
		txtMedParams.setText(resourceMap.getString("txtParamsMediator.text")); // NOI18N
		txtMedParams.setName("txtParamsMediator"); // NOI18N

		btnParamsMediator.setAction(actionMap.get("popupMediatorParams")); // NOI18N
		btnParamsMediator.setText(resourceMap.getString("btnMedParams.text")); // NOI18N
		btnParamsMediator.setName("btnMedParams"); // NOI18N

		txtMediatorID.setText(resourceMap.getString("txtMediatorID.text")); // NOI18N
		txtMediatorID.setName("txtMediatorID"); // NOI18N

		jLabelMedID.setText(resourceMap.getString("lblmediatorID.text")); // NOI18N
		jLabelMedID.setName("lblmediatorID"); // NOI18N

		cmbMediatorStr.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		cmbMediatorStr.setName("cmbMedStrategy"); // NOI18N

		btnAddMediator.setAction(actionMap.get("addMediator")); // NOI18N
		btnAddMediator.setText(resourceMap.getString("btnAddMediator.text")); // NOI18N
		btnAddMediator.setName("btnAddMediator"); // NOI18N

		org.jdesktop.layout.GroupLayout jPanelMediatorLayout = new org.jdesktop.layout.GroupLayout(
				jPanelMediator);
		jPanelMediator.setLayout(jPanelMediatorLayout);
		jPanelMediatorLayout
				.setHorizontalGroup(jPanelMediatorLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(org.jdesktop.layout.GroupLayout.TRAILING,
								jPanelMediatorLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(jPanelMediatorLayout
												.createParallelGroup(
														org.jdesktop.layout.GroupLayout.TRAILING)
												.add(btnAddMediator,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														110,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.add(org.jdesktop.layout.GroupLayout.LEADING,
														jPanelMediatorLayout
																.createSequentialGroup()
																.add(jPanelMediatorLayout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.LEADING)
																		.add(jLabelMedID)
																		.add(jLabelMedStr))
																.add(14, 14, 14)
																.add(jPanelMediatorLayout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.TRAILING)
																		.add(txtMediatorID,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				481,
																				Short.MAX_VALUE)
																		.add(org.jdesktop.layout.GroupLayout.LEADING,
																				cmbMediatorStr,
																				0,
																				481,
																				Short.MAX_VALUE)))
												.add(org.jdesktop.layout.GroupLayout.LEADING,
														jPanelMediatorLayout
																.createSequentialGroup()
																.add(jPanelMediatorLayout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.LEADING)
																		.add(jLabelMedParam)
																		.add(jLabelMedPref))
																.add(10, 10, 10)
																.add(jPanelMediatorLayout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.LEADING)
																		.add(jPanelMediatorLayout
																				.createSequentialGroup()
																				.add(txtMedParams,
																						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																						446,
																						Short.MAX_VALUE)
																				.addPreferredGap(
																						org.jdesktop.layout.LayoutStyle.UNRELATED)
																				.add(btnParamsMediator,
																						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																						25,
																						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																		.add(cmbMediatorPref,
																				0,
																				481,
																				Short.MAX_VALUE))))
										.addContainerGap()));
		jPanelMediatorLayout
				.setVerticalGroup(jPanelMediatorLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanelMediatorLayout
								.createSequentialGroup()
								.add(jPanelMediatorLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabelMedID)
										.add(txtMediatorID,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelMediatorLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(cmbMediatorStr,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(jLabelMedStr))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelMediatorLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(cmbMediatorPref,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(jLabelMedPref))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelMediatorLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jPanelMediatorLayout
												.createParallelGroup(
														org.jdesktop.layout.GroupLayout.BASELINE)
												.add(btnParamsMediator)
												.add(txtMedParams,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.add(jPanelMediatorLayout
												.createSequentialGroup()
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED,
														3,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.add(jLabelMedParam)))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(btnAddMediator,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										25, Short.MAX_VALUE)));

		cmbMediatorPref
				.getAccessibleContext()
				.setAccessibleName(
						resourceMap
								.getString("cmbMedProfile.AccessibleContext.accessibleName")); // NOI18N
		btnParamsMediator
				.getAccessibleContext()
				.setAccessibleName(
						resourceMap
								.getString("btnMedParams.AccessibleContext.accessibleName")); // NOI18N
		txtMediatorID
				.getAccessibleContext()
				.setAccessibleName(
						resourceMap
								.getString("txtMediatorID.AccessibleContext.accessibleName")); // NOI18N
		jLabelMedID
				.getAccessibleContext()
				.setAccessibleName(
						resourceMap
								.getString("lblmediatorID.AccessibleContext.accessibleName")); // NOI18N
		cmbMediatorStr
				.getAccessibleContext()
				.setAccessibleName(
						resourceMap
								.getString("cmbMedStrategy.AccessibleContext.accessibleName")); // NOI18N

		cmbProtocol.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Multi Party Protocol" }));
		cmbProtocol.setName("cmbProtocol"); // NOI18N

		btnstartMultiSession.setAction(actionMap.get("startMultiSession")); // NOI18N
		btnstartMultiSession.setFont(resourceMap
				.getFont("btnstartMultiSession.font")); // NOI18N
		btnstartMultiSession.setText(resourceMap
				.getString("btnstartMultiSession.text")); // NOI18N
		btnstartMultiSession.setName("btnstartMultiSession"); // NOI18N

		jPanelParticipant.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanelParticipant.border.title"))); // NOI18N
		jPanelParticipant.setName("jPanelParticipant"); // NOI18N

		jLabelPartyPref.setText(resourceMap.getString("jLabelPartyPref.text")); // NOI18N
		jLabelPartyPref.setName("jLabelPartyPref"); // NOI18N

		jLabelPartyStr.setText(resourceMap.getString("jLabelPartyStr.text")); // NOI18N
		jLabelPartyStr.setName("jLabelPartyStr"); // NOI18N

		jLabelPartyParam
				.setText(resourceMap.getString("jLabelPartyParam.text")); // NOI18N
		jLabelPartyParam.setName("jLabelPartyParam"); // NOI18N

		cmbPrefParty.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		cmbPrefParty.setName("cmbPrefParty"); // NOI18N

		cmbPartyStr.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"Item 1", "Item 2", "Item 3", "Item 4" }));
		cmbPartyStr.setName("cmbParty"); // NOI18N

		txtPartyParams.setEditable(false);
		txtPartyParams.setText(resourceMap.getString("txtPartyParams.text")); // NOI18N
		txtPartyParams.setName("txtPartyParams"); // NOI18N

		btnParamsEachParty.setAction(actionMap.get("popupPartyParams")); // NOI18N
		btnParamsEachParty.setText(resourceMap
				.getString("btnParamsEachParty.text")); // NOI18N
		btnParamsEachParty.setName("btnParamsEachParty"); // NOI18N

		txtPartyID.setName("txtPartyID"); // NOI18N
		txtPartyID.setText(resourceMap.getString("txtPartyID.text")); // NOI18N

		jLabelPartyId.setText(resourceMap.getString("jLabelPartyId.text")); // NOI18N
		jLabelPartyId.setName("jLabelPartyId"); // NOI18N

		btnAddParty.setAction(actionMap.get("addParty")); // NOI18N
		btnAddParty.setText(resourceMap.getString("btnAddParty.text")); // NOI18N
		btnAddParty.setActionCommand(resourceMap
				.getString("btnAddParty.actionCommand")); // NOI18N
		btnAddParty.setName("btnAddParty"); // NOI18N

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		jTableParty.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] {

				}, new String[] { "Party ID", "Strategy", "Preference" }) {
			boolean[] canEdit = new boolean[] { false, false, false };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		jTableParty.setName("jTableParty"); // NOI18N
		jScrollPane1.setViewportView(jTableParty);

		btnRemoveParty.setAction(actionMap.get("removeParty")); // NOI18N
		btnRemoveParty.setText(resourceMap.getString("btnRemoveParty.text")); // NOI18N
		btnRemoveParty.setName("btnRemoveParty"); // NOI18N

		org.jdesktop.layout.GroupLayout jPanelParticipantLayout = new org.jdesktop.layout.GroupLayout(
				jPanelParticipant);
		jPanelParticipant.setLayout(jPanelParticipantLayout);
		jPanelParticipantLayout
				.setHorizontalGroup(jPanelParticipantLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(org.jdesktop.layout.GroupLayout.TRAILING,
								jPanelParticipantLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(jPanelParticipantLayout
												.createParallelGroup(
														org.jdesktop.layout.GroupLayout.TRAILING)
												.add(jPanelParticipantLayout
														.createSequentialGroup()
														.add(jScrollPane1,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED,
																22,
																Short.MAX_VALUE)
														.add(jPanelParticipantLayout
																.createParallelGroup(
																		org.jdesktop.layout.GroupLayout.TRAILING)
																.add(btnAddParty,
																		org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																		108,
																		org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																.add(btnRemoveParty,
																		org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																		108,
																		org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
												.add(jPanelParticipantLayout
														.createSequentialGroup()
														.add(jPanelParticipantLayout
																.createParallelGroup(
																		org.jdesktop.layout.GroupLayout.LEADING)
																.add(jLabelPartyId)
																.add(jLabelPartyPref)
																.add(jLabelPartyParam)
																.add(jLabelPartyStr))
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED)
														.add(jPanelParticipantLayout
																.createParallelGroup(
																		org.jdesktop.layout.GroupLayout.TRAILING)
																.add(org.jdesktop.layout.GroupLayout.LEADING,
																		txtPartyID,
																		org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		487,
																		Short.MAX_VALUE)
																.add(org.jdesktop.layout.GroupLayout.LEADING,
																		cmbPrefParty,
																		0,
																		487,
																		Short.MAX_VALUE)
																.add(cmbPartyStr,
																		0,
																		487,
																		Short.MAX_VALUE)
																.add(jPanelParticipantLayout
																		.createSequentialGroup()
																		.add(txtPartyParams,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				452,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.UNRELATED)
																		.add(btnParamsEachParty,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				25,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
										.addContainerGap()));
		jPanelParticipantLayout
				.setVerticalGroup(jPanelParticipantLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanelParticipantLayout
								.createSequentialGroup()
								.add(jPanelParticipantLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabelPartyId)
										.add(txtPartyID,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelParticipantLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(cmbPartyStr,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(jLabelPartyStr,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												17,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelParticipantLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(cmbPrefParty,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(jLabelPartyPref))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelParticipantLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jPanelParticipantLayout
												.createParallelGroup(
														org.jdesktop.layout.GroupLayout.BASELINE)
												.add(btnParamsEachParty)
												.add(txtPartyParams,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.add(jLabelPartyParam))
								.add(18, 18, 18)
								.add(jPanelParticipantLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jPanelParticipantLayout
												.createSequentialGroup()
												.add(btnAddParty)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED,
														48, Short.MAX_VALUE)
												.add(btnRemoveParty))
										.add(org.jdesktop.layout.GroupLayout.TRAILING,
												jScrollPane1,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												94, Short.MAX_VALUE))
								.addContainerGap()));

		jPanelTimeOut.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanelTimeOut.border.title"))); // NOI18N
		jPanelTimeOut.setName("jPanelTimeOut"); // NOI18N

		jLabelDeadlineType.setText(resourceMap
				.getString("jLabelDeadlineType.text")); // NOI18N
		jLabelDeadlineType.setName("jLabelDeadlineType"); // NOI18N

		txtDeadlineType.setText(resourceMap.getString("txtDeadlineType.text")); // NOI18N
		txtDeadlineType.setName("txtDeadlineType"); // NOI18N

		jLabelMaxTimeOrRound.setText(resourceMap
				.getString("jLabelMaxTimeOrRound.text")); // NOI18N
		jLabelMaxTimeOrRound.setName("jLabelMaxTimeOrRound"); // NOI18N

		txtMaxTimeOrRound.setText(resourceMap
				.getString("txtMaxTimeOrRound.text")); // NOI18N
		txtMaxTimeOrRound.setName("txtMaxTimeOrRound"); // NOI18N

		org.jdesktop.layout.GroupLayout jPanelTimeOutLayout = new org.jdesktop.layout.GroupLayout(
				jPanelTimeOut);
		jPanelTimeOut.setLayout(jPanelTimeOutLayout);
		jPanelTimeOutLayout
				.setHorizontalGroup(jPanelTimeOutLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanelTimeOutLayout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanelTimeOutLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jLabelDeadlineType)
										.add(jLabelMaxTimeOrRound))
								.add(14, 14, 14)
								.add(jPanelTimeOutLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(txtMaxTimeOrRound,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												493, Short.MAX_VALUE)
										.add(txtDeadlineType,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												493, Short.MAX_VALUE))
								.addContainerGap()));
		jPanelTimeOutLayout
				.setVerticalGroup(jPanelTimeOutLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanelTimeOutLayout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanelTimeOutLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabelDeadlineType)
										.add(txtDeadlineType,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED)
								.add(jPanelTimeOutLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabelMaxTimeOrRound)
										.add(txtMaxTimeOrRound,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(19, Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout jPanelMainLayout = new org.jdesktop.layout.GroupLayout(
				jPanelMain);
		jPanelMain.setLayout(jPanelMainLayout);
		jPanelMainLayout
				.setHorizontalGroup(jPanelMainLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanelMainLayout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanelMainLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jPanelMainLayout
												.createParallelGroup(
														org.jdesktop.layout.GroupLayout.LEADING)
												.add(jPanelMainLayout
														.createSequentialGroup()
														.add(jPanelParticipant,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addContainerGap())
												.add(jPanelMainLayout
														.createSequentialGroup()
														.add(jLabelProtocol)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.UNRELATED)
														.add(cmbProtocol, 0,
																488,
																Short.MAX_VALUE)
														.add(29, 29, 29))
												.add(jPanelMainLayout
														.createSequentialGroup()
														.add(jPanelMediator,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addContainerGap())
												.add(org.jdesktop.layout.GroupLayout.TRAILING,
														jPanelMainLayout
																.createSequentialGroup()
																.add(jPanelTimeOut,
																		org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addContainerGap()))
										.add(org.jdesktop.layout.GroupLayout.TRAILING,
												jPanelMainLayout
														.createSequentialGroup()
														.add(btnstartMultiSession,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																75,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(47, 47, 47)))));
		jPanelMainLayout
				.setVerticalGroup(jPanelMainLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanelMainLayout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanelMainLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabelProtocol)
										.add(cmbProtocol,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.add(4, 4, 4)
								.add(jPanelMediator,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelParticipant,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelTimeOut,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED)
								.add(btnstartMultiSession)
								.addContainerGap(
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup()
						.addContainerGap()
						.add(jPanelMain,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup()
						.addContainerGap()
						.add(jPanelMain,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
	}// </editor-fold>

	@Action
	public void popupMediatorParams() {
		try {
			System.out.println("Enter mediator parameters");
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Action
	public void addMediator() {
		if (cmbMediatorStr.getItemCount() == 0)
			return;
		try {
			System.out.println("The mediator ID is checked");

			String mediatorID = txtMediatorID.getText().trim();

			if (mediatorID.length() == 0) {// empty mediator id
				JOptionPane.showMessageDialog(null,
						"The mediator id cannot be empty !", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			for (int i = 0; i < jTableParty.getRowCount(); i++) { // check the
																	// mediator
																	// id is
																	// unique
				if (mediatorID.equals(((AgentID) jTableParty.getValueAt(i, 0))
						.toString())) {
					JOptionPane.showMessageDialog(null,
							"The mediator id should be unique !", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			}

			selectedPartyIDList.add(new AgentID(mediatorID));
			selectedPartyRepItems.add(((PartyComboBoxItem) cmbMediatorStr
					.getSelectedItem()).party);

			if (((MultiPartyProtocolComboBoxItem) cmbProtocol.getSelectedItem()).multiPartyProtocol
					.getHasMediatorProfile())
				selectedProfileRepItems
						.add(((ProfileComboBoxItem) cmbMediatorPref
								.getSelectedItem()).profile);
			else
				selectedProfileRepItems.add(new ProfileRepItem());

			selectedPartyParams
					.add(new HashMap<AgentParameterVariable, AgentParamValue>());

			System.out.println("The mediator is added");
			txtMediatorID.setEnabled(false);
			cmbMediatorStr.setEnabled(false);
			cmbMediatorPref.setEnabled(false);
			btnAddMediator.setEnabled(false);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Action
	public void popupPartyParams() {
		try {
			System.out.println("Check whether the id is unique");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Action
	public void addParty() {
		try {

			String partyID = txtPartyID.getText().trim();

			if ((partyID.length() == 0)
					|| ((selectedPartyIDList.size() > 0) && (((AgentID) selectedPartyIDList
							.get(0)).toString().equals(partyID)))) {// empty
																	// party id
																	// or equal
																	// to
																	// mediator
																	// id
				JOptionPane
						.showMessageDialog(
								null,
								"The mediator id cannot be empty or equal to mediator id!",
								"Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// check the party id is unique

			for (int i = 0; i < jTableParty.getRowCount(); i++) {
				if (partyID.equals(((AgentID) jTableParty.getValueAt(i, 0))
						.toString())) {
					JOptionPane.showMessageDialog(null,
							"The party id should be unique !", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			}

			DefaultTableModel partyModel = (DefaultTableModel) jTableParty
					.getModel();
			Object[] currentPartyObject = new Object[3];

			currentPartyObject[0] = new AgentID(partyID);
			currentPartyObject[1] = ((PartyComboBoxItem) cmbPartyStr
					.getSelectedItem()).party;
			currentPartyObject[2] = ((ProfileComboBoxItem) cmbPrefParty
					.getSelectedItem()).profile;

			partyModel.addRow(currentPartyObject);

			jTableParty.setModel(partyModel);
			txtPartyID.setText("Party " + (jTableParty.getRowCount() + 1));
			cmbPrefParty.setSelectedIndex(cmbPrefParty.getSelectedIndex() + 1);

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	@Action
	public void removeParty() {
		try {
			int i_selected_index = jTableParty.getSelectedRow();
			if (i_selected_index < 0)
				return;
			DefaultTableModel tbm = (DefaultTableModel) jTableParty.getModel();
			tbm.removeRow(i_selected_index);
			jTableParty.setModel(tbm);

			System.out.println("The selected party is removed.");
		} catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();

		}
	}

	@Action
	public void startMultiSession() {
		try {
			addMediator();
			start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * this is to override the toString of a PartyRepItem, to show only the
	 * short name.
	 */
	class PartyComboBoxItem {
		public PartyRepItem party;

		public PartyComboBoxItem(PartyRepItem p) {
			party = p;
		}

		public String toString() {
			return party.getName();
		}
	}

	/**
	 * this is to override the toString of a MultiPartyProtocolRepItem, to show
	 * only the short name.
	 */
	class MultiPartyProtocolComboBoxItem {
		public MultiPartyProtocolRepItem multiPartyProtocol;

		public MultiPartyProtocolComboBoxItem(MultiPartyProtocolRepItem mp) {
			multiPartyProtocol = mp;
		}

		public String toString() {
			return multiPartyProtocol.getName();
		}
	}
}