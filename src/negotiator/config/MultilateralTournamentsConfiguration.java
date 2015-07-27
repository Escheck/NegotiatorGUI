package negotiator.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import negotiator.Deadline;
import negotiator.exceptions.Warning;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;

/**
 * A list of {@link MultilateralTournamentConfiguration}s. It is possible to
 * {@link #load(File)} and {@link #save(File)} to file using XML format.
 * 
 * @author W.Pasman 27jul15
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MultilateralTournamentsConfiguration {

	/**
	 * The list of {@link MultilateralTournamentConfiguration}s
	 */
	@XmlElementWrapper(name = "tournaments")
	@XmlElement(name = "tournament")
	private List<MultilateralTournamentConfiguration> tournaments;

	/**
	 * Default empty tournaments. Mainly for XML support
	 */
	public MultilateralTournamentsConfiguration() {
		tournaments = new ArrayList<MultilateralTournamentConfiguration>();
	}

	/**
	 * Create tournaments with given set
	 * 
	 * @param tours
	 *            tournaments to run in this order.
	 */
	public MultilateralTournamentsConfiguration(
			List<MultilateralTournamentConfiguration> tours) {
		tournaments = tours;
	}

	public List<MultilateralTournamentConfiguration> getTournaments() {
		return tournaments;
	}

	public static MultilateralTournamentsConfiguration load(File file)
			throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(
				MultilateralTournamentsConfiguration.class,
				MultilateralTournamentConfiguration.class, PartyRepItem.class,
				ProfileRepItem.class, MultiPartyProtocolRepItem.class,
				DomainRepItem.class, AgentRepItem.class, Deadline.class,
				ProtocolRepItem.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller
				.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
		MultilateralTournamentsConfiguration config = (MultilateralTournamentsConfiguration) (unmarshaller
				.unmarshal(file));
		return config;
	}

	public void save(File file) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(
					MultilateralTournamentsConfiguration.class,
					MultilateralTournamentConfiguration.class,
					ProfileRepItem.class, DomainRepItem.class,
					AgentRepItem.class, PartyRepItem.class,
					ProtocolRepItem.class, MultiPartyProtocolRepItem.class,
					Deadline.class, ProtocolRepItem.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					new Boolean(true));

			marshaller.marshal(new JAXBElement(new QName(
					"MultilateralTournamentConfiguration"),
					MultilateralTournamentsConfiguration.class, this), file);
		} catch (Exception e) {
			new Warning("xml save failed: " + e);
			e.printStackTrace();
		}
	}

}
