
package negotiator;

import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Event;

import negotiator.exceptions.*;

import javax.swing.JFrame;

public class Main2 extends JFrame {
    public static void main(String[] args) {
    	new Main2();
    }

    Main2() {
        setMenuBar(setupMenuBar());
        add(new Label("Negotiator"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // only works with JFrame

        pack();
        setVisible(true);
    }
    
    final static String AGENT_REPOSITORY="Agent Repository";
    final static String DOMAIN_REPOSITORY="Domain & Profile Repository";
    final static String SINGLE_SESSION="Single Session";
    final static String TOURNAMENT="Tournament";
    
    private MenuBar setupMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu editMenu = new Menu("Edit");
        editMenu.add(AGENT_REPOSITORY);
        editMenu.add(DOMAIN_REPOSITORY);
        
        Menu runMenu = new Menu("Run");
        runMenu.add(SINGLE_SESSION);
        runMenu.add(TOURNAMENT);
        
        Menu importMenu = new Menu("Import");
        importMenu.add("Prolog");
        
        menuBar.add(editMenu);
        menuBar.add(runMenu);
        
        return menuBar;
    }
    
    public boolean action(Event pEvt, Object pObj) {
    	try {
			if (AGENT_REPOSITORY.equals(pObj)) {
		        new negotiator.gui.agentrepository.AgentRepositoryUI();
		    }
			
			if (DOMAIN_REPOSITORY.equals(pObj)) {
				new negotiator.gui.domainrepository.DomainRepositoryUI();
		    }
			if (SINGLE_SESSION.equals(pObj)) {
				new negotiator.gui.negosession.NegoSessionUI();
		    }
			if (TOURNAMENT.equals(pObj)) {
				new negotiator.gui.tournamentvars.TournamentVarsUI();
		    }
    	} catch (Exception e) { new Warning("menu action "+pEvt+" failed: "+e); }
		return true;
    }
}

