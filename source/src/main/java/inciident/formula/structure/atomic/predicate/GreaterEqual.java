
package inciident.formula.structure.atomic.predicate;

import java.util.List;

import inciident.formula.structure.term.Term;


public class GreaterEqual extends ComparingPredicate {

    public GreaterEqual(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    protected GreaterEqual() {
        super();
    }

    @Override
    public String getName() {
        return ">=";
    }

    @Override
    public GreaterEqual cloneNode() {
        return new GreaterEqual();
    }

    @Override
    public LessThan flip() {
        final List<? extends Term> children = getChildren();
        return new LessThan(children.get(0), children.get(1));
    }

    @Override
    protected boolean compareDiff(int diff) {
        return diff >= 0;
    }
}
