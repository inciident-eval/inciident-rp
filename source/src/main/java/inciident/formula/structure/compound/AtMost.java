
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;


public class AtMost extends Cardinal {

    public AtMost(List<? extends Formula> nodes, int max) {
        super(nodes, 0, max);
    }

    private AtMost(AtMost oldNode) {
        super(oldNode);
    }

    @Override
    public AtMost cloneNode() {
        return new AtMost(this);
    }

    @Override
    public String getName() {
        return "atMost-" + max;
    }

    @Override
    public void setMax(int max) {
        super.setMax(max);
    }
}
