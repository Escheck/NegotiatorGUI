package negotiator.repository;

import java.util.ArrayList;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RepositoryItemTypeAdapter extends XmlAdapter<RepositoryItemType, ArrayList<RepItem>> {

    // adapt original Java construct to a type, NotificationsType,
    // which we can easily map to the XML output we want
    public RepositoryItemType marshal(ArrayList<RepItem> events) throws Exception {
        ArrayList<DomainRepItem> appointments = new ArrayList<DomainRepItem>();
        ArrayList<AgentRepItem> birthdays = new ArrayList<AgentRepItem>();
        
        for (RepItem e : events) {
            if (e instanceof DomainRepItem) {
                appointments.add((DomainRepItem)e);
            } else {
                birthdays.add((AgentRepItem)e);              
            }
        }        
        return new RepositoryItemType(birthdays, appointments);
    }

    // map XML type to Java
    public ArrayList<RepItem> unmarshal(RepositoryItemType notifications) throws Exception {
        ArrayList<RepItem> events = new ArrayList<RepItem>();
        events.addAll(notifications.getAgentRepItem());
        events.addAll(notifications.getDomainRepItem());
        return events;
    }
}
  
