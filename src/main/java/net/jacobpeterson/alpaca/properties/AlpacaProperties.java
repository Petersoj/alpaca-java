package net.jacobpeterson.alpaca.properties;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.properties.DataAPIType;
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType;

import static net.jacobpeterson.alpaca.util.properties.PropertyUtil.getProperty;

/**
 * {@link AlpacaProperties} defines properties for {@link AlpacaAPI}.
 */
public class AlpacaProperties {

    private static final String ALPACA_PROPERTIES_FILE = "alpaca.properties";
    private static final String ALPACA_DEFAULT_PROPERTIES_FILE = "alpaca.default.properties";

    private static final String KEY_ID_KEY = "key_id";
    /** The value of {@link #KEY_ID_KEY} in {@link #ALPACA_PROPERTIES_FILE}. */
    public static final String KEY_ID = getProperty(
            ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            KEY_ID_KEY);

    private static final String SECRET_KEY_KEY = "secret_key";
    /** The value of {@link #SECRET_KEY_KEY} in {@link #ALPACA_PROPERTIES_FILE}. */
    public static final String SECRET_KEY = getProperty(
            ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            SECRET_KEY_KEY);

    private static final String ENDPOINT_API_TYPE_KEY = "endpoint_api_type";
    /** The value of {@link #ENDPOINT_API_TYPE_KEY} in {@link #ALPACA_PROPERTIES_FILE}. */
    public static final EndpointAPIType ENDPOINT_API_TYPE = EndpointAPIType.fromValue(getProperty(
            ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            ENDPOINT_API_TYPE_KEY));

    private static final String DATA_API_TYPE_KEY = "data_api_type";
    /** The value of {@link #DATA_API_TYPE_KEY} in {@link #ALPACA_PROPERTIES_FILE}. */
    public static final DataAPIType DATA_API_TYPE = DataAPIType.fromValue(getProperty(
            ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            DATA_API_TYPE_KEY));

    private static final String USER_AGENT_KEY = "user_agent";
    /** The value of {@link #USER_AGENT_KEY} in {@link #ALPACA_PROPERTIES_FILE}. */
    public static final String USER_AGENT = getProperty(
            ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            USER_AGENT_KEY);
}
