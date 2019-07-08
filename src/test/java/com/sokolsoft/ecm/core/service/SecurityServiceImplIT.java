package com.sokolsoft.ecm.core.service;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

public class SecurityServiceImplIT {
    SecurityServiceImpl securityService = new SecurityServiceImpl();

    @Before
    public void before() {
        securityService.setFile("classpath:/security/SecurityServiceImpl.test.txt");
    }

    @Test
    public void testGetFieldsRights() {
        Map<String, String> levels = securityService.getFieldsRights("Входящий",
                "Черновик", "USER");
        StringBuilder result = new StringBuilder();
        for (String key : levels.keySet().stream().sorted().collect(Collectors.toList())) {
            String level = levels.get(key);
            result.append(key).append(" = ").append(level).append(";\n");
            System.out.println(key + " = " + level);
        }
        assertEquals(result.toString(), "addressee = 3;\n" +
                "addresseeCopies = 2;\n" +
                "documentKind = 2;\n" +
                "registrationDate = 1;\n" +
                "title = 1;\n");
    }



    @Test
    public void testGetFieldsRightsForSeveralRoles() {
        Map<String, String> levels = securityService.getFieldsRights("Входящий",
                "Черновик", Arrays.asList("USER", "ADMIN"));
        for (String key : levels.keySet()) {
            String level = levels.get(key);
            System.out.println(key + " = " + level);
            assertEquals(level, "3");
        }
    }
}