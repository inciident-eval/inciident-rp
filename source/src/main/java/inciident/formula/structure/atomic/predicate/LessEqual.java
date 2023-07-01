
package inciident.formula.structure.atomic.predicate;

import java.util.List;

import inciident.formula.structure.term.Term;


public class LessEqual extends ComparingPredicate {

    public LessEqual(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    protected LessEqual() {
        super();
    }

    @Override
    public String getName() {
        return "<=";
    }

    @Override
    public LessEqual cloneNode() {
        return new LessEqual();
    }

    @Override
    public GreaterThan flip() {
        final List<? extends Term> children = getChildren();
        return new GreaterThan(children.get(0), children.get(1));
    }

    @Override
    protected boolean compareDiff(int diff) {
        return diff <= 0;
    }
}
