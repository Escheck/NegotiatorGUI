package negotiator.decoupledframework.repository;

public class BiddingStrategyItem extends BOArepItem {

	String defaultOM;
	
	public BiddingStrategyItem(String classPath, String tooltip, String defaultOM) {
		super(classPath, tooltip);
		this.defaultOM = defaultOM;
		if (defaultOM == null) {
			defaultOM = "";
		}
	}

	public String getDefaultOM() {
		return defaultOM;
	}

	public void setDefaultOM(String defaultOM) {
		this.defaultOM = defaultOM;
	}
}
