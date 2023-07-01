
package inciident.formula.structure.compound;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.VariableMap.Variable;

public abstract class Quantifier extends Compound {

    protected Variable boundVariable;

    public Quantifier(Variable boundVariable, Formula formula) {
        super(formula);
        setBoundVariable(boundVariable);
    }

    protected Quantifier(Quantifier oldNode) {
        super();
        setBoundVariable(boundVariable);
    }

    public Variable getBoundVariable() {
        return boundVariable;
    }

    public void setBoundVariable(Variable boundVariable) {
        Objects.requireNonNull(boundVariable);
        this.boundVariable = boundVariable;
    }

    @Override
    public Object eval(List<?> values) {
        return null;
    }

    public void setFormula(Formula formula) {
        Objects.requireNonNull(formula);
        setChildren(Arrays.asList(formula));
    }

    @Override
    public Quantifier cloneNode() {
        throw new IllegalStateException();
    }

    @Override
    public int computeHashCode() {
        int hashCode = super.computeHashCode();
        hashCode = (37 * hashCode) + Objects.hashCode(boundVariable);
        return hashCode;
    }

    @Override
    public boolean equalsNode(Object other) {
        if (!super.equalsNode(other)) {
            return false;
        }
        return Objects.equals(boundVariable, ((Quantifier) other).boundVariable);
    }
}
