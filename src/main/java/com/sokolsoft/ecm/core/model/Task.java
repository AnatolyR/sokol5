package com.sokolsoft.ecm.core.model;

import com.sokolsoft.ecm.core.repository.UserRepository;
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

    private UUID executorId;

    @TitleField(idField = "executorId", repository = UserRepository.class)
    private String executorTitle;

    private UUID executedByUser;

    private String type;

    private String status;

    private String statusTitle;

    private String stage;

    private String description;

    private Instant createDate;

    private UUID author;

    @TitleField(idField = "author", repository = UserRepository.class)
    private String authorTitle;

    private Instant dueDate;

    private Instant executedDate;

    private String comment;

    private String controllerComment;

    private String result;

    private UUID controllerId;

    @TitleField(idField = "controllerId", repository = UserRepository.class)
    private String controllerTitle;

    private Boolean primal;

    @Transient
    private Boolean isControlled;

    @Transient
    private Boolean isCurrentUserTask;
}
