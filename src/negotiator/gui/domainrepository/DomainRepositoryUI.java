package negotiator.gui.domainrepository;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JTree;

import negotiator.Domain;
import negotiator.gui.DirectoryRestrictedFileSystemView;
import negotiator.gui.GenericFileFilter;
import negotiator.gui.NegoGUIView;
import negotiator.repository.DomainRepItem;
import negotiator.repository.Repository;
import negotiator.repository.RepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.utility.UtilitySpace;
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
	private NegoGUIView negoView;
	
	public DomainRepositoryUI(JTree pTree, NegoGUIView negoView) throws Exception
	{
		this.scenarioTree = pTree;
		domainrepository = Repository.get_domain_repos();
		initTree();
		scenarioTree.setModel(scenarioTreeModel);
		this.negoView = negoView;
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
	        	if (e.getButton() == MouseEvent.BUTTON3) {
		        	TreePath selPath = scenarioTree.getPathForLocation(e.getX(), e.getY());
		            if (selPath == null){
		            	JPopupMenu popup = createPopupMenu(null);
		            	popup.show(e.getComponent(), e.getX(), e.getY());
		            	return;
		            }
		            
		            MyTreeNode node = (MyTreeNode) selPath.getLastPathComponent();
		            scenarioTree.setSelectionRow(root.getIndex(node));
		            if (e.isPopupTrigger() && e.getComponent() instanceof JTree ) {
		                JPopupMenu popup = createPopupMenu(node);
		                popup.show(e.getComponent(), e.getX(), e.getY());
		            }
	        	}
	        }
		});
	}

	private JPopupMenu createPopupMenu(final MyTreeNode node) {
		JPopupMenu popup = new JPopupMenu();

		JMenuItem newDomain = new JMenuItem("New domain");
		newDomain.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addDomain();
            }
        });

		popup.add(newDomain);
		
		if (node != null) {
			if (node.getRepositoryItem() instanceof ProfileRepItem) {
				JMenuItem deletePP = new JMenuItem("Delete preference profile");
				 deletePP.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
						deleteProfile(node);
		            }
		         });
				 popup.add(deletePP);
			} else {
				JMenuItem newPP = new JMenuItem("New preference profile");
				newPP.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		            	if (domainHasIssues(node)) {
		            		newPreferenceProfile(node);
		            	} else {
		            		JOptionPane.showMessageDialog(null, "Before creating a preference profile, the domain must be saved with at least one isue.", "Domain error", 0);
		            	}
		            }
		         });
				popup.add(newPP);
				JMenuItem deleteDomain = new JMenuItem("Delete domain");
				deleteDomain.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		            	deleteDomain(node);
		            }
		         });
				 popup.add(deleteDomain);
			}
		}
		return popup;
	}
	
	protected boolean domainHasIssues(MyTreeNode node) {
		// get the directory of the domain
		DomainRepItem dri = (DomainRepItem) node.getRepositoryItem();
		String fullPath = dri.getURL().toString().substring(5);
		Domain domain = null;
		try {
			domain = new Domain(fullPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return domain != null && domain.getIssues().size() > 0;
	}

	private void newPreferenceProfile(MyTreeNode node) {
		// get the directory of the domain
		DomainRepItem dri = (DomainRepItem) node.getRepositoryItem();
		String fullPath = dri.getURL().toString().substring(5); // remove "file:"
		String[] split = fullPath.split("/");
		String domainDir = fullPath.substring(0, fullPath.length() - split[split.length-1].length()).replace("/", File.separator);
		String completePath = "";
		try {
			completePath = new java.io.File(".").getCanonicalPath() + File.separator + domainDir;
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Restrict file picker to root and subdirectories.
		// Ok, you can escape if you put in a path as directory. We catch this later on.
		FileSystemView fsv = new DirectoryRestrictedFileSystemView(new File(completePath));
		JFileChooser fc = new JFileChooser(fsv.getHomeDirectory(), fsv);
		
		// Filter such that only directories and .class files are shown.
		FileFilter filter = new GenericFileFilter("xml", "Domain XML files (.xml)");
		fc.setFileFilter(filter);
		
		// Open the file picker
		int returnVal = fc.showSaveDialog(null);

		// If file selected
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            // Catch people who tried to escape our directory
            if (!file.getPath().startsWith(completePath)) {
            	JOptionPane.showMessageDialog(null, "Only preference profiles in the root or a subdirectory of the root are allowed.", "Agent import error", 0);
            } else {
            	String nameInPath = file.getPath().substring(completePath.length());
            	String fullPathOfPref = "file:" + domainDir + nameInPath + ".xml";
            	ProfileRepItem newPref = null;
				try {
					newPref = new ProfileRepItem(new URL(fullPathOfPref), dri);
	            	dri.getProfiles().add(newPref);
	            	MyTreeNode newNode = new MyTreeNode(newPref);
	            	scenarioTreeModel.insertNodeInto(newNode, node, node.getChildCount());
	            	domainrepository.save();
	            	
	            	Domain domain = null;
	        		try {
	        			domain = new Domain(fullPath);
	        		} catch (Exception e1) {
	        			e1.printStackTrace();
	        		}
	        		UtilitySpace space = null;
					try {
						space = new UtilitySpace(domain, "");
					} catch (Exception e) {
						e.printStackTrace();
					}
	        		space.toXML().saveToFile(completePath + nameInPath + ".xml");
	        		negoView.showRepositoryItemInTab(newPref, newNode);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
            }
        }		        
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
		FileFilter filter = new GenericFileFilter("xml", "Domain XML files (.xml)");
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
	    		MyTreeNode newNode = new MyTreeNode(dri);
	    		scenarioTreeModel.insertNodeInto(newNode, root, root.getChildCount());
	    		saveDomainAsFile(path, domainName);
	    		negoView.showRepositoryItemInTab(dri, newNode);
            }
        }
        scenarioTree.updateUI();
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