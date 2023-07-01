
package inciident.evaluation.interactionfinder;

import java.util.List;

import inciident.clauses.LiteralList;
import inciident.formula.structure.AuxiliaryRoot;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.Atomic;
import inciident.formula.structure.atomic.literal.BooleanLiteral;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.formula.structure.compound.Compound;
import inciident.util.tree.visitor.TreeVisitor;

public class AtomicSetReplacer implements TreeVisitor<Void, Formula> {
    final VariableMap variables;
    final List<LiteralList> atomicSets;

    public AtomicSetReplacer(VariableMap variables, List<LiteralList> atomicSets) {
        this.variables = variables;
        this.atomicSets = atomicSets;
    }

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
        node.mapChildren(c -> {
            if (c instanceof BooleanLiteral) {
                BooleanLiteral l = (BooleanLiteral) c;
                int index = l.getIndex();
                for (LiteralList atomicSet : atomicSets) {
                    if (atomicSet.containsAnyLiteral(index)) {
                        int substitute = atomicSet.get(0);
                        if (index != substitute) {
                            if (l.isPositive()) {
                                return variables.createLiteral(Math.abs(substitute), substitute > 0);
                            } else {
                                return variables.createLiteral(Math.abs(substitute), substitute < 0);
                            }
                        }
                        break;
                    } else if (atomicSet.containsAnyLiteral(-index)) {
                        int substitute = atomicSet.get(0);
                        if (-index != substitute) {
                            if (l.isPositive()) {
                                return variables.createLiteral(Math.abs(substitute), substitute < 0);
                            } else {
                                return variables.createLiteral(Math.abs(substitute), substitute > 0);
                            }
                        }
                        break;
                    }
                }
            }
            return null;
        });
        return VisitorResult.Continue;
    }
}
