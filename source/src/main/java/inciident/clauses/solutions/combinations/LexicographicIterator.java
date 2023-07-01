
package inciident.clauses.solutions.combinations;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class LexicographicIterator extends ACombinationIterator {

    public static Stream<int[]> stream(int t, int size) {
        return StreamSupport.stream(new LexicographicIterator(t, size).spliterator(), false);
    }

    private final int[] c;

    public LexicographicIterator(int t, int size) {
        super(size, t);
        c = new int[t];
        for (int i = 0; i < (c.length - 1); i++) {
            c[i] = i;
        }
        c[t - 1] = t - 2;
    }

    @Override
    protected int[] computeNext() {
        int i = t;
        for (; i > 0; i--) {
            final int ci = ++c[i - 1];
            if (ci < ((n - t) + i)) {
                break;
            }
        }
        if ((i == 0) && (c[i] == ((n - t) + 1))) {
            return null;
        }

        for (; i < t; i++) {
            if (i == 0) {
                c[i] = 0;
            } else {
                c[i] = c[i - 1] + 1;
            }
        }
        return c;
    }

    @Override
    public void reset() {
        super.reset();
        for (int i = 0; i < (c.length - 1); i++) {
            c[i] = i;
        }
        c[t - 1] = t - 2;
    }
}
