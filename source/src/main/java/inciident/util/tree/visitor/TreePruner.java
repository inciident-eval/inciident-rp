
package inciident.util.tree.visitor;

import java.util.Collections;
import java.util.List;

import inciident.util.tree.structure.Tree;

public class TreePruner implements TreeVisitor<Void, Tree<?>> {

    private int depthLimit = Integer.MAX_VALUE;

    public int getDepthLimit() {
        return depthLimit;
    }

    public void setDepthLimit(int depthLimit) {
        this.depthLimit = depthLimit;
    }

    @Override
    public VisitorResult firstVisit(List<Tree<?>> path) {
        try {
            if (path.size() > depthLimit) {
                final Tree<?> node = TreeVisitor.getCurrentNode(path);
                node.setChildren(Collections.emptyList());
                return VisitorResult.SkipChildren;
            }
            return VisitorResult.Continue;
        } catch (final Exception e) {
            return VisitorResult.SkipAll;
        }
    }
}
