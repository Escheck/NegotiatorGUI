package negotiator.gui.tournamentvars;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.*;

import javax.swing.JPanel;
import java.awt.Panel;
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

import negotiator.gui.DefaultOKCancelDialog;
/**
 * This is a UI for editing a profile variable.
 * @author wouter
 *
 */

class ProfileVarUI extends DefaultOKCancelDialog {
	
	ArrayList<MyCheckBox> checkboxes; // copy of what's in the panel, for easy check-out

	/**
	 * Ask user which profiles he wants to be used. 
	 * TODO copy old selection into the new checkboxes.
	 * TODO force selection of exactly ONE checkbox.
	 * 
	 * @param v is the profilevariable under editing
	 * @param owner is used to place the dialog properly over the owner's window.
	 * @throws if domain repository has a problem
	 */
	public ProfileVarUI(JFrame owner)  throws Exception {
		super(owner,"Profile Selector GUI"); // modal dialog.
	}
	
	public Panel getPanel() {		
		Panel agentlist=new Panel();
		try {
			checkboxes=new ArrayList<MyCheckBox>(); // static initialization does NOT WORK now as getPanel is part of constructor!!
			Repository domainrep=Repository.get_domain_repos();
			agentlist.setLayout(new BoxLayout(agentlist,BoxLayout.Y_AXIS));
			for (RepItem domain :domainrep.getItems()) {
				for (ProfileRepItem profile: ((DomainRepItem)domain).getProfiles()) {
					MyCheckBox cbox=new MyCheckBox(profile);
					checkboxes.add(cbox);
					agentlist.add(cbox);
				}
			}
		} catch (Exception e) {
			new Warning("creation of content panel failed: "+e); e.printStackTrace();
		}
		return agentlist;
	}

	public ArrayList<ProfileRepItem> ok() {
		ArrayList<ProfileRepItem> result=new ArrayList<ProfileRepItem>();
		for (MyCheckBox cbox: checkboxes) {
			if (cbox.isSelected()) result.add(cbox.profileRepItem);
		}
		return result;
	}		
}

class MyCheckBox extends JCheckBox {
	public ProfileRepItem profileRepItem;
	public MyCheckBox(ProfileRepItem profileitem) { 
		super(""+profileitem.getURL()); 
		profileRepItem=profileitem;
	}
}