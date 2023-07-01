
package inciident.util.job;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;


public class DefaultMonitor implements InternalMonitor {

    protected final List<DefaultMonitor> children = new CopyOnWriteArrayList<>();
    protected final DefaultMonitor parent;
    protected final int parentWork;

    protected String taskName;
    protected Supplier<String> statusReporter;

    protected boolean canceled, done;
    protected long currentWork;
    protected long totalWork;

    public DefaultMonitor() {
        parent = null;
        parentWork = 0;
    }

    private DefaultMonitor(DefaultMonitor parent, int parentWork) {
        this.parent = parent;
        canceled = parent.canceled;
        done = parent.done;
        this.parentWork = parentWork;
    }

    protected void uncertainWorked(long work) {
        synchronized (this) {
            currentWork += work;
            totalWork += work;
        }
    }

    protected void worked(long work) {
        synchronized (this) {
            currentWork += work;
        }
    }

    @Override
    public final void uncertainStep() throws MethodCancelException {
        uncertainWorked(1);
        checkCancel();
    }

    @Override
    public final void uncertainStep(long work) throws MethodCancelException {
        uncertainWorked(work);
        checkCancel();
    }

    @Override
    public final void step() throws MethodCancelException {
        worked(1);
        checkCancel();
    }

    @Override
    public final void step(long work) throws MethodCancelException {
        worked(work);
        checkCancel();
    }

    @Override
    public final void setTotalWork(long work) {
        totalWork = work;
        checkCancel();
    }

    @Override
    public void done() {
        currentWork = totalWork;
        done = true;
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
    public void cancel() {
        canceled = true;
    }

    @Override
    public void checkCancel() throws MethodCancelException {
        if (canceled) {
            throw new MethodCancelException();
        }
        if (parent != null) {
            parent.checkCancel();
        }
    }

    @Override
    public long getTotalWork() {
        return totalWork;
    }

    @Override
    public long getRemainingWork() {
        return totalWork - getWorkDone();
    }

    @Override
    public long getWorkDone() {
        long workDone = currentWork;
        for (final DefaultMonitor child : children) {
            workDone += child.getRelativeWorkDone() * child.parentWork;
        }
        return workDone;
    }

    @Override
    public double getRelativeWorkDone() {
        if (totalWork == 0) {
            return 0;
        }
        double workDone = currentWork;
        for (final DefaultMonitor child : children) {
            workDone += child.getRelativeWorkDone() * child.parentWork;
        }
        return workDone / totalWork;
    }

    @Override
    public void setTaskName(String name) {
        taskName = name;
    }

    @Override
    public String getTaskName() {
        return String.valueOf(taskName);
    }

    @Override
    public DefaultMonitor subTask(int size) {
        final DefaultMonitor child = new DefaultMonitor(this, size);
        children.add(child);
        return child;
    }

    @Override
    public void setStatusReporter(Supplier<String> reporter) {
        statusReporter = reporter;
    }

    @Override
    public String reportStatus() {
        return statusReporter == null ? "" : statusReporter.get();
    }
}
