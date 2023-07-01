
package inciident.analysis.sat4j.solver.strategy;

import static org.sat4j.core.LiteralsUtils.negLit;
import static org.sat4j.core.LiteralsUtils.posLit;

import java.util.Random;

import org.sat4j.minisat.core.IPhaseSelectionStrategy;


public class RandomSelectionStrategy implements IPhaseSelectionStrategy {

    private static final long serialVersionUID = 1L;

    public final Random RAND = new Random(123456789);

    @Override
    public void assignLiteral(int p) {}

    @Override
    public void init(int nlength) {}

    @Override
    public void init(int var, int p) {}

    @Override
    public int select(int var) {
        return RAND.nextBoolean() ? posLit(var) : negLit(var);
    }

    @Override
    public void updateVar(int p) {}

    @Override
    public void updateVarAtDecisionLevel(int q) {}

    @Override
    public String toString() {
        return "random phase selection";
    }
}
