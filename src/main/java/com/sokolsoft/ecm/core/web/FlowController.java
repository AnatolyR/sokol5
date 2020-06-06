package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.model.Task;
import com.sokolsoft.ecm.core.service.FlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class FlowController {
    private final FlowService flowService;

    @GetMapping("/api/document/{documentId}/actions")
    @ResponseBody
    public List<FlowService.Action> getActions(@PathVariable("documentId") String documentId) {
        return flowService.getActions(UUID.fromString(documentId));
    }

    @PostMapping("/api/document/{documentId}/actions/{actionId}")
    public void executeAction(@PathVariable("documentId") String documentIdStr,
                              @PathVariable("actionId") String actionId) {
        UUID documentId = UUID.fromString(documentIdStr);
        flowService.executeAction(FlowService.ExtendedAction.builder()
                .documentId(documentId)
                .actionId(actionId)
                .build());

    }

    @PostMapping("/api/document/{documentId}/actions")
    public void executeExtendedAction(@RequestBody FlowService.ExtendedAction action) {
        flowService.executeAction(action);
    }

    @GetMapping("/api/document/{documentId}/tasks")
    public Page<Task> getTasks(@PathVariable("documentId") UUID documentId) {
        return flowService.getTasks(documentId);
    }

    @GetMapping(path = "/api/tasks/availableActions")
    @ResponseBody
    public List<String> getAvailableActions(@Param("objectId") String objectId, @Param("objectType") String objectType) {
        return flowService.getAvailableActions(objectId, objectType);
    }

    @DeleteMapping(value = "/api/delete/tasks/{ids}")
    @Transactional
    public void deleteFiles(@PathVariable String ids) {
        flowService.deleteTasks(Arrays.asList(ids.split(",")));
    }
}
