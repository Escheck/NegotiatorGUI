package negotiator.repository;

import java.util.ArrayList;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.io.File;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import negotiator.Domain;
import negotiator.Global;
import negotiator.ScenarioValidator;
import negotiator.exceptions.Warning;
import negotiator.utility.UtilitySpace;
/**
 * Repository contains a set of known files
 * This can be agent files or domain+profile files.
 * @author  Dmytro Tykhonov, W.Pasman
 * 
 */
@XmlRootElement
public class RepositoryNAlexAnyMore
{

	@XmlJavaTypeAdapter(RepositoryItemTypeAdapter.class)
	ArrayList<RepItem> items; // the items in the domain, either AgentRepItems or DomainRepItems 
	@XmlAttribute
	String fileName; // the filename of this repository.

	String sourceFolder=null;

	private static Repository domainRepos = null;
	public RepositoryNAlexAnyMore() { 
		items=new ArrayList<RepItem>();
	}

	public RepositoryNAlexAnyMore(String fn) throws Exception {
		setFilename(fn);
		copyFrom(load(fileName));
	}

	public void setFilename(String fn) { 
		fileName=fn;
	}

	public String getFilename() { return fileName; }

	public void copyFrom(Repository rep) {
		items=rep.getItems();
	}

	/** @author Dmytro */
	public Repository load(String fileName) throws Exception {
		Repository rep = null;
		JAXBContext jaxbContext = JAXBContext.newInstance(Repository.class,ProfileRepItem.class,MultiPartyProtocolRepItem.class, DomainRepItem.class,AgentRepItem.class, PartyRepItem.class);		
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
		rep = (Repository)( unmarshaller.unmarshal(new File(fileName)));
		unmarshaller = null;
		jaxbContext = null;
		return rep;
	}
	
	/** @author Dmytro */
	public void save() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Repository.class, ProfileRepItem.class,DomainRepItem.class,AgentRepItem.class, PartyRepItem.class, ProtocolRepItem.class, MultiPartyProtocolRepItem.class);		
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					new Boolean(true));

			marshaller.marshal(new JAXBElement(new QName("repository"),Repository.class, this),new File(fileName));
		} catch (Exception e) {
			new Warning("xml save failed: "+e); //e.printStackTrace();
		}

	}
	
	public ArrayList<RepItem> getItems() { return items; }

	/** @returns AgentRepItem of given className, or null if none exists */
	public AgentRepItem getAgentOfClass(String className)
	{
		for (RepItem it: items) {
			if (it instanceof AgentRepItem)
				if (((AgentRepItem)it).classPath.equals(className))
					return (AgentRepItem) it;
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
		String ret="{";
		for (RepItem i: items) {
			ret=ret+i+",";
		}
		ret=ret+"}";
		return ret;

	}

	public static DomainRepItem getDomainByName(String name) throws Exception {
		Repository domRep = get_domain_repos();
		DomainRepItem domainRepItem = null;
		for(RepItem  tmp : domRep.getItems()) {
			if(((DomainRepItem)tmp).url.toString().equals(name)) {
				domainRepItem =(DomainRepItem)tmp;
				break;
			}
		}
		return domainRepItem; 
	}
	
	public Domain getDomain(DomainRepItem domainRepItem) 
	{
		String file = domainRepItem.getURL().getFile();
		return getDomain(file);
	}

	public Domain getDomain(String file)
	{
		Domain domain = null;
		try {
			if((sourceFolder!=null)&&(!sourceFolder.equals("")))
				domain = new Domain(sourceFolder +"/"+ file);
			else 
				domain = new Domain(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return domain;
	}

	public UtilitySpace getUtilitySpace(Domain domain, ProfileRepItem profile) 
	{
		String file = profile.getURL().getFile();
		return getUtilitySpace(domain, file);
	}

	public UtilitySpace getUtilitySpace(Domain domain, String file)
	{
		UtilitySpace us = null;			
		try {
			if((sourceFolder!=null)&&(!sourceFolder.equals(""))) 
				us = new UtilitySpace(domain, sourceFolder+"/"+ file);
			else 
				us = new UtilitySpace(domain, file);
		} catch (Exception e) {
			System.out.println("Failed to load space:" +file);
			e.printStackTrace();
		}
		return us;
	}
	public boolean existUtilitySpace(Domain domain, ProfileRepItem profile) {
		UtilitySpace us = null;			
		try {
			File file;
			if((sourceFolder!=null)&&(!sourceFolder.equals(""))) file = new File(sourceFolder+"/"+ profile.getURL().getFile());
			else file = new File(profile.getURL().getFile());
			return file.exists();
		} catch (Exception e) {
			System.out.println("Failed to load space:" +profile.getURL().getFile());
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Uses Jax to load the {@link Repository}.
	 */
	public static Repository get_domain_repos(String filename, String sourceFolder) throws Exception {
		if(domainRepos!=null ) return domainRepos;
		Repository repos;
		try {
			repos=new Repository(filename);
			domainRepos = repos;
			repos.sourceFolder = sourceFolder;	
			String result = ScenarioValidator.validateDomainRepository(domainRepos);
			if (!result.equals("")) {
				JTextArea textArea = new JTextArea("Errors were found in the scenario XML files. It is " +
													"advised to correct them to avoid incompatibilities " +
													"with some agents.\n\n" + result);  
		        textArea.setEditable(false);
				textArea.setLineWrap(true);  
		        textArea.setWrapStyleWord(true);  
		        textArea.setMargin(new Insets(5,5,5,5));  
		        JScrollPane scrollPane = new JScrollPane(textArea);
		        scrollPane.setPreferredSize(new Dimension(700,500)); 
		        Object message = scrollPane;  
				JOptionPane.showMessageDialog(null, message, "Scenario errors", 0);
			}
		} catch (Exception e) {
			repos=new Repository();
			repos.setFilename(filename);
			repos.getItems().addAll(makedemorepository());
			repos.save();
		}
		return repos;
	}

	/****************** code that creates repos if none exists ********************/
	public static Repository get_domain_repos() throws Exception
	{
		final String FILENAME= Global.DOMAIN_REPOSITORY; // ASSUMPTION  there is only one domain repository
		return get_domain_repos(FILENAME,"");

	}

	static ArrayList<RepItem> makedemorepository() throws Exception
	{
		ArrayList<RepItem> its=new ArrayList<RepItem>();

		//DomainRepItem dri=new DomainRepItem(new URL("file:/Volumes/Documents/Wouter/Negotiator/NegoWorkspace/Negotiator/etc/templates/laptopdomain/laptop_domain.xml"));
		DomainRepItem dri=new DomainRepItem(new URL("file:H:/Negotiator/negotiator/templates/laptopdomain/laptop_domain.xml"));


		//dri.getProfiles().add(new ProfileRepItem(new URL("file:/Volumes/Documents/Wouter/Negotiator/NegoWorkspace/Negotiator/etc/templates/laptopdomain/laptop_seller_utility.xml"),dri));
		//dri.getProfiles().add(new ProfileRepItem(new URL("file:/Volumes/Documents/Wouter/Negotiator/NegoWorkspace/Negotiator/etc/templates/laptopdomain/laptop_empty_utility.xml"),dri));
		dri.getProfiles().add(new ProfileRepItem(new URL("file:H:/Negotiator/negotiator/templates/laptopdomain/laptop_seller_utility.xml"),dri));
		dri.getProfiles().add(new ProfileRepItem(new URL("file:H:/Negotiator/negotiator/templates/laptopdomain/laptop_empty_utility.xml"),dri));
		its.add(dri);

		dri=new DomainRepItem(new URL("file:domain2"));
		dri.getProfiles().add(new ProfileRepItem(new URL("file:profilec"),dri));
		dri.getProfiles().add(new ProfileRepItem(new URL("file:profiled"),dri));
		dri.getProfiles().add(new ProfileRepItem(new URL("file:profilee"),dri));
		its.add(dri);

		return its;
	}


	static ArrayList<RepItem> init_temp_repository()
	{
		ArrayList<RepItem> items=new ArrayList<RepItem>();
		items.add(new 	AgentRepItem("Warning: Repository not loaded", "", ""));
		items.add(new 	AgentRepItem("Please check that Genius can find agentrepository.xml and domainrepository.xml", "", ""));
		items.add(new 	AgentRepItem("And make sure /etc/templates is in the same directory", "", ""));
		items.add(new 	AgentRepItem("BayesianAgent", "agents.BayesianAgent", "simple agent"));
		items.add(new 	AgentRepItem("UI agent", "agents.UIAgent", "basic UI agent"));
		return items;
	}
	
	static ArrayList<RepItem> init_temp_repository2()
	{
		ArrayList<RepItem> items=new ArrayList<RepItem>();
		items.add(new PartyRepItem("Simple Party", "parties.SimpleParty", "Simple Negotiator", null));
		return items;
	}
	
	static ArrayList<RepItem> init_temp_prot_repository() {
		ArrayList<RepItem> items=new ArrayList<RepItem>();
		items.add(new 	ProtocolRepItem("Alternating Offers", "negotiator.protocol.AlternatingOffersMetaProtocol", "Alternating Offers"));
		items.add(new 	ProtocolRepItem("Auction", "negotiator.protocol.AuctionMetaProtocol", "Auction"));
		return items;

	}
	
	static ArrayList<RepItem> init_temp_multiprot_repository() {
		ArrayList<RepItem> items=new ArrayList<RepItem>();
		items.add(new  MultiPartyProtocolRepItem("Simple Mediated Multiparty Protocol", "negotiator.multiPartyProtocol.SimpleMediatedMultipartyProtocol", "Simple Mediated Multiparty Protocol", null, null));
		return items;
	}
	
	
	public static Repository getProtocolRepository() {
		final String FILENAME = Global.PROTOCOL_REPOSITORY;
		Repository repos;

		try {
			repos=new Repository(FILENAME);
		} catch (Exception e) {
			System.out.println("load of saved repository failed:"+e);
			repos=new Repository();
			repos.setFilename(FILENAME);
			repos.getItems().addAll(init_temp_prot_repository());
			repos.save();
		}

		return repos;		
	}
	
	public static Repository getMultiPartyProtocolRepository() {
		final String FILENAME="multipartyprotocolrepository.xml";
		Repository repos;

		try {
			repos=new Repository(FILENAME);
		} catch (Exception e) {
			System.out.println("load of saved repository failed:"+e);
			repos=new Repository();
			repos.setFilename(FILENAME);
			repos.getItems().addAll(init_temp_multiprot_repository());
			repos.save();
		}

		return repos;		
	}
	
	public static Repository getProtocolRepository(String filename, String sourceFolder) throws Exception {

		Repository repos;
		try {
			repos=new Repository(filename);				
			repos.sourceFolder = sourceFolder;				
		} catch (Exception e) {
			repos=new Repository();
			repos.setFilename(filename);
			repos.getItems().addAll(init_temp_prot_repository());
			repos.save();
		}
		return repos;
	}
	

	public static Repository get_agent_repository() {
		final String FILENAME=Global.AGENT_REPOSITORY; // ASSUMPTION: there is only one agent repository
		Repository repos;

		try {
			repos=new Repository(FILENAME);
		} catch (Exception e) {
			System.out.println("load of saved repository failed:"+e);
			repos=new Repository();
			repos.setFilename(FILENAME);
			repos.getItems().addAll(init_temp_repository());
			repos.save();
		}

		return repos;
	}
	
	public static Repository get_party_repository() {
		final String FILENAME="partyrepository.xml"; // ASSUMPTION: there is only one agent reposityro
		Repository repos;

		try {
			repos=new Repository(FILENAME);
		} catch (Exception e) {
			System.out.println("load of saved repository failed:"+e);
			repos=new Repository();
			repos.setFilename(FILENAME);
			repos.getItems().addAll(init_temp_repository2());
			repos.save();
		}

		return repos;
	}	
	
	public static Repository get_agent_repos(String filename, String sourceFolder) throws Exception {

		Repository repos;
		try {
			repos=new Repository(filename);				
			repos.sourceFolder = sourceFolder;				
		} catch (Exception e) {
			repos=new Repository();
			repos.setFilename(filename);
			repos.getItems().addAll(init_temp_repository());
			repos.save();
		}
		return repos;
	}

	public RepItem getItemByName(String name)
	{
		for(RepItem ri : items)
		{
			//get protocol name
			if(ri.getName().equals(name))
				return ri;
		}
		return null;
	}
}