package com.sokolsoft.ecm.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Profile("prod")
public class ConfigServiceClasspathImpl extends ConfigServiceBase {
    public ConfigServiceClasspathImpl(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    protected JsonNode getPublicRawConfig(String configName) {
        return getJsonNode("/configs/public/", configName);
    }

    @Override
    protected JsonNode getPrivateRawConfig(String configName) {
        return getJsonNode("/configs/private/", configName);
    }

    private JsonNode getJsonNode(String path, String configName) {
        InputStream configStream = ConfigServiceClasspathImpl.class.getResourceAsStream(path + configName + ".json");
        if (configStream == null) {
            return null;
        }
        try {
            String content = IOUtils.toString(configStream, "utf-8");
            JsonNode config = mapper.readTree(content);
            return config;
        } catch (IOException e) {
            throw new RuntimeException("Can not read config " + configName, e);
        }
    }
}
