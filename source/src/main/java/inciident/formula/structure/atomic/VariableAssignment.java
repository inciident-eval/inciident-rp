
package inciident.formula.structure.atomic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import inciident.formula.structure.atomic.literal.NamedTermMap.ValueTerm;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.formula.structure.atomic.literal.VariableMap.Variable;
import inciident.util.data.Pair;

public class VariableAssignment implements Assignment {

    protected final LinkedHashMap<Integer, Object> assignments;
    protected final VariableMap variables;

    public VariableAssignment(VariableMap variables) {
        this.variables = Objects.requireNonNull(variables);
        final int assignmentSize = variables.getVariableCount() + 1;
        assignments = new LinkedHashMap<>(assignmentSize);
    }

    @Override
    public void set(int index, Object assignment) {
        final Variable sig = variables
                .getVariableSignature(index)
                .orElseThrow(() -> new NoSuchElementException(String.valueOf(index)));
        if (assignment == null) {
            assignments.remove(index);
        } else {
            if (assignment.getClass() == sig.getType()) {
                assignments.put(index, assignment);
            } else {
                throw new ClassCastException(String.valueOf(sig.getType()));
            }
        }
    }

    public void set(String name, Object assignment) {
        final Variable sig = variables.getVariable(name).orElseThrow(() -> new NoSuchElementException(name));
        if (assignment == null) {
            assignments.remove(sig.getIndex());
        } else {
            if (assignment.getClass() == sig.getType()) {
                assignments.put(sig.getIndex(), assignment);
            } else {
                throw new ClassCastException(String.valueOf(sig.getType()));
            }
        }
    }

    @Override
    public void unsetAll() {
        assignments.clear();
    }

    @Override
    public Optional<Object> get(int index) {
        return Optional.ofNullable(assignments.get(index));
    }

    public Optional<Object> get(String name) {
        return variables.getVariable(name).map(ValueTerm::getIndex).map(assignments::get);
    }

    public List<Pair<Integer, Object>> getAll() {
        return assignments.entrySet().stream()
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public VariableMap getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<Integer, Object> entry : assignments.entrySet()) {
            sb.append(variables.getVariableName(entry.getKey()));
            sb.append(": ");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }
}
