package com.sokolsoft.ecm;

import com.sokolsoft.ecm.core.model.*;
import com.sokolsoft.ecm.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DemoData {
    private final ContragentRepository contragentRepository;
    
    private final UserRepository userRepository;

    private final ContragentPersonRepository contragentPersonRepository;

    private final DocumentRepository documentRepository;

    private final DeliveryMethodRepository deliveryMethodRepository;

    private final DocumentKindRepository documentKindRepository;

    private final AttachRepository attachRepository;

    private final AttachContentRepository attachContentRepository;

//    private final JdbcUserDetailsManager detailsManager;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final GroupRepository groupRepository;

    private final TaskRepository taskRepository;

    public void uploadData() {
        SecurityContext securityContext = new SecurityContextImpl();
        
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SYSTEM"));
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("test", new User(), grantedAuthorities);
        securityContext.setAuthentication(anonymousAuthenticationToken);

        SecurityContextHolder.setContext(securityContext);

        uploadDocuments();
        uploadContragents();
        uploadEmployees();
        uploadDeliveryMethods();
        uploadDocumentRinds();

        SecurityContextHolder.clearContext();
    }

    private void uploadDocumentRinds() {
        String [][] kinds = new String[][] {
                {"57389717-462c-4f7a-8158-e56fab991805", "Акт"},
                {"064e11aa-3324-4f4a-aeaf-bfddb0de8e98", "Апелляционная жалоба"},
                {"d23512f2-803a-4532-9600-6bc356d341f7", "Жалоба"},
                {"45f9aaa9-1002-4331-992b-eac36416e7a3", "Запрос"},
                {"2c994487-b708-4eec-b77b-817ba9b16b75", "Заявка"},
                {"ed029aa7-53de-4435-aca1-fa0748377ffb", "Извещение"},
                {"7ab26b19-a39f-4db9-a36f-597726886e29", "Исковое заявление"},
                {"4134dd82-49bd-4178-8451-737f6596fcf2", "Исполнительный лист"},
                {"612e569b-377e-4a5e-b9c3-6495f8998752", "Кассационная жалоба"},
                {"5e8efcef-75c3-40a7-85ca-c76b51536eda", "Определение"},
                {"192d2255-40e4-4a5a-ba0e-f6598ff45cae", "Ответ на запрос"},
                {"2c908a83-8749-496f-87d4-8cd3148be548", "Отчет"},
                {"0798f787-ef23-4dd4-8baf-e2cc460eff54", "Письмо"},
                {"fd8e6386-d0e4-4177-a50d-b8f39117edc0", "Повестка"},
                {"8060d45e-8c9c-4906-9c6b-0a8e3104e26c", "Поручение"},
                {"e57665ca-80e3-4d07-8de2-fe8f2639c986", "Постановление"},
                {"9fca7204-677b-41d3-a2e5-b04716c60cbb", "Предписание"},
                {"1ec4e749-da74-429a-9959-aea45863b835", "Представление"},
                {"5976eafb-1017-4d42-8b45-c3a52e6ee51b", "Предупреждение"},
                {"853739a6-2eac-4409-8c07-66a5aacb2d30", "Претензия"},
                {"eae6988b-c2f5-4016-8a86-b6f6923483e5", "Приглашение"},
                {"77373195-ae98-4da4-a054-4a42aedee601", "Приговор суда"},
                {"221476e8-7e89-4a60-914a-069bec890a22", "Приказ"},
                {"e192085c-88f4-4eb2-b7cc-23c92a144d61", "Протокол"},
                {"72ad160d-bb2c-4d85-9751-a194992e4df3", "Распоряжение"},
                {"81797761-df21-45b2-bc03-e13f6c2c954c", "Решение"},
                {"68cd1f1f-d948-4803-b684-76a65e05260d", "Справка"},
                {"785377b6-251b-48e8-8627-2e4de478d22e", "Судебная повестка"},
                {"70c67c16-be97-408e-8cde-b2f4e80b963b", "Телеграмма"},
                {"b07e1ee2-4450-4cdf-b797-72a6692e7fa4", "Телефонограмма"},
                {"a1a5944f-9d6c-484f-beab-2b3f9f532b99", "Тест"},
                {"e4f5fc4f-6e67-4f86-ac6a-68123f9e8253", "Требование"},
                {"9b329205-0eca-4b73-be71-b098fd6ebff6", "Уведомление"},
                {"f619a798-a695-4a8c-b2ef-8ff7d4a40510", "Указ"},
                {"ba615b41-4e67-4ab8-aefb-d2a65454813f", "Указание Минтранса"}
        };
        Stream.of(kinds).forEach(k -> {
            DocumentKind kind = new DocumentKind();
            kind.setId(UUID.fromString(k[0]));
            kind.setTitle(k[1]);
            documentKindRepository.save(kind);
        });
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

        IncomingDocument d1 = new IncomingDocument();
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
        d1.setCreateDate(Instant.parse("2019-03-03T00:00:00.00Z"));
        d1.setComment("Необходимо предоставить все нужные данные");
        d1.setPageCount(3);
        d1.setAppendixCount(1);
        d1.setCaseNumber("52 Запросы");
        d1.setRegistrar(UUID.fromString("c90b9c9f-ca1a-4b7c-bc77-3557c908f8d7"));
        d1.setRegistrarTitle("Енотина А. В.");
        d1.setDeliveryMethod("Почта");
        d1.setExecutionDate(Instant.parse("2019-08-14T00:00:00.00Z"));
        d1.setExecutor(UUID.fromString("5ca6d548-afa3-4c26-a72e-f0f19100e701"));
        d1.setExecutorTitle("Луков Б. П.");
        d1.setController(UUID.fromString("d259e840-0b34-4512-bc2e-5b5498dc4171"));
        d1.setControllerTitle("Карандашов К. Н.");
        d1.setCreator(UUID.fromString("c90b9c9f-ca1a-4b7c-bc77-3557c908f8d7"));
        d1.setCreatorTitle("Енотина А. В.");
        documents.add(d1);

        AttachContent attachContent1 = new AttachContent();
        byte[] content1 = "Тестовое содержимое 1".getBytes();
        attachContent1.setContent(content1);
        UUID attachContent1Id = UUID.fromString("e5f72dfb-cd7e-44ad-a5c9-8e9a4263eacd");
        attachContent1.setId(attachContent1Id);
        attachContentRepository.save(attachContent1);

        Attach attach1 = new Attach();
        attach1.setTitle("Тестовое вложение 1.txt");
        attach1.setSize(content1.length);
        attach1.setObjectId(d1.getId());
        attach1.setObjectType("document");
        attach1.setId(UUID.fromString("9d0b8a59-8bc7-4b49-b559-be326cd1337f"));
        attach1.setAttachContentId(attachContent1Id);
        attachRepository.save(attach1);

        AttachContent attachContent2 = new AttachContent();

        byte[] content2 = "Тестовое содержимое 2".getBytes();
        try {
            content2 = FileUtils.readFileToByteArray(new File("/Users/anatolii/Downloads/b7ac6d39ea4e2cfc905ebde4d06fabea.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        attachContent2.setContent(content2);
        UUID attachContent2Id = UUID.fromString("a24811ad-77cc-4881-950d-94a51876e21a");
        attachContent2.setId(attachContent2Id);
        attachContentRepository.save(attachContent2);

        Attach attach2 = new Attach();
        attach2.setTitle("Тестовое вложение 2.jpg");
        attach2.setSize(content2.length);
        attach2.setObjectId(d1.getId());
        attach2.setObjectType("document");
        attach2.setId(UUID.fromString("e36df78d-e4b1-4645-b729-e7459ae21c3f"));
        attach2.setAttachContentId(attachContent2Id);
        attachRepository.save(attach2);


        IncomingDocument d2 = new IncomingDocument();
        d2.setId(UUID.fromString("f0e0615a-8ab6-43bf-bf5d-a1259ec24ab4"));
        d2.setTitle("О направлении информации");
        d2.setDocumentType("Входящий");
        d2.setDocumentKind("Запрос");
        d2.setRegistrationDate(Instant.parse("2019-02-02T00:00:00.00Z"));
        d2.setDocumentNumber("144");
        d2.setStatus("Исполнение");
        d2.setCreateDate(Instant.now());
        documents.add(d2);

        IncomingDocument d3 = new IncomingDocument();
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

//        IncomingDocument incomingDocument = new IncomingDocument();
//        incomingDocument.setId(UUID.fromString("d595a410-52fc-4b87-af1f-0c73ed2e8924"));
        Task task = Task.builder()
                .executorId(UUID.fromString("722b151c-f9d7-4222-b541-cfc554695510"))
                .executorTitle("Ивашов В. Н.")
                .status("execution")
                .description("Проверить и выполнить")
                .author(UUID.fromString("5ca6d548-afa3-4c26-a72e-f0f19100e701"))
                .authorTitle("Луков Б. П.")
                .dueDate(Instant.parse("2019-10-14T00:00:00.00Z"))
                .type("execution")
                .document(d3)
                .controllerId(UUID.fromString("d259e840-0b34-4512-bc2e-5b5498dc4171"))
                .controllerTitle("Карандашов К. Н.")
                .build();
        taskRepository.save(task);

        Task task2 = Task.builder()
                .executorId(UUID.fromString("722b151c-f9d7-4222-b541-cfc554695510"))
                .executorTitle("Ивашов В. Н.")
                .status("execution")
                .description("Посмотреть")
                .author(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"))
                .authorTitle("Admin")
                .dueDate(Instant.parse("2019-10-14T00:00:00.00Z"))
                .type("execution")
                .document(d1)
                .controllerId(UUID.fromString("d259e840-0b34-4512-bc2e-5b5498dc4171"))
                .controllerTitle("Карандашов К. Н.")
                .build();
        taskRepository.save(task2);

        Task task3 = Task.builder()
                .executorId(UUID.fromString("580f62b3-7b96-4109-a321-dc7d24109a1a"))
                .status("execution")
                .dueDate(Instant.parse("2019-10-14T00:00:00.00Z"))
                .type("execution")
                .document(d1)
                .build();
        taskRepository.save(task3);

        Task task4 = Task.builder()
                .executorId(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"))
                .status("execution")
                .dueDate(Instant.parse("2019-10-14T00:00:00.00Z"))
                .type("execution")
                .document(d3)
                .build();
        taskRepository.save(task4);

        for (int i = 0; i < 100; i++) {
            String type = new String[]{"Входящий", "Исходящий", "Внутренний"}[new Random().nextInt(3)];
            Document d = null;

            switch (type) {
                case "Входящий": d = new IncomingDocument(); break;
                case "Исходящий": d = new OutgoingDocument(); break;
                case "Внутренний": d = new InnerDocument(); break;
            }
            d.setId(UUID.randomUUID());
            d.setTitle("Тестовый документ " + i);
            d.setDocumentType(type);
            d.setDocumentKind("Запрос");
            d.setStatus(new String[]{"На рассмотрении", "На согласовании", "На исполнении"}[new Random().nextInt(3)]);
            d.setCreator(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
            d.setCreateDate(Instant.now());

            documentRepository.save(d);
        }

        IncomingDocument td1 = new IncomingDocument();
        td1.setId(UUID.fromString("5b92f46c-5398-43d2-bd31-70fb9fe65579"));
        td1.setTitle("Тестовый - Нет полей");
        td1.setDocumentType("Тестовый");
        td1.setDocumentKind("Тест");
        td1.setStatus("Тест<Нет полей>");
        td1.setCreator(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
        td1.setCreateDate(Instant.now());
        documentRepository.save(td1);

        IncomingDocument td2 = new IncomingDocument();
        td2.setId(UUID.fromString("24112a41-1ace-4e30-ac69-65ddac5cc282"));
        td2.setTitle("Тестовый - Только чтение");
        td2.setDocumentType("Тестовый");
        td2.setDocumentKind("Тест");
        td2.setStatus("Тест<Только чтение>");
        td2.setCreator(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
        td2.setCreateDate(Instant.now());
        documentRepository.save(td2);

        IncomingDocument td3 = new IncomingDocument();
        td3.setId(UUID.fromString("789cd5c3-3428-495e-a2d4-f9132a1edb04"));
        td3.setTitle("Тестовый - Все поля");
        td3.setDocumentType("Тестовый");
        td3.setDocumentKind("Тест");
        td3.setStatus("Тест<Все редактируемые>");
        td3.setCreator(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
        td3.setCreateDate(Instant.now());
        documentRepository.save(td3);

        IncomingDocument td4 = new IncomingDocument();
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

        String[][] contragents = {{"8ce9ecce-c2d1-4ab4-b72a-48c6b711a241", "НТБ 32", "г. Москва, ул. Ленина, д. 122, оф. 255", "8-800-1234455", "НТБ 32"},
        {"63e5b8b9-d2e0-499b-9b2e-75fb02191ae7", "СТРИМ, автострахование", "г. Москва, ул. Маркса, д. 30, оф. 12", "8-800-1233322", "СТРИМ"},
        {"e4a238ef-2824-4374-88c1-92388a9b05eb", "АЛАТОО ИТЦ, электромонтажные работы", "г. Москва, ул. Ленина, д. 10, оф. 25", "8-800-1237777", "АЛАТОО ИТЦ, электромонтажные работы"},
        {"970cdb4c-cd9a-4944-9bf0-f78585220893", "РОСТРА, ОАО, страховая компания", "г. Москва, ул. Аксенова, д. 34, оф. 322", "8-800-1236655", "РОСТРА"},
        {"afc55206-1820-4da1-8bed-69705d202332", "ФЕНИКС, страховое агентство", "г. Москва, ул. Маркса, д. 45, оф. 56", "8-800-3334433", "ФЕНИКС"},
        {"4a2bec91-3bff-48bf-a052-19a150dde393", "УралСиб, ЗАО Страховая группа", "г. Москва, ул. Гагарина, д. 88, оф. 35", "8-800-1115577", "УралСиб"},
        {"60d3fde7-c523-4afb-8a56-e713775a3be1", "КАПИТАЛ, деловой центр", "г. Москва, ул. Буденого, д. 98, оф. 21", "8-800-7771122", "КАПИТАЛ"},
        {"9f470a59-4216-4958-b01c-a6093f6de5fb", "ПЛАНЕТА, кадастровый центр", "г. Москва, ул. Маркса, д. 211, оф. 54", "8-800-2224433", "ПЛАНЕТА"},
        {"65208064-e1ef-4bf5-be7b-39a3839668d3", "КВАЛИТЕТ, строительная экспертиза", "г. Москва, ул. Аксенова, д. 38, оф. 17", "8-800-1234545", "КВАЛИТЕТ"},
        {"f1eddfae-b6e1-43f6-b73a-31f3f14f517c", "ПРОФИНТЕР ООО, реклама", "г. Москва, ул. Кирова, д. 37, оф. 404", "8-800-1237700", "ПРОФИНТЕР"},
        {"55c3554b-99cc-4857-a964-7a2731a6ddfe", "ИСКУССТВО СТИЛЯ, имидж-агентство", "г. Москва, ул. Ленина, д. 101, оф. 508", "8-800-3330000", "ИСКУССТВО СТИЛЯ"},
        {"ccfde8f8-1563-4dea-81f9-4b15dd3633a0", "КАРАВЕЛЛА, бюро переводов", "г. Москва, ул. Королева, д. 58, оф. 323", "8-800-1232200", "КАРАВЕЛЛА"},
        {"57a1ecbb-e1cc-4ab1-b782-88e723b416dc", "КРОВПРОЕКТ, ООО, строительная организация", "г. Москва, ул. Гагарина, д. 11, оф. 203", "8-800-5553311", "Строительная организация ООО КРОВПРОЕКТ"},
        {"05a45146-6d08-4021-9248-014a0f373a51", "РОСТЕЛЕ, услуги связи, интернет-провайдер", "г. Москва, ул. Маркса, д. 50, оф. 101", "8-800-5551212", "РОСТЕЛЕ"},
        {"3704a2c0-015f-4c3f-b5ec-01556fc4a204", "АРХИТЕКТУРА, ООО, проектная организация", "г. Москва, ул. Гагарина, д. 30, оф. 307", "8-800-4448811", "Проектная организация ООО АРХИТЕКТУРА"},
        {"79876265-1e71-47c5-8fec-1f203e862b7c", "ЭНЕРГОГАРАНТ, страхование", "", "8-800-1232323", "ЭНЕРГОГАРАНТ"},
        {"648f37bd-f6b3-4795-8b5c-e7d6f70b8b40", "ГРАЖДАНПРОЕКТ, ООО, проектно-конструкторская фирма", "г. Москва, ул. Курчатова, д. 54, оф. 315", "8-800-1234545", "ГРАЖДАНПРОЕКТ"}};

        for (String[] contragent : contragents) {
            Contragent c = new Contragent();
            c.setId(UUID.fromString(contragent[0]));
            c.setTitle(contragent[1]);
            c.setAddress(contragent[2]);
            c.setPhone(contragent[3]);
            c.setFullName(contragent[4]);
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
    
    private void uploadEmployees() {
        List<User> list = new ArrayList<>();

        String[][] users = {{"580f62b3-7b96-4109-a321-dc7d24109a1a", "Поляков И. В.", null, null, "Иван", "Вячеславович", "Поляков"},
            {"722b151c-f9d7-4222-b541-cfc554695510", "Ивашов В. Н.", "ivashov", passwordEncoder.encode("123"), "Виктор", "Николаевич", "Ивашов"},
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
        {"9bb42bab-8965-49d2-b134-cec0d1505cc3", "Ivashova", null, null, "Анна", "Егоровна", "Ивашова"},
        {"c90b9c9f-ca1a-4b7c-bc77-3557c908f8d7", "Енотина А. В.", null, null, "Анна", "Вячеславовна", "Енотина"},
        {"39d3f6ae-ca8b-4814-a25e-57b6f253bde3", "Карандашова А. А.", null, null, "Антонина", "Аркадьевна", "Карандашова"},
        {"c5e87618-76c8-491f-bb25-aee010be5ab7", "Грибова В. М.", null, null, "Валентина", "Михайловна", "Грибова"},
        {"c0d2e91b-81f6-4560-b2c3-11654c6835ef", "Гарина А. А.", null, null, "Анна", "Аркадьевна", "Гарина"},
        {"cc1b2ba7-c245-4680-bc15-97b04be8b50e", "Грибова А. С.", null, null, "Антонина", "Сергеевна", "Грибова"},
        {"697660a9-8bed-4548-a15e-757282776ebb", "Карандашова А. В.", null, null, "Анна", "Вячеславовна", "Карандашова"},
        {"a4cff11c-936a-45e1-889f-f478f27fcc20", "Зверева А. М.", null, null, "Антонина", "Михайловна", "Зверева"},
//        {"52cc85b5-fab7-4365-a9cd-94afac1f0e8d", "Admin", null, null, "Admin", "Admin", "Admin"},
        {"a4fa069b-64ac-4a7e-ba5f-a3dc3e84c66e", "Карандашова Д. Г.", null, null, "Дарья", "Георгиевна", "Карандашова"}};

        for (String[] user : users) {
            User u = new User();
            u.setId(UUID.fromString(user[0]));

            if (user[2] != null && user[3] != null) {
                u.setUsername(user[2]);
                u.setPassword(user[3]);
                u.setEnabled(true);
            }

            u.setTitle(user[1]);
            u.setFirstName(user[4]);
            u.setMiddleName(user[5]);
            u.setLastName(user[6]);
            list.add(u);
        }

        List<String> roles = Arrays.asList(
                "ROLE_USER",
                "ROLE_DIC_DELIVERY_METHODS",
                "ROLE_DIC_DELIVERY_METHODS_SAVE",
                "ROLE_DIC_DELIVERY_METHODS_DEL",
                "ROLE_DIC_DOC_KINDS",
                "ROLE_DIC_DOC_KINDS_DEL",
                "ROLE_DIC_DOC_KINDS_SAVE",
                "ROLE_DIC_CONTRAGENT_PERSONS",
                "ROLE_DIC_CONTRAGENTS",
                "ROLE_DIC_CONTRAGENTS_SAVE",
                "ROLE_DIC_CONTRAGENTS_DEL",
                "ROLE_DIC_USERS",
                "ROLE_DIC_USERS_SAVE",
                "ROLE_DIC_USERS_RESET_PASS",
                "ROLE_USER_ROLES_VIEW",
                "ROLE_USER_ROLE_ADD",
                "ROLE_USER_ROLE_DEL",
                "ROLE_DIC_USERS_DEL");
        List<Authority> authorities = roles.stream().map(r -> {
            Authority authority = new Authority();
            authority.setUsername("admin");
            authority.setAuthority(r);
            return authority;
        }).collect(Collectors.toList());
        authorityRepository.saveAll(authorities);

        List<Authority> authorities2 = roles.stream().map(r -> {
            Authority authority = new Authority();
            authority.setUsername("ivashov");
            authority.setAuthority(r);
            return authority;
        }).collect(Collectors.toList());
        authorityRepository.saveAll(authorities2);


        User u = new User();
        u.setId(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
        u.setTitle("Admin");
        u.setFirstName("Admin");
        u.setLastName("Admin");
        u.setMiddleName("Admin");
        u.setUsername("admin");
        u.setEnabled(true);
        u.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(u);



        userRepository.saveAll(list);

        Group group = new Group();
        group.setGroupName("TEST_GROUP");
        group.setAuthorities(Arrays.asList("ROLE_GROUP_1", "ROLE_GROUP_2"));
        group.setMember(Arrays.asList("admin"));
        groupRepository.save(group);
    }
}
