package com.sokolsoft.ecm.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sokolsoft.ecm.core.repository.UserRepository;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A Document.
 */
@Entity
@Table(name = "document")
//@org.springframework.data.elasticsearch.annotations.Document(indexName = "document")
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IncomingDocument.class, name = "Входящий"),
        @JsonSubTypes.Type(value = IncomingDocument.class, name = "Тестовый"),
        @JsonSubTypes.Type(value = OutgoingDocument.class, name = "Исходящий"),
        @JsonSubTypes.Type(value = InnerDocument.class, name = "Внутренний")
})
public abstract class Document implements Serializable {

    @Id
    protected UUID id;

    protected UUID addressee;

    @TitleField(idField = "addressee", repository = UserRepository.class)
    protected String addresseeTitle;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    protected List<UUID> addresseeCopies;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    @TitleField(idField = "addresseeCopies", repository = UserRepository.class)
    protected List<String> addresseeCopiesTitles;

    protected String documentKind;

    protected String documentSubKind;

    protected Instant registrationDate;

    protected Instant createDate;

    protected String title;

    protected String comment;

    protected Integer pageCount;

    protected Integer appendixCount;

    protected String caseNumber;

    protected UUID registrar;

    @TitleField(idField = "registrar", repository = UserRepository.class)
    protected String registrarTitle;

    protected String documentNumber;

    protected String deliveryMethod;

    protected Instant executionDate;

    protected UUID executor;

    @TitleField(idField = "executor", repository = UserRepository.class)
    protected String executorTitle;

    protected UUID controller;

    @TitleField(idField = "controller", repository = UserRepository.class)
    protected String controllerTitle;

    protected String status;

    protected String documentType;

    protected UUID creator;

    @TitleField(idField = "creator", repository = UserRepository.class)
    protected String creatorTitle;

    private String documentGroup;
}
