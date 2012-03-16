package negotiator.gui;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * Extends the default ListModel by allowing it to be loaded afterwards
 * with data.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 05/12/11
 */
public class ExtendedListModel<A> extends AbstractListModel implements ListModel {
	private static final long serialVersionUID = -8345719619830961700L;
	ArrayList<A> items = new ArrayList<A>();

	public void setInitialContent(ArrayList<A> items) {
		this.items = items;
	}

	public A getElementAt(int index) {
		return items.get(index);
	}

	public int getSize() {
		return items.size();
	}
	
	public void removeElementAt(int i) {
		items.remove(i);
	}
}