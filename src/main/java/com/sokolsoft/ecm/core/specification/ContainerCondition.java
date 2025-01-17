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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 20.07.2016
 */
public class ContainerCondition extends Condition {
    private List<Condition> conditions;
    private ContainerOperation operation;

    public ContainerCondition() {
    }

    public ContainerCondition(ContainerOperation operation, Condition... condition) {
        this.operation = operation;
        this.conditions = new ArrayList<>(Arrays.asList(condition));
    }

    public List<Condition> getConditions() {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public ContainerOperation getOperation() {
        return operation;
    }

    public void setOperation(ContainerOperation operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "ContainerCondition{" +
            "conditions=" + conditions +
            ", operation=" + operation +
            '}';
    }
}
