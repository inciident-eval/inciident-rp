
package inciident.formula.structure.compound;

import java.util.List;

import inciident.formula.structure.Formula;


public class Between extends Cardinal {

    public Between(List<Formula> nodes, int min, int max) {
        super(nodes, min, max);
    }

    private Between(Between oldNode) {
        super(oldNode);
    }

    @Override
    public Between cloneNode() {
        return new Between(this);
    }

    @Override
    public String getName() {
        return "between-" + min + "-" + max;
    }

    @Override
    public void setMin(int min) {
        super.setMin(min);
    }

    @Override
    public void setMax(int max) {
        super.setMax(max);
    }
}
