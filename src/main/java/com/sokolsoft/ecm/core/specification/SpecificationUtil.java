/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.sokolsoft.ecm.core.specification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.metamodel.internal.EntityTypeImpl;
import org.hibernate.metamodel.internal.PluralAttributeImpl;
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 20.01.2017
 */
public class SpecificationUtil {
    public static Condition read(ArrayNode node) {
        Iterator<JsonNode> iterator = node.iterator();
        Condition condition = toCondition(null, iterator);
        return condition;
    }

    public static ContainerCondition toCondition(ContainerOperation operation, Iterator<JsonNode> iterator) {
        List<Condition> conditions = new ArrayList<>();
        while (iterator.hasNext()) {
            ObjectNode conditionNode = (ObjectNode) iterator.next();
            String conditionValue = conditionNode.get("condition") != null ? conditionNode.get("condition").asText() : "";
            if ("".equals(conditionValue)) {
                Condition c = toCondition(conditionNode);
                if (c != null) {
                    conditions.add(c);
                }
            } else if ("and_block".equals(conditionValue)) {
                ContainerCondition c = toCondition(ContainerOperation.AND, iterator);
                if (c != null && c.getConditions().size() > 0) {
                    conditions.add(c);
                }
            } else if ("or_block".equals(conditionValue)) {
                ContainerCondition c = toCondition(ContainerOperation.OR, iterator);
                if (c != null && c.getConditions().size() > 0) {
                    conditions.add(c);
                }
            } else if ("end_block".equals(conditionValue)) {
                break;
            }
        }
        if (conditions.size() == 1 && conditions.get(0) instanceof ContainerCondition) {
            return (ContainerCondition) conditions.get(0);
        } else if (conditions.size() > 0) {
            ContainerCondition condition = new ContainerCondition();
            condition.setOperation(operation != null ? operation : ContainerOperation.AND);
            condition.setConditions(conditions);
            return condition;
        } else {
            return null;
        }
    }

    public static Condition toCondition(ObjectNode conditionNode) {
        if (conditionNode.get("column") != null && !conditionNode.get("column").asText().isEmpty()
            && conditionNode.get("operation") != null && !conditionNode.get("operation").asText().isEmpty()) {
            ValueCondition condition = new ValueCondition();

            condition.setField(conditionNode.get("column").asText());
            condition.setOperation(Operation.valueOf(conditionNode.get("operation").asText()));
            condition.setValue(conditionNode.get("value") != null ? conditionNode.get("value").asText() : null);
            return condition;
        } else {
            return null;
        }
    }

    public static <T> org.springframework.data.jpa.domain.Specification<T> conditionToSpringSpecification(Condition condition, Class<T> c) {
        return conditionToSpringSpecification(condition, c, null);
    }

    public static <T> org.springframework.data.jpa.domain.Specification<T> conditionToSpringSpecification(Condition condition, Class<T> c, String joinAttribute) {
        org.springframework.data.jpa.domain.Specification<T> specification = (root, query, criteriaBuilder) -> {
            Join<Object, Object> join = null;
            if (joinAttribute != null) {
                join = root.join(joinAttribute);
            }

            return conditionToPredicate(condition, root, criteriaBuilder, c, (SingularAttributeJoin) join);
        };
        return specification;
    }

    public static Predicate conditionToPredicate(Condition condition, Root root, CriteriaBuilder criteriaBuilder, Class docClass, SingularAttributeJoin join) {

        if (condition instanceof ContainerCondition) {
            List<Predicate> subconditions = new ArrayList<>();
            ((ContainerCondition) condition).getConditions().forEach(c -> {
                Predicate p = conditionToPredicate(c, root, criteriaBuilder, docClass, join);
                if (p != null) {
                    subconditions.add(p);
                }
            });
            if (subconditions.size() == 0) {
                return null;
            }
            if (((ContainerCondition) condition).getOperation() == ContainerOperation.AND) {
                return criteriaBuilder.and(subconditions.toArray(new Predicate[subconditions.size()]));
            } else {
                return criteriaBuilder.or(subconditions.toArray(new Predicate[subconditions.size()]));
            }
        } else if (condition instanceof ValueCondition) {
            ValueCondition valueCondition = (ValueCondition) condition;
            String field = valueCondition.getField();

            Class joinClass = null;
            if (field.contains(")")) {
                String joinClassName = field.substring(1, field.indexOf(")"));
                try {
                    joinClass = Class.forName("com.sokolsoft.ecm.core.model." + joinClassName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                field = field.substring(field.indexOf(")") + 1);
            }

            boolean useJoin = false;
            if (field.contains(".")) {
                useJoin = true;
                field = field.substring(field.indexOf(".") + 1);
            }
            
            Attribute attr = useJoin
                    ? (
                            joinClass != null
                                    ? ((EntityTypeImpl) criteriaBuilder.treat(join, joinClass).getModel()).getAttribute(field)
                                    : ((EntityTypeImpl) join.getModel()).getAttribute(field))
                    : criteriaBuilder.treat(root, docClass).getModel().getAttribute(field);

            Expression path = attr instanceof SingularAttribute
                    ? (useJoin ? join.get((SingularAttribute) attr) : root.get((SingularAttribute) attr))
                    : (useJoin ? join.get((PluralAttribute) attr) : root.get((PluralAttribute) attr));
            Predicate predicate;

            if (valueCondition.getOperation() == Operation.EQUAL) {
                if (UUID.class.equals(attr.getJavaType())) {
                    predicate = criteriaBuilder.equal(path, UUID.fromString((String) valueCondition.getValue()));
                } else {
                    predicate = criteriaBuilder.equal(path, valueCondition.getValue());
                }
            } else if (valueCondition.getOperation() == Operation.NOT_EQUAL) {
                if (UUID.class.equals(attr.getJavaType())) {
                    predicate = criteriaBuilder.equal(path, UUID.fromString((String) valueCondition.getValue()));
                } else {
                    predicate = criteriaBuilder.equal(path, valueCondition.getValue());
                }
            } else if (valueCondition.getOperation() == Operation.LIKE && List.class.equals(attr.getJavaType()) && valueCondition.getValue() instanceof String) {
                Class elementType = ((PluralAttributeImpl) attr).getElementType().getJavaType();
                if (UUID.class.equals(elementType)) {
                    predicate = criteriaBuilder.isMember(UUID.fromString((String) valueCondition.getValue()), path);
                } else {
                    predicate = criteriaBuilder.isMember((String) valueCondition.getValue(), path);
                }
            } else if (valueCondition.getOperation() == Operation.FULLTEXTSEARCH && valueCondition.getValue() instanceof String) {
                predicate = criteriaBuilder.isTrue(criteriaBuilder.function("fts", Boolean.class, path, criteriaBuilder.literal(valueCondition.getValue())));
            } else if (valueCondition.getOperation() == Operation.LIKE && valueCondition.getValue() instanceof String) {
                predicate = criteriaBuilder.like(path, "%" + valueCondition.getValue() + "%");
            } else if (valueCondition.getOperation() == Operation.STARTS && valueCondition.getValue() instanceof String) {
                predicate = criteriaBuilder.like(path, valueCondition.getValue() + "%");
            } else if (valueCondition.getOperation() == Operation.ENDS && valueCondition.getValue() instanceof String) {
                predicate = criteriaBuilder.like(path, "%" + valueCondition.getValue());
            } else {
                return null;
            }
            return predicate;
        }
        return null;
    }
}
