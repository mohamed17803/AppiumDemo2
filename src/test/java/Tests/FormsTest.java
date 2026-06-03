package Tests;

import Pages.FormsPage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.qameta.allure.*;
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
import java.nio.file.Paths;


@Epic("Appium Android")
@Feature("Forms Automation Feature")
public class FormsTest {

    // Preparation variables
    private AndroidDriver driver;
    private AppiumDriverLocalService service;
    private PropertyReader propertyReader;
    private JsonReader jsonReader;


    public AndroidDriver getDriver() {
        return this.driver;
    }


    @BeforeClass
    public void setUpAppiumService() {

        String propertiesPath = Paths.get(System.getProperty("user.dir"),
                "src", "main", "resources", "Enviroment.properties").toString();
        propertyReader = new PropertyReader(propertiesPath);

        // Initializing  custom JsonReader class to process external TestData.json values
        jsonReader = new JsonReader("./src/test/resources/TestData.json");

        // Configuring and Starting the Appium Server dynamically based on properties configuration
        service = new AppiumServiceBuilder()
                .withIPAddress(propertyReader.getProperty("ipAddress"))
                .usingPort(Integer.parseInt(propertyReader.getProperty("port")))
                .withArgument(() -> "--use-drivers", propertyReader.getProperty("driver")) // Forces Appium to execute using the specified driver
                .build();

        service.start();
    }

    @BeforeMethod
    public void setUpDriver() throws MalformedURLException, URISyntaxException {
        // Constructing UiAutomator2 execution capabilities dynamically
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(Platform.ANDROID.name())
                .setDeviceName(propertyReader.getProperty("device"))
                .noReset() // To make appium not start with clean state
                .setApp(System.getProperty("user.dir") + "/src/test/resources/" + propertyReader.getProperty("app"));

        String appiumUrl = "http://" + propertyReader.getProperty("ipAddress") + ":" + propertyReader.getProperty("port");
        driver = new AndroidDriver(new URI(appiumUrl).toURL(), options);
    }


    @Test(description = "Validate text field input and checkbox selection in Forms controls")
    @Story("User can interact with Light Theme controls smoothly")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Navigates to Forms controls menu, inputs dynamic JSON data, hides keyboard, and validates structural component state transitions.")
    public void validateFormsTextFieldFlow() {
        // Instantiate the page object and inject the active driver session
        FormsPage formsPage = new FormsPage(driver);

        // Injecting test execution parameters directly into the Allure Report dashboard context
        Allure.parameter("Target Device Architecture", propertyReader.getProperty("device"));
        Allure.parameter("Automation Engine Driver", propertyReader.getProperty("driver"));

        // STEP 1: Navigation Steps

        formsPage.clickViewsBtn();
        formsPage.clickControlsBtn();
        formsPage.clickLightThemeBtn();


        // STEP 2: Test Data Retrieval (Data-Driven Testing)

        String expectedSample = jsonReader.getJson("sample");
        Allure.parameter("Injected Payload Value", expectedSample);

        // STEP 3: Action

        formsPage.TextFieldValue();
        formsPage.TypeTextValue(expectedSample);


        // STEP 4: Verification / Assertions

        // Validate that the visual text currently present on the mobile UI matches the JSON file exactly.
        formsPage.verifyTextFieldText(expectedSample);

        // Interact with Checkbox 1 and assert its state changes to checked
        formsPage.clickCheckbox1();
        Assert.assertTrue(formsPage.isCheckbox1Checked(), "Error: Checkbox 1 was clicked but failed to switch to checked!");

        // Interact with Checkbox 2 and assert its state changes to checked
        formsPage.clickCheckbox2();
        Assert.assertTrue(formsPage.isCheckbox2Checked(), "Error: Checkbox 2 was clicked but failed to switch to checked!");
    }


    @AfterMethod
    public void tearDownDriver() {
        // Ensure the driver instance is active before performing tear-down actions
        if (driver != null) {
            try {
                // 1. Dynamic Package Retrieval
                String currentPackage = driver.getCurrentPackage();

                // 2. Force Kill Application: Resets execution state to safeguard Test Isolation
                driver.terminateApp(currentPackage);
                System.out.println("Done: Application terminated successfully.");
            } catch (Exception e) {
                // Wrapped in a catch block to protect execution flow if the app closed unexpectedly beforehand
                System.out.println("Notice: Could not terminate app window: " + e.getMessage());
            }

            // 3. Close the core automation driver session
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