package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.model.Task;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface FlowService {

    List<Action> getActions(UUID documentId);

    void executeAction(ExtendedAction action);

    Page<Task> getTasks(UUID documentId);

    List<String> getAvailableActions(String objectId, String objectType);

    void deleteTasks(List<String> ids);

    @Data
    @Builder
    public static class Action {
        private String id;
        private String title;
        private String form;
    }

    @Data
    @Builder
    public static class ExtendedAction {
        private UUID documentId;
        private String actionId;
        private UUID controller;
        private Instant executionDate;
        private UUID mainExecutor;
        private String note;
        private List<Executor> executors;
    }

    @Data
    public static class Executor {
        private UUID controller;
        private Instant executionDate;
        private UUID executor;
    }
}
