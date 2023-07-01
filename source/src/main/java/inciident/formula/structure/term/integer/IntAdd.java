
package inciident.formula.structure.term.integer;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.term.Add;
import inciident.formula.structure.term.Term;

public class IntAdd extends Add {

    public IntAdd(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    public IntAdd(List<Term> arguments) {
        super(arguments);
    }

    private IntAdd() {
        super();
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public IntAdd cloneNode() {
        return new IntAdd();
    }

    @Override
    public Long eval(List<?> values) {
        return Formula.reduce(values, Long::sum);
    }
}
