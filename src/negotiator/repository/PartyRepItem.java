package negotiator.repository;

import static negotiator.repository.Property.IS_MEDIATOR;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Global;
import negotiator.parties.NegotiationParty;

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

	/**************************** PRIVATE FIELDS. DO NOT ACCESS DIRECTLY!!! **********************/
	// only public to enable XML serialization JAXB.
	// These fields are all just cached results from the real agent object.
	@XmlAttribute
	String partyName; // must be short version of class path.

	/**
	 * This can be two things:
	 * <ul>
	 * <li>a class path, eg "agents.anac.y2010.AgentFSEGA.AgentFSEGA". In this
	 * case, the agent must be on the class path to load.
	 * <li>a full path, eg
	 * "/Volumes/documents/NegoWorkspace3/NegotiatorGUI/src/agents/anac/y2010/AgentFSEGA/AgentFSEGA.java"
	 * . In this case, we can figure out the class path ourselves and load it.
	 * </ul>
	 * */
	@XmlAttribute
	String classPath = "";
	/**
	 * file path including the class name
	 */

	@XmlAttribute
	String description = "";
	/**
	 * description of this agent
	 */

	@XmlAttribute
	String protocolClassPath = "";

	/**
	 * Completely unused, we need to figure this out.
	 */
	@XmlElementWrapper(name = "properties")
	@XmlElement(name = "property")
	List<String> properties = new ArrayList<String>();

	/**
	 * Do not use this: It's only here to support XML de-serialization.
	 */
	public PartyRepItem() {
	}

	/**
	 * 
	 * @param path
	 *            full.path.to.class or file name.
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws MalformedURLException
	 */
	public PartyRepItem(String path) throws MalformedURLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		classPath = path;
		init();
	}

	public NegotiationParty load() throws MalformedURLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		return (NegotiationParty) Global.loadObject(classPath);

	}

	/**
	 * Init our fields to cache the party information. party must have been set
	 * before getting here.
	 * 
	 * @param party
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws MalformedURLException
	 */
	private void init() throws MalformedURLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		NegotiationParty party1 = load();
		partyName = party1.getClass().getSimpleName();
		description = party1.getDescription();
		protocolClassPath = party1.getProtocol().getClass().getCanonicalName();

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
		return classPath.equals(((PartyRepItem) o).classPath);
	}

	public String getName() {
		if (partyName.isEmpty()) {
			partyName = classPath;
		}
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