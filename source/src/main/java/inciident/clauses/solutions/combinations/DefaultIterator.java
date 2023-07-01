
package inciident.clauses.solutions.combinations;


public class DefaultIterator extends ABinomialCombinationIterator {

    public DefaultIterator(int t, int size) {
        super(size, t);
    }

    @Override
    protected long nextIndex() {
        return counter - 1;
    }
}
