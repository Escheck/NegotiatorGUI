/*
 * NegotiationOutcome.java
 *
 * Created on November 21, 2006, 1:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

/**
 *
 * @author dmytro
 */
public class NegotiationOutcome {
    public int sessionNumber;
    public String agentAname;
    public String agentBname;
    public String agentAutility;
    public String agentButility;
    public String ErrorRemarks; // non-null if something happens crashing the negotiation

    /** Creates a new instance of NegotiationOutcome */
    public NegotiationOutcome(int sessionNumber, 
                    String agentAname,
                    String agentBname,
                    String agentAutility,
                    String agentButility,
                    String err) {
        this.sessionNumber = sessionNumber;
        this.agentAutility = agentAutility;
        this.agentButility = agentButility;
        this.agentAname = agentAname;
        this.agentBname = agentBname;    
        ErrorRemarks=err;
    }
    public String toString() {
        return String.valueOf(sessionNumber) + ";"+agentAname + ";" + agentBname + ";"+agentAutility+ ";"+agentButility;
    }
   
}
