package com.sokolsoft.ecm.core;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.stream.Collectors;

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

    public static String[] getAccessiblePropertyNames (Object source, Map<String, String> fieldsLevels) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            String propertyName = pd.getName();
            String level = fieldsLevels.get(propertyName);
            if (level == null || (!level.equals("2") && !level.equals("3"))) {
                emptyNames.add(propertyName);
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
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
}
