package negotiator.repository;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class RepositoryItemType {
    
    // produce a wrapper XML element around this collection
    @XmlElementWrapper(name = "agentRepItems")
    // maps each member of this list to an XML element named appointment
    @XmlElement(name = "agentRepItem")
    //private List<Appointment> appointments;
    private ArrayList<AgentRepItem> agentRepItem;
    // produce a wrapper XML element around this collection
    @XmlElementWrapper(name = "domainRepItem")
    // maps each member of this list to an XML element named birthday
    @XmlElement(name = "domainRepItem")
    //private List<Birthday> birthdays;
    private ArrayList<DomainRepItem> domainRepItem;
    
    public RepositoryItemType() {}
    
    public RepositoryItemType(ArrayList<AgentRepItem> agentRepItem, ArrayList<DomainRepItem> domainRepItem) {
        this.agentRepItem = agentRepItem;
        this.domainRepItem = domainRepItem;
    }

    public ArrayList<AgentRepItem> getAgentRepItem() {
        return agentRepItem;
    }

    public ArrayList<DomainRepItem> getDomainRepItem() {
        return domainRepItem;
    }
}


