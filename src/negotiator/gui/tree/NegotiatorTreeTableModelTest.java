package negotiator.gui.tree;

import javax.swing.JScrollPane;

import negotiator.issue.*;
import negotiator.gui.tree.*;
import jtreetable.*;
import javax.swing.*;
import java.awt.event.*;

public class NegotiatorTreeTableModelTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Objective root = new Objective(null, "Root", 0);
		Objective obj1 = new Objective(root, "Objective 1", 9);
		Objective obj2 = new Objective(root, "Objective 2", 10);
		root.addChild(obj1);
		root.addChild(obj2);
		obj1.addChild(new Issue("First Issue", 1));
		obj1.addChild(new Issue("Second Issue", 2));
		obj1.addChild(new Issue("Third Issue", 3));
		
		Issue is = new Issue("Instanceof test", 4);
		System.out.println((is instanceof Objective) && !(is instanceof Issue));
		System.out.println(!((is instanceof Objective) && !(is instanceof Issue)));
		
		NegotiatorTreeTableModel model = new NegotiatorTreeTableModel(root);
		
		//JTreeTable treeTable = new JTreeTable(model);
		//JTree treeTable = new JTree(model);
		
		JFrame mainFrame = new JFrame();
		
		mainFrame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent we) {
			System.exit(0);
		    }
		});
		
		TreePanel panel = new TreePanel(model);
		mainFrame.getContentPane().add(panel);
		
		mainFrame.pack();
		mainFrame.setVisible(true);
		
		//Test the getObjective method
		System.out.println(root.getObjective(3).toString());
		Object test = root.getObjective(5);
		if (test == null)
			System.out.println("all seems well");
		
		System.out.println("\n2, 2 editable: " + panel.getTreeTable().isCellEditable(2,2));
		
		try {Thread.sleep(15000);}
		catch (Exception e) {}
		
		System.out.println("\n2, 2 editable: " + panel.getTreeTable().isCellEditable(2,2));
		//model.fire
		
		/*
		 * testFrame.getContentPane().add(new JScrollPane(treeTable));
		testFrame.pack();
		testFrame.setVisible(true);
		 */
	}

}
