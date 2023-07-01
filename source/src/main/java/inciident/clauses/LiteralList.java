
package inciident.clauses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import inciident.formula.structure.atomic.literal.NamedTermMap.ValueTerm;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.util.data.Problem;
import inciident.util.data.Problem.Severity;
import inciident.util.data.Result;

// TODO add methods for adding literals (e.g. addAll, union, ...)

public class LiteralList implements Cloneable, Comparable<LiteralList>, Serializable {

    private static final long serialVersionUID = 8360925003112707082L;

    public enum Order {
        NATURAL,
        INDEX,
        UNORDERED,
    }

    protected int[] literals;

    private int hashCode;

    private Order order = null;

    
    public static List<LiteralList> negate(List<LiteralList> clauseList) {
        final List<LiteralList> negatedClauseList = new ArrayList<>();
        clauseList.stream().map(LiteralList::negate).forEach(negatedClauseList::add);
        return negatedClauseList;
    }

    public static Result<List<LiteralList>> adapt(
            List<LiteralList> clauseList, VariableMap oldVariableMap, VariableMap newVariableMap) {
        final List<LiteralList> adaptedClauseList = new ArrayList<>();
        for (final LiteralList clause : clauseList) {
            final Result<LiteralList> adapted = clause.adapt(oldVariableMap, newVariableMap);
            if (adapted.isEmpty()) {
                return Result.empty(adapted.getProblems());
            }
            adaptedClauseList.add(adapted.get());
        }
        return Result.of(adaptedClauseList);
    }

    
    public static List<LiteralList> convert(List<LiteralList> clauseList) {
        final List<LiteralList> convertedClauseList = new ArrayList<>();
        convert(clauseList, convertedClauseList, new int[clauseList.size()], 0);
        return convertedClauseList;
    }

    private static void convert(List<LiteralList> nf1, List<LiteralList> nf2, int[] literals, int index) {
        if (index == nf1.size()) {
            final LiteralList literalSet =
                    new LiteralList(literals, Order.UNORDERED, false).clean().get();
            if (literalSet != null) {
                nf2.add(literalSet);
            }
        } else {
            for (final int literal : nf1.get(index).getLiterals()) {
                literals[index] = literal;
                convert(nf1, nf2, literals, index + 1);
            }
        }
    }

    
    public static void resetConflicts(final int[] solution1, int[] solution2) {
        for (int i = 0; i < solution1.length; i++) {
            final int x = solution1[i];
            final int y = solution2[i];
            if (x != y) {
                solution1[i] = 0;
            }
        }
    }

    public static LiteralList getVariables(CNF cnf) {
        return getVariables(cnf.getVariableMap());
    }

    public static LiteralList getVariables(VariableMap variableMap) {
        return new LiteralList(constructVariableStream(variableMap).toArray());
    }

    public static LiteralList getVariables(VariableMap variableMap, Collection<String> variableNames) {
        return new LiteralList(
                constructVariableStream(variableMap, variableNames).toArray());
    }

    public static LiteralList getLiterals(CNF cnf) {
        return getLiterals(cnf.getVariableMap());
    }

    public static LiteralList getLiterals(VariableMap variables) {
        return new LiteralList(constructVariableStream(variables)
                .flatMap(n -> IntStream.of(-n, n))
                .toArray());
    }

    public static LiteralList getLiterals(VariableMap variableMap, Collection<String> variableNames) {
        return new LiteralList(constructVariableStream(variableMap, variableNames)
                .flatMap(n -> IntStream.of(-n, n))
                .toArray());
    }

    private static IntStream constructVariableStream(VariableMap variables) {
        return IntStream.rangeClosed(1, variables.getVariableCount());
    }

    private static IntStream constructVariableStream(VariableMap variableMap, Collection<String> variableNames) {
        return variableNames.stream()
                .map(variableMap::getVariable)
                .flatMap(Optional::stream)
                .mapToInt(ValueTerm::getIndex)
                .distinct();
    }

    public static LiteralList merge(Collection<LiteralList> collection, int max) {
        if (collection.size() < max) {
            return new LiteralList(collection.stream()
                    .flatMapToInt(l -> Arrays.stream(l.getLiterals()))
                    .distinct()
                    .toArray());
        } else {
            int[] literals = new int[max + 1];
            collection.stream()
                    .flatMapToInt(l -> Arrays.stream(l.getLiterals()))
                    .forEach(i -> literals[Math.abs(i)] = i);
            return new LiteralList(Arrays.stream(literals).filter(e -> e != 0).toArray());
        }
    }

    
    public LiteralList(LiteralList clause) {
        literals = Arrays.copyOf(clause.literals, clause.literals.length);
        hashCode = clause.hashCode;
        order = clause.order;
    }

    public LiteralList(LiteralList clause, Order literalOrder) {
        literals = Arrays.copyOf(clause.literals, clause.literals.length);
        setOrder(literalOrder);
    }

    
    public LiteralList(int... literals) {
        this(literals, Order.NATURAL);
    }

    public LiteralList(int[] literals, Order literalOrder) {
        this(literals, literalOrder, true);
    }

    public LiteralList(int[] literals, Order literalOrder, boolean sort) {
        this.literals = literals;
        if (sort) {
            setOrder(literalOrder);
        } else {
            hashCode = Arrays.hashCode(literals);
            order = literalOrder;
        }
    }

    public LiteralList(Collection<Integer> literals) {
        this(literals.stream().mapToInt(Integer::intValue).toArray());
    }

    public LiteralList(Collection<Integer> literals, Order literalOrder) {
        this(literals.stream().mapToInt(Integer::intValue).toArray(), literalOrder);
    }

    public LiteralList(Collection<Integer> literals, Order literalOrder, boolean sort) {
        this(literals.stream().mapToInt(Integer::intValue).toArray(), literalOrder, sort);
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        if (this.order != order) {
            sortLiterals(order);
            this.order = order;
        }
    }

    private void sortLiterals(Order newOrder) {
        switch (newOrder) {
            case INDEX:
                final int[] sortedLiterals =
                        new int[Arrays.stream(literals).map(Math::abs).max().orElse(0)];
                for (int i = 0; i < literals.length; i++) {
                    final int literal = literals[i];
                    if (literal != 0) {
                        sortedLiterals[Math.abs(literal) - 1] = literal;
                    }
                }
                literals = sortedLiterals;
                break;
            case NATURAL:
                Arrays.sort(literals);
                break;
            case UNORDERED:
                break;
            default:
                break;
        }
        hashCode = Arrays.hashCode(literals);
    }

    public int[] getLiterals() {
        return literals;
    }

    public int get(int index) {
        return literals[index];
    }

    public int[] get(int start, int end) {
        return Arrays.copyOfRange(literals, start, end);
    }

    public boolean containsAnyLiteral(int... literals) {
        for (final int literal : literals) {
            if (indexOfLiteral(literal) >= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAllLiterals(int... literals) {
        for (final int literal : literals) {
            if (indexOfLiteral(literal) < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAnyVariable(int... variables) {
        for (final int variable : variables) {
            if (indexOfVariable(variable) >= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAllVariables(int... variables) {
        for (final int variable : variables) {
            if (indexOfVariable(variable) >= 0) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAny(LiteralList otherLiteralSet) {
        for (final int otherLiteral : otherLiteralSet.getLiterals()) {
            if (indexOfLiteral(otherLiteral) >= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(int[] otherLiteralSet) {
        for (final int otherLiteral : otherLiteralSet) {
            if (indexOfLiteral(otherLiteral) < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAll(LiteralList otherLiteralSet) {
        return containsAll(otherLiteralSet.getLiterals());
    }

    public int indexOfLiteral(int literal) {
        switch (order) {
            case UNORDERED:
                for (int i = 0; i < literals.length; i++) {
                    if (literal == literals[i]) {
                        return i;
                    }
                }
                return -1;
            case INDEX:
                final int index = Math.abs(literal) - 1;
                return literal == 0 || index >= literals.length ? -1 : literals[index] == literal ? index : -1;
            case NATURAL:
                return Arrays.binarySearch(literals, literal);
            default:
                throw new AssertionError(order);
        }
    }

    public int indexOfVariable(int variable) {
        switch (order) {
            case INDEX: {
                final int i = variable - 1;
                return (variable > 0) && (variable < size()) && literals[i] != 0 ? i : -1;
            }
            case UNORDERED:
            case NATURAL: {
                for (int i = 0; i < literals.length; i++) {
                    if (Math.abs(literals[i]) == variable) {
                        return i;
                    }
                }
                return -1;
            }
            default:
                throw new AssertionError(order);
        }
    }

    public int countNonNull() {
        int count = 0;
        switch (order) {
            case UNORDERED:
            case INDEX:
                for (final int literal : literals) {
                    if (literal != 0) {
                        count++;
                    }
                }
                break;
            case NATURAL:
                int i = 0;
                for (; i < literals.length; i++) {
                    if (literals[i] < 0) {
                        count++;
                    } else {
                        break;
                    }
                }
                for (; i < literals.length; i++) {
                    if (literals[i] > 0) {
                        break;
                    }
                }
                count += literals.length - i;
                break;
        }
        return count;
    }

    public int countNegative() {
        int count = 0;
        switch (order) {
            case UNORDERED:
            case INDEX:
                for (final int literal : literals) {
                    if (literal < 0) {
                        count++;
                    }
                }
                break;
            case NATURAL:
                for (int i = 0; i < literals.length; i++) {
                    if (literals[i] < 0) {
                        count++;
                    } else {
                        break;
                    }
                }
                break;
        }
        return count;
    }

    public int countPositive() {
        int count = 0;
        switch (order) {
            case UNORDERED:
            case INDEX:
                for (final int literal : literals) {
                    if (literal > 0) {
                        count++;
                    }
                }
                break;
            case NATURAL:
                for (int i = literals.length - 1; i >= 0; i--) {
                    if (literals[i] > 0) {
                        count++;
                    } else {
                        break;
                    }
                }
                break;
        }
        return count;
    }

    public int size() {
        return literals.length;
    }

    public LiteralList getVariables() {
        final int[] absoluteLiterals = new int[literals.length];
        for (int i = 0; i < literals.length; i++) {
            absoluteLiterals[i] = Math.abs(literals[i]);
        }
        return new LiteralList(absoluteLiterals);
    }

    public LiteralList addAll(LiteralList otherLiterals) {
        final boolean[] marker = new boolean[literals.length];
        final int count = countDuplicates(otherLiterals.literals, marker);

        final int[] newLiterals = new int[literals.length + otherLiterals.literals.length - count];
        int j = 0;
        for (int i = 0; i < literals.length; i++) {
            if (!marker[i]) {
                newLiterals[j++] = literals[i];
            }
        }
        System.arraycopy(otherLiterals.literals, 0, newLiterals, j, otherLiterals.literals.length);
        return new LiteralList(newLiterals, Order.UNORDERED, false);
    }

    public LiteralList removeAll(LiteralList otherLiterals) {
        final boolean[] removeMarker = new boolean[literals.length];
        final int count = countDuplicates(otherLiterals.literals, removeMarker);

        final int[] newLiterals = new int[literals.length - count];
        int j = 0;
        for (int i = 0; i < literals.length; i++) {
            if (!removeMarker[i]) {
                newLiterals[j++] = literals[i];
            }
        }
        return new LiteralList(newLiterals, order, false);
    }

    public LiteralList removeVariables(LiteralList variables) {
        return removeVariables(variables.literals);
    }

    public LiteralList removeVariables(int... variables) {
        final boolean[] removeMarker = new boolean[literals.length];
        final int count = countVariables(variables, removeMarker);

        final int[] newLiterals = new int[literals.length - count];
        int j = 0;
        for (int i = 0; i < literals.length; i++) {
            if (!removeMarker[i]) {
                newLiterals[j++] = literals[i];
            }
        }
        return new LiteralList(newLiterals, order, false);
    }

    public LiteralList retainAll(LiteralList otherLiterals) {
        final boolean[] marker = new boolean[literals.length];
        final int count = countDuplicates(otherLiterals.literals, marker);

        final int[] newLiterals = new int[count];
        int j = 0;
        for (int i = 0; i < literals.length; i++) {
            if (marker[i]) {
                newLiterals[j++] = literals[i];
            }
        }
        return new LiteralList(newLiterals, Order.UNORDERED, false);
    }

    public LiteralList retainVariables(LiteralList variables) {
        return retainVariables(variables.getLiterals());
    }

    public LiteralList retainVariables(int... variables) {
        final boolean[] removeMarker = new boolean[literals.length];
        final int count = countVariables(variables, removeMarker);

        final int[] newLiterals = new int[count];
        int j = 0;
        for (int i = 0; i < literals.length; i++) {
            if (removeMarker[i]) {
                newLiterals[j++] = literals[i];
            }
        }
        return new LiteralList(newLiterals, order, false);
    }

    private int countDuplicates(int[] otherLiterals, final boolean[] removeMarker) {
        int count = 0;
        for (int i = 0; i < otherLiterals.length; i++) {
            final int index = indexOfLiteral(otherLiterals[i]);
            if (index >= 0) {
                count++;
                if (removeMarker != null) {
                    removeMarker[index] = true;
                }
            }
        }
        return count;
    }

    private int countVariables(int[] otherLiterals, final boolean[] removeMarker) {
        int count = 0;
        for (int i = 0; i < otherLiterals.length; i++) {
            final int index = indexOfVariable(otherLiterals[i]);
            if (index >= 0) {
                count++;
                if (removeMarker != null) {
                    removeMarker[index] = true;
                }
            }
        }
        return count;
    }

    public boolean hasDuplicates(LiteralList variables) {
        final int[] otherLiterals = variables.getLiterals();
        for (int i = 0; i < otherLiterals.length; i++) {
            if (indexOfLiteral(otherLiterals[i]) >= 0) {
                return true;
            }
        }
        return false;
    }

    public int countDuplicates(LiteralList variables) {
        return countDuplicates(variables.literals, null);
    }

    public boolean hasConflicts(LiteralList literals) {
        return hasConflicts(literals.getLiterals());
    }

    public boolean hasConflicts(final int[] otherLiterals) {
        for (int i = 0; i < otherLiterals.length; i++) {
            if (indexOfLiteral(-otherLiterals[i]) >= 0) {
                return true;
            }
        }
        return false;
    }

    public int countConflicts(LiteralList variables) {
        final int[] otherLiterals = variables.getLiterals();
        int count = 0;
        for (int i = 0; i < otherLiterals.length; i++) {
            if (indexOfLiteral(-otherLiterals[i]) >= 0) {
                count++;
            }
        }
        return count;
    }

    
    public LiteralList negate() {
        final int[] negLiterals = new int[literals.length];
        switch (order) {
            case INDEX:
            case UNORDERED:
                for (int i = 0; i < negLiterals.length; i++) {
                    negLiterals[i] = -literals[i];
                }
                break;
            case NATURAL:
                final int highestIndex = negLiterals.length - 1;
                for (int i = 0; i < negLiterals.length; i++) {
                    negLiterals[highestIndex - i] = -literals[i];
                }
                break;
        }
        return new LiteralList(negLiterals, order, false);
    }

    public LiteralList getPositiveLiterals() {
        final int countPositive = countPositive();
        final int[] positiveLiterals;
        switch (order) {
            case INDEX:
            case UNORDERED:
                positiveLiterals = new int[countPositive];
                int i = 0;
                for (final int literal : literals) {
                    if (literal > 0) {
                        positiveLiterals[i++] = literal;
                    }
                }
                break;
            case NATURAL:
                positiveLiterals = Arrays.copyOfRange(literals, literals.length - countPositive, literals.length);
                break;
            default:
                throw new AssertionError(order);
        }
        return new LiteralList(positiveLiterals, order, false);
    }

    public LiteralList getNegativeLiterals() {
        final int countNegative = countNegative();
        final int[] negativeLiterals;
        switch (order) {
            case INDEX:
            case UNORDERED:
                negativeLiterals = new int[countNegative];
                int i = 0;
                for (final int literal : literals) {
                    if (literal < 0) {
                        negativeLiterals[i++] = literal;
                    }
                }
                break;
            case NATURAL:
                negativeLiterals = Arrays.copyOfRange(literals, 0, countNegative);
                break;
            default:
                throw new AssertionError(order);
        }
        return new LiteralList(negativeLiterals, order, false);
    }

    public Optional<LiteralList> clean() {
        final LinkedHashSet<Integer> newLiteralSet = new LinkedHashSet<>();

        for (final int literal : literals) {
            if (newLiteralSet.contains(-literal)) {
                return Optional.empty();
            } else {
                newLiteralSet.add(literal);
            }
        }

        final int[] uniqueVarArray;
        if (newLiteralSet.size() == literals.length) {
            uniqueVarArray = Arrays.copyOf(literals, literals.length);
        } else {
            uniqueVarArray = new int[newLiteralSet.size()];
            int i = 0;
            for (final int lit : newLiteralSet) {
                uniqueVarArray[i++] = lit;
            }
        }
        return Optional.of(new LiteralList(uniqueVarArray, order, false));
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        return Arrays.equals(literals, ((LiteralList) obj).literals);
    }

    @Override
    public String toString() {
        return "Clause " + toLiteralString();
    }

    @Override
    public LiteralList clone() {
        return new LiteralList(this);
    }

    public boolean isEmpty() {
        return literals.length == 0;
    }

    @Override
    public int compareTo(LiteralList o) {
        final int lengthDiff = literals.length - o.literals.length;
        if (lengthDiff != 0) {
            return lengthDiff;
        }
        for (int i = 0; i < literals.length; i++) {
            final int diff = literals[i] - o.literals[i];
            if (diff != 0) {
                return diff;
            }
        }
        return lengthDiff;
    }

    public Result<LiteralList> adapt(VariableMap oldVariables, VariableMap newVariables) {
        final int[] oldLiterals = literals;
        final int[] newLiterals = new int[oldLiterals.length];
        for (int i = 0; i < oldLiterals.length; i++) {
            final int l = oldLiterals[i];
            final Optional<String> name = oldVariables.getVariableName(Math.abs(l));
            if (name.isPresent()) {
                final Optional<Integer> index = newVariables.getVariableIndex(name.get());
                if (index.isPresent()) {
                    newLiterals[i] = l < 0 ? -index.get() : index.get();
                } else {
                    return Result.empty(new Problem("No variable named " + name.get(), Severity.ERROR));
                }
            } else {
                return Result.empty(new Problem("No variable with index " + l, Severity.ERROR));
            }
        }
        return Result.of(new LiteralList(newLiterals, order, true));
    }

    public LiteralList adapt2(VariableMap oldVariables, VariableMap newVariables) {
        final int[] oldLiterals = literals;
        final int[] newLiterals = new int[newVariables.getVariableCount() + 1];
        for (int i = 0; i < oldLiterals.length; i++) {
            final int l = oldLiterals[i];
            final Optional<String> name = oldVariables.getVariableName(Math.abs(l));
            if (name.isPresent()) {
                final Optional<Integer> index = newVariables.getVariableIndex(name.get());
                if (index.isPresent()) {
                    newLiterals[index.get()] = l < 0 ? -index.get() : index.get();
                }
            }
        }
        return new LiteralList(newLiterals, order, true);
    }

    public String toBinaryString() {
        final StringBuilder sb = new StringBuilder(literals.length);
        for (final int literal : literals) {
            sb.append(literal == 0 ? '?' : literal < 0 ? '0' : '1');
        }
        return sb.toString();
    }

    public String toLiteralString() {
        return Arrays.toString(literals);
    }
}
