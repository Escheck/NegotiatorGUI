package negotiator.tournament.VariablesAndValues;

import negotiator.boaframework.DecoupledAgentInfo;

public class DecoupledAgentValue extends TournamentValue
{
	DecoupledAgentInfo value;	
	
	public DecoupledAgentValue(DecoupledAgentInfo val) { value = val; }
	public String toString() { return value.toString(); }
	public DecoupledAgentInfo getValue() { return value; }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DecoupledAgentValue other = (DecoupledAgentValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}