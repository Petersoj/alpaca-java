package io.github.mainstringargs.alpaca.properties;

import static io.github.mainstringargs.util.properties.PropertyUtil.getProperty;

/**
 * The Class AlpacaProperties.
 */
public class AlpacaProperties {

    private static final String ALPACA_PROPERTIES_FILE = "alpaca.properties";

    /** The Constant API_VERSION_KEY. */
    private static final String API_VERSION_KEY = "api_version";

    /** The Constant API_VERSION_VALUE. */
    public static final String API_VERSION_VALUE = getProperty(ALPACA_PROPERTIES_FILE, API_VERSION_KEY);

    /** The Constant KEY_ID_KEY. */
    private static final String KEY_ID_KEY = "key_id";

    /** The Constant KEY_ID_VALUE. */
    public static final String KEY_ID_VALUE = getProperty(ALPACA_PROPERTIES_FILE, KEY_ID_KEY);

    /** The Constant SECRET_KEY. */
    private static final String SECRET_KEY = "secret";

    /** The Constant SECRET_VALUE. */
    public static final String SECRET_VALUE = getProperty(ALPACA_PROPERTIES_FILE, SECRET_KEY);

    /** The Constant BASE_API_URL_KEY. */
    private static final String BASE_API_URL_KEY = "base_api_url";

    /** The Constant BASE_API_URL_VALUE. */
    public static final String BASE_API_URL_VALUE = getProperty(ALPACA_PROPERTIES_FILE, BASE_API_URL_KEY);

    /** The Constant BASE_DATA_URL_KEY. */
    private static final String BASE_DATA_URL_KEY = "base_data_url";

    /** The base data url value. */
    public static final String BASE_DATA_URL_VALUE = getProperty(ALPACA_PROPERTIES_FILE, BASE_DATA_URL_KEY);

    /** The Constant USER_AGENT_KEY. */
    private static final String USER_AGENT_KEY = "user_agent";

    /** The Constant USER_AGENT_VALUE. */
    public static final String USER_AGENT_VALUE = getProperty(ALPACA_PROPERTIES_FILE, USER_AGENT_KEY);

}
