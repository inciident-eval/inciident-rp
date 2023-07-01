
package inciident.util.tree.structure;

import java.util.Collections;
import java.util.List;

public abstract class AbstractTerminal<T extends Tree<T>> implements Tree<T> {

    @Override
    public void setChildren(List<? extends T> children) {}

    @Override
    public List<? extends T> getChildren() {
        return Collections.emptyList();
    }
}
