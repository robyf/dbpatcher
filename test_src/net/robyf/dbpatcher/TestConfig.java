package net.robyf.dbpatcher;

import java.io.IOException;
import java.util.Properties;

public final class TestConfig {

    private static Properties config = null;

    private TestConfig() {
    }

    private static void readProperties() {
        if (config == null) {
            try {
                config = new Properties();
                config.load(TestConfig.class.getResourceAsStream("/test/test.properties"));
            } catch (IOException ioe) {
                throw new RuntimeException("Error reading test.properties", ioe);
            }
        }
    }

    public static String getDatabaseName() {
        readProperties();
        return config.getProperty("database.name");
    }

    public static String getUsername() {
        readProperties();
        return config.getProperty("username");
    }

    public static String getPassword() {
        readProperties();
        return config.getProperty("password");
    }

}
