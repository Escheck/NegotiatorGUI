package negotiator.gui.tree;

import jtreetable.*;

import java.util.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import negotiator.*;
import negotiator.issue.*;
import negotiator.utility.*;

/**
*
* @author Richard Noorlandt
* 
*/

//TODO: replace instances of root by Domain.getRoot (or something similar)

public class NegotiatorTreeTableModel extends AbstractTreeTableModel implements TreeTableModel {

	//Attributes
	private Objective root;
	private Domain domain;
	private String[] colNames;// = {"Name", "Eval Type", "Issue Type", "Value", "Weight"};
	private Class[] colTypes;// = {TreeTableModel.class, String.class, String.class, String.class, WeightSlider.class};
	private UtilitySpace utilitySpace;
	private boolean containsUtilitySpace;
	private Map<Objective, WeightSlider> sliders;
	
	private static final String[] domainColNames = {"Name", "Type", "Number"};
	private static final Class[] domainColTypes = {TreeTableModel.class, String.class, Integer.class};
	private static final String[] domainAndUtilityColNames = {"Name", "Type", "Number", "Value", "Weight"};
	private static final Class[] domainAndUtilityColTypes = {TreeTableModel.class, String.class, Integer.class, String.class, WeightSlider.class};
	
	
	//Constructors
	public NegotiatorTreeTableModel(Domain domain) {
		this.domain = domain;
		this.root = domain.getObjectivesRoot();
		this.containsUtilitySpace = false;
		this.colNames = domainColNames;
		this.colTypes = domainColTypes;
	}
	
	public NegotiatorTreeTableModel(Domain domain, UtilitySpace utilitySpace) {
		this.domain = domain;
		this.root = domain.getObjectivesRoot();
		this.utilitySpace = utilitySpace;
		this.containsUtilitySpace = true;
		this.colNames = domainAndUtilityColNames;
		this.colTypes = domainAndUtilityColTypes;
		sliders = new HashMap<Objective, WeightSlider>();
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
		if (column == 4 || column == 0)
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
		
		switch(column) {
		case 0: 	return objective.getName();
		case 1: 	return objective.getType();
		case 2:		return objective.getNumber();
		case 3:		return utilitySpace.getEvaluator(objective.getNumber());//Is this going to work in all cases? Answer: no
		case 4:		return getWeightSlider(objective); 
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
	
	/**
	 * Recursively calculates the highest Objective / Issue number in the tree.
	 * @return the highest Objective / Issue  number in the tree, or -1.
	 */
	public int getHighestObjectiveNr() {
		if (root != null)
			return root.getHighestObjectiveNr(-1);
		else
			return -1;
	}
	
	/**
	 * 
	 * @return the UtilitySpace.
	 */
	public UtilitySpace getUtilitySpace() {
		return utilitySpace;
	}
	
	/**
	 * Sets this model's UtilitySpace. A UtilitySpace is required to map utilities to treeNodes.
	 * @param space a UtilitySpace object.
	 */
	public void setUtilitySpace(UtilitySpace space) {
		utilitySpace = space;
	}
	
	public void updateWeights(WeightSlider caller, double newWeight) {
		//Calculate the new weights for the tree, and return to caller with the caller's new weight. This new weight can be 
		//different from the requested weight, for instance if that modification is impossible for some reason.
		
		//Root may not be null!
		
		//TODO Implement this method. Need new weight calculations from Herbert.
		Enumeration<Objective> objectives = root.getPreorderEnumeration();
		while (objectives.hasMoreElements()) {
			Objective obj = objectives.nextElement();
			double updatedWeight = utilitySpace.getWeight(obj.getNumber());
			getWeightSlider(obj).setWeight(updatedWeight);
		}
	}
	
	/**
	 * Returns the WeightSlider belonging to the given Objective. If there is no WeightSlider attached to the given Objective,
	 * a new one is created and added using setWeightSlider(Objective, WeightSlider).
	 * @param node an Objective.
	 * @return the slider associated with node.
	 */
	protected WeightSlider getWeightSlider(Objective node) {
		WeightSlider slider = sliders.get(node);
		if (slider == null) {
			slider = new WeightSlider(this);
			setWeightSlider(node, slider);
			slider.setWeight(utilitySpace.getWeight(node.getNumber()));
		}
		return slider;
	}
	
	/**
	 * Sets the WeightSlider object for the given Objective.
	 * @param node Objective to attach the slider to.
	 * @param slider the WeightSlider to be attached to node.
	 */
	protected void setWeightSlider(Objective node, WeightSlider slider) {
		sliders.put(node, slider);
	}
	
	/**
	 * Notifies the listeners that the structure of the tree has changed. In it's current
	 * implementation, this method is just a wrapper for the protected method
	 * fireTreeStructureChanged where the child index array and the children array
	 * are left empty.
	 * @param source the source that triggered the change.
	 * @param path a TreePath object that identifies the path to the parent of the modified item(s)
	 */
	public void treeStructureChanged(Object source, Object[] path) {
		fireTreeStructureChanged(source, path, new int[0], new Object[0]);
	}
}
