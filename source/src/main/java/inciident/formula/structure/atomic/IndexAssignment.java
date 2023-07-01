
package inciident.formula.structure.atomic;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import inciident.util.data.Pair;

public class IndexAssignment implements Assignment {

    protected final HashMap<Integer, Object> assignments = new HashMap<>();

    @Override
    public void set(int index, Object assignment) {
        if (index > 0) {
            if (assignment == null) {
                assignments.remove(index);
            } else {
                assignments.put(index, assignment);
            }
        }
    }

    @Override
    public Optional<Object> get(int index) {
        return Optional.ofNullable(assignments.get(index));
    }

    @Override
    public List<Pair<Integer, Object>> getAll() {
        return assignments.entrySet().stream()
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public void unsetAll() {
        assignments.clear();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<Integer, Object> entry : assignments.entrySet()) {
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }
}
