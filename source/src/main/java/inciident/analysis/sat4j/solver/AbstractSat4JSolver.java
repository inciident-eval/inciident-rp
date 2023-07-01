
package inciident.analysis.sat4j.solver;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

import inciident.analysis.solver.SolutionSolver;
import inciident.clauses.CNF;
import inciident.clauses.LiteralList;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.util.data.Pair;


public abstract class AbstractSat4JSolver<T extends ISolver> implements SolutionSolver<LiteralList> {

    public static final int MAX_SOLUTION_BUFFER = 1000;

    protected CNF satInstance;

    protected final T solver;
    protected final Sat4JAssumptions assumptions;
    protected final Sat4JFormula formula;

    // TODO extract solution history in separate class
    protected LinkedList<LiteralList> solutionHistory = null;
    protected int solutionHistoryLimit = -1;
    protected int[] lastModel = null;

    protected boolean globalTimeout = false;

    private boolean contradiction = false;

    public AbstractSat4JSolver(VariableMap variableMap) {
        satInstance = null;
        solver = createSolver();
        configureSolver();
        formula = new Sat4JFormula(this, variableMap);
        initSolver(Collections.emptyList());

        assumptions = new Sat4JAssumptions(variableMap);
    }

    public AbstractSat4JSolver(CNF cnf) {
        satInstance = cnf;
        solver = createSolver();
        configureSolver();
        formula = new Sat4JFormula(this, cnf.getVariableMap());
        initSolver(cnf.getClauses());

        assumptions = new Sat4JAssumptions(cnf.getVariableMap());
    }

    
    public CNF getCnf() {
        return satInstance;
    }

    @Override
    public Sat4JAssumptions getAssumptions() {
        return assumptions;
    }

    @Override
    public Sat4JFormula getDynamicFormula() {
        return formula;
    }

    @Override
    public VariableMap getVariables() {
        return formula.getVariableMap();
    }

    
    @Override
    public LiteralList getSolution() {
        return new LiteralList(getLastModelCopy(), LiteralList.Order.INDEX, false);
    }

    public int[] getInternalSolution() {
        return lastModel;
    }

    private int[] getLastModelCopy() {
        return Arrays.copyOf(lastModel, lastModel.length);
    }

    
    public SatResult hasSolution(LiteralList assignment) {
        return hasSolution(assignment.getLiterals());
    }

    
    @Override
    public void reset() {
        solver.reset();
        if (solutionHistory != null) {
            solutionHistory.clear();
            lastModel = null;
        }
    }

    
    protected abstract T createSolver();

    
    protected void initSolver(List<LiteralList> clauses) {
        final int size = formula.getVariableMap().getVariableCount();
        //		final List<LiteralList> clauses = satInstance.getClauses();
        try {
            if (!clauses.isEmpty()) {
                solver.setExpectedNumberOfClauses(clauses.size() + 1);
                formula.push(clauses);
            }
            if (size > 0) {
                final VecInt pseudoClause = new VecInt(size + 1);
                for (int i = 1; i <= size; i++) {
                    pseudoClause.push(i);
                }
                pseudoClause.push(-1);
                solver.addClause(pseudoClause);
            }
        } catch (final Exception e) {
            contradiction = true;
        }
    }

    public void setTimeout(int timeout) {
        solver.setTimeoutMs(timeout);
    }

    public Sat4JFormula getFormula() {
        return formula;
    }

    @Override
    public LiteralList findSolution() {
        return hasSolution() == SatResult.TRUE ? getSolution() : null;
    }

    public List<LiteralList> getSolutionHistory() {
        return solutionHistory != null ? Collections.unmodifiableList(solutionHistory) : Collections.emptyList();
    }

    private int[] getAssumptionArray() {
        final List<Pair<Integer, Object>> all = assumptions.getAll();
        final int[] literals = new int[all.size()];
        int index = 0;
        for (final Pair<Integer, Object> entry : all) {
            final int variable = entry.getKey();
            literals[index++] = (entry.getValue() == Boolean.TRUE) ? variable : -variable;
        }
        return literals;
    }

    
    @Override
    public SatResult hasSolution() {
        if (contradiction) {
            lastModel = null;
            return SatResult.FALSE;
        }

        final int[] assumptionArray = getAssumptionArray();
        if (solutionHistory != null) {
            for (final LiteralList solution : solutionHistory) {
                if (solution.containsAllLiterals(assumptionArray)) {
                    lastModel = solution.getLiterals();
                    return SatResult.TRUE;
                }
            }
        }

        try {
            if (solver.isSatisfiable(new VecInt(assumptionArray), globalTimeout)) {
                lastModel = solver.model();
                addSolution();
                return SatResult.TRUE;
            } else {
                lastModel = null;
                return SatResult.FALSE;
            }
        } catch (final TimeoutException e) {
            lastModel = null;
            return SatResult.TIMEOUT;
        }
    }

    
    public SatResult hasSolution(int... assignment) {
        if (contradiction) {
            return SatResult.FALSE;
        }

        if (solutionHistory != null) {
            for (final LiteralList solution : solutionHistory) {
                if (solution.containsAllLiterals(assignment)) {
                    lastModel = solution.getLiterals();
                    return SatResult.TRUE;
                }
            }
        }

        final int[] unitClauses = new int[assignment.length];
        System.arraycopy(assignment, 0, unitClauses, 0, unitClauses.length);

        try {
            // TODO why is this necessary?
            if (solver.isSatisfiable(new VecInt(unitClauses), globalTimeout)) {
                lastModel = solver.model();
                addSolution();
                return SatResult.TRUE;
            } else {
                lastModel = null;
                return SatResult.FALSE;
            }
        } catch (final TimeoutException e) {
            lastModel = null;
            return SatResult.TIMEOUT;
        }
    }

    private void addSolution() {
        if (solutionHistory != null) {
            solutionHistory.addFirst(getSolution());
            if (solutionHistory.size() > solutionHistoryLimit) {
                solutionHistory.removeLast();
            }
        }
    }

    public int[] getContradictoryAssignment() {
        final IVecInt unsatExplanation = solver.unsatExplanation();
        return Arrays.copyOf(unsatExplanation.toArray(), unsatExplanation.size());
    }

    public List<LiteralList> rememberSolutionHistory(int numberOfSolutions) {
        if (numberOfSolutions > 0) {
            solutionHistory = new LinkedList<>();
            solutionHistoryLimit = numberOfSolutions;
        } else {
            solutionHistory = null;
            solutionHistoryLimit = -1;
        }
        return getSolutionHistory();
    }

    public boolean isGlobalTimeout() {
        return globalTimeout;
    }

    public void setGlobalTimeout(boolean globalTimeout) {
        this.globalTimeout = globalTimeout;
    }

    protected void configureSolver() {
        solver.setTimeoutMs(1_000_000);
        // TODO allow for non-modifiable solvers
        solver.setDBSimplificationAllowed(false);
        solver.setKeepSolverHot(true);
        solver.setVerbose(false);
    }
}
