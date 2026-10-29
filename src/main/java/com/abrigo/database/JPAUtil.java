package com.abrigo.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory FACTORY;

    static {
        Map<String, Object> properties = new HashMap<>();
        Properties prop = new Properties();

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            prop.load(fis);
            properties.put("jakarta.persistence.jdbc.url", prop.getProperty("db.url"));
            properties.put("jakarta.persistence.jdbc.user", prop.getProperty("db.user"));
            properties.put("jakarta.persistence.jdbc.password", prop.getProperty("db.password"));
        } catch (IOException e) {
            System.err.println("Arquivo config.properties não encontrado na raiz do projeto!");
        }

        properties.put("hibernate.archive.autodetection", "class");
        
        properties.put("hibernate.scanner", "org.hibernate.boot.archive.scan.internal.StandardScanner");

        FACTORY = Persistence.createEntityManagerFactory("soprap-pu", properties);
    }

    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }
}