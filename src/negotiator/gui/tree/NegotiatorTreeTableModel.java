package negotiator.gui.tree;

import jtreetable.*;

import javax.swing.table.*;
import javax.swing.tree.*;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class NegotiatorTreeTableModel extends AbstractTreeTableModel implements TreeTableModel {

	//Attributes
	private Objective root;
	private String[] colNames = {"Tree", "Col 1", "Weight"};
	private Class[] colTypes = {TreeTableModel.class, String.class, WeightSlider.class};
	
	//TODO remove this: TEST CODE
	WeightSlider slider = new WeightSlider();
	
	//Constructors
	public NegotiatorTreeTableModel(Objective root) {
		this.root = root;
	}
	
	//Methods
	
	/**
	 * @return the root Object of the tree.
	 */
	public Object getRoot() {
		return root;
	}
	
	/**
	 * @return true if and only if node is an Issue.
	 */
	public boolean isLeaf(Object node) {
		return (node instanceof Issue);
	}
	
	/**
	 * 
	 * @param row the row number of the cell.
	 * @param col the column number of the cell.
	 * @return if the given cell is editable.
	 */
	public boolean isCellEditable(int row, int col) {
		if (col >= colTypes.length || col < 0)
			return false;
		else if (colTypes[col] == TreeTableModel.class)
			return false;
		else if (colTypes[col] == WeightSlider.class)
			return true;
		else
			return false;
		
		//TODO make a sensible implementation
		//if (col == 2)
		//	return true;
		//return false;
	}
	
	public boolean isCellEditable(Object node, int column) {
		//TODO make a sensible implementation. Overridden from AbstractTreeTAbleModel
        //Default: return getColumnClass(column) == TreeTableModel.class;
		if (column == 2 || column == 0)
			return true;
		return false;
   }

	
	/**
	 * Method is empty at the moment. Default implementation from AbstractTreeTableModel.
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {}
			
	/**
	 * @return the number of columns for the TreeTable. 
	 */
	public int getColumnCount() {
		return colNames.length;
	}

	/**
	 * @return the name of column. If column >= getColumnCount, an empty String is returned.
	 */
	public String getColumnName(int column) {
		if (column < getColumnCount())
			return colNames[column];
		else
			return "";
	}
	
	public Class getColumnClass(int column) {
		return colTypes[column];
	}

	/**
	 * When node is an Objective, this method returns the object beloging in the given column.
	 * If node is no Objective, or column has an invalid value, null is returned.
	 * 
	 * @return the contents of column, for the given node.
	 */
	public Object getValueAt(Object node, int column) {
		Objective objective;
		if (!(node instanceof Objective) || getColumnCount() <= column || column < 0)
			return null;
		else
			objective = (Objective)node;
		
		//TODO Maybe also instanceof Issue.
		//do the rest
		WeightSlider slider = new WeightSlider();
		switch(column) {
		case 0: 	return objective.getName();
		case 1: 	return "Test";
		case 2:		return slider;//new WeightSlider(); //TEST
		}
		
		return null;
	}

	/**
	 * Returns parent's child at the given index. If parent is not of type Objective, or index is invalid, null is returned.
	 */
	public Object getChild(Object parent, int index) {
		if (!(parent instanceof Objective) || ((Objective)parent).getChildCount() <= index || index < 0)
			return null;
		else
			return ((Objective)parent).getChildAt(index);
	}

	/**
	 * If parent is instanceof Objective, returns the number of children. Otherwise, 0 is returned.
	 */
	public int getChildCount(Object parent) {
		if (parent instanceof Objective)
			return ((Objective)parent).getChildCount();
		else
			return 0;
	}
	
	//TODO TEST CODE
	public WeightSlider getWeightSlider() {
		return slider;
	}
	
}
