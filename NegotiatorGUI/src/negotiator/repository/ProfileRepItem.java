package negotiator.repository;

import javax.xml.bind.annotation.*;
import java.net.URL;
import negotiator.exceptions.*;
import javax.xml.bind.Unmarshaller;

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
	String url; 	// URL is not accepted by JAXB xml thingie. We convert in getURL().
	@XmlTransient
	DomainRepItem domain;
	
	/** This creator is not for public use, only to keep XML parser happy... */
	public ProfileRepItem() { 
		try { 
			url="file:uninstantiatedProfilerepitem"; 
		} catch (Exception e) { new Warning("failed to set filename default value"+e); }
	}
	
	public ProfileRepItem(URL file,DomainRepItem dom) {
		url=file.toString();
		domain=dom;
	}
	
	public  URL getURL() { 		
		try { return new URL(url); } // should work, since we checked it.
		catch (Exception e) { new Warning("failed to set default URL",e); }
		return null; 
	}
	
	public DomainRepItem getDomain() { return domain; }
	
	public String toString() {
		return "ProfileRepItem["+url+"]";
	}
	
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.domain = (DomainRepItem)parent;
	}
	
	
}