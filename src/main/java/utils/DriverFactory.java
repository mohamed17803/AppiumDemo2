package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.Platform;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;

/**
 * Thread-safe Appium driver lifecycle management for parallel TestNG execution.
 */
public final class DriverFactory {

    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();
    private static final Object SERVICE_LOCK = new Object();
    private static volatile AppiumDriverLocalService service;

    private DriverFactory() {
    }

    public static void initDriver() {
        startAppiumServerIfNeeded();
        String platform = Optional.ofNullable(ConfigReader.get("platformName"))
                .orElse(Platform.ANDROID.name());

        AppiumDriver driver;
        if (Platform.IOS.name().equalsIgnoreCase(platform)) {
            driver = createIosDriver();
        } else {
            driver = createAndroidDriver();
        }
        DRIVER.set(driver);
        LogHelper.info("Driver initialized for platform: " + platform);
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("Driver is not initialized. Call initDriver() first.");
        }
        return driver;
    }

    /** Returns the current thread's driver, or {@code null} if not initialized. */
    public static AppiumDriver getDriverOrNull() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
                LogHelper.info("Driver session quit successfully");
            } catch (Exception e) {
                LogHelper.warn("Error while quitting driver: " + e.getMessage());
            } finally {
                DRIVER.remove();
            }
        }
    }

    public static void stopAppiumServer() {
        synchronized (SERVICE_LOCK) {
            if (service != null && service.isRunning()) {
                service.stop();
                LogHelper.info("Local Appium server stopped");
            }
            service = null;
        }
    }

    private static AndroidDriver createAndroidDriver() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(Platform.ANDROID.name())
                .setDeviceName(ConfigReader.get("deviceName"))
                .noReset();

        String platformVersion = ConfigReader.get("platformVersion");
        if (platformVersion != null && !platformVersion.isBlank()) {
            options.setPlatformVersion(platformVersion);
        }

        String app = ConfigReader.get("app");
        if (app != null && !app.isBlank()) {
            options.setApp(resolveAppPath(app));
        }

        String appPackage = ConfigReader.get("appPackage");
        String appActivity = ConfigReader.get("appActivity");
        if (appPackage != null && !appPackage.isBlank()) {
            options.setAppPackage(appPackage);
        }
        if (appActivity != null && !appActivity.isBlank()) {
            options.setAppActivity(appActivity);
        }

        return new AndroidDriver(resolveServerUrl(), options);
    }

    private static IOSDriver createIosDriver() {
        XCUITestOptions options = new XCUITestOptions()
                .setPlatformName(Platform.IOS.name())
                .setDeviceName(ConfigReader.get("deviceName"))
                .noReset();

        String platformVersion = ConfigReader.get("platformVersion");
        if (platformVersion != null && !platformVersion.isBlank()) {
            options.setPlatformVersion(platformVersion);
        }

        String app = ConfigReader.get("app");
        if (app != null && !app.isBlank()) {
            options.setApp(resolveAppPath(app));
        }

        String bundleId = ConfigReader.get("appPackage");
        if (bundleId != null && !bundleId.isBlank()) {
            options.setBundleId(bundleId);
        }

        return new IOSDriver(resolveServerUrl(), options);
    }

    private static void startAppiumServerIfNeeded() {
        if (!ConfigReader.getBoolean("autoStartAppiumServer")) {
            LogHelper.info("autoStartAppiumServer=false — expecting an externally managed Appium server");
            return;
        }
        synchronized (SERVICE_LOCK) {
            if (service != null && service.isRunning()) {
                return;
            }
            URL serverUrl = resolveServerUrl();
            String host = serverUrl.getHost();
            int port = serverUrl.getPort() > 0 ? serverUrl.getPort() : 4723;

            service = new AppiumServiceBuilder()
                    .withIPAddress(host)
                    .usingPort(port)
                    .withTimeout(Duration.ofSeconds(60))
                    .build();
            service.start();
            LogHelper.info("Local Appium server started at " + serverUrl);
        }
    }

    private static URL resolveServerUrl() {
        try {
            return new URI(ConfigReader.get("appiumServerUrl")).toURL();
        } catch (MalformedURLException | java.net.URISyntaxException e) {
            throw new IllegalArgumentException("Invalid appiumServerUrl: " + ConfigReader.get("appiumServerUrl"), e);
        }
    }

    private static String resolveAppPath(String app) {
        Path path = Paths.get(app);
        if (path.isAbsolute()) {
            return path.toString();
        }
        return Paths.get(System.getProperty("user.dir"), "src", "test", "resources", app)
                .toAbsolutePath()
                .toString();
    }
}
