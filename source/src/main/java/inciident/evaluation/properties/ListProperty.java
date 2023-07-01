
package inciident.evaluation.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ListProperty<T> extends Property<List<T>> {

    public ListProperty(String name, Function<String, T> converter) {
        super(name, parseList(converter), Collections.emptyList());
    }

    public ListProperty(String name, Function<String, T> converter, T defaultValue) {
        super(name, parseList(converter), Arrays.asList(defaultValue));
    }
}
