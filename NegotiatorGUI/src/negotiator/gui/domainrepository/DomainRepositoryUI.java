package negotiator.gui.domainrepository;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;

import javax.swing.JTree;

import negotiator.Domain;
import negotiator.Global;
import negotiator.gui.NegoGUIApp;
import negotiator.gui.agentrepository.DirectoryRestrictedFileSystemView;

import negotiator.repository.DomainRepItem;
import negotiator.repository.Repository;
import negotiator.repository.RepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.xml.SimpleElement;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * A user interface to the domain repository 
 * @author Wouter Pasman, Mark Hendrikx
 */
public class DomainRepositoryUI
{
	private Repository domainrepository;
	private MyTreeNode root=new MyTreeNode(null);
	private JTree scenarioTree;
	private DefaultTreeModel scenarioTreeModel;
	
	public DomainRepositoryUI(JTree pTree) throws Exception
	{
		this.scenarioTree = pTree;
		domainrepository = Repository.get_domain_repos();
		initTree();
		scenarioTree.setModel(scenarioTreeModel);
		
	}	
	
	private void initTree(){
		// for all domains in the domain repository
		for (RepItem repitem: domainrepository.getItems()) {
			DomainRepItem dri=(DomainRepItem)repitem;
			MyTreeNode domainNode = new MyTreeNode(dri);
			// add all preference profiles of the domain as nodes
			for (ProfileRepItem profileitem: dri.getProfiles()) {
				domainNode.add(new MyTreeNode(profileitem));
			}
			root.add(domainNode);
		}
			
		scenarioTreeModel = new DefaultTreeModel(root);
		scenarioTree.setModel(scenarioTreeModel);
		Font currentFont = scenarioTree.getFont();
		Font bigFont = new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() + 2);
		scenarioTree.setRowHeight(23);
		scenarioTree.setFont(bigFont);
		scenarioTree.setRootVisible(false);
		scenarioTree.setShowsRootHandles(true);
		scenarioTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		scenarioTree.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseReleased(MouseEvent e) {
	        	TreePath selPath = scenarioTree.getPathForLocation(e.getX(), e.getY());
	            if (selPath == null){
	                return;
	            } else {
	            	scenarioTree.setSelectionPath(selPath);
	            }
	            
	            MyTreeNode node = (MyTreeNode) scenarioTree.getSelectionPath().getLastPathComponent();
	            
	            if (e.isPopupTrigger() && e.getComponent() instanceof JTree ) {
	                JPopupMenu popup = createPopupMenu(node);
	                popup.show(e.getComponent(), e.getX(), e.getY());
	            }
	        }
		});
	}

	private JPopupMenu createPopupMenu(final MyTreeNode node) {
		JPopupMenu popup = new JPopupMenu();
		
		JMenuItem addExistingDomain = new JMenuItem("Add existing domain");
		JMenuItem addExistingPP = new JMenuItem("Add existing preference profile");
		JMenuItem newDomain = new JMenuItem("New domain");
		newDomain.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addDomain();
            }
        });
		JMenuItem newPP = new JMenuItem("New preference profile");


		popup.add(addExistingDomain);
		popup.add(addExistingPP);
		popup.add(newDomain);
		popup.add(newPP);
		
		if (node.getRepositoryItem() instanceof ProfileRepItem) {
			JMenuItem deletePP = new JMenuItem("Delete preference profile");
			 deletePP.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
					deleteProfile(node);
	            }
	         });
			 popup.add(deletePP);
		} else {
			JMenuItem deleteDomain = new JMenuItem("Delete domain");
			deleteDomain.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	deleteDomain(node);
	            }
	         });
			 popup.add(deleteDomain);
		}
		return popup;
	}
	
	private void addDomain() {
		// Get the root of Genius
		String domainRoot = "";
		String subdirectory = "etc" + File.separator + "templates" + File.separator;
		try {
			domainRoot = new java.io.File(".").getCanonicalPath() + File.separator + subdirectory;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Restrict file picker to root and subdirectories.
		// Ok, you can escape if you put in a path as directory. We catch this later on.
		FileSystemView fsv = new DirectoryRestrictedFileSystemView(new File(domainRoot));
		JFileChooser fc = new JFileChooser(fsv.getHomeDirectory(), fsv);
		
		// Filter such that only directories and .class files are shown.
		FileFilter filter = new FileFilter() {
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
			        return true;
			    }
				String name = f.getName();
				int pos = name.lastIndexOf('.');
				String ext = name.substring(pos+1);
		        
				return ext.equals("xml");
			}

			@Override
			public String getDescription() {
				return "Domain XML files (.xml)";
			}
		};
		fc.setFileFilter(filter);
		
		// Open the file picker
		int returnVal = fc.showSaveDialog(null);
		
		// If file selected
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            // Catch people who tried to escape our directory
            if (!file.getPath().startsWith(domainRoot)) {
            	JOptionPane.showMessageDialog(null, "Only domains in the root or a subdirectory of the root are allowed.", "Agent import error", 0);
            } else {
	            String relativePath = file.getPath().substring(domainRoot.length());
	            String domainName = file.getName();
	            	
	            String path = subdirectory + relativePath + ".xml";
	    		DomainRepItem dri = null;
				try {
					dri = new DomainRepItem(new URL("file:" + path));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
	    		domainrepository.getItems().add(dri);
	    		domainrepository.save();
	    		scenarioTreeModel.insertNodeInto(new MyTreeNode(dri), root, root.getChildCount());
	    		saveDomainAsFile(path, domainName);
            }
        }
	}
	
	private void saveDomainAsFile(String relativePath, String domainName) {
		SimpleElement template = new SimpleElement("negotiation_template");
		SimpleElement utilSpace = new SimpleElement("utility_space");
		SimpleElement objective = new SimpleElement("objective");
		objective.setAttribute("index", "0");
		objective.setAttribute("description", "");
		objective.setAttribute("name", domainName);
		objective.setAttribute("type", "objective");
		objective.setAttribute("etype", "objective");
		utilSpace.addChildElement(objective);
		template.addChildElement(utilSpace);
		template.saveToFile(relativePath);
	}

	private void deleteDomain(MyTreeNode node) {
		DomainRepItem dri = (DomainRepItem) node.getRepositoryItem();
		scenarioTreeModel.removeNodeFromParent(node);
		domainrepository.getItems().remove(dri);
		domainrepository.save();
	}
	
	private void deleteProfile(MyTreeNode node) {
		ProfileRepItem pri = (ProfileRepItem) node.getRepositoryItem();
		scenarioTreeModel.removeNodeFromParent(node);
		domainrepository.removeProfileRepItem(pri);
		domainrepository.save();
	}
}