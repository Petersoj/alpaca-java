package net.jacobpeterson.alpacajava.broker;

import org.jspecify.annotations.NullMarked;

import java.util.Base64;

@NullMarked
public final class BrokerApiUtil {

    /**
     * @return {@link Base64.Encoder#encodeToString(byte[])} <code>apiKey + ":" + apiSecret</code>
     *
     * @see <a href="https://docs.alpaca.markets/docs/getting-started-with-broker-api#api-keys">docs.alpaca.markets</a>
     */
    public static String getAuthorizationToken(final String apiKey, final String apiSecret) {
        return Base64.getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes());
    }

    private BrokerApiUtil() {}
}
