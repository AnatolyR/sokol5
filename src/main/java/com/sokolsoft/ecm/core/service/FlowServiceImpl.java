package com.sokolsoft.ecm.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.model.Task;
import com.sokolsoft.ecm.core.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class FlowServiceImpl implements FlowService {
    private final ConfigService configService;

    private final DocumentService documentService;

    private final AccessRightsService accessRightsService;

    private final UserService userService;

    private final TaskRepository taskRepository;

    @Override
    public List<Action> getActions(UUID documentId) {
        Document document = documentService.getDocument(documentId);
        String documentType = document.getDocumentType();

        JsonNode flow = configService.getPrivateConfig("flows/" + documentType);

        List<String> roles = accessRightsService.getRolesForObject(document.getId(), "document");

        String documentStatus = document.getStatus();
        List<Action> actions = StreamSupport.stream(flow.get("states").spliterator(), false)
                .filter(s -> s.get("title").asText().equals(documentStatus))
                .filter(s -> s.get("actions") != null)
                .flatMap(s -> StreamSupport.stream(s.get("actions").spliterator(), false))
//                .filter(a -> Utils.checkAccess(a.get("secured").asText(), roles))
                .map(a -> Action.builder()
                        .id(a.get("id").asText())
                        .title(a.get("title").asText())
                        .form(a.get("form") != null ? a.get("form").asText() : null)
                        .build())
                .collect(Collectors.toList());

        return actions;
    }

    @Override
    public void executeAction(ExtendedAction action) {
        UUID documentId = action.getDocumentId();
        String actionId = action.getActionId();

        Document document = documentService.getDocument(documentId);
        String documentType = document.getDocumentType();

        JsonNode flow = configService.getPrivateConfig("flows/" + documentType);

        //todo use
        List<String> roles = accessRightsService.getRolesForObject(document.getId(), "document");

        String documentStatus = document.getStatus();

        JsonNode state = StreamSupport.stream(flow.get("states").spliterator(), false)
                .filter(s -> s.get("title").asText().equals(documentStatus))
                .findFirst().orElseThrow(() -> new RuntimeException("Cannot find state"));

        JsonNode actionObject = StreamSupport.stream(state.get("actions").spliterator(), false)
                .filter(a -> a.get("id").asText().equals(actionId))
                .findFirst().orElseThrow(() -> new RuntimeException("Cannot find action"));

        switch (actionId) {
            case "sign":
                break;
            case "doresolution":
                moveToExecution(action, flow, documentId);
                break;
            default:
                moveToState(actionObject.get("state").asText(), flow, documentId);
        }
    }

    private void moveToState(String stateId, JsonNode flow, UUID documentId) {
        JsonNode state = StreamSupport.stream(flow.get("states").spliterator(), false)
                .filter(s -> s.get("id").asText().equals(stateId))
                .findFirst().orElseThrow(() -> new RuntimeException("Cannot find state"));

        documentService.moveDocumentToState(documentId, state.get("title").asText());
    }

    private void moveToExecution(ExtendedAction action, JsonNode flow, UUID documentId) {
        List<Task> tasks = new ArrayList<>();

        //todo check mandatory fields
        Task mainExecutorTask = new Task();
        mainExecutorTask.setType("execution");
        mainExecutorTask.setAuthor(userService.getCurrentUser().getId());
        mainExecutorTask.setCreated(Instant.now());
        mainExecutorTask.setDescription(action.getNote());
        mainExecutorTask.setDocumentId(action.getDocumentId());
        mainExecutorTask.setDueDate(action.getExecutionDate());
        mainExecutorTask.setUserId(action.getMainExecutor());
        mainExecutorTask.setStatus("execution");
        mainExecutorTask.setController(action.getController());
        mainExecutorTask.setPrimal(true);
        tasks.add(mainExecutorTask);

        action.getExecutors().forEach(e -> {
            Task task = new Task();
            task.setType("execition");
            task.setAuthor(userService.getCurrentUser().getId());
            task.setCreated(Instant.now());
            task.setDescription(action.getNote());
            task.setDocumentId(action.getDocumentId());
            task.setDueDate(e.getExecutionDate());
            task.setUserId(e.getExecutor());
            task.setStatus("execution");
            task.setController(e.getController());
            task.setPrimal(false);
            tasks.add(task);
        });

        taskRepository.saveAll(tasks);

        moveToState("execution", flow, documentId);
    }
}
