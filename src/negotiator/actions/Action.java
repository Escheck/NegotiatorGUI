/*
 * Action.java
 *
 * Created on November 6, 2006, 10:10 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;

import negotiator.Agent;

/**
 * @author Dmytro Tykhonov
 * 
 */
public class Action {
    protected   Agent       agent;
    
    /** Creates a new instance of Action 
     * @param agent is the agent performing the action. 
     * Note that by referring to the agent class object we effectively prevent the agent
     * from garbage collection. */
    public Action(Agent agent) {
        this.agent = agent;
    }
    public Agent getAgent() {
        return agent;
    }
    public String toString() {
        return "(Unknown action)";
    }    
}
