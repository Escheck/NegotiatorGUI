package negotiator.repository;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ProfileRepItem is a profile, as an item to put in the registry.
 * The profile is not contained here, it's just a (assumed unique) filename.
 * 
 * @author wouter
 *
 */
@XmlRootElement
public class ProfileRepItem implements RepItem
{
	@XmlAttribute
	String fileName;
	DomainRepItem domain;
	
	public ProfileRepItem() { fileName="uninstantiated profilerepitem"; }
	
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