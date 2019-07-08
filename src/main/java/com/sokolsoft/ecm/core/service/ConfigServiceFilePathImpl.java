package com.sokolsoft.ecm.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Profile("dev")
public class ConfigServiceFilePathImpl extends ConfigServiceBase {
    private String configPath;

    @Value( "${sokol.configurationPath}" )
    public void setConfigPath(String configPath) {
        if (!configPath.endsWith("/")) {
            configPath += "/";
        }
        this.configPath = configPath;
    }

    public ConfigServiceFilePathImpl(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    protected JsonNode getPublicRawConfig(String configName) {
        File configFile = new File(configPath + "public/" +configName + ".json");
        return getJsonNode(configName, configFile);
    }

    @Override
    protected JsonNode getPrivateRawConfig(String configName) {
        File configFile = new File(configPath + "private/" +configName + ".json");
        return getJsonNode(configName, configFile);
    }

    private JsonNode getJsonNode(String configName, File configFile) {
        if (!configFile.exists()) {
            return null;
        }
        try {
            JsonNode config = mapper.readTree(configFile);
            return config;
        } catch (IOException e) {
            throw new RuntimeException("Can not read config " + configName, e);
        }
    }
}
