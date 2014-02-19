/*
 * NegoSessionUI2.java
 *
 * Created on September 3, 2008, 3:36 PM
 */

package negotiator.gui.negosession;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import negotiator.Global;
import negotiator.gui.NegoGUIApp;
import negotiator.gui.progress.ProgressUI2;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.tournament.TournamentConfiguration;
import negotiator.utility.UTILITYSPACETYPE;

import org.jdesktop.application.Action;

/**
 * 
 * @author dmytro
 */
public class NegoSessionUI2 extends javax.swing.JPanel {

	private static final long serialVersionUID = 2692787665652874858L;
	private static final boolean fShowProgressUI = true;

	/** Creates new form NegoSessionUI2 */
	public NegoSessionUI2() {
		initComponents();
		try {
			initValues();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initValues() throws Exception {
		ArrayList<ProtocolComboBoxItem> protocolsList = new ArrayList<ProtocolComboBoxItem>();
		Repository protocolRep = Repository.getProtocolRepository();
		for (RepItem protocl : protocolRep.getItems()) {
			protocolsList.add(new ProtocolComboBoxItem(
					(ProtocolRepItem) protocl));
		}

		Repository agent_rep = Repository.get_agent_repository();
		ArrayList<AgentComboBoxItem> agentslist = new ArrayList<AgentComboBoxItem>();
		for (RepItem agt : agent_rep.getItems()) {
			agentslist.add(new AgentComboBoxItem((AgentRepItem) agt));
		}

		cmbPrefProfileA.removeAllItems();
		cmbPrefProfileB.removeAllItems();
		for (RepItem prof : getProfiles()) {
			cmbPrefProfileA.addItem(new ProfileComboBoxItem(
					(ProfileRepItem) prof));
			cmbPrefProfileB.addItem(new ProfileComboBoxItem(
					(ProfileRepItem) prof));
		}
		cmbAgentA.removeAllItems();
		cmbAgentB.removeAllItems();

		/**
		 * #870 sort agents on non-linear space ability
		 */
		LinkedList<Object> agentlist = new LinkedList<Object>();
		agentlist.add("---- linear only agents ------");
		for (RepItem agt : agent_rep.getItems()) {
			AgentRepItem agentitem = (AgentRepItem) agt;
			try {
				if (agentitem.getInstance().getSupportedNegotiationSetting()
						.getUtilityspaceType() == UTILITYSPACETYPE.LINEAR) {
					agentlist
							.addLast(new AgentComboBoxItem((AgentRepItem) agt));
				} else {
					agentlist
							.addFirst(new AgentComboBoxItem((AgentRepItem) agt));
				}
			} catch (Exception e) {
				// if we get here, the agent can not be loaded. Ignore these.
			}

		}

		for (Object comp : agentlist) {
			cmbAgentA.addItem(comp);
			cmbAgentB.addItem(comp);
		}

		cmbProtocol.removeAllItems();
		for (RepItem protocol : protocolRep.getItems()) {
			cmbProtocol.addItem(new ProtocolComboBoxItem(
					(ProtocolRepItem) protocol));
		}
	}

	/**
	 * Start the session TODO use the parameters.
	 */
	public void start() throws Exception {
		int deadline = 180;
		try {
			deadline = Integer.parseInt(txtNonGUITimeout.getText());
			if (deadline < 1) {
				JOptionPane
						.showMessageDialog(this,
								"Invalid deadline. The deadline should be a positive number.");
				return;
			}
		} catch (Exception e) {
			JOptionPane
					.showMessageDialog(this,
							"Invalid deadline. The deadline should be a positive number.");
			return;
		}
		TournamentConfiguration.addOption("deadline", deadline);
		ProtocolRepItem protocol = ((ProtocolComboBoxItem) cmbProtocol
				.getSelectedItem()).protocol;
		if (protocol == null)
			throw new NullPointerException("Please select a protocol");

		ProfileRepItem[] agentProfiles = new ProfileRepItem[2];
		agentProfiles[0] = ((ProfileComboBoxItem) cmbPrefProfileA
				.getSelectedItem()).profile;

		agentProfiles[1] = ((ProfileComboBoxItem) cmbPrefProfileB
				.getSelectedItem()).profile;
		for (ProfileRepItem item : agentProfiles)
			if (item == null)
				throw new NullPointerException(
						"Please select a profile for agent");

		AgentRepItem[] agents = new AgentRepItem[2];
		if (cmbAgentA.getSelectedItem() instanceof AgentComboBoxItem) {
			agents[0] = ((AgentComboBoxItem) cmbAgentA.getSelectedItem()).agent;
		} else {
			JOptionPane.showMessageDialog(this, "Please select agent A.");
			return;

		}

		if (cmbAgentB.getSelectedItem() instanceof AgentComboBoxItem) {
			agents[1] = ((AgentComboBoxItem) cmbAgentB.getSelectedItem()).agent;
		} else {
			JOptionPane.showMessageDialog(this, "Please select agent B.");
			return;

		}

		// determine the domain
		DomainRepItem domain = agentProfiles[0].getDomain();
		if (domain != agentProfiles[1].getDomain()) {
			JOptionPane
					.showMessageDialog(this,
							"The profiles for agent A and B are from different domains.");
			return;
		}
		ProgressUI2 graphlistener = null;
		if (fShowProgressUI)
			graphlistener = new ProgressUI2(false, true);
		Protocol ns;
		try {
			ns = Global.createProtocolInstance(protocol, agents, agentProfiles,
					null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Cannot create protocol.");
		}
		if (fShowProgressUI) {
			NegoGUIApp.negoGUIView.replaceTab("Sess. Prog.", this,
					graphlistener);
			// ns.addNegotiationEventListener(graphlistener);
			// graphlistener.setNegotiationSession(ns);
			graphlistener.setProtocol(ns);
		}

		ns.startSession();
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
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		cmbPrefProfileA = new javax.swing.JComboBox();
		cmbAgentA = new javax.swing.JComboBox();
		cmbProtocol = new javax.swing.JComboBox();
		btnStart = new javax.swing.JButton();
		jPanel3 = new javax.swing.JPanel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		cmbPrefProfileB = new javax.swing.JComboBox();
		cmbAgentB = new javax.swing.JComboBox();
		jPanel6 = new javax.swing.JPanel();
		jLabel14 = new javax.swing.JLabel();
		txtNonGUITimeout = new javax.swing.JTextField();

		setName("Form"); // NOI18N

		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
				.getInstance(negotiator.gui.NegoGUIApp.class).getContext()
				.getResourceMap(NegoSessionUI2.class);
		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanel1.border.title"))); // NOI18N
		jPanel1.setName("jPanel1"); // NOI18N

		jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
		jLabel1.setName("jLabel1"); // NOI18N

		jPanel2.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanel2.border.title"))); // NOI18N
		jPanel2.setName("jPanel2"); // NOI18N

		jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
		jLabel2.setName("jLabel2"); // NOI18N

		jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
		jLabel3.setName("jLabel3"); // NOI18N

		cmbPrefProfileA.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		cmbPrefProfileA.setName("cmbPrefProfileA"); // NOI18N

		cmbAgentA.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"Item 1", "Item 2", "Item 3", "Item 4" }));
		cmbAgentA.setName("cmbAgentA"); // NOI18N

		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel2Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel2Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jLabel2).add(jLabel3))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel2Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(cmbAgentA, 0, 227, Short.MAX_VALUE)
										.add(cmbPrefProfileA, 0, 227,
												Short.MAX_VALUE)
										.add(org.jdesktop.layout.GroupLayout.TRAILING,
												jPanel2Layout
														.createSequentialGroup()))
								.addContainerGap()));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel2Layout
								.createSequentialGroup()
								.add(jPanel2Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabel2)
										.add(cmbPrefProfileA,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel2Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabel3)
										.add(cmbAgentA,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel2Layout
										.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE))
								.addContainerGap(
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		cmbProtocol.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Alternating offers" }));
		cmbProtocol.setName("cmbProtocol"); // NOI18N

		javax.swing.ActionMap actionMap = org.jdesktop.application.Application
				.getInstance(negotiator.gui.NegoGUIApp.class).getContext()
				.getActionMap(NegoSessionUI2.class, this);
		btnStart.setAction(actionMap.get("startSession")); // NOI18N
		btnStart.setText(resourceMap.getString("btnStart.text")); // NOI18N
		btnStart.setName("btnStart"); // NOI18N

		jPanel3.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanel3.border.title"))); // NOI18N
		jPanel3.setName("jPanel3"); // NOI18N

		jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
		jLabel5.setName("jLabel5"); // NOI18N

		jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
		jLabel6.setName("jLabel6"); // NOI18N

		cmbPrefProfileB.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		cmbPrefProfileB.setName("cmbPrefProfileB"); // NOI18N

		cmbAgentB.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"Item 1", "Item 2", "Item 3", "Item 4" }));
		cmbAgentB.setName("cmbAgentB"); // NOI18N

		org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel3Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel3Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jLabel5).add(jLabel6))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel3Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(cmbAgentB, 0, 227, Short.MAX_VALUE)
										.add(cmbPrefProfileB, 0, 227,
												Short.MAX_VALUE)
										.add(org.jdesktop.layout.GroupLayout.TRAILING,
												jPanel3Layout
														.createSequentialGroup()
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.UNRELATED)))
								.addContainerGap()));
		jPanel3Layout
				.setVerticalGroup(jPanel3Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel3Layout
								.createSequentialGroup()
								.add(jPanel3Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabel5)
										.add(cmbPrefProfileB,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel3Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabel6)
										.add(cmbAgentB,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel3Layout
										.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE))
								.addContainerGap(
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		jPanel6.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanel6.border.title"))); // NOI18N
		jPanel6.setName("jPanel6"); // NOI18N

		jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
		jLabel14.setName("jLabel14"); // NOI18N

		txtNonGUITimeout
				.setText(resourceMap.getString("txtNonGUITimeout.text")); // NOI18N
		txtNonGUITimeout.setName("txtNonGUITimeout"); // NOI18N

		org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(
				jPanel6);
		jPanel6.setLayout(jPanel6Layout);
		jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel6Layout
						.createSequentialGroup()
						.addContainerGap()
						.add(jPanel6Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								jLabel14))
						.add(14, 14, 14)
						.add(jPanel6Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								txtNonGUITimeout,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								227, Short.MAX_VALUE)).addContainerGap()));
		jPanel6Layout
				.setVerticalGroup(jPanel6Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel6Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabel14)
										.add(txtNonGUITimeout,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED)
								.add(jPanel6Layout
										.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE))
								.addContainerGap(19, Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel1Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel1Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jPanel1Layout
												.createSequentialGroup()
												.add(jPanel6,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addContainerGap())
										.add(jPanel1Layout
												.createSequentialGroup()
												.add(jPanel2,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addContainerGap())
										.add(jPanel1Layout
												.createSequentialGroup()
												.add(jLabel1)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.UNRELATED)
												.add(cmbProtocol, 0, 228,
														Short.MAX_VALUE)
												.add(29, 29, 29))
										.add(org.jdesktop.layout.GroupLayout.TRAILING,
												jPanel1Layout
														.createSequentialGroup()
														.add(btnStart,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																75,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.addContainerGap())))
						.add(jPanel1Layout
								.createParallelGroup(
										org.jdesktop.layout.GroupLayout.LEADING)
								.add(jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.add(jPanel3,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addContainerGap())));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel1Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel1Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jLabel1)
										.add(cmbProtocol,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED)
								.add(jPanel2,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(138, 138, 138)
								.add(jPanel6,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).add(btnStart)
								.addContainerGap())
						.add(jPanel1Layout
								.createParallelGroup(
										org.jdesktop.layout.GroupLayout.LEADING)
								.add(jPanel1Layout
										.createSequentialGroup()
										.add(174, 174, 174)
										.add(jPanel3,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(155, Short.MAX_VALUE))));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup()
						.addContainerGap()
						.add(jPanel1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup()
						.addContainerGap()
						.add(jPanel1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addContainerGap()));
	}// </editor-fold>//GEN-END:initComponents

	@Action
	public void startSession() {
		try {
			start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private javax.swing.JButton btnStart;
	private javax.swing.JComboBox cmbAgentA;
	private javax.swing.JComboBox cmbAgentB;
	private javax.swing.JComboBox cmbPrefProfileA;
	private javax.swing.JComboBox cmbPrefProfileB;
	private javax.swing.JComboBox cmbProtocol;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel14;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JTextField txtNonGUITimeout;
}

/**
 * this is to override the toString of an AgentRepItem, to show only the short
 * name.
 */
class AgentComboBoxItem extends JComponent {
	public AgentRepItem agent;

	public AgentComboBoxItem(AgentRepItem a) {
		agent = a;
	}

	public String toString() {
		return agent.getName();
	}
}

/**
 * this is to override the toString of an ProfileRepItem, to show only the short
 * name.
 */
class ProfileComboBoxItem {
	public ProfileRepItem profile;

	public ProfileComboBoxItem(ProfileRepItem p) {
		profile = p;
	}

	public String toString() {
		return profile.getURL().getFile();
	}
}

class ProtocolComboBoxItem {
	public ProtocolRepItem protocol;

	public ProtocolComboBoxItem(ProtocolRepItem p) {
		protocol = p;
	}

	public String toString() {
		return protocol.getName();
	}
}