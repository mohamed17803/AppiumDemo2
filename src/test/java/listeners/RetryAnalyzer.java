package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import utils.ConfigReader;
import utils.LogHelper;

/**
 * Re-runs failed tests up to {@code maxRetries} times from config.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetries = ConfigReader.getInt("maxRetries");
        if (retryCount < maxRetries) {
            retryCount++;
            LogHelper.warn(String.format(
                    "Retrying test '%s' (%d/%d) after failure: %s",
                    result.getName(),
                    retryCount,
                    maxRetries,
                    result.getThrowable() != null ? result.getThrowable().getMessage() : "unknown"));
            return true;
        }
        return false;
    }
}
