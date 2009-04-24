package negotiator.test;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import agents.SimpleAgent;
import negotiator.*;
import negotiator.actions.*;
import negotiator.exceptions.Warning;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.repository.DomainRepItem;
import negotiator.repository.Repository;
import junit.framework.TestCase;

public class XMLSerialization extends TestCase {

	public void testActionXMLSerialization() {
		Agent agent = new SimpleAgent();
		agent.setAgentID(new AgentID("this is me!"));
		Domain domain = null;
		try {
			DomainRepItem item = (DomainRepItem) (Repository.get_domain_repos()
					.getItems().get(0));
			domain = new Domain(item.getURL().getFile());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail("Can't load domain");
		}
		Bid bid = domain.getRandomBid();
		Action offer = new Offer(agent.getAgentID(), bid);
		StringWriter writer =null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Action.class,
					Offer.class, Accept.class, EndNegotiation.class, Bid.class,
					AgentID.class, ValueDiscrete.class,
					ValueReal.class, ValueInteger.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					new Boolean(true));
			writer = new StringWriter();
			marshaller.marshal(new JAXBElement(new QName("action"),
					Action.class, offer), writer);
			System.out.println(writer.toString());
		} catch (Exception e) {
			new Warning("xml save failed: " + e); // e.printStackTrace();
		}
		try { 
			JAXBContext jaxbContext = JAXBContext.newInstance(Action.class,
					Offer.class, Accept.class, EndNegotiation.class, Bid.class,
					AgentID.class, ValueDiscrete.class,
					ValueReal.class, ValueInteger.class);
			Unmarshaller marshaller = jaxbContext.createUnmarshaller();	
			Object obj = marshaller.unmarshal(new StringReader(writer.toString()));
			Action resOffer = (Action)obj;
			failNotEquals("Problem with xml serialization", offer, resOffer);
		} catch (Exception e) {
			//e. 
			e.printStackTrace();
		}

	}



}
