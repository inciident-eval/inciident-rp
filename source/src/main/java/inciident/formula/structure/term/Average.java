
package inciident.formula.structure.term;

import java.util.Arrays;
import java.util.List;

public abstract class Average extends Function {

    public Average(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    public Average(List<Term> arguments) {
        super(arguments);
    }

    protected Average() {
        super();
    }

    public void setArguments(List<Term> arguments) {
        setChildren(arguments);
    }

    public void setArguments(Term leftArgument, Term rightArgument) {
        setChildren(Arrays.asList(leftArgument, rightArgument));
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public abstract Average cloneNode();
}
