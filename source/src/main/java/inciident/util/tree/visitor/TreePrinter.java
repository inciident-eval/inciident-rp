
package inciident.util.tree.visitor;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import inciident.util.tree.structure.Tree;

public class TreePrinter implements TreeVisitor<String, Tree<?>> {

    private String indentation = "  ";

    private StringBuilder treeStringBuilder = new StringBuilder();
    private Predicate<Tree<?>> filter = null;
    private Function<Tree<?>, String> toStringFunction = Object::toString;

    @Override
    public void reset() {
        treeStringBuilder.delete(0, treeStringBuilder.length());
    }

    @Override
    public Optional<String> getResult() {
        return Optional.of(treeStringBuilder.toString());
    }

    public String getIndentation() {
        return indentation;
    }

    public void setIndentation(String indentation) {
        this.indentation = indentation;
    }

    @Override
    public VisitorResult firstVisit(List<Tree<?>> path) {
        final Tree<?> currentNode = TreeVisitor.getCurrentNode(path);
        if ((filter == null) || filter.test(currentNode)) {
            try {
                for (int i = 1; i < path.size(); i++) {
                    treeStringBuilder.append(indentation);
                }
                treeStringBuilder.append(toStringFunction.apply(currentNode));
                treeStringBuilder.append('\n');
            } catch (final Exception e) {
                return VisitorResult.SkipAll;
            }
        }
        return VisitorResult.Continue;
    }

    public void setFilter(Predicate<Tree<?>> filter) {
        this.filter = filter;
    }

    public void setToStringFunction(Function<Tree<?>, String> toStringFunction) {
        this.toStringFunction = toStringFunction;
    }
}
