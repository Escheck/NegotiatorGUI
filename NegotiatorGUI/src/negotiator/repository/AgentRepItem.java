package negotiator.repository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Agent;
import negotiator.Global;
import negotiator.exceptions.Warning;
import negotiator.utility.UTILITYSPACETYPE;

/**
 * This repository item contains all info about an agent that can be loaded.
 * 
 * @author wouter
 */
@XmlRootElement
public class AgentRepItem implements RepItem {
	private static final long serialVersionUID = 2395318378966487611L;
	/**
	 * the key: short but unique name of the agent as it will be known in the
	 * nego system. This is an arbitrary but unique label for this TYPE of
	 * agent. Note that there may still be multiple actual agents of this type
	 * during a negotiation.
	 */
	@XmlAttribute
	String agentName;

	/**
	 * This can now be two things:
	 * <ul>
	 * <li>a class path, eg "agents.anac.y2010.AgentFSEGA.AgentFSEGA". In this
	 * case, the agent must be on the class path to load.
	 * <li>a full path, eg
	 * "/Volumes/documents/NegoWorkspace3/NegotiatorGUI/src/agents/anac/y2010/AgentFSEGA/AgentFSEGA.java"
	 * . In this case, we can figure out the class path ourselves, but the ref
	 * is system dependent (backslashes on windows) and might be absolute path.
	 * </ul>
	 * {@see #load()}
	 * */
	@XmlAttribute
	String classPath;
	/** description of this agent */
	@XmlAttribute
	String description;
	/** Parameters of the agent, for example a concession parameter */
	@XmlAttribute
	String params;

	/**
	 * @return true if agentName and classPath equal. Note that agentName alone
	 *         is sufficient to be equal as keys are unique.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof AgentRepItem))
			return false;
		return agentName.equals(((AgentRepItem) o).agentName)
				&& classPath.equals(((AgentRepItem) o).classPath);
	}

	/**
	 * Empty constructor. Do not use. Only here and public because the XML
	 * serializer needs it.
	 */
	public AgentRepItem() {
	}

	public AgentRepItem(String aName, String cPath, String desc) {
		agentName = aName;
		classPath = cPath;
		description = desc;
	}

	public AgentRepItem(String aName, String cPath, String desc, String param) {
		agentName = aName;
		classPath = cPath;
		description = desc;
		params = param;
	}

	/**
	 * construct the item given the file. We check that the file actually loads
	 * in and throw if we can't load it. name will be set to
	 * {@link Agent#getName()} of the file. description will be constructed
	 * using name and {@link Agent#getVersion()}.
	 * 
	 * 
	 * @param classFile
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws ClassCastException
	 * @throws IOException
	 */
	public AgentRepItem(File classFile) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, ClassCastException,
			IllegalArgumentException, IOException {
		Agent agent = (Agent) Global.loadClassFromFile(classFile);
		String version = agent.getVersion();
		agentName = agent.getName();
		if (agentName == null) {
			agentName = classFile.getName();
		}
		if (agent.getSupportedNegotiationSetting().getUtilityspaceType() == UTILITYSPACETYPE.NONLINEAR) {
			agentName += " (compatible with non-linear utility spaces)";
		}
		classPath = classFile.getCanonicalPath();

		description = agentName
				+ (version == null || version.equals("unknown")
						|| version.isEmpty() ? "" : " (" + version + ")");

	}

	public String getName() {
		return agentName;
	}

	public String getClassPath() {
		return classPath;
	}

	/**
	 * Get the version of this agent.
	 * 
	 * @return version of this agent.
	 */
	public String getVersion() {

		try {
			return getInstance().getVersion();

			// we don't really need the static call, instantiating shouldn't be
			// expensive.
			// return "" + callStaticAgentFunction("getVersion", new Object[0]);

		} catch (Exception e) {
			new Warning("can't get version for " + agentName + " :", e); // e.printStackTrace();
		}
		return "ERR";
	}

	public String getParams() {
		return params;
	}

	public String getDescription() {
		return description;
	}

	public String toString() {
		return agentName;
	}

	/**
	 * Try to load the agent that this reference points to.
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws ClassCastException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public Agent getInstance() throws MalformedURLException,
			ClassCastException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		return Global.loadAgent(classPath);
	}
}