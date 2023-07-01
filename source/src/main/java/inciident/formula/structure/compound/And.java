
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;


public class And extends Compound {

    public And(List<? extends Formula> nodes) {
        super(nodes);
    }

    public And(Formula... nodes) {
        super(nodes);
    }

    private And() {
        super();
    }

    @Override
    public And cloneNode() {
        return new And();
    }

    @Override
    public String getName() {
        return "and";
    }

    @Override
    public Object eval(List<?> values) {
        if (values.stream().anyMatch(v -> v == Boolean.FALSE)) {
            return Boolean.FALSE;
        }
        return values.stream().filter(v -> v == Boolean.TRUE).count() == children.size() ? Boolean.TRUE : null;
    }
}
