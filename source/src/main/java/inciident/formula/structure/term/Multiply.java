
package inciident.formula.structure.term;

import java.util.List;

public abstract class Multiply extends Function {

    public Multiply(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    public Multiply(List<Term> arguments) {
        super(arguments);
    }

    protected Multiply() {
        super();
    }

    @Override
    public String getName() {
        return "*";
    }

    @Override
    public abstract Multiply cloneNode();
}
