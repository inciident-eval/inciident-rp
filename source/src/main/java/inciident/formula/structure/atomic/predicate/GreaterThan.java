
package inciident.formula.structure.atomic.predicate;

import java.util.List;

import inciident.formula.structure.term.Term;


public class GreaterThan extends ComparingPredicate {

    public GreaterThan(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    protected GreaterThan() {
        super();
    }

    @Override
    public String getName() {
        return ">";
    }

    @Override
    public GreaterThan cloneNode() {
        return new GreaterThan();
    }

    @Override
    public LessEqual flip() {
        final List<? extends Term> children = getChildren();
        return new LessEqual(children.get(0), children.get(1));
    }

    @Override
    protected boolean compareDiff(int diff) {
        return diff > 0;
    }
}
