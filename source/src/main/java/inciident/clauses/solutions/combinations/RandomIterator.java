
package inciident.clauses.solutions.combinations;

import java.util.Random;
import java.util.stream.LongStream;


public class RandomIterator extends ABinomialCombinationIterator {

    long[] index;

    public RandomIterator(int t, int size) {
        super(size, t);
        if (numCombinations > Integer.MAX_VALUE) {
            throw new RuntimeException();
        }

        index = LongStream.range(0, numCombinations).toArray();
        final Random rand = new Random(123);

        for (int i = 0; i < index.length; i++) {
            final int randomIndexToSwap = rand.nextInt(index.length);
            final long temp = index[randomIndexToSwap];
            index[randomIndexToSwap] = index[i];
            index[i] = temp;
        }
    }

    @Override
    protected long nextIndex() {
        return index[(int) (counter - 1)];
    }
}
