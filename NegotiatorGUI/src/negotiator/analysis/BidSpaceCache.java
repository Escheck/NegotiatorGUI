package negotiator.analysis;

import java.util.Arrays;

import negotiator.utility.UtilitySpace;

public class BidSpaceCache {
	
	private static String[] identifier;
	private static BidSpace cachedBidSpace;
	
	public static void cacheBidSpace(BidSpace bidSpace, String ... ident) throws Exception {
		// extra check, just to be sure that we are not calculating the space twice.
		if (isCached(ident)) {
			throw new Exception("This space is already cached. Check that you are not " +
								"calculating the space multiple times.");
		} else {
			System.out.println("CACHING: " + ident);
			identifier = ident;
			cachedBidSpace = bidSpace;
		}
	}
	
	public static BidSpace getCachedSpace() {
		return cachedBidSpace;
	}
	
	public static boolean isCached(String[] ident) {
		return Arrays.equals(ident, identifier);
	}
	
	public static boolean isCached(UtilitySpace spaceA, UtilitySpace spaceB) {
		String[] ident = {spaceA.getFileName(), spaceB.getFileName()};
		return Arrays.equals(ident, identifier);
	}
}