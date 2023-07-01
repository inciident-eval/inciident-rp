
package inciident.formula.structure.atomic;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import inciident.util.data.Pair;

public interface Assignment {

    default void setAll(Collection<Pair<Integer, Object>> assignments) {
        for (final Pair<Integer, Object> pair : assignments) {
            set(pair.getKey(), pair.getValue());
        }
    }

    default void unsetAll(Collection<Pair<Integer, Object>> assignments) {
        for (final Pair<Integer, Object> pair : assignments) {
            set(pair.getKey(), null);
        }
    }

    void unsetAll();

    default void unset(int index) {
        set(index, null);
    }

    void set(int index, Object assignment);

    Optional<Object> get(int index);

    List<Pair<Integer, Object>> getAll();
}
