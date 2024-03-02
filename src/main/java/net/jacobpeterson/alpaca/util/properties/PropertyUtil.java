package net.jacobpeterson.alpaca.util.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * {@link PropertyUtil} is a utility class for all {@link Properties}-related handling.
 */
public final class PropertyUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);

    public static final Map<String, Properties> CACHED_PROPERTIES = Collections.synchronizedMap(new HashMap<>());

    /**
     * Gets a string property from a property file. Will try to IO load the properties in the property file if not
     * cached already.
     *
     * @param propertyFile the property file
     * @param key          the key
     *
     * @return the string
     */
    public static String getProperty(String propertyFile, String key) {
        return getProperty(propertyFile, null, key, null);
    }

    /**
     * Gets a string property from a property file. Will try to IO load the properties in the property file if not
     * cached already. If the desired property is not found in the <code>propertyFile</code>, then the
     * <code>defaultPropertyFile</code> is searched.
     *
     * @param propertyFile        the property file
     * @param defaultPropertyFile the default property file
     * @param key                 the key
     *
     * @return the string
     */
    public static String getProperty(String propertyFile, String defaultPropertyFile, String key) {
        return getProperty(propertyFile, defaultPropertyFile, key, null);
    }

    /**
     * Gets a string property from a property file. Will try to IO load the properties in the property file if not
     * cached already. If the desired property is not found in the <code>propertyFile</code>, then the
     * <code>defaultPropertyFile</code> is searched, and if it's not there, then <code>defaultValue</code> is returned.
     *
     * @param propertyFile        the property file
     * @param defaultPropertyFile the default property file
     * @param key                 the key
     * @param defaultValue        the default value (if the desired property wasn't found, then this is returned)
     *
     * @return the string
     */
    public static String getProperty(String propertyFile, String defaultPropertyFile, String key, String defaultValue) {
        final Properties properties;
        if (!CACHED_PROPERTIES.containsKey(propertyFile)) {
            properties = loadPropertyFile(propertyFile, defaultPropertyFile);
            CACHED_PROPERTIES.put(propertyFile, properties);
        } else {
            properties = CACHED_PROPERTIES.get(propertyFile);
        }
        return properties == null ? defaultValue : properties.getProperty(key, defaultValue);
    }

    /**
     * Loads property file {@link Properties}.
     *
     * @param propertyFile        the property file name
     * @param defaultPropertyFile the default property file name
     *
     * @return the properties
     */
    public synchronized static Properties loadPropertyFile(String propertyFile, String defaultPropertyFile) {
        ClassLoader classLoader = PropertyUtil.class.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        // Load the default property file if exists
        Properties defaultProperties = null;
        if (defaultPropertyFile != null) {
            try (InputStream defaultPropertyStream = classLoader.getResourceAsStream(defaultPropertyFile)) {
                if (defaultPropertyStream == null) {
                    LOGGER.warn("No default property file \"{}\" exists on the classpath.", defaultPropertyFile);
                } else {

                    defaultProperties = new Properties();
                    try {
                        defaultProperties.load(defaultPropertyStream);
                        LOGGER.debug("Loaded default properties file: {}", defaultPropertyFile);
                    } catch (IOException exception) {
                        LOGGER.error("Could not load default property file: {}", defaultPropertyFile, exception);
                    }
                }
            } catch (Exception exception) {
                LOGGER.error("Could not read default property file stream: {}", defaultPropertyFile, exception);
            }
        } else {
            LOGGER.warn("No default property file given for: {}", propertyFile);
        }

        // Load the property file if exists
        Properties properties = null;
        if (propertyFile != null) {
            try (InputStream propertyStream = classLoader.getResourceAsStream(propertyFile)) {
                if (propertyStream == null) {
                    LOGGER.warn("No property file \"{}\" exists on the classpath.", propertyFile);
                } else {
                    // Add default properties if they were found
                    properties = defaultProperties == null ? new Properties() : new Properties(defaultProperties);

                    // Load the properties
                    try {
                        properties.load(propertyStream);
                        LOGGER.info("Loaded properties file: {}", propertyFile);
                    } catch (IOException exception) {
                        LOGGER.error("Could not load property file: {}", propertyFile, exception);
                    }
                }
            } catch (Exception exception) {
                LOGGER.error("Could not read property file stream: {}", propertyFile, exception);
            }
        } else if (defaultProperties == null) {
            throw new IllegalStateException("No property files were found!");
        }

        if (properties == null) {
            LOGGER.debug("Could not find property file: {}", propertyFile);

            if (defaultProperties != null) {
                LOGGER.info("Using default properties for: {}", propertyFile);
                properties = defaultProperties;
            }
        }

        return properties;
    }
}
