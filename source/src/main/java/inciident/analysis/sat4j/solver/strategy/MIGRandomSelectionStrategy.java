
package inciident.analysis.sat4j.solver.strategy;

import static org.sat4j.core.LiteralsUtils.negLit;
import static org.sat4j.core.LiteralsUtils.posLit;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.minisat.core.IPhaseSelectionStrategy;

import inciident.analysis.sat4j.solver.SampleDistribution;


public class MIGRandomSelectionStrategy implements IPhaseSelectionStrategy {

    private static final long serialVersionUID = 1L;

    private final SampleDistribution dist;

    public MIGRandomSelectionStrategy(SampleDistribution sampleDistribution) {
        dist = sampleDistribution;
    }

    public void undo(int var) {
        dist.unset(var);
    }

    @Override
    public void assignLiteral(int p) {
        dist.set(LiteralsUtils.toDimacs(p));
    }

    @Override
    public void init(int nlength) {}

    @Override
    public void init(int var, int p) {}

    @Override
    public int select(int var) {
        return dist.getRandomLiteral(var) > 0 ? posLit(var) : negLit(var);
    }

    @Override
    public void updateVar(int p) {}

    @Override
    public void updateVarAtDecisionLevel(int q) {}

    @Override
    public String toString() {
        return "uniform random phase selection";
    }
}
