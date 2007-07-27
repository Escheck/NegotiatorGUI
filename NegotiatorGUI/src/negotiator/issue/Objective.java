package negotiator.issue;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
*
* @author Richard Noorlandt
* 
*/

public class Objective {//implements MutableTreeNode{
	
	//Attributes
	private String name;
	private int number;
	private double weight;
	private String description;
	private Object userObject; //can be a picture, for instance
	private Objective parent;
	private Vector<Objective> children = new Vector();
		
	//Constructors
	public Objective() {
	}
	
	public Objective(Objective parent) {
		this.parent = parent;
	}
	
	public Objective(Objective parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	
	public Objective(Objective parent, String name, int nr) {
		this.parent = parent;
		this.name = name;
		this.number = nr;
	}
	
	//Methods
	
	/**
	 * @return the name of this node.
	 */
	public String getName() {
		//TODO Implement
		return "toedelodokie";//name;
	}
	
	/**
	 * Sets a new name for this node.
	 * 
	 * @param newName the new name for this node.
	 */
	public void setName(String newName) {
		name = newName;
	}
	
	/**
	 * @return the number of this Objective / Issue.
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Sets the number of this Objective / Issue.
	 */
	public void setNumber(int nr) {
		number = nr;
	}
	
	/**
	 * 
	 * @return the weight associated with this node.
	 */
	public double getWeight() {
		return weight;
	}
	
	/**
	 * Sets a new weight for this node.
	 * 
	 * @param newWeight the new weight.
	 */
	public void setWeight(double newWeight) {
		weight = newWeight;
		// TODO Update dependent weights
	}
	
	/**
	 * @return this node's description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets a new description for this node.
	 * 
	 * @param newDescription the new description.
	 */
	public void setDescription(String newDescription) {
		description = newDescription;
	}
	
	/**
	 * @return the user object containted within this node.
	 */
	public Object getUserObject() {
		return userObject;
	}
	
	//public void setUserObject -> see Methods from MutableTreeNode interface 
	
	/**
	 * @return true if and only if this node is of the Objective type, but not of the Issue type. This is
	 * implemented by looking if the receiver is instanceof Objective, but not instanceof Issue.
	 */
	public boolean isObjective() {
		return ((this instanceof Objective) && !(this instanceof Issue));
	}
	
	/**
	 * @return true if and only if this node is of the Issue type. It is implemented with instanceof Issue.
	 */
	public boolean isIssue() {
		return (this instanceof Issue);
	}
	
	/**
	 * Adds a child to this Objective. The new child must be an Objective or an issue. The child is messaged
	 * to set it's parent to the receiver.
	 * 
	 * @param newObjective a child to be added to this Objective.
	 */
	public void addChild(Objective newObjective) {
		children.add(newObjective);
		newObjective.setParent(this);
	}
	
	/**
	 * This method does a recursive depth-first search on the subtree that is rooted at the receiver, and returns
	 *  the first Objective or Issue with the given number. If there is no matching node found, null is returned.
	 * @param objectiveNr the Objective/Issue number that is being searched for.
	 * @return the Objective/Issue with the given number, or null if the requested Objective/Issue is nog found
	 * in the subtree that is rooted at the receiver.
	 */
	public Objective getObjective(int objectiveNr) {
		if (getNumber() == objectiveNr)
			return this;
		
		Enumeration<Objective> descendants = children();
		while (descendants.hasMoreElements()) {
			Objective obj = descendants.nextElement().getObjective(objectiveNr);
			if (obj != null)
				return obj;
		}
		return null;
	}
	
	/**
	 * @return the name of this Objective.
	 */
	public String toString() {
		return name;
	}
	
	//Methods from the TreeNode interface
	
	/**
	 * @return an Enumeration of this Objective's children.
	 */
	public Enumeration<Objective> children() {
		return children.elements();
	}

	/**
	 * @return true iff the node is an OBJECTIVE, of false if the node is an ISSUE.
	 */
	public boolean getAllowsChildren() {
		return (this instanceof Objective);
	}

	/**
	 * @return the child at the given index, or null if the index is invalid. 
	 */
	public Objective getChildAt(int childIndex) {
		if (childIndex < children.size() && childIndex >= 0)
			return children.elementAt(childIndex);
		else
			return null;
	}

	/**
	 * @return the number of children of this node.
	 */
	public int getChildCount() {
		return children.size();
	}

	/**
	 *  @return the index of node in the receivers children. If the receiver does not contain node, -1 will be returned.
	 */
	public int getIndex(TreeNode node) {
		for (int i = 0; i < children.size(); i++) {
			if (node == children.elementAt(i))
				return i;
		}
		return -1;
	}

	/**
	 * @return the parent Objective of the receiver.
	 */
	public Objective getParent() {
		return parent;
	}

	/**
	 * @return is the receiving node is a leaf node. A Objective is a leaf node when it is of the ISSUE type.
	 */
	public boolean isLeaf() {
		return isIssue();
	}
	
	/**
	 * Adds child to the receiver at index. child will be messaged with setParent. Nodes at the given index and above are
	 * moved one place up to make room for the new node. If index > getChildCount() or index < 0, nothing happens.
	 * 
	 * @param child	the Objective to be inserted. If child is no NegotionTreeNode, a ClassCastException will be
	 * 				thrown.
	 * @param index	the index where the new node is to be inserted.
	 */
	public void insert(Objective child, int index) {
		if (index <= getChildCount() && index >= 0) {
			children.insertElementAt((Objective)child, index);
			child.setParent(this);
		}
		// TODO Update weights
	}

	/**
	 * Removes the child at the given index, setting it's parent to null. If index >= getChildCount or index < 0, nothing
	 * happens.
	 */
	public void remove(int index) {
		if (index < getChildCount() && index >= 0) {
			getChildAt(index).setParent(null);
			children.remove(index);
		}
		// TODO Update weights
	}

	/**
	 * Removes node from the receiver's children, and sets it's parent to null. If node is not one of the receiver's
	 * children, nothing happens. 
	 */
	public void remove(Objective node) {
		for (int i = 0; i < children.size(); i++) {
			if (node == children.elementAt(i)) {
				getChildAt(i).setParent(null);
				children.remove(i);
			}
		}
		// TODO Update weights		
	}

	/**
	 * Removes the subtree rooted at this node from the tree, giving this node a null parent. Does nothing if this node is
	 * the root of its tree.
	 */
	public void removeFromParent() {
		if (parent != null) {
			parent.remove(this);
			parent = null;
		}
		// TODO Update weights
	}

	/**
	 * Sets this node's parent to newParent but does not change the parent's child array. This method is called from insert()
	 * and remove() to reassign a child's parent, it should not be messaged from anywhere else. Also, newParent is cast to a
	 * Objective. Calling this method with a different type of TreeNode will result in a ClassCastException.
	 */
	public void setParent(Objective newParent) {
		parent = newParent;
	}

	/**
	 * Sets a user object associated with the receiving Objective. This method is primarily available in order
	 * to implement the MutableTreeNode interface, but because the user object can be of any type it may well be used to
	 * associate extra information about the node. For instance a picture of this node's OBJECTIVE or ISSUE. 
	 */
	public void setUserObject(Object object) {
		userObject = object;
	}
	
	/**
	 * Constructs an Enumeration of the entire subtree of the receiver (including itself) in preorder. The enumeration is
	 * immediately constructed against the current state of the tree, so modifications to the tree afterwards are not 
	 * reflected in the Enumeration.
	 * @return the preorder Enumeration of the subtree.
	 */
	public Enumeration<Objective> getPreorderEnumeration() {
		return getPreorderElements(true, true, new Vector<Objective>()).elements();
	}
	
	/**
	 * Constructs an Enumeration of the entire subtree of the receiver (including itself) in preorder, containting only the
	 * Ojectives, but not the Issues.. The enumeration is immediately constructed against the current state of the tree, so
	 * modifications to the tree afterwards are not reflected in the Enumeration.
	 * @return the preorder Enumeration of Objectives from the subtree.
	 */
	public Enumeration<Objective> getPreorderObjectiveEnumeration() {
		return getPreorderElements(true, false, new Vector<Objective>()).elements();
	}
	
	/**
	 * Constructs an Enumeration of the entire subtree of the receiver (including itself) in preorder, containing only the 
	 * Issues, but not the normal Objectives. The enumeration is immediately constructed against the current state of the 
	 * tree, so modifications to the tree afterwards are not reflected in the Enumeration.
	 * @return the preorder Enumeration of Issues from the subtree.
	 */
	public Enumeration<Objective> getPreorderIssueEnumeration() {
		//TODO return Enumeration<Issue>, but lets do that once we can really test these methods.
		return getPreorderElements(false, true, new Vector<Objective>()).elements();
	}
	
	/**
	 * Makes a preorder traversal of the tree, adding Objectives and Issues to the Vector elems. 
	 * @param includeObjectives defines if Objectives should be included in the Vector.
	 * @param includeIssues defines if Issues should be included in the Vector.
	 * @param elems the Vector that will contain all the elements of the tree. It may not be null.
	 * @return elems, with all descendants added to the vector in preorder.
	 */
	private Vector<Objective> getPreorderElements(boolean includeObjectives, boolean includeIssues, Vector<Objective> elems) {
		//TODO Test if it works flawless
		if (isIssue() && includeIssues) {
			elems.add(this);
			//If this is an Issue, it doesn't have children so we can return.
			return elems;
		}
		else if (isObjective()) {
			if (includeObjectives)
				elems.add(this);
			
			Enumeration<Objective> desc = children();
			while (desc.hasMoreElements()) {
				desc.nextElement().getPreorderElements(includeObjectives, includeIssues, elems);
			}
		}
		return elems;
	}
	
	//TODO Add other traversal types
}
