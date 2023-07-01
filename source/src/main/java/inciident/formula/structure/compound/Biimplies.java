
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;


public class Biimplies extends Compound {

    public Biimplies(Formula leftNode, Formula rightNode) {
        super(leftNode, rightNode);
    }

    private Biimplies() {
        super();
    }

    public Biimplies(List<? extends Formula> nodes) {
        super(nodes);
        if (nodes.size() != 2) throw new IllegalArgumentException("biimplies requires two arguments");
    }

    @Override
    public Biimplies cloneNode() {
        return new Biimplies();
    }

    @Override
    public String getName() {
        return "biimplies";
    }

    @Override
    public Object eval(List<?> values) {
        return (boolean) values.get(1) == (boolean) values.get(0);
    }
}
