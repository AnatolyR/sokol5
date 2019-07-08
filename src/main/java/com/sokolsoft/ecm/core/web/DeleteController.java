package com.sokolsoft.ecm.core.web;


import com.sokolsoft.ecm.core.model.Contragent;
import com.sokolsoft.ecm.core.model.DeliveryMethod;
import com.sokolsoft.ecm.core.model.DocumentKind;
import com.sokolsoft.ecm.core.model.User;
import com.sokolsoft.ecm.core.repository.ContragentRepository;
import com.sokolsoft.ecm.core.repository.DeliveryMethodRepository;
import com.sokolsoft.ecm.core.repository.DocumentKindRepository;
import com.sokolsoft.ecm.core.repository.UserRepository;
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
    private ContragentRepository contragentRepository;
    private UserRepository userRepository;

    @Autowired
    public DeleteController(DocumentKindRepository documentKindRepository, DeliveryMethodRepository deliveryMethodRepository, ContragentRepository contragentRepository, UserRepository userRepository) {
        this.documentKindRepository = documentKindRepository;
        this.deliveryMethodRepository = deliveryMethodRepository;
        this.contragentRepository = contragentRepository;
        this.userRepository = userRepository;
    }

    @DeleteMapping("/api/delete/documentKinds/{ids}")
    public void deleteDocumentKinds(@PathVariable String ids) {
        List<DocumentKind> documentKinds = Arrays.stream(ids.split(","))
                .map(id -> {
                    DocumentKind documentKind = new DocumentKind();
                    documentKind.setId(UUID.fromString(id));
                    return documentKind;
                }).collect(Collectors.toList());
        documentKindRepository.deleteAll(documentKinds);
    }

    @DeleteMapping("/api/delete/deliveryMethods/{ids}")
    public void deleteDeliveryMethods(@PathVariable String ids) {
        List<DeliveryMethod> documentKinds = Arrays.stream(ids.split(","))
                .map(id -> {
                    DeliveryMethod deliveryMethod = new DeliveryMethod();
                    deliveryMethod.setId(UUID.fromString(id));
                    return deliveryMethod;
                }).collect(Collectors.toList());
        deliveryMethodRepository.deleteAll(documentKinds);
    }

    @DeleteMapping("/api/delete/contragents/{ids}")
    public void deleteContragents(@PathVariable String ids) {
        List<Contragent> contragents = Arrays.stream(ids.split(","))
                .map(id -> {
                    Contragent contragent = new Contragent();
                    contragent.setId(UUID.fromString(id));
                    return contragent;
                }).collect(Collectors.toList());
        contragentRepository.deleteAll(contragents);
    }

    @DeleteMapping("/api/delete/users/{ids}")
    public void deleteUsers(@PathVariable String ids) {
        List<User> users = Arrays.stream(ids.split(","))
                .map(id -> {
                    User user = new User();
                    user.setId(UUID.fromString(id));
                    return user;
                }).collect(Collectors.toList());
        userRepository.deleteAll(users);
    }
}
