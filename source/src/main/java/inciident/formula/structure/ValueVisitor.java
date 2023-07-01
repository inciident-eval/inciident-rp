
package inciident.formula.structure;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import inciident.formula.structure.atomic.Assignment;
import inciident.formula.structure.atomic.literal.VariableMap.Variable;
import inciident.util.tree.visitor.TreeVisitor;

public class ValueVisitor implements TreeVisitor<Object, Formula> {

    public enum UnknownVariableHandling {
        ERROR,
        FALSE,
        TRUE,
        NULL
    }

    private final LinkedList<Object> values = new LinkedList<>();

    private UnknownVariableHandling unknownVariableHandling = UnknownVariableHandling.FALSE;

    private final Assignment assignment;
    private Boolean defaultBooleanValue;

    public ValueVisitor(Assignment assignment) {
        this.assignment = assignment;
    }

    public UnknownVariableHandling getUnknown() {
        return unknownVariableHandling;
    }

    public void setUnknown(UnknownVariableHandling unknown) {
        unknownVariableHandling = unknown;
    }

    public Boolean getDefaultBooleanValue() {
        return defaultBooleanValue;
    }

    public void setDefaultBooleanValue(Boolean defaultBooleanValue) {
        this.defaultBooleanValue = defaultBooleanValue;
    }

    @Override
    public void reset() {
        values.clear();
    }

    @Override
    public Optional<Object> getResult() {
        return Optional.ofNullable(values.peek());
    }

    @Override
    public VisitorResult lastVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Variable) {
            final Variable variable = (Variable) node;
            final int index = variable.getIndex();
            if (index <= 0) {
                switch (unknownVariableHandling) {
                    case ERROR:
                        throw new IllegalArgumentException(variable.getName());
                    case NULL:
                        values.push(null);
                        break;
                    case FALSE:
                        values.push(Boolean.FALSE);
                        break;
                    case TRUE:
                        values.push(Boolean.TRUE);
                        break;
                    default:
                        throw new IllegalStateException(String.valueOf(unknownVariableHandling));
                }
            } else {
                final Object value = assignment.get(index).orElse(null);
                if (value != null) {
                    if (variable.getType().isInstance(value)) {
                        values.push(value);
                    } else {
                        throw new IllegalArgumentException(String.valueOf(value));
                    }
                } else {
                    if (variable.getType() == Boolean.class) {
                        values.push(defaultBooleanValue);
                    } else {
                        values.push(null);
                    }
                }
            }
        } else {
            final List<Object> arguments = values.subList(0, node.getChildren().size());
            Collections.reverse(arguments);
            final Object value = node.eval(arguments);
            arguments.clear();
            values.push(value);
        }
        return VisitorResult.Continue;
    }
}
