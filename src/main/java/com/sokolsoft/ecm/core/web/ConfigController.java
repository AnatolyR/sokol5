package com.sokolsoft.ecm.core.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.sokolsoft.ecm.core.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {
    private final ConfigService configService;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping(value = "/api/config", produces = "application/json")
    public JsonNode getConfig(String configName) {
        if (configName == null || configName.split("/").length > 2 || !configName.replace("/", "").matches("^[a-zA-Z]+$")) {
            throw new RuntimeException("Empty or wrong config id");
        }
        JsonNode config = configService.getPublicConfig(configName);
        return config;
    }
}
