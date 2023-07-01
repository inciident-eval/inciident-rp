
package inciident.util.job;

import java.util.function.Supplier;


public final class NullMonitor implements InternalMonitor {

    private boolean canceled = false;
    private boolean done = false;

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public void done() {
        done = true;
    }

    @Override
    public void checkCancel() throws MethodCancelException {
        if (canceled) {
            throw new MethodCancelException();
        }
    }

    @Override
    public NullMonitor subTask(int size) {
        return new NullMonitor();
    }

    @Override
    public String getTaskName() {
        return "";
    }

    @Override
    public long getRemainingWork() {
        return 0;
    }

    @Override
    public long getTotalWork() {
        return 0;
    }

    @Override
    public long getWorkDone() {
        return 0;
    }

    @Override
    public double getRelativeWorkDone() {
        return 0;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void setTotalWork(long work) {}

    @Override
    public void step() throws MethodCancelException {}

    @Override
    public void step(long work) throws MethodCancelException {}

    @Override
    public void uncertainStep() throws MethodCancelException {}

    @Override
    public void uncertainStep(long work) throws MethodCancelException {}

    @Override
    public void setTaskName(String name) {}

    @Override
    public void setStatusReporter(Supplier<String> reporter) {}

    @Override
    public String reportStatus() {
        return "";
    }
}
