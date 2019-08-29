package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.model.IncomingDocument;
import com.sokolsoft.ecm.core.model.OutgoingDocument;
import com.sokolsoft.ecm.core.model.Task;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Bindable;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TaskRepositoryTestIT {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TaskRepository taskRepository;

    @Test
    public void test() {
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//
//        CriteriaQuery<Task> query = cb.createQuery(Task.class);
//        Root<Task> task = query.from(Task.class);
////        Join<Object, Object> document = task.join("documentId", JoinType.INNER);
////        query.multiselect(task, document);
//
//        List<Task> resultList = entityManager.createQuery(query).getResultList();
//


        List<Task> tasks = taskRepository.findAll((Specification<Task>) (root, query, criteriaBuilder) -> {

            Join<Task, Document> join = root.join("document");

            Bindable<Document> model = join.getModel();

            Path<Object> externalNumber = criteriaBuilder.treat(join, IncomingDocument.class).get("externalNumber");
            Path<Object> documentKind = criteriaBuilder.treat(join, OutgoingDocument.class).get("documentKind");

            return criteriaBuilder.or(criteriaBuilder.equal(externalNumber, "0012"), criteriaBuilder.equal(documentKind, "Справка"));
//            return criteriaBuilder.equal(documentKind, "Справка");
        });

        System.out.println(">>>>>>>>>>>>>>>>>>");
        System.out.println(tasks.size());
        tasks.forEach(System.out::println);
        System.out.println(">>>>>>>>>>>>>>>>>>");
    }

    @Data
    public static class DocumentAndTask {
        private Document document;
        private Task task;
    }
}
