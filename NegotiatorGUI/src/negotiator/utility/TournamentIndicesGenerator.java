package negotiator.utility;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.util.*;

import static java.lang.Math.max;

/**
 * Created by dfesten on 21-8-2014.
 */
public class TournamentIndicesGenerator implements Iterable<List<Integer>>
{
    int numIndices = 0;
    private ICombinatoricsVector<Integer> elements;
    private int testSize;
    private int profileSize;
    private boolean repetitionAllowed;
    private Iterator<ICombinatoricsVector<Integer>> combinationIterator;
    private Iterator<ICombinatoricsVector<Integer>> permutationIterator;

    public TournamentIndicesGenerator(int testSize, int profileSize, boolean repetitionAllowed, Integer... elements)
    {
        this(testSize, profileSize, repetitionAllowed, Arrays.asList(elements));
    }

    public TournamentIndicesGenerator(int testSize, int profileSize, boolean repetitionAllowed, Collection<Integer> elements)
    {
        if (profileSize < testSize) return; // test size can not be smaller than profile size
        this.elements = Factory.createVector(elements);
        this.testSize = testSize;
        this.profileSize = profileSize;
        this.repetitionAllowed = repetitionAllowed;
        this.combinationIterator = createCombinationGenerator().iterator();
        if (combinationIterator.hasNext())
        {
            ICombinatoricsVector<Integer> next = combinationIterator.next();
            this.permutationIterator = createPermutationGenerator(next).iterator();
        }
    }

    private Generator<Integer> createPermutationGenerator(ICombinatoricsVector<Integer> input)
    {
        // pad with null values to make sure it equals testSize
        for (int i = 0; i < profileSize - testSize; i++)
            input.addValue(-1);

        return Factory.createPermutationGenerator(input);
    }

    private Generator<Integer> createCombinationGenerator()
    {

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
    public Iterator<List<Integer>> iterator()
    {
        return new Iterator<List<Integer>>()
        {
            @Override
            public boolean hasNext()
            {
                boolean moreCombinations = combinationIterator != null && combinationIterator.hasNext();
                boolean morePermutations = permutationIterator != null && permutationIterator.hasNext();
                return moreCombinations || morePermutations;
            }

            @Override
            public List<Integer> next()
            {
                if (permutationIterator == null || !permutationIterator.hasNext())
                {
                    if (combinationIterator == null || !combinationIterator.hasNext())
                        throw new NoSuchElementException("No more elements");
                    else
                        permutationIterator = createPermutationGenerator(combinationIterator.next()).iterator();
                }
                return permutationIterator.next().getVector();
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("The remove operation is not supported by this Iterator");
            }
        };
    }

//    public int getNumIndices()
//    {
//        if (numIndices > 0) return numIndices;
//        int t = testSize;
//        int p = profileSize;
//        int e = elements.getSize();
//
//        int permutationsPerCombination = (repetitionAllowed) ? t * t : fac(t);
//        int combinations = max(comb(e, t), 1);
//
//        numIndices = combinations * permutationsPerCombination;
//        return numIndices;
//    }

    public int comb(int n, int r)
    {
        return fac(n) / (fac(r) * fac(n - r));
    }

    public int fac(int base)
    {
        if (base == 0) return 1;
        else
        {
            int fac = 1;
            for (int i = 2; i <= base; i++)
                fac *= i;
            return fac;
        }
    }
}
