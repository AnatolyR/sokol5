package com.sokolsoft.stepdefs;

import cucumber.api.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.То;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommonStepDefs extends BaseSetpDefs {
    @Before
    public void openBrowser() throws MalformedURLException {
        driver.get("http://localhost:9090");
        wait(d -> d.findElements(By.id("inputUser")).size() > 0);
    }

    @After(order = 1)
    public void closeBrowser() {
//        driver.close();
    }

    @After(order = 2)
    public void afterScenario(Scenario scenario){
        WebDriver augmentedDriver = new Augmenter().augment(driver);
        File screenshot = ((TakesScreenshot)augmentedDriver).
                getScreenshotAs(OutputType.FILE);

//        String name = scenario.getName() + "." + screenshot.getName();
        String name = scenario.getName() + ".png";
        String[] path = scenario.getUri().split("/");
        try {
            Path scrPath = Paths.get(screenshotDirectory.getAbsolutePath(), path[path.length - 2], path[path.length - 1], name);
            Files.createDirectories(scrPath.getParent());
            Files.copy(screenshot.toPath(), scrPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Дано("пользователь {string} с паролем {string} выполнивший вход")
    public void пользовательВыполнившийВход(String userId, String userPass) {
        driver.findElement(By.id("inputUser")).sendKeys(userId);
        driver.findElement(By.id("inputPassword")).sendKeys(userPass);
        driver.findElement(By.tagName("button")).click();
        wait(d -> d.findElements(By.className("navbar")).size() > 0);
    }

    @И("нажать кнопку {string}")
    public void нажатьКнопку(String button) {
        List<WebElement> buttonElements = driver.findElements(By.xpath("//button[contains(text(),'" + button + "')]"));
        wait(d -> buttonElements.size() > 0);
        buttonElements.get(0).click();
    }

    @И("в поле {string} ввести {string}")
    public void вПолеВвести(String field, String value) {
        wait(d -> driver.findElements(By.className("s-form-field")).size() > 0);
        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 's-form-field')]/label[contains(text(),'" + field + "')]"));
        WebElement inputDiv = fieldLabel.get(0).findElement(By.xpath("following-sibling::*"));

        inputDiv.findElement(By.tagName("input")).sendKeys(value);
    }

    @То("будет выведено информационное сообщение {string}")
    public void будетВыведеноИнформационноеСообщение(String message) {
        wait(d -> driver.findElements(By.className("toast-body")).size() > 0);
        assertEquals(message, driver.findElement(By.className("toast-body")).getText());
    }

    @И("в поле {string} будет {string}")
    public void вПолеБудет(String title, String value) {
        wait(d -> driver.findElements(By.className("s-form-field")).size() > 0);

        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 's-form-field')]/label[contains(text(),'" + title + "')]"));

        WebElement inputDiv = fieldLabel.get(0).findElement(By.xpath("following-sibling::*"));
        assertEquals(value, inputDiv.findElement(By.tagName("input")).getAttribute("value"));
    }
}
