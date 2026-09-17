package utils;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * High-level mobile interactions with retry and cross-platform smart scroll.
 */
public final class Actions {

    private Actions() {
    }

    public static void type(final By locator, final WebDriver driver, final String... keys) {
        int attempts = ConfigReader.getInt("maxRetries");
        Exception last = null;
        for (int i = 1; i <= attempts; i++) {
            try {
                Finder.elementVisibility(locator, driver).sendKeys(keys);
                return;
            } catch (Exception e) {
                last = e;
                LogHelper.warn("type() attempt " + i + "/" + attempts + " failed: " + e.getMessage());
            }
        }
        throw new RuntimeException("Failed to type into element after " + attempts + " attempts: " + locator, last);
    }

    public static void click(final By locator, final WebDriver driver) {
        int attempts = ConfigReader.getInt("maxRetries");
        Exception last = null;
        for (int i = 1; i <= attempts; i++) {
            try {
                Finder.elementVisibility(locator, driver).click();
                return;
            } catch (Exception e) {
                last = e;
                LogHelper.warn("click() attempt " + i + "/" + attempts + " failed: " + e.getMessage());
            }
        }
        throw new RuntimeException("Failed to click element after " + attempts + " attempts: " + locator, last);
    }

    public static String getText(final By locator, final WebDriver driver) {
        try {
            return Finder.elementVisibility(locator, driver).getText();
        } catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            return Finder.elementPresence(locator, driver).getText();
        }
    }

    public static boolean isDisplayed(final By locator, final WebDriver driver) {
        return Finder.elementVisibility(locator, driver).isDisplayed();
    }

    /**
     * @deprecated Use {@link #smartScrollToElement(WebDriver, By)} for cross-platform scroll.
     */
    @Deprecated
    public static void scrollToText(final WebDriver driver, final String text) {
        Finder.elementPresence(AppiumBy.androidUIAutomator(String.format(
                        "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"%s\"));",
                        text)),
                driver);
    }

    /**
     * Cross-platform scroll until the target locator is visible.
     * Android: UiScrollable forward scan then reverse scan.
     * iOS: NSPredicate / XCUITest auto-scroll to visible.
     */
    public static WebElement smartScrollToElement(final WebDriver driver, final By locator) {
        if (Finder.isPresentQuickly(locator, driver, 2)) {
            return Finder.elementVisibility(locator, driver);
        }

        AppiumDriver appiumDriver = Finder.asAppium(driver);
        if (appiumDriver instanceof AndroidDriver) {
            return smartScrollAndroid(driver, locator);
        }
        if (appiumDriver instanceof IOSDriver) {
            return smartScrollIos(driver, locator);
        }
        throw new UnsupportedOperationException("smartScrollToElement unsupported for: " + appiumDriver.getClass());
    }

    private static WebElement smartScrollAndroid(WebDriver driver, By locator) {
        // Forward (down) coverage
        try {
            Finder.elementPresence(AppiumBy.androidUIAutomator(
                            "new UiScrollable(new UiSelector().scrollable(true)).setAsVerticalList()"
                                    + ".scrollForward()"),
                    driver);
        } catch (Exception ignored) {
            // may already be at edge
        }
        if (Finder.isPresentQuickly(locator, driver, 2)) {
            return Finder.elementVisibility(locator, driver);
        }

        // Full-page forward sweep via scrollIntoView when locator is text-based is not always possible;
        // perform bounded forward then reverse scans with swipe fallbacks.
        // Swipe UP to reveal content below; then reverse (swipe DOWN) for full coverage.
        for (int i = 0; i < 8; i++) {
            if (Finder.isPresentQuickly(locator, driver, 1)) {
                return Finder.elementVisibility(locator, driver);
            }
            W3CTouches.swipe(driver, W3CTouches.Direction.UP, 0.6);
        }
        for (int i = 0; i < 10; i++) {
            if (Finder.isPresentQuickly(locator, driver, 1)) {
                return Finder.elementVisibility(locator, driver);
            }
            W3CTouches.swipe(driver, W3CTouches.Direction.DOWN, 0.6);
        }
        return Finder.elementVisibility(locator, driver);
    }

    private static WebElement smartScrollIos(WebDriver driver, By locator) {
        // XCUITest often auto-scrolls; if not visible, use predicate-based lookup + swipe scan.
        if (locator instanceof AppiumBy) {
            try {
                return Finder.elementVisibility(locator, driver);
            } catch (Exception ignored) {
                // fall through to swipe scan
            }
        }
        for (int i = 0; i < 8; i++) {
            if (Finder.isPresentQuickly(locator, driver, 1)) {
                return Finder.elementVisibility(locator, driver);
            }
            W3CTouches.swipe(driver, W3CTouches.Direction.UP, 0.55);
        }
        for (int i = 0; i < 10; i++) {
            if (Finder.isPresentQuickly(locator, driver, 1)) {
                return Finder.elementVisibility(locator, driver);
            }
            W3CTouches.swipe(driver, W3CTouches.Direction.DOWN, 0.55);
        }
        // Final attempt: iOS class chain / predicate visibility
        return Finder.elementVisibility(locator, driver);
    }

    /**
     * Convenience: scroll until text is visible (Android UiScrollable + iOS predicate).
     */
    public static WebElement smartScrollToText(final WebDriver driver, final String text) {
        AppiumDriver appiumDriver = Finder.asAppium(driver);
        if (appiumDriver instanceof AndroidDriver) {
            try {
                Finder.elementPresence(AppiumBy.androidUIAutomator(String.format(
                                "new UiScrollable(new UiSelector().scrollable(true)).setAsVerticalList()"
                                        + ".scrollIntoView(new UiSelector().text(\"%s\"))",
                                text)),
                        driver);
                return Finder.elementVisibility(AppiumBy.androidUIAutomator(
                        String.format("new UiSelector().text(\"%s\")", text)), driver);
            } catch (Exception e) {
                // Reverse scan
                try {
                    Finder.elementPresence(AppiumBy.androidUIAutomator(String.format(
                                    "new UiScrollable(new UiSelector().scrollable(true)).setAsVerticalList()"
                                            + ".scrollBackward().scrollIntoView(new UiSelector().text(\"%s\"))",
                                    text)),
                            driver);
                } catch (Exception ignored) {
                    // fall through
                }
                return smartScrollToElement(driver, AppiumBy.androidUIAutomator(
                        String.format("new UiSelector().text(\"%s\")", text)));
            }
        }
        if (appiumDriver instanceof IOSDriver) {
            By predicate = AppiumBy.iOSNsPredicateString(
                    String.format("name == '%s' OR label == '%s' OR value == '%s'", text, text, text));
            return smartScrollToElement(driver, predicate);
        }
        throw new UnsupportedOperationException("smartScrollToText unsupported driver: " + appiumDriver.getClass());
    }
}
