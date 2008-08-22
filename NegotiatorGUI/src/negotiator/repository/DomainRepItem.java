package negotiator.repository;

import java.util.ArrayList;
import java.net.URL;
import negotiator.exceptions.*;
import javax.xml.bind.annotation.*;

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
	String url;	// URL is not accepted by JAXB xml thingie. We convert in getURL().
	@XmlElement(name="profile")
	ArrayList<ProfileRepItem> profiles=new ArrayList<ProfileRepItem>(); //default to empty profiles.

	public DomainRepItem() { 
		url="file:unknownfilename";
	}

	public DomainRepItem(URL newurl)  {
		url=newurl.toString();
	}
	
	public URL getURL() {
		try { return new URL(url); } // should work, since we checked it.
		catch (Exception e) { new Warning("failed to set default URL",e); }
		return null;
	}
	
	public ArrayList<ProfileRepItem> getProfiles()  { return profiles; }
	
	public String toString() {
		return "DomainRepItem["+url+","+profiles+"]";
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof DomainRepItem)) return false;
		return url.equals(((DomainRepItem)o).getURL());
	}
}