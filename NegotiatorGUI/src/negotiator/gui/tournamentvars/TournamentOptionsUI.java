package negotiator.gui.tournamentvars;

import java.util.HashMap;

import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * The options specifying how to run a tournament.
 *
 * @author Mark Hendrikx
 */
public class TournamentOptionsUI extends JDialog {

	private static final long serialVersionUID = 6798249525629036801L;

    private JCheckBox accessPartnerPreferencesCheck;
    private JLabel accessPartnerPreferencesLabel;
    private JCheckBox oneSidedBiddingCheck;
    private JLabel oneSidedBiddingLabel;
    private JCheckBox allowPausingTimelineCheck;
    private JLabel allowPausingTimelineLabel;
    private JCheckBox appendModeAndDeadlineCheck;
    private JLabel appendModeAndDeadlineLabel;
    private JButton cancelButton;
    private JTextField deadlineField;
    private JLabel deadlineLabel;
    private JCheckBox disableGUICheck;
    private JLabel disableGUILabel;
    private JLabel logCompetitiveness;
    private JCheckBox logCompetitivenessCheck;
    private JCheckBox logDetailedAnalysisCheck;
    private JLabel logDetailedAnalysisLabel;
    private JCheckBox logNegotiationTraceCheck;
    private JCheckBox logFinalAccuracyCheck;
    private JLabel logFinalAccuracyLabel;
    private JLabel logNegotiationTraceLabel;
    private JPanel loggingTab;
    private JButton okButton;
    private JCheckBox playAgainstSelfCheck;
    private JLabel playAgainstSelfLabel;
    private JCheckBox playBothSidesCheck;
    private JLabel playBothSidesLabel;
    private JLabel generationModeLabel;
    private JComboBox generationModeSelector;
    private JLabel startingAgentLabel;
    private JComboBox startingAgentSelector;
    private JLabel randomSeedLabel;
    private JTextField randomSeedField;
    private JComboBox protocolModeSelector;
    private JLabel protocolModeLabel;
    private JPanel protocolSettingsTab;
    private JButton resetToDefaultButton;
    private JPanel sessionGenerationTab;
    private JCheckBox showAllBidsCheck;
    private JLabel showAllBidsLabel;
    private JCheckBox showLastBidCheck;
    private JLabel showLastBidLabel;
    private JTabbedPane tabbedPane;
    private JPanel visualizationTab;
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

    	tabbedPane = new javax.swing.JTabbedPane();
    	initProtocolSettingsTab();
    	initSessionGenerationTab();
    	initLoggingTab();
    	initVisualizationTab();
    	initButtons();
    	
        restoreOptions(config);

        setTitle("Options");
        setName("optionsFrame"); // NOI18N
        setResizable(false);

        pack();
        setVisible(true);
		return config;
    }
    
    private void initProtocolSettingsTab() {
    	protocolSettingsTab = new JPanel();
    	
    	protocolModeLabel = new JLabel("Protocol mode");
    	
    	String[] options = {"Time", "Rounds"};
        protocolModeSelector = new JComboBox(options);
        protocolModeSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (protocolModeSelector.getSelectedIndex() == 0) {
                	deadlineLabel.setText("Deadline (seconds)");
                	allowPausingTimelineCheck.setEnabled(true);
                } else {
                	deadlineLabel.setText("Deadline (rounds)");
                	allowPausingTimelineCheck.setEnabled(false);
                	allowPausingTimelineCheck.setSelected(false);
                }
            }
        });
    	
        deadlineLabel = new JLabel("Deadline (seconds)");
        deadlineField = new JTextField();
        
        accessPartnerPreferencesLabel = new JLabel("Access partner preferences");
        accessPartnerPreferencesCheck = new JCheckBox();
        
        oneSidedBiddingLabel = new JLabel("One-sided bidding");
        oneSidedBiddingCheck = new JCheckBox();
        
        allowPausingTimelineLabel = new JLabel("Allow pausing timeline");
        allowPausingTimelineCheck = new JCheckBox();
        allowPausingTimelineCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (allowPausingTimelineCheck.isSelected()) {
                	JOptionPane.showMessageDialog(null, "As threads are now not automatically quit when\n" +
                										"an agent ignores the deadline, ensure that all\n" +
                										"agents work correctly.", "Option info", 1);
                }
            }
        });
        
        javax.swing.GroupLayout protocolSettingsTabLayout = new javax.swing.GroupLayout(protocolSettingsTab);
        protocolSettingsTab.setLayout(protocolSettingsTabLayout);
        protocolSettingsTabLayout.setHorizontalGroup(
            protocolSettingsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(protocolSettingsTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(protocolSettingsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(protocolSettingsTabLayout.createSequentialGroup()
                        .addComponent(protocolModeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(protocolModeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(protocolSettingsTabLayout.createSequentialGroup()
                        .addComponent(accessPartnerPreferencesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 189, Short.MAX_VALUE)
                        .addComponent(accessPartnerPreferencesCheck))
                    .addGroup(protocolSettingsTabLayout.createSequentialGroup()
                        .addComponent(oneSidedBiddingLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 189, Short.MAX_VALUE)
                        .addComponent(oneSidedBiddingCheck))
                    .addGroup(protocolSettingsTabLayout.createSequentialGroup()
                        .addComponent(deadlineLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deadlineField, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(protocolSettingsTabLayout.createSequentialGroup()
                        .addComponent(allowPausingTimelineLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(allowPausingTimelineCheck)))
                .addContainerGap())
        );
        protocolSettingsTabLayout.setVerticalGroup(
            protocolSettingsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(protocolSettingsTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(protocolSettingsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(protocolSettingsTabLayout.createSequentialGroup()
                        .addGroup(protocolSettingsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(protocolModeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(protocolModeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deadlineLabel))
                    .addComponent(deadlineField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(protocolSettingsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(accessPartnerPreferencesLabel)
                    .addComponent(accessPartnerPreferencesCheck))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(protocolSettingsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
               		.addComponent(oneSidedBiddingLabel)
               		.addComponent(oneSidedBiddingCheck))
               		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(protocolSettingsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(allowPausingTimelineLabel)
                    .addComponent(allowPausingTimelineCheck))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        tabbedPane.addTab("Protocol settings", protocolSettingsTab);
    }
    
    public void initSessionGenerationTab() {
    	sessionGenerationTab = new JPanel();
    	
    	playBothSidesLabel = new JLabel("Play both sides");
    	playBothSidesCheck = new JCheckBox();
    	
    	playAgainstSelfLabel = new JLabel("Play against self");
    	playAgainstSelfCheck = new JCheckBox();
    	
    	generationModeLabel = new JLabel("Generation mode");
    	String[] options = {"Standard", "Random"};
        generationModeSelector = new JComboBox(options);
        generationModeSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	randomSeedField.setEnabled(generationModeSelector.getSelectedIndex() == 1);
            }
        });
        
        startingAgentLabel = new JLabel("Starting agent");
    	String[] startingAgentOptions = {"A", "B", "Random"};
        startingAgentSelector = new JComboBox(startingAgentOptions);
    	
        randomSeedLabel = new JLabel("Random seed");
        randomSeedField = new JTextField();
        randomSeedField.setText("0");
        randomSeedField.setEnabled(false);
    	
        javax.swing.GroupLayout sessionGenerationTabLayout = new javax.swing.GroupLayout(sessionGenerationTab);
        sessionGenerationTab.setLayout(sessionGenerationTabLayout);
        sessionGenerationTabLayout.setHorizontalGroup(
            sessionGenerationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sessionGenerationTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sessionGenerationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sessionGenerationTabLayout.createSequentialGroup()
                        .addComponent(playBothSidesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(playBothSidesCheck))
                    .addGroup(sessionGenerationTabLayout.createSequentialGroup()
                        .addComponent(playAgainstSelfLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 258, Short.MAX_VALUE)
                        .addComponent(playAgainstSelfCheck))
                    .addGroup(sessionGenerationTabLayout.createSequentialGroup()
	                        .addComponent(generationModeLabel)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 258, Short.MAX_VALUE)
	                        .addComponent(generationModeSelector))
	                .addGroup(sessionGenerationTabLayout.createSequentialGroup()
	                        .addComponent(startingAgentLabel)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 258, Short.MAX_VALUE)
	                        .addComponent(startingAgentSelector))
	                .addGroup(sessionGenerationTabLayout.createSequentialGroup()
	                        .addComponent(randomSeedLabel)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 258, Short.MAX_VALUE)
	                        .addComponent(randomSeedField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    ))
                .addContainerGap())
        );
        sessionGenerationTabLayout.setVerticalGroup(
            sessionGenerationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sessionGenerationTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sessionGenerationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(playBothSidesLabel)
                    .addComponent(playBothSidesCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sessionGenerationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(playAgainstSelfLabel)
                    .addComponent(playAgainstSelfCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sessionGenerationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(startingAgentLabel)
                    .addComponent(startingAgentSelector))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sessionGenerationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(generationModeLabel)
                    .addComponent(generationModeSelector))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sessionGenerationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(randomSeedLabel)
                    .addComponent(randomSeedField))
                .addContainerGap(164, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Session generation", sessionGenerationTab);
    }
    
    public void initLoggingTab() {
    	loggingTab = new JPanel();
    	
    	logDetailedAnalysisLabel = new JLabel("Log detailed analysis");
    	logDetailedAnalysisCheck = new JCheckBox();
    	
    	logNegotiationTraceLabel = new JLabel("Log negotiation trace");
        logNegotiationTraceCheck = new JCheckBox();
        logNegotiationTraceCheck.setEnabled(false);
        
        logFinalAccuracyLabel = new JLabel("Log final accuracy");
        logFinalAccuracyCheck = new JCheckBox();
        logFinalAccuracyCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (logFinalAccuracyCheck.isSelected()) {
                	JOptionPane.showMessageDialog(null, "Profiles the opponent model of the BOA agents on\n" +
                										"side A.\n", "Option info", 1);
        			
                }
            }
        });
        
        logCompetitiveness = new JLabel("Log competitiveness");
        logCompetitivenessCheck = new JCheckBox();

        appendModeAndDeadlineLabel = new JLabel("Append mode and deadline");
        appendModeAndDeadlineCheck = new JCheckBox();
        
        javax.swing.GroupLayout loggingTabLayout = new javax.swing.GroupLayout(loggingTab);
        loggingTab.setLayout(loggingTabLayout);
        loggingTabLayout.setHorizontalGroup(
            loggingTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loggingTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loggingTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(loggingTabLayout.createSequentialGroup()
                        .addComponent(logDetailedAnalysisLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logDetailedAnalysisCheck))
                    .addGroup(loggingTabLayout.createSequentialGroup()
                        .addComponent(logNegotiationTraceLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logNegotiationTraceCheck))
                    .addGroup(loggingTabLayout.createSequentialGroup()
                        .addComponent(logFinalAccuracyLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logFinalAccuracyCheck))
                    .addGroup(loggingTabLayout.createSequentialGroup()
                        .addComponent(logCompetitiveness)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logCompetitivenessCheck))
                    .addGroup(loggingTabLayout.createSequentialGroup()
                        .addComponent(appendModeAndDeadlineLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 194, Short.MAX_VALUE)
                        .addComponent(appendModeAndDeadlineCheck)))
                .addContainerGap())
        );
        loggingTabLayout.setVerticalGroup(
            loggingTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loggingTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loggingTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(loggingTabLayout.createSequentialGroup()
                        .addGroup(loggingTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(loggingTabLayout.createSequentialGroup()
                                .addGroup(loggingTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(logDetailedAnalysisLabel)
                                    .addComponent(logDetailedAnalysisCheck))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logNegotiationTraceLabel))
                            .addComponent(logNegotiationTraceCheck))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(logFinalAccuracyLabel))
                    .addComponent(logFinalAccuracyCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(loggingTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(logCompetitiveness)
                    .addComponent(logCompetitivenessCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(loggingTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(appendModeAndDeadlineLabel)
                    .addComponent(appendModeAndDeadlineCheck))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Logging", loggingTab);
    }
    
    public void initVisualizationTab() {
    	visualizationTab = new JPanel();
    	
    	showAllBidsLabel = new JLabel("Show all bids");
    	showAllBidsCheck = new JCheckBox();
    	
    	showLastBidLabel = new JLabel("Show last bid");
    	showLastBidCheck = new JCheckBox();
    	
    	disableGUILabel = new JLabel();
        disableGUICheck = new JCheckBox();
        disableGUILabel.setText("Disable GUI");
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
        
		javax.swing.GroupLayout visualizationTabLayout = new javax.swing.GroupLayout(visualizationTab);
        visualizationTab.setLayout(visualizationTabLayout);
        visualizationTabLayout.setHorizontalGroup(
            visualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visualizationTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(visualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visualizationTabLayout.createSequentialGroup()
                        .addComponent(showAllBidsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(showAllBidsCheck))
                    .addGroup(visualizationTabLayout.createSequentialGroup()
                        .addComponent(disableGUILabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(disableGUICheck))
                    .addGroup(visualizationTabLayout.createSequentialGroup()
                        .addComponent(showLastBidLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 273, Short.MAX_VALUE)
                        .addComponent(showLastBidCheck)))
                .addContainerGap())
        );
        visualizationTabLayout.setVerticalGroup(
            visualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visualizationTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(visualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(visualizationTabLayout.createSequentialGroup()
                        .addGroup(visualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(showAllBidsLabel)
                            .addComponent(showAllBidsCheck))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(showLastBidLabel))
                    .addComponent(showLastBidCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(visualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(disableGUILabel)
                    .addComponent(disableGUICheck))
                .addContainerGap(135, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Visualization", visualizationTab);
    }
    
    private void initButtons() {
    	okButton = new JButton();
        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	boolean allValid = true;
            	
            	try {
            		int deadline = Integer.parseInt(deadlineField.getText());
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
            	
            	try {
            		int randomSeed = Integer.parseInt(randomSeedField.getText());
        			config.put("randomSeed", randomSeed);
            	} catch (NumberFormatException e) {
            		allValid = false;
            		JOptionPane.showMessageDialog(null, "The random seed should be an integer.");
            	}
            	
            	config.put("startingAgent", startingAgentSelector.getSelectedIndex());
            	config.put("generationMode", generationModeSelector.getSelectedIndex());
            	config.put("accessPartnerPreferences", accessPartnerPreferencesCheck.isSelected() ? 1 : 0);
            	config.put("oneSidedBidding", oneSidedBiddingCheck.isSelected() ? 1 : 0);
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
        
        resetToDefaultButton = new JButton("Reset to default");
        resetToDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetToDefaults();
            }
        });
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resetToDefaultButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton)
                    .addComponent(resetToDefaultButton))
                .addGap(0, 13, Short.MAX_VALUE))
        );
    }
    
	private void restoreOptions(HashMap<String, Integer> prevConfig) {
		if (prevConfig != null && prevConfig.size() > 0) 
		{
			if (prevConfig.containsKey("protocolMode")) {
				protocolModeSelector.setSelectedIndex(prevConfig.get("protocolMode"));
			}
			if (prevConfig.containsKey("startingAgent")) {
				startingAgentSelector.setSelectedIndex(prevConfig.get("startingAgent"));
			}
			if (prevConfig.containsKey("generationMode")) {
				generationModeSelector.setSelectedIndex(prevConfig.get("generationMode"));
			}
			
			if (prevConfig.containsKey("randomSeed")) {
				randomSeedField.setText(prevConfig.get("randomSeed") + "");
			}
			
			if (prevConfig.containsKey("deadline")) {
				deadlineField.setText(prevConfig.get("deadline") + "");
			}
			allowPausingTimelineCheck.setSelected(prevConfig.containsKey("allowPausingTimeline") && prevConfig.get("allowPausingTimeline") != 0);
			oneSidedBiddingCheck.setSelected(prevConfig.containsKey("oneSidedBidding") && prevConfig.get("oneSidedBidding") != 0);
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
		} else {
			resetToDefaults();
		}
		
	}
	
	public void resetToDefaults() {
		protocolModeSelector.setSelectedIndex(0);
        deadlineField.setText("180");
        oneSidedBiddingCheck.setSelected(false);
        accessPartnerPreferencesCheck.setSelected(false);
        allowPausingTimelineCheck.setEnabled(true);
        allowPausingTimelineCheck.setSelected(false);
        playBothSidesCheck.setSelected(true);
        playAgainstSelfCheck.setSelected(false);
        logDetailedAnalysisCheck.setSelected(false);
        logNegotiationTraceCheck.setSelected(false);
        logFinalAccuracyCheck.setSelected(false);
        logCompetitivenessCheck.setSelected(false);
        appendModeAndDeadlineCheck.setSelected(false);
        showAllBidsCheck.setSelected(true);
        showAllBidsCheck.setEnabled(true);
        showLastBidCheck.setSelected(true);
        showLastBidCheck.setEnabled(true);
        disableGUICheck.setSelected(false);
        startingAgentSelector.setSelectedIndex(0);
        generationModeSelector.setSelectedIndex(0);
        randomSeedField.setText("0");
        randomSeedField.setEnabled(false);
	}
}