package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads configuration with environment-variable override support.
 * camelCase keys map to UPPER_SNAKE_CASE env vars (e.g. appiumServerUrl → APPIUM_SERVER_URL).
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException(
                        "config.properties not found on classpath (expected under src/test/resources)");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String envValue = System.getenv(toEnvKey(key));
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        return PROPERTIES.getProperty(key);
    }

    public static int getInt(String key) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing integer config key: " + key);
        }
        return Integer.parseInt(value.trim());
    }

    public static boolean getBoolean(String key) {
        String value = get(key);
        return value != null && Boolean.parseBoolean(value.trim());
    }

    /**
     * Converts camelCase / mixed keys to UPPER_SNAKE_CASE for env lookup.
     */
    static String toEnvKey(String key) {
        return key.replaceAll("([a-z])([A-Z])", "$1_$2")
                .replace('.', '_')
                .replace('-', '_')
                .toUpperCase();
    }
}
