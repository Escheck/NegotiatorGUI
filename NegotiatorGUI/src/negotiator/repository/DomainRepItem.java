package negotiator.repository;

import java.util.ArrayList;
import java.net.URL;
import negotiator.exceptions.*;
import javax.xml.bind.annotation.*;

/**
 * A DomainRepItem is a domain reference that can be put in the domain repository.
 * It contains only a unique reference to an xml file with the domain description.
 * @author wouter
 */
@XmlRootElement
public class DomainRepItem implements RepItem
{
    @XmlAttribute
    URL url;	// URL is not accepted by JAXB xml thingie. We convert in getURL().
    @XmlElement(name = "profile")
    ArrayList<ProfileRepItem> profiles = new ArrayList<ProfileRepItem>(); //default to empty profiles.

    public DomainRepItem()
    {
        try
        {
            url = new URL("file:unknownfilename");
        }
        catch(Exception e)
        {
            new Warning("default url failed!?", e);
        }
    }

    public DomainRepItem(URL newurl)
    {
        url = newurl;
    }

    public URL getURL()
    {
        return url;
    }

    public ArrayList<ProfileRepItem> getProfiles()
    {
        return profiles;
    }

    @Override
    public String toString()
    {
        return getName();
        //return "DomainRepItem[" + url + "," + profiles + "]";
    }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof DomainRepItem))
        {
            return false;
        }
        return url.equals(((DomainRepItem)o).getURL());
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 89 * hash + (this.url != null ? this.url.hashCode() : 0);
        return hash;
    }

    public String getName()
    {
        String name = url.getFile();
        int dotindex = name.lastIndexOf('.'), slashindex = name.lastIndexOf('/');

        if(slashindex < 0)
            slashindex = 0;
        if(dotindex < 0)
            dotindex = name.length() - 1;
        name = name.substring(slashindex + 1, dotindex);

        return name;
    }

}
