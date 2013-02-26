package negotiator.boaframework;

import java.io.Serializable;

public class SessionData implements Serializable {
	
	private static final long serialVersionUID = -9213385945385035097L;

	private Serializable biddingStrategyData;
	
	private Serializable opponentModelData;
	
	private Serializable acceptanceStrategyData;
	
	public SessionData() {
		biddingStrategyData = null;
		opponentModelData = null;
		acceptanceStrategyData = null;
	}
	
	public Serializable getData(ComponentsEnum type) {
		Serializable result = null;
		if (type == ComponentsEnum.BIDDINGSTRATEGY) {
			result = biddingStrategyData;
		} else if (type == ComponentsEnum.OPPONENTMODEL) {
			result = opponentModelData;
		} else if (type == ComponentsEnum.ACCEPTANCESTRATEGY) {
			result = acceptanceStrategyData;
		}
		return result;
	}

	public void setData(ComponentsEnum component, Serializable data) {
		if (component == ComponentsEnum.BIDDINGSTRATEGY) {
			biddingStrategyData = data;
		} else if (component == ComponentsEnum.OPPONENTMODEL) {
			opponentModelData = data;
		} else if (component == ComponentsEnum.ACCEPTANCESTRATEGY) {
			acceptanceStrategyData = data;
		}
	}

	public boolean isEmpty() {
		return biddingStrategyData == null && opponentModelData == null && acceptanceStrategyData == null;
	}
}