
package inciident.formula.structure.term.real;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.term.Divide;
import inciident.formula.structure.term.Term;

public class RealDivide extends Divide {

    public RealDivide(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    private RealDivide() {
        super();
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public RealDivide cloneNode() {
        return new RealDivide();
    }

    @Override
    public Double eval(List<?> values) {
        return Formula.reduce(values, (a, b) -> a / b);
    }
}
