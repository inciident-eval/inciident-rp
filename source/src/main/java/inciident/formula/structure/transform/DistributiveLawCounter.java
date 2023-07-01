
package inciident.formula.structure.transform;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import inciident.formula.structure.AuxiliaryRoot;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.Atomic;
import inciident.formula.structure.compound.Compound;
import inciident.util.tree.visitor.TreeVisitor;

public class DistributiveLawCounter implements TreeVisitor<Integer, Formula> {

    private static class StackElement {
        int clauseNumber = 1;
        int clauseSize = 1;
        Formula node;

        public StackElement(Formula node) {
            this.node = node;
        }
    }

    private ArrayDeque<StackElement> stack = new ArrayDeque<>();

    @Override
    public void reset() {
        stack.clear();
    }

    @Override
    public Optional<Integer> getResult() { // TODO BigInteger?
        return Optional.of(stack.pop().clauseNumber);
    }

    @Override
    public VisitorResult firstVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            return VisitorResult.SkipChildren;
        } else if ((node instanceof Compound) || (node instanceof AuxiliaryRoot)) {
            stack.push(new StackElement((Formula) node));
            return VisitorResult.Continue;
        } else {
            return VisitorResult.Fail;
        }
    }

    @Override
    public VisitorResult lastVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            stack.push(new StackElement(node));
        } else {
            final ArrayList<StackElement> children = new ArrayList<>();
            StackElement lastNode = stack.pop();
            boolean invalid = false;
            for (; lastNode.node != node; lastNode = stack.pop()) {
                children.add(lastNode);
                if (lastNode.clauseNumber < 0) {
                    invalid = true;
                }
            }
            if (invalid) {
                lastNode.clauseNumber = -1;
            } else {
                try {
                    for (final StackElement child : children) {
                        for (int i = 0; i < child.clauseNumber; i++) {
                            lastNode.clauseNumber = Math.multiplyExact(lastNode.clauseNumber, child.clauseSize);
                        }
                    }
                } catch (final ArithmeticException e) {
                    lastNode.clauseNumber = -1;
                }
                lastNode.clauseSize = children.size();
            }
            stack.push(lastNode);
        }
        return TreeVisitor.super.lastVisit(path);
    }
}
