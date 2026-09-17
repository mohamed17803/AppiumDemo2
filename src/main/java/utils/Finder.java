package utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * Config-driven FluentWait helpers for element presence and visibility.
 */
public final class Finder {

    private Finder() {
    }

    public static WebElement elementPresence(final By locator, final WebDriver driver) {
        return wait(driver).until(d -> d.findElement(locator));
    }

    public static WebElement elementVisibility(final By locator, final WebDriver driver) {
        return wait(driver).until(d -> {
            WebElement element = d.findElement(locator);
            return element.isDisplayed() ? element : null;
        });
    }

    public static boolean isVisible(final By locator, final WebDriver driver) {
        try {
            return elementVisibility(locator, driver).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private static FluentWait<AppiumDriver> wait(final WebDriver driver) {
        AppiumDriver appiumDriver = asAppium(driver);
        return new FluentWait<>(appiumDriver)
                .withTimeout(Duration.ofSeconds(ConfigReader.getInt("explicitWaitSeconds")))
                .pollingEvery(Duration.ofMillis(ConfigReader.getInt("pollingMillis")))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Short wait used by smart-scroll loops (does not use full explicit timeout).
     */
    static boolean isPresentQuickly(final By locator, final WebDriver driver, long timeoutSeconds) {
        try {
            new FluentWait<>(asAppium(driver))
                    .withTimeout(Duration.ofSeconds(timeoutSeconds))
                    .pollingEvery(Duration.ofMillis(ConfigReader.getInt("pollingMillis")))
                    .ignoring(NoSuchElementException.class)
                    .until((Function<AppiumDriver, WebElement>) d -> {
                        WebElement el = d.findElement(locator);
                        return el.isDisplayed() ? el : null;
                    });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static AppiumDriver asAppium(WebDriver driver) {
        if (driver instanceof AppiumDriver appiumDriver) {
            return appiumDriver;
        }
        throw new IllegalArgumentException("Expected AppiumDriver, got: " + driver.getClass().getName());
    }
}
