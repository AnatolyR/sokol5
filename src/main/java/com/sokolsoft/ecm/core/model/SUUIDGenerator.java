package com.sokolsoft.ecm.core.model;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class SUUIDGenerator extends UUIDGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        try {
            Method idField = object.getClass().getMethod("getId");
            if (idField.getReturnType().equals(UUID.class)) {
                if (idField.invoke(object) != null) {
                    return (Serializable) idField.invoke(object);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return super.generate(session, object);
    }
}
