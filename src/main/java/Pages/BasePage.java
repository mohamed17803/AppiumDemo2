package Pages;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.Actions;
import utils.Finder;
import utils.W3CTouches;

/**
 * Fluent base for Page Objects — wraps Actions/Finder and supports {@code return this} chaining.
 */
public abstract class BasePage {

    protected final AppiumDriver driver;

    protected BasePage(AppiumDriver driver) {
        this.driver = driver;
    }

    protected void click(By locator) {
        Actions.click(locator, driver);
    }

    protected void type(By locator, String... keys) {
        Actions.type(locator, driver, keys);
    }

    protected String getText(By locator) {
        return Actions.getText(locator, driver);
    }

    protected boolean isDisplayed(By locator) {
        return Actions.isDisplayed(locator, driver);
    }

    protected void smartScrollTo(By locator) {
        Actions.smartScrollToElement(driver, locator);
    }

    protected void tap(By locator) {
        W3CTouches.tap(driver, locator);
    }

    protected WebElementWait waitFor(By locator) {
        return new WebElementWait(locator);
    }

    /**
     * Small helper so subclasses can assert attribute values fluently.
     */
    protected void assertAttributeEquals(By locator, String attribute, String expected) {
        String actual = Finder.elementVisibility(locator, driver).getAttribute(attribute);
        Assert.assertEquals(actual, expected,
                "Attribute '" + attribute + "' mismatch for " + locator);
    }

    protected final class WebElementWait {
        private final By locator;

        private WebElementWait(By locator) {
            this.locator = locator;
        }

        public void visible() {
            Finder.elementVisibility(locator, driver);
        }

        public void present() {
            Finder.elementPresence(locator, driver);
        }
    }
}
