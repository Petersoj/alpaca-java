package io.github.mainstringargs.alpaca.properties;

import io.github.mainstringargs.alpaca.AlpacaConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * The Class AlpacaProperties.
 */
public class AlpacaProperties {

    /** The Constant INVALID_VALUE. */
    public static final String INVALID_VALUE = "<PLACEHOLDER>";

    /** The Constant API_VERSION_KEY. */
    private static final String API_VERSION_KEY = "api_version";

    /** The Constant KEY_ID_KEY. */
    private static final String KEY_ID_KEY = "key_id";

    /** The Constant SECRET_KEY. */
    private static final String SECRET_KEY = "secret";

    /** The Constant BASE_ACCOUNT_URL_KEY. */
    private static final String BASE_ACCOUNT_URL_KEY = "base_url";

    /** The Constant BASE_DATA_URL_KEY. */
    private static final String BASE_DATA_URL_KEY = "base_data_url";

    /** The Constant USER_AGENT_KEY. */
    private static final String USER_AGENT_KEY = "user_agent";

    /** The Constant DEFAULT_API_VERSION_VALUE. */
    private static final String DEFAULT_API_VERSION_VALUE = AlpacaConstants.VERSION_2_ENDPOINT;

    /** The Constant DEFAULT_USER_AGENT. */
    private static final String DEFAULT_USER_AGENT =
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36";

    /** The Constant DEFAULT_ACCOUNT_URL. */
    private static final String DEFAULT_ACCOUNT_URL = "https://paper-api.alpaca.markets";

    /** The Constant DEFAULT_DATA_URL. */
    private static final String DEFAULT_DATA_URL = "https://data.alpaca.markets";

    /** The property file. */
    private static LinkedHashSet<Properties> propertyFiles = new LinkedHashSet<>();

    /** The logger. */
    private static Logger LOGGER = LogManager.getLogger(AlpacaProperties.class);

    /** The Constant API_VERSION_VALUE. */
    public static final String API_VERSION_VALUE =
                    getProperty(API_VERSION_KEY, DEFAULT_API_VERSION_VALUE);

    /** The Constant KEY_ID_VALUE. */
    public static final String KEY_ID_VALUE = getProperty(KEY_ID_KEY, INVALID_VALUE);

    /** The Constant SECRET_VALUE. */
    public static final String SECRET_VALUE = getProperty(SECRET_KEY, INVALID_VALUE);

    /** The Constant BASE_ACCOUNT_URL_VALUE. */
    public static final String BASE_ACCOUNT_URL_VALUE =
                    getProperty(BASE_ACCOUNT_URL_KEY, DEFAULT_ACCOUNT_URL);

    /** The base data url value. */
    public static final String BASE_DATA_URL_VALUE =
                    getProperty(BASE_DATA_URL_KEY, DEFAULT_DATA_URL);

    /** The Constant USER_AGENT_VALUE. */
    public static final String USER_AGENT_VALUE = getProperty(USER_AGENT_KEY, DEFAULT_USER_AGENT);

    /** The initialized. */
    private static boolean initialized = false;

    /**
     * Inits the properties.
     */
    private synchronized static void initProperties() {
        if (!initialized) {
            LinkedHashSet<URL> propertyUrls = new LinkedHashSet<>();
            String propertyFile = "alpaca.properties";
            URL url = AlpacaProperties.class.getResource("/" + propertyFile);
            propertyUrls.add(url);

            Enumeration<URL> urls = null;
            try {
                urls = ClassLoader.getSystemClassLoader().getResources(propertyFile);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            while (urls.hasMoreElements()) {
                propertyUrls.add(urls.nextElement());
            }

            for (URL propUrl : propertyUrls) {
                LOGGER.debug("Found " + propertyFile + " File: " + propUrl);

                InputStream is = null;
                try {
                    is = (url.openStream());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                Properties propFile = new Properties();
                try {
                    propFile.load(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                propertyFiles.add(propFile);
            }

            initialized = true;
        }
    }



    /**
     * Gets the property.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the property
     */
    public static String getProperty(String key, String defaultValue) {
        initProperties();

        for (Properties prop : propertyFiles) {
            if (prop.containsKey(key)) {
                String propertyVal = prop.getProperty(key);
                if (!propertyVal.equals(INVALID_VALUE) && !propertyVal.trim().isEmpty()) {
                    LOGGER.info("Loading " + key + " " + propertyVal);
                    return propertyVal.trim();
                }
            }
        }
        LOGGER.debug("Loading " + key + " " + defaultValue);

        return defaultValue.trim();
    }
}
