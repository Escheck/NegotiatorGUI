/*
 * UIAgent.java
 *
 * Created on November 16, 2006, 10:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.gui;

import javax.swing.JOptionPane;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.NegotiationTemplate;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.utility.UtilitySpace;
import negotiator.Domain;

/**
 *
 * @author dmytro
 */
public class UIAgent extends Agent{
    private Action opponentAction=null;
    private EnterBidDialog ui=null;
    private Bid myPreviousBid=null;
    /** Creates a new instance of UIAgent */
    
    public UIAgent() {
        ui = new EnterBidDialog(this, null, true);
    }
    
    protected void init(int sessionNumber, int sessionTotalNumber, Domain d) {
        super.init (sessionNumber, sessionTotalNumber, d);
    }
    
    public void ReceiveMessage(Action opponentAction) {
        this.opponentAction = opponentAction;
        if(opponentAction instanceof Accept)
            JOptionPane.showMessageDialog(null, "Opponent accepted your last offer.");

        if(opponentAction instanceof EndNegotiation)
            JOptionPane.showMessageDialog(null, "Opponent canceled the negotiation session");

        return;
    }
    
    public Action chooseAction() {
        Action action = ui.askUserForAction(opponentAction, myPreviousBid, domain);
        if((action != null)&&(action instanceof Offer)) myPreviousBid=((Offer)action).getBid(); 
        return action;
    }

    public void loadUtilitySpace(String fileName) {
        //load the utility space
        utilitySpace = new UtilitySpace(domain, fileName);
        return;
    }
    
    public boolean isUIAgent() { return true; }

  
}
