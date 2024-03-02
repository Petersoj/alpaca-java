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

    public static final String KEY_ID = getProperty(
            ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            "key_id");
    public static final String SECRET_KEY = getProperty(
            ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            "secret_key");
    public static final EndpointAPIType ENDPOINT_API_TYPE = EndpointAPIType.fromValue(getProperty(
            ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            "endpoint_api_type"));
    public static final DataAPIType DATA_API_TYPE = DataAPIType.fromValue(getProperty(
            ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            "data_api_type"));
}
