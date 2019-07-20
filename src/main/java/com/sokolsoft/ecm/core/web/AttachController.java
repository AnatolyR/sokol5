package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.model.Attach;
import com.sokolsoft.ecm.core.model.AttachContent;
import com.sokolsoft.ecm.core.model.User;
import com.sokolsoft.ecm.core.repository.AttachContentRepository;
import com.sokolsoft.ecm.core.repository.AttachRepository;
import com.sokolsoft.ecm.core.service.AccessRightsService;
import com.sokolsoft.ecm.core.service.DocumentService;
import com.sokolsoft.ecm.core.service.SecurityService;
import com.sokolsoft.ecm.core.service.UserService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

@Controller
public class AttachController {
    private AttachContentRepository attachContentRepository;

    private AttachRepository attachRepository;

    private UserService userService;

    private DocumentService documentService;

    private SecurityService securityService;

    private AccessRightsService accessRightsService;

    @Autowired
    public AttachController(AttachContentRepository attachContentRepository,
                            AttachRepository attachRepository,
                            UserService userService,
                            DocumentService documentService,
                            SecurityService securityService,
                            AccessRightsService accessRightsService) {
        this.attachContentRepository = attachContentRepository;
        this.attachRepository = attachRepository;
        this.userService = userService;
        this.documentService = documentService;
        this.securityService = securityService;
        this.accessRightsService = accessRightsService;
    }

//    @PostMapping
//    public void save(Attach attach) {
//        attachRepository.save(attach);
//    }
    

    @GetMapping(path = "/api/attaches/search/attachesByObjectId")
    @ResponseBody
    Page findByObjectIdEquals(@Param("objectId") String objectId, @Param("objectType") String objectType, Pageable p) {
        List<String> rolesForObject = accessRightsService.getRolesForObject(objectId, objectType);
        if (rolesForObject.contains("ROLE_ATTACH_LIST")) {
            return attachRepository.findByObjectIdEqualsAndObjectTypeEquals(UUID.fromString(objectId), objectType, p);
        } else {
            return Page.empty();
        }

    }

    @GetMapping(path = "/api/attaches/search/countByObjectId")
    @ResponseBody
    public long countByObjectId(@Param("objectId") String objectId, @Param("objectType") String objectType) {
        List<String> rolesForObject = accessRightsService.getRolesForObject(objectId, objectType);
        if (rolesForObject.contains("ROLE_ATTACH_COUNT")) {
            return attachRepository.countByObjectIdEqualsAndObjectTypeEquals(UUID.fromString(objectId), objectType);
        } else {
            return 0;
        }
    }

    @PostMapping("/api/file")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file, String objectId, String objectType) {
        try {
            List<String> rolesForObject = accessRightsService.getRolesForObject(objectId, objectType);
            if (!rolesForObject.contains("ROLE_ATTACH_ADD")) {
                throw new RuntimeException("No access rights to add file");
            }

            UUID contentId = UUID.randomUUID();
            InputStream inputStream = file.getInputStream();
            byte[] content = IOUtils.toByteArray(inputStream);

            AttachContent attachContent = new AttachContent();
            attachContent.setId(contentId);
            attachContent.setContent(content);

            attachContentRepository.save(attachContent);

            Attach attach = new Attach();
            attach.setId(UUID.randomUUID());
            attach.setTitle(file.getOriginalFilename());
            attach.setSize(content.length);
            attach.setObjectId(UUID.fromString(objectId));
            attach.setObjectType(objectType);

            User user = userService.getCurrentUser();
            attach.setAuthor(user.getId());
            attach.setAuthorTitle(user.getTitle());

            attach.setAttachContentId(contentId);
            attachRepository.save(attach);



            return attach.getId().toString();
        } catch (IOException e) {
            throw new RuntimeException("Cannot upload file");
        }
    }

    @GetMapping(value = "/api/file/{id}", produces = "application/octet-stream")
    @ResponseBody
    public byte[] download(@PathVariable("id") String id, ServletResponse response) {
        Attach attach = attachRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Cannot find attach"));

        List<String> rolesForObject = accessRightsService.getRolesForObject(attach.getObjectId().toString(),
                attach.getObjectType());
        if (!rolesForObject.contains("ROLE_ATTACH_CONTENT")) {
            throw new RuntimeException("No access right to view attach");
        }

        UUID attachContentId = attach.getAttachContentId();

        String fileName = attach.getTitle();
        String fileNameAdditional = URLEncoder.encode(fileName);
        fileNameAdditional = fileNameAdditional.replace("+", " ");
        Integer size = attach.getSize();

        byte[] content = attachContentRepository.findById(attachContentId)
                .orElseThrow(() -> new RuntimeException("Cannot find attach")).getContent();
        ((HttpServletResponse) response).setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + fileNameAdditional + "; size=" + size);

        return content;
    }

    //todo move to service
    //todo add delete
    //todo add transaction
    //todo add show or hide buttons depend on AR
    //todo update attach count in documents dynamically
}
