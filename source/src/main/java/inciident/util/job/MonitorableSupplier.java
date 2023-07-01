
package inciident.util.job;


@FunctionalInterface
public interface MonitorableSupplier<T> {

    T execute(InternalMonitor monitor) throws Exception;
}
