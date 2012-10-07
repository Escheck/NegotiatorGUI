package negotiator.gui.domainrepository;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.JTree;
import negotiator.repository.DomainRepItem;
import negotiator.repository.Repository;
import negotiator.repository.RepItem;
import negotiator.repository.ProfileRepItem;
import javax.swing.JFileChooser;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
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
			JMenuItem deletePP = new JMenuItem("Delete domain");
			 deletePP.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	deleteDomain(node);
	            }
	         });
			 popup.add(deletePP);
		}
		return popup;
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