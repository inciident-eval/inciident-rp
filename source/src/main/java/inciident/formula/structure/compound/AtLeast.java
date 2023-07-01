
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;


public class AtLeast extends Cardinal {

    public AtLeast(List<Formula> nodes, int min) {
        super(nodes, min, Integer.MAX_VALUE);
    }

    private AtLeast(AtLeast oldNode) {
        super(oldNode);
    }

    @Override
    public AtLeast cloneNode() {
        return new AtLeast(this);
    }

    @Override
    public String getName() {
        return "atLeast-" + min;
    }

    @Override
    public int getMin() {
        return super.getMin();
    }

    @Override
    public void setMin(int min) {
        super.setMin(min);
    }
}
