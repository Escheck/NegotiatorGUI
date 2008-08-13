package negotiator.repository;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.io.*;

@XmlRootElement
public class Repository
{
		@XmlElementWrapper
		@XmlAnyElement
		ArrayList<RepItem> items;
		
		public Repository() { 
			items=new ArrayList<RepItem>();
		}
		public static Repository load(String fileName) throws Exception {
			Repository rep = null;
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Repository.class);		
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
				rep = (Repository)( unmarshaller.unmarshal(new File(fileName)));
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("Repository file not foud");
				}
			return rep;
		}
		public static void save(Repository rep) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Repository.class, AgentRepItem.class);		
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						   new Boolean(true));

				marshaller.marshal(new JAXBElement(new QName("repository"),Repository.class, rep),new File("rep.xml"));
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
}