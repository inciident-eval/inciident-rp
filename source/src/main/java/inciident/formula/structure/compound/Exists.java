
package inciident.formula.structure.compound;

import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.VariableMap.Variable;

public class Exists extends Quantifier {

    public Exists(Variable boundVariable, Formula formula) {
        super(boundVariable, formula);
    }

    private Exists(Exists oldNode) {
        super(oldNode);
    }

    @Override
    public String getName() {
        return "exists";
    }

    @Override
    public Exists cloneNode() {
        return new Exists(this);
    }
}
