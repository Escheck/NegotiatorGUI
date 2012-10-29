package negotiator.gui.tournamentvars;

import java.awt.Frame;
import java.util.HashMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * A tournament 
 *
 * @author Mark Hendrikx
 */
public class TournamentOptionsUI extends JDialog {

	private static final long serialVersionUID = 6798249525629036801L;

	private JButton cancelButton;
	private JLabel deadline;
	private JTextField deadlineTextField;
	private JLabel accessPartnerPreferences;
	private JCheckBox accessPartnerPreferencesCheck;
	private JLabel allowPausingTimeline;
	private JCheckBox allowPausingTimelineCheck;
	private JLabel logDetailedAnalysis;
	private JCheckBox logDetailedAnalysisCheck;
	private JLabel logFinalAccuracy;
	private JCheckBox logFinalAccuracyCheck;
	private JLabel logCompetitiveness;
	private JCheckBox logCompetitivenessCheck;
	private JLabel logNegotiationTrace;
    private JCheckBox logNegotiationTraceCheck;
    private JLabel logging;
    private JLabel appendModeAndDeadline;
    private JCheckBox appendModeAndDeadlineCheck;
    private JButton okButton;
    private JLabel playAgainstSelf;
    private JCheckBox playAgainstSelfCheck;
    private JLabel playBothSides;
    private JCheckBox playBothSidesCheck;
    private JLabel protocolMode;
    private JComboBox protocolModeSelector;
    private JLabel protocolSettings;
    private JLabel sessionGeneration;
    private JLabel showAllBids;
    private JCheckBox showAllBidsCheck;
    private JLabel showLastBid;
    private JCheckBox showLastBidCheck;
    private JLabel visualization;
    private JLabel disableGUI;
    private JCheckBox disableGUICheck;
    private HashMap<String, Integer> config;

    public TournamentOptionsUI(Frame frame) {
		super(frame, true);
		this.setLocation(frame.getLocation().x + frame.getWidth() / 2, frame.getLocation().y + frame.getHeight() / 4);
		this.setSize(frame.getSize().width / 3, frame.getSize().height / 2);
	}
    
    public HashMap<String, Integer> getResult(HashMap<String, Integer> prevConfig) {
    	if (prevConfig == null) {
    		config = new HashMap<String, Integer>();
    	} else {
    		config = prevConfig;
    	}

    	// HEADER: protocol settings
        protocolSettings = new JLabel();
        protocolSettings.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        protocolSettings.setText("Protocol settings");
        
        // 		OPTION: protocol mode
        protocolMode = new JLabel();
        protocolMode.setText("Protocol mode");
        String[] options = {"Time", "Rounds"};
        protocolModeSelector = new JComboBox(options);
        protocolModeSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (protocolModeSelector.getSelectedIndex() == 0) {
                	deadline.setText("Deadline (seconds)");
                	allowPausingTimelineCheck.setEnabled(true);
                } else {
                	deadline.setText("Deadline (rounds)");
                	allowPausingTimelineCheck.setEnabled(false);
                	allowPausingTimelineCheck.setSelected(false);
                }
            }
        });
        
        // 		OPTION: deadline
        deadline = new JLabel();
        deadline = new JLabel();
        deadline.setText("Deadline (seconds)");
        deadlineTextField = new JTextField();
        
        //		OPTION: access opponent's preferences
		accessPartnerPreferences = new JLabel();
		accessPartnerPreferencesCheck = new JCheckBox();
        accessPartnerPreferences.setText("Access partner preferences");
        
        //		OPTION: all ow pausing time
        allowPausingTimeline = new JLabel();
        allowPausingTimelineCheck = new JCheckBox();
        allowPausingTimeline.setText("Allow pausing timeline");
        allowPausingTimelineCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (allowPausingTimelineCheck.isSelected()) {
                	JOptionPane.showMessageDialog(null, "As threads are now not automatically quit when\n" +
                										"an agent ignores the deadline, ensure that all\n" +
                										"agents work correctly.", "Option info", 1);
        			
                }
            }
        });
        
        // HEADER: session generation
        sessionGeneration = new JLabel();
        sessionGeneration.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        sessionGeneration.setText("Session generation");
        
		// 		OPTION: Play both sides
        playBothSides = new JLabel();
        playBothSidesCheck = new JCheckBox();
        playBothSides.setText("Play both sides");
        
    	// 		OPTION: play against self
        playAgainstSelf = new JLabel();
        playAgainstSelfCheck = new JCheckBox();
        playAgainstSelf.setText("Play against self");
        
    	// HEADER: logging
        logging = new JLabel();
        logging.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        logging.setText("Logging");
        
        //		OPTION: log detailed analysis
        logDetailedAnalysis = new JLabel();
        logDetailedAnalysisCheck = new JCheckBox();
        logDetailedAnalysis.setText("Log detailed analysis");
        
        //		OPTION: log negotiation trace
        logNegotiationTrace = new JLabel();
        logNegotiationTraceCheck = new JCheckBox();
        logNegotiationTraceCheck.setEnabled(false);
        logNegotiationTrace.setText("Log negotiation trace");

        //		OPTION: log final accuracy
        logFinalAccuracy = new JLabel();
        logFinalAccuracyCheck = new JCheckBox();
        logFinalAccuracy.setText("Log final accuracy");
        logFinalAccuracyCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (logFinalAccuracyCheck.isSelected()) {
                	JOptionPane.showMessageDialog(null, "Profiles the opponent model of the BOA agents on\n" +
                										"side A.\n", "Option info", 1);
        			
                }
            }
        });
        
        //		OPTION: log competitiveness
        logCompetitiveness = new JLabel();
        logCompetitivenessCheck = new JCheckBox();
        logCompetitiveness.setText("Log competitiveness");
        
        //		OPTION: append mode and deadline
        appendModeAndDeadline = new JLabel();
        appendModeAndDeadlineCheck = new JCheckBox();
        appendModeAndDeadline.setText("Append mode and deadline");
        
        // HEADER: visualization
        visualization = new JLabel();
        visualization.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        visualization.setText("Visualization");
        
        //		OPTION: show all bids
        showAllBids = new JLabel();
        showAllBidsCheck = new JCheckBox();
        showAllBids.setText("Show all bids");

        //		OPTION: show last bid
        showLastBid = new JLabel();
        showLastBidCheck = new JCheckBox();
        showLastBid.setText("Show last bid");
        
        //		OPTION: disable GUI
        disableGUI = new JLabel();
        disableGUICheck = new JCheckBox();
        disableGUI.setText("Disable GUI");
        disableGUICheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (disableGUICheck.isSelected()) {
                	showLastBidCheck.setSelected(false);
                	showLastBidCheck.setEnabled(false);
                	showAllBidsCheck.setSelected(false);
                	showAllBidsCheck.setEnabled(false);
                } else {
                	showLastBidCheck.setEnabled(true);
                	showAllBidsCheck.setEnabled(true);
                }
            }
        });
        
        okButton = new JButton();
        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	boolean allValid = true;
            	
            	try {
            		int deadline = Integer.parseInt(deadlineTextField.getText());
            		if (deadline > 0) {
            			config.put("deadline", deadline);
            		} else {
            			allValid = false;
            		}
            	} catch (NumberFormatException e) {
            		allValid = false;
            	}
            	if (!allValid) {
            		JOptionPane.showMessageDialog(null, "Please input a valid deadline.");
            	}
            	
            	config.put("accessPartnerPreferences", accessPartnerPreferencesCheck.isSelected() ? 1 : 0);
            	config.put("protocolMode", protocolModeSelector.getSelectedIndex());
            	config.put("allowPausingTimeline", allowPausingTimelineCheck.isSelected() ? 1 : 0);
                config.put("playBothSides", playBothSidesCheck.isSelected() ? 1 : 0);
                config.put("playAgainstSelf", playAgainstSelfCheck.isSelected() ? 1 : 0);
                config.put("logDetailedAnalysis", logDetailedAnalysisCheck.isSelected() ? 1 : 0);
                config.put("logNegotiationTrace", logNegotiationTraceCheck.isSelected() ? 1 : 0);
                config.put("logFinalAccuracy", logFinalAccuracyCheck.isSelected() ? 1 : 0);
                config.put("showAllBids", showAllBidsCheck.isSelected() ? 1 : 0);
                config.put("showLastBid", showLastBidCheck.isSelected() ? 1 : 0);
                config.put("disableGUI", disableGUICheck.isSelected() ? 1 : 0);
                config.put("appendModeAndDeadline", appendModeAndDeadlineCheck.isSelected() ? 1 : 0);
                config.put("logCompetitiveness", logCompetitivenessCheck.isSelected() ? 1 : 0);
                
                if (allValid) {
                	dispose();
                }
            }
        });
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });
        
        restoreOptions(config);

        setTitle("Options");
        setName("optionsFrame"); // NOI18N
        setResizable(false);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                    .addComponent(deadline, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(accessPartnerPreferences, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(allowPausingTimeline, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(protocolMode, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(logNegotiationTrace, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(protocolSettings)
                                    .addComponent(showAllBids, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(logDetailedAnalysis, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(disableGUI, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(45, 45, 45)
                                        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                                    		.addComponent(accessPartnerPreferencesCheck)
                                    		.addComponent(allowPausingTimelineCheck)
                                            .addComponent(playBothSidesCheck)
                                            .addComponent(playAgainstSelfCheck)
                                            .addComponent(logDetailedAnalysisCheck)
                                            .addComponent(logNegotiationTraceCheck)
                                            .addComponent(showAllBidsCheck)
                                            .addComponent(showLastBidCheck)
                                            .addComponent(logFinalAccuracyCheck)
                                            .addComponent(logCompetitivenessCheck)
                                            .addComponent(appendModeAndDeadlineCheck)
                                            .addComponent(disableGUICheck))
                                        .addGap(25, 25, 25))
                                    .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                            .addComponent(protocolModeSelector, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(deadlineTextField, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(sessionGeneration)
                            .addComponent(playAgainstSelf, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                            .addComponent(logging)
                            .addComponent(playBothSides, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                            .addComponent(logFinalAccuracy, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                            .addComponent(logCompetitiveness, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                            .addComponent(appendModeAndDeadline, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                            .addComponent(visualization)
                            .addComponent(showLastBid, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                        .addGap(84, 84, 84))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(protocolSettings)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(protocolMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(protocolModeSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(deadline, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(deadlineTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
            	.addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(accessPartnerPreferences, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(accessPartnerPreferencesCheck, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(allowPausingTimeline, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(allowPausingTimelineCheck, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(sessionGeneration)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(playBothSides, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(playBothSidesCheck))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(playAgainstSelf, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(playAgainstSelfCheck))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(logging)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(logDetailedAnalysis, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(logDetailedAnalysisCheck))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(logNegotiationTrace, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(logNegotiationTraceCheck))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(logFinalAccuracy, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(logFinalAccuracyCheck))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(logCompetitiveness, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(logCompetitivenessCheck))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(appendModeAndDeadline, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(appendModeAndDeadlineCheck))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(visualization)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(showAllBids, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                            .addComponent(showAllBidsCheck))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(showLastBid, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                            .addComponent(showLastBidCheck))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(disableGUI, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                    .addComponent(disableGUICheck))
                .addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );
        
        pack();
        setVisible(true);
		return config;
    }

    
	private void restoreOptions(HashMap<String, Integer> prevConfig) {
		if (prevConfig != null && prevConfig.size() > 0) {
			if (prevConfig.containsKey("protocolMode")) {
				protocolModeSelector.setSelectedIndex(prevConfig.get("protocolMode"));
			}
			if (prevConfig.containsKey("deadline")) {
				deadlineTextField.setText(prevConfig.get("deadline") + "");
			}
			allowPausingTimelineCheck.setSelected(prevConfig.containsKey("allowPausingTimeline") && prevConfig.get("allowPausingTimeline") != 0);
			accessPartnerPreferencesCheck.setSelected(prevConfig.containsKey("accessPartnerPreferences") && prevConfig.get("accessPartnerPreferences") != 0);
			playBothSidesCheck.setSelected(prevConfig.containsKey("playBothSides") && prevConfig.get("playBothSides") != 0);
			playAgainstSelfCheck.setSelected(prevConfig.containsKey("playAgainstSelf") && prevConfig.get("playAgainstSelf") != 0);
			logDetailedAnalysisCheck.setSelected(prevConfig.containsKey("logDetailedAnalysis") && prevConfig.get("logDetailedAnalysis") != 0);
			logNegotiationTraceCheck.setSelected(prevConfig.containsKey("logNegotiationTrace") && prevConfig.get("logNegotiationTrace") != 0);
			logFinalAccuracyCheck.setSelected(prevConfig.containsKey("logFinalAccuracy") && prevConfig.get("logFinalAccuracy") != 0);
			showAllBidsCheck.setSelected(prevConfig.containsKey("showAllBids") && prevConfig.get("showAllBids") != 0);
			showLastBidCheck.setSelected(prevConfig.containsKey("showLastBid") && prevConfig.get("showLastBid") != 0);
			disableGUICheck.setSelected(prevConfig.containsKey("disableGUI") && prevConfig.get("disableGUI") != 0);
			appendModeAndDeadlineCheck.setSelected(prevConfig.containsKey("appendModeAndDeadline") && prevConfig.get("appendModeAndDeadline") != 0);
			logCompetitivenessCheck.setSelected(prevConfig.containsKey("logCompetitiveness") && prevConfig.get("logCompetitiveness") != 0);
			
			if (disableGUICheck.isSelected()) {
				showAllBidsCheck.setEnabled(false);
				showLastBidCheck.setEnabled(false);
			}
		}
		
	}
}