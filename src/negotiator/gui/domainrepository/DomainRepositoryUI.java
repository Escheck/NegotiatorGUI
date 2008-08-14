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

import negotiator.Domain;
import negotiator.repository.*;
import negotiator.utility.UtilitySpace;

import javax.swing.JFileChooser;
import java.io.FileFilter;

import java.util.ArrayList;
import negotiator.repository.*;
import negotiator.exceptions.Warning;
import negotiator.gui.agentrepository.AgentRepositoryUI;
import negotiator.gui.tree.TreeFrame;
import negotiator.issue.Objective;

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
	

	Repository temp_domain_repos; // TODO locate this somewhere better
	static String FILENAME="domainrepository.xml"; // ASSUMPTION  there is only one domain repository
	MyTreeNode root=new MyTreeNode(null);
	JTree tree;
	DefaultTreeModel treemodel;
	
	public DomainRepositoryUI() throws Exception
	{
		
		try {
			temp_domain_repos=new Repository(FILENAME);
		} catch (Exception e) {
			temp_domain_repos=new Repository();
			temp_domain_repos.setFilename(FILENAME);
			temp_domain_repos.getItems().addAll(makedemorepository());
			temp_domain_repos.save();
		}
		
		
		setTitle("Negotiation Domains and Preference Profile Repository");
		setLayout(new BorderLayout());
	
		 // CREATE THE BUTTONS
		JPanel buttons=new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.Y_AXIS));
		adddomainbutton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				try { adddomain(); } 
				catch (Exception err)  { new Warning("add domain failed:"+err);}
			}
		});
		removedomainbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { removedomain(); }
				catch (Exception err)  { new Warning("remove domain failed:"+err);}
			}
		});
		addprofilebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { addprofile(); }
				catch (Exception err)  { new Warning("remove failed:"+err);}
			}
		});
		removeprofilebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { removeprofile(); }
				catch (Exception err)  { new Warning("remove failed:"+err);}
			}
		});
		editbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { edit(); }
				catch (Exception err)  { new Warning("remove failed:"+err); err.printStackTrace();}
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
	
	void adddomain() throws Exception { 
		//System.out.println("Add domain to " +((MyTreeNode)(tree.getLastSelectedPathComponent())).getRepositoryItem());
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
			temp_domain_repos.save();
	     }	
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
		temp_domain_repos.save();
	}
	
	ArrayList<RepItem> makedemorepository()
	{
		ArrayList<RepItem> its=new ArrayList<RepItem>();
		
		DomainRepItem dri=new DomainRepItem("file:domain1");
		dri.getProfiles().add(new ProfileRepItem("file:profilea",dri));
		dri.getProfiles().add(new ProfileRepItem("file:profileb",dri));
		its.add(dri);
			
		dri=new DomainRepItem("file:domain2");
		dri.getProfiles().add(new ProfileRepItem("file:profilec",dri));
		dri.getProfiles().add(new ProfileRepItem("file:profiled",dri));
		dri.getProfiles().add(new ProfileRepItem("file:profilee",dri));
		its.add(dri);

		return its;
	}
	
	void addprofile() throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null) throw new Exception("please select a domain to add the profile to");
		RepItem item=selection.getRepositoryItem();
		if (!(item instanceof DomainRepItem))
			throw new Exception("please select a domain node");
		
		JFileChooser fd=new JFileChooser(); 
	    int returnVal = fd.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	        System.out.println("You chose to open this file: " +
	             fd.getSelectedFile().toURL());
	        // TODO check that selected profile indeed belongs to our domain.
	        ProfileRepItem newnode=new ProfileRepItem(""+fd.getSelectedFile().toURL(),(DomainRepItem)item);
	        ((DomainRepItem)item).getProfiles().add(newnode);		        
			treemodel.insertNodeInto(new MyTreeNode(newnode), selection, selection.getChildCount());
			temp_domain_repos.save();
	     }	
	}
	
	void removeprofile() throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null) throw new Exception("please select a profile to remove first");
		RepItem item=selection.getRepositoryItem();
		if (!(item instanceof ProfileRepItem))
			throw new Exception("please select a profile node");
		System.out.println("remove profile " +item);
		
		DomainRepItem domain=((ProfileRepItem)item).getDomain();
		domain.getProfiles().remove(item);
		treemodel.removeNodeFromParent(selection);
		temp_domain_repos.save();
	}

	void edit() throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null ) 
			throw new Exception("please first select an item to be edited");
		RepItem item=selection.getRepositoryItem();
		if (item instanceof DomainRepItem) {
			String filename=((DomainRepItem)item).getFileName();
			if (!(filename.startsWith("file:")))
				throw new IllegalArgumentException("filename does not start with 'file:'");
	    	Domain domain=new Domain(filename.substring(5));
	    	TreeFrame treeFrame = new TreeFrame(domain);
		}
		else if (item instanceof ProfileRepItem) {
			String filename=((ProfileRepItem)item).getFileName();
			if (!(filename.startsWith("file:")))
				throw new IllegalArgumentException("filename does not start with 'file:'");
			String domainfilename=((ProfileRepItem)item).getDomain().getFileName();
			if (!(domainfilename.startsWith("file:")))
				throw new IllegalArgumentException("domainfilename does not start with 'file:'");

	    	Domain domain=new Domain(domainfilename.substring(5));
	    	UtilitySpace us=new UtilitySpace(domain,filename.substring(5));
	    	TreeFrame treeFrame=new TreeFrame(domain, us);
		}
		else
			throw new IllegalStateException("found unknown node in tree: "+item);
		
	}
	/** run this for a demo of AgentReposUI */
	public static void main(String[] args) 
	{
		try {  new DomainRepositoryUI(); }
		catch (Exception e) { new Warning("DomainRepositoryUI failed to launch: "+e); }
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
			return shortfilename(((DomainRepItem)repository_item).getFileName());
		if (repository_item instanceof ProfileRepItem)
			return shortfilename( ((ProfileRepItem)repository_item).getFileName());
		new Warning("encountered item "+repository_item+" of type "+repository_item.getClass());
		return "ERR";
	}
	/** returns only the filename given a full path with separating '/' */
	public String shortfilename(String filename) {
		int lastslash=filename.lastIndexOf('/');
		if (lastslash==-1) return filename;
		return filename.substring(lastslash+1); 
	}
	
	public RepItem getRepositoryItem() { return repository_item; }
}