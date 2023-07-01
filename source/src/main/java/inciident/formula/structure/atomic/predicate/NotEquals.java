
package inciident.formula.structure.atomic.predicate;

import java.util.List;

import inciident.formula.structure.term.Term;


public class NotEquals extends ComparingPredicate {

    public NotEquals(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    protected NotEquals() {
        super();
    }

    @Override
    public String getName() {
        return "!=";
    }

    @Override
    public NotEquals cloneNode() {
        return new NotEquals();
    }

    @Override
    public Equals flip() {
        final List<? extends Term> children = getChildren();
        return new Equals(children.get(0), children.get(1));
    }

    @Override
    protected boolean compareDiff(int diff) {
        return diff != 0;
    }
}
