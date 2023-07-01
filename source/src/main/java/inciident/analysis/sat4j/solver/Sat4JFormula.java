
package inciident.analysis.sat4j.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;

import inciident.analysis.solver.AbstractDynamicFormula;
import inciident.analysis.solver.RuntimeContradictionException;
import inciident.clauses.FormulaToCNF;
import inciident.clauses.LiteralList;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.VariableMap;


public class Sat4JFormula extends AbstractDynamicFormula<IConstr> {

    private final AbstractSat4JSolver<?> sat4jSolver;

    public Sat4JFormula(AbstractSat4JSolver<?> solver, VariableMap variableMap) {
        super(variableMap);
        sat4jSolver = solver;
    }

    protected Sat4JFormula(AbstractSat4JSolver<?> solver, Sat4JFormula oldFormula) {
        super(oldFormula);
        sat4jSolver = solver;
    }

    @Override
    public List<IConstr> push(Formula formula) throws RuntimeContradictionException {
        return push(FormulaToCNF.convert(formula, variableMap).getClauses());
    }

    public List<IConstr> push(List<? extends LiteralList> clauses) {
        final ArrayList<IConstr> constrs = new ArrayList<>();
        for (final LiteralList clause : clauses) {
            try {
                if ((clause.size() == 1) && (clause.getLiterals()[0] == 0)) {
                    throw new ContradictionException();
                }
                final IConstr constr =
                        sat4jSolver.solver.addClause(new VecInt(Arrays.copyOf(clause.getLiterals(), clause.size())));
                constrs.add(constr);
            } catch (final ContradictionException e) {
                for (final IConstr constr : constrs) {
                    sat4jSolver.solver.removeConstr(constr);
                }
                throw new RuntimeContradictionException(e);
            }
        }
        if (sat4jSolver.solutionHistory != null) {
            sat4jSolver.solutionHistory.clear();
            sat4jSolver.lastModel = null;
        }
        constraints.addAll(constrs);
        return constrs;
    }

    public IConstr push(LiteralList clause) throws RuntimeContradictionException {
        try {
            if ((clause.size() == 1) && (clause.getLiterals()[0] == 0)) {
                throw new ContradictionException();
            }
            final IConstr constr = sat4jSolver.solver.addClause(
                    new VecInt(Arrays.copyOfRange(clause.getLiterals(), 0, clause.size())));
            constraints.add(constr);
            if (sat4jSolver.solutionHistory != null) {
                sat4jSolver.solutionHistory.clear();
                sat4jSolver.lastModel = null;
            }
            return constr;
        } catch (final ContradictionException e) {
            throw new RuntimeContradictionException(e);
        }
    }

    @Override
    public IConstr pop() {
        final IConstr lastConstraint = super.pop();
        sat4jSolver.solver.removeConstr(lastConstraint);
        return lastConstraint;
    }

    @Override
    public void remove(IConstr constr) {
        if (constr != null) {
            sat4jSolver.solver.removeConstr(constr);
            super.remove(constr);
        }
    }

    @Override
    public void pop(int count) {
        if (count > constraints.size()) {
            count = constraints.size();
        }
        for (int i = 0; i < count; i++) {
            final IConstr lastConstraint = removeConstraint(constraints.size() - 1);
            if (lastConstraint != null) {
                try {
                    sat4jSolver.solver.removeSubsumedConstr(lastConstraint);
                } catch (IllegalArgumentException e) {
                }
            }
        }
        sat4jSolver.solver.clearLearntClauses();
    }
}
