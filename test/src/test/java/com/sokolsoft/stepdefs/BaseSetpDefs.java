package com.sokolsoft.stepdefs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

public class BaseSetpDefs {
    protected WebDriver driver = Driver.driver();
    protected File screenshotDirectory = Driver.screenshotDirectory();

    protected void wait(ExpectedCondition<Boolean> condition) {
        (new WebDriverWait(driver, 10)).until(condition);
    }

    protected void waitClass(String className) {
        wait(d -> driver.findElements(By.className(className)).size() > 0);
    }
}
