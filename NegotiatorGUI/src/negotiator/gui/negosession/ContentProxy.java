package negotiator.gui.negosession;

import java.util.ArrayList;
import java.util.List;

import negotiator.repository.DomainRepItem;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;

/**
 * Proxy service to the {@link negotiator.repository.Repository}. This abstracts
 * all the plumbing and lets the GUI/Config just grab the requested values
 */
public final class ContentProxy {
	/**
	 * Use the repository object to fetch protocols
	 *
	 * @return A list of MultiPartyProtocolRepItem represented in
	 *         multipartprotocolsrepository.xml
	 */
	public static List<MultiPartyProtocolRepItem> fetchProtocols() {
		// initialize original list and casted list
		List<RepItem> items = Repository.getMultiPartyProtocolRepository()
				.getItems();
		List<MultiPartyProtocolRepItem> itemsCasted = new ArrayList<MultiPartyProtocolRepItem>(
				items.size());

		// cast list element-wise
		for (RepItem item : items)
			itemsCasted.add((MultiPartyProtocolRepItem) item);

		// return casted list
		return itemsCasted;
	}

	/**
	 * Use the repository object to fetch non-mediator parties
	 *
	 * @return All parties as defined in partyrepository.xml that have mediator
	 *         not set (or set to false)
	 */
	public static List<PartyRepItem> fetchParties() {
		// initialize original list and casted list
		List<RepItem> items = Repository.get_party_repository().getItems();
		List<PartyRepItem> itemsCasted = new ArrayList<PartyRepItem>(
				items.size());

		// cast list element-wise and remove mediators
		for (RepItem item : items) {
			PartyRepItem prItem = (PartyRepItem) item;
			if (!prItem.getIsMediator())
				itemsCasted.add(prItem);
		}

		// return casted list
		return itemsCasted;
	}

	/**
	 * Use the repository object to fetch non-mediator parties
	 *
	 * @param protocol
	 *            The protocol the requested parties should support
	 * @return All parties as defined in partyrepository.xml that have mediator
	 *         not set (or set to false) and have protocol set to the given
	 *         protocol
	 */
	public static List<PartyRepItem> fetchPartiesForProtocol(
			MultiPartyProtocolRepItem protocol) {
		List<PartyRepItem> items = fetchParties();
		List<PartyRepItem> filtered = new ArrayList<PartyRepItem>(items.size());
		for (PartyRepItem item : items)
			if (item.getProtocolClassPath().equals(protocol.getClassPath()))
				filtered.add(item);

		return filtered;
	}

	/**
	 * Use the repository object to fetch mediator parties
	 *
	 * @return All the parties as defined in partyrepository.xml that have
	 *         mediator set to true
	 */
	public static List<PartyRepItem> fetchMediators() {
		// initialize original list and casted list
		List<RepItem> items = Repository.get_party_repository().getItems();
		List<PartyRepItem> itemsCasted = new ArrayList<PartyRepItem>(
				items.size());

		// cast list element-wise and only add mediators
		for (RepItem item : items) {
			PartyRepItem prItem = (PartyRepItem) item;
			if (prItem.getIsMediator())
				itemsCasted.add(prItem);
		}

		// return casted list
		return itemsCasted;
	}

	/**
	 * Use the repository object to fetch mediator parties
	 *
	 * @param protocol
	 *            The protocol the requested mediator should support
	 * @return All the parties as defined in partyrepository.xml that have
	 *         mediator set to true and have protocol set to the given protocol
	 */
	public static List<PartyRepItem> fetchMediatorsForProtocol(
			MultiPartyProtocolRepItem protocol) {
		List<PartyRepItem> items = fetchMediators();
		List<PartyRepItem> filtered = new ArrayList<PartyRepItem>(items.size());
		for (PartyRepItem item : items)
			if (item.getProtocolClassPath().equals(protocol.getClassPath()))
				filtered.add(item);

		return filtered;
	}

	/**
	 * Use the repository object to fetch profiles
	 *
	 * @return All the profiles as defined in domainrepository.xmls
	 */
	public static List<ProfileRepItem> fetchProfiles() {
		try {
			Repository domainrep = Repository.get_domain_repos();
			ArrayList<ProfileRepItem> profiles = new ArrayList<ProfileRepItem>();
			for (RepItem domain : domainrep.getItems()) {
				if (!(domain instanceof DomainRepItem))
					throw new IllegalStateException(
							"Found a non-DomainRepItem in domain repository:"
									+ domain);
				for (ProfileRepItem profile : ((DomainRepItem) domain)
						.getProfiles())
					profiles.add(profile);
			}
			return profiles;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ProfileRepItem>();
		}

	}

	/**
	 * When fetching a list of type PartyRepItem, you must also provide the
	 * protocol. Otherwise it can be null.
	 * 
	 * @param elementType
	 *            can be ProfileRepItem.class or PartyRepItem.class and must be
	 *            equal to T.
	 * @param protocol
	 * @return list of objects of type T in the repository of type T.
	 */
	public static <T extends RepItem> List<T> fetchList(Class<T> elementType,
			MultiPartyProtocolRepItem protocol) {
		if (elementType == ProfileRepItem.class) {
			return (List<T>) fetchProfiles();
		}
		if (elementType == PartyRepItem.class) {
			return (List<T>) fetchPartiesForProtocol(protocol);
		}
		throw new IllegalArgumentException("unhandled type " + elementType);
	}
}
