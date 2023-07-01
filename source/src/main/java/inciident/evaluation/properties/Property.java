
package inciident.evaluation.properties;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import inciident.evaluation.Evaluator;

public class Property<T> {

    public static final Function<String, Boolean> BooleanConverter = Boolean::parseBoolean;
    public static final Function<String, Integer> IntegerConverter = Integer::parseInt;
    public static final Function<String, Double> DoubleConverter = Double::parseDouble;
    public static final Function<String, Long> LongConverter = Long::parseLong;
    public static final Function<String, String> StringConverter = String::toString;

    private final Function<String, T> converter;
    private final String name;
    private final T defaultValue;
    private T value;

    protected static <E> Function<String, List<E>> parseList(Function<String, E> elementConverter) {
        return valueString -> Arrays.stream(valueString.split(",")) //
                .map(elementConverter) //
                .collect(Collectors.toList());
    }

    public Property(String name, Function<String, T> converter) {
        this(name, converter, null);
    }

    public Property(String name, Function<String, T> converter, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.converter = converter;
        Evaluator.addProperty(this);
    }

    public T getValue() {
        return (value != null) ? value : defaultValue;
    }

    public String getKey() {
        return name;
    }

    protected T getDefaultValue() {
        return defaultValue;
    }

    public boolean setValue(String valueString) {
        if (valueString != null) {
            try {
                value = cast(valueString);
                return true;
            } catch (final Exception e) {
            }
        }
        return false;
    }

    protected T cast(String valueString) throws Exception {
        return converter.apply(valueString);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" = ");
        if (value != null) {
            sb.append(value.toString());
        } else if (defaultValue != null) {
            sb.append(defaultValue.toString());
            sb.append(" (default value)");
        } else {
            sb.append("null");
        }
        return sb.toString();
    }
}
