
package inciident.formula.structure.atomic.predicate;

import java.util.List;

import inciident.formula.structure.term.Term;


public class Equals extends ComparingPredicate {

    public Equals(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    protected Equals() {
        super();
    }

    @Override
    public String getName() {
        return "=";
    }

    @Override
    public Equals cloneNode() {
        return new Equals();
    }

    @Override
    public NotEquals flip() {
        final List<? extends Term> children = getChildren();
        return new NotEquals(children.get(0), children.get(1));
    }

    @Override
    protected boolean compareDiff(int diff) {
        return diff == 0;
    }
}
