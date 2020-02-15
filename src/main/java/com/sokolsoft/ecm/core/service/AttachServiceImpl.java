package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.model.Attach;
import com.sokolsoft.ecm.core.model.AttachContent;
import com.sokolsoft.ecm.core.model.User;
import com.sokolsoft.ecm.core.repository.AttachContentRepository;
import com.sokolsoft.ecm.core.repository.AttachRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AttachServiceImpl implements AttachService {
    private AttachRepository attachRepository;

    private AttachContentRepository attachContentRepository;

    private AccessRightsService accessRightsService;

    private UserService userService;

    @Autowired
    public AttachServiceImpl(AttachRepository attachRepository,
                             AttachContentRepository attachContentRepository,
                             AccessRightsService accessRightsService,
                             UserService userService) {
        this.attachRepository = attachRepository;
        this.attachContentRepository = attachContentRepository;
        this.accessRightsService = accessRightsService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public UUID saveFile(byte[] content, String originalFileName, UUID objectId, String objectType) {
        List<String> rolesForObject = accessRightsService.getRolesForObject(objectId, objectType);
        if (!rolesForObject.contains("ROLE_ATTACH_ADD")) {
            throw new RuntimeException("No access rights to add file");
        }

        UUID contentId = UUID.randomUUID();

        AttachContent attachContent = new AttachContent();
        attachContent.setId(contentId);
        attachContent.setContent(content);

        attachContentRepository.save(attachContent);

        Attach attach = new Attach();
        attach.setId(UUID.randomUUID());
        attach.setTitle(originalFileName);
        attach.setSize(content.length);
        attach.setSizeStr(FileUtils.byteCountToDisplaySize(content.length));
        attach.setCreated(Instant.now());

        attach.setVersion(attachRepository.findByObjectIdEqualsAndObjectTypeEquals(objectId, objectType, null).getContent()
                .stream()
                .filter(a -> a.getTitle().equals(originalFileName))
                .map(Attach::getVersion)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .map(v -> v + 1)
                .orElse(1));

        attach.setObjectId(objectId);
        attach.setObjectType(objectType);

        User user = userService.getCurrentUser();
        attach.setAuthor(user.getId());
        attach.setAuthorTitle(user.getTitle());

        attach.setAttachContentId(contentId);
        attachRepository.save(attach);

        return attach.getId();
    }

    @Override
    public byte[] loadFile(UUID attachId) {
        Attach attach = attachRepository.findById(attachId)
                .orElseThrow(() -> new RuntimeException("Cannot find attach"));

        List<String> rolesForObject = accessRightsService.getRolesForObject(attach.getObjectId(),
                attach.getObjectType());
        if (!rolesForObject.contains("ROLE_ATTACH_CONTENT")) {
            throw new RuntimeException("No access right to view attach");
        }

        UUID attachContentId = attach.getAttachContentId();
        byte[] content = attachContentRepository.findById(attachContentId)
                .orElseThrow(() -> new RuntimeException("Cannot find attach")).getContent();

        return content;
    }
}
