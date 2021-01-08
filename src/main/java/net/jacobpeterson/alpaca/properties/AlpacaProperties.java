package net.jacobpeterson.alpaca.properties;

import java.util.StringJoiner;

import static net.jacobpeterson.util.properties.PropertyUtil.getProperty;

/**
 * {@link AlpacaProperties} defines properties for {@link net.jacobpeterson.alpaca.AlpacaAPI}.
 */
public class AlpacaProperties {

    private static final String ALPACA_PROPERTIES_FILE = "alpaca.properties";

    private static final String KEY_ID_KEY = "key_id";
    public static final String KEY_ID_VALUE = getProperty(ALPACA_PROPERTIES_FILE, KEY_ID_KEY);

    private static final String SECRET_KEY = "secret";
    public static final String SECRET_VALUE = getProperty(ALPACA_PROPERTIES_FILE, SECRET_KEY);

    private static final String BASE_API_URL_KEY = "base_api_url";
    public static final String BASE_API_URL_VALUE = getProperty(ALPACA_PROPERTIES_FILE, BASE_API_URL_KEY);

    private static final String BASE_DATA_URL_KEY = "base_data_url";
    public static final String BASE_DATA_URL_VALUE = getProperty(ALPACA_PROPERTIES_FILE, BASE_DATA_URL_KEY);

    private static final String USER_AGENT_KEY = "user_agent";
    public static final String USER_AGENT_VALUE = getProperty(ALPACA_PROPERTIES_FILE, USER_AGENT_KEY);

    /**
     * Static {@link #toString()}.
     *
     * @return the static {@link #toString()}
     */
    public static String staticToString() {
        return new StringJoiner(", ", AlpacaProperties.class.getSimpleName() + "[", "]")
                .add("ALPACA_PROPERTIES_FILE = " + ALPACA_PROPERTIES_FILE)
                .add("BASE_API_URL_KEY = " + BASE_API_URL_KEY)
                .add("BASE_API_URL_VALUE = " + BASE_API_URL_VALUE)
                .add("BASE_DATA_URL_KEY = " + BASE_DATA_URL_KEY)
                .add("BASE_DATA_URL_VALUE = " + BASE_DATA_URL_VALUE)
                .add("KEY_ID_KEY = " + KEY_ID_KEY)
                .add("KEY_ID_VALUE = " + KEY_ID_VALUE)
                .add("SECRET_KEY = " + SECRET_KEY)
                .add("SECRET_VALUE = " + SECRET_VALUE)
                .add("USER_AGENT_KEY = " + USER_AGENT_KEY)
                .add("USER_AGENT_VALUE = " + USER_AGENT_VALUE)
                .toString();
    }
}
