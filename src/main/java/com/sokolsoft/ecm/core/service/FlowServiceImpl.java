package com.sokolsoft.ecm.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sokolsoft.ecm.core.SokolException;
import com.sokolsoft.ecm.core.SokolSecurityException;
import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.model.Task;
import com.sokolsoft.ecm.core.repository.DocumentRepository;
import com.sokolsoft.ecm.core.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
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

    private final DocumentRepository documentRepository;

    @Override
    public List<Action> getActions(UUID documentId) {
        Document document = documentService.getDocument(documentId);
        String documentType = document.getDocumentType();

        JsonNode flow = configService.getPrivateConfig("flows/" + documentType);
        if (flow == null) {
            return Collections.emptyList();
        }

        List<String> availableActions = accessRightsService.getActionsForObject(document.getId(), "document");

        String documentStatus = document.getStatus();
        List<Action> actions = flow.get("states") != null
                ? StreamSupport.stream(flow.get("states").spliterator(), false)
                .filter(s -> s.get("title").asText().equals(documentStatus))
                .filter(s -> s.get("actions") != null)
                .flatMap(s -> StreamSupport.stream(s.get("actions").spliterator(), false))
//                .filter(a -> Utils.checkAccess(a.get("secured").asText(), roles))
                .filter(a -> availableActions.contains("@" + a.get("id").asText()))
                .map(a -> Action.builder()
                        .id(a.get("id").asText())
                        .title(a.get("title").asText())
                        .form(a.get("form") != null ? a.get("form").asText() : null)
                        .build())
                .collect(Collectors.toList())
                : Collections.emptyList();

        if (availableActions.contains("@edit")) {
            actions = new ArrayList<>(actions);
            actions.add(Action.builder()
                    .id("edit")
                    .build());
        }

        return actions;
    }

    @Override
    public void executeAction(ExtendedAction action) {
        UUID documentId = action.getDocumentId();
        String actionId = action.getActionId();

        if (!accessRightsService.isActionAvailable(documentId, "document", "@" + actionId)) {
            throw new SokolSecurityException("Нет прав на выполнение действия");
        }

        Document document = documentService.getDocument(documentId);
        String documentType = document.getDocumentType();

        JsonNode flow = configService.getPrivateConfig("flows/" + documentType);

        String documentStatus = document.getStatus();

        JsonNode state = StreamSupport.stream(flow.get("states").spliterator(), false)
                .filter(s -> s.get("title").asText().equals(documentStatus))
                .findFirst().orElseThrow(() -> new RuntimeException("Cannot find state"));

        JsonNode actionObject = StreamSupport.stream(state.get("actions").spliterator(), false)
                .filter(a -> a.get("id").asText().equals(actionId))
                .findFirst().orElse(null);

        switch (actionId) {
            case "sign":
                break;
            case "doresolution":
                moveToExecution(action, flow, documentId);
                break;
            case "addtasks":
                moveToExecution(action, flow, documentId);
                break;
            case "createExecutionReport":
                createExecutionReport(action, flow, documentId);
                break;
            default:
                moveToState(actionObject.get("state").asText(), flow, documentId);
        }
    }

    private Task getUserCurrentTask(UUID documentId, String taskType, String taskStatus) {
        UUID userId = userService.getCurrentUser().getId();
        return taskRepository.findAllByDocumentId(documentId).stream()
                .filter(t -> (taskStatus == null || t.getType().equals(taskType))
                        && t.getExecutorId().equals(userId)
                        && t.getStatus().equals(taskStatus)).findFirst().orElse(null);
    }

    private void createExecutionReport(ExtendedAction action, JsonNode flow, UUID documentId) {
        Task task = getUserCurrentTask(documentId, "execution", "execution");
        if (task != null) {
            task.setComment(action.getNote());
            task.setStatus("done");
            taskRepository.save(task);

            if (taskRepository.findAllByDocumentId(documentId).stream()
                    .noneMatch(t -> t.getType().equals("execution")
                    && t.getStatus().equals("execution"))) {
                documentService.moveDocumentToState(documentId, "Исполнено");
            }
        } else {
            throw new SokolException("101", "Нет задачи для выполняемого действия");
        }
    }

    @Override
    public Page<Task> getTasks(UUID documentId) {
        if (!accessRightsService.isActionAvailable(documentId, "document", "@viewtasks")) {
            throw new SokolSecurityException("Нет прав на выполнение действия");
        }
        Page<Task> tasks = new PageImpl<>(taskRepository.findAllByDocumentId(documentId));
        tasks.forEach(t -> {
            switch (t.getStatus()) {
                case "execution":
                    t.setStatusTitle("Исполнение");
                    break;
                case "done":
                    t.setStatusTitle("Исполнено");
                    break;
            }
        });
        return tasks;
    }

    @Override
    public List<String> getAvailableActions(String objectId, String objectType) {
        List<String> actions = accessRightsService.getActionsForObject(UUID.fromString(objectId), objectType);
        List<String> resultActions = new ArrayList<>();
        if (actions.contains("@addtasks")) {
            resultActions.add("add");
        }
        if (actions.contains("@deleteTask")) {
            resultActions.add("del");
        }
        if (actions.contains("@editTask")) {
            resultActions.add("edit");
        }
        return resultActions;
    }

    @Override
    public void deleteTasks(List<String> ids) {
        ids.stream()
                .map(UUID::fromString)
                .map(taskRepository::findById)
                .map(Optional::get)
                .forEach(t -> {
                    if (!accessRightsService.isActionAvailable(t.getDocument().getId(), "document", "@deleteTask")) {
                        throw new SokolSecurityException("Нет прав на удаление задачи");
                    }
                    taskRepository.delete(t);
                });
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

//        if (!"addtasks".equals(action.getActionId())) {
//            Task mainExecutorTask = new Task();
//            mainExecutorTask.setType("execution");
//            mainExecutorTask.setAuthor(userService.getCurrentUser().getId());
//            mainExecutorTask.setCreateDate(Instant.now());
//            mainExecutorTask.setDescription(action.getNote());
//            mainExecutorTask.setDocument(documentRepository.getOne(action.getDocumentId()));
//            mainExecutorTask.setDueDate(action.getExecutionDate());
//            mainExecutorTask.setExecutorId(action.getMainExecutor());
//            mainExecutorTask.setStatus("execution");
//            mainExecutorTask.setControllerId(action.getController());
//            mainExecutorTask.setPrimal(true);
//            tasks.add(mainExecutorTask);
//        }
        List<Task> currentTasks = taskRepository.findAllByDocumentId(documentId).stream()
                .filter(t -> t.getType().equals("execution"))
                .collect(Collectors.toList());

        action.getExecutors().forEach(e -> {
            if (currentTasks.stream().anyMatch(t -> t.getExecutorId().equals(e.getExecutor()))) {
                throw new SokolException("102", "Исполнитель с ИД '" + e.getExecutor() + "' уже есть в спике исполнителей");
            }
            if (e.getExecutor() == null) {
                throw new SokolException("103", "Не указан исполнитель");
            }

            Task task = new Task();
            task.setType("execution");
            task.setAuthor(userService.getCurrentUser().getId());
            task.setCreateDate(Instant.now());
            task.setDescription(action.getNote());
            task.setDocument(documentRepository.getOne(action.getDocumentId()));
            task.setDueDate(e.getExecutionDate());
            task.setExecutorId(e.getExecutor());
            task.setStatus("execution");
            task.setControllerId(e.getController());
            task.setPrimal(false);

            documentService.fillTitles(task);

            tasks.add(task);
        });

        taskRepository.saveAll(tasks);

        if ("doresolution".equals(action.getActionId())) {
            moveToState("execution", flow, documentId);
        }
    }
}
