
package inciident.formula.structure.term;

public abstract class Divide extends Function {

    public Divide(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    protected Divide() {
        super();
    }

    @Override
    public String getName() {
        return "/";
    }

    @Override
    public abstract Divide cloneNode();
}
