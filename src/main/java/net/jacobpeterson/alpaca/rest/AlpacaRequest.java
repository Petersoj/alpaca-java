package net.jacobpeterson.alpaca.rest;

import com.google.common.base.Preconditions;
import net.jacobpeterson.abstracts.rest.AbstractRequest;
import net.jacobpeterson.alpaca.properties.AlpacaProperties;

/**
 * {@link AlpacaRequest} contains methods for HTTP requests for {@link net.jacobpeterson.alpaca.AlpacaAPI}.
 */
public class AlpacaRequest extends AbstractRequest {

    private static final String USER_AGENT_KEY = "user-agent";
    private static final String API_KEY_ID = "APCA-API-KEY-ID";
    private static final String API_SECRET_KEY = "APCA-API-SECRET-KEY";
    private static final String AUTH_TOKEN = "Authorization";

    /**
     * Instantiates a new {@link AlpacaRequest}.
     *
     * @param keyID     the key ID
     * @param secretKey the secret key
     */
    public AlpacaRequest(String keyID, String secretKey) {
        super();
        Preconditions.checkNotNull(keyID);
        Preconditions.checkNotNull(secretKey);

        headers.put(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE);
        headers.put(API_KEY_ID, keyID);
        headers.put(API_SECRET_KEY, secretKey);
    }

    /**
     * Instantiates a new {@link AlpacaRequest}.
     *
     * @param oAuthToken the OAuth token
     */
    public AlpacaRequest(String oAuthToken) {
        super();
        Preconditions.checkNotNull(oAuthToken);

        headers.put(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE);
        headers.put(AUTH_TOKEN, "Bearer " + oAuthToken);
    }
}
