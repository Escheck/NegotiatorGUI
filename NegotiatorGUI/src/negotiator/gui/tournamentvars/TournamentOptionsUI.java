package negotiator.gui.tournamentvars;

import java.awt.Frame;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * A tournament 
 *
 * @author Mark Hendrikx
 */
public class TournamentOptionsUI extends JDialog {

	private static final long serialVersionUID = 6798249525629036801L;
	// Category labels
    private JLabel sessionGenerationLabel;
    private JLabel loggingLabel;
    
    // Options
    private JLabel logSessionsLabel;   
    private JCheckBox logSessionsCheck;
    private JLabel logDetailedAnalysis;
    private JCheckBox logDetailedAnalysisCheck;
    private JLabel playBothSidesLabel;  
    private JCheckBox playBothSidesCheck;  
    private JLabel playAgainstSelf;
    private JCheckBox playAgainstSelfCheck;
    private HashMap<String, Integer> config;

    // Buttons
    private JButton okButton;
    private JButton cancelButton;

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
        playAgainstSelfCheck = new JCheckBox();
        playBothSidesLabel = new JLabel();
        playBothSidesCheck = new JCheckBox();
        sessionGenerationLabel = new JLabel();
        playAgainstSelf = new JLabel();
        okButton = new JButton();
        cancelButton = new JButton();
        loggingLabel = new JLabel();
        logSessionsLabel = new JLabel();
        logSessionsCheck = new JCheckBox();
        logDetailedAnalysis = new JLabel();
        logDetailedAnalysisCheck = new JCheckBox();
        
        restoreOptions(config);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Options");
        setName("optionsFrame"); // NOI18N
        setResizable(false);

        logSessionsLabel.setText("Log sessions");
        
        playBothSidesLabel.setText("Play both sides");
        playBothSidesLabel.setMaximumSize(new java.awt.Dimension(85, 25));
        playBothSidesLabel.setMinimumSize(new java.awt.Dimension(85, 25));
        playBothSidesLabel.setPreferredSize(new java.awt.Dimension(85, 25));

        sessionGenerationLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        sessionGenerationLabel.setText("Session generation");

        loggingLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        loggingLabel.setText("Logging");
        logDetailedAnalysis.setText("Log detailed analysis");
        
        playAgainstSelf.setText("Play against self");

        okButton.setText("Ok");
        okButton.setMaximumSize(new java.awt.Dimension(80, 25));
        okButton.setMinimumSize(new java.awt.Dimension(80, 25));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                config.put("playBothSides", playBothSidesCheck.isSelected() ? 1 : 0);
                config.put("playAgainstSelf", playAgainstSelfCheck.isSelected() ? 1 : 0);
                config.put("logDetailedAnalysis", logDetailedAnalysisCheck.isSelected() ? 1 : 0);
                config.put("logSessions", logSessionsCheck.isSelected() ? 1 : 0);
                dispose();
            }
        });
        
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sessionGenerationLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(logDetailedAnalysis, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(playBothSidesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(loggingLabel)
                            .addComponent(logSessionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(playAgainstSelf, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(logDetailedAnalysisCheck)
                                    .addComponent(playBothSidesCheck)
                                    .addComponent(logSessionsCheck))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(playAgainstSelfCheck)))))
                .addContainerGap(9, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sessionGenerationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(playBothSidesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playBothSidesCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(playAgainstSelf, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loggingLabel))
                    .addComponent(playAgainstSelfCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(logSessionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(logDetailedAnalysis, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(logSessionsCheck)
                        .addGap(11, 11, 11)
                        .addComponent(logDetailedAnalysisCheck)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pack();
        setVisible(true);
		return config;
    }

    
	private void restoreOptions(HashMap<String, Integer> prevConfig) {
		if (prevConfig != null) {
			playBothSidesCheck.setSelected(prevConfig.containsKey("playBothSides") && prevConfig.get("playBothSides") != 0);
			playAgainstSelfCheck.setSelected(prevConfig.containsKey("playAgainstSelf") && prevConfig.get("playAgainstSelf") != 0);
			logDetailedAnalysisCheck.setSelected(prevConfig.containsKey("logDetailedAnalysis") && prevConfig.get("logDetailedAnalysis") != 0);
			logSessionsCheck.setSelected(prevConfig.containsKey("logSessions") && prevConfig.get("logSessions") != 0);
		}
		
	}
}