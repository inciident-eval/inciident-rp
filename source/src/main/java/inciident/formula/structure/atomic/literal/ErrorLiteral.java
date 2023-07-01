
package inciident.formula.structure.atomic.literal;

import java.util.List;
import java.util.Objects;

import inciident.formula.structure.Formula;
import inciident.formula.structure.Terminal;


public class ErrorLiteral extends Terminal implements Literal {

    private final String errorMessage;
    private final boolean positive;

    public ErrorLiteral(String errorMessage) {
        this(errorMessage, true);
    }

    public ErrorLiteral(String errorMessage, boolean positive) {
        this.errorMessage = errorMessage;
        this.positive = positive;
    }

    @Override
    public String getName() {
        return errorMessage;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }

    @Override
    public ErrorLiteral flip() {
        return new ErrorLiteral(errorMessage, !positive);
    }

    @Override
    public ErrorLiteral cloneNode() {
        return new ErrorLiteral(errorMessage, positive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positive);
    }

    @Override
    public boolean equalsNode(Object other) {
        if (getClass() != other.getClass()) {
            return false;
        }
        final ErrorLiteral otherLiteral = (ErrorLiteral) other;
        return (positive == otherLiteral.positive);
    }

    @Override
    public String toString() {
        return (positive ? "+" : "-") + getName();
    }

    @Override
    public Object eval(List<?> values) {
        assert Formula.checkValues(0, values);
        return positive;
    }
}
