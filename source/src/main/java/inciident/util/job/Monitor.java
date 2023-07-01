
package inciident.util.job;

import java.util.function.Supplier;


public interface Monitor {

    long getTotalWork();

    long getRemainingWork();

    long getWorkDone();

    double getRelativeWorkDone();

    String getTaskName();

    void cancel();

    boolean isCanceled();

    boolean isDone();

    void setStatusReporter(Supplier<String> reporter);

    String reportStatus();
}
