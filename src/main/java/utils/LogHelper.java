package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thin Log4j2 wrapper for consistent framework logging.
 */
public final class LogHelper {

    private static final Logger LOG = LogManager.getLogger("AppiumFramework");

    private LogHelper() {
    }

    public static void info(String message) {
        LOG.info(message);
    }

    public static void warn(String message) {
        LOG.warn(message);
    }

    public static void error(String message) {
        LOG.error(message);
    }

    public static void error(String message, Throwable throwable) {
        LOG.error(message, throwable);
    }

    public static void debug(String message) {
        LOG.debug(message);
    }
}
