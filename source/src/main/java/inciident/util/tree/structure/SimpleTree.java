
package inciident.util.tree.structure;

import java.util.Objects;

public class SimpleTree<D> extends AbstractNonTerminal<SimpleTree<D>> {

    protected D data;

    public SimpleTree() {
        super();
    }

    public SimpleTree(D data) {
        super();
        this.data = data;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    @Override
    public SimpleTree<D> cloneNode() {
        final SimpleTree<D> clone = new SimpleTree<>();
        clone.data = data;
        return clone;
    }

    @Override
    public boolean equalsNode(Object other) {
        return (getClass() == other.getClass()) && Objects.equals(getData(), ((SimpleTree<?>) other).getData());
    }

    @Override
    public String toString() {
        return "SimpleTree [" + data + "]";
    }
}
