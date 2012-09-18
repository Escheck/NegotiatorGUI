package negotiator.gui.tournamentvars;

import java.awt.Frame;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
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
	private JLabel logDetailedAnalysis;
	private JCheckBox logDetailedAnalysisCheck;
	private JLabel logFinalAccuracy;
	private JCheckBox logFinalAccuracyCheck;
	private JLabel logNegotiationTrace;
    private JCheckBox logNegotiationTraceCheck;
    private JLabel logSessions;
    private JCheckBox logSessionsCheck;
    private JLabel logging;
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
                	deadlineTextField.setEnabled(false);
                } else {
                	deadline.setText("Deadline (rounds)");
                	deadlineTextField.setEnabled(true);
                }
            }
        });
        
        // 		OPTION: deadline
        deadline = new JLabel();
        deadline = new JLabel();
        deadline.setText("Deadline (seconds)");
        deadlineTextField = new JTextField();
        
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
        
    	// 		OPTION: log sessions
    	logSessions = new JLabel();
        logSessionsCheck = new JCheckBox();
        logSessions.setText("Log sessions");
        logSessionsCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (!logSessionsCheck.isSelected()) {
                	logDetailedAnalysisCheck.setSelected(false);
                	logNegotiationTraceCheck.setSelected(false);
                	logFinalAccuracyCheck.setSelected(false);
                }
            }
        });
        
        //		OPTION: log detailed analysis
        logDetailedAnalysis = new JLabel();
        logDetailedAnalysisCheck = new JCheckBox();
        logDetailedAnalysis.setText("Log detailed analysis");
        logDetailedAnalysisCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (logDetailedAnalysisCheck.isSelected()) {
                	logSessionsCheck.setSelected(true);
                }
            }
        });
        
        //		OPTION: log negotiation trace
        logNegotiationTrace = new JLabel();
        logNegotiationTraceCheck = new JCheckBox();
        logNegotiationTraceCheck.setEnabled(false);
        logNegotiationTrace.setText("Log negotiation trace");
        logNegotiationTraceCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (logNegotiationTraceCheck.isSelected()) {
                	logSessionsCheck.setSelected(true);
                }
            }
        });

        //		OPTION: log final accuracy
        logFinalAccuracy = new JLabel();
        logFinalAccuracyCheck = new JCheckBox();
        logFinalAccuracyCheck.setEnabled(false);
        logFinalAccuracy.setText("Log final accuracy");
        logFinalAccuracyCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (logFinalAccuracyCheck.isSelected()) {
                	logSessionsCheck.setSelected(true);
                }
            }
        });
        

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
            	config.put("protocolMode", protocolModeSelector.getSelectedIndex());
                config.put("playBothSides", playBothSidesCheck.isSelected() ? 1 : 0);
                config.put("playAgainstSelf", playAgainstSelfCheck.isSelected() ? 1 : 0);
                config.put("logDetailedAnalysis", logDetailedAnalysisCheck.isSelected() ? 1 : 0);
                config.put("logSessions", logSessionsCheck.isSelected() ? 1 : 0);
                config.put("logNegotiationTrace", logNegotiationTraceCheck.isSelected() ? 1 : 0);
                config.put("logFinalAccuracy", logFinalAccuracyCheck.isSelected() ? 1 : 0);
                config.put("showAllBids", showAllBidsCheck.isSelected() ? 1 : 0);
                config.put("showLastBid", showLastBidCheck.isSelected() ? 1 : 0);
    			
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
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
                            .addComponent(deadline, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                            .addComponent(protocolMode, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                            .addComponent(logNegotiationTrace, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                            .addComponent(protocolSettings)
                            .addComponent(showAllBids, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                            .addComponent(logDetailedAnalysis, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                                    .addComponent(playBothSidesCheck)
                                    .addComponent(playAgainstSelfCheck)
                                    .addComponent(logSessionsCheck)
                                    .addComponent(logDetailedAnalysisCheck)
                                    .addComponent(logNegotiationTraceCheck)
                                    .addComponent(showAllBidsCheck)
                                    .addComponent(showLastBidCheck)
                                    .addComponent(logFinalAccuracyCheck))
                                .addGap(25, 25, 25))
                            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(protocolModeSelector, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(deadlineTextField, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(sessionGeneration)
                    .addComponent(playAgainstSelf, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                    .addComponent(logging)
                    .addComponent(logSessions, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                    .addComponent(playBothSides, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                    .addComponent(logFinalAccuracy, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                    .addComponent(visualization)
                    .addComponent(showLastBid, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(logSessions, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(logSessionsCheck))
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
                .addComponent(visualization)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(showAllBids, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(showAllBidsCheck))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(showLastBid, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(okButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelButton))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(showLastBidCheck)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
			playBothSidesCheck.setSelected(prevConfig.containsKey("playBothSides") && prevConfig.get("playBothSides") != 0);
			playAgainstSelfCheck.setSelected(prevConfig.containsKey("playAgainstSelf") && prevConfig.get("playAgainstSelf") != 0);
			logDetailedAnalysisCheck.setSelected(prevConfig.containsKey("logDetailedAnalysis") && prevConfig.get("logDetailedAnalysis") != 0);
			logNegotiationTraceCheck.setSelected(prevConfig.containsKey("logNegotiationTrace") && prevConfig.get("logNegotiationTrace") != 0);
			logSessionsCheck.setSelected(prevConfig.containsKey("logSessions") && prevConfig.get("logSessions") != 0);
			logFinalAccuracyCheck.setSelected(prevConfig.containsKey("logFinalAccuracy") && prevConfig.get("logFinalAccuracy") != 0);
			showAllBidsCheck.setSelected(prevConfig.containsKey("showAllBids") && prevConfig.get("showAllBids") != 0);
			showLastBidCheck.setSelected(prevConfig.containsKey("showLastBid") && prevConfig.get("showLastBid") != 0);
		}
		
	}
}