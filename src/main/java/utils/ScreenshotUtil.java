package utils;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;

/**
 * Captures screenshots as byte arrays and attaches them to Allure safely.
 */
public final class ScreenshotUtil {

    private ScreenshotUtil() {
    }

    public static byte[] captureAsBytes() {
        return captureAsBytes(DriverFactory.getDriverOrNull());
    }

    public static byte[] captureAsBytes(AppiumDriver driver) {
        if (driver == null) {
            LogHelper.warn("Cannot capture screenshot: driver is null");
            return new byte[0];
        }
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            LogHelper.error("Screenshot capture failed: " + e.getMessage(), e);
            return new byte[0];
        }
    }

    /**
     * Captures a screenshot and attaches it to Allure.
     * No-ops safely when the driver is null or capture fails.
     */
    public static void attachToAllure(String name) {
        try {
            byte[] bytes = captureAsBytes();
            if (bytes.length == 0) {
                LogHelper.warn("Skipping Allure screenshot attachment '" + name + "': no image data");
                return;
            }
            Allure.addAttachment(name, new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            LogHelper.warn("Could not attach screenshot '" + name + "' to Allure: " + e.getMessage());
        }
    }
}
