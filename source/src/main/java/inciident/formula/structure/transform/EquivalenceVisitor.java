
package inciident.formula.structure.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import inciident.formula.structure.AuxiliaryRoot;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.Atomic;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.atomic.literal.VariableMap.Variable;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.AtLeast;
import inciident.formula.structure.compound.AtMost;
import inciident.formula.structure.compound.Between;
import inciident.formula.structure.compound.Biimplies;
import inciident.formula.structure.compound.Choose;
import inciident.formula.structure.compound.Compound;
import inciident.formula.structure.compound.Implies;
import inciident.formula.structure.compound.Not;
import inciident.formula.structure.compound.Or;
import inciident.formula.structure.compound.Quantifier;
import inciident.util.tree.visitor.TreeVisitor;

public class EquivalenceVisitor implements TreeVisitor<Void, Formula> {

    private boolean fail;

    @Override
    public void reset() {
        fail = false;
    }

    @Override
    public VisitorResult firstVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            return VisitorResult.SkipChildren;
        } else if (node instanceof Compound) {
            if (node instanceof Quantifier) {
                return VisitorResult.Fail;
            }
            return VisitorResult.Continue;
        } else if (node instanceof AuxiliaryRoot) {
            return VisitorResult.Continue;
        } else {
            return VisitorResult.Fail;
        }
    }

    @Override
    public VisitorResult lastVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        node.mapChildren(this::replace);
        if (fail) {
            return VisitorResult.Fail;
        }
        return VisitorResult.Continue;
    }

    @SuppressWarnings("unchecked")
    private Formula replace(Formula node) {
        if (((node instanceof Variable)
                || (node instanceof Atomic)
                || (node instanceof And)
                || (node instanceof Or)
                || (node instanceof Not))) {
            return null;
        }
        final List<Formula> children = (List<Formula>) node.getChildren();
        Formula newNode = null;
        if (node instanceof Implies) {
            newNode = new Or(new Not(children.get(0)), children.get(1));
        } else if (node instanceof Biimplies) {
            newNode = new And( //
                    new Or(new Not(children.get(0)), children.get(1)),
                    new Or(new Not(children.get(1)), children.get(0)));
        } else if (node instanceof AtLeast) {
            newNode = new And(atLeastK(children, ((AtLeast) node).getMin()));
        } else if (node instanceof AtMost) {
            newNode = new And(atMostK(children, ((AtMost) node).getMax()));
        } else if (node instanceof Between) {
            final Between between = (Between) node;
            newNode = new And(
                    new And(atLeastK(children, between.getMin())), new And(atMostK(children, between.getMax())));
        } else if (node instanceof Choose) {
            final Choose choose = (Choose) node;
            newNode = new And(new And(atLeastK(children, choose.getK())), new And(atMostK(children, choose.getK())));
        } else {
            fail = true;
            return null;
        }
        return newNode;
    }

    private List<Formula> atMostK(List<? extends Formula> elements, int k) {
        final int n = elements.size();

        // return tautology
        if (k <= 0) {
            return Arrays.asList(Literal.False);
        }

        // return contradiction
        if (k > n) {
            return Arrays.asList(Literal.True);
        }

        return groupElements(elements.stream().map(Not::new).collect(Collectors.toList()), k, n);
    }

    private List<Formula> atLeastK(List<? extends Formula> elements, int k) {
        final int n = elements.size();

        // return tautology
        if (k <= 0) {
            return Arrays.asList(Literal.True);
        }

        // return contradiction
        if (k > n) {
            return Arrays.asList(Literal.False);
        }

        return groupElements(elements, n - k, n);
    }

    private List<Formula> groupElements(List<? extends Formula> elements, int k, final int n) {
        final List<Formula> groupedElements = new ArrayList<>();
        final Formula[] clause = new Formula[k + 1];
        final int[] index = new int[k + 1];

        // the position that is currently filled in clause
        int level = 0;
        index[level] = -1;

        while (level >= 0) {
            // fill this level with the next element
            index[level]++;
            // did we reach the maximum for this level
            if (index[level] >= (n - (k - level))) {
                // go to previous level
                level--;
            } else {
                clause[level] = elements.get(index[level]);
                if (level == k) {
                    final Formula[] clonedClause = new Formula[clause.length];
                    Arrays.copyOf(clause, clause.length);
                    for (int i = 0; i < clause.length; i++) {
                        //						clonedClause[i] = Trees.cloneTree(clause[i]);
                        clonedClause[i] = clause[i];
                    }
                    groupedElements.add(new Or(clonedClause));
                } else {
                    // go to next level
                    level++;
                    // allow only ascending orders (to prevent from duplicates)
                    index[level] = index[level - 1];
                }
            }
        }
        return groupedElements;
    }
}
