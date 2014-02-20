package negotiator.gui.domainrepository;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import negotiator.Domain;
import negotiator.gui.NegoGUIApp;
import negotiator.gui.NegoGUIView;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

/**
 * A user interface to the domain repository
 * 
 * @author Wouter Pasman, Mark Hendrikx
 */
public class DomainRepositoryUI {
	private Repository domainrepository;
	private MyTreeNode root = new MyTreeNode(null);
	private JTree scenarioTree;
	private DefaultTreeModel scenarioTreeModel;
	private NegoGUIView negoView;

	public DomainRepositoryUI(JTree pTree, NegoGUIView negoView)
			throws Exception {
		this.scenarioTree = pTree;
		domainrepository = Repository.get_domain_repos();
		initTree();
		scenarioTree.setModel(scenarioTreeModel);
		this.negoView = negoView;
	}

	private void initTree() {
		// for all domains in the domain repository
		for (RepItem repitem : domainrepository.getItems()) {
			DomainRepItem dri = (DomainRepItem) repitem;
			MyTreeNode domainNode = new MyTreeNode(dri);
			// add all preference profiles of the domain as nodes
			for (ProfileRepItem profileitem : dri.getProfiles()) {
				domainNode.add(new MyTreeNode(profileitem));
			}
			root.add(domainNode);
		}

		scenarioTreeModel = new DefaultTreeModel(root);
		scenarioTree.setModel(scenarioTreeModel);
		Font currentFont = scenarioTree.getFont();
		Font bigFont = new Font(currentFont.getName(), currentFont.getStyle(),
				currentFont.getSize() + 2);
		scenarioTree.setRowHeight(23);
		scenarioTree.setFont(bigFont);
		scenarioTree.setRootVisible(false);
		scenarioTree.setShowsRootHandles(true);
		scenarioTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		scenarioTree.addMouseListener(new MouseAdapter() {

			// for Windows
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseCode(e);
			}

			// for Linux
			@Override
			public void mousePressed(MouseEvent e) {
				mouseCode(e);
			}

			private void mouseCode(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					TreePath selPath = scenarioTree.getPathForLocation(
							e.getX(), e.getY());
					if (selPath == null) {
						JPopupMenu popup = createPopupMenu(null);
						popup.show(e.getComponent(), e.getX(), e.getY());
						return;
					}

					MyTreeNode node = (MyTreeNode) selPath
							.getLastPathComponent();
					scenarioTree.setSelectionPath(selPath);
					if (e.isPopupTrigger() && e.getComponent() instanceof JTree) {
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
							JOptionPane
									.showMessageDialog(
											null,
											"Before creating a preference profile, the domain must be saved with at least one isue.",
											"Domain error", 0);
						}
					}
				});
				popup.add(newPP);
				JMenuItem deleteDomain = new JMenuItem("Delete domain");
				deleteDomain
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(
									java.awt.event.ActionEvent evt) {
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
			e.printStackTrace();
		}
		return domain != null && domain.getIssues().size() > 0;
	}

	private void newPreferenceProfile(MyTreeNode node) {

		DomainRepItem dri = (DomainRepItem) node.getRepositoryItem();
		String domainName = dri.getURL().toString().replace("file:", "");
		String cleanName = domainName.replace(".xml", "")
				.replace("_domain", "");
		String path = "";

		int i = 1;
		boolean found = false;
		while (!found && i < 100) {
			path = cleanName + "_util" + i + ".xml";
			File file = new File(path);
			if (!file.exists()) {
				found = true;
			}
			i++;
		}
		String url = "file:" + path;
		ProfileRepItem newPref = null;

		try {
			newPref = new ProfileRepItem(new URL(url), dri);
			dri.getProfiles().add(newPref);
			MyTreeNode newNode = new MyTreeNode(newPref);
			scenarioTreeModel.insertNodeInto(newNode, node,
					node.getChildCount());
			domainrepository.save();

			Domain domain = null;
			try {
				domain = new Domain(domainName);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			UtilitySpace space = null;
			try {
				space = new UtilitySpace(domain, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				space.toXML().saveToFile(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			negoView.showRepositoryItemInTab(newPref, newNode);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void addDomain() {

		String result = new CreateNewDomain(NegoGUIApp.negoGUIView.getFrame())
				.getResult();
		if (result != null) {
			String dirName = "etc" + File.separator + "templates"
					+ File.separator + result;

			File dir = new File(dirName);
			if (!dir.exists()) {
				dir.mkdir();
			}

			String path = dirName + File.separator + result + ".xml";
			DomainRepItem dri = null;
			try {
				dri = new DomainRepItem(new URL("file:" + path));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			domainrepository.getItems().add(dri);
			domainrepository.save();
			MyTreeNode newNode = new MyTreeNode(dri);
			scenarioTreeModel.insertNodeInto(newNode, root,
					root.getChildCount());
			saveDomainAsFile(path, result);
			negoView.showRepositoryItemInTab(dri, newNode);
			scenarioTree.updateUI();
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