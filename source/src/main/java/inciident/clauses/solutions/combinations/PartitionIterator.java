
package inciident.clauses.solutions.combinations;


public class PartitionIterator extends ABinomialCombinationIterator {

    protected final int[][] dim;
    private final int[] pos;
    private final int radix;

    public PartitionIterator(int t, int size) {
        this(t, size, 2);
    }

    protected PartitionIterator(int t, int size, int dimNumber) {
        super(size, t);

        final int numDim = dimNumber * t;
        radix = (int) Math.ceil(Math.pow(numCombinations, 1.0 / numDim));
        dim = new int[numDim][radix];
        pos = new int[numDim];

        for (int i = 0; i < dim.length; i++) {
            final int[] dimArray = dim[i];
            for (int j = 0; j < radix; j++) {
                dimArray[j] = j;
            }
        }
    }

    @Override
    protected long nextIndex() {
        int result;
        do {
            result = 0;
            for (int i = 0; i < pos.length; i++) {
                result += Math.pow(radix, i) * dim[i][pos[i]];
            }
            for (int i = pos.length - 1; i >= 0; i--) {
                final int p = pos[i];
                if ((p + 1) < radix) {
                    pos[i] = p + 1;
                    break;
                } else {
                    pos[i] = 0;
                }
            }
        } while (result >= numCombinations);

        return result;
    }
}
