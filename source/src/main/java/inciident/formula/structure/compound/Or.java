
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;


public class Or extends Compound {

    public Or(List<? extends Formula> nodes) {
        super(nodes);
    }

    public Or(Formula... nodes) {
        super(nodes);
    }

    private Or() {
        super();
    }

    @Override
    public Or cloneNode() {
        return new Or();
    }

    @Override
    public String getName() {
        return "or";
    }

    @Override
    public Object eval(List<?> values) {
        if (values.stream().anyMatch(v -> v == Boolean.TRUE)) {
            return Boolean.TRUE;
        }
        return values.stream().filter(v -> v == Boolean.FALSE).count() == children.size() ? Boolean.FALSE : null;
    }
}
