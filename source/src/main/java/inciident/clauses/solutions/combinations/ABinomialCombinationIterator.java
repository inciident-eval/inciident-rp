
package inciident.clauses.solutions.combinations;


public abstract class ABinomialCombinationIterator extends ACombinationIterator {

    protected final BinomialCalculator binomialCalculator;

    public ABinomialCombinationIterator(int size, int t) {
        this(size, t, new BinomialCalculator(size, t));
    }

    public ABinomialCombinationIterator(int size, int t, BinomialCalculator binomialCalculator) {
        super(size, t, binomialCalculator);
        this.binomialCalculator = binomialCalculator;
    }

    @Override
    protected int[] computeNext() {
        return binomialCalculator.combination(nextIndex());
    }

    protected abstract long nextIndex();
}
