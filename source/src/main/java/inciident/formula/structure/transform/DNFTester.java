
package inciident.formula.structure.transform;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.Atomic;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Or;
import inciident.util.tree.visitor.TreeVisitor;

public class DNFTester extends NFTester {

    @Override
    public VisitorResult firstVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Or) {
            if (path.size() > 1) {
                isNf = false;
                isClausalNf = false;
                return VisitorResult.SkipAll;
            }
            for (final Formula child : node.getChildren()) {
                if (!(child instanceof And)) {
                    if (!(child instanceof Atomic)) {
                        isNf = false;
                        isClausalNf = false;
                        return VisitorResult.SkipAll;
                    }
                    isClausalNf = false;
                }
            }
            return VisitorResult.Continue;
        } else if (node instanceof And) {
            if (path.size() > 2) {
                isNf = false;
                isClausalNf = false;
                return VisitorResult.SkipAll;
            }
            if (path.size() < 2) {
                isClausalNf = false;
            }
            for (final Formula child : node.getChildren()) {
                if (!(child instanceof Atomic)) {
                    isNf = false;
                    isClausalNf = false;
                    return VisitorResult.SkipAll;
                }
            }
            return VisitorResult.Continue;
        } else if (node instanceof Atomic) {
            if (path.size() > 3) {
                isNf = false;
                isClausalNf = false;
                return VisitorResult.SkipAll;
            }
            if (path.size() < 3) {
                isClausalNf = false;
            }
            return VisitorResult.SkipChildren;
        } else {
            isNf = false;
            isClausalNf = false;
            return VisitorResult.SkipAll;
        }
    }
}
