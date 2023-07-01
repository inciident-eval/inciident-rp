
package inciident.formula.structure.term.integer;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.term.Divide;
import inciident.formula.structure.term.Term;

public class IntDivide extends Divide {

    public IntDivide(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    private IntDivide() {
        super();
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public IntDivide cloneNode() {
        return new IntDivide();
    }

    @Override
    public Long eval(List<?> values) {
        return Formula.reduce(values, (a, b) -> a / b);
    }
}
