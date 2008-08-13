package negotiator.repository;

import java.util.ArrayList;
/**
 * A DomainRepItem is a domain reference that can be put in the domain repository.
 * It contains only a unique reference to an xml file with the domain description.
 * @author wouter
 *
 */
public class DomainRepItem implements RepItem
{
	String fileName;
	ArrayList<ProfileRepItem> profiles=new ArrayList<ProfileRepItem>(); //default to empty profiles.
	
	public DomainRepItem(String file) {
		fileName=file;
	}
	
	public String getFileName() { return fileName; }
	
	public ArrayList<ProfileRepItem> getProfiles()  { return profiles; }
	
	public String toString() {
		return "DomainRepItem["+fileName+","+profiles+"]";
	}
}