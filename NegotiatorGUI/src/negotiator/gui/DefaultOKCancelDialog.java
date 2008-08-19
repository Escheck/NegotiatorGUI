package negotiator.gui;

import java.util.ArrayList;

import javax.swing.JButton;
import java.awt.Panel;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import negotiator.repository.ProfileRepItem;


/**
 * open a modal OK/Cancel dialog. 
 * you must implement ok() and getPanel() to make this working.
 * 
 * @author wouter
 *
 */
public abstract class DefaultOKCancelDialog extends JDialog {
	JButton okbutton=new JButton("OK");
	JButton cancelbutton=new JButton("Cancel");
	Object the_result=null; // will be set after ok button is pressed. null in other cases (eg cancel)

	/** 
	 * 
	 * @param owner is the parent frame, used only to center the dialog properly. Probably can be null
	 * @param title the title of the dialog
	 */
	public DefaultOKCancelDialog(Frame owner, String title) {
		super(owner,title,true); // modal dialog.
		getContentPane().setLayout(new BorderLayout());
		
		 // actionlisteners MUST be added before putting buttons in panel!
		okbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				//System.out.println("OK pressed");
				the_result=ok();
				dispose();
			}
		});
		
		cancelbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				//System.out.println("cancel pressed");
				dispose();
			}
		});
		
		
		
		Panel buttonrow=new Panel(new BorderLayout());
		buttonrow.add(okbutton,BorderLayout.WEST);
		buttonrow.add(cancelbutton,BorderLayout.EAST);

		add(buttonrow,BorderLayout.SOUTH);
		
		add(getPanel(),BorderLayout.CENTER);

		pack();
		setVisible(true);
	}

	/** this function computes the result of the dialog.
	 * You may return null if the user entered illegal choices or somehow cancelled the dialog.
	 * This function will only be called when user presses OK button, 
	 * which also finishes the dialog and closes the dialog window.
	 */
	public abstract Object ok();
	
	/**
	 * this fucnction returns the actual contents for the dialog panel
	 * I implemented this as a function, because the frame contents need to be determined in the constructor function.
	 * However, the first thing *you* need to do in the constructor is to call the constructor of this
	 * DefaultOKCancelDialog, leaving you no chance to compute the needed panel.
	 * Therefore we call back on your function after the construction of this root panel.
	 * NOTE your class constructor will NOT have been finished when this function is called!!!
	 * We recommend to leave your constructor empty, apart from the call to the constructor of superclass
	 * @return a Panel containing the actual dialog contents.
	 * 
	 */
	public abstract Panel getPanel();
	
	public Object getResult() { return the_result; }
}