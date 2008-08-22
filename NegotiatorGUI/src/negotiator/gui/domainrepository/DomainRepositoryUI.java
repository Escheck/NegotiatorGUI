package negotiator.gui.domainrepository;

import javax.swing.JFrame;
import java.net.URL;
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

import negotiator.exceptions.Warning;


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
	

	Repository domainrepository; // TODO locate this somewhere better
	MyTreeNode root=new MyTreeNode(null);
	JTree tree;
	DefaultTreeModel treemodel;
	
	public DomainRepositoryUI() throws Exception
	{
		domainrepository=Repository.get_domain_repos();
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
		for (RepItem repitem: domainrepository.getItems()) {
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
	        DomainRepItem newnode=new DomainRepItem(fd.getSelectedFile().toURL());
	        domainrepository.getItems().add(newnode);		        
			treemodel.insertNodeInto(new MyTreeNode(newnode), root, root.getChildCount());
			domainrepository.save();
	     }	
	}
	
	void removedomain() throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null) throw new Exception("please select a domain to remove first");
		RepItem item=selection.getRepositoryItem();
		if (!(item instanceof DomainRepItem))
			throw new Exception("please select a domain node");
		System.out.println("remove domain " +item);
		domainrepository.getItems().remove(item);
		treemodel.removeNodeFromParent(selection);
		domainrepository.save();
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
	        ProfileRepItem newnode=new ProfileRepItem(fd.getSelectedFile().toURL(),(DomainRepItem)item);
	        ((DomainRepItem)item).getProfiles().add(newnode);		        
			treemodel.insertNodeInto(new MyTreeNode(newnode), selection, selection.getChildCount());
			domainrepository.save();
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
		domainrepository.save();
	}

	void edit() throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null ) 
			throw new Exception("please first select an item to be edited");
		RepItem item=selection.getRepositoryItem();
		if (item instanceof DomainRepItem) {
			URL filename=((DomainRepItem)item).getURL();
	    	Domain domain=new Domain(filename.getFile());
	    	TreeFrame treeFrame = new TreeFrame(domain);
		}
		else if (item instanceof ProfileRepItem) {
			URL filename=((ProfileRepItem)item).getURL();
			URL domainfilename=((ProfileRepItem)item).getDomain().getURL();

	    	Domain domain=new Domain(domainfilename.getFile());
	    	UtilitySpace us=new UtilitySpace(domain,filename.getFile());
	    	TreeFrame treeFrame=new TreeFrame(domain, us);
		}
		else
			throw new IllegalStateException("found unknown node in tree: "+item);
		
	}
	
	
	
	/******************DEMO CODE************************/

	
	
	/** run this for a demo of AgentReposUI */
	public static void main(String[] args) 
	{
		try {
			new DomainRepositoryUI(); 
			}
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
			return shortfilename(((DomainRepItem)repository_item).getURL().getFile());
		if (repository_item instanceof ProfileRepItem)
			return shortfilename( ((ProfileRepItem)repository_item).getURL().getFile());
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