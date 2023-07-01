
package inciident.formula.structure.atomic.predicate;

import java.util.List;

import inciident.formula.structure.term.Term;


public class LessThan extends ComparingPredicate {

    public LessThan(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    protected LessThan() {
        super();
    }

    @Override
    public String getName() {
        return "<";
    }

    @Override
    public LessThan cloneNode() {
        return new LessThan();
    }

    @Override
    public GreaterEqual flip() {
        final List<? extends Term> children = getChildren();
        return new GreaterEqual(children.get(0), children.get(1));
    }

    @Override
    protected boolean compareDiff(int diff) {
        return diff < 0;
    }
}
