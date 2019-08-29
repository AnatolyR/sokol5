package com.sokolsoft.ecm.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "com.sokolsoft.ecm.core.model.SUUIDGenerator"
    )
    private UUID id;

    @ManyToOne
    private Document document;

    private UUID listId;

    private UUID userId;

    private UUID executedByUser;

    private String type;

    private String status;

    private String stage;

    private String description;

    private Instant createDate;

    private UUID author;

    private String authorTitle;

    private Instant dueDate;

    private Instant executedDate;

    private String comment;

    private String result;

    private UUID controller;

    private Boolean primal;
}
