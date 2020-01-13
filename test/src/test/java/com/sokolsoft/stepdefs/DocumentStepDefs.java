package com.sokolsoft.stepdefs;

import io.cucumber.java.ru.Если;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.То;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DocumentStepDefs extends BaseSetpDefs {
    @Если("открыт Список документов")
    public void открытСправочникКонтрагентов() {
        
    }

    @И("нажать меню {string}")
    public void нажатьМеню(String text) {
        driver.findElement(By.linkText(text)).click();
        wait(d -> d.findElements(By.className("dropdown-menu")).size() > 0);
    }

    @И("нажать пункт меню {string}")
    public void нажатьПунктМеню(String text) {
        WebElement menu = driver.findElement(By.className("dropdown-menu"));
        menu.findElement(By.linkText(text)).click();
    }

    @То("в заголовке карточки документа будет")
    public void первыеЗначенияСправочника(List<List<String>> data) {
        wait(d -> d.findElements(By.className("s-document-header")).size() > 0);
        wait(d -> d.findElements(By.className("s-document-subheader")).size() > 0);
        wait(d -> d.findElements(By.className("s-document-status")).size() > 0);
        List<WebElement> column1 = driver.findElements(By.className("s-document-header"));
        List<WebElement> column2 = driver.findElements(By.className("s-document-subheader"));
        List<WebElement> column3 = driver.findElements(By.className("s-document-status"));
        assertEquals(data.get(0).get(0), column1.get(0).getText());
        assertEquals(data.get(1).get(0), column2.get(0).getText());
        assertEquals(data.get(2).get(0), column3.get(0).getText());
    }

    @И("в селектор {string} ввести {string}")
    public void вСелекторВвести(String field, String value) {
        wait(d -> driver.findElements(By.className("form-group")).size() > 0);
        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 'form-group')]/label[contains(text(),'" + field + "')]"));
        WebElement selectDiv = fieldLabel.get(0).findElement(By.xpath("following-sibling::*"));

        selectDiv.click();
        selectDiv.findElement(By.tagName("input")).sendKeys(value);
        wait(d -> selectDiv.findElements(By.className("selectize-dropdown")).size() > 0);
        wait(d -> selectDiv.findElement(By.className("selectize-dropdown")).isDisplayed());
        selectDiv.findElement(By.tagName("input")).sendKeys(Keys.ENTER);
    }

    @И("в селекторе с возможностью добавления {string} ввести {string}")
    public void вСелекторСВозможжностьюДобавленияВвести(String field, String value) {
        wait(d -> driver.findElements(By.className("form-group")).size() > 0);
        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 'form-group')]/label[contains(text(),'" + field + "')]"));
        WebElement selectDiv = fieldLabel.get(0).findElement(By.xpath("following-sibling::*"));

        selectDiv.click();
        selectDiv.findElement(By.tagName("input")).sendKeys(value);
        wait(d -> selectDiv.findElements(By.className("selectize-dropdown")).size() > 0);
        wait(d -> selectDiv.findElement(By.className("selectize-dropdown")).isDisplayed());
        wait(d -> selectDiv.findElement(
                By.className("selectize-dropdown"))
                .findElements(By.className("option")).size() > 0);

        selectDiv.findElement(By.tagName("input")).sendKeys(Keys.ENTER);
    }

    @И("в селекторе {string} будет {string}")
    public void вСелектореБудет(String title, String value) {
        wait(d -> driver.findElements(By.className("s-form-field")).size() > 0);

        List<WebElement> fieldLabel = driver.findElements(By.xpath("//div[contains(@class, 'form-group')]/label[contains(text(),'" + title + "')]"));

        WebElement inputDiv = fieldLabel.get(0).findElement(By.xpath("following-sibling::*"));
        assertEquals(value, inputDiv.findElement(By.tagName("input")).getAttribute("value"));
    }
}
