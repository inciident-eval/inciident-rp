
package inciident.util.data;

import java.nio.file.Path;
import java.util.function.BiFunction;

import inciident.util.io.IO;
import inciident.util.io.format.Format;
import inciident.util.io.format.FormatSupplier;
import inciident.util.job.Executor;
import inciident.util.job.InternalMonitor;
import inciident.util.job.MonitorableFunction;


public interface Provider<T> extends BiFunction<Cache, InternalMonitor, Result<T>> {

    Object defaultParameters = new Object();

    Identifier<T> getIdentifier();

    default Object getParameters() {
        return defaultParameters;
    }

    default boolean storeInCache() {
        return true;
    }

    static <T, R> Result<R> convert(
            Cache cache, Identifier<T> identifier, MonitorableFunction<T, R> function, InternalMonitor monitor) {
        return cache.get(identifier).flatMap(o -> Executor.run(function, o, monitor));
    }

    static <T, R> Result<R> convert(
            Cache cache, Provider<T> provider, MonitorableFunction<T, R> function, InternalMonitor monitor) {
        return cache.get(provider).flatMap(o -> Executor.run(function, o, monitor));
    }

    static <R> Result<R> load(Path path, FormatSupplier<R> formatSupplier, FactorySupplier<R> factorySupplier) {
        return IO.load(path, formatSupplier, factorySupplier);
    }

    static <R> Result<R> load(Path path, FormatSupplier<R> formatSupplier) {
        return IO.load(path, formatSupplier);
    }

    static <R> Result<R> load(Path path, Format<R> format) {
        return IO.load(path, format);
    }
}
