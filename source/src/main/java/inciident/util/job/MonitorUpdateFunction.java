
package inciident.util.job;

import inciident.util.logging.Logger;


public final class MonitorUpdateFunction implements UpdateFunction {

    private final Monitor monitor;

    public MonitorUpdateFunction(Monitor monitor) {
        this.monitor = monitor;
        Logger.startProgressEstimation();
    }

    @Override
    public boolean update() {
        if (monitor.isCanceled() || monitor.isDone()) {
            Logger.stopProgressEstimation();
            return false;
        } else {
            Logger.showProgress(monitor);
            return true;
        }
    }

    public Monitor getMonitor() {
        return monitor;
    }
}
