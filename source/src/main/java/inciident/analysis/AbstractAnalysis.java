
package inciident.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import inciident.analysis.solver.RuntimeContradictionException;
import inciident.analysis.solver.Solver;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.Assignment;
import inciident.formula.structure.atomic.IndexAssignment;
import inciident.util.data.Cache;
import inciident.util.data.Provider;
import inciident.util.data.Result;
import inciident.util.job.Executor;
import inciident.util.job.InternalMonitor;


public abstract class AbstractAnalysis<T, S extends Solver, I> implements Analysis<T>, Provider<T> {

    protected static Object defaultParameters = new Object();

    // TODO fix caching / improve handling of many results with different parameters
    @Override
    public boolean storeInCache() {
        return false;
    }

    @Override
    public Result<T> apply(Cache c, InternalMonitor m) {
        return Executor.run(this::execute, c, m);
    }

    protected final Assignment assumptions = new IndexAssignment();
    protected final List<Formula> assumedConstraints = new ArrayList<>();
    protected Provider<I> solverInputProvider;
    protected S solver;

    public void setSolver(S solver) {
        this.solver = solver;
    }

    public void setSolverInputProvider(Provider<I> solverInputProvider) {
        this.solverInputProvider = solverInputProvider;
    }

    public Assignment getAssumptions() {
        return assumptions;
    }

    public List<Formula> getAssumedConstraints() {
        return assumedConstraints;
    }

    public void updateAssumptions() {
        updateAssumptions(this.solver);
    }

    public Object getParameters() {
        return Arrays.asList(assumptions, assumedConstraints);
    }

    @Override
    public final T execute(Cache c, InternalMonitor monitor) {
        if (solver == null) {
            solver = createSolver(c.get(solverInputProvider).get());
        }
        return execute(solver, monitor);
    }

    public T execute(S solver, InternalMonitor monitor) {
        if (this.solver == null) {
            this.solver = solver;
        }
        monitor.checkCancel();
        prepareSolver(solver);
        try {
            return analyze(solver, monitor);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            resetSolver(solver);
        }
    }

    

    protected abstract S createSolver(I input) throws RuntimeContradictionException;

    protected void prepareSolver(S solver) {
        updateAssumptions();
    }

    private void updateAssumptions(S solver) {
        solver.getAssumptions().setAll(assumptions.getAll());
        solver.getDynamicFormula().push(assumedConstraints);
    }

    protected abstract T analyze(S solver, InternalMonitor monitor) throws Exception;

    protected void resetSolver(S solver) {
        solver.getAssumptions().unsetAll(assumptions.getAll());
        solver.getDynamicFormula().pop(assumedConstraints.size());
    }

    @Override
    public void resetAssumptions() {
        solver.getAssumptions().unsetAll(assumptions.getAll());
        solver.getDynamicFormula().pop(assumedConstraints.size());
    }
}
