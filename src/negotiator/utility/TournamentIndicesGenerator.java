package negotiator.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

/**
 * Created by dfesten on 21-8-2014.
 */
public class TournamentIndicesGenerator implements Iterable<List<Integer>> {
	int numIndices = 0;
	private ICombinatoricsVector<Integer> elements;
	private int testSize;
	private int profileSize;
	private boolean repetitionAllowed;
	private Iterator<ICombinatoricsVector<Integer>> combinationIterator;
	private Iterator<ICombinatoricsVector<Integer>> permutationIterator;

	public TournamentIndicesGenerator(int testSize, int profileSize,
			boolean repetitionAllowed, Collection<Integer> elements) {
		init(testSize, profileSize, repetitionAllowed, elements);
	}

	private void init(int testSize, int profileSize, boolean repetitionAllowed,
			Collection<Integer> elements) {
		if (profileSize < testSize)
			return; // test size can not be smaller than profile size
		this.elements = Factory.createVector(elements);
		this.testSize = testSize;
		this.profileSize = profileSize;
		this.repetitionAllowed = repetitionAllowed;
		this.combinationIterator = createCombinationGenerator().iterator();
		if (combinationIterator.hasNext()) {
			ICombinatoricsVector<Integer> next = combinationIterator.next();
			this.permutationIterator = createPermutationGenerator(next)
					.iterator();
		}
	}

	public TournamentIndicesGenerator(int numAgentsPerSession,
			int profilesPerRun, boolean repAllowed, int numPartyRepItems) {
		List<Integer> indices = new ArrayList<Integer>(numPartyRepItems);
		for (int i = 0; i < numPartyRepItems; i++) {
			indices.add(i);
		}
		init(numAgentsPerSession, profilesPerRun, repAllowed, indices);
	}

	private Generator<Integer> createPermutationGenerator(
			ICombinatoricsVector<Integer> input) {
		// pad with null values to make sure it equals testSize
		for (int i = 0; i < profileSize - testSize; i++)
			input.addValue(-1);

		return Factory.createPermutationGenerator(input);
	}

	private Generator<Integer> createCombinationGenerator() {

		ICombinatoricsVector<Integer> vector = Factory.createVector(elements);
		if (repetitionAllowed)
			return Factory.createMultiCombinationGenerator(vector, testSize);
		else
			return Factory.createSimpleCombinationGenerator(vector, testSize);
	}

	/**
	 * Returns an iterator over a set of elements of type T.
	 *
	 * @return an Iterator.
	 */
	@Override
	public Iterator<List<Integer>> iterator() {
		return new Iterator<List<Integer>>() {
			@Override
			public boolean hasNext() {
				boolean moreCombinations = combinationIterator != null
						&& combinationIterator.hasNext();
				boolean morePermutations = permutationIterator != null
						&& permutationIterator.hasNext();
				return moreCombinations || morePermutations;
			}

			@Override
			public List<Integer> next() {
				if (permutationIterator == null
						|| !permutationIterator.hasNext()) {
					if (combinationIterator == null
							|| !combinationIterator.hasNext())
						throw new NoSuchElementException("No more elements");
					else
						permutationIterator = createPermutationGenerator(
								combinationIterator.next()).iterator();
				}
				return permutationIterator.next().getVector();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"The remove operation is not supported by this Iterator");
			}
		};
	}

}
