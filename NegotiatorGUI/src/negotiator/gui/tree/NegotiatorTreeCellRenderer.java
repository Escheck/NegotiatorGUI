package negotiator.gui.tree;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class NegotiatorTreeCellRenderer extends DefaultTreeCellRenderer {
	
	//Attributes
	
	//Constructors
	public NegotiatorTreeCellRenderer() {
		super();
	}
	
	//Methods
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)  {
		if (row == 2) {
			setBackground(Color.MAGENTA);
			setBackgroundSelectionColor(Color.MAGENTA);
			setBackgroundNonSelectionColor(Color.MAGENTA);
			sel = true;
		}
		else {
			setBackground(Color.DARK_GRAY);
			setBackgroundSelectionColor(Color.DARK_GRAY);
			setBackgroundNonSelectionColor(Color.DARK_GRAY);
		}
		Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		System.out.println(comp.toString());
		if (comp == this)
			System.out.println("Component == this");
		
		if (row == 2) {
			setBackground(Color.BLUE);
			setBackgroundNonSelectionColor(Color.BLUE);
			setForeground(Color.YELLOW);
			//setOpaque(false);
		}
		else {
			setBackground(Color.WHITE);
			setBackgroundNonSelectionColor(Color.WHITE);
		}
		
		System.out.println("Tree:     " + tree.toString());
		System.out.println("Value:    " + value.toString());
		System.out.println("Selected: " + sel);
		System.out.println("Expanded: " + expanded);
		System.out.println("Leaf:     " + leaf);
		System.out.println("Row:      " + row);
		System.out.println("Hasfocus: " + hasFocus);
		System.out.println("============================================");
		
		return comp;
		/*if (row == 1) {
			setBackgroundSelectionColor(Color.GREEN);
		}
		else {
			setBackgroundSelectionColor(Color.RED);
		}
		return this;*/
	}

}
