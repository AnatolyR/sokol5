package com.sokolsoft.ecm.core.web;


import com.sokolsoft.ecm.core.model.DeliveryMethod;
import com.sokolsoft.ecm.core.model.DocumentKind;
import com.sokolsoft.ecm.core.repository.DeliveryMethodRepository;
import com.sokolsoft.ecm.core.repository.DocumentKindRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class DeleteController {
    private DocumentKindRepository documentKindRepository;
    private DeliveryMethodRepository deliveryMethodRepository;

    @Autowired
    public DeleteController(DocumentKindRepository documentKindRepository, DeliveryMethodRepository deliveryMethodRepository) {
        this.documentKindRepository = documentKindRepository;
        this.deliveryMethodRepository = deliveryMethodRepository;
    }

    @DeleteMapping("/delete/documentKinds/{ids}")
    public void deleteDocumentKinds(@PathVariable String ids) {
        List<DocumentKind> documentKinds = Arrays.stream(ids.split(","))
                .map(id -> {
                    DocumentKind documentKind = new DocumentKind();
                    documentKind.setId(UUID.fromString(id));
                    return documentKind;
                }).collect(Collectors.toList());
        documentKindRepository.deleteAll(documentKinds);
    }

    @DeleteMapping("/delete/deliveryMethods/{ids}")
    public void deleteDeliveryMethods(@PathVariable String ids) {
        List<DeliveryMethod> documentKinds = Arrays.stream(ids.split(","))
                .map(id -> {
                    DeliveryMethod deliveryMethod = new DeliveryMethod();
                    deliveryMethod.setId(UUID.fromString(id));
                    return deliveryMethod;
                }).collect(Collectors.toList());
        deliveryMethodRepository.deleteAll(documentKinds);
    }
}
