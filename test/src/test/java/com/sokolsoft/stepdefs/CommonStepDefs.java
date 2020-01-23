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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        driver.manage().deleteAllCookies();
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

    @И("прокрутить страницу наверх")
    public void прокрутитьСтраницуНаверх() {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, 0)");
    }

    @И("в поле даты {string} ввести {string}")
    public void вПолеДатыИсходящаяДатаВвести(String field, String value) {
        wait(d -> driver.findElements(By.className("form-group")).size() > 0);
        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 'form-group')]/label[contains(text(),'" + field + "')]"));
        WebElement selectDiv = fieldLabel.get(0).findElement(By.xpath("following-sibling::*"));

        selectDiv.click();
        WebElement input = selectDiv.findElement(By.tagName("input"));
        wait(d -> selectDiv.findElements(By.className("dropdown-menu")).size() > 0);
        wait(d -> selectDiv.findElement(By.className("dropdown-menu")).isDisplayed());

        input.clear();

        input.sendKeys(value);
        input.sendKeys(Keys.ENTER);
    }

    @То("будет выведено модальное окно с заголовком {string} и сообщением {string}")
    public void будетВыведеноМодальноеОкно(String title, String message) {
        wait(d -> driver.findElements(By.className("s-modal-incorrect-fields")).size() > 0);
        WebElement dialog = driver.findElement(By.className("s-modal-incorrect-fields"));
        wait(d -> !dialog.findElements(By.className("modal-title")).get(0).getText().isEmpty());
        assertEquals(title, dialog.findElements(By.className("modal-title")).get(0).getText());
        wait(d -> !dialog.findElements(By.className("modal-body")).get(0).findElement(By.tagName("p")).getText().isEmpty());
        assertEquals(message, dialog.findElements(By.className("modal-body")).get(0).findElement(By.tagName("p")).getText().trim());
    }

    @И("очистить поле {string}")
    public void очиститьПоле(String field) {
        wait(d -> driver.findElements(By.className("s-form-field")).size() > 0);
        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 's-form-field')]/label[contains(text(),'" + field + "')]"));
        WebElement inputDiv = fieldLabel.get(0).findElement(By.xpath("following-sibling::*"));

        inputDiv.findElement(By.tagName("input")).clear();
    }

    @И("очистить селектор {string}")
    public void очиститьСелектор(String field) {
        wait(d -> driver.findElements(By.className("form-group")).size() > 0);
        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 'form-group')]/label[.//text()='" + field + "']"));
        WebElement selectDiv = fieldLabel.get(0).findElement(By.xpath("following-sibling::*"));

        String id = selectDiv.findElement(By.className("s-select-fix")).getAttribute("id");

        ((JavascriptExecutor)driver).executeScript("document.getElementById(\"" + id + "\").getElementsByClassName(\"selectized\")[0].selectize.clear()");

//        selectDiv.click();
//        wait(d -> selectDiv.findElements(By.className("selectize-dropdown")).size() > 0);
//        wait(d -> selectDiv.findElement(By.className("selectize-dropdown")).isDisplayed());
//        new Select(selectDiv.findElement(By.tagName("select"))).
    }

    @И("в селектор {string} ввести {string}")
    public void вСелекторВвести(String field, String value) {
        int index = 0;
        if (field.contains("|")) {
            String[] vals = field.split("\\|");
            field = vals[0];
            index = Integer.parseInt(vals[1]) - 1;
        }
        wait(d -> driver.findElements(By.className("form-group")).size() > 0);
//        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 'form-group')]/label[contains(text(),'" + field + "')]"));
        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 'form-group')]/label[.//text()='" + field + "']"));
        WebElement selectDiv = fieldLabel.get(index).findElement(By.xpath("following-sibling::*"));

        selectDiv.click();
        selectDiv.findElement(By.tagName("input")).sendKeys(value);
        wait(d -> selectDiv.findElements(By.className("selectize-dropdown")).size() > 0);
        wait(d -> selectDiv.findElement(By.className("selectize-dropdown")).isDisplayed());
        selectDiv.findElement(By.tagName("input")).sendKeys(Keys.ENTER);
    }

    @И("очистить дату {string}")
    public void очиститьДату(String field) {
        wait(d -> driver.findElements(By.className("form-group")).size() > 0);
        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 'form-group')]/label[contains(text(),'" + field + "')]"));
        WebElement selectDiv = fieldLabel.get(0).findElement(By.xpath("following-sibling::*"));

        selectDiv.click();
        WebElement input = selectDiv.findElement(By.tagName("input"));
        wait(d -> selectDiv.findElements(By.className("dropdown-menu")).size() > 0);
        wait(d -> selectDiv.findElement(By.className("dropdown-menu")).isDisplayed());

        input.clear();
    }
}
