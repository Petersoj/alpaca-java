package net.jacobpeterson.polygon.properties;

import net.jacobpeterson.alpaca.properties.AlpacaProperties;

import java.util.StringJoiner;

import static net.jacobpeterson.util.properties.PropertyUtil.getProperty;

/**
 * {@link PolygonProperties} defines properties for {@link net.jacobpeterson.polygon.PolygonAPI}.
 */
public class PolygonProperties {

    private static final String POLYGON_PROPERTIES_FILE = "polygon.properties";

    private static final String KEY_ID_KEY = "key_id";
    public static final String KEY_ID_VALUE =
            getProperty(POLYGON_PROPERTIES_FILE, KEY_ID_KEY, AlpacaProperties.KEY_ID_VALUE);

    private static final String BASE_API_URL_KEY = "base_api_url";
    public static final String BASE_API_URL_VALUE =
            getProperty(POLYGON_PROPERTIES_FILE, BASE_API_URL_KEY);

    private static final String POLYGON_WEB_SOCKET_SERVER_URL_KEY = "web_socket_server_url";
    public static final String POLYGON_WEB_SOCKET_SERVER_URL_VALUE =
            getProperty(POLYGON_PROPERTIES_FILE, POLYGON_WEB_SOCKET_SERVER_URL_KEY);

    private static final String USER_AGENT_KEY = "user_agent";
    public static final String USER_AGENT_VALUE = getProperty(POLYGON_PROPERTIES_FILE, USER_AGENT_KEY);

    /**
     * Static {@link #toString()}.
     *
     * @return the static {@link #toString()}
     */
    public static String staticToString() {
        return new StringJoiner(", ", PolygonProperties.class.getSimpleName() + "[", "]")
                .add("BASE_API_URL_KEY = " + BASE_API_URL_KEY)
                .add("BASE_API_URL_VALUE = " + BASE_API_URL_VALUE)
                .add("KEY_ID_KEY = " + KEY_ID_KEY)
                .add("KEY_ID_VALUE = " + KEY_ID_VALUE)
                .add("POLYGON_PROPERTIES_FILE = " + POLYGON_PROPERTIES_FILE)
                .add("POLYGON_WEB_SOCKET_SERVER_URL_KEY = " + POLYGON_WEB_SOCKET_SERVER_URL_KEY)
                .add("POLYGON_WEB_SOCKET_SERVER_URL_VALUE = " + POLYGON_WEB_SOCKET_SERVER_URL_VALUE)
                .add("USER_AGENT_KEY = " + USER_AGENT_KEY)
                .add("USER_AGENT_VALUE = " + USER_AGENT_VALUE)
                .toString();
    }
}
