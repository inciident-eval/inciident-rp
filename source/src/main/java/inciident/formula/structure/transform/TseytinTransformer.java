
package inciident.formula.structure.transform;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import inciident.formula.structure.AuxiliaryRoot;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.Atomic;
import inciident.formula.structure.atomic.literal.BooleanLiteral;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.formula.structure.atomic.literal.VariableMap.Variable;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Compound;
import inciident.formula.structure.compound.Or;
import inciident.util.job.InternalMonitor;
import inciident.util.job.MonitorableFunction;
import inciident.util.tree.Trees;
import inciident.util.tree.visitor.TreeVisitor;

public class TseytinTransformer
        implements MonitorableFunction<Formula, List<TseytinTransformer.Substitute>>, TreeVisitor<Formula, Formula> {

    public static final class Substitute {
        private Formula orgFormula;
        private Variable variable;
        private List<Formula> clauses = new ArrayList<>();

        private Substitute(Formula orgFormula, Variable variable, int numberOfClauses) {
            this.orgFormula = orgFormula;
            this.variable = variable;
            clauses = new ArrayList<>(numberOfClauses);
        }

        private Substitute(Formula orgFormula, Variable variable, Formula clause) {
            this.orgFormula = orgFormula;
            this.variable = variable;
            clauses = new ArrayList<>(1);
            clauses.add(clause);
        }

        private Substitute(Formula orgFormula, Variable variable, List<? extends Formula> clauses) {
            this.orgFormula = orgFormula;
            this.variable = variable;
            this.clauses = new ArrayList<>(clauses);
        }

        private void addClause(Formula clause) {
            clauses.add(clause);
        }

        public Variable getVariable() {
            return variable;
        }

        public List<Formula> getClauses() {
            return clauses;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(orgFormula);
        }

        @Override
        public boolean equals(Object obj) {
            return (obj != null)
                    && (getClass() == obj.getClass())
                    && Objects.equals(orgFormula, ((Substitute) obj).orgFormula);
        }
    }

    private final List<Substitute> substitutes = new ArrayList<>();

    private VariableMap variableMap;

    public void setVariableMap(VariableMap variableMap) {
        this.variableMap = variableMap;
    }

    private Variable newVariable(final ArrayList<Literal> newChildren, final Formula clonedLastNode) {
        Variable addBooleanVariable = variableMap.addBooleanVariable();
        final Substitute substitute = new Substitute(clonedLastNode, addBooleanVariable, newChildren.size() + 1);
        substitutes.add(substitute);

        final BooleanLiteral tempLiteral = new BooleanLiteral(substitute.variable, true);
        if (clonedLastNode instanceof And) {
            final ArrayList<Literal> flippedChildren = new ArrayList<>();
            for (final Literal l : newChildren) {
                substitute.addClause(new Or(tempLiteral.flip(), l.cloneNode()));
                flippedChildren.add(l.flip());
            }
            flippedChildren.add(tempLiteral.cloneNode());
            substitute.addClause(new Or(flippedChildren));
        } else if (clonedLastNode instanceof Or) {
            final ArrayList<Literal> flippedChildren = new ArrayList<>();
            for (final Literal l : newChildren) {
                substitute.addClause(new Or(tempLiteral.cloneNode(), l.flip()));
                flippedChildren.add(l.cloneNode());
            }
            flippedChildren.add(tempLiteral.flip());
            substitute.addClause(new Or(flippedChildren));
        } else {
            throw new RuntimeException(clonedLastNode.getClass().toString());
        }
        return substitute.variable;
    }

    private final ArrayDeque<Formula> stack = new ArrayDeque<>();

    @Override
    public List<Substitute> execute(Formula child, InternalMonitor monitor) {
        substitutes.clear();
        stack.clear();

        Trees.sortTree(child);
        if (variableMap == null) {
            variableMap = child.getVariableMap().map(VariableMap::clone).orElseGet(VariableMap::new);
        }

        try {
            Trees.dfsPrePost(child, this);
        } catch (final Exception ignored) {
        }
        return substitutes;
    }

    @Override
    public VisitorResult firstVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            return VisitorResult.SkipChildren;
        } else if ((node instanceof Compound) || (node instanceof AuxiliaryRoot)) {
            stack.push((Formula) node);
            return VisitorResult.Continue;
        } else {
            return VisitorResult.Fail;
        }
    }

    @Override
    public VisitorResult lastVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            final Formula clonedNode = node;
            if (path.isEmpty()) {
                substitutes.add(new Substitute(clonedNode, null, clonedNode));
            } else {
                stack.push(clonedNode);
            }
        } else {
            final ArrayList<Literal> newChildren = new ArrayList<>();
            Formula lastNode = stack.pop();
            while (lastNode != node) {
                newChildren.add((Literal) lastNode);
                lastNode = stack.pop();
            }

            if (stack.isEmpty()) {
                final Formula clonedLastNode = lastNode;
                if (lastNode instanceof And) {
                    substitutes.add(new Substitute(clonedLastNode, null, newChildren));
                } else {
                    substitutes.add(new Substitute(clonedLastNode, null, new Or(newChildren)));
                }
            } else {
                final Formula clonedLastNode = lastNode;
                final Variable variable = newVariable(newChildren, clonedLastNode);
                stack.push(new BooleanLiteral(variable, true));
            }
        }
        return TreeVisitor.super.lastVisit(path);
    }
}
