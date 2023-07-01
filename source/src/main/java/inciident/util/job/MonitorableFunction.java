
package inciident.util.job;


@FunctionalInterface
public interface MonitorableFunction<T, R> {

    R execute(T input, InternalMonitor monitor) throws Exception;
}
