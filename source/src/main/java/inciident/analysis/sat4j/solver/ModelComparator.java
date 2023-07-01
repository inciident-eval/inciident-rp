
package inciident.analysis.sat4j.solver;

import org.sat4j.specs.TimeoutException;

import inciident.analysis.solver.SatSolver;
import inciident.clauses.CNF;
import inciident.clauses.LiteralList;

public abstract class ModelComparator {

    public static boolean eq(CNF cnf1, final CNF cnf2) throws TimeoutException {
        return compare(cnf2, cnf1) && compare(cnf1, cnf2);
    }

    public static boolean compare(CNF cnf1, final CNF cnf2) throws TimeoutException {
        final Sat4JSolver solver = new Sat4JSolver(cnf1);
        for (final LiteralList clause : cnf2.getClauses()) {
            final SatSolver.SatResult satResult = solver.hasSolution(clause.negate());
            switch (satResult) {
                case FALSE:
                    break;
                case TIMEOUT:
                    throw new TimeoutException();
                case TRUE:
                    return false;
                default:
                    assert false;
            }
        }
        return true;
    }
}
