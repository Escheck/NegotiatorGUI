package negotiator.config;

import negotiator.Deadline;
import negotiator.protocol.MultilateralProtocol;

/**
 * Implementation of MultilateralTournamentConfigurationInterface
 *
 * @author W.Pasman 27jul15
 */
public class MultilateralTournamentConfiguration implements
		MultilateralTournamentConfigurationInterface {

	private Deadline deadline;
	private MultilateralProtocol protocol;

	@Override
	public Deadline getDeadline() {
		return deadline;
	}

	public void setDeadline(Deadline dl) {
		deadline = dl;
	}

	@Override
	public MultilateralProtocol getProtocol() throws Exception {
		return protocol;
	}

	@Override
	public void setProtocol(MultilateralProtocol newProtocol) {
		protocol = newProtocol;
	}

	@Override
	public int getNumTournaments() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumAgentsPerSession() {
		// TODO Auto-generated method stub
		return 0;
	}

}
