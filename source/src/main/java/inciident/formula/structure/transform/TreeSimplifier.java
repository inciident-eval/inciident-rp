
package inciident.formula.structure.transform;

import java.util.Arrays;
import java.util.List;

import inciident.formula.structure.AuxiliaryRoot;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.Atomic;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Compound;
import inciident.formula.structure.compound.Or;
import inciident.util.tree.visitor.TreeVisitor;

public class TreeSimplifier implements TreeVisitor<Void, Formula> {

    @Override
    public VisitorResult firstVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            return VisitorResult.SkipChildren;
        } else if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
            return VisitorResult.Continue;
        } else {
            return VisitorResult.Fail;
        }
    }

    @Override
    public VisitorResult lastVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
            if (node instanceof And) {
                if (node.getChildren().stream().anyMatch(c -> c == Literal.False)) {
                    node.setChildren(Arrays.asList(Literal.False));
                } else {
                    node.flatMapChildren(this::mergeAnd);
                }
            } else if (node instanceof Or) {
                if (node.getChildren().stream().anyMatch(c -> c == Literal.True)) {
                    node.setChildren(Arrays.asList(Literal.True));
                } else {
                    node.flatMapChildren(this::mergeOr);
                }
            }
        }
        return VisitorResult.Continue;
    }

    private List<? extends Formula> mergeAnd(final Formula child) {
        return (child instanceof And)
                        || (!(child instanceof Atomic) && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }

    private List<? extends Formula> mergeOr(final Formula child) {
        return (child instanceof Or)
                        || (!(child instanceof Atomic) && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }
}
