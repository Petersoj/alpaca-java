package net.jacobpeterson.util.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The type {@link PropertyUtil}.
 */
public class PropertyUtil {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);

    /** The property file. */
    public static final Map<String, Properties> CACHED_PROPERTIES = Collections.synchronizedMap(new HashMap<>());

    /**
     * Get property string. Will try to IO load the properties in the property file if not cached already.
     *
     * @param propertyFile the property file (.default properties files are also cached if they exist)
     * @param key          the key
     *
     * @return the string
     */
    public static String getProperty(String propertyFile, String key) {
        return getProperty(propertyFile, key, null);
    }

    /**
     * Get property string. Will try to IO load the properties in the property file if not cached already.
     *
     * @param propertyFile the property file (.default properties files are also cached if they exist)
     * @param key          the key
     * @param defaultValue the default value (if not .default properties were found, then this is returned)
     *
     * @return the string
     */
    public static String getProperty(String propertyFile, String key, String defaultValue) {
        if (!CACHED_PROPERTIES.containsKey(propertyFile)) {
            CACHED_PROPERTIES.put(propertyFile, loadPropertyFile(propertyFile));
        }
        Properties properties = CACHED_PROPERTIES.get(propertyFile);
        if (properties != null) {
            return properties.getProperty(key, defaultValue);
        }
        return null;
    }

    /**
     * Load property file properties.
     *
     * @param propertyFile the property file name (.default properties files are also searched)
     *
     * @return the properties
     */
    public synchronized static Properties loadPropertyFile(String propertyFile) {
        Properties properties = null;

        // Load the default property file if exists
        Properties defaultProperties = null;
        String defaultPropertyFile = propertyFile + ".default";
        InputStream defaultPropertyStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(defaultPropertyFile);

        if (defaultPropertyStream != null) {
            defaultProperties = new Properties();

            // Load the properties
            try {
                defaultProperties.load(defaultPropertyStream);
                LOGGER.debug("Loaded default properties file: {}", defaultPropertyFile);
            } catch (IOException exception) {
                LOGGER.error("Could not load default property file: {}\n{}", defaultPropertyFile, exception);
            }

            // Close the InputStream
            try {
                defaultPropertyStream.close();
            } catch (IOException exception) {
                LOGGER.error("Could not close default property file stream: {}\n{}", defaultPropertyFile, exception);
            }

        } else {
            LOGGER.warn("No default property file found for: {}", propertyFile);
        }

        // Load the property file
        InputStream propertyStream = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);

        if (propertyStream != null) {
            // Add default properties if they were found
            properties = defaultProperties == null ? new Properties() : new Properties(defaultProperties);

            // Load the properties
            try {
                properties.load(propertyStream);
                LOGGER.info("Loaded properties file: {}", propertyFile);
            } catch (IOException exception) {
                LOGGER.error("Could not load property file: {}\n{}", propertyFile, exception);
            }

            // Close the InputStream
            try {
                propertyStream.close();
            } catch (IOException exception) {
                LOGGER.error("Could not close property file stream: {}\n{}", propertyFile, exception);
            }
        } else {
            LOGGER.error("Could not find property file: {}", propertyFile);

            if (defaultProperties != null) {
                LOGGER.info("Using default properties for: {}", propertyFile);
                properties = defaultProperties;
            }
        }

        return properties;
    }
}
