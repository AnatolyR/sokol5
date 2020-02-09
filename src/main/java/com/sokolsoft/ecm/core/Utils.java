package com.sokolsoft.ecm.core;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.*;

public class Utils {
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static String[] getNotAccessibleWritablePropertyNames(Object source, Map<String, String> fieldsLevels) {
        return getNotAccessiblePropertyNames(source, fieldsLevels, true);
    }

    private static String[] getNotAccessiblePropertyNames(Object source, Map<String, String> fieldsLevels, boolean writable) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            String propertyName = pd.getName();
            String level = fieldsLevels.get(propertyName);
            if (level == null) {
                level = fieldsLevels.get("*");
            }
            
            if (level == null || level.equals("0") || (writable && level.equals("1"))) {
                emptyNames.add(propertyName);
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static String[] getNotAccessibleReadablePropertyNames(Object source, Map<String, String> fieldsLevels) {
        return getNotAccessiblePropertyNames(source, fieldsLevels, false);
    }

    public static boolean checkAccess(String secured) {
        ExpressionParser parser = new SpelExpressionParser();

        Expression exp = parser.parseExpression(secured);
        Boolean result = (Boolean) exp.getValue(new Object() {
            public boolean hasRole(String role) {
                return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> ((GrantedAuthority) a).getAuthority().equals(role));
            }
        });
        return result;
    }

    public static boolean checkAccess(String secured, List<String> roles) {
        ExpressionParser parser = new SpelExpressionParser();

        Expression exp = parser.parseExpression(secured);
        Boolean result = (Boolean) exp.getValue(new Object() {
            public boolean hasRole(String role) {
                return roles.stream().anyMatch(r -> r.equals(role));
            }
        });
        return result;
    }

    public static List<Field> getFields(Class cl) {
        List<Field> fields = new ArrayList<>();
        do {
            fields.addAll(Arrays.asList(cl.getDeclaredFields()));
            cl = cl.getSuperclass();
        } while (cl != null);
        return fields;
    }
}
