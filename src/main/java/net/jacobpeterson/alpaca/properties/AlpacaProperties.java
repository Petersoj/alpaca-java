package net.jacobpeterson.alpaca.properties;

import net.jacobpeterson.util.reflection.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static net.jacobpeterson.util.properties.PropertyUtil.getProperty;

/**
 * {@link AlpacaProperties} defines properties for {@link net.jacobpeterson.alpaca.AlpacaAPI}.
 */
public class AlpacaProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaProperties.class);

    private static final String ALPACA_PROPERTIES_FILE = "alpaca.properties";
    private static final String ALPACA_DEFAULT_PROPERTIES_FILE = "alpaca.default.properties";

    private static final String KEY_ID_KEY = "key_id";
    public static final String KEY_ID_VALUE = getProperty(ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            KEY_ID_KEY);

    private static final String SECRET_KEY = "secret";
    public static final String SECRET_VALUE = getProperty(ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            SECRET_KEY);

    private static final String API_ENDPOINT_TYPE_KEY = "endpoint_api_type";
    public static final String API_ENDPOINT_TYPE_VALUE = getProperty(ALPACA_PROPERTIES_FILE,
            ALPACA_DEFAULT_PROPERTIES_FILE, API_ENDPOINT_TYPE_KEY);

    private static final String USER_AGENT_KEY = "user_agent";
    public static final String USER_AGENT_VALUE = getProperty(ALPACA_PROPERTIES_FILE, ALPACA_DEFAULT_PROPERTIES_FILE,
            USER_AGENT_KEY);

    /**
     * Static {@link #toString()}.
     *
     * @return the static {@link #toString()}
     */
    public static String staticToString() {
        return AlpacaProperties.class.getName() + "[" +
                ReflectionUtil.getAllDeclaredFields(AlpacaProperties.class).stream()
                        .filter(field -> !field.getName().contains("LOGGER"))
                        .map(field -> {
                            try {
                                return field.getName() + " = " + field.get(null);
                            } catch (IllegalAccessException exception) {
                                LOGGER.error("Could not get field!", exception);
                            }

                            return field.getName() + " = ?";
                        }).collect(Collectors.joining(", "))
                + "]";
    }
}
