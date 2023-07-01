
package inciident.util.tree.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import inciident.util.data.Result;

public abstract class AbstractNonTerminal<T extends Tree<T>> implements Tree<T> {

    protected final List<T> children = new ArrayList<>();

    @Override
    public List<? extends T> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Optional<Integer> getChildIndex(T child) {
        return Result.indexToOptional(children.indexOf(child));
    }

    public boolean hasChild(T child) {
        return getChildIndex(child).isPresent();
    }

    public boolean isFirstChild(T child) {
        return getChildIndex(child).filter(index -> index == 0).isPresent();
    }

    @Override
    public void setChildren(List<? extends T> children) {
        Objects.requireNonNull(children);
        this.children.clear();
        this.children.addAll(children);
    }

    public void addChild(int index, T newChild) {
        if (index > getNumberOfChildren()) {
            children.add(newChild);
        } else {
            children.add(index, newChild);
        }
    }

    public void addChild(T newChild) {
        children.add(newChild);
    }

    public void removeChild(T child) {
        if (!children.remove(child)) {
            throw new NoSuchElementException();
        }
    }

    public T removeChild(int index) {
        return children.remove(index);
    }

    public void replaceChild(T oldChild, T newChild) {
        final int index = children.indexOf(oldChild);
        children.set(index, newChild);
    }

    public void mapChildren(Function<T, ? extends T> mapper) {
        Objects.requireNonNull(mapper);
        for (ListIterator<T> it = children.listIterator(); it.hasNext(); ) {
            final T child = it.next();
            final T replacement = mapper.apply(child);
            if ((replacement != null) && (replacement != child)) {
                it.set(replacement);
            }
        }
    }
}
