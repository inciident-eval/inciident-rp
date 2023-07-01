
package inciident.formula.structure.term.real;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.term.Add;
import inciident.formula.structure.term.Term;

public class RealAdd extends Add {

    public RealAdd(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    public RealAdd(List<Term> arguments) {
        super(arguments);
    }

    private RealAdd() {
        super();
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public RealAdd cloneNode() {
        return new RealAdd();
    }

    @Override
    public Double eval(List<?> values) {
        return Formula.reduce(values, Double::sum);
    }
}
