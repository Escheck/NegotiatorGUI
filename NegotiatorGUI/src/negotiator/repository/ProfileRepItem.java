package negotiator.repository;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URL;
import negotiator.exceptions.*;
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
	URL url;
	DomainRepItem domain;
	
	/** This creator is not for public use, only to keep XML parser happy... */
	public ProfileRepItem() { 
		try { 
			url=new URL("file:uninstantiatedProfilerepitem"); 
		} catch (Exception e) { new Warning("failed to set filename default value"+e); }
	}
	
	public ProfileRepItem(URL file,DomainRepItem dom) {
		url=file;
		domain=dom;
	}
	
	public  URL getURL() { return url; }
	
	public DomainRepItem getDomain() { return domain; }
	
	public String toString() {
		return "ProfileRepItem["+url.getFile()+"]";
	}
	
	
}