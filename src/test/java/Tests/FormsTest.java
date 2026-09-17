package Tests;

import Pages.FormsPage;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.JsonReader;
import utils.LogHelper;

/**
 * Forms controls flow against ApiDemos, driven by ConfigReader + DriverFactory.
 */
@Epic("Appium Android")
@Feature("Forms Automation Feature")
public class FormsTest {

    private JsonReader jsonReader;

    @BeforeClass
    public void setUpTestData() {
        jsonReader = new JsonReader("./src/test/resources/TestData.json");
        LogHelper.info("Test data loaded from TestData.json");
    }

    @BeforeMethod
    public void setUpDriver() {
        DriverFactory.initDriver();
    }

    @Test(description = "Validate text field input and checkbox selection in Forms controls")
    @Story("User can interact with Light Theme controls smoothly")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Navigates to Forms controls menu, inputs dynamic JSON data, hides keyboard, and validates structural component state transitions.")
    public void validateFormsTextFieldFlow() {
        FormsPage formsPage = new FormsPage(DriverFactory.getDriver());

        Allure.parameter("Target Device", ConfigReader.get("deviceName"));
        Allure.parameter("Platform", ConfigReader.get("platformName"));
        Allure.parameter("Appium Server", ConfigReader.get("appiumServerUrl"));

        String expectedSample = jsonReader.getJson("sample");
        Allure.parameter("Injected Payload Value", expectedSample);

        formsPage
                .clickViewsBtn()
                .clickControlsBtn()
                .clickLightThemeBtn()
                .focusTextField()
                .typeTextValue(expectedSample)
                .verifyTextFieldText(expectedSample)
                .clickCheckbox1();

        Assert.assertTrue(formsPage.isCheckbox1Checked(),
                "Error: Checkbox 1 was clicked but failed to switch to checked!");

        formsPage.clickCheckbox2();
        Assert.assertTrue(formsPage.isCheckbox2Checked(),
                "Error: Checkbox 2 was clicked but failed to switch to checked!");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownDriver() {
        try {
            if (DriverFactory.getDriverOrNull() instanceof AndroidDriver androidDriver) {
                String currentPackage = androidDriver.getCurrentPackage();
                androidDriver.terminateApp(currentPackage);
                LogHelper.info("Application terminated: " + currentPackage);
            }
        } catch (Exception e) {
            LogHelper.warn("Could not terminate app: " + e.getMessage());
        } finally {
            DriverFactory.quitDriver();
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownService() {
        DriverFactory.stopAppiumServer();
    }
}
