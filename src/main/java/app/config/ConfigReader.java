package app.config;

import java.io.*;
import java.util.Properties;

/**
 * class that is reading configuration parameters from app_config.conf file
 */
public class ConfigReader {
    /**
     * the name of the config file
     */

    private static final String CONFIG_FILE_PATH = "src/main/resources/app_conf.conf";
    public static final String TEST_CONFIG_PATH = "src/test/resources/test_conf.conf";


    /**
     * method read properties from configuration file
     * @return properties from configuration file
     */
    public Properties readProperties() {
        Properties properties = new Properties();
        File testConfigFile = new File(TEST_CONFIG_PATH);
        File configFileToUse = testConfigFile.exists()
                ? testConfigFile
                : new File(CONFIG_FILE_PATH);
        if (!configFileToUse.exists()) {
            throw new IllegalStateException("config file not found");
        }
        try (InputStream inputStream = new FileInputStream(configFileToUse)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return properties;
    }
}
