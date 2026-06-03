package Tests;

import Pages.FormsPage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.Platform;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.JsonReader;
import utils.PropertyReader;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Test Suite Execution Class. Handles Lifecycle setups,
 * reads properties and test data, and manages the end-to-end execution flow.
 */
public class FormsTest {

    // Global variables accessible across all lifecycle and test methods in this class
    private AndroidDriver driver;
    private AppiumDriverLocalService service;
    private PropertyReader propertyReader;

    // FIX 1: Declared jsonReader globally at the class-level so that the @Test method can resolve it perfectly.
    private JsonReader jsonReader;

    /**
     * Executed ONCE before any test starts. Used to load property files,
     * json data files, and boot up the Appium node server programmatically.
     */
    @BeforeClass
    public void setUpAppiumService() {
        // Initializing the PropertyReader to handle server IP, ports, and execution parameters
        propertyReader = new PropertyReader("D:\\Testing\\Appium\\AppiumDemo2\\src\\main\\resources\\Enviroment.properties");

        // Initializing your custom JsonReader class to process external TestData.json values
        jsonReader = new JsonReader("./src/test/resources/TestData.json");

        // Configuring and Starting the Appium Server dynamically based on properties configuration
        service = new AppiumServiceBuilder()
                .withIPAddress(propertyReader.getProperty("ipAddress"))
                .usingPort(Integer.parseInt(propertyReader.getProperty("port")))
                .withArgument(() -> "--use-drivers", propertyReader.getProperty("driver")) // Forces Appium to execute using the specified driver
                .build();

        service.start(); // Boots the Appium node server
    }

    /**
     * Executed BEFORE EACH individual test method. Initializes a fresh session
     * on the mobile device/emulator using the specified Android capabilities.
     */
    @BeforeMethod
    public void setUpDriver() throws MalformedURLException, URISyntaxException {
        // Constructing UiAutomator2 execution capabilities dynamically
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(Platform.ANDROID.name())
                .setDeviceName(propertyReader.getProperty("device"))
                .noReset() // Preserves the app state, cache, and permissions between sessions
                .setApp(System.getProperty("user.dir") + "/src/test/resources/" + propertyReader.getProperty("app"));

        // Combining IP and Port dynamically into a safely formatted URI object, then converting to URL
        String appiumUrl = "http://" + propertyReader.getProperty("ipAddress") + ":" + propertyReader.getProperty("port");
        driver = new AndroidDriver(new URI(appiumUrl).toURL(), options);
    }


    @Test
    public void validateFormsTextFieldFlow() {
        // Instantiate the page object and inject the active driver session
        FormsPage formsPage = new FormsPage(driver);

        // ------------------------------------------
        // STEP 1: Navigation Steps
        // ------------------------------------------
        formsPage.clickViewsBtn();
        formsPage.clickControlsBtn();
        formsPage.clickLightThemeBtn();

        // ------------------------------------------
        // STEP 2: Test Data Retrieval (Data-Driven Testing)
        // ------------------------------------------
        // Extracting the test payload value mapped under the "sample" key inside TestData.json
        String expectedSample = jsonReader.getJson("sample");

        // ------------------------------------------
        // STEP 3: Execution Actions (Fixing the Sequence)
        // ------------------------------------------
        // FIX 2: Instead of calling assertion immediately, we now run the actual typing operations.
        formsPage.TextFieldValue();              // 1. Click the field to place the focus cursor.
        formsPage.TypeTextValue(expectedSample); // 2. Send the exact value extracted from JSON ("Mohamed Sayed").

        // ------------------------------------------
        // STEP 4: Verification / Assertions
        // ------------------------------------------
        // Validate that the visual text currently present on the mobile UI strictly mirrors the JSON payload.
        formsPage.verifyTextFieldText(expectedSample);

        // Interact with Checkbox 1 and explicitly assert that it transitions to the 'Checked' (True) state
        formsPage.clickCheckbox1();
        Assert.assertTrue(formsPage.isCheckbox1Checked(), "Error: Checkbox 1 was clicked but failed to switch to checked!");

        // Interact with Checkbox 2 and explicitly assert that it transitions to the 'Checked' (True) state
        formsPage.clickCheckbox2();
        Assert.assertTrue(formsPage.isCheckbox2Checked(), "Error: Checkbox 2 was clicked but failed to switch to checked!");
    }


    @AfterMethod
    public void tearDownDriver() {
        // Ensure the driver instance is active before performing tear-down actions
        if (driver != null) {
            try {
                // 1. Dynamic Retrieval: Appium fetches the exact package name
                // of the currently running application on the fly.
                String currentPackage = driver.getCurrentPackage();

                // 2. Force Kill: Instructs Android to forcefully close and terminate
                // the application from both the foreground and background.
                driver.terminateApp(currentPackage);
                System.out.println("Done: Application terminated successfully.");
            } catch (Exception e) {
                // Wrapped in a try-catch block so that if the app is already closed,
                // the test suite won't crash during the teardown phase.
                System.out.println("Notice: Could not terminate app window: " + e.getMessage());
            }

            // 3. Session Closure: Gracefully closes the Appium session and releases resources.
            driver.quit();
        }
    }


    @AfterClass
    public void tearDownService() {
        if (service != null) {
            service.stop();
        }
    }
}