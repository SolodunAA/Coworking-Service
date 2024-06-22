package app.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * class that is reading configuration parameters from app_config.conf file
 */
public class ConfigReader {
    /**
     * the name of the config file
     */

    private static final String CONFIG_FILE_NAME = "app_conf.conf";

    /**
     * method read properties from configuration file
     * @return properties from configuration file
     */
    public Properties readProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return properties;
    }
}
