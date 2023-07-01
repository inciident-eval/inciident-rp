
package inciident.util.tree.visitor;

import java.util.List;
import java.util.Optional;

import inciident.util.tree.structure.Tree;


public class TreeDepthCounter implements TreeVisitor<Integer, Tree<?>> {

    private Class<? extends Tree<?>> terminalNode = null;

    private int maxDepth = 0;

    @Override
    public void reset() {
        maxDepth = 0;
    }

    @Override
    public VisitorResult firstVisit(List<Tree<?>> path) {
        final int depth = path.size();
        if (maxDepth < depth) {
            maxDepth = depth;
        }
        final Tree<?> node = TreeVisitor.getCurrentNode(path);
        if ((terminalNode != null) && terminalNode.isInstance(node)) {
            return VisitorResult.SkipChildren;
        } else {
            return VisitorResult.Continue;
        }
    }

    @Override
    public Optional<Integer> getResult() {
        return Optional.of(maxDepth);
    }

    public Class<? extends Tree<?>> getTerminalNode() {
        return terminalNode;
    }

    public void setTerminalNode(Class<? extends Tree<?>> terminalNode) {
        this.terminalNode = terminalNode;
    }
}
