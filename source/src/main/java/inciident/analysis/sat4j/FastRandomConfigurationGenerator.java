
package inciident.analysis.sat4j;

import inciident.analysis.sat4j.solver.SStrategy;
import inciident.analysis.sat4j.solver.Sat4JSolver;
import inciident.clauses.solutions.SolutionList;
import inciident.util.data.Identifier;


public class FastRandomConfigurationGenerator extends RandomConfigurationGenerator {

    public static final Identifier<SolutionList> identifier = new Identifier<>();
    private SStrategy originalSelectionStrategy;

    @Override
    public Identifier<SolutionList> getIdentifier() {
        return identifier;
    }

    @Override
    protected void prepareSolver(Sat4JSolver solver) {
        super.prepareSolver(solver);
        originalSelectionStrategy = solver.getSelectionStrategy();
        solver.setSelectionStrategy(SStrategy.random(getRandom()));
    }

    @Override
    protected void resetSolver(Sat4JSolver solver) {
        super.resetSolver(solver);
        solver.setSelectionStrategy(originalSelectionStrategy);
    }
}
