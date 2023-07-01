
package inciident.formula.structure.compound;

import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.VariableMap.Variable;

public class ForAll extends Quantifier {

    public ForAll(Variable boundVariable, Formula formula) {
        super(boundVariable, formula);
    }

    private ForAll(ForAll oldNode) {
        super(oldNode);
    }

    @Override
    public String getName() {
        return "for all";
    }

    @Override
    public ForAll cloneNode() {
        return new ForAll(this);
    }
}
