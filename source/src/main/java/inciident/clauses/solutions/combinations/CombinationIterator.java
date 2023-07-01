
package inciident.clauses.solutions.combinations;

import java.util.Iterator;
import java.util.List;

import inciident.clauses.LiteralList;


public interface CombinationIterator extends Iterator<int[]>, Iterable<int[]> {

    public static <T> T[] select(List<T> items, int[] indices, T[] selection) {
        for (int i = 0; i < indices.length; i++) {
            selection[i] = items.get(indices[i]);
        }
        return selection;
    }

    public static <T> T[] select(T[] items, int[] indices, T[] selection) {
        for (int i = 0; i < indices.length; i++) {
            selection[i] = items[indices[i]];
        }
        return selection;
    }

    public static <T> int[] select(LiteralList items, int[] indices, int[] selection) {
        for (int i = 0; i < indices.length; i++) {
            selection[i] = items.get(indices[i]);
        }
        return selection;
    }

    default Iterator<int[]> iterator() {
        return this;
    }

    void reset();

    long size();
}
