package com.sokolsoft.ecm;

import com.sokolsoft.ecm.core.model.*;
import com.sokolsoft.ecm.core.repository.*;
import com.sokolsoft.ecm.core.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class DemoData {
    private ContragentRepository contragentRepository;
    
    private UserRepository userRepository;

    private ContragentPersonRepository contragentPersonRepository;

    private DocumentRepository documentRepository;

    private DeliveryMethodRepository deliveryMethodRepository;

    @Autowired
    public DemoData(ContragentRepository contragentRepository,
                    UserRepository userRepository,
                    ContragentPersonRepository contragentPersonRepository,
                    DocumentRepository documentRepository,
                    DeliveryMethodRepository deliveryMethodRepository) {
        this.contragentRepository = contragentRepository;
        this.userRepository = userRepository;
        this.contragentPersonRepository = contragentPersonRepository;
        this.documentRepository = documentRepository;
        this.deliveryMethodRepository = deliveryMethodRepository;
    }
    
    public void uploadData() {
        uploadDocuments();
        uploadContragents();
        uploadUsers();
        uploadDeliveryMethods();
    }

    private void uploadDeliveryMethods() {
        String[][] methods = new String[][] {
                {"1a84dc46-0352-4b95-a3b0-bdb8faa53fda", "Почта"},
                {"f0ba70b3-b7e5-44e9-aa0e-9b1d33a0436a", "Электронная почта"},
                {"730dcee9-3e6a-477c-907c-444a4e49cf40", "Курьер"},
        };
        Stream.of(methods).forEach(m -> {
            DeliveryMethod deliveryMethod = new DeliveryMethod();
            deliveryMethod.setId(UUID.fromString(m[0]));
            deliveryMethod.setTitle(m[1]);
            deliveryMethodRepository.save(deliveryMethod);
        });
    }

    public void uploadDocuments() {
        List<Document> documents = new ArrayList<>();

        Document d1 = new Document();
        d1.setId(UUID.fromString("c32a1bdd-d6a4-4a09-8e7b-249d6c7fd673"));
        d1.setTitle("Запрос даных для выполнения работ");
        d1.setDocumentType("Входящий");
        d1.setDocumentKind("Запрос");
        d1.setRegistrationDate(Instant.parse("2019-03-03T00:00:00.00Z"));
        d1.setDocumentNumber("131");
        d1.setStatus("На рассмотрении");
        d1.setAddressee(UUID.fromString("9bb42bab-8965-49d2-b134-cec0d1505cc3"));
        d1.setAddresseeTitle("Ивашова А. Е.");

        d1.setAddresseeCopies(Arrays.asList(UUID.fromString("b1fea135-9e3e-4e41-ad6d-492841868fd5"), UUID.fromString("335938f6-877b-4754-b20e-ea5dc2d4f1b4")));
        d1.setAddresseeCopiesTitles(Arrays.asList("Агапов Н. В.", "Виноградова А. А."));

        d1.setExternalOrganization(UUID.fromString("60d3fde7-c523-4afb-8a56-e713775a3be1"));
        d1.setExternalOrganizationTitle("КАПИТАЛ, деловой центр");
        d1.setExternalExecutor("Кирилченко И. П.");
        d1.setExternalSigner("Васин И. Ю.");
        d1.setExternalNumber("0012");
        d1.setExternalDate(Instant.parse("2019-02-14T00:00:00.00Z"));
        d1.setCreateDate(Instant.now());
        documents.add(d1);


        Document d2 = new Document();
        d2.setId(UUID.fromString("f0e0615a-8ab6-43bf-bf5d-a1259ec24ab4"));
        d2.setTitle("О направлении информации");
        d2.setDocumentType("Входящий");
        d2.setDocumentKind("Запрос");
        d2.setRegistrationDate(Instant.parse("2019-02-02T00:00:00.00Z"));
        d2.setDocumentNumber("144");
        d2.setStatus("На согласовании");
        d2.setCreateDate(Instant.now());
        documents.add(d2);

        Document d3 = new Document();
        d3.setId(UUID.fromString("d595a410-52fc-4b87-af1f-0c73ed2e8924"));
        d3.setTitle("Архивная справка");
        d3.setDocumentType("Входящий");
        d3.setDocumentKind("Справка");
        d3.setRegistrationDate(Instant.parse("2019-01-05T00:00:00.00Z"));
        d3.setDocumentNumber("145");
        d3.setStatus("На исполнении");
        d3.setCreateDate(Instant.now());
        documents.add(d3);

        documentRepository.saveAll(documents);

        for (int i = 0; i < 100; i++) {
            Document d = new Document();
            d.setId(UUID.randomUUID());
            d.setTitle("Тестовый документ " + i);
            d.setDocumentType(new String[]{"Входящий", "Исходящий", "Внутренний"}[new Random().nextInt(3)]);
            d.setDocumentKind("Запрос");
            d.setStatus(new String[]{"Рассмотрение", "Согласование", "Исполнение"}[new Random().nextInt(3)]);
            d.setCreator(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
            d.setCreateDate(Instant.now());

            documentRepository.save(d);
        }

        Document td1 = new Document();
        td1.setId(UUID.fromString("5b92f46c-5398-43d2-bd31-70fb9fe65579"));
        td1.setTitle("Тестовый - Нет полей");
        td1.setDocumentType("Тестовый");
        td1.setDocumentKind("Тест");
        td1.setStatus("Тест<Нет полей>");
        td1.setCreator(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
        td1.setCreateDate(Instant.now());
        documentRepository.save(td1);

        Document td2 = new Document();
        td2.setId(UUID.fromString("24112a41-1ace-4e30-ac69-65ddac5cc282"));
        td2.setTitle("Тестовый - Только чтение");
        td2.setDocumentType("Тестовый");
        td2.setDocumentKind("Тест");
        td2.setStatus("Тест<Только чтение>");
        td2.setCreator(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
        td2.setCreateDate(Instant.now());
        documentRepository.save(td2);

        Document td3 = new Document();
        td3.setId(UUID.fromString("789cd5c3-3428-495e-a2d4-f9132a1edb04"));
        td3.setTitle("Тестовый - Все поля");
        td3.setDocumentType("Тестовый");
        td3.setDocumentKind("Тест");
        td3.setStatus("Тест<Все редактируемые>");
        td3.setCreator(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
        td3.setCreateDate(Instant.now());
        documentRepository.save(td3);

        Document td4 = new Document();
        td4.setId(UUID.fromString("34d7b9af-06aa-4e3b-b5a3-091d54e394bf"));
        td4.setTitle("Тестовый - Поля обязательные");
        td4.setDocumentType("Тестовый");
//        td4.setDocumentKind("Тест");
        td4.setStatus("Тест<Все обязательные>");
        td4.setCreator(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
        td4.setCreateDate(Instant.now());
        documentRepository.save(td4);

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

        ContragentPerson person1 = new ContragentPerson();
        person1.setId(UUID.fromString("b45b2465-8590-4f09-8d7b-4b2e58683599"));
        person1.setTitle("Васин И. Ю.");
        person1.setOrganizationId(UUID.fromString("60d3fde7-c523-4afb-8a56-e713775a3be1"));
        contragentPersonRepository.save(person1);

        ContragentPerson person2 = new ContragentPerson();
        person2.setId(UUID.fromString("dde4a9a4-754f-4438-875c-5a6b2d5b0ba0"));
        person2.setTitle("Кирилченко И. П.");
        person2.setOrganizationId(UUID.fromString("60d3fde7-c523-4afb-8a56-e713775a3be1"));
        contragentPersonRepository.save(person2);

    }
    
    private void uploadUsers() {
        List<User> list = new ArrayList<>();

        String[][] users = {{"580f62b3-7b96-4109-a321-dc7d24109a1a", "Поляков И. В.", null, null, "Иван", "Вячеславович", "Поляков"},
            {"722b151c-f9d7-4222-b541-cfc554695510", "Ивашов В. Н.", "test", "749bc613e059779cc5e4c22e8fa7bba9", "Виктор", "Николаевич", "Ивашов"},
        {"dc175f6e-b18d-495f-aca9-58c956e48a42", "Беломестов Г. В.", null, null, "Густав", "Валерьевич", "Беломестов"},
        {"e879c49c-4fdf-43a1-8507-7091f2dea03d", "Волков Б. П.", null, null, "Бронислав", "Петрович", "Волков"},
        {"3379db0f-8221-43fd-8d46-e05edcef9686", "Ивашов Н. В.", null, null, "Никита", "Валерьевич", "Ивашов"},
        {"a1109397-b265-4cf4-8d6e-986c0fa0ce54", "Былинкин Б. Н.", null, null, "Борис", "Николаевич", "Былинкин"},
        {"a2cfff23-4070-4063-9e94-c3956e824122", "Захаров Н. В.", null, null, "Никита", "Вячеславович", "Захаров"},
        {"fb2f4ebc-34c6-4fb5-b73a-7ed1b9249a68", "Агапов Г. В.", null, null, "Густав", "Валерьевич", "Агапов"},
        {"0cde912f-55e6-48c9-97f8-464fbe74cbb5", "Добрынин Л. В.", null, null, "Леонид", "Валерьевич", "Добрынин"},
        {"b1fea135-9e3e-4e41-ad6d-492841868fd5", "Агапов Н. В.", null, null, "Никита", "Вячеславович", "Агапов"},
        {"06fad082-6745-4265-bec1-fd1aa741844b", "Полякова В. Е.", null, null, "Валентина", "Егоровна", "Полякова"},
        {"335938f6-877b-4754-b20e-ea5dc2d4f1b4", "Виноградова А. А.", null, null, "Анна", "Аркадьевна", "Виноградова"},
        {"4f4181fc-6953-4fd0-8684-37e435e3cce1", "Волкова А. Г.", null, null, "Антонина", "Георгиевна", "Волкова"},
        {"18af0599-b299-415f-8642-f1b1d1eef37f", "Гарина К. М.", null, null, "Катерина", "Михайловна", "Гарина"},
        {"b2d8bff1-f48f-482d-bf2d-9606fe0e1bae", "Беломестова В. М.", null, null, "Валентина", "Михайловна", "Беломестова"},
        {"c7e81475-f453-49bc-bdfd-5f8614f063fc", "Алексеева А. Е.", null, null, "Антонина", "Егоровна", "Алексеева"},
        {"551697fd-8dac-4132-a557-e74e43c44390", "Полякова К. А.", null, null, "Катерина", "Аркадьевна", "Полякова"},
        {"995b431c-c776-4084-81fc-1ef9d8acd087", "Зверева А. Г.", null, null, "Анна", "Георгиевна", "Зверева"},
        {"5809dcc7-2903-4dfa-bea9-eb3d30035d51", "Балашова Д. М.", null, null, "Дарья", "Михайловна", "Балашова"},
        {"f179d503-c3fe-4b31-b596-fb36aa58f364", "Гарина В. М.", null, null, "Валентина", "Михайловна", "Гарина"},

        {"5be7fed3-fde0-4aa9-8a3a-498d5026a3a2", "Беломестов И. М.", null, null, "Иван", "Максимович", "Беломестов"},
        {"5ca6d548-afa3-4c26-a72e-f0f19100e701", "Луков Б. П.", null, null, "Бронислав", "Петрович", "Луков"},
        {"a36d1e1d-a00b-41f7-b0a4-12c63646d384", "Карандашов Б. С.", null, null, "Бронислав", "Сергеевич", "Карандашов"},
        {"5d729b02-4d8c-4abd-80cc-c7497d1945cf", "Луков В. А.", null, null, "Виктор", "Алексеевич", "Луков"},
        {"00fb33e6-061c-46d5-a5c9-22053eef30c4", "Грибов Б. В.", null, null, "Борис", "Валерьевич", "Грибов"},
        {"1b025ff2-948d-4f7f-8261-6456f8f8c19e", "Зверев Б. А.", null, null, "Бронислав", "Алексеевич", "Зверев"},
        {"c58f8de5-4431-4c1b-a62e-55c804ba914f", "Грибов К. С.", null, null, "Карл", "Сергеевич", "Грибов"},
        {"fc947860-1a91-4a23-a275-82dec5bb5c96", "Болотников К. Н.", null, null, "Карл", "Николаевич", "Болотников"},
        {"d259e840-0b34-4512-bc2e-5b5498dc4171", "Карандашов К. Н.", null, null, "Карл", "Николаевич", "Карандашов"},
        {"7fa705ee-ac8e-40df-8c83-4024aedc0421", "Виноградов К. В.", null, null, "Карл", "Валерьевич", "Виноградов"},
        {"721f2b0b-91d1-4703-bff0-05a130a3b114", "Зверева А. А.", null, null, "Антонина", "Аркадьевна", "Зверева"},
        {"9bb42bab-8965-49d2-b134-cec0d1505cc3", "Ивашова А. Е.", null, null, "Анна", "Егоровна", "Ивашова"},
        {"c90b9c9f-ca1a-4b7c-bc77-3557c908f8d7", "Енотина А. В.", null, null, "Анна", "Вячеславовна", "Енотина"},
        {"39d3f6ae-ca8b-4814-a25e-57b6f253bde3", "Карандашова А. А.", null, null, "Антонина", "Аркадьевна", "Карандашова"},
        {"c5e87618-76c8-491f-bb25-aee010be5ab7", "Грибова В. М.", null, null, "Валентина", "Михайловна", "Грибова"},
        {"c0d2e91b-81f6-4560-b2c3-11654c6835ef", "Гарина А. А.", null, null, "Анна", "Аркадьевна", "Гарина"},
        {"cc1b2ba7-c245-4680-bc15-97b04be8b50e", "Грибова А. С.", null, null, "Антонина", "Сергеевна", "Грибова"},
        {"697660a9-8bed-4548-a15e-757282776ebb", "Карандашова А. В.", null, null, "Анна", "Вячеславовна", "Карандашова"},
        {"a4cff11c-936a-45e1-889f-f478f27fcc20", "Зверева А. М.", null, null, "Антонина", "Михайловна", "Зверева"},
        {"52cc85b5-fab7-4365-a9cd-94afac1f0e8d", "Admin", null, null, "Admin", "Admin", "Admin"},
        {"a4fa069b-64ac-4a7e-ba5f-a3dc3e84c66e", "Карандашова Д. Г.", null, null, "Дарья", "Георгиевна", "Карандашова"}};

        for (String[] user : users) {
            User u = new User();
            u.setId(UUID.fromString(user[0]));
            u.setTitle(user[1]);
            u.setFirstName(user[4]);
            u.setMiddleName(user[5]);
            u.setLastName(user[6]);
            list.add(u);
        }

        userRepository.saveAll(list);
    }
}
