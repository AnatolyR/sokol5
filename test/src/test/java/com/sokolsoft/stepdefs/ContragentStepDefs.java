package com.sokolsoft.stepdefs;

import cucumber.api.Scenario;
import cucumber.api.java.ru.Дано;
import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.То;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
//import io.cucumber.java.ru.*;

import static org.junit.Assert.*;



public class ContragentStepDefs {
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

    @Before
    public void openBrowser() throws MalformedURLException {
        driver.get("http://localhost:9090");
        wait(d -> d.findElements(By.id("inputUser")).size() > 0);
    }

    @After(order = 1)
    public void closeBrowser() {
        driver.close();
    }

    @Дано("пользователь {string} с паролем {string} выполнивший вход")
    public void пользовательВыполнившийВход(String userId, String userPass) {
        driver.findElement(By.id("inputUser")).sendKeys(userId);
        driver.findElement(By.id("inputPassword")).sendKeys(userPass);
        driver.findElement(By.tagName("button")).click();
        wait(d -> d.findElements(By.className("navbar")).size() > 0);
    }

    @Если("открыт Справочник контрагентов")
    public void открытСправочникКонтрагентов() {
        driver.findElement(By.linkText("Справочники")).click();
        wait(d -> d.findElements(By.className("s-dictionaries-list")).size() > 0);

        driver.findElement(By.linkText("Контрагенты")).click();
        wait(d -> d.findElements(By.className("s-dictionary-container")).size() > 0);
    }

    @То("первые значения справочника")
    public void первыеЗначенияСправочника(List<List<String>> data) {
        wait(d -> d.findElements(By.className("s-table-cell-title")).size() > 0);
        wait(d -> d.findElements(By.className("s-table-cell-address")).size() > 0);
        wait(d -> d.findElements(By.className("s-table-cell-phone")).size() > 0);
        wait(d -> d.findElements(By.className("s-table-cell-fullName")).size() > 0);
        List<WebElement> column1 = driver.findElements(By.className("s-table-cell-title"));
        List<WebElement> column2 = driver.findElements(By.className("s-table-cell-address"));
        List<WebElement> column3 = driver.findElements(By.className("s-table-cell-phone"));
        List<WebElement> column4 = driver.findElements(By.className("s-table-cell-fullName"));
        for (int r = 0; r < data.size(); r++) {
            List<String> row = data.get(r);
            assertEquals(row.get(0), column1.get(r).getText());
            assertEquals(row.get(1), column2.get(r).getText());
            assertEquals(row.get(2), column3.get(r).getText());
            assertEquals(row.get(3), column4.get(r).getText());
        }
    }

    protected void wait(ExpectedCondition<Boolean> condition) {
        (new WebDriverWait(driver, 10)).until(condition);
    }

    protected void waitClass(String className) {
        wait(d -> driver.findElements(By.className(className)).size() > 0);
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

    @То("будет выведено модальное окно с заголовком {string} и сообщением {string}")
    public void будетВыведеноМодальноеОкно(String title, String message) {
        wait(d -> driver.findElements(By.className("s-modal-incorrect-fields")).size() > 0);
        WebElement dialog = driver.findElement(By.className("s-modal-incorrect-fields"));
        wait(d -> !dialog.findElements(By.className("modal-title")).get(0).getText().isEmpty());
        assertEquals(title, dialog.findElements(By.className("modal-title")).get(0).getText());
        wait(d -> !dialog.findElements(By.className("modal-body")).get(0).findElement(By.tagName("p")).getText().isEmpty());
        assertEquals(message, dialog.findElements(By.className("modal-body")).get(0).findElement(By.tagName("p")).getText().trim());
    }

    @After(order = 2)
    public void afterScenario(Scenario scenario){
        WebDriver augmentedDriver = new Augmenter().augment(driver);
        File screenshot = ((TakesScreenshot)augmentedDriver).
                getScreenshotAs(OutputType.FILE);

//        String name = scenario.getName() + "." + screenshot.getName();
        String name = scenario.getName() + ".png";
        try {
            FileUtils.copyFile(screenshot, new File(screenshotDirectory, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @И("нажать значение справочника {string}")
    public void нажатьЗначениеСправочника(String linkText) {
        wait(d -> driver.findElements(By.className("s-dictionary-container")).size() > 0);
        WebElement container = driver.findElement(By.className("s-dictionary-container"));
        wait(d -> driver.findElements(By.linkText(linkText)).size() > 0);
        container.findElement(By.linkText(linkText)).click();
    }

    @То("будет открыта карточка значения справочника контрагентов")
    public void будетОткрытаКарточкаЗначенияСправочникаКонтрагентов() {
        waitClass("s-contragent-container");
        waitClass("s-contragent-button-bar");
        waitClass("s-contragent-header");
        waitClass("s-contragent-subheader");
        waitClass("s-contragent-form");
    }

    @И("в поле {string} будет {string}")
    public void вПолеБудет(String title, String value) {
        wait(d -> driver.findElements(By.className("s-form-field")).size() > 0);

        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 's-form-field')]/label[contains(text(),'" + title + "')]"));

        WebElement inputDiv = fieldLabel.get(0).findElement(By.xpath("following-sibling::*"));
        assertEquals(value, inputDiv.findElement(By.tagName("input")).getAttribute("value"));
    }

    @И("кнопки карточки будут {string}")
    public void кнопкиКарточкиБудут(String buttons) {
        WebElement container = driver.findElement(By.className("s-contragent-container"));
        assertEquals(buttons, container.findElement(By.className("s-contragent-button-bar")).getText());
    }

    @И("заголовок карточки будет {string}")
    public void заголовокКарточкиБудет(String text) {
        WebElement container = driver.findElement(By.className("s-contragent-container"));
        assertEquals(text, container.findElement(By.className("s-contragent-header")).getText());
    }

    @И("подзаголовок карточки будет {string}")
    public void подзаголовокКарточкиБудет(String text) {
        WebElement container = driver.findElement(By.className("s-contragent-container"));
        assertEquals(text, container.findElement(By.className("s-contragent-subheader")).getText());
    }

    @И("выбрать первое значение справочника щелкнув на чекбоксе")
    public void выбратьПервоеЗначениеСправочникаЩелкнувНаЧекбоксе() {
        waitClass("s-table-cell-select");
        WebElement dictionary = driver.findElement(By.className("s-dictionary-container"));
        dictionary.findElement(By.className("s-table-cell-select")).click();
    }

    @И("в модальном окне подтверждения удаления нажать {string}")
    public void вМодальномОкнеПодтвержденияУдаленияНажать(String button) {
        waitClass("s-modal-confirm-delete");
        WebElement modal = driver.findElement(By.className("s-modal-confirm-delete"));

        List<WebElement> buttonElements = modal.findElements(By.xpath(".//button[contains(text(),'" + button + "')]"));
        wait(d -> buttonElements.size() > 0);
        buttonElements.get(0).click();
    }
}
