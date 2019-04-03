package com.sokolsoft.ecm;

import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.repository.DocumentRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class EcmApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EcmApplication.class, args);

        List<Document> documents = new ArrayList<>();

        Document d1 = new Document();
        d1.setId(UUID.fromString("c32a1bdd-d6a4-4a09-8e7b-249d6c7fd673"));
        d1.setTitle("Запрос даных для выполнения работ");
        d1.setDocumentType("Входящий");
        d1.setDocumentKind("Запрос");
        d1.setRegistrationDate(Instant.parse("2019-03-03T00:00:00.00Z"));
        d1.setDocumentNumber("131");
        d1.setStatus("На рассмотрении");
        d1.setAddressee(UUID.fromString("f48cc6f9-7f14-4f80-ab48-dbb909a44e0b"));
        d1.setAddresseeTitle("Петров И. И., зам. д.");
        d1.setAddresseeCopies(Arrays.asList(UUID.fromString("ce45b799-86da-489a-9ada-7d0567963847"), UUID.fromString("710fc8a3-78a5-4459-b38f-6dbcfbaf416b")));
        d1.setAddresseeCopiesTitles(Arrays.asList("Шишкин И. П., гл. инж.", "Звякин В. В., бух."));

        documents.add(d1);


        Document d2 = new Document();
        d2.setId(UUID.fromString("f0e0615a-8ab6-43bf-bf5d-a1259ec24ab4"));
        d2.setTitle("О направлении информации");
        d2.setDocumentType("Входящий");
        d2.setDocumentKind("Запрос");
        d2.setRegistrationDate(Instant.parse("2019-02-02T00:00:00.00Z"));
        d2.setDocumentNumber("144");
        d2.setStatus("На согласовании");
        documents.add(d2);

        Document d3 = new Document();
        d3.setId(UUID.fromString("d595a410-52fc-4b87-af1f-0c73ed2e8924"));
        d3.setTitle("Архивная справка");
        d3.setDocumentType("Входящий");
        d3.setDocumentKind("Справка");
        d3.setRegistrationDate(Instant.parse("2019-01-05T00:00:00.00Z"));
        d3.setDocumentNumber("145");
        d3.setStatus("На исполнении");
        documents.add(d3);


        context.getBean(DocumentRepository.class).saveAll(documents);
    }

    @Configuration
    @EnableWebSecurity
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/document**", "/document/**", "/createDocument/**", "/config**", "/folders/**")
                    .permitAll()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/**").authenticated();
        }
    }
}
