package com.sokolsoft.ecm.core.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface ConfigService {
    JsonNode getPublicConfig(String configName);
    JsonNode getPrivateConfig(String configName);
}
