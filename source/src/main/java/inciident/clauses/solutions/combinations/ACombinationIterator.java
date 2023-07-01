
package inciident.clauses.solutions.combinations;


public abstract class ACombinationIterator implements CombinationIterator {

    protected final int t, n;
    protected final long numCombinations;

    protected long counter;

    public ACombinationIterator(int n, int t) {
        this.t = t;
        this.n = n;
        numCombinations = BinomialCalculator.computeBinomial(n, t);
    }

    public ACombinationIterator(int n, int t, BinomialCalculator binomialCalculator) {
        this.t = t;
        this.n = n;
        numCombinations = binomialCalculator.binomial(n, t);
    }

    @Override
    public boolean hasNext() {
        return counter < numCombinations;
    }

    @Override
    public int[] next() {
        if (counter++ >= numCombinations) {
            return null;
        }
        return computeNext();
    }

    @Override
    public void reset() {
        counter = 0;
    }

    @Override
    public long size() {
        return numCombinations;
    }

    protected abstract int[] computeNext();
}
