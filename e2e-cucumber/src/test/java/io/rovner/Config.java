package io.rovner;

import java.io.IOException;
import java.util.Properties;

public class Config {

    private static final Properties PROPERTIES = loadProperties();

    public static String getUiUrl() {
        return PROPERTIES.getProperty("ui.url", "https://demoblaze.com/");
    }

    public static String getApiUrl() {
        return PROPERTIES.getProperty("base.url", "https://api.demoblaze.com/");
    }

    public static String getBrowser() {
        return PROPERTIES.getProperty("browser", "chrome");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
