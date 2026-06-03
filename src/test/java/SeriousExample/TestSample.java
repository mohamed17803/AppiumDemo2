package SeriousExample;


import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.Platform;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class TestSample {
    @Test
    public static void TestExampleRun() throws URISyntaxException, MalformedURLException {

        // Appium Server launched via : --address 127.0.0.1 --port 4723 --use-drivers UIAutomator2
        AppiumDriverLocalService service;

        service = new AppiumServiceBuilder()
                .usingPort(4723)
                .withIPAddress("127.0.0.1")
                .withArgument(() -> "--use-drivers", "uiautomator2")  //x`
                .build();

        service.start();


        //Capabilities
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName(Platform.ANDROID.name());
        options.noReset();
        options.setDeviceName("Pixel 6 API");
        options.setApp("./src/test/resources/ApiDemos-debug.apk");




        AndroidDriver driver;

        try {
            driver = new AndroidDriver(
                    // The default URL in Appium  is http://127.0.0.1:4723/wd/hub
                    new URI("http://127.0.0.1:4723").toURL(),
                    options
            );
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
// Tear Down
        driver.quit();
        service.stop();
    }
}