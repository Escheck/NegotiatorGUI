package negotiator.repository;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * A DomainRepItem is a domain reference that can be put in the domain repository.
 * It contains only a unique reference to an xml file with the domain description.
 * @author wouter
 *
 */
@XmlRootElement
public class DomainRepItem implements RepItem
{	
	@XmlAttribute
	String fileName;
	@XmlElementWrapper
	@XmlAnyElement
	ArrayList<ProfileRepItem> profiles=new ArrayList<ProfileRepItem>(); //default to empty profiles.

	public DomainRepItem() { fileName="unknownfilename"; }

	public DomainRepItem(String file) {
		fileName=file;
	}
	
	public String getFileName() { return fileName; }
	
	public ArrayList<ProfileRepItem> getProfiles()  { return profiles; }
	
	public String toString() {
		return "DomainRepItem["+fileName+","+profiles+"]";
	}
}