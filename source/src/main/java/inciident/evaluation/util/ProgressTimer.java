
package inciident.evaluation.util;

import inciident.util.logging.Logger;


public class ProgressTimer {

    private boolean running = false;
    private boolean verbose = true;

    private long startTime;

    private long curTime = 0;

    private long lastTime = -1;

    private static long getTime() {
        return System.nanoTime();
    }

    public void start() {
        if (!running) {
            startTime = getTime();
            curTime = startTime;
            running = true;
        }
    }

    public long stop() {
        if (running) {
            lastTime = getTime() - startTime;

            printTime();

            running = false;
        }
        return lastTime;
    }

    public long split() {
        final long startTime = curTime;
        curTime = getTime();

        lastTime = curTime - startTime;

        printTime();

        return lastTime;
    }

    private void printTime() {
        if (verbose) {
            final double timeDiff = (lastTime / 1_0000_00L) / 1_000.0;
            Logger.logInfo("Time: " + timeDiff + "s");
        }
    }

    public final boolean isRunning() {
        return running;
    }

    public long getLastTime() {
        return lastTime;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
