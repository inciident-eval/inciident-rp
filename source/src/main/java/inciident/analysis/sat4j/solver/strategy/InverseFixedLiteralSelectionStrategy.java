
package inciident.analysis.sat4j.solver.strategy;

import static org.sat4j.core.LiteralsUtils.negLit;
import static org.sat4j.core.LiteralsUtils.posLit;

public class InverseFixedLiteralSelectionStrategy extends FixedLiteralSelectionStrategy {

    public InverseFixedLiteralSelectionStrategy(int[] model) {
        super(model);
    }

    private static final long serialVersionUID = -1563968211094190882L;

    @Override
    protected void reset(int nlength) {
        for (int i = 1; i < nlength; i++) {
            phase[i] = model[i - 1] < 0 ? posLit(i) : negLit(i);
        }
    }
}
