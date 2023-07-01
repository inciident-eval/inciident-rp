
package inciident.formula.structure.atomic.literal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import inciident.formula.structure.Formula;
import inciident.formula.structure.Terminal;
import inciident.formula.structure.atomic.literal.NamedTermMap.ValueTerm;
import inciident.formula.structure.term.Term;


public class NamedTermMap<T extends ValueTerm> implements Cloneable, Iterable<T> {

    public abstract static class ValueTerm extends Terminal implements Term {

        protected int index;
        protected String name;
        protected final Class<?> type;
        protected VariableMap map;

        protected ValueTerm(String name, int index, Class<?> type, VariableMap map) {
            this.index = index;
            this.name = name;
            this.type = type;
            this.map = map;
        }

        protected abstract ValueTerm copy(VariableMap newMap);

        void setIndex(int index) {
            this.index = index;
        }

        void setName(String name) {
            this.name = name;
        }

        public void rename(String newName) {
            map.renameVariable(index, newName);
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public void setVariableMap(VariableMap map) {
            this.map = map;
        }

        @Override
        public Formula cloneNode() {
            return this;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public boolean equalsNode(Object other) {
            if (this == other) return true;
            if (getClass() != other.getClass()) return false;
            return Objects.equals(name, ((ValueTerm) other).name);
        }

        @Override
        public boolean equals(Object obj) {
            return equalsNode(obj);
        }

        @Override
        public String toString() {
            return index + ": " + name;
        }

        @Override
        public Optional<VariableMap> getVariableMap() {
            return Optional.of(map);
        }

        public VariableMap getMap() {
            return map;
        }
    }

    private final ArrayList<T> fromIndex;
    private final LinkedHashMap<String, T> fromName = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    protected NamedTermMap(NamedTermMap<T> otherMap, VariableMap newVariableMap) {
        fromIndex = new ArrayList<>(otherMap.fromIndex.size());
        for (T term : otherMap.fromIndex) {
            if (term == null) {
                fromIndex.add(null);
            } else {
                term = (T) term.copy(newVariableMap);
                fromIndex.add(term);
                fromName.put(term.getName(), term);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected NamedTermMap(NamedTermMap<T> map1, NamedTermMap<T> map2, VariableMap newVariableMap) {
        for (T term : map1.fromIndex) {
            if (term != null) {
                term = (T) term.copy(newVariableMap);
                fromName.put(term.getName(), term);
            }
        }
        for (T term : map2.fromIndex) {
            if (term != null) {
                final T t = fromName.get(term.name);
                if (t != null && t.type != term.type) {
                    if (t.type != term.type) {
                        throw new IllegalArgumentException("Merged maps have incompatible types for term " + term.name);
                    }
                } else {
                    term = (T) term.copy(newVariableMap);
                    fromName.put(term.getName(), term);
                }
            }
        }
        fromIndex = new ArrayList<>(fromName.size());
        fromIndex.add(null);
        for (ValueTerm term : fromName.values()) {
            term.setIndex(fromIndex.size());
            fromIndex.add((T) term);
        }
    }

    NamedTermMap() {
        fromIndex = new ArrayList<>();
        fromIndex.add(null);
    }

    public boolean has(int index) {
        return isValidIndex(index) && (fromIndex.get(index) != null);
    }

    public boolean has(String name) {
        return fromName.containsKey(name);
    }

    private boolean isValidIndex(final int index) {
        return (index >= getMinIndex()) && (index <= getMaxIndex());
    }

    public int getMinIndex() {
        return 1;
    }

    public int getMaxIndex() {
        return fromIndex.size() - 1;
    }

    public void rename(int index, String newName) {
        Objects.requireNonNull(newName);
        if (isValidIndex(index)) {
            final T term = fromIndex.get(index);
            if (term != null) {
                fromName.remove(term.getName());
                term.setName(newName);
                fromName.put(newName, term);
            } else {
                throw new NoSuchElementException(String.valueOf(index));
            }
        } else {
            throw new NoSuchElementException(String.valueOf(index));
        }
    }

    public void rename(String oldName, String newName) {
        Objects.requireNonNull(oldName);
        Objects.requireNonNull(newName);
        final T term = fromName.get(oldName);
        if (term != null) {
            fromName.remove(term.getName());
            term.setName(newName);
            fromName.put(newName, term);
        } else {
            throw new NoSuchElementException(String.valueOf(oldName));
        }
    }

    public T add(final T term) {
        int index = term.getIndex();
        if (index == 0) {
            throw new IllegalArgumentException("Element with the index 0 is invalid");
        } else if (index < 0) {
            index = getMaxIndex() + 1;
            term.setIndex(index);
        } else if (index <= getMaxIndex() && fromIndex.get(index) != null) {
            throw new IllegalArgumentException("Element with the index " + term.getIndex() + " already defined");
        }
        if (term.getName() == null) {
            term.setName(String.valueOf(index));
        }
        if (fromName.containsKey(term.getName())) {
            throw new IllegalArgumentException("Element with the name " + term.getName() + " already defined");
        }
        for (int i = getMaxIndex(); i < index; i++) {
            fromIndex.add(null);
        }
        fromName.put(term.getName(), term);
        fromIndex.set(index, term);
        return term;
    }

    public boolean remove(String name) {
        final T oldTerm = fromName.get(name);
        if (oldTerm != null) {
            if (oldTerm.getIndex() == getMaxIndex()) {
                fromIndex.remove(oldTerm.getIndex());
            } else {
                fromIndex.set(oldTerm.getIndex(), null);
            }
            fromName.remove(name);
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(int index) {
        if (isValidIndex(index)) {
            final T term = fromIndex.get(index);
            if (term != null) {
                fromName.remove(term.getName());
            }
            if (index == getMaxIndex()) {
                fromIndex.remove(index);
            } else {
                fromIndex.set(index, null);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean hasGaps() {
        return fromName.size() != fromIndex.size();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fromIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        return Objects.equals(fromIndex, ((NamedTermMap<?>) obj).fromIndex);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Map\n");
        for (int i = 1; i < fromIndex.size(); i++) {
            sb.append('\t');
            sb.append(fromIndex.get(i));
            sb.append('\n');
        }
        return sb.toString();
    }

    public boolean containsAll(NamedTermMap<T> variables) {
        return fromName.keySet().containsAll(variables.fromName.keySet());
    }

    public List<T> getTerms() {
        return fromIndex.subList(getMinIndex(), fromIndex.size());
    }

    public Optional<T> get(int index) {
        return isValidIndex(index) ? Optional.ofNullable(fromIndex.get(index)) : Optional.empty();
    }

    public Optional<T> get(String name) {
        return Optional.ofNullable(fromName.get(name));
    }

    public void normalize() {
        fromIndex.clear();
        fromIndex.add(null);
        for (T term : fromName.values()) {
            term.setIndex(fromIndex.size());
            fromIndex.add(term);
        }
    }

    public void clear() {
        fromName.clear();
        fromIndex.clear();
        fromIndex.add(null);
    }

    public void randomize(Random random) {
        Collections.shuffle(fromIndex.subList(getMinIndex(), getMaxIndex()), random);
        int count = getMinIndex();
        for (T term : fromIndex) {
            if (term != null) {
                term.setIndex(count++);
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return getTerms().iterator();
    }
}
