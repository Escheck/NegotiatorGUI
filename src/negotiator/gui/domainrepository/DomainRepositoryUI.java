package negotiator.gui.domainrepository;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.*;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JButton;
import negotiator.repository.*;
import javax.swing.JFileChooser;
import java.io.FileFilter;

import java.util.ArrayList;
import negotiator.repository.*;
import negotiator.exceptions.Warning;
import negotiator.gui.agentrepository.AgentRepositoryUI;

/**
 * A user interface to the agent repository 
 * @author wouter
 *
 */
public class DomainRepositoryUI extends JFrame
{
	JButton	adddomainbutton=new JButton("Add Domain");
	JButton	removedomainbutton=new JButton("Remove Domain");
	JButton addprofilebutton=new JButton("Add Profile");
	JButton removeprofilebutton=new JButton("Remove Profile");
	JButton editbutton=new JButton("Edit");
	

	Repository temp_domain_repos=new Repository(); // TODO locate this somewhere better
	MyTreeNode root=new MyTreeNode(null);
	JTree tree;
	DefaultTreeModel treemodel;
	
	public DomainRepositoryUI()
	{
		setTitle("Negotiation Domains and Preference Profile Repository");
		setLayout(new BorderLayout());
		temp_domain_repos.getItems().addAll(makedemorepository());
	
		 // CREATE THE BUTTONS
		JPanel buttons=new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.Y_AXIS));
		adddomainbutton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				adddomain(); 
			}
		});
		removedomainbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { removedomain(); }
				catch (Exception err)  { new Warning("remove failed:"+err);}
			}
		});
		buttons.add(adddomainbutton);
		buttons.add(removedomainbutton);
		buttons.add(addprofilebutton);
		buttons.add(removeprofilebutton);
		buttons.add(editbutton);
		

		// create the tree
		for (RepItem repitem: temp_domain_repos.getItems()) {
			DomainRepItem dri=(DomainRepItem)repitem;
			MyTreeNode newchild=new MyTreeNode(dri);
			for (ProfileRepItem profileitem: dri.getProfiles())
			{
				newchild.add(new MyTreeNode(profileitem));
			}
			root.add(newchild);
		}
			
		treemodel=new DefaultTreeModel(root);
		tree=new JTree(treemodel);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true) ;
		JScrollPane scrollpane = new JScrollPane(tree);

		add(buttons,BorderLayout.EAST);
		add(scrollpane,BorderLayout.CENTER);
		pack();
		show();
	}
	
	void adddomain() { 
		try {
			System.out.println("Add domain to " +((MyTreeNode)(tree.getLastSelectedPathComponent())).getRepositoryItem());
			JFileChooser fd=new JFileChooser(); 
		    //ExampleFileFilter filter = new ExampleFileFilter();
		    //filter.addExtension("xml");
		    //filter.setDescription("domain xml file");
		    //fd.setFileFilter(filter);
			//fd.setFileFilter(filter);
		    int returnVal = fd.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		        System.out.println("You chose to open this file: " +
		             fd.getSelectedFile().toURL());
		        DomainRepItem newnode=new DomainRepItem(""+fd.getSelectedFile().toURL());
		        temp_domain_repos.getItems().add(newnode);		        
				treemodel.insertNodeInto(new MyTreeNode(newnode), root, root.getChildCount());
		     }	
		  }
		catch (Exception e) { new Warning("add domain failed:"+e); }
	}
	
	void removedomain() throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null) throw new Exception("please select a domain to remove first");
		RepItem item=selection.getRepositoryItem();
		if (!(item instanceof DomainRepItem))
			throw new Exception("please select a domain node");
		System.out.println("remove domain " +item);
		temp_domain_repos.getItems().remove(item);
		treemodel.removeNodeFromParent(selection);
	}
	
	ArrayList<RepItem> makedemorepository()
	{
		ArrayList<RepItem> its=new ArrayList<RepItem>();
		
		DomainRepItem dri=new DomainRepItem("file:domain1");
		dri.getProfiles().add(new ProfileRepItem("file:profilea"));
		dri.getProfiles().add(new ProfileRepItem("file:profileb"));
		its.add(dri);
			
		dri=new DomainRepItem("file:domain2");
		dri.getProfiles().add(new ProfileRepItem("file:profilec"));
		dri.getProfiles().add(new ProfileRepItem("file:profiled"));
		dri.getProfiles().add(new ProfileRepItem("file:profilee"));
		its.add(dri);

		return its;
	}

	/** run this for a demo of AgentReposUI */
	public static void main(String[] args) 
	{
	 new DomainRepositoryUI();
	}
}


class MyTreeNode extends DefaultMutableTreeNode {
	RepItem repository_item;
	
	public MyTreeNode(RepItem item)
	{
		super(item);
		repository_item=item;
	}
	
	public String toString() {
		if (repository_item==null) return "";
		if (repository_item instanceof DomainRepItem)
			return ((DomainRepItem)repository_item).getFileName();
		if (repository_item instanceof ProfileRepItem)
			return ((ProfileRepItem)repository_item).getFileName();
		new Warning("encountered item "+repository_item+" of type "+repository_item.getClass());
		return "ERR";
	}
	
	public RepItem getRepositoryItem() { return repository_item; }
}