package negotiator.repository;

import static negotiator.repository.Property.IS_MEDIATOR;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This repository item contains all info about an agent that can be loaded.
 *
 * @author Reyhan modifies the AgentRepItem
 * @author David Festen added the properties list
 */

@XmlRootElement
public class PartyRepItem implements RepItem {
	/**
	 * getVersion is bit involved, need to call the agent getVersion() to get it
	 */
	private static final Class[] parameters = new Class[] { URL.class };
	@XmlAttribute
	String partyName;
	/**
	 * the key: short but unique name of the agent as it will be known in the
	 * nego system. This is an arbitrary but unique label for this TYPE of
	 * agent. Note that there may still be multiple actual agents of this type
	 * during a negotiation.
	 */
	@XmlAttribute
	String classPath;
	/**
	 * file path including the class name
	 */
	@XmlAttribute
	String description;
	/**
	 * description of this agent
	 */

	// @XmlAttribute //RA: For multiparty negotiation, there are two type of
	// agents: mediator and negotiating party
	// Boolean isMediator; /** whether the party is a mediator */

	@XmlElementWrapper(name = "properties")
	@XmlElement(name = "property")
	List<String> properties;
	@XmlAttribute
	String protocolClassPath;

	/**
	 * Do not use this: It's only here to support XML de-serialization.
	 */
	public PartyRepItem() {
		properties = new ArrayList<String>();
	}

	public PartyRepItem(String classPath, String protocolClassPath) {
		this.partyName = classPath;
		this.classPath = classPath;
		this.description = classPath;
		this.protocolClassPath = protocolClassPath;
		this.properties = new ArrayList<String>();
	}

	public String getProtocolClassPath() {
		return protocolClassPath;
	}

	/**
	 * @returns true if partyName and classPath equal. Note that partyName alone
	 *          is sufficient to be equal as keys are unique.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof PartyRepItem))
			return false;
		return partyName.equals(((PartyRepItem) o).partyName)
				&& classPath.equals(((PartyRepItem) o).classPath);
	}

	public String getName() {
		return partyName;
	}

	public String getClassPath() {
		return classPath;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getIsMediator() {
		return properties.contains(IS_MEDIATOR);
	} // RA

	public String toString() {
		return "PartyRepositoryItem[" + partyName + "," + classPath + ","
				+ description + ", is mediator=" + getIsMediator().toString()
				+ "]";
	} // RA
}