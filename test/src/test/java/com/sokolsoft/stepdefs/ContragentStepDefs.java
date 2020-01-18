package com.sokolsoft.stepdefs;

import io.cucumber.java.ru.Если;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.То;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;

//import io.cucumber.java.ru.*;



public class ContragentStepDefs extends BaseSetpDefs {
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
