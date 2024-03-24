package net.jacobpeterson.alpaca.util.apikey;

import java.util.Base64;

/**
 * {@link APIKeyUtil} is a utility class for Alpaca API keys.
 */
public class APIKeyUtil {

    /**
     * Creates a broker API auth key.
     *
     * @param brokerAPIKey    the broker API key
     * @param brokerAPISecret the broker API secret
     *
     * @return the key {@link String}
     *
     * @see <a href="https://docs.alpaca.markets/docs/getting-started-with-broker-api#api-keys">Alpaca Docs</a>
     */
    public static String createBrokerAPIAuthKey(String brokerAPIKey, String brokerAPISecret) {
        return Base64.getEncoder().encodeToString((brokerAPIKey + ":" + brokerAPISecret).getBytes());
    }
}
