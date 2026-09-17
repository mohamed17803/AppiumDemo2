package listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.LogHelper;
import utils.ScreenshotUtil;

/**
 * Attaches pass/fail screenshots to Allure and logs test lifecycle events.
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        LogHelper.info("Starting test: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogHelper.info("Passed: " + result.getMethod().getMethodName());
        ScreenshotUtil.attachToAllure("Passed Test Screenshot");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LogHelper.error("Failed: " + result.getMethod().getMethodName()
                + " — " + (result.getThrowable() != null ? result.getThrowable().getMessage() : ""));
        ScreenshotUtil.attachToAllure("Failure screenshot - " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogHelper.warn("Skipped: " + result.getMethod().getMethodName());
    }
}
