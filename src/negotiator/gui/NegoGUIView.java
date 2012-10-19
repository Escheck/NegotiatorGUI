package negotiator.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;
import negotiator.CSVLoader;
import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.SingleFrameApplication;
import negotiator.Domain;
import negotiator.gui.agentrepository.AgentRepositoryUI;
import negotiator.gui.domainrepository.DomainRepositoryUI;
import negotiator.gui.domainrepository.MyTreeNode;
import negotiator.gui.negosession.NegoSessionUI2;
import negotiator.gui.progress.ProgressUI2;
import negotiator.gui.progress.TournamentProgressUI2;
import negotiator.gui.tab.CloseListener;
import negotiator.gui.tab.CloseTabbedPane;
import negotiator.gui.tournamentvars.TournamentUI;
import negotiator.gui.tree.TreeFrame;
import negotiator.protocol.Protocol;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.RepItem;
import negotiator.tournament.TournamentRunner;
import negotiator.utility.UtilitySpace;

/**
 * The application's main frame.
 */
public class NegoGUIView extends FrameView {
	private static final boolean fTournamentEnabled =true;
	private static final boolean dTournamentEnabled =true;
	private DomainRepositoryUI domainRep = null;
    public NegoGUIView(SingleFrameApplication app) {
        super(app);
        initComponents(); 

        try{
        	new AgentRepositoryUI(tableAgents);
        	domainRep = new DomainRepositoryUI(treeDomains, this);
        } catch (Exception e) {
        	e.printStackTrace();
		}
        CloseListener cl = new CloseListener() {
			public void closeOperation(MouseEvent e, int overTabIndex) {
				closeTabbedPane1.remove(overTabIndex);
			}
        };
        closeTabbedPane1.addCloseListener(cl);
    }
    
    public void processCommandLineOptions()
    {
        if (NegoGUIApp.getOptions().newTournament)
        	newTournamentAction();
    }
    
    /**
     * @author W.Pasman
     * @param filename 
     * @return part of filename following the last slash, or full filename if there is no slash.
     */
    public String GetPlainFileName(String filename) {
    	int i=filename.lastIndexOf('/');
    	if (i==-1) return filename;
    	return filename.substring(i+1);
    }
    
    /**
     * @author W.Pasman
     * @param filename
     * @return filename stripped of its extension (the part after the last dot).
     */
    public String StripExtension(String filename) {
    	int i=filename.lastIndexOf('.');
    	if (i==-1) return filename;
    	return filename.substring(0, i);
    }
    
    public void replaceTab(String title, Component oldComp, Component newComp) {
    	closeTabbedPane1.remove(oldComp);
    	addTab(title, newComp);
    }
    public void addTab(String title, Component comp) {
    	closeTabbedPane1.addTab(title, comp);    	
    	closeTabbedPane1.setSelectedComponent(comp);
    }
    public DomainRepositoryUI getDomainRepositoryUI() {
    	return domainRep;
    }

    private void initComponents() {
        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeDomains = new javax.swing.JTree();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableAgents = new javax.swing.JTable();
        closeTabbedPane1 = new negotiator.gui.tab.CloseTabbedPane();

        menuBar = new javax.swing.JMenuBar();
        JMenu startMenu = new javax.swing.JMenu();
        newSessionMenuItem = new javax.swing.JMenuItem();
        newTournamentMenuItem = new javax.swing.JMenuItem();
    	newDistributedTournamentMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        treeDomains.setMinimumSize(new java.awt.Dimension(100, 100));
        treeDomains.setName("treeDomains"); // NOI18N
        treeDomains.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeDomainsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(treeDomains);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(NegoGUIView.class);
        jTabbedPane1.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, Short.MAX_VALUE)
        );

        jPanel2.setName("jPanel2"); // NOI18N
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableAgents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableAgents.setMinimumSize(new java.awt.Dimension(100, 100));
        tableAgents.setName("tableAgents"); // NOI18N
        jScrollPane1.setViewportView(tableAgents);

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N
        jSplitPane1.setLeftComponent(jPanel1);

        closeTabbedPane1.setName("closeTabbedPane1"); // NOI18N
        jSplitPane1.setRightComponent(closeTabbedPane1);

        

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
            
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE))
            .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(mainPanelLayout.createSequentialGroup()))
        );

        menuBar.setName("menuBar"); // NOI18N

        startMenu.setText(resourceMap.getString("startMenu.text")); // NOI18N
        startMenu.setName("startMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(NegoGUIView.class, this);
        
        newSessionMenuItem.setAction(actionMap.get("newNegoSession")); // NOI18N
        newSessionMenuItem.setName("newSessionMenuItem"); // NOI18N
        startMenu.add(newSessionMenuItem);

        newTournamentMenuItem.setAction(actionMap.get("newTournamentAction")); // NOI18N
        newTournamentMenuItem.setName("newTournamentMenuItem"); // NOI18N
        startMenu.add(newTournamentMenuItem);
        
    	newDistributedTournamentMenuItem.setAction(actionMap.get("newDistributedTournamentAction")); // NOI18N
    	newDistributedTournamentMenuItem.setName("newDistributedTournamentMenuItem"); // NOI18N
    	startMenu.add(newDistributedTournamentMenuItem);

        jMenuItem1.setAction(actionMap.get("runCSVFile")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        startMenu.add(jMenuItem1);
        menuBar.add(startMenu);



        setComponent(mainPanel);
        setMenuBar(menuBar);
        //mainPanel.hide();
    }// </editor-fold>//GEN-END:initComponents

    private void treeDomainsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeDomainsMouseClicked
    	// TODO add your handling code here:
		 
         int selRow = treeDomains.getRowForLocation(evt.getX(), evt.getY());
         TreePath selPath = treeDomains.getPathForLocation(evt.getX(), evt.getY());
         if(selRow != -1) {
             if(evt.getClickCount() == 2) {
                if(selPath!=null) {
                    MyTreeNode node = (MyTreeNode)(selPath.getLastPathComponent());
                    RepItem repItem = node.getRepositoryItem();
                    showRepositoryItemInTab(repItem, node);                
                }
             }
         }
    
}//GEN-LAST:event_treeDomainsMouseClicked

    public void showRepositoryItemInTab(RepItem repItem, MyTreeNode node) {
        TreeFrame tf;
    	if(repItem instanceof DomainRepItem) {
            try {
            	boolean hasNoProfiles = ((DomainRepItem) repItem).getProfiles().size() == 0;
            	String filename=((DomainRepItem) repItem).getURL().getFile();
                Domain domain = new Domain( filename);
                tf = new TreeFrame(domain, hasNoProfiles);
                addTab(StripExtension(GetPlainFileName(filename)), tf);
            } catch (Exception e) {
                e.printStackTrace();
            }                        
        } else if(repItem instanceof ProfileRepItem) {
            try {
            	MyTreeNode parentNode = (MyTreeNode)(node.getParent());
            	String filename=((ProfileRepItem) repItem).getURL().getFile();

                Domain domain = new Domain( ((DomainRepItem) (parentNode.getRepositoryItem()) ).getURL().getFile());
                UtilitySpace utilitySpace = new UtilitySpace(domain, filename);
                
                tf = new TreeFrame(domain,utilitySpace);
                addTab(StripExtension(GetPlainFileName(filename)), tf);
            } catch (Exception e) {
                e.printStackTrace();
            
            }                        
        }
	}

	@Action
    public void newNegoSession() {
    	//JFrame frame = new JFrame();
    	try {  
    		NegoSessionUI2 sessionUI = new NegoSessionUI2();    		
    		addTab("Sess. Editor", sessionUI);
    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		} 
    }

    @Action
    public void newTournamentAction() {
    	if(fTournamentEnabled) {
    		try {
    			TournamentUI tournamentUI = new TournamentUI(false);            
    			addTab("Tour."+tournamentUI.getTournament().TournamentNumber+" settings", tournamentUI);
    			
    			if (NegoGUIApp.getOptions().startTournament)
    				tournamentUI.startTournament();
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} else {
    		JOptionPane.showMessageDialog(this.getComponent(), "The tournament functionality is switched off in this version.");
    	}
    }
    
    @Action
    public void newDistributedTournamentAction() {
    	if(dTournamentEnabled) {
    		try {
    			TournamentUI tournamentUI = new TournamentUI(true);            
    			addTab("DistributedTour."+tournamentUI.getTournament().TournamentNumber+" settings", tournamentUI);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} else {
    		JOptionPane.showMessageDialog(this.getComponent(), "The tournament functionality is switched off in this version.");
    	}
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private negotiator.gui.tab.CloseTabbedPane closeTabbedPane1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newSessionMenuItem;
    private javax.swing.JMenuItem newTournamentMenuItem;
    private javax.swing.JMenuItem newDistributedTournamentMenuItem;
    private javax.swing.JTable tableAgents;
    private javax.swing.JTree treeDomains;

    public  CloseTabbedPane getMainTabbedPane() {
    	return closeTabbedPane1;
    }

    @Action
    public void runCSVFile()
    {
        final JFileChooser csvFileChooser = new JFileChooser();
        csvFileChooser.setCurrentDirectory(new File("."));

        csvFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f)
            {
                if((f.getName().endsWith(".sc"))||(f.isDirectory()))
                    return true;
                else
                    return false;
            }

            @Override
            public String getDescription()
            {
                return "Script files";
            }
        });

        csvFileChooser.setDialogTitle("Open script");
        csvFileChooser.showOpenDialog(null);

        try
        {
            CSVLoader csvLoader = new CSVLoader(csvFileChooser.getSelectedFile().getPath());
            List<Protocol> sessions = csvLoader.getSessions();
            
            ProgressUI2 progressUI = new ProgressUI2(true, true);
            TournamentProgressUI2 tournamentProgressUI = new TournamentProgressUI2(progressUI );
            NegoGUIApp.negoGUIView.addTab("Run from file: " + csvLoader.getFilePath(), tournamentProgressUI);
                      
            new Thread(new TournamentRunner(sessions, tournamentProgressUI)).start();
        }
        catch(Exception e)
        {
            
        }
    }
}
