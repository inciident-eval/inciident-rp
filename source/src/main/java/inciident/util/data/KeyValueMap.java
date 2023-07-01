
package inciident.util.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;


public class KeyValueMap {

    private Map<Identifier<?>, Object> elements = new HashMap<>();

    public KeyValueMap() {}

    public KeyValueMap(KeyValueMap other) {
        elements.putAll(other.elements);
    }

    @SuppressWarnings("unchecked")
    public <T> Result<T> set(Identifier<T> key, T value) {
        try {
            return Result.of((T) elements.put(key, value));
        } catch (final ClassCastException e) {
            return Result.empty(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Result<T> get(Identifier<T> key) {
        try {
            return Result.of((T) elements.get(key));
        } catch (final ClassCastException e) {
            return Result.empty(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Result<T> remove(Identifier<T> key) {
        try {
            return Result.of((T) elements.remove(key));
        } catch (final ClassCastException e) {
            return Result.empty(e);
        }
    }

    public void clear() {
        elements.clear();
    }

    public Set<Identifier<?>> getKeys() {
        return Collections.unmodifiableSet(elements.keySet());
    }

    public Set<Entry<Identifier<?>, Object>> getElements() {
        return Collections.unmodifiableSet(elements.entrySet());
    }

    @Override
    public int hashCode() {
        int result = elements.size();
        for (final Entry<Identifier<?>, Object> entry : elements.entrySet()) {
            result += 37 * entry.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if ((other == null) || (getClass() != other.getClass())) {
            return false;
        }
        return Objects.equals(elements, ((KeyValueMap) other).elements);
    }
}
