
package inciident.formula.structure.atomic.predicate;

import java.util.Arrays;
import java.util.List;

import inciident.formula.structure.NonTerminal;
import inciident.formula.structure.atomic.Atomic;
import inciident.formula.structure.term.Term;

public abstract class Predicate extends NonTerminal implements Atomic {

    protected Predicate(List<Term> nodes) {
        super(nodes);
    }

    @SafeVarargs
    protected Predicate(Term... nodes) {
        super(nodes);
    }

    protected Predicate() {
        super();
    }

    public void setArguments(Term leftArgument, Term rightArgument) {
        setChildren(Arrays.asList(leftArgument, rightArgument));
    }

    @Override
    public String getName() {
        return "=";
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<? extends Term> getChildren() {
        return (List<? extends Term>) super.getChildren();
    }

    @Override
    public abstract Predicate flip();
}
