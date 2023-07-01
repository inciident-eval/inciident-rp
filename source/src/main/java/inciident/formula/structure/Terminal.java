
package inciident.formula.structure;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import inciident.formula.structure.term.Term;
import inciident.util.tree.structure.AbstractTerminal;


public abstract class Terminal extends AbstractTerminal<Formula> implements Formula {

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), getName());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return equalsNode(other);
    }

    @Override
    public boolean equalsNode(Object other) {
        return (getClass() == other.getClass()) && Objects.equals(getName(), ((Terminal) other).getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public List<? extends Term> getChildren() {
        return Collections.emptyList();
    }
}
