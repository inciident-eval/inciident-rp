
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;


public abstract class Cardinal extends Compound {

    protected int min;
    protected int max;

    public Cardinal(List<? extends Formula> nodes, int min, int max) {
        super(nodes);
        setMin(min);
        setMax(max);
    }

    public Cardinal(Cardinal oldNode) {
        super();
        setMin(oldNode.min);
        setMax(oldNode.max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    protected void setMin(int min) {
        if (min < 0) {
            throw new IllegalArgumentException(Integer.toString(min));
        }
        this.min = min;
    }

    protected void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException(Integer.toString(max));
        }
        this.max = max;
    }

    public void setChildren(List<? extends Formula> children) {
        if (min > children.size()) {
            throw new IllegalArgumentException(Integer.toString(min));
        }
        if (max > children.size()) {
            throw new IllegalArgumentException(Integer.toString(max));
        }
        super.setChildren(children);
    }

    @Override
    protected int computeHashCode() {
        int hashCode = super.computeHashCode();
        hashCode = (37 * hashCode) + min;
        hashCode = (37 * hashCode) + max;
        return hashCode;
    }

    @Override
    public boolean equalsNode(Object other) {
        if (!super.equalsNode(other)) {
            return false;
        }
        final Cardinal otherCardinal = (Cardinal) other;
        return (min == otherCardinal.min) && (max == otherCardinal.max);
    }

    @Override
    public Object eval(List<?> values) {
        final int trueCount =
                (int) values.stream().filter(v -> v == Boolean.TRUE).count();
        final int nullCount = (int) values.stream().filter(v -> v == null).count();
        if (trueCount + nullCount < min || trueCount > max) {
            return Boolean.FALSE;
        }
        if (trueCount >= min && trueCount + nullCount <= max) {
            return Boolean.TRUE;
        }
        return null;
    }
}
