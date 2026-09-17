package Pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.Finder;

/**
 * Fluent Page Object for ApiDemos Views → Controls → Light Theme flow.
 */
public class FormsPage extends BasePage {

    private final By viewsBtn = AppiumBy.accessibilityId("Views");
    private final By controlsBtn = AppiumBy.accessibilityId("Controls");
    private final By lightThemeBtn = AppiumBy.accessibilityId("1. Light Theme");
    private final By checkbox1 = AppiumBy.accessibilityId("Checkbox 1");
    private final By checkbox2 = AppiumBy.accessibilityId("Checkbox 2");
    private final By textField = AppiumBy.className("android.widget.EditText");

    public FormsPage(AppiumDriver driver) {
        super(driver);
    }

    @Step("Open Views screen")
    public FormsPage clickViewsBtn() {
        click(viewsBtn);
        return this;
    }

    @Step("Open Controls screen")
    public FormsPage clickControlsBtn() {
        click(controlsBtn);
        return this;
    }

    @Step("Open Light Theme controls")
    public FormsPage clickLightThemeBtn() {
        click(lightThemeBtn);
        return this;
    }

    @Step("Focus the text field")
    public FormsPage focusTextField() {
        click(textField);
        return this;
    }

    /**
     * @deprecated Use {@link #focusTextField()} — kept for backward compatibility.
     */
    @Deprecated
    public FormsPage TextFieldValue() {
        return focusTextField();
    }

    @Step("Type text field value")
    public FormsPage typeTextValue(String sample) {
        Finder.elementVisibility(textField, driver).clear();
        type(textField, sample);
        try {
            if (driver instanceof io.appium.java_client.android.AndroidDriver androidDriver) {
                androidDriver.hideKeyboard();
            }
        } catch (Exception e) {
            // Keyboard may already be hidden on some devices/emulators.
        }
        return this;
    }

    /**
     * @deprecated Use {@link #typeTextValue(String)} — kept for backward compatibility.
     */
    @Deprecated
    public FormsPage TypeTextValue(String sample) {
        return typeTextValue(sample);
    }

    @Step("Select Checkbox 1")
    public FormsPage clickCheckbox1() {
        click(checkbox1);
        return this;
    }

    @Step("Select Checkbox 2")
    public FormsPage clickCheckbox2() {
        click(checkbox2);
        return this;
    }

    @Step("Verify Checkbox 1 is checked")
    public boolean isCheckbox1Checked() {
        String isChecked = Finder.elementVisibility(checkbox1, driver).getAttribute("checked");
        return Boolean.parseBoolean(isChecked);
    }

    @Step("Verify Checkbox 2 is checked")
    public boolean isCheckbox2Checked() {
        String isChecked = Finder.elementVisibility(checkbox2, driver).getAttribute("checked");
        return Boolean.parseBoolean(isChecked);
    }

    @Step("Verify text field value")
    public FormsPage verifyTextFieldText(String expectedSample) {
        String actualTextOnScreen = Finder.elementVisibility(textField, driver).getAttribute("text");
        Assert.assertEquals(actualTextOnScreen, expectedSample,
                "Error: The text displayed on the screen [" + actualTextOnScreen + "] "
                        + "does not match the expected text from JSON [" + expectedSample + "]!");
        return this;
    }
}
