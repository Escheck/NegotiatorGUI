package negotiator.actions;

import negotiator.parties.NegotiationParty;

import java.util.ArrayList;

/**
 * This class is used to createFrom an action which symbolizes
 * that an agent accepts an offer.
 *
 * @author David Festen
 */
public class InformPartyList extends Action
{
    ArrayList<NegotiationParty> parties = new ArrayList<NegotiationParty>();

    public InformPartyList(ArrayList<NegotiationParty> parties)
    {
        this.parties = parties;
    }

    public ArrayList<NegotiationParty> getParties()
    {
        return parties;
    }

    /**
     * Enforces that actions implements a string-representation.
     */
    @Override
    public String toString()
    {
        String stringRepr = "Parties: ";
        for (int i = 0; i < parties.size(); i++)
        {
            stringRepr += parties.toString();
            if (i < parties.size() - 1) stringRepr += ", ";
        }
        return stringRepr;
    }
}