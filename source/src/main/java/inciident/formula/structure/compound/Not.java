
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;


public class Not extends Compound {

    public Not(Formula node) {
        super(node);
    }

    private Not() {
        super();
    }

    public Not(List<? extends Formula> nodes) {
        super(nodes);
        if (nodes.size() != 1) throw new IllegalArgumentException("not requires one argument");
    }

    @Override
    public Not cloneNode() {
        return new Not();
    }

    @Override
    public String getName() {
        return "not";
    }

    @Override
    public Boolean eval(List<?> values) {
        return !(boolean) values.get(0);
    }
}
