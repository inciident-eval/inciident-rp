
package inciident.util.cli;

import java.util.List;
import java.util.ListIterator;

import inciident.util.data.Result;
import inciident.util.extension.Extension;


public abstract class AlgorithmWrapper<T> implements Extension {

    public Result<T> parseArguments(List<String> args) {
        final T algorithm = createAlgorithm();
        try {
            for (final ListIterator<String> iterator = args.listIterator(); iterator.hasNext(); ) {
                final String arg = iterator.next();
                if (!parseArgument(algorithm, arg, iterator)) {
                    throw new IllegalArgumentException("Unknown argument " + arg);
                }
            }
            return Result.of(algorithm);
        } catch (final Exception e) {
            return Result.empty(e);
        }
    }

    protected abstract T createAlgorithm();

    protected boolean parseArgument(T algorithm, String arg, ListIterator<String> iterator)
            throws IllegalArgumentException {
        return false;
    }

    public Object parseResult(Object result, Object arg) {
        return result;
    }

    public abstract String getName();

    public abstract String getHelp();
}
