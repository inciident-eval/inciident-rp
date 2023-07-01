
package inciident.analysis.sat4j;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import inciident.analysis.sat4j.solver.Sat4JSolver;
import inciident.analysis.solver.RuntimeContradictionException;
import inciident.clauses.CNF;
import inciident.clauses.CNFProvider;
import inciident.clauses.Clauses;
import inciident.clauses.LiteralList;
import inciident.clauses.LiteralList.Order;
import inciident.clauses.solutions.analysis.ConfigurationUpdater;
import inciident.formula.ModelRepresentation;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.Assignment;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.util.data.Pair;
import inciident.util.job.NullMonitor;

public class RandomConfigurationUpdater implements ConfigurationUpdater {
    private final RandomConfigurationGenerator generator;
    private final ModelRepresentation model;

    public RandomConfigurationUpdater(ModelRepresentation model, Random random) {
        this.model = model;
        generator = new FastRandomConfigurationGenerator();
        generator.setAllowDuplicates(true);
        generator.setRandom(random);
        generator.init(model.getCache(), new NullMonitor());
    }

    @Override
    public Optional<LiteralList> update(LiteralList partialSolution) {
        final CoreDeadAnalysis analysis = new CoreDeadAnalysis();
        for (int l : partialSolution.getLiterals()) {
            analysis.getAssumptions().set(Math.abs(l), l > 0);
        }
        final LiteralList otherLiterals = model.get(analysis);
        return otherLiterals == null ? Optional.empty() : Optional.of(partialSolution.addAll(otherLiterals));
    }

    @Override
    public Optional<LiteralList> complete(LiteralList partialSolution, Collection<LiteralList> excludeClauses) {
    	excludeClauses = excludeClauses != null ? excludeClauses : Collections.emptyList();
    	if (partialSolution == null && excludeClauses.isEmpty()) {
            return Optional.ofNullable(generator.get());
        }
        final Assignment assumptions = generator.getAssumptions();
        final List<Pair<Integer, Object>> oldAssumptions = assumptions.getAll();

        if (partialSolution != null) {
            for (int literal : partialSolution.getLiterals()) {
                assumptions.set(Math.abs(literal), literal > 0);
            }
        }
        VariableMap variables = model.getVariables();
        List<Formula> assumedConstraints = generator.getAssumedConstraints();
        for (LiteralList clause : excludeClauses) {
            assumedConstraints.add(Clauses.toOrClause(clause.negate(), variables));
        }
        try {
            generator.updateAssumptions();
            return Optional.ofNullable(generator.get());
        } catch (RuntimeContradictionException e) {
            return Optional.empty();
        } finally {
            generator.resetAssumptions();
            assumptions.unsetAll();
            assumptions.setAll(oldAssumptions);
            assumedConstraints.clear();
            generator.updateAssumptions();
        }
    }

    @Override
    public Optional<LiteralList> choose(Collection<LiteralList> clauses) {
        if (clauses.isEmpty()) {
            return Optional.ofNullable(generator.get());
        }
        LiteralList merge = LiteralList.merge(clauses, model.getVariables().getVariableCount());

        CNF modelCnf = model.getCache().get(CNFProvider.fromFormula()).get();
        VariableMap variables = new VariableMap(modelCnf.getVariableMap());
        CNF cnf = new CNF(variables, modelCnf.getClauses());

        int[] newNegativeLiterals = new int[clauses.size()];
        int i = 0;
        for (LiteralList clause : clauses) {
            int newVar = variables.addBooleanVariable().getIndex();
            newNegativeLiterals[i++] = -newVar;

            for (int l : clause.getLiterals()) {
                cnf.addClause(new LiteralList(new int[] {l, newVar}, Order.UNORDERED, false));
            }
        }
        cnf.addClause(new LiteralList(newNegativeLiterals, Order.UNORDERED, false));
        cnf.addClause(merge.negate());
        try {
            RandomConfigurationGenerator generator = new FastRandomConfigurationGenerator();
            // TODO return list?
            generator.setAllowDuplicates(true);
            generator.setRandom(this.generator.getRandom());
            generator.setSolver(new Sat4JSolver(cnf));
            LiteralList literalList = generator.get();

            return literalList == null //
                    ? Optional.empty()
                    : Optional.of(new LiteralList(Arrays.copyOf(
                            literalList.getLiterals(), modelCnf.getVariableMap().getVariableCount())));
        } catch (RuntimeContradictionException e) {
            return Optional.empty();
        }
    }
}
