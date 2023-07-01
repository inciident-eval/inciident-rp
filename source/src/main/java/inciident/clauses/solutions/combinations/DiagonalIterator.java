
package inciident.clauses.solutions.combinations;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class DiagonalIterator extends ACombinationIterator {

    public static void main(String[] args) {
        DiagonalIterator diagonalIterator = new DiagonalIterator(2, 10);
        while (true) {
            int[] next = diagonalIterator.next();
            if (next == null) {
                break;
            }
            System.out.println(Arrays.toString(next));
        }
    }

    public static Stream<int[]> stream(int t, int size) {
        return StreamSupport.stream(new DiagonalIterator(t, size).spliterator(), false);
    }

    private final int[] c;
    int dist = 0;

    public DiagonalIterator(int t, int size) {
        super(size, t);
        if (t != 2) {
            throw new IllegalArgumentException("t != 2");
        }
        c = new int[t];
        c[1] = n - 1;
    }

    @Override
    protected int[] computeNext() {
        if (c[1] == n - 1) {
            if (dist == n - 2) {
                return null;
            }
            dist++;
            c[0] = 0;
            c[1] = dist;
        } else {
            for (int i = 0; i < t; i++) {
                c[i]++;
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
