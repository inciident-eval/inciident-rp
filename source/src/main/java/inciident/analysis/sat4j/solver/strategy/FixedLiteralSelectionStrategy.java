
package inciident.analysis.sat4j.solver.strategy;

import static org.sat4j.core.LiteralsUtils.negLit;
import static org.sat4j.core.LiteralsUtils.posLit;
import static org.sat4j.core.LiteralsUtils.var;

import org.sat4j.minisat.core.IPhaseSelectionStrategy;

public class FixedLiteralSelectionStrategy implements IPhaseSelectionStrategy {

    private static final long serialVersionUID = -1687370944480053808L;

    protected final int[] model;

    protected final int[] phase;

    public FixedLiteralSelectionStrategy(int[] model) {
        this.model = model;
        phase = new int[model.length + 1];
        reset(model.length + 1);
    }

    @Override
    public void updateVar(int p) {}

    @Override
    public void assignLiteral(int p) {
        final int var = var(p);
        if (model[var - 1] == 0) {
            phase[var] = p;
        }
    }

    @Override
    public void updateVarAtDecisionLevel(int q) {}

    @Override
    public void init(int nlength) {
        reset(nlength);
    }

    protected void reset(int nlength) {
        for (int i = 1; i < nlength; i++) {
            phase[i] = model[i - 1] > 0 ? posLit(i) : negLit(i);
        }
    }

    @Override
    public void init(int var, int p) {}

    @Override
    public int select(int var) {
        return phase[var];
    }
}
