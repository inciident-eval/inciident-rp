
package inciident.util.data;

import java.util.Map;
import java.util.function.BiFunction;

import inciident.util.logging.Logger;


public abstract class Operation {

    protected abstract Map<Identifier<?>, BiFunction<?, ?, ?>> getImplementations();

    @SuppressWarnings("unchecked")
    public final <T> T apply(Identifier<T> identifier, Object parameters, Object element) {
        try {
            final BiFunction<T, Object, T> op4Rep =
                    (BiFunction<T, Object, T>) getImplementations().get(identifier);
            return (op4Rep != null) ? op4Rep.apply((T) element, parameters) : null;
        } catch (final ClassCastException e) {
            Logger.logError(e);
            return null;
        }
    }
}
