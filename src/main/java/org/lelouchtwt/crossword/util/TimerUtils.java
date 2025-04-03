package org.lelouchtwt.crossword.util;

import java.util.logging.Logger;

public class TimerUtils {
    public static <T> T time(String label, TimerSupplier<T> action, Logger logger) {
        long start = System.nanoTime();
        T result = action.get();
        long end = System.nanoTime();
        double ms = (end - start) / 1_000_000.0;
        logger.info(String.format("%s executed in %.2f ms", label, ms));
        return result;
    }

    public static void time(String label, Runnable action, Logger logger) {
        long start = System.nanoTime();
        action.run();
        long end = System.nanoTime();
        double ms = (end - start) / 1_000_000.0;
        logger.info(String.format("%s executed in %.2f ms", label, ms));
    }

    @FunctionalInterface
    public interface TimerSupplier<T> {
        T get();
    }
}
