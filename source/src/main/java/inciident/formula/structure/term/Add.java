
package inciident.formula.structure.term;

import java.util.Arrays;
import java.util.List;

public abstract class Add extends Function {

    public Add(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    public Add(List<Term> arguments) {
        super(arguments);
    }

    protected Add() {
        super();
    }

    public void setArguments(Term leftArgument, Term rightArgument) {
        setChildren(Arrays.asList(leftArgument, rightArgument));
    }

    @Override
    public String getName() {
        return "+";
    }

    @Override
    public abstract Add cloneNode();
}
