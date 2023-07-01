
package inciident.formula.structure.term.integer;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.term.Multiply;
import inciident.formula.structure.term.Term;

public class IntMultiply extends Multiply {

    public IntMultiply(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    public IntMultiply(List<Term> arguments) {
        super(arguments);
    }

    private IntMultiply() {
        super();
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public IntMultiply cloneNode() {
        return new IntMultiply();
    }

    @Override
    public Long eval(List<?> values) {
        return Formula.reduce(values, (a, b) -> a * b);
    }
}
