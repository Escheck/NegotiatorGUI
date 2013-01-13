package negotiator.gui.boaframework;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

import negotiator.Global;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.BOAagentInfo;
import negotiator.boaframework.BOAparameter;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.gui.DirectoryRestrictedFileSystemView;
import negotiator.gui.ExtendedListModel;
import negotiator.gui.GenericFileFilter;

/**
 *
 * @author Mark Hendrikx
 */
public class BOAComponentLoader extends javax.swing.JFrame {

	private JButton addParameterButton;
    private JLabel agentClassLabel;
    private JTextField agentClassTextField;
    private JLabel defaultValueLabel;
    private JTextField defaultValueTextField;
    private JLabel descriptionLabel;
    private JTextField descriptionTextField;
    private JButton editParameterButton;
    private JSeparator lowerSeparator;
    private JButton addComponent;
    private JButton openButton;
    private JList parameterList;
    private DefaultListModel parameterListModel;
    private JScrollPane parameterListScrollPane;
    private JLabel parameterNameLabel;
    private JTextField parameterNameTextField;
    private JButton removeParameterButton;
    private JSeparator upperSeparator;
    
    /**
     * Creates new form Test
     */
    public BOAComponentLoader() {
        initComponents();
    }

    private void initComponents() {

        agentClassLabel = new JLabel("Agent class");
        agentClassTextField = new javax.swing.JTextField();
        agentClassTextField.setEditable(false);
        
        parameterNameLabel = new JLabel("Parameter name");
        parameterNameTextField = new javax.swing.JTextField();
        
        descriptionLabel = new JLabel("Description");
        descriptionTextField = new JTextField();
        
        defaultValueLabel = new JLabel("Default value");
        defaultValueTextField = new JTextField();
        
        addComponent = new JButton("Add component");
        
        removeParameterButton = new JButton("Remove parameter");
        removeParameterButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			removeParameterAction();
    		}
    	});
        
        editParameterButton = new javax.swing.JButton("Edit parameter");
        editParameterButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			editParameterAction();
    		}
    	});
        openButton = new javax.swing.JButton("Open");
        openButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			openAction();
    		}
    	});
        
        addParameterButton = new javax.swing.JButton("Add parameter");
        addParameterButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			addParameterAction();
    		}
    	});
        
        parameterListScrollPane = new JScrollPane();
        parameterList = new javax.swing.JList();
        parameterList.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
    		  	JList list = (JList) e.getSource();
    	  		if ((list.getValueIsAdjusting())) {
    	  			BOAparameter param = (BOAparameter) list.getSelectedValue();
    	  			if (param != null) {
	    	  			parameterNameTextField.setText(param.getName());
	    	  			descriptionTextField.setText(param.getDescription());
	    	  			defaultValueTextField.setText(param.getHigh().toString());
    	  			}
    	  		}
        	}
        });
        
        parameterListModel = new DefaultListModel();
        parameterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        parameterList.setModel(parameterListModel);
        
        upperSeparator = new javax.swing.JSeparator();
        lowerSeparator = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        
        parameterListScrollPane.setViewportView(parameterList);


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(upperSeparator)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(agentClassLabel)
                        .addGap(18, 18, 18)
                        .addComponent(agentClassTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(parameterNameLabel)
                                .addGap(18, 18, 18)
                                .addComponent(parameterNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(defaultValueLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(defaultValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(descriptionLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(descriptionTextField))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(parameterListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(addParameterButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeParameterButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editParameterButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(addComponent)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(lowerSeparator)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(agentClassLabel)
                    .addComponent(agentClassTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(openButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(upperSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(parameterNameLabel)
                    .addComponent(parameterNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(descriptionLabel)
                    .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(defaultValueLabel)
                    .addComponent(defaultValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeParameterButton)
                    .addComponent(editParameterButton)
                    .addComponent(addParameterButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameterListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lowerSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addComponent))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        this.setTitle("Add BOA component");
        pack();
    }

    private void removeParameterAction() {
    	if (parameterList.getSelectedIndex() >= 0) {
    		parameterListModel.remove(parameterList.getSelectedIndex());
    	}
    }
    
    private void editParameterAction() {
    	removeParameterAction();
    	addParameterAction();
    }
    
    private void addParameterAction() {
    	boolean valid = true;
    	String paramName = parameterNameTextField.getText();
    	if (paramName.length() == 0 || paramName.length() > 12) {
    		valid = false;
    		JOptionPane.showMessageDialog(null, "Parameter name length should be non-empty and at most 12 characters.", "Invalid parameter input", 0);
    	}
    	BigDecimal defaultValue = null;
    	try {
    		defaultValue = new BigDecimal(defaultValueTextField.getText());
    	} catch (NumberFormatException e) {
    		valid = false;
    		JOptionPane.showMessageDialog(null, "Invalid default value", "Invalid parameter input", 0);
    	}
    	String description = descriptionTextField.getText();
    	if (description.length() > 60) {
    		valid = false;
    		JOptionPane.showMessageDialog(null, "Parameter description length should be atmost 60 characters.", "Invalid parameter input", 0);
    	}
    	if (valid) {
    		BOAparameter boaParam = new BOAparameter(paramName, defaultValue, description);
    		parameterListModel.addElement(boaParam);
    	}
    }
    
    private void openAction() {
		// Get the root of Genius
		String root = Global.getBinaryRoot();
		
		// Restrict file picker to root and subdirectories.
		// Ok, you can escape if you put in a path as directory. We catch this later on.
		FileSystemView fsv = new DirectoryRestrictedFileSystemView(new File(root));
		JFileChooser fc = new JFileChooser(fsv.getHomeDirectory(), fsv);
		
		// Filter such that only directories and .class files are shown.
		FileFilter filter = new GenericFileFilter("class", "Java class files (.class)");
		fc.setFileFilter(filter);
		
		// Open the file picker
		int returnVal = fc.showOpenDialog(null);
		
		// If file selected
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            // Catch people who tried to escape our directory
            if (!file.getPath().startsWith(root)) {
            	JOptionPane.showMessageDialog(null, "Only components in the root or a subdirectory of the root are allowed.", "Component import error", 0);
            } else {
            	// Get the relative path of the agent class file
	            String relativePath = file.getPath().substring(root.length(), file.getPath().length() - 6);
	            
	            // Convert path to agent path as in XML file
	            relativePath = relativePath.replace(File.separatorChar + "", ".");
	            agentClassTextField.setText(relativePath);
            }
        }
	}
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BOAComponentLoader().setVisible(true);
            }
        });
    }
}