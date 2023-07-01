
package inciident.util.tree.visitor;

import java.util.List;

import inciident.util.tree.structure.Tree;


public interface DfsVisitor<R, T extends Tree<?>> extends TreeVisitor<R, T> {

    default VisitorResult visit(List<T> path) {
        return VisitorResult.Continue;
    }
}
