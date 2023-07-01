
package inciident.util.tree.visitor;

import java.util.List;
import java.util.Optional;

import inciident.util.tree.structure.Tree;


public interface TreeVisitor<R, T extends Tree<?>> {

    enum VisitorResult {
        Continue,
        SkipChildren,
        SkipAll,
        Fail
    }

    static <T> T getCurrentNode(List<T> path) {
        return path.get(path.size() - 1);
    }

    static <T> T getParentNode(List<T> path) {
        return (path.size() > 1) ? path.get(path.size() - 2) : null;
    }

    default VisitorResult firstVisit(List<T> path) {
        return VisitorResult.Continue;
    }

    default VisitorResult lastVisit(List<T> path) {
        return VisitorResult.Continue;
    }

    default void reset() {}

    default Optional<R> getResult() {
        return Optional.empty();
    }
}
