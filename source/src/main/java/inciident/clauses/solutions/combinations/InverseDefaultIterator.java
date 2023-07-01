
package inciident.clauses.solutions.combinations;


public class InverseDefaultIterator extends ABinomialCombinationIterator {

    public InverseDefaultIterator(int t, int size) {
        super(size, t);
    }

    @Override
    protected long nextIndex() {
        return numCombinations - counter;
    }
}
