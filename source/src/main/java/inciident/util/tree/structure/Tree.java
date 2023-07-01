
package inciident.util.tree.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;


public interface Tree<T extends Tree<T>> {

    Tree<T> cloneNode();

    default boolean equalsNode(Object other) {
        return getClass() == other.getClass();
    }

    // todo: equals as in NonTerminal

    // todo: clone as in Trees.clone?

    default boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    List<? extends T> getChildren();

    void setChildren(List<? extends T> children);

    default int getNumberOfChildren() {
        return getChildren().size();
    }

    default void flatMapChildren(Function<T, List<? extends T>> mapper) {
        Objects.requireNonNull(mapper);
        final List<? extends T> oldChildren = getChildren();
        if (!oldChildren.isEmpty()) {
            final ArrayList<T> newChildren = new ArrayList<>(oldChildren.size());
            boolean modified = false;
            for (final T child : oldChildren) {
                final List<? extends T> replacement = mapper.apply(child);
                if (replacement != null) {
                    newChildren.addAll(replacement);
                    modified = true;
                } else {
                    newChildren.add(child);
                }
            }
            if (modified) {
                setChildren(newChildren);
            }
        }
    }

    default void mapChildren(Function<T, ? extends T> mapper) {
        Objects.requireNonNull(mapper);
        final List<? extends T> oldChildren = getChildren();
        if (!oldChildren.isEmpty()) {
            final List<T> newChildren = new ArrayList<>(oldChildren.size());
            boolean modified = false;
            for (final T child : oldChildren) {
                final T replacement = mapper.apply(child);
                if ((replacement != null) && (replacement != child)) {
                    newChildren.add(replacement);
                    modified = true;
                } else {
                    newChildren.add(child);
                }
            }
            if (modified) {
                setChildren(newChildren);
            }
        }
    }

    default Optional<T> getFirstChild() {
        if (getChildren().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(getChildren().get(0));
    }

    default Optional<T> getLastChild() {
        if (getChildren().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(getChildren().get(getNumberOfChildren() - 1));
    }
}
