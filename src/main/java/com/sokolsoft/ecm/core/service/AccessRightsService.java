package com.sokolsoft.ecm.core.service;

import java.util.List;
import java.util.UUID;

public interface AccessRightsService {
    List<String> getRolesForObject(UUID objectId, String objectType);

    boolean isActionAvailable(UUID objectId, String objectType, String action);

    List<String> getActionsForObject(UUID fromString, String objectType);
}
