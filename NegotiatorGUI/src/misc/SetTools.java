package misc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Class which generates the cartesian product of a list of sets.
 * An iterative approach would have been faster, but also less clear.
 * 
 * @author Mark Hendrikx
 */
public class SetTools {

	public static Set<Set<Object>> cartesianProduct(Set<?>... sets) {
	    if (sets.length < 2) {
	    	Iterator setIterator = sets[0].iterator();
	    	HashSet mainSet = new HashSet();
	    	while (setIterator.hasNext()) {
	    		Object item = setIterator.next();
	    		Set set = new HashSet();
	    		set.add(item);
	    		mainSet.add(set);
	    	}
	    	return mainSet;
	    }
	    return _cartesianProduct(0, sets);
	}

	private static Set<Set<Object>> _cartesianProduct(int index, Set<?>... sets) {
	    Set<Set<Object>> ret = new HashSet<Set<Object>>();
	    if (index == sets.length) {
	        ret.add(new HashSet<Object>());
	    } else {
	        for (Object obj : sets[index]) {
	            for (Set<Object> set : _cartesianProduct(index+1, sets)) {
	                set.add(obj);
	                ret.add(set);
	            }
	        }
	    }
	    return ret;
	}
}