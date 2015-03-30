package negotiator.events;


import negotiator.Bid;
import negotiator.session.Session;

public class MultipartyNegotiationSessionEvent extends NegotiationEvent
{
    private Session session;
    private Bid agreement;

    public MultipartyNegotiationSessionEvent(Object source, Session session, Bid agreement)
    {

        super(source);
        this.session = session;
        this.agreement = agreement;
    }

    public Session getSession()
    {
        return session;
    }

    public Bid getAgreement()
    {
        return agreement;
    }
}
