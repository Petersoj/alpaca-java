package io.github.mainstringargs.polygon.properties;

import io.github.mainstringargs.alpaca.properties.AlpacaProperties;

import static io.github.mainstringargs.util.properties.PropertyUtil.getProperty;

/**
 * The Class AlpacaProperties.
 */
public class PolygonProperties {

    /** The constant POLYGON_PROPERTIES_FILE. */
    private static final String POLYGON_PROPERTIES_FILE = "polygon.properties";

    /** The Constant KEY_ID_KEY. */
    private static final String KEY_ID_KEY = "key_id";

    /** The Constant KEY_ID_VALUE. */
    public static final String KEY_ID_VALUE =
            getProperty(POLYGON_PROPERTIES_FILE, KEY_ID_KEY, AlpacaProperties.KEY_ID_VALUE);

    /** The Constant BASE_API_URL_KEY. */
    private static final String BASE_API_URL_KEY = "base_api_url";

    /** The Constant BASE_API_URL_VALUE. */
    public static final String BASE_API_URL_VALUE =
            getProperty(POLYGON_PROPERTIES_FILE, BASE_API_URL_KEY);

    /** The polygon web socket server url key. */
    private static final String POLYGON_WEB_SOCKET_SERVER_URL_KEY = "web_socket_server_url";

    /** The polygon web socket server url value. */
    public static final String POLYGON_WEB_SOCKET_SERVER_URL_VALUE =
            getProperty(POLYGON_PROPERTIES_FILE, POLYGON_WEB_SOCKET_SERVER_URL_KEY);

    /** The Constant USER_AGENT_KEY. */
    private static final String USER_AGENT_KEY = "user_agent";

    /** The Constant USER_AGENT_VALUE. */
    public static final String USER_AGENT_VALUE = getProperty(POLYGON_PROPERTIES_FILE, USER_AGENT_KEY);

}
