
package inciident.formula.structure.term.real;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.term.Multiply;
import inciident.formula.structure.term.Term;

public class RealMultiply extends Multiply {

    public RealMultiply(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    public RealMultiply(List<Term> arguments) {
        super(arguments);
    }

    private RealMultiply() {
        super();
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public RealMultiply cloneNode() {
        return new RealMultiply();
    }

    @Override
    public Double eval(List<?> values) {
        return Formula.reduce(values, (a, b) -> a * b);
    }
}
