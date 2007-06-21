/*
 * MainFrame.java
 *
 * Created on November 6, 2006, 3:58 PM
 */

package negotiator.gui;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import negotiator.Main;
import negotiator.NegotiationManager;
import negotiator.NegotiationTemplate;

import java.net.URL;
/**
 *
 * @author  dmytro
 */
public class MainFrame extends javax.swing.JFrame {
    Thread negoManagerThread;
    /** Creates new form MainFrame */
    public MainFrame(String[] args) {
        super();
        initComponents();
        if(args.length>=1) {
            if(args.length>=2){
                if(args[1].equals("-b")) Main.batchMode = true;
            }            
            fieldNegotiationTemplate.setText(args[0]);
            try {
                NegotiationTemplate.loadParamsFromFile(args[0], this);
            } catch (Exception e) {
                // we did not succedd in loading the agents and spaces
                e.printStackTrace();                
                if (Main.batchMode) System.exit(1);
            }
        }
    }
    public void setNemberOfSessions(String value) {
        fieldNumberOfSession.setText(value);
    }
    public void setAgentAClassName(String value) {
        fieldAgentAClassName.setText(value);
    }
    public void setAgentBClassName(String value) {
        fieldAgentBClassName.setText(value);
    }
    public void setAgentAName(String value) {
        fieldAgentAName.setText(value);
    }
    public void setAgentBName(String value) {
        fieldAgentBName.setText(value);
    }
    public void setAgentAUtilitySpace(String value) {
        fieldAgentAUtilitySpace.setText(value);
    }
    public void setAgentBUtilitySpace(String value) {
        fieldAgentBUtilitySpace.setText(value);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        fieldNegotiationTemplate = new javax.swing.JTextField();
        buttonBrowseNegotiationTemplate = new javax.swing.JButton();
        fieldNumberOfSession = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        fieldAgentAClassName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        fieldAgentAName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        fieldAgentAUtilitySpace = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        fieldAgentBClassName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        buttonBrowseAgentBUtilitySpace = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        fieldAgentBUtilitySpace = new javax.swing.JTextField();
        fieldAgentBName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        buttonBrowseAgentAUtilitySpace = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textOutput = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        buttonStart = new javax.swing.JButton();
        buttonStop = new javax.swing.JButton();
        buttonShowModel = new javax.swing.JButton();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Negotiator ");
        setMinimumSize(new java.awt.Dimension(400, 340));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setText("Negotiation template");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 120, -1));

        jPanel1.add(fieldNegotiationTemplate, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 180, 20));

        buttonBrowseNegotiationTemplate.setText("Browse");
        buttonBrowseNegotiationTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseNegotiationTemplateActionPerformed(evt);
            }
        });

        jPanel1.add(buttonBrowseNegotiationTemplate, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, -1, -1));

        fieldNumberOfSession.setText("5");
        fieldNumberOfSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldNumberOfSessionActionPerformed(evt);
            }
        });

        jPanel1.add(fieldNumberOfSession, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 180, 20));

        jLabel1.setText("Number of sessions");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 120, -1));

        jPanel1.add(fieldAgentAClassName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 180, 20));

        jLabel3.setText("Agent A class name");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 120, -1));

        jPanel1.add(fieldAgentAName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 180, 20));

        jLabel8.setText("Agent A name");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 120, -1));

        jPanel1.add(fieldAgentAUtilitySpace, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 130, 180, 20));

        jLabel4.setText("Agent A utility space");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 120, -1));

        jPanel1.add(fieldAgentBClassName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 160, 180, 20));

        jLabel6.setText("Agent B class name");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 120, -1));

        buttonBrowseAgentBUtilitySpace.setText("Browse");
        buttonBrowseAgentBUtilitySpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseAgentBUtilitySpaceActionPerformed(evt);
            }
        });

        jPanel1.add(buttonBrowseAgentBUtilitySpace, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 220, -1, -1));

        jLabel7.setText("Agent B utility space");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 120, -1));

        jPanel1.add(fieldAgentBUtilitySpace, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, 180, 20));

        fieldAgentBName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldAgentBNameActionPerformed(evt);
            }
        });

        jPanel1.add(fieldAgentBName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, 180, 20));

        jLabel9.setText("Agent B name");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 120, -1));

        buttonBrowseAgentAUtilitySpace.setText("Browse");
        buttonBrowseAgentAUtilitySpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseAgentAUtilitySpaceActionPerformed(evt);
            }
        });

        jPanel1.add(buttonBrowseAgentAUtilitySpace, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 130, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 410, 250));

        textOutput.setColumns(20);
        textOutput.setLineWrap(true);
        textOutput.setRows(5);
        jScrollPane1.setViewportView(textOutput);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 250, 410, 210));

        jPanel2.setLayout(null);

        buttonStart.setText("Start");
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        jPanel2.add(buttonStart);
        buttonStart.setBounds(10, 10, 70, 23);

        buttonStop.setText("Stop");
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        jPanel2.add(buttonStop);
        buttonStop.setBounds(100, 10, 70, 23);

        buttonShowModel.setText("Show Model");
        buttonShowModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowModelActionPerformed(evt);
            }
        });

        jPanel2.add(buttonShowModel);
        buttonShowModel.setBounds(300, 10, 100, 23);

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 460, 410, 50));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonShowModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowModelActionPerformed
// TODO Show the model GUI here!
    }//GEN-LAST:event_buttonShowModelActionPerformed

    private void buttonBrowseNegotiationTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseNegotiationTemplateActionPerformed

        browseAgentUtilitySpace(fieldNegotiationTemplate);
        NegotiationTemplate.loadParamsFromFile(fieldNegotiationTemplate.getText(), this);
    }//GEN-LAST:event_buttonBrowseNegotiationTemplateActionPerformed

    private void fieldAgentBNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldAgentBNameActionPerformed

    }//GEN-LAST:event_fieldAgentBNameActionPerformed

    private void buttonBrowseAgentBUtilitySpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseAgentBUtilitySpaceActionPerformed

        browseAgentUtilitySpace(fieldAgentBUtilitySpace);
    }//GEN-LAST:event_buttonBrowseAgentBUtilitySpaceActionPerformed

    private void buttonBrowseAgentAUtilitySpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseAgentAUtilitySpaceActionPerformed

        browseAgentUtilitySpace(fieldAgentAUtilitySpace);
    }//GEN-LAST:event_buttonBrowseAgentAUtilitySpaceActionPerformed
    /** handles file browsing for the agent A/B utility space **/
    private void browseAgentUtilitySpace(javax.swing.JTextField output) {
        JFileChooser fileChooser = new JFileChooser();
        if( fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            output.setText(fileChooser.getSelectedFile().toString());
        }
    }
    /** handles file browsing for the agent A/B classes **/
    private void browseAgentClass(javax.swing.JTextField output) {
        JFileChooser fileChooser = new JFileChooser();
        if( fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            output.setText(fileChooser.getSelectedFile().toURI().toString() );
        }
    }
    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        Main.nm = new NegotiationManager(
                        fieldAgentAClassName.getText(),
                        fieldAgentAName.getText(),
                        fieldAgentAUtilitySpace.getText(),
                        fieldAgentBClassName.getText(), 
                        fieldAgentBName.getText(),
                        fieldAgentBUtilitySpace.getText(),
                        fieldNegotiationTemplate.getText(), 
                        Integer.valueOf(fieldNumberOfSession.getText()));
       negoManagerThread = new Thread(Main.nm);
       negoManagerThread.start();

    }//GEN-LAST:event_buttonStartActionPerformed

    private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
        if (Main.nm!=null) Main.nm.stopNegotiation();
        if (negoManagerThread!=null) negoManagerThread.stop();
        Main.logger.add("All sessions have been stoped!!!");
    }//GEN-LAST:event_buttonStopActionPerformed

    private void fieldNumberOfSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldNumberOfSessionActionPerformed

    }//GEN-LAST:event_fieldNumberOfSessionActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBrowseAgentAUtilitySpace;
    private javax.swing.JButton buttonBrowseAgentBUtilitySpace;
    private javax.swing.JButton buttonBrowseNegotiationTemplate;
    private javax.swing.JButton buttonShowModel;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JTextField fieldAgentAClassName;
    private javax.swing.JTextField fieldAgentAName;
    private javax.swing.JTextField fieldAgentAUtilitySpace;
    private javax.swing.JTextField fieldAgentBClassName;
    private javax.swing.JTextField fieldAgentBName;
    private javax.swing.JTextField fieldAgentBUtilitySpace;
    private javax.swing.JTextField fieldNegotiationTemplate;
    private javax.swing.JTextField fieldNumberOfSession;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textOutput;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JTextArea getOutputArea() {
        return textOutput;
    }        
    public javax.swing.JButton getButtonStart() {
        return buttonStart;
    }
}
