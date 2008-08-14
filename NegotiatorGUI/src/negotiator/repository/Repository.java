package negotiator.repository;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
import javax.xml.namespace.QName;
import java.io.*;

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
				JAXBContext jaxbContext = JAXBContext.newInstance(Repository.class, DomainRepItem.class,AgentRepItem.class,ProfileRepItem.class);		
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						   new Boolean(true));

				marshaller.marshal(new JAXBElement(new QName("repository"),Repository.class, this),new File(fileName));
				} catch (Exception e) {
					e.printStackTrace();
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
}