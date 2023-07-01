
package inciident.formula.structure.atomic.literal;

import java.util.List;
import java.util.Objects;

import inciident.formula.structure.Formula;
import inciident.formula.structure.NonTerminal;
import inciident.formula.structure.atomic.literal.NamedTermMap.ValueTerm;


public final class BooleanLiteral extends NonTerminal implements Literal {

    private final boolean positive;

    public BooleanLiteral(ValueTerm valueTerm) {
        this(valueTerm, true);
    }

    public BooleanLiteral(ValueTerm valueTerm, boolean positive) {
        super(valueTerm);
        this.positive = positive;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<? extends ValueTerm> getChildren() {
        return (List<? extends ValueTerm>) super.getChildren();
    }

    @Override
    public String getName() {
        // TODO change to this and update all uses of Literal#getName
        //		return (positive ? "+" : "-") + children.get(0).getName();
        return getVariable().getName();
    }

    public int getIndex() {
        return getVariable().getIndex();
    }

    public ValueTerm getVariable() {
        return (ValueTerm) children.get(0);
    }

    @Override
    public boolean isPositive() {
        return positive;
    }

    @Override
    public BooleanLiteral flip() {
        return new BooleanLiteral(getVariable(), !positive);
    }

    @Override
    public BooleanLiteral cloneNode() {
        return new BooleanLiteral(getVariable(), positive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVariable(), positive);
    }

    @Override
    public boolean equalsNode(Object other) {
        if (getClass() != other.getClass()) {
            return false;
        }
        final BooleanLiteral otherLiteral = (BooleanLiteral) other;
        return ((positive == otherLiteral.positive) && Objects.equals(getVariable(), otherLiteral.getVariable()));
    }

    @Override
    public String toString() {
        return (positive ? "+" : "-") + getName();
    }

    @Override
    public Boolean eval(List<?> values) {
        assert Formula.checkValues(1, values);
        assert Formula.checkValues(Boolean.class, values);
        final Boolean b = (Boolean) values.get(0);
        return b != null ? positive == b : null;
    }
}
