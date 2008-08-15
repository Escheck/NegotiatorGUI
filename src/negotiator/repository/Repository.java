package negotiator.repository;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
import javax.xml.namespace.QName;
import java.io.*;
import negotiator.exceptions.*;
/**
 * Repository contains a set of known files
 * This can be agent files or domain+profile files.
 * @author W.Pasman, Dmytro Tychonov
 * 
 */
@XmlRootElement
public class Repository
{

		@XmlJavaTypeAdapter(RepositoryItemTypeAdapter.class)
		ArrayList<RepItem> items;
		@XmlAttribute
		String fileName; // the filename of this repository.
		
		public Repository() { 
			items=new ArrayList<RepItem>();
		}
		
		public Repository(String fn) throws Exception {
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
			JAXBContext jaxbContext = JAXBContext.newInstance(Repository.class);		
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			rep = (Repository)( unmarshaller.unmarshal(new File(fileName)));
			return rep;
		}

		/** @author Dmytro */
		public void save() {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Repository.class, ProfileRepItem.class,DomainRepItem.class,AgentRepItem.class, URL.class);		
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
		public String toString() {
			String ret="{";
			for (RepItem i: items) {
				ret=ret+i+",";
			}
			ret=ret+"}";
			return ret;
		
		}
		
		
		/****************** code that creates repos if none exists ********************/
		public static Repository get_domain_repos() throws Exception
		{
			final String FILENAME="domainrepository.xml"; // ASSUMPTION  there is only one domain repository

			Repository repos;
			try {
				repos=new Repository(FILENAME);
			} catch (Exception e) {
				repos=new Repository();
				repos.setFilename(FILENAME);
				repos.getItems().addAll(makedemorepository());
				repos.save();
			}
			return repos;
		}
		
		static ArrayList<RepItem> makedemorepository() throws Exception
		{
			ArrayList<RepItem> its=new ArrayList<RepItem>();
			
			DomainRepItem dri=new DomainRepItem(new URL("file:domain1"));
			dri.getProfiles().add(new ProfileRepItem(new URL("file:profilea"),dri));
			dri.getProfiles().add(new ProfileRepItem(new URL("file:profileb"),dri));
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
			items.add(new 	AgentRepItem("aap", "/Volumes/aap.class", "apy negotiator"));
			items.add(new 	AgentRepItem("beer", "/Volumes/beer.class", "beary negotiator"));
			items.add(new 	AgentRepItem("BayesianAgent", "agents.BayesianAgent", "simple agent"));
			return items;
		}
		
		public static Repository get_agent_repository() {
			final String FILENAME="agentrepository.xml"; // ASSUMPTION: there is only one agent reposityro
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
		
}