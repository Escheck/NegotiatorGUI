package negotiator.decoupledframework;

import negotiator.Bid;

public class OutcomeTuple {

	Bid lastBid;
	String name;
	double time;
	int agentASize;
	int agentBSize;
	
	public OutcomeTuple(Bid lastBid, String name, double time, int agentASize, int agentBSize){
		this.lastBid = lastBid;
		this.name = name;
		this.time = time;
		this.agentASize = agentASize;
		this.agentBSize = agentBSize;
	}

	public int getAgentASize() {
		return agentASize;
	}

	public void setAgentASize(int agentASize) {
		this.agentASize = agentASize;
	}

	public int getAgentBSize() {
		return agentBSize;
	}

	public void setAgentBSize(int agentBSize) {
		this.agentBSize = agentBSize;
	}

	public Bid getLastBid() {
		return lastBid;
	}

	public void setLastBid(Bid lastBid) {
		this.lastBid = lastBid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
	
	public String toString() {
		return "LastBid: " + lastBid + ", Name of AC: " + name + ", Time of agreement: " + time + 
				" agentASize: " + agentASize + " agentBSize: " + agentBSize;
	}
}
