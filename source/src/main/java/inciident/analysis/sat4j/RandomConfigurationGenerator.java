
package inciident.analysis.sat4j;

import java.util.Random;

import inciident.analysis.sat4j.solver.SStrategy;
import inciident.analysis.sat4j.solver.Sat4JSolver;
import inciident.analysis.solver.RuntimeContradictionException;
import inciident.analysis.solver.RuntimeTimeoutException;
import inciident.analysis.solver.SatSolver.SatResult;
import inciident.clauses.LiteralList;
import inciident.util.job.InternalMonitor;


public abstract class RandomConfigurationGenerator extends AbstractConfigurationGenerator {

    protected boolean satisfiable = true;

    public RandomConfigurationGenerator() {
        super();
        setRandom(new Random());
    }

    @Override
    protected void init(InternalMonitor monitor) {
        super.init(monitor);
        satisfiable = true;
    }

    @Override
    protected void prepareSolver(Sat4JSolver solver) {
        super.prepareSolver(solver);
        solver.setSelectionStrategy(SStrategy.random());
    }

    @Override
    public LiteralList get() {
        reset();
        solver.shuffleOrder(random);
        final SatResult hasSolution = solver.hasSolution();
        switch (hasSolution) {
            case FALSE:
                satisfiable = false;
                return null;
            case TIMEOUT:
                throw new RuntimeTimeoutException();
            case TRUE:
                final LiteralList solution = solver.getSolution();
                if (!allowDuplicates) {
                    try {
                        forbidSolution(solution.negate());
                    } catch (final RuntimeContradictionException e) {
                        satisfiable = false;
                    }
                }
                return solution;
            default:
                throw new IllegalStateException(String.valueOf(hasSolution));
        }
    }

    protected void forbidSolution(final LiteralList negate) {
        solver.getFormula().push(negate);
    }

    protected void reset() {}
}
