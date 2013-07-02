package negotiator.utility;

public class Bound {

	private int issueIndex;
	private int min;
	private int max; 
	//the type of min and max can be updated to real for real-issue values
	
	
	public Bound(int issueIndex, int min, int max) {
			this.setIssueIndex(issueIndex);
			this.setMin(min);
			this.setMax(max);
		}

	public Bound(String issueIndex, String min, String max) {
		this.setIssueIndex(Integer.parseInt(issueIndex));
		this.setMin(Integer.parseInt(min));
		this.setMax(Integer.parseInt(max));
	}
	
	public int getIssueIndex() {
		return issueIndex;
	}
	public void setIssueIndex(int issueIndex) {
		this.issueIndex = issueIndex;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}	
	
}
