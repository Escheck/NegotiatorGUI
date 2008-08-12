package negotiator.repository;

/**
 * ProfileRepItem is a profile, as an item to put in the registry.
 * The profile is not contained here, it's just a (assumed unique) filename.
 * 
 * @author wouter
 *
 */
public class ProfileRepItem implements RepItem
{
	String fileName;
	
	ProfileRepItem(String file) {
		fileName=file;
	}
	
}