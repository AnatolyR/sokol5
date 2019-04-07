package com.sokolsoft.ecm;

import com.sokolsoft.ecm.core.model.Contragent;
import com.sokolsoft.ecm.core.repository.ContragentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DemoData {
    private ContragentRepository contragentRepository;

    @Autowired
    public DemoData(ContragentRepository contragentRepository) {
        this.contragentRepository = contragentRepository;
    }
    
    public void uploadData() {
        uploadContragents();
    }
    
    public void uploadContragents() {
        List<Contragent> list = new ArrayList<>();
        String[][] contragents = {{"970cdb4c-cd9a-4944-9bf0-f78585220893", "РОСТРА, ОАО, страховая компания"},
        {"63e5b8b9-d2e0-499b-9b2e-75fb02191ae7", "СТРИМ, автострахование"},
        {"4a2bec91-3bff-48bf-a052-19a150dde393", "УралСиб, ЗАО Страховая группа"},
        {"afc55206-1820-4da1-8bed-69705d202332", "ФЕНИКС, страховое агентство"},
        {"79876265-1e71-47c5-8fec-1f203e862b7c", "ЭНЕРГОГАРАНТ, страхование"},
        {"60d3fde7-c523-4afb-8a56-e713775a3be1", "КАПИТАЛ, деловой центр"},
        {"f1eddfae-b6e1-43f6-b73a-31f3f14f517c", "ПРОФИНТЕР ООО, реклам"},
        {"05a45146-6d08-4021-9248-014a0f373a51", "РОСТЕЛЕКОМ, услуги связи, интернет-провайдер"},
        {"55c3554b-99cc-4857-a964-7a2731a6ddfe", "ИСКУССТВО СТИЛЯ, имидж-агентство"},
        {"ccfde8f8-1563-4dea-81f9-4b15dd3633a0", "КАРАВЕЛЛА, бюро переводов"},
        {"3704a2c0-015f-4c3f-b5ec-01556fc4a204", "АРХИТЕКТУРА, ООО, проектная организация"},
        {"648f37bd-f6b3-4795-8b5c-e7d6f70b8b40", "ГРАЖДАНПРОЕКТ, ООО, проектно-конструкторская фирма"},
        {"65208064-e1ef-4bf5-be7b-39a3839668d3", "КВАЛИТЕТ, строительная экспертиза"},
        {"9f470a59-4216-4958-b01c-a6093f6de5fb", "ПЛАНЕТА, кадастровый центр"},
        {"57a1ecbb-e1cc-4ab1-b782-88e723b416dc", "КРОВПРОЕКТ, ООО, строительная организация"},
        {"e4a238ef-2824-4374-88c1-92388a9b05eb", "АЛАТОО ИТЦ, электромонтажные работы"}};
        for (String[] contragent : contragents) {
            Contragent c = new Contragent();
            c.setId(UUID.fromString(contragent[0]));
            c.setTitle(contragent[1]);
            list.add(c);
        }
        contragentRepository.saveAll(list);
    }
}
