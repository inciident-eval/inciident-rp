
package inciident.analysis.sat4j;

import inciident.analysis.sat4j.solver.Sat4JSolver;
import inciident.analysis.solver.SatSolver;
import inciident.util.data.Identifier;
import inciident.util.job.InternalMonitor;


public class HasSolutionAnalysis extends Sat4JAnalysis<Boolean> {

    public static final Identifier<Boolean> identifier = new Identifier<>();

    @Override
    public Identifier<Boolean> getIdentifier() {
        return identifier;
    }

    @Override
    public Boolean analyze(Sat4JSolver solver, InternalMonitor monitor) throws Exception {
        final SatSolver.SatResult hasSolution = solver.hasSolution();
        switch (hasSolution) {
            case FALSE:
                return false;
            case TIMEOUT:
                reportTimeout();
                return false;
            case TRUE:
                return true;
            default:
                throw new AssertionError(hasSolution);
        }
    }
}
