
package inciident.util.job;

import inciident.util.data.Result;
import inciident.util.job.InternalMonitor.MethodCancelException;


public final class Executor {

    private Executor() {}

    public static <T> Result<T> run(MonitorableSupplier<T> supplier) {
        return run(supplier, new DefaultMonitor());
    }

    public static <T, R> Result<R> run(MonitorableFunction<T, R> function, T input) {
        return run(function, input, new DefaultMonitor());
    }

    public static <T> Result<T> run(MonitorableSupplier<T> supplier, InternalMonitor monitor) {
        monitor = monitor != null ? monitor : new DefaultMonitor();
        try {
            return Result.of(supplier.execute(monitor));
        } catch (final Exception e) {
            return Result.empty(e);
        } finally {
            monitor.done();
        }
    }

    public static <T, R> Result<R> run(MonitorableFunction<T, R> function, T input, InternalMonitor monitor)
            throws MethodCancelException {
        monitor = monitor != null ? monitor : new DefaultMonitor();
        try {
            return Result.of(function.execute(input, monitor));
        } catch (final Exception e) {
            return Result.empty(e);
        } finally {
            monitor.done();
        }
    }
}
