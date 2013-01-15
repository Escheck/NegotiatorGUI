package negotiator.gui.boaframework;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import negotiator.Global;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.BOAparameter;
import negotiator.boaframework.ComponentsEnum;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.repository.BOAagentRepository;
import negotiator.boaframework.repository.BOArepItem;
import negotiator.gui.DirectoryRestrictedFileSystemView;
import negotiator.gui.GenericFileFilter;

/**
 *
 * @author Mark Hendrikx
 */
public class BOAComponentLoader extends JDialog {

	private static final long serialVersionUID = -7204112461104285605L;
	private JButton addParameterButton;
    private JLabel componentNameLabel;
    private JTextField componentNameTextField;
    private JLabel componentClassLabel;
    private JTextField componentClassTextField;
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
    private BOArepItem result = null;
    private ComponentsEnum type;
    
	public BOAComponentLoader(Frame frame, String title) {
		super(frame, title, true);
		this.setLocation(frame.getLocation().x + frame.getWidth() / 2, frame.getLocation().y + frame.getHeight() / 4);
		this.setSize(frame.getSize().width / 3, frame.getSize().height / 2);
	}

	public BOArepItem getResult() {
		componentNameLabel = new JLabel("Component name");
		componentNameTextField = new JTextField();
		
        componentClassLabel = new JLabel("Component class");
        componentClassTextField = new javax.swing.JTextField();
        componentClassTextField.setEditable(false);
        
        parameterNameLabel = new JLabel("Parameter name");
        parameterNameTextField = new javax.swing.JTextField();
        
        descriptionLabel = new JLabel("Description");
        descriptionTextField = new JTextField();
        
        defaultValueLabel = new JLabel("Default value");
        defaultValueTextField = new JTextField();
        
        addComponent = new JButton("Add component");
        addComponent.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			addComponent();
    		}
    	});
        
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
                		.addComponent(componentNameLabel)
                        .addGap(14, 14, 14)
                        .addComponent(componentNameTextField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(componentClassLabel)
                        .addGap(18, 18, 18)
                        .addComponent(componentClassTextField)
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
                	.addComponent(componentNameLabel)
                    .addComponent(componentNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                		)                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)   
              
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            		.addComponent(componentClassLabel)
                    .addComponent(componentClassTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        pack();
        setVisible(true);
        return result;
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
    
    private void addComponent() {
    	boolean valid = true;
    	if (componentNameTextField.getText().length() == 0 || componentNameTextField.getText().length() > 35) {
    		valid = false;
    		JOptionPane.showMessageDialog(null, "Component name should be non-empty and at most 35 characters.", "Invalid parameter input", 0);
    	}
    	if (componentClassTextField.getText().length() == 0) {
    		valid = false;
    		JOptionPane.showMessageDialog(null, "Please specify a class.", "Invalid parameter input", 0);
    	}
    	if (valid) {
    		String name = componentNameTextField.getText();
    		String classPath = componentClassTextField.getText();
    		BOArepItem newComponent = new BOArepItem(name, classPath, type);
    		for (int i = 0; i < parameterListModel.getSize(); i++) {
                BOAparameter item = (BOAparameter) parameterListModel.getElementAt(i);
                newComponent.addParameter(item);		
            }
    		BOAagentRepository.getInstance().addComponent(newComponent);
    		result = newComponent;
    		dispose();
    	}
    }
    
    private void addParameterAction() {
    	boolean valid = true;
    	String paramName = parameterNameTextField.getText();
    	if (paramName.length() == 0 || paramName.length() > 12) {
    		valid = false;
    		JOptionPane.showMessageDialog(null, "Parameter name should be non-empty and at most 12 characters.", "Invalid parameter input", 0);
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
            	// Get the relative path of the component class file
	            String relativePath = file.getPath().substring(root.length(), file.getPath().length() - 6);
	            
	            // Convert path to agent path as in XML file
	            relativePath = relativePath.replace(File.separatorChar + "", ".");
	            
	            boolean succes = true;
	            
	    		java.lang.ClassLoader loader = BOAComponentLoader.class.getClassLoader();
	    		try {
	    			Object object = loader.loadClass(relativePath).newInstance();
	    			if (object instanceof OfferingStrategy) {
	    				type = ComponentsEnum.BIDDINGSTRATEGY;
	    			} else if (object instanceof AcceptanceStrategy) {
	    				type = ComponentsEnum.ACCEPTANCESTRATEGY;
	    			} else if (object instanceof OpponentModel) {
	    				type = ComponentsEnum.OPPONENTMODEL;
	    			} else if (object instanceof OMStrategy) {
	    				type = ComponentsEnum.OMSTRATEGY;
	    			} else {
	    				succes = false;
	    				JOptionPane.showMessageDialog(null, "Class does not extend OfferingStrategy, AcceptanceStrategy, \n" +
	    													"OpponentModel, or OMStrategy.", "Load error", 0);
	    			}
	    		} catch (ClassNotFoundException e) {
	    			succes = false;
	    			JOptionPane.showMessageDialog(null, "No class found at " + relativePath, "Load error", 0);
	    		} catch (InstantiationException e) { // happens when object instantiated is interface or abstract
	    			succes = false;
	    			JOptionPane.showMessageDialog(null, "Class cannot be instantiated. Reasons may be that there is no constructor without arguments, " +								"or the class is abstract or an interface.", "Load error", 0);
	    		} catch (IllegalAccessException e) {
	    			succes = false;
	    			JOptionPane.showMessageDialog(null, "Missing constructor without arguments", "Load error", 0);
	    		} catch (NoClassDefFoundError e) {
	    			succes = false;
	    			JOptionPane.showMessageDialog(null, "Errors in loaded class. Most likely it is in the wrong folder relative to its package.", "Load error", 0);
	    		}
	    		
	    		if (succes) {
	    			componentClassTextField.setText(relativePath);
	    		}
            }
        }
	}
}