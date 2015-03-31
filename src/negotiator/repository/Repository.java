package negotiator.repository;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import negotiator.Domain;
import negotiator.exceptions.Warning;
import negotiator.session.RepositoryException;
import negotiator.utility.ConstraintUtilitySpace;
import negotiator.utility.NonlinearUtilitySpace;
import negotiator.utility.UTILITYSPACETYPE;
import negotiator.utility.UtilitySpace;

/**
 * Repository contains a set of known files This can be agent files or
 * domain+profile files.
 * 
 * @author Dmytro Tykhonov, W.Pasman
 * 
 */
@XmlRootElement
public class Repository {

	@XmlJavaTypeAdapter(RepositoryItemTypeAdapter.class)
	ArrayList<RepItem> items; // the items in the domain, either AgentRepItems
								// or DomainRepItems
	@XmlAttribute
	String fileName; // the filename of this repository.

	String sourceFolder = null;

	private static Repository domainRepos = null;

	public Repository() {
		items = new ArrayList<RepItem>();
	}

	public Repository(String fn) throws RepositoryException {
		setFilename(fn);
		try {
			copyFrom(load(fileName));
		} catch (Exception e) {
			throw new RepositoryException("Failed to load repository " + fn, e);
		}
	}

	public void setFilename(String fn) {
		fileName = fn;
	}

	public String getFilename() {
		return fileName;
	}

	public void copyFrom(Repository rep) {
		items = rep.getItems();
	}

	/** @author Dmytro */
	public Repository load(String fileName) throws Exception {
		Repository rep = null;
		JAXBContext jaxbContext = JAXBContext.newInstance(Repository.class,
				PartyRepItem.class, ProfileRepItem.class,
				MultiPartyProtocolRepItem.class, DomainRepItem.class,
				AgentRepItem.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller
				.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
		rep = (Repository) (unmarshaller.unmarshal(new File(fileName)));
		unmarshaller = null;
		jaxbContext = null;
		return rep;
	}

	/** @author Dmytro */
	public void save() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Repository.class,
					ProfileRepItem.class, DomainRepItem.class,
					AgentRepItem.class, PartyRepItem.class,
					ProtocolRepItem.class, MultiPartyProtocolRepItem.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					new Boolean(true));

			marshaller.marshal(new JAXBElement(new QName("repository"),
					Repository.class, this), new File(fileName));
		} catch (Exception e) {
			new Warning("xml save failed: " + e); // e.printStackTrace();
		}

	}

	/**
	 * Get available agents
	 * 
	 * @return
	 */
	public ArrayList<RepItem> getItems() {
		return items;
	}

	/** @returns AgentRepItem of given className, or null if none exists */
	public AgentRepItem getAgentOfClass(String className) {
		for (RepItem it : items) {
			if (it instanceof AgentRepItem)
				if (((AgentRepItem) it).classPath.equals(className))
					return (AgentRepItem) it;
		}
		return null;
	}

	/** @returns AgentRepItem of given className, or null if none exists */
	public PartyRepItem getPartyOfClass(String className) {
		for (RepItem it : items) {
			if (it instanceof PartyRepItem)
				if (((PartyRepItem) it).classPath.equals(className))
					return (PartyRepItem) it;
		}
		return null;
	}

	public boolean removeProfileRepItem(ProfileRepItem item) {
		for (int i = 0; i < items.size(); i++) {
			System.out.println(items.get(i).getName());
			DomainRepItem drp = (DomainRepItem) items.get(i);
			for (int a = 0; a < drp.getProfiles().size(); a++) {
				ProfileRepItem pri = drp.getProfiles().get(a);
				if (pri.getName().equals(item.getName())) {
					drp.getProfiles().remove(a);
					return true;
				}
			}
		}
		return false;
	}

	public String toString() {
		String ret = "{";
		for (RepItem i : items) {
			ret = ret + i + ",";
		}
		ret = ret + "}";
		return ret;

	}

	public static DomainRepItem getDomainByName(String name) throws Exception {
		Repository domRep = get_domain_repos();
		DomainRepItem domainRepItem = null;
		for (RepItem tmp : domRep.getItems()) {
			if (((DomainRepItem) tmp).url.toString().equals(name)) {
				domainRepItem = (DomainRepItem) tmp;
				break;
			}
		}
		return domainRepItem;
	}

	public Domain getDomain(DomainRepItem domainRepItem) {
		String file = domainRepItem.getURL().getFile();
		return getDomain(file);
	}

	public Domain getDomain(String file) {
		Domain domain = null;
		try {
			if ((sourceFolder != null) && (!sourceFolder.equals("")))
				domain = new Domain(sourceFolder + "/" + file);
			else
				domain = new Domain(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return domain;
	}

	public UtilitySpace getUtilitySpace(Domain domain, ProfileRepItem profile) {
		String file = profile.getURL().getFile();
		return getUtilitySpace(domain, file);
	}

	public UtilitySpace getUtilitySpace(Domain domain, String file) {
		UtilitySpace us = null;
		try {
			if ((sourceFolder != null) && (!sourceFolder.equals(""))) {

				if (UTILITYSPACETYPE.getUtilitySpaceType(file) == UTILITYSPACETYPE.NONLINEAR) // RA
					us = new NonlinearUtilitySpace(domain, sourceFolder + "/"
							+ file); // RA
				else if (UTILITYSPACETYPE.getUtilitySpaceType(file) == UTILITYSPACETYPE.CONSTRAINT) // RA
					us = new ConstraintUtilitySpace(domain, sourceFolder + "/"
							+ file); // RA
				else
					us = new UtilitySpace(domain, sourceFolder + "/" + file);
			} else {
				if (UTILITYSPACETYPE.getUtilitySpaceType(file) == UTILITYSPACETYPE.NONLINEAR) // RA
					us = new NonlinearUtilitySpace(domain, file);
				else if (UTILITYSPACETYPE.getUtilitySpaceType(file) == UTILITYSPACETYPE.CONSTRAINT) // RA
					us = new ConstraintUtilitySpace(domain, file);
				else
					us = new UtilitySpace(domain, file);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to load space:" + file);
			e.printStackTrace();
		}
		return us;
	}

	public boolean existUtilitySpace(Domain domain, ProfileRepItem profile) {
		UtilitySpace us = null;
		try {
			File file;
			if ((sourceFolder != null) && (!sourceFolder.equals("")))
				file = new File(sourceFolder + "/" + profile.getURL().getFile());
			else
				file = new File(profile.getURL().getFile());
			return file.exists();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to load space:"
					+ profile.getURL().getFile());
			e.printStackTrace();
		}
		return false;
	}

	public static Repository get_domain_repos(String filename,
			String sourceFolder) throws RepositoryException {
		if (domainRepos != null)
			return domainRepos;
		Repository repos;
		try {
			repos = new Repository(filename);
			domainRepos = repos;
			repos.sourceFolder = sourceFolder;
		} catch (RepositoryException e) {
			repos = new Repository();
			repos.setFilename(filename);
			try {
				repos.getItems().addAll(makedemorepository());
			} catch (MalformedURLException e1) {
				throw new RepositoryException(
						"Failed to load normal repository and also failed to create the backup repository",
						e1);
			}
			repos.save();
		}
		return repos;
	}

	/****************** code that creates repos if none exists ********************/
	public static Repository get_domain_repos() throws RepositoryException {
		final String FILENAME = "domainrepository.xml"; // ASSUMPTION there is
														// only one domain
														// repository
		return get_domain_repos(FILENAME, "");

	}

	static ArrayList<RepItem> makedemorepository() throws MalformedURLException {
		ArrayList<RepItem> its = new ArrayList<RepItem>();

		// DomainRepItem dri=new DomainRepItem(new
		// URL("file:/Volumes/Documents/Wouter/Negotiator/NegoWorkspace/Negotiator/etc/templates/laptopdomain/laptop_domain.xml"));
		DomainRepItem dri = new DomainRepItem(
				new URL(
						"file:H:/Negotiator/negotiator/templates/laptopdomain/laptop_domain.xml"));

		// dri.getProfiles().add(new ProfileRepItem(new
		// URL("file:/Volumes/Documents/Wouter/Negotiator/NegoWorkspace/Negotiator/etc/templates/laptopdomain/laptop_seller_utility.xml"),dri));
		// dri.getProfiles().add(new ProfileRepItem(new
		// URL("file:/Volumes/Documents/Wouter/Negotiator/NegoWorkspace/Negotiator/etc/templates/laptopdomain/laptop_empty_utility.xml"),dri));
		dri.getProfiles()
				.add(new ProfileRepItem(
						new URL(
								"file:H:/Negotiator/negotiator/templates/laptopdomain/laptop_seller_utility.xml"),
						dri));
		dri.getProfiles()
				.add(new ProfileRepItem(
						new URL(
								"file:H:/Negotiator/negotiator/templates/laptopdomain/laptop_empty_utility.xml"),
						dri));
		its.add(dri);

		dri = new DomainRepItem(new URL("file:domain2"));
		dri.getProfiles()
				.add(new ProfileRepItem(new URL("file:profilec"), dri));
		dri.getProfiles()
				.add(new ProfileRepItem(new URL("file:profiled"), dri));
		dri.getProfiles()
				.add(new ProfileRepItem(new URL("file:profilee"), dri));
		its.add(dri);

		return its;
	}

	static ArrayList<RepItem> init_temp_repository() {
		ArrayList<RepItem> items = new ArrayList<RepItem>();
		items.add(new AgentRepItem("Warning: Repository not loaded", "", ""));
		items.add(new AgentRepItem(
				"Please check that Genius can find agentrepository.xml and domainrepository.xml",
				"", ""));
		items.add(new AgentRepItem(
				"And make sure /etc/templates is in the same directory", "", ""));
		items.add(new AgentRepItem("BayesianAgent", "agents.BayesianAgent",
				"simple agent"));
		items.add(new AgentRepItem("UI agent", "agents.UIAgent",
				"basic UI agent"));
		return items;
	}

	static ArrayList<RepItem> init_temp_repository2() {
		ArrayList<RepItem> items = new ArrayList<RepItem>();
		items.add(new PartyRepItem("Simple Party", "parties.SimpleParty",
				"Simple Negotiator", null));
		return items;
	}

	static ArrayList<RepItem> init_temp_prot_repository() {
		ArrayList<RepItem> items = new ArrayList<RepItem>();
		items.add(new ProtocolRepItem("Alternating Offers",
				"negotiator.protocol.AlternatingOffersMetaProtocol",
				"Alternating Offers"));
		items.add(new ProtocolRepItem("Auction",
				"negotiator.protocol.AuctionMetaProtocol", "Auction"));
		return items;
	}

	static ArrayList<RepItem> init_temp_multiprot_repository() {
		ArrayList<RepItem> items = new ArrayList<RepItem>();
		items.add(new MultiPartyProtocolRepItem(
				"Simple Mediated Multiparty Protocol",
				"negotiator.multiPartyProtocol.SimpleMediatedMultipartyProtocol",
				"Simple Mediated Multiparty Protocol", null, null));
		return items;
	}

	public static Repository getProtocolRepository() {
		final String FILENAME = "protocolrepository.xml";
		Repository repos;

		try {
			repos = new Repository(FILENAME);
		} catch (Exception e) {
			System.out.println("load of saved repository failed:" + e);
			repos = new Repository();
			repos.setFilename(FILENAME);
			repos.getItems().addAll(init_temp_prot_repository());
			repos.save();
		}

		return repos;
	}

	public static Repository getMultiPartyProtocolRepository() {
		final String FILENAME = "multipartyprotocolrepository.xml";
		Repository repos;

		try {
			repos = new Repository(FILENAME);
		} catch (Exception e) {
			System.out.println("load of saved repository failed:" + e);
			repos = new Repository();
			repos.setFilename(FILENAME);
			repos.getItems().addAll(init_temp_multiprot_repository());
			repos.save();
		}

		return repos;
	}

	public static Repository getProtocolRepository(String filename,
			String sourceFolder) throws Exception {

		Repository repos;
		try {
			repos = new Repository(filename);
			repos.sourceFolder = sourceFolder;
		} catch (Exception e) {
			repos = new Repository();
			repos.setFilename(filename);
			repos.getItems().addAll(init_temp_prot_repository());
			repos.save();
		}
		return repos;
	}

	public static Repository get_agent_repository() {
		final String FILENAME = "agentrepository.xml"; // ASSUMPTION: there is
														// only one agent
														// reposityro
		Repository repos;

		try {
			repos = new Repository(FILENAME);
		} catch (Exception e) {
			System.out.println("load of saved repository failed:" + e);
			repos = new Repository();
			repos.setFilename(FILENAME);
			repos.getItems().addAll(init_temp_repository());
			repos.save();
		}

		return repos;
	}

	public static Repository get_party_repository() {
		final String FILENAME = "partyrepository.xml"; // ASSUMPTION: there is
														// only one agent
														// reposityro
		Repository repos;

		try {
			repos = new Repository(FILENAME);
		} catch (Exception e) {
			System.out.println("load of saved repository failed:" + e);
			repos = new Repository();
			repos.setFilename(FILENAME);
			repos.getItems().addAll(init_temp_repository2());
			repos.save();
		}

		return repos;
	}

	public static Repository get_agent_repos(String filename,
			String sourceFolder) throws Exception {

		Repository repos;
		try {
			repos = new Repository(filename);
			repos.sourceFolder = sourceFolder;
		} catch (Exception e) {
			repos = new Repository();
			repos.setFilename(filename);
			repos.getItems().addAll(init_temp_repository());
			repos.save();
		}
		return repos;
	}

	public RepItem getItemByName(String name) {
		for (RepItem ri : items) {
			// get protocol name
			if (ri.getName().equals(name))
				return ri;
		}
		return null;
	}
}