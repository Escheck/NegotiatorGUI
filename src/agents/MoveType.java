package agents;

/**
 * Move types and functionality for it.
 * 
 * @author W.Pasman 2sep14
 *
 */
enum MoveType {
	CONCESSION, UNFORTUNATE, FORTUNATE, SELFISH, SILENT, NICE;

	/**
	 * Get a move type associated with deltaSelf and deltaOther utility
	 * differences
	 * 
	 * @param deltaSelf
	 *            the difference ownUtility(bid) - ownutility(previous bid)
	 * @param deltaOther
	 *            the difference otherUtility(bid) - otherutility(previous bid)
	 * @return the MoveType for the given set of delta's
	 */
	static MoveType getMoveType(double deltaSelf, double deltaOther) {
		if (deltaSelf == 0 && deltaOther == 0)
			return SILENT;
		if (deltaSelf == 0 && deltaOther > 0)
			return NICE;
		if (deltaSelf <= 0 && deltaOther < 0)
			return UNFORTUNATE;
		if (deltaSelf < 0 && deltaOther >= 0)
			return CONCESSION;
		if (deltaSelf > 0 && deltaOther > 0)
			return FORTUNATE;
		if (deltaSelf > 0 && deltaOther <= 0)
			return SELFISH;

		// It should not be possible to get here. All case are mathematically
		// covered
		throw new IllegalStateException("Unexpected case " + deltaSelf + ","
				+ deltaOther);
	}
};