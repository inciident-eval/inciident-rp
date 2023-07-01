
package inciident.util.logging;

import java.util.Arrays;


public final class Progress {

    private static final int slidingWindowSize = 60;
    private static char[] runningIndicator =
            new char[] {'\u28F7', '\u28EF', '\u28DF', '\u287F', '\u28BF', '\u28FB', '\u28FD', '\u28FE'};

    private final char[] progressString = new char[44];

    private long startTime;
    private long[] timeSlidingWindow = new long[slidingWindowSize];
    private double[] workSlidingWindow = new double[slidingWindowSize];

    private int index = 0;
    private int runningIndex = 0;
    private int slidingWindowIndex = 0;

    public Progress() {
        startTime = System.currentTimeMillis() / 1000L;
        Arrays.fill(timeSlidingWindow, startTime);
    }

    public String toBlankString() {
        index = 0;
        fill('\r');
        fill(' ', progressString.length - 1);
        return new String(progressString) + '\r';
    }

    public synchronized String toString(double relativeProgress) {
        if (relativeProgress < 0) {
            relativeProgress = 0;
        } else if (relativeProgress > 1) {
            relativeProgress = 1;
        }
        long curTime = System.currentTimeMillis() / 1000L;

        index = 0;
        fill("\r ");
        fill(runningIndicator[runningIndex]);
        runningIndex = (runningIndex + 1) % runningIndicator.length;
        fill(' ');
        fill(String.format("%5.1f", ((Math.floor(relativeProgress * 1000)) / 10.0)));
        fill(" % | ");
        {
            long curTimeDiff = curTime - startTime;
            int day = (int) (curTimeDiff / 86400L);
            if (day > 99) {
                fill(" > 99 days ");
            } else {
                int sec = (int) (curTimeDiff % 60L);
                int min = (int) ((curTimeDiff / 60L) % 60L);
                int hour = (int) ((curTimeDiff / 3600L) % 24L);
                fill(String.format("%02d %02d:%02d:%02d", day, hour, min, sec));
            }
        }
        fill(" | ETR: ");

        if (relativeProgress == 0) {
            fill("N/A        ");
        } else {
            double curWork = relativeProgress;
            timeSlidingWindow[slidingWindowIndex] = curTime;
            workSlidingWindow[slidingWindowIndex] = curWork;

            long average = 0;
            long count = 0;
            for (int i = 1; i < slidingWindowSize; i++) {
                int j = (slidingWindowIndex + i) % slidingWindowSize;
                long timeDiff = curTime - timeSlidingWindow[j];
                double workDiff = curWork - workSlidingWindow[j];
                if (timeDiff > 0 && workDiff > 0) {
                    average += (long) ((timeDiff * (1 - relativeProgress)) / workDiff);
                    count++;
                }
            }

            slidingWindowIndex = (slidingWindowIndex + 1) % slidingWindowSize;

            if (count == 0) {
                fill("N/A        ");
            } else {
                average /= count;

                int day = (int) (average / 86400L);
                if (day > 99) {
                    fill(" > 99 days ");
                } else {
                    int sec = (int) (average % 60L);
                    int min = (int) ((average / 60L) % 60L);
                    int hour = (int) ((average / 3600L) % 24L);
                    fill(String.format("%02d %02d:%02d:%02d", day, hour, min, sec));
                }
            }
        }
        return new String(progressString);
    }

    private void fill(char c, int length) {
        Arrays.fill(progressString, index, index + length, c);
        index += length;
    }

    private void fill(char c) {
        progressString[index++] = c;
    }

    private void fill(String string) {
        final char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            progressString[i + index] = charArray[i];
        }
        index += charArray.length;
    }
}
