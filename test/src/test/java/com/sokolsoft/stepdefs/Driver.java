package com.sokolsoft.stepdefs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Driver {
    private static Driver instance;

    private WebDriver driver;

    private File screenshotDirectory;

    {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        DesiredCapabilities dc = DesiredCapabilities.chrome();
        dc.setCapability(ChromeOptions.CAPABILITY, options);
        try {
            driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515/"), dc);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        screenshotDirectory = new File("/Users/anatolii/sokol5/test/screenshots");
    }

    protected void wait(ExpectedCondition<Boolean> condition) {
        (new WebDriverWait(driver, 10)).until(condition);
    }

    protected Driver() {

    }

    public static synchronized Driver instance() {
        if (instance == null) {
            instance = new Driver();
        }

        return instance;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public File getScreenshotDirectory() {
        return screenshotDirectory;
    }

    public static WebDriver driver() {
        return instance().getDriver();
    }

    public static File screenshotDirectory() {
        return instance().getScreenshotDirectory();
    }
}
