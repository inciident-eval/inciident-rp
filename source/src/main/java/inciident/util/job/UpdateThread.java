
package inciident.util.job;

import inciident.util.logging.Logger;


public class UpdateThread extends Thread {

    private static final int DEFAULT_UPDATE_TIME = 1_000;

    private final UpdateFunction function;

    protected boolean monitorRun = true;
    private long updateTime;

    public UpdateThread(UpdateFunction function) {
        this(function, DEFAULT_UPDATE_TIME);
    }

    
    public UpdateThread(UpdateFunction function, long updateTime) {
        super();
        this.function = function;
        this.updateTime = updateTime;
    }

    @Override
    public void run() {
        monitorRun = function.update();
        try {
            while (monitorRun) {
                Thread.sleep(updateTime);
                monitorRun = function.update();
            }
        } catch (final InterruptedException e) {
        } finally {
            function.update();
        }
    }

    public void finish() {
        // to ensure to stop the monitor thread
        monitorRun = false;
        interrupt();
        try {
            join();
        } catch (InterruptedException e) {
            Logger.logError(e);
        }
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
