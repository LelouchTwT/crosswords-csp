package org.lelouchtwt.crossword.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingUtils {
    public static void configureLogger(Logger logger, boolean debug) {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(debug ? Level.FINE : Level.INFO);
        logger.addHandler(handler);
        logger.setLevel(debug ? Level.FINE : Level.INFO);
        logger.setUseParentHandlers(false);
    }
}