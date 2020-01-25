package com.sokolsoft.ecm.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sokolsoft.ecm.core.Utils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.StreamSupport;

public abstract class ConfigServiceBase implements ConfigService {
    protected final ObjectMapper mapper;

    @Autowired
    public ConfigServiceBase(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonNode getPublicConfig(String configName) {
        JsonNode config = getPublicRawConfig(configName);
        config = processConfig(configName, config);
        return config;
    }

    private JsonNode processConfig(String configName, JsonNode config) {
        if (configName.startsWith("folderLists")) {
            config = processLists(config, "folders");
        } else if (configName.startsWith("admin/index")) {
            config = processLists(config, "admin");
        } else if (configName.startsWith("dictionaries/index")) {
            config = processLists(config, "dictionaries");
        }
        return config;
    }

    private JsonNode processLists(JsonNode config, String type) {
        ArrayNode filledFolders = mapper.createArrayNode();
        StreamSupport.stream(config.spliterator(), false).forEachOrdered(node -> {
            String folderName = node.asText();
            if ("--".equals(folderName)) {
                ObjectNode delimiterNode = mapper.createObjectNode();
                delimiterNode.put("type", "--");
                filledFolders.add(delimiterNode);
            } else {
                JsonNode folderConfig = getPublicRawConfig(type + "/" + folderName);
                if (folderConfig != null) {
                    filledFolders.add(folderConfig);
                    ArrayNode actions = ((ObjectNode) folderConfig).putArray("actions");
                    JsonNode privateConfig = getPrivateRawConfig(type + "/" + folderName);
                    if (privateConfig != null && privateConfig.has("actions")) {
                        privateConfig.findValue("actions").forEach(a -> {
                            if (a.get("secured") != null && Utils.checkAccess(a.get("secured").asText())) {
                                actions.add(a.get("id").asText());
                            }
                        });
                    }
                }
            }
        });
        return filledFolders;
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

    public JsonNode getPrivateConfig(String configName) {
        JsonNode config = getPrivateRawConfig(configName);
        config = processConfig(configName, config);
        return config;
    }

    protected abstract JsonNode getPublicRawConfig(String configName);

    protected abstract JsonNode getPrivateRawConfig(String configName);
}
