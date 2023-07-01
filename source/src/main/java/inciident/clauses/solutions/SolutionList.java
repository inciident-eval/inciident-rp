
package inciident.clauses.solutions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import inciident.clauses.CNF;
import inciident.clauses.LiteralList;
import inciident.formula.structure.atomic.literal.VariableMap;


public class SolutionList implements Serializable {

    private static final long serialVersionUID = 3882530497452645334L;

    protected final List<LiteralList> solutions;
    protected VariableMap variables;

    public SolutionList() {
        solutions = new ArrayList<>();
    }

    public SolutionList(VariableMap mapping, List<LiteralList> solutions) {
        variables = mapping;
        this.solutions = solutions;
    }

    public void addSolution(LiteralList clause) {
        solutions.add(clause);
    }

    public void addSolutions(Collection<LiteralList> clauses) {
        solutions.addAll(clauses);
    }

    public void setVariables(VariableMap variables) {
        this.variables = variables;
    }

    public VariableMap getVariableMap() {
        return variables;
    }

    public void adapt2(VariableMap newVariables) {
        for (ListIterator<LiteralList> iterator = solutions.listIterator(); iterator.hasNext(); ) {
            iterator.set(iterator.next().adapt2(variables, newVariables));
        }
    }

    public List<LiteralList> getSolutions() {
        return solutions;
    }

    public LiteralList getSolution(int index) {
        return solutions.get(index);
    }

    public LiteralList getVariableAssingment(int variable) {
        final int[] assignment = new int[solutions.size()];
        int index = 0;
        for (final LiteralList solution : solutions) {
            assignment[index++] = solution.getLiterals()[variable];
        }
        return new LiteralList(assignment, LiteralList.Order.UNORDERED);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((solutions == null) ? 0 : solutions.hashCode());
        result = (prime * result) + ((variables == null) ? 0 : variables.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final SolutionList other = (SolutionList) obj;
        if (solutions == null) {
            if (other.solutions != null) {
                return false;
            }
        } else if (!solutions.equals(other.solutions)) {
            return false;
        }
        if (variables == null) {
            if (other.variables != null) {
                return false;
            }
        } else if (!variables.equals(other.variables)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CNF\n\tvariables=" + variables + "\n\tsolutions=" + solutions;
    }

    private String literalToString(int literal) {
        final Optional<String> name = variables.getVariableName(Math.abs(literal));
        return name.isEmpty() ? "?" : (literal > 0 ? "" : "-") + name.get();
    }

    public String getSolutionsString() {
        final StringBuilder sb = new StringBuilder();
        for (final LiteralList clause : solutions) {
            sb.append("(");
            final List<String> literals = Arrays.stream(clause.getLiterals())
                    .mapToObj(this::literalToString)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            for (final String literal : literals) {
                sb.append(literal);
                sb.append(", ");
            }
            if (!literals.isEmpty()) {
                sb.delete(sb.length() - 2, sb.length());
            }
            sb.append("), ");
        }
        if (!solutions.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

    public Stream<LiteralList> getInvalidSolutions(CNF cnf) {
        return solutions.stream() //
                .filter(s -> cnf.getClauses().stream() //
                        .anyMatch(clause -> s.containsAll(clause.negate())));
    }

    public Stream<LiteralList> getValidSolutions(CNF cnf) {
        return solutions.stream() //
                .filter(s -> cnf.getClauses().stream() //
                        .allMatch(clause -> s.hasDuplicates(clause)));
    }
}
