package negotiator.gui.negosession;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.AgreementEvent;
import negotiator.events.NegotiationEvent;
import negotiator.events.SessionFailedEvent;
import negotiator.gui.progress.DataKey;
import negotiator.gui.progress.DataKeyTableModel;

/**
 * Tracks the Multiparty tournament and keeps a {@link DataKeyTableModel} up to
 * date. This determines the layout of log file and tables. This can be listened
 * to and shown in a table.
 * 
 * @author W.Pasman
 *
 */
@SuppressWarnings("serial")
public class MultiPartyDataModel extends DataKeyTableModel implements
		MultipartyNegotiationEventListener {

	public MultiPartyDataModel(int numAgents) {
		super(makeDataModel(numAgents));
	}

	/**
	 * create the dataModel. This determines what is logged, the exact order of
	 * the columns, etc. Currently it makes a table with ALL known
	 * {@link DataKey}s.
	 * 
	 * @return datamodel that layouts data.
	 */
	private static LinkedHashMap<DataKey, Integer> makeDataModel(int numAgents) {

		LinkedHashMap<DataKey, Integer> colspec = new LinkedHashMap<DataKey, Integer>();
		for (DataKey key : DataKey.values()) {
			if (key == DataKey.AGENTS || key == DataKey.FILES
					|| key == DataKey.UTILS || key == DataKey.DISCOUNTED_UTILS) {
				colspec.put(key, numAgents);
			} else {
				colspec.put(key, 1);
			}
		}

		return colspec;
	}

	/******************** implements MultipartyNegotiationEventListener **********************/
	@Override
	public void handleEvent(NegotiationEvent e) {
		if (e instanceof AgreementEvent) {
			AgreementEvent e1 = (AgreementEvent) e;
			addRow(e1.getValues());
		} else if (e instanceof SessionFailedEvent) {
			Map<DataKey, Object> row = new HashMap<DataKey, Object>();
			row.put(DataKey.EXCEPTION, ((SessionFailedEvent) e).toString());
			addRow(row);
		}

	}
}
