package com.sokolsoft.ecm.core.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "com.sokolsoft.ecm.core.model.SUUIDGenerator"
    )
    private UUID id;

    private UUID documentId;

    private UUID listId;

    private UUID userId;

    private UUID executedByUser;

    private String type;

    private String status;

    private String stage;

    private String description;

    private Instant created;

    private UUID author;

    private Instant dueDate;

    private Instant executedDate;

    private String comment;

    private String result;

    private UUID controller;

    private Boolean primal;
}
