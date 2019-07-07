package com.sokolsoft;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

@RunWith(Cucumber.class)
//@CucumberOptions(plugin = "pretty", features = "src/test/features", tags = "@current")
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
}
