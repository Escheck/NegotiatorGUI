package negotiator.gui.negosession;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JFrame;

//import negotiator.ActionEventListener;
import negotiator.Logger;
import negotiator.NegotiationEventListener;

import negotiator.events.LogMessageEvent;
import negotiator.exceptions.Warning;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import negotiator.repository.*;

import java.util.ArrayList;
import negotiator.tournament.NegotiationSession2;

import negotiator.gui.progress.*;

/**
 * This shows a GUI enabling you to edit all settings for a nego sessino.
 * @author wouter
 *
 */
public class NegoSessionUI {
	
	JButton startbutton=new JButton("Start Negotiation");
	JComboBox profileA,profileB; // profiles of the agents.
	JComboBox agentAselection, agentBselection; // selection of the agent to represent A and B.
	Logger logger;
	JFrame frame;
	public NegoSessionUI(JFrame pFrame) throws Exception {
		this.frame = pFrame;
		initFrame();
	}
	public NegoSessionUI() throws Exception {
		frame = new JFrame();
		initFrame();
	}
	private void initFrame() throws Exception {
		frame.getContentPane().setLayout(new GridLayout(0,2)); 
		
		startbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { start();	}
				catch (Exception err) { new Warning("start failed: ",err); }
			}
		});
		
		frame.add(new JLabel("Protocol")); // Don't use the awt.Label, its height shows incorrect and renders incorrect within Swing.
		frame.add(new JComboBox(new String[] {"alternating turns"}));
		
		Repository agent_rep=Repository.get_agent_repository();
		ArrayList<AgentComboBoxItem> agentslist=new ArrayList<AgentComboBoxItem>();
		for (RepItem agt: agent_rep.getItems()) {
			agentslist.add(new AgentComboBoxItem((AgentRepItem)agt));
		}
		ArrayList<ProfileComboBoxItem> profileslist=new ArrayList<ProfileComboBoxItem>();
		for (RepItem prof: getProfiles()) profileslist.add(new ProfileComboBoxItem((ProfileRepItem)prof));
		
		frame.add(new JSeparator(JSeparator.HORIZONTAL));
		frame.add(new JSeparator(JSeparator.HORIZONTAL));
		frame.add(new JLabel("Party A settings"));
		frame.add(new JLabel(""));		
		frame.add(new JLabel("Preferences Profile"));
		profileA=new JComboBox(profileslist.toArray());
		frame.add(profileA);
		frame.add(new JLabel("Agent Name"));
		agentAselection=new JComboBox(agentslist.toArray());
		frame.add(agentAselection);
		frame.add(new JLabel("Parameters"));
		frame.add(new JComboBox(new String[] {"1","2"}));

		frame.add(new JSeparator(JSeparator.HORIZONTAL));
		frame.add(new JSeparator(JSeparator.HORIZONTAL));
		frame.add(new JLabel("Party B settings"));
		frame.add(new JLabel(""));
		frame.add(new JLabel("Preferences Profile"));
		profileB=new JComboBox(profileslist.toArray());
		frame.add(profileB);
		frame.add(new JLabel("Agent Name"));
		agentBselection=new JComboBox(agentslist.toArray());
		frame.add(agentBselection);
		frame.add(new JLabel("Parameters"));
		frame.add(new JComboBox(new String[] {"1","2"}));
		
		frame.add(new JLabel(""));
		frame.add(startbutton);
		frame.pack();
		frame.setVisible(true);

	}
	public ArrayList<ProfileRepItem> getProfiles() throws Exception
	{
		Repository domainrep=Repository.get_domain_repos();
		ArrayList<ProfileRepItem> profiles=new ArrayList<ProfileRepItem>();
		for (RepItem domain:domainrep.getItems()) {
			if (!(domain instanceof DomainRepItem))
				throw new IllegalStateException("Found a non-DomainRepItem in domain repository:"+domain);
			for (ProfileRepItem profile:((DomainRepItem)domain).getProfiles())	profiles.add(profile);
		}
		return profiles;
	}
	
	/** TODO use the parameters. */
	public void start() throws Exception {
		
		ProfileRepItem agentAprofile=((ProfileComboBoxItem)profileA.getSelectedItem()).profile;
		if (agentAprofile==null) throw new NullPointerException("Please select a profile for agent A");
		
		ProfileRepItem agentBprofile=((ProfileComboBoxItem)profileB.getSelectedItem()).profile;
		if (agentBprofile==null) throw new NullPointerException("Please select a profile for agent B");
		
		AgentComboBoxItem agentAsel=((AgentComboBoxItem)agentAselection.getSelectedItem());
		if (agentAsel==null) throw new NullPointerException("Please select agent A");
		AgentComboBoxItem agentBsel=((AgentComboBoxItem)agentBselection.getSelectedItem());
		if (agentBsel==null) throw new NullPointerException("Please select agent B");
		
		 // determine the domain
		DomainRepItem domain=agentAprofile.getDomain();
		if (domain!=agentBprofile.getDomain())
			throw new IllegalArgumentException("profiles for agent A and B do not have the same domain. Please correct your profiles");
		
		NegotiationEventListener ael=new NegotiationEventListener() {
			public void handleActionEvent(negotiator.events.ActionEvent evt) {
				System.out.println("Caught event "+evt);
			}

			public void handleLogMessageEvent(LogMessageEvent evt) {
				// TODO Auto-generated method stub
				
			}
		};

		NegotiationEventListener graphlistener=new ProgressUI();
		
		NegotiationSession2 ns=new NegotiationSession2(agentAsel.agent, agentBsel.agent, agentAprofile, agentBprofile,
	    		"agent A", "agent B",null,null,1, 1,false,graphlistener);
		
		// java.awt.EventQueue.invokeLater(ns); // this does not work... still deadlock in swing.
		 
		Thread negosession=new Thread(ns);
		negosession.start();
	}
	
	/** run this for a demo of NegoSessionUI */
	public static void main(String[] args) 
	{
		try {
			new NegoSessionUI(); 
		} catch (Exception e) { new Warning("NegoSessionUI failed to launch: ",e); }
	}
}


/** this is to override the toString of an AgentRepItem, to show only the short name. */
class AgentComboBoxItem {
	public AgentRepItem agent;
	public AgentComboBoxItem(AgentRepItem a) {agent=a; } 
	public String toString() { return agent.getName(); }
}

/** this is to override the toString of an ProfileRepItem, to show only the short name. */
class ProfileComboBoxItem {
	public ProfileRepItem profile;
	public ProfileComboBoxItem(ProfileRepItem p) {profile=p; } 
	public String toString() { return profile.getURL().getFile(); }
}