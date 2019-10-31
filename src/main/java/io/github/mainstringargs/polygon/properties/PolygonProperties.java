package io.github.mainstringargs.polygon.properties;

import io.github.mainstringargs.alpaca.properties.AlpacaProperties;
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
public class PolygonProperties {

    /** The property file. */
    private static final LinkedHashSet<Properties> propertyFiles = new LinkedHashSet<>();

    /** The logger. */
    private static final Logger LOGGER = LogManager.getLogger(PolygonProperties.class);

    /** The Constant INVALID_VALUE. */
    private static final String INVALID_VALUE = "<PLACEHOLDER>";

    /** The Constant KEY_ID_KEY. */
    private static final String KEY_ID_KEY = "key_id";

    /** The Constant KEY_ID_VALUE. */
    public static final String KEY_ID_VALUE =
                    getProperty(KEY_ID_KEY, AlpacaProperties.KEY_ID_VALUE);

    /** The Constant BASE_DATA_URL_KEY. */
    private static final String BASE_DATA_URL_KEY = "base_data_url";

    /** The Constant USER_AGENT_KEY. */
    private static final String USER_AGENT_KEY = "user_agent";

    /** The Constant DEFAULT_USER_AGENT. */
    private static final String DEFAULT_USER_AGENT =
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36";

    /** The Constant USER_AGENT_VALUE. */
    public static final String USER_AGENT_VALUE = getProperty(USER_AGENT_KEY, DEFAULT_USER_AGENT);

    /** The Constant DEFAULT_DATA_URL. */
    private static final String DEFAULT_DATA_URL = "https://api.polygon.io";

    /** The base data url value. */
    public static final String BASE_DATA_URL_VALUE =
                    getProperty(BASE_DATA_URL_KEY, DEFAULT_DATA_URL);

    /** The default polygon web socket server. */
    private static final String DEFAULT_POLYGON_WEB_SOCKET_SERVER_URL =
                    "wss://alpaca.socket.polygon.io/stocks";

    /** The polygon web socket server url key. */
    private static final String POLYGON_WEB_SOCKET_SERVER_URL_KEY = "web_socket_server_url";

    /** The polygon web socket server url value. */
    public static final String POLYGON_WEB_SOCKET_SERVER_URL_VALUE = getProperty(
                    POLYGON_WEB_SOCKET_SERVER_URL_KEY, DEFAULT_POLYGON_WEB_SOCKET_SERVER_URL);

    /** The initialized. */
    private static boolean initialized = false;

    /**
     * Inits the properties.
     */
    private synchronized static void initProperties() {
        if (!initialized) {
            LinkedHashSet<URL> propertyUrls = new LinkedHashSet<>();
            String propertyFile = "polygon.properties";
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
                LOGGER.info("Found " + propertyFile + " File: " + propUrl);

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
                    LOGGER.debug("Loading " + key + " " + propertyVal);
                    return propertyVal.trim();
                }
            }
        }

        LOGGER.debug("Loading " + key + " " + defaultValue);

        return defaultValue.trim();
    }
}
