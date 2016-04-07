/*
 * ProgressUI2.java
 *
 * Created on September 8, 2008, 3:24 PM
 */

package negotiator.gui.progress;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;
import negotiator.events.NegotiationEvent;
import negotiator.gui.chart.MultipartyBidChart;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.protocol.MediatorProtocol;
import negotiator.session.Session;
import negotiator.session.SessionManager;
import negotiator.utility.UtilitySpace;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class MultipartyProgressUI extends javax.swing.JPanel implements
		MultipartyNegotiationEventListener {

	public static final int MAX_TEXT_OUTPUT = 100000; // 100K
	protected int round = 0;
	/** the table model at the bottom */
	private String[] progressTableInfo;
	private MultipartyBidChart bidChart;
	private TextArea logText;
	private JPanel chart;

	// about negotiation session
	private Session session;

	// about negotiation session mgr
	private SessionManager manager;

	private List<NegotiationPartyInternal> parties;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTable biddingTable;
	private javax.swing.JPanel jPanelNegoChart;
	private javax.swing.JPanel jPanelNegoLog;
	private javax.swing.JPanel jPanelNegoTable;
	private javax.swing.JScrollPane jScrollPaneNegoChart;
	private javax.swing.JScrollPane jScrollPaneNegoLog;
	private javax.swing.JScrollPane jScrollPaneNegoTable;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JSplitPane jSplitPane2;
	private javax.swing.JSplitPane jSplitPane3;
	private javax.swing.JTextArea textOutput;

	/** Creates new form ProgressUI2 */
	public MultipartyProgressUI(ArrayList<String> partyInfo,
			SessionManager mgr, List<NegotiationPartyInternal> parties) {

		this.manager = mgr;
		this.parties = parties;

		progressTableInfo = new String[partyInfo.size() + 1];
		progressTableInfo[0] = "Round";
		for (int i = 0; i < partyInfo.size(); i++)
			progressTableInfo[i + 1] = partyInfo.get(i);

		initComponents();
		bidChart = new MultipartyBidChart(partyInfo); // maximum round will be
														// given
		biddingTable.setGridColor(Color.lightGray);
		initializeProgressGUI("initialized...", bidChart, biddingTable);
	}

	private void initializeProgressGUI(String logging,
			MultipartyBidChart bidChart, JTable bidTable) {

		Container pane = jPanelNegoChart;
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		Border loweredetched = BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED);

		// the chart panel
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		JFreeChart plot = bidChart.getChart();
		chart = new ChartPanel(plot);
		chart.setMinimumSize(new Dimension(350, 350));
		chart.setBorder(loweredetched);
		c.insets = new Insets(10, 0, 0, 10);
		c.ipadx = 10;
		c.ipady = 10;
		pane.add(chart, c);

		jPanelNegoChart.add(chart);
		logText = new TextArea();
		logText.setText("");

	}

	// End of variables declaration//GEN-END:variables

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jSplitPane2 = new javax.swing.JSplitPane();
		jSplitPane1 = new javax.swing.JSplitPane();
		jSplitPane3 = new javax.swing.JSplitPane();
		jPanelNegoChart = new javax.swing.JPanel();
		jScrollPaneNegoChart = new javax.swing.JScrollPane();
		jPanelNegoTable = new javax.swing.JPanel();
		jScrollPaneNegoTable = new javax.swing.JScrollPane();
		biddingTable = new javax.swing.JTable();
		jPanelNegoLog = new javax.swing.JPanel();
		jScrollPaneNegoLog = new javax.swing.JScrollPane();
		textOutput = new javax.swing.JTextArea();

		jSplitPane2.setName("jSplitPane2"); // NOI18N

		setName("Form"); // NOI18N

		jSplitPane1.setDividerSize(3);
		jSplitPane1.setName("jSplitPane1"); // NOI18N

		jSplitPane3.setDividerSize(3);
		jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		jSplitPane3.setName("jSplitPane3"); // NOI18N

		jPanelNegoChart.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Negotiation dynamics chart"));
		jPanelNegoChart.setName("jPanelNegoChart"); // NOI18N

		jScrollPaneNegoChart.setName("jScrollPaneNegoChart"); // NOI18N

		org.jdesktop.layout.GroupLayout jPanelNegoChartLayout = new org.jdesktop.layout.GroupLayout(
				jPanelNegoChart);
		jPanelNegoChart.setLayout(jPanelNegoChartLayout);
		jPanelNegoChartLayout.setHorizontalGroup(jPanelNegoChartLayout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(jPanelNegoChartLayout
						.createSequentialGroup()
						.addContainerGap()
						.add(jScrollPaneNegoChart,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								346,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(26, Short.MAX_VALUE)));
		jPanelNegoChartLayout.setVerticalGroup(jPanelNegoChartLayout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(jPanelNegoChartLayout
						.createSequentialGroup()
						.addContainerGap()
						.add(jScrollPaneNegoChart,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								71, Short.MAX_VALUE)));

		jSplitPane3.setTopComponent(jPanelNegoChart);

		jPanelNegoTable
				.setBorder(javax.swing.BorderFactory
						.createTitledBorder("Discounted utility of the proposed offers"));
		jPanelNegoTable.setName("jPanelNegoTable"); // NOI18N

		jScrollPaneNegoTable.setName("jScrollPaneNegoTable"); // NOI18N

		biddingTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] {}, progressTableInfo) {
			boolean[] canEdit = new boolean[progressTableInfo.length];

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});

		biddingTable.setName("biddingTable"); // NOI18N
		jScrollPaneNegoTable.setViewportView(biddingTable);

		org.jdesktop.layout.GroupLayout jPanelNegoTableLayout = new org.jdesktop.layout.GroupLayout(
				jPanelNegoTable);
		jPanelNegoTable.setLayout(jPanelNegoTableLayout);
		jPanelNegoTableLayout.setHorizontalGroup(jPanelNegoTableLayout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(jScrollPaneNegoTable,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 382,
						Short.MAX_VALUE));
		jPanelNegoTableLayout.setVerticalGroup(jPanelNegoTableLayout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(jScrollPaneNegoTable,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165,
						Short.MAX_VALUE));

		jSplitPane3.setRightComponent(jPanelNegoTable);
		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
				.getInstance().getContext()
				.getResourceMap(MultipartyProgressUI.class);
		jPanelNegoTable.getAccessibleContext().setAccessibleName(
				resourceMap
						.getString("jPanel3.AccessibleContext.accessibleName")); // NOI18N

		jSplitPane1.setRightComponent(jSplitPane3);

		jPanelNegoLog.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Negotiation log"));
		jPanelNegoLog.setName("jPanelNegoLog"); // NOI18N

		jScrollPaneNegoLog.setName("jScrollPaneNegoLog"); // NOI18N

		textOutput.setColumns(20);
		textOutput.setRows(5);
		textOutput.setName("textLogOutput"); // NOI18N
		jScrollPaneNegoLog.setViewportView(textOutput);

		org.jdesktop.layout.GroupLayout jPanelNegoLogLayout = new org.jdesktop.layout.GroupLayout(
				jPanelNegoLog);
		jPanelNegoLog.setLayout(jPanelNegoLogLayout);
		jPanelNegoLogLayout.setHorizontalGroup(jPanelNegoLogLayout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(jScrollPaneNegoLog,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81,
						Short.MAX_VALUE));
		jPanelNegoLogLayout.setVerticalGroup(jPanelNegoLogLayout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(jScrollPaneNegoLog,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 275,
						Short.MAX_VALUE));

		jSplitPane1.setLeftComponent(jPanelNegoLog);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(jSplitPane1,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 494,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(jSplitPane1,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300,
				Short.MAX_VALUE));
	}// </editor-fold>//GEN-END:initComponents

	private void addRowBiddingTable(int round, int turn,
			ArrayList<Double> partyUtilities) {

		DefaultTableModel partyModel = (DefaultTableModel) biddingTable
				.getModel();
		Object[] currentBiddingObject = new Object[partyUtilities.size() + 1];

		currentBiddingObject[0] = round + turn / 10d;
		for (int i = 0; i < partyUtilities.size(); i++) {
			currentBiddingObject[i + 1] = partyUtilities.get(i);
		}

		partyModel.addRow(currentBiddingObject);
		if (partyModel.getRowCount() > MAX_TEXT_OUTPUT) {
			partyModel.removeRow(0);
		}

		biddingTable.setModel(partyModel);
	}

	private void handleLogMessageEvent(LogMessageEvent evt) {
		textOutput.append(evt.getMessage() + "\n");
		if (textOutput.getLineCount() > MAX_TEXT_OUTPUT) {
			try {
				int end = textOutput.getLineEndOffset(0);
				textOutput.replaceRange("", 0, end);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		// writing log to session --> session.setLog(textOutput.getText());
	}

	@Override
	public void handleEvent(NegotiationEvent e) {
		if (e instanceof LogMessageEvent) {
			handleLogMessageEvent((LogMessageEvent) e);
		} else if (e instanceof MultipartyNegotiationOfferEvent) {
			MultipartyNegotiationOfferEvent e1 = (MultipartyNegotiationOfferEvent) e;
			// time will be used later
			addRowBiddingTable(e1.getRound(), e1.getTurn(),
					e1.getPartyUtilities());
		} else if (e instanceof MultipartyNegotiationSessionEvent) {
			handleMultipartyNegotiationEvent((MultipartyNegotiationSessionEvent) e);
		}
	}

	private void handleMultipartyNegotiationEvent(
			MultipartyNegotiationSessionEvent evt) {
		session = evt.getSession();
		bidChart.setMaxRound(session.getRoundNumber() + 1);
		bidChart.setNashSeries(getNashProduct(session.getRoundNumber()));
		bidChart.setBidSeries(manager.getAgentUtilsDiscounted());

		if (evt.getAgreement() != null) {
			bidChart.setAgreementPoints(manager.getAgreementUtilitiesDiscounted());
		}
	}

	private List<UtilitySpace> getUtilitySpaces() {
		List<UtilitySpace> spaces = new ArrayList<UtilitySpace>();
		for (NegotiationPartyInternal party : MediatorProtocol
				.getNonMediators(parties))
			spaces.add(party.getUtilitySpace());
		return spaces;
	}

	private void calculateNashProduct() throws Exception {

		double tempProduct = 1.0;
		Bid currentBid, nashBid = null;
		BidIterator lBidIter = new BidIterator(getUtilitySpaces().get(0)
				.getDomain());
		int i = 0;

		while (lBidIter.hasNext()) {
			i++;
			tempProduct = 1.0;

			currentBid = lBidIter.next();

			for (UtilitySpace utilitySpace : getUtilitySpaces())
				tempProduct *= utilitySpace.getUtility(currentBid);

			if (tempProduct > nashProduct) {
				nashProduct = tempProduct;
			}
		}
	}

	private double nashProduct = -1d;

	public double[][] getNashProduct(double roundRange) {
		try {
			if (nashProduct == -1d)
				calculateNashProduct();
		} catch (Exception e) {
			e.printStackTrace();
		}

		double[][] nashDataSeries = new double[2][2];
		nashDataSeries[0][0] = -1;
		nashDataSeries[1][0] = nashProduct;
		nashDataSeries[0][1] = roundRange + 2;
		nashDataSeries[1][1] = nashProduct;

		return nashDataSeries;
	}

}
