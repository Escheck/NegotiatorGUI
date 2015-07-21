package negotiator.repository;

import java.io.FileNotFoundException;
import java.net.URL;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import negotiator.Domain;
import negotiator.exceptions.Warning;
import negotiator.session.RepositoryException;
import negotiator.utility.UtilitySpace;

/**
 * ProfileRepItem is a profile, as an item to put in the registry. The profile
 * is not contained here, it's just a (assumed unique) filename.
 * 
 * @author wouter
 *
 */
@XmlRootElement
public class ProfileRepItem implements RepItem {
	private static final long serialVersionUID = -5071749178482314158L;
	@XmlAttribute
	URL url; // URL is not accepted by JAXB xml thingie. We convert in getURL().
	@XmlTransient
	DomainRepItem domain;

	/** This creator is not for public use, only to keep XML parser happy... */
	public ProfileRepItem() {
		try {
			url = new URL("file:uninstantiatedProfilerepitem");
		} catch (Exception e) {
			new Warning("failed to set filename default value" + e);
		}
	}

	public ProfileRepItem(URL file, DomainRepItem dom) {
		url = file;
		domain = dom;
	}

	public URL getURL() {
		return url;
	}

	public DomainRepItem getDomain() {
		return domain;
	}

	@Override
	public String toString() {
		return getURL().getFile();
	}

	public String getFullName() {
		return "ProfileRepItem[" + url + "]";
	}

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.domain = (DomainRepItem) parent;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.url != null ? this.url.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ProfileRepItem)) {
			return false;
		}
		return url.equals(((ProfileRepItem) o).getURL());
	}

	public String getName() {
		String name = url.getFile();
		name = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));

		return name;
	}

	/**
	 * Create a new UtilitySpace from a ProfileRepItem. If
	 * {@link ProfileRepItem#getDomain()} returns new instead of an actual
	 * domain, this method also returns null.
	 *
	 * @param item
	 *            the item to create a UtilitySpace out of.
	 * @return the UtilitySpace corresponding to the item.
	 * @throws FileNotFoundException
	 * @throws RepositoryException
	 * @throws java.lang.Exception
	 *             If
	 *             {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)}
	 *             throws an exception.
	 */
	public UtilitySpace create() throws RepositoryException {
		Domain domain;
		try {
			domain = Repository.get_domain_repos().getDomain(getDomain());

			return Repository.get_domain_repos().getUtilitySpace(domain, this);
		} catch (Exception e) {
			throw new RepositoryException("File not found for " + this, e);
		}
	}

}
