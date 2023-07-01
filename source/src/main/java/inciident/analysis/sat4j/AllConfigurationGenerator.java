
package inciident.analysis.sat4j;

import inciident.analysis.solver.RuntimeContradictionException;
import inciident.clauses.LiteralList;
import inciident.clauses.solutions.SolutionList;
import inciident.util.data.Identifier;


public class AllConfigurationGenerator extends AbstractConfigurationGenerator {

    public static final Identifier<SolutionList> identifier = new Identifier<>();

    @Override
    public Identifier<SolutionList> getIdentifier() {
        return identifier;
    }

    private boolean satisfiable = true;

    @Override
    public LiteralList get() {
        if (!satisfiable) {
            return null;
        }
        final LiteralList solution = solver.findSolution();
        if (solution == null) {
            satisfiable = false;
            return null;
        }
        try {
            solver.getFormula().push(solution.negate());
        } catch (final RuntimeContradictionException e) {
            satisfiable = false;
        }
        return solution;
    }
}
