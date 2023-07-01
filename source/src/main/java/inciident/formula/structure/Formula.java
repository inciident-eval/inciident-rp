
package inciident.formula.structure;

import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

import inciident.formula.structure.atomic.literal.NamedTermMap.ValueTerm;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.util.tree.Trees;
import inciident.util.tree.structure.Tree;


public interface Formula extends Tree<Formula> {
    String getName();

    Class<?> getType();

    default Optional<VariableMap> getVariableMap() {
        return Trees.preOrderStream(this)
                .filter(n -> n instanceof ValueTerm)
                .map(n -> ((ValueTerm) n).getMap())
                .findAny();
    }

    @Override
    List<? extends Formula> getChildren();

    @Override
    Formula cloneNode();

    Object eval(List<?> values);

    public static boolean checkValues(int size, List<?> values) {
        return values.size() == size;
    }

    public static boolean checkValues(Class<?> type, List<?> values) {
        return values.stream().allMatch(v -> v == null || type.isInstance(v));
    }

    @SuppressWarnings("unchecked")
    public static <T> T reduce(List<?> values, final BinaryOperator<T> binaryOperator) {
        if (values.stream().anyMatch(value -> value == null)) {
            return null;
        }
        return values.stream().map(l -> (T) l).reduce(binaryOperator).orElse(null);
    }
}
