package negotiator.repository;

import java.util.ArrayList;
import java.net.URL;
import negotiator.exceptions.*;
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
	URL url;
	@XmlElementWrapper
	@XmlAnyElement
	ArrayList<ProfileRepItem> profiles=new ArrayList<ProfileRepItem>(); //default to empty profiles.

	public DomainRepItem() { 
		try { url=new URL("unknownfilename"); }
		catch (Exception e) { new Warning("failed to set default URL"); }
	}

	public DomainRepItem(URL newurl) {
		url=newurl;
	}
	
	public URL getURL() { return url; }
	
	public ArrayList<ProfileRepItem> getProfiles()  { return profiles; }
	
	public String toString() {
		return "DomainRepItem["+url+","+profiles+"]";
	}
}