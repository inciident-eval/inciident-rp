
package inciident.analysis.sat4j;

import java.util.Random;

import inciident.analysis.AbstractAnalysis;
import inciident.analysis.sat4j.solver.Sat4JSolver;
import inciident.analysis.solver.RuntimeContradictionException;
import inciident.analysis.solver.RuntimeTimeoutException;
import inciident.clauses.CNF;
import inciident.clauses.CNFProvider;
import inciident.util.job.InternalMonitor;


public abstract class Sat4JAnalysis<T> extends AbstractAnalysis<T, Sat4JSolver, CNF> {

    protected boolean timeoutOccurred = false;
    private boolean throwTimeoutException = true;
    private int timeout = 1000;

    protected Random random = new Random(112358);

    public Sat4JAnalysis() {
        super();
        solverInputProvider = CNFProvider.fromFormula();
    }

    @Override
    public Object getParameters() {
        return assumptions != null ? assumptions : super.getParameters();
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public final T execute(CNF cnf, InternalMonitor monitor) {
        if (solver == null) {
            solver = createSolver(cnf);
        }
        return execute(solver, monitor);
    }

    @Override
    protected Sat4JSolver createSolver(CNF input) throws RuntimeContradictionException {
        return new Sat4JSolver(input);
    }

    @Override
    protected void prepareSolver(Sat4JSolver solver) {
        super.prepareSolver(solver);
        solver.setTimeout(timeout);
        timeoutOccurred = false;
    }

    protected final void reportTimeout() throws RuntimeTimeoutException {
        timeoutOccurred = true;
        if (throwTimeoutException) {
            throw new RuntimeTimeoutException();
        }
    }

    public final boolean isThrowTimeoutException() {
        return throwTimeoutException;
    }

    public final void setThrowTimeoutException(boolean throwTimeoutException) {
        this.throwTimeoutException = throwTimeoutException;
    }

    public final boolean isTimeoutOccurred() {
        return timeoutOccurred;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
