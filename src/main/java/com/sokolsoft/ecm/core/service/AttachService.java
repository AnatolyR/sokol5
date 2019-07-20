package com.sokolsoft.ecm.core.service;

import java.util.UUID;

public interface AttachService {
    UUID saveFile(byte[] content, String originalFileName, UUID objectId, String objectType);

    byte[] loadFile(UUID attachId);
}
