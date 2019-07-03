package com.sokolsoft.ecm.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sokolsoft.ecm.core.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.stream.StreamSupport;

@Service
public class ConfigServiceFilePathImpl implements ConfigService {
    private String configPath;

    private final ObjectMapper mapper;

    @Autowired
    public ConfigServiceFilePathImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Value( "${sokol.configurationPath}" )
    public void setConfigPath(String configPath) {
        if (!configPath.endsWith("/")) {
            configPath += "/";
        }
        this.configPath = configPath;
    }

    public JsonNode getPublicConfig(String configName) {
        JsonNode config = getPublicRawConfig(configName);
        config = processConfig(configName, config);
        return config;
    }

    private JsonNode processConfig(String configName, JsonNode config) {
        if (configName.startsWith("folderLists")) {
            config = processFolderLists(config);
        } else if (configName.startsWith("dictionaries/index")) {
            config = processDictionaryLists(config);
        }
        return config;
    }

    private JsonNode processDictionaryLists(JsonNode config) {
        ArrayNode filledDictionaries = mapper.createArrayNode();
        StreamSupport.stream(config.spliterator(), false).forEachOrdered(node -> {
            String folderName = node.asText();
            if ("--".equals(folderName)) {
                ObjectNode delimiterNode = mapper.createObjectNode();
                delimiterNode.put("type", "--");
                filledDictionaries.add(delimiterNode);
            } else {
                JsonNode folderConfig = getPublicRawConfig("dictionaries/" + folderName);
                if (folderConfig != null) {
                    filledDictionaries.add(folderConfig);
                    ArrayNode actions = ((ObjectNode) folderConfig).putArray("actions");
                    JsonNode privateConfig = getPrivateRawConfig("dictionaries/" + folderName);
                    if (privateConfig != null) {
                        privateConfig.get("actions").forEach(a -> {
                            if (Utils.checkAccess(a.get("secured").asText())) {
                                actions.add(a.get("id").asText());
                            }
                        });
                    }
                }
            }
        });
        return filledDictionaries;
    }

    private JsonNode processFolderLists(JsonNode config) {
        ArrayNode filledFolders = mapper.createArrayNode();
        StreamSupport.stream(config.spliterator(), false).forEachOrdered(node -> {
            String folderName = node.asText();
            JsonNode folderConfig = getPublicRawConfig("folders/" + folderName);
            if (folderConfig != null) {
                filledFolders.add(folderConfig);
            }
        });
        return filledFolders;
    }

    private JsonNode getPublicRawConfig(String configName) {
        File configFile = new File(configPath + "public/" +configName + ".json");
        return getJsonNode(configName, configFile);
    }

    public JsonNode getPrivateConfig(String configName) {
        JsonNode config = getPrivateRawConfig(configName);
        config = processConfig(configName, config);
        return config;
    }

    private JsonNode getPrivateRawConfig(String configName) {
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
