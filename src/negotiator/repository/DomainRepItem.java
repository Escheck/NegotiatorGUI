package negotiator.repository;
/**
 * A DomainRepItem is a domain reference that can be put in the domain repository.
 * It contains only a unique reference to an xml file with the domain description.
 * @author wouter
 *
 */
public class DomainRepItem implements RepItem
{
	String fileName;
	
	DomainRepItem(String file) {
		fileName=file;
	}
}