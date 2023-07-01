
package inciident.clauses;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import inciident.formula.io.FormulaFormatManager;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Or;
import inciident.util.data.Cache;
import inciident.util.data.Provider;
import inciident.util.data.Result;
import inciident.util.job.Executor;
import inciident.util.logging.Logger;


public final class Clauses {

    private Clauses() {}

    public static LiteralList getVariables(Collection<LiteralList> clauses) {
        return new LiteralList(clauses.stream()
                .flatMapToInt(c -> Arrays.stream(c.getLiterals()))
                .distinct()
                .toArray());
    }

    public static LiteralList getLiterals(VariableMap variables) {
        return new LiteralList(IntStream.rangeClosed(1, variables.getVariableCount())
                .flatMap(i -> IntStream.of(-i, i))
                .toArray());
    }

    
    public static Stream<LiteralList> negate(Collection<LiteralList> clauses) {
        return clauses.stream().map(LiteralList::negate);
    }

    public static Result<LiteralList> adapt(LiteralList clause, VariableMap oldVariables, VariableMap newVariables) {
        return clause.adapt(oldVariables, newVariables);
    }

    public static int adapt(int literal, VariableMap oldVariables, VariableMap newVariables) {
        final String name = oldVariables.getVariableName(Math.abs(literal)).orElse(null);
        final int index = newVariables.getVariableIndex(name).orElse(0);
        return literal < 0 ? -index : index;
    }

    public static CNF convertToCNF(Formula formula) {
        return Executor.run(new FormulaToCNF(), formula).get();
    }

    public static CNF convertToCNF(Formula formula, VariableMap variableMap) {
        final FormulaToCNF function = new FormulaToCNF();
        function.setVariableMapping(variableMap);
        return Executor.run(function, formula).get();
    }

    public static CNF convertToDNF(Formula formula) {
        final CNF cnf = Executor.run(new FormulaToCNF(), formula).get();
        return new CNF(cnf.getVariableMap(), convertNF(cnf.getClauses()));
    }

    public static CNF convertToDNF(Formula formula, VariableMap variableMap) {
        final FormulaToCNF function = new FormulaToCNF();
        function.setVariableMapping(variableMap);
        final CNF cnf = Executor.run(function, formula).get();
        return new CNF(variableMap, convertNF(cnf.getClauses()));
    }

    
    public static List<LiteralList> convertNF(List<LiteralList> clauses) {
        final List<LiteralList> convertedClauseList = new ArrayList<>();
        convertNF(clauses, convertedClauseList, new int[clauses.size()], 0);
        return convertedClauseList;
    }

    private static void convertNF(List<LiteralList> cnf, List<LiteralList> dnf, int[] literals, int index) {
        if (index == cnf.size()) {
            final int[] newClauseLiterals = new int[literals.length];
            int count = 0;
            for (final int literal : literals) {
                if (literal != 0) {
                    newClauseLiterals[count++] = literal;
                }
            }
            if (count < newClauseLiterals.length) {
                dnf.add(new LiteralList(Arrays.copyOf(newClauseLiterals, count)));
            } else {
                dnf.add(new LiteralList(newClauseLiterals));
            }
        } else {
            final HashSet<Integer> literalSet = new HashSet<>();
            for (int i = 0; i <= index; i++) {
                literalSet.add(literals[i]);
            }
            int redundantCount = 0;
            final int[] literals2 = cnf.get(index).getLiterals();
            for (final int literal : literals2) {
                if (!literalSet.contains(-literal)) {
                    if (!literalSet.contains(literal)) {
                        literals[index] = literal;
                        convertNF(cnf, dnf, literals, index + 1);
                    } else {
                        redundantCount++;
                    }
                }
            }
            literals[index] = 0;
            if (redundantCount == literals2.length) {
                convertNF(cnf, dnf, literals, index + 1);
            }
        }
    }

    public static CNF open(Path path) {
        return Provider.load(path, FormulaFormatManager.getInstance())
                .map(Clauses::convertToCNF)
                .orElse(Logger::logProblems);
    }

    public static Result<CNF> load(Path path) {
        return Provider.load(path, FormulaFormatManager.getInstance()).map(Clauses::convertToCNF);
    }

    public static Result<CNF> load(Path path, Cache cache) {
        return cache.get(CNFProvider.loader(path));
    }

    public static Cache createCache(Path path) {
        final Cache cache = new Cache();
        cache.set(CNFProvider.loader(path));
        return cache;
    }

    public static Cache createCache(CNF cnf) {
        final Cache cache = new Cache();
        cache.set(CNFProvider.of(cnf));
        return cache;
    }

    public static Or toOrClause(LiteralList clause, VariableMap variableMap) {
        return new Or(toLiterals(clause, variableMap));
    }

    public static And toAndClause(LiteralList clause, VariableMap variableMap) {
        return new And(toLiterals(clause, variableMap));
    }

    public static List<Literal> toLiterals(LiteralList clause, VariableMap variableMap) {
        return Arrays.stream(clause.getLiterals())
                .mapToObj(l -> variableMap.createLiteral(Math.abs(l), l > 0))
                .collect(Collectors.toList());
    }
}
