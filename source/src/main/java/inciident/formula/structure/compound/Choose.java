
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;


public class Choose extends Cardinal {

    public Choose(List<Formula> nodes, int k) {
        super(nodes, k, k);
    }

    private Choose(Choose oldNode) {
        super(oldNode);
    }

    @Override
    public Choose cloneNode() {
        return new Choose(this);
    }

    @Override
    public String getName() {
        return "choose-" + min;
    }

    public void setK(int k) {
        super.setMin(k);
        super.setMax(k);
    }

    public int getK() {
        return super.getMin();
    }
}
