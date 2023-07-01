
package inciident.clauses.solutions.combinations;


public class InverseLexicographicIterator extends ACombinationIterator {

    private final int[] c;

    public InverseLexicographicIterator(int t, int size) {
        super(size, t);
        c = new int[t];
        for (int i = t; i > 0; i--) {
            c[t - i] = n - i;
        }
        c[t - 1] = n;
    }

    @Override
    protected int[] computeNext() {
        int i = t - 1;
        for (; i >= 0; i--) {
            if (i == 0) {
                c[i]--;
            } else if ((c[i - 1] + 1) < c[i]) {
                c[i]--;
                return c;
            } else {
                c[i] = (n - t) + i;
            }
        }
        if (c[0] < 0) {
            return null;
        }

        return c;
    }
}
