
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.NonTerminal;


public abstract class Compound extends NonTerminal implements Formula {
    public Compound(List<? extends Formula> nodes) {
        super(nodes);
    }

    public Compound(Formula... nodes) {
        super(nodes);
    }

    protected Compound() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Formula> getChildren() {
        return (List<Formula>) super.getChildren();
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}
