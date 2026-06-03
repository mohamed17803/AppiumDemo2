package Pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.Actions;
import utils.Finder;

/**
 * FormsPage Class implementing the Page Object Model (POM).
 * This class isolates the UI elements (Locators) and behaviors (Actions/Assertions)
 * of the Forms/Controls screen from the test logic.
 */
public class FormsPage {

    // Global Appium AndroidDriver instance to interact with the mobile elements
    private final AndroidDriver driver;

    /**
     * Constructor to pass the active driver session from the Test Class to this Page Class.
     * @param driver Active AndroidDriver instance.
     */
    public FormsPage(AndroidDriver driver) {
        this.driver = driver;
    }

    // ==========================================
    // LOCATORS (UI Elements Identifiers)
    // ==========================================

    // Accessibility IDs are highly recommended for Android/iOS testing as they are unique and fast.
    private final By ViewsBtn = AppiumBy.accessibilityId("Views");
    private final By ControlsBtn = AppiumBy.accessibilityId("Controls");
    private final By LightThemeBtn = AppiumBy.accessibilityId("1. Light Theme");
    private final By Checkbox1 = AppiumBy.accessibilityId("Checkbox 1");
    private final By Checkbox2 = AppiumBy.accessibilityId("Checkbox 2");

    // Using ClassName locator for the EditText field since it's the standard Android widget for input.
    private final By TextField = AppiumBy.className("android.widget.EditText");

    // ==========================================
    // ACTIONS (User Interactions Methods)
    // ==========================================

    /**
     * Clicks on the 'Views' option from the main menu.
     */
    public void clickViewsBtn() {
        Actions.click(ViewsBtn, driver);
    }

    /**
     * Clicks on the 'Controls' option inside the Views menu.
     */
    public void clickControlsBtn() {
        Actions.click(ControlsBtn, driver);
    }

    /**
     * Clicks on the '1. Light Theme' option to enter the form layout.
     */
    public void clickLightThemeBtn() {
        Actions.click(LightThemeBtn, driver);
    }

    /**
     * Simulates clicking on the EditText field to gain focus before typing.
     */
    public void TextFieldValue() {
        Actions.click(TextField, driver);
    }

    /**
     * Safe Typing Action: Clears the field, types the text, and handles the keyboard.
     * @param Sample The actual text data fetched dynamically from the TestData.json file.
     */
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

    /**
     * Clicks on the Checkbox 1 element to change its state.
     */
    public void clickCheckbox1() {
        Actions.click(Checkbox1, driver);
    }

    /**
     * Clicks on the Checkbox 2 element to change its state.
     */
    public void clickCheckbox2() {
        Actions.click(Checkbox2, driver);
    }

    // ==========================================
    // ASSERTION & VERIFICATION METHODS
    // ==========================================

    /**
     * Checks whether Checkbox 1 is selected or not.
     * @return true if checked, false otherwise.
     */
    public boolean isCheckbox1Checked() {
        // Fetching the "checked" attribute dynamically from the element (returns "true" or "false" as String)
        String isChecked = Finder.elementVisibility(Checkbox1, driver).getAttribute("checked");
        // Parsing the String attribute to a primitive boolean value
        return Boolean.parseBoolean(isChecked);
    }

    /**
     * Checks whether Checkbox 2 is selected or not.
     * @return true if checked, false otherwise.
     */
    public boolean isCheckbox2Checked() {
        // Fetching the "checked" attribute dynamically from the element
        String isChecked = Finder.elementVisibility(Checkbox2, driver).getAttribute("checked");
        // Parsing the String attribute to a primitive boolean value
        return Boolean.parseBoolean(isChecked);
    }

    /**
     * Advanced Verification: Validates that the input field contains the exact text written.
     * @param expectedSample The expected text value originating from the TestData.json file.
     */
    public void verifyTextFieldText(String expectedSample) {
        // CRITICAL FIX: Instead of Actions.getText() which can occasionally return the placeholder/hint,
        // we use getAttribute("text") directly on the visible element to capture what is truly rendered on screen.
        String actualTextOnScreen = Finder.elementVisibility(TextField, driver).getAttribute("text");

        // Execute the hard assertion with an explicit descriptive failure message
        Assert.assertEquals(actualTextOnScreen, expectedSample,
                "Error: The text displayed on the screen [" + actualTextOnScreen + "] " +
                        "does not match the expected text from JSON [" + expectedSample + "]!");
    }
}