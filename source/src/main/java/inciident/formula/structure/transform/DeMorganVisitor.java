
package inciident.formula.structure.transform;

import java.util.List;
import java.util.stream.Collectors;

import inciident.formula.structure.AuxiliaryRoot;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.Atomic;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Compound;
import inciident.formula.structure.compound.Not;
import inciident.formula.structure.compound.Or;
import inciident.util.tree.visitor.TreeVisitor;

public class DeMorganVisitor implements TreeVisitor<Void, Formula> {

    @Override
    public VisitorResult firstVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            return VisitorResult.SkipChildren;
        } else if (node instanceof Compound) {
            node.mapChildren(this::replace);
            return VisitorResult.Continue;
        } else if (node instanceof AuxiliaryRoot) {
            node.mapChildren(this::replace);
            return VisitorResult.Continue;
        } else {
            return VisitorResult.Fail;
        }
    }

    private Formula replace(Formula node) {
        Formula newNode = node;
        while (newNode instanceof Not) {
            final Formula notChild = (Formula) newNode.getChildren().iterator().next();
            if (notChild instanceof Literal) {
                newNode = ((Literal) notChild).flip();
            } else if (notChild instanceof Not) {
                newNode = notChild.getChildren().get(0);
            } else if (notChild instanceof Or) {
                newNode = new And(((Compound) notChild)
                        .getChildren().stream().map(Not::new).collect(Collectors.toList()));
            } else if (notChild instanceof And) {
                newNode = new Or(((Compound) notChild)
                        .getChildren().stream().map(Not::new).collect(Collectors.toList()));
            }
        }
        return newNode;
    }
}
