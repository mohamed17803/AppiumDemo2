package Pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.Actions;
import utils.Finder;


public class FormsPage {

    // Global Appium AndroidDriver instance to interact with the mobile elements
    private final AndroidDriver driver;


    public FormsPage(AndroidDriver driver) {
        this.driver = driver;
    }


    // LOCATORS (UI Elements Identifiers)

    // Accessibility IDs are highly recommended for Android/iOS testing as they are unique and fast.
    private final By ViewsBtn = AppiumBy.accessibilityId("Views");
    private final By ControlsBtn = AppiumBy.accessibilityId("Controls");
    private final By LightThemeBtn = AppiumBy.accessibilityId("1. Light Theme");
    private final By Checkbox1 = AppiumBy.accessibilityId("Checkbox 1");
    private final By Checkbox2 = AppiumBy.accessibilityId("Checkbox 2");

    // Using ClassName locator for the EditText field since it's the standard Android widget for input.
    private final By TextField = AppiumBy.className("android.widget.EditText");


    // ACTIONS (User Interactions Methods)



    @Step("Open Views screen")
    public void clickViewsBtn() {
        Actions.click(ViewsBtn, driver);
    }


    @Step("Open Controls screen")
    public void clickControlsBtn() {
        Actions.click(ControlsBtn, driver);
    }


    @Step("Open Light Theme controls")
    public void clickLightThemeBtn() {
        Actions.click(LightThemeBtn, driver);
    }


    @Step("Focus the text field")
    public void TextFieldValue() {
        Actions.click(TextField, driver);
    }


    @Step("Type text field value: {0}")
    public void TypeTextValue(String Sample) {
        // CRITICAL FIX: We clear the input field first to completely wipe out the background "hint text"
        driver.findElement(TextField).clear();

        // Type the dynamic data passed from the JSON file into the field
        Actions.type(TextField, driver, Sample);

        // BEST PRACTICE: Hide the keyboard after typing.
        // In Android, failing to hide the keyboard might block other elements like checkboxes.
        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            // Wrapped in a try-catch block to prevent the test from crashing if the keyboard hides itself automatically.
        }
    }


    @Step("Select Checkbox 1")
    public void clickCheckbox1() {
        Actions.click(Checkbox1, driver);
    }



    @Step("Select Checkbox 2")
    public void clickCheckbox2() {
        Actions.click(Checkbox2, driver);
    }


    // ASSERTION & VERIFICATION METHODS



    @Step("Verify Checkbox 1 is checked")
    public boolean isCheckbox1Checked() {
        // Fetching the "checked" attribute dynamically from the element (returns "true" or "false" as String)
        String isChecked = Finder.elementVisibility(Checkbox1, driver).getAttribute("checked");
        // Parsing the String attribute to a primitive boolean value
        return Boolean.parseBoolean(isChecked);
    }


    @Step("Verify Checkbox 2 is checked")
    public boolean isCheckbox2Checked() {
        // Fetching the "checked" attribute dynamically from the element
        String isChecked = Finder.elementVisibility(Checkbox2, driver).getAttribute("checked");
        // Parsing the String attribute to a primitive boolean value
        return Boolean.parseBoolean(isChecked);
    }


    @Step("Verify text field value")
    public void verifyTextFieldText(String expectedSample) {

        String actualTextOnScreen = Finder.elementVisibility(TextField, driver).getAttribute("text");

        // Execute the hard assertion with an explicit descriptive failure message
        Assert.assertEquals(actualTextOnScreen, expectedSample,
                "Error: The text displayed on the screen [" + actualTextOnScreen + "] " +
                        "does not match the expected text from JSON [" + expectedSample + "]!");
    }
}
