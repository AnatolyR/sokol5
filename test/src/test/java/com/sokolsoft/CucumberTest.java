package com.sokolsoft;

import com.sokolsoft.stepdefs.Driver;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

@RunWith(Cucumber.class)
//@CucumberOptions(plugin = {"pretty", "html:target/cucumber-reports"}, features = "src/test/features", tags = "@current")
@CucumberOptions(plugin = "pretty", features = "src/test/features")
public class CucumberTest {
    public CucumberTest() {
        File screenshotDirectory = new File("/Users/anatolii/sokol5/test/screenshots");
        try {
            FileUtils.cleanDirectory(screenshotDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterAll() {
        Driver.driver().close();
    }
}
