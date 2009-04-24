package negotiator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AgentID {

	@XmlAttribute
	String ID;
	public AgentID(){
	
	}
	public AgentID(String id) {
		this.ID = id;
	}
}
