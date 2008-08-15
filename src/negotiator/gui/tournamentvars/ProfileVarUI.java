package negotiator.gui.tournamentvars;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.*;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;

import javax.swing.BoxLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JButton;

import negotiator.Domain;
import negotiator.repository.*;
import negotiator.utility.UtilitySpace;

import javax.swing.JFileChooser;
import java.io.FileFilter;

import java.util.ArrayList;
import negotiator.exceptions.Warning;
import negotiator.gui.agentrepository.AgentRepositoryUI;
import negotiator.gui.domainrepository.DomainRepositoryUI;
import negotiator.gui.tree.TreeFrame;
import negotiator.issue.Objective;
import negotiator.tournament.Tournament;
import negotiator.tournament.VariablesAndValues.*;
import negotiator.repository.*;
import javax.swing.JCheckBox;
/**
 * This is a UI for editing a profile variable.
 * @author wouter
 *
 */

class ProfileVarUI extends JDialog {
	
	JButton okbutton=new JButton("OK");
	JButton cancelbutton=new JButton("Cancel");
	ArrayList<MyCheckBox> checkboxes=new ArrayList<MyCheckBox>(); // copy of what's in the panel, for easy check-out

	ArrayList<ProfileRepItem> result=null; // result after user has selected and pressed OK. Stays null if user presses cancel.
	/**
	 * Ask user which profiles he wants to be used. 
	 * TODO copy old selection into the new checkboxes.
	 * TODO force selection of exactly ONE checkbox.
	 * 
	 * @param v is the profilevariable under editing
	 * @param owner is used to place the dialog properly over the owner's window.
	 * @throws if domain repository has a problem
	 */
	public ProfileVarUI(ProfileVariable v,JFrame owner) throws Exception {
		super(owner,"Profile Selector GUI",true); // modal dialog.
		getContentPane().setLayout(new BorderLayout());

			
		 // actionlisteners MUST be added before putting buttons in panel!
		okbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				System.out.println("OK pressed");
				ok();
				dispose();
			}
		});
		
		cancelbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				System.out.println("cancel pressed");
				dispose();
			}
		});
		
		/* create list of all profiles and make combo boxes for each */

		Repository domainrep=Repository.get_domain_repos();
		
		JPanel profileslist=new JPanel();
		profileslist.setLayout(new BoxLayout(profileslist,BoxLayout.Y_AXIS));
		for (RepItem domain :domainrep.getItems()) {
			for (ProfileRepItem profile: ((DomainRepItem)domain).getProfiles()) {
				MyCheckBox cbox=new MyCheckBox(profile);
				checkboxes.add(cbox);
				profileslist.add(cbox);
			}
		}
		add(new JScrollPane(profileslist),BorderLayout.CENTER);

		JPanel buttonrow=new JPanel(new BorderLayout());
		buttonrow.add(okbutton,BorderLayout.WEST);
		buttonrow.add(cancelbutton,BorderLayout.EAST);

		add(buttonrow,BorderLayout.SOUTH);

		pack();
		setVisible(true);
		
	}

	public void ok() {
		result=new ArrayList<ProfileRepItem>();
		for (MyCheckBox cbox: checkboxes) {
			if (cbox.isSelected()) result.add(cbox.profileRepItem);
		}
	}
	
		
	public ArrayList<ProfileRepItem> getResult() { return result; }
}

class MyCheckBox extends JCheckBox {
	public ProfileRepItem profileRepItem;
	public MyCheckBox(ProfileRepItem profileitem) { 
		super(""+profileitem.getURL()); 
		profileRepItem=profileitem;
	}
}