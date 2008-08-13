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
	DomainRepItem domain;
	
	public ProfileRepItem(String file,DomainRepItem dom) {
		fileName=file;
		domain=dom;
	}
	
	public  String getFileName() { return fileName; }
	
	public DomainRepItem getDomain() { return domain; }
	
	public String toString() {
		return "ProfileRepItem["+fileName+"]";
	}
	
	
}