
package inciident.analysis.solver;

import java.util.Collection;


public interface Assumptions<T> {

    void push(T assumption);

    default void pushAll(Collection<? extends T> assumptions) {
        for (final T assumption : assumptions) {
            push(assumption);
        }
    }

    default void replaceLast(T assumption) {
        pop();
        push(assumption);
    }

    T peek();

    T pop();

    default void pop(int size) {
        for (int i = 0; i < size; i++) {
            pop();
        }
    }

    void clear();

    default void clear(int newSize) {
        pop(size() - newSize);
    }

    int size();
}
