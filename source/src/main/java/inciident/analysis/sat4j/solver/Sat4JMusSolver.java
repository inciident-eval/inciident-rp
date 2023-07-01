
package inciident.analysis.sat4j.solver;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.xplain.Xplain;

import inciident.analysis.solver.MusSolver;
import inciident.clauses.CNF;
import inciident.clauses.CNFProvider;
import inciident.formula.ModelRepresentation;


public class Sat4JMusSolver extends AbstractSat4JSolver<Xplain<ISolver>> implements MusSolver<IConstr> {

    public Sat4JMusSolver(ModelRepresentation modelRepresentation) {
        this(modelRepresentation.getCache().get(CNFProvider.fromFormula()).get());
    }

    public Sat4JMusSolver(CNF cnf) {
        super(cnf);
    }

    @Override
    protected Xplain<ISolver> createSolver() {
        return new Xplain<>(SolverFactory.newDefault());
    }

    @Override
    public List<IConstr> getMinimalUnsatisfiableSubset() throws IllegalStateException {
        if (hasSolution() == SatResult.TRUE) {
            throw new IllegalStateException("Problem is satisfiable");
        }
        try {
            return IntStream.of(solver.minimalExplanation()) //
                    .mapToObj(getFormula().getConstraints()::get) //
                    .collect(Collectors.toList());
        } catch (final TimeoutException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<List<IConstr>> getAllMinimalUnsatisfiableSubsets() throws IllegalStateException {
        return Collections.singletonList(getMinimalUnsatisfiableSubset());
    }
}
