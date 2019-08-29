package com.sokolsoft.ecm.core.model;

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
public abstract class Document implements Serializable {

    @Id
    protected UUID id;

    protected UUID addressee;

    protected String addresseeTitle;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    protected List<UUID> addresseeCopies;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
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

    protected String registrarTitle;

    protected String documentNumber;

    protected String deliveryMethod;

    protected Instant executionDate;

    protected UUID executor;

    protected String executorTitle;

    protected UUID controller;

    protected String controllerTitle;

    protected String status;

    protected String documentType;

    protected UUID creator;

    protected String creatorTitle;

    private String documentGroup;
}
