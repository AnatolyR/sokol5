package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.model.Attach;
import com.sokolsoft.ecm.core.model.AttachContent;
import com.sokolsoft.ecm.core.repository.AttachContentRepository;
import com.sokolsoft.ecm.core.repository.AttachRepository;
import com.sokolsoft.ecm.core.service.AccessRightsService;
import com.sokolsoft.ecm.core.service.AttachService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class AttachController {
    private AttachContentRepository attachContentRepository;

    private AttachRepository attachRepository;

    private AccessRightsService accessRightsService;

    private AttachService attachService;

    @Autowired
    public AttachController(AttachContentRepository attachContentRepository,
                            AttachRepository attachRepository,
                            AccessRightsService accessRightsService,
                            AttachService attachService) {
        this.attachContentRepository = attachContentRepository;
        this.attachRepository = attachRepository;
        this.accessRightsService = accessRightsService;
        this.attachService = attachService;
    }

//    @PostMapping
//    public void save(Attach attach) {
//        attachRepository.save(attach);
//    }
    

    @GetMapping(path = "/api/attaches/search/attachesByObjectId")
    @ResponseBody
    public Page getAttaches(@Param("objectId") String objectId, @Param("objectType") String objectType, Pageable p) {
        List<String> rolesForObject = accessRightsService.getRolesForObject(UUID.fromString(objectId), objectType);
        if (rolesForObject.contains("ROLE_ATTACH_LIST")) {
            return attachRepository.findByObjectIdEqualsAndObjectTypeEquals(UUID.fromString(objectId), objectType, p);
        } else {
            return Page.empty();
        }

    }

    @GetMapping(path = "/api/attaches/search/countByObjectId")
    @ResponseBody
    public long countByObjectId(@Param("objectId") String objectId, @Param("objectType") String objectType) {
        List<String> rolesForObject = accessRightsService.getRolesForObject(UUID.fromString(objectId), objectType);
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
            InputStream inputStream = file.getInputStream();
            byte[] content = IOUtils.toByteArray(inputStream);

            UUID attachId = attachService.saveFile(content, file.getOriginalFilename(),
                    UUID.fromString(objectId),
                    objectType);

            return attachId.toString();
        } catch (IOException e) {
            throw new RuntimeException("Cannot upload file");
        }
    }

    @GetMapping(value = "/api/file/{id}", produces = "application/octet-stream")
    @ResponseBody
    public byte[] download(@PathVariable("id") String id, ServletResponse response) {
        UUID attachId = UUID.fromString(id);
        
        byte[] content = attachService.loadFile(attachId);
        Attach attach = attachRepository.findById(attachId)
                .orElseThrow(() -> new RuntimeException("Cannot find attach"));

        String fileName = attach.getTitle();
        String fileNameAdditional = URLEncoder.encode(fileName);
        fileNameAdditional = fileNameAdditional.replace("+", " ");
        Integer size = attach.getSize();

        ((HttpServletResponse) response).setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + fileNameAdditional + "; size=" + size);
        return content;
    }

    @DeleteMapping(value = "/api/delete/files/{ids}")
    @Transactional
    public void deleteFiles(@PathVariable String ids) {
        List<Attach> attaches = attachRepository.findAllById(Arrays.stream(ids.split(","))
                .map(UUID::fromString)
                .collect(Collectors.toList()));

        attaches.forEach(a -> {
            List<String> rolesForObject = accessRightsService.getRolesForObject(a.getObjectId(), a.getObjectType());
            if (!rolesForObject.contains("ROLE_ATTACH_DEL")) {
                throw new RuntimeException("No access rights to delete file");
            }
        });

        List<AttachContent> attachesContent = attaches.stream().map(Attach::getAttachContentId).map(id -> {
            AttachContent attachContent = new AttachContent();
            attachContent.setId(id);
            return attachContent;
        }).collect(Collectors.toList());

        attachRepository.deleteAll(attaches);
        attachContentRepository.deleteAll(attachesContent);
    }

    @GetMapping(path = "/api/attaches/availableActions")
    @ResponseBody
    public List<String> getAvailableActions(@Param("objectId") String objectId, @Param("objectType") String objectType) {
        List<String> rolesForObject = accessRightsService.getRolesForObject(UUID.fromString(objectId), objectType);
        List<String> actions = new ArrayList<>();
        if (rolesForObject.contains("ROLE_ATTACH_ADD")) {
            actions.add("add");
        }
        if (rolesForObject.contains("ROLE_ATTACH_DEL")) {
            actions.add("del");
        }
        if (rolesForObject.contains("ROLE_ATTACH_CONTENT")) {
            actions.add("view");
        }
        return actions;
    }
}
