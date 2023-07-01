
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;


public class Implies extends Compound {

    public Implies(Formula leftNode, Formula rightNode) {
        super(leftNode, rightNode);
    }

    private Implies() {
        super();
    }

    public Implies(List<? extends Formula> nodes) {
        super(nodes);
        if (nodes.size() != 2) throw new IllegalArgumentException("implies requires two arguments");
    }

    @Override
    public Implies cloneNode() {
        return new Implies();
    }

    @Override
    public String getName() {
        return "implies";
    }

    @Override
    public Object eval(List<?> values) {
        return (boolean) values.get(1) || !(boolean) values.get(0);
    }
}
