package negotiator.gui.boaframework;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import negotiator.boaframework.BOAparameter;

/**
 * The form used to input a parameter in the DecoupledAgentsFrame GUI.
 * The GUI was originally created using Netbeans, and is therefore probably
 * still compatible with their form editor.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 11-12-11
 */
public class ParameterInput extends JDialog {

	private static final long serialVersionUID = -7926588728259592459L;
	private JLabel paramNameLabel;
    private JLabel valueRangeLabel;
    private JLabel lowLabel;
    private JLabel highLabel;
    private JLabel stepLabel;
    private JTextField paramNameTextField;
    private JTextField lowTextField;
    private JTextField highTextField;
    private JTextField stepTextField;
    private JButton okButton;
    private JButton cancelButton;
    private JSeparator seperator;
    private BOAparameter result = null;
    
    public ParameterInput(Frame frame, String title) {
    	super(frame, title, true);
    	this.setLocation(frame.getLocation().x + frame.getWidth() / 3, frame.getLocation().y + frame.getHeight() / 3);
    }

    /**
     * Inits the full GUI. A result-object is only returned when the textfield 
     * input resulted in the creation of a valid DecoupledParameters-object.
     * 
     * @return valid parameter specification
     */
    public BOAparameter getResult() {
    	initFrameGUI();
    	initParamNameGUI();
    	initValueRangeGUI();
    	initButtonsGUI();
    	initControls();
        pack();
        setVisible(true);
        return result;
    }
    
    /**
     * Adds an action to the cancel- and okbutton. The action for the okbutton 
     * includes an extensive validation of the input. The try-catch tree is
     * necessary to ensure that only a single error message is thrown.
     * A parameter can have a single value, or a range of values.
     */
    private void initControls() {
    	okButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			String name = paramNameTextField.getText();
    			
    			// Check if the parameter has a name
    			if (name.equals("")) {
    				JOptionPane.showMessageDialog(null, "The parameter should have a name.", "Parameter input error", 0);
    				result = null;
    			} else {
	    			double low = 0, high = 0, step = 0;
	    			try {
	    				// check if the low value can be parsed as a valid double
	    				low = Double.parseDouble(lowTextField.getText());
	    				// if only the low value was filled in, then this was a parameter with a single value
	    				if (highTextField.getText().equals("") && stepTextField.getText().equals("")) {
	        				result = (new BOAparameter(name, low, low, 1));
	        			} else {
		    				try {
		    					// check if the high value can be parsed as a valid double
		        				high = Double.parseDouble(highTextField.getText());
		        				try {
		        					// check if the step value can be parsed as a valid double
		            				step = Double.parseDouble(stepTextField.getText());
		            				try {
		            					// check that the inputed range exists
		                				if (high >= low && step > 0) {
		                    				result = new BOAparameter(name, low, high, step);
		                    			} else {
		                    				JOptionPane.showMessageDialog(null, "High should be higher or equal to low and step should be positive.", "Parameter input error", 0);
		                    			}
		                			} catch (NumberFormatException error) {
		                				JOptionPane.showMessageDialog(null, "The value for step should be a number.", "Parameter input error", 0);
		                			}
		                			
		            			} catch (NumberFormatException error) {
		            				JOptionPane.showMessageDialog(null, "The value for step should be a number.", "Parameter input error", 0);
		            			}
		        			} catch (NumberFormatException error) {
		        				JOptionPane.showMessageDialog(null, "The value for high should be a number.", "Parameter input error", 0);
		        			}
	        			}
	    			} catch (NumberFormatException error) {
	    				JOptionPane.showMessageDialog(null, "The value for low should be a number.", "Parameter input error", 0);
	    			}
    			}
    			// if a valid parameter was inputed
    			if (result != null) {
    				dispose();
    			}
    		}
    	});

    	cancelButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			dispose();
    		}
    	});
	}

	private void initValueRangeGUI() {
    	paramNameLabel = new JLabel("Parameter name");
        paramNameTextField = new JTextField();
        getContentPane().add(paramNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 16, -1, -1));
        getContentPane().add(paramNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 16, 214, -1));
	}

	private void initParamNameGUI() {
		valueRangeLabel = new JLabel("Value range: if one parameter only fill in low");
		getContentPane().add(valueRangeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 48, -1, -1));
		
        lowLabel = new JLabel("Low");
        getContentPane().add(lowLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 74, -1, -1));
        
        lowTextField = new JTextField();
        getContentPane().add(lowTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 74, 70, -1));
        
        highLabel = new JLabel("High");
        getContentPane().add(highLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(121, 74, -1, -1));

        highTextField = new JTextField();
        getContentPane().add(highTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(151, 74, 70, -1));

        stepLabel = new JLabel("Step");
        getContentPane().add(stepLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(232, 74, -1, -1));

        stepTextField = new JTextField();
        getContentPane().add(stepTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(263, 74, 70, -1));	
	}

	private void initButtonsGUI() {
		okButton = new JButton("Ok");
        getContentPane().add(okButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 80, -1));

        cancelButton = new JButton("Cancel");
        getContentPane().add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 80, -1));

        seperator = new JSeparator();
        getContentPane().add(seperator, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 350, -1));
	}
	
	private void initFrameGUI() {
    	setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
    }
}