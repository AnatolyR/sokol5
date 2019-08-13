package com.sokolsoft.ecm.core.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.sokolsoft.ecm.core.Utils;
import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.model.Task;
import com.sokolsoft.ecm.core.service.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
}
