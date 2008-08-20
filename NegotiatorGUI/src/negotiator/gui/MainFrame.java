/*
 * MainFrame.java
 *
 * Created on November 6, 2006, 3:58 PM
 * Wouter: this file was once created using a GUI editor.
 * But now we edit it manually.
 * 
 * TODO make the window resizable. Size is too small on Mac and button texts do not fit.
 */

package negotiator.gui;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.JCheckBox;

import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

import negotiator.Main;
import negotiator.gui.tree.*;
import negotiator.NegotiationManager;
import negotiator.NegotiationTemplate;

import java.net.URL;
/**
 *
 * @author  dmytro
 * @author W.Pasman (modifications 7nov2007)
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
    private javax.swing.JCheckBox agentAStarts; 
    	// Warning, if you use awt.CheckBox the checkbox will not appear for some time...
     
    
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
     * Wouter: this was once generated with a Form Editor.
     * But such an editor does not exist in Eclipse and we have to move on.
     * So currently this is hand-edited.
     */
    private void initComponents() {
    	
    	int COL1WIDTH=150;
    	jPanel1=new JPanel();
    	jPanel1.setLayout(new BoxLayout(jPanel1,BoxLayout.Y_AXIS));// vertical flow is the basis for this panel.
    	
    	/**************create top panel with 8 rows of text fields and buttons ****************/
    	//row 1. Nego Template/domain file
        JPanel row1 = new JPanel(new BorderLayout()); 
        jLabel10 = new JLabel("Negotiation template");
        jLabel10.setPreferredSize(new Dimension(COL1WIDTH, 10));
        fieldNegotiationTemplate = new javax.swing.JTextField();
        buttonBrowseNegotiationTemplate = new javax.swing.JButton("Browse");
        row1.setLayout(new BorderLayout());
        row1.add(jLabel10,"West");
        row1.add(fieldNegotiationTemplate, "Center");
        buttonBrowseNegotiationTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseNegotiationTemplateActionPerformed(evt);
            }
        });
        row1.add(buttonBrowseNegotiationTemplate, "East");
        jPanel1.add(row1);

        // row 2: #sessions
        JPanel row2=new JPanel(new BorderLayout());        
        jLabel1 = new javax.swing.JLabel("Number of sessions");
        jLabel1.setPreferredSize(new Dimension(COL1WIDTH, 10));
        fieldNumberOfSession = new javax.swing.JTextField("5");
        fieldNumberOfSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldNumberOfSessionActionPerformed(evt);
            }
        }); // Whast's this for?????
        row2.add(jLabel1, "West");
        row2.add(fieldNumberOfSession, "Center");
        jPanel1.add(row2);

        // row 3: Agent A cclass name
        JPanel row3=new JPanel(new BorderLayout());
        fieldAgentAClassName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel("Agent A class name");
        jLabel3.setPreferredSize(new Dimension(COL1WIDTH, 10));
        row3.add(jLabel3, "West");
        row3.add(fieldAgentAClassName, "Center");
        jPanel1.add(row3);

        // row 4: Agent A name
        JPanel row4=new JPanel(new BorderLayout());
        fieldAgentAName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel("Agent A name");
        jLabel8.setPreferredSize(new Dimension(COL1WIDTH, 10));
        row4.add(jLabel8, "West");
        row4.add(fieldAgentAName, "Center");
        jPanel1.add(row4);

        // row 5: Agent A utility space
        JPanel row5=new JPanel(new BorderLayout());
        fieldAgentAUtilitySpace = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel("Agent A utility space");
        jLabel4.setPreferredSize(new Dimension(COL1WIDTH, 10));
        buttonBrowseAgentAUtilitySpace = new javax.swing.JButton("Browse");
        buttonBrowseAgentAUtilitySpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseAgentAUtilitySpaceActionPerformed(evt);
            }
        });
        row5.add(jLabel4, "West");
        row5.add(fieldAgentAUtilitySpace, "Center");
        row5.add(buttonBrowseAgentAUtilitySpace,"East");
        jPanel1.add(row5);
        
        // row 6: Agent B class name
        JPanel row6=new JPanel(new BorderLayout());
        fieldAgentBClassName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel("Agent B class name");
        jLabel6.setPreferredSize(new Dimension(COL1WIDTH, 10));
        row6.add(jLabel6,"West");
        row6.add(fieldAgentBClassName,"Center");
        jPanel1.add(row6);
        
        // row 7: Agent B name
        JPanel row7=new JPanel(new BorderLayout());
        fieldAgentBName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel("Agent B name");
        jLabel9.setPreferredSize(new Dimension(COL1WIDTH, 10));
        fieldAgentBName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldAgentBNameActionPerformed(evt);
            }
        }); // Wouter: what's the purpose of this???
        row7.add(jLabel9, "West");
        row7.add(fieldAgentBName, "Center");
        jPanel1.add(row7);

        // row 8: Agent B util space
        JPanel row8=new JPanel(new BorderLayout());
        jLabel7 = new javax.swing.JLabel("Agent B utility space");
        jLabel7.setPreferredSize(new Dimension(COL1WIDTH, 10));
        fieldAgentBUtilitySpace = new javax.swing.JTextField();
        buttonBrowseAgentBUtilitySpace = new javax.swing.JButton("Browse");
        buttonBrowseAgentBUtilitySpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseAgentBUtilitySpaceActionPerformed(evt);
            }
        });
        row8.add(jLabel7, "West");
        row8.add(fieldAgentBUtilitySpace, "Center");
        row8.add(buttonBrowseAgentBUtilitySpace, "East");
        jPanel1.add(row8);

        
       /**************create center panel ************/        
        
        jScrollPane1 = new javax.swing.JScrollPane();
        textOutput = new javax.swing.JTextArea();
        textOutput.setColumns(20);
        textOutput.setLineWrap(true);
        textOutput.setRows(5);
        jScrollPane1.setViewportView(textOutput);

        
        
        /************** create bottom button panel ******************/
        jPanel2 = new javax.swing.JPanel(new FlowLayout());
        agentAStarts=new JCheckBox("Agent A starts negotiation",false);
        buttonStart = new javax.swing.JButton("Start");
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });
        buttonStop = new javax.swing.JButton("Stop");
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });
        buttonShowModel = new javax.swing.JButton("Show Model");
        buttonShowModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowModelActionPerformed(evt);
            }
        });
        
        jPanel2.add(agentAStarts);
        jPanel2.add(buttonStart);
        jPanel2.add(buttonStop);
        jPanel2.add(buttonShowModel);
        
        
         /**************fill the main pane **************/
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Negotiator ");
        setPreferredSize(new java.awt.Dimension(640, 400));
               
        // MAIN PANEL LAYOUT
        getContentPane().add(jPanel2, "South");
        getContentPane().add(jPanel1, "North");
        getContentPane().add(jScrollPane1, "Center");

        pack();
    }
    
    
    
    private void buttonShowModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowModelActionPerformed
    	// TODO At the moment it is hardcoded that the utilitySpace from Agent A is used in the GUI. Change this later (maybe 2 separate buttons?)
    	//Temporarily just create a TreePanel with an empty domain and utilitySpace, because reading the utilitySpaces
    	//doesn't work properly yet (Herbert is working on that)
    	//Wouter: quick hack: set nego time to 0.
    	// I don't see why the showModel needs a nego template at all, showModel is about editing the
    	// datastructures, not the negotiation template?
    	try {
    		NegotiationTemplate template = 
    			new NegotiationTemplate(fieldNegotiationTemplate.getText(),fieldAgentAUtilitySpace.getText(),fieldAgentBUtilitySpace.getText(),0);
    		TreeFrame treeFrame = new TreeFrame(template.getDomain(), template.getAgentAUtilitySpace());
		
    		treeFrame.pack();
    		treeFrame.setVisible(true);
    	}
    	catch (Exception err)
    	{
    		JOptionPane.showMessageDialog(this, "ShowModel failed:"+err.getMessage());
    		err.printStackTrace();
    	}
    }
    
    private void buttonBrowseNegotiationTemplateActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_buttonBrowseNegotiationTemplateActionPerformed

        browseAgentUtilitySpace(fieldNegotiationTemplate);
        try { NegotiationTemplate.loadParamsFromFile(fieldNegotiationTemplate.getText(), this); }
        catch (Exception e)
        {			JOptionPane.showMessageDialog(this, e.getMessage());      }
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
    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {
    	try
    	{
		    Main.nm = new NegotiationManager(
		                    fieldAgentAClassName.getText(),
		                    fieldAgentAName.getText(),
		                    fieldAgentAUtilitySpace.getText(),
		                    fieldAgentBClassName.getText(), 
		                    fieldAgentBName.getText(),
		                    fieldAgentBUtilitySpace.getText(),
		                    fieldNegotiationTemplate.getText(), 
		                    Integer.valueOf(fieldNumberOfSession.getText()),
		                    agentAStarts.isSelected());
		   negoManagerThread = new Thread(Main.nm);
		   
		   
		   negoManagerThread.start();
    	}
    	catch (Exception err)
    	{
    		JOptionPane.showMessageDialog(this, "ShowModel failed:"+err.getMessage());
    		err.printStackTrace();
    	}

    }

    private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {
    	if (Main.nm!=null) Main.nm.stopNegotiation();
        if (negoManagerThread!=null) negoManagerThread.stop();
        Main.log("All sessions have been stoped!!!");
    }

    private void fieldNumberOfSessionActionPerformed(java.awt.event.ActionEvent evt) {
    	
    }


    public javax.swing.JTextArea getOutputArea() {
        return textOutput;
    }        
    public javax.swing.JButton getButtonStart() {
        return buttonStart;
    }
}
