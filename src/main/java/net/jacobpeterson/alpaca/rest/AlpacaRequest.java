package net.jacobpeterson.alpaca.rest;

import com.google.common.base.Preconditions;
import net.jacobpeterson.abstracts.rest.AbstractRequest;
import net.jacobpeterson.alpaca.properties.AlpacaProperties;

/**
 * The Class AlpacaRequest.
 */
public class AlpacaRequest extends AbstractRequest {

    /** The Constant USER_AGENT_KEY. */
    private static final String USER_AGENT_KEY = "user-agent";

    /** The Constant API_KEY_ID. */
    private static final String API_KEY_ID = "APCA-API-KEY-ID";

    /** The Constant API_SECRET_KEY. */
    private static final String API_SECRET_KEY = "APCA-API-SECRET-KEY";

    /** The Constant AUTH_TOKEN. */
    private static final String AUTH_TOKEN = "Authorization";

    /**
     * Instantiates a new AlpacaRequest.
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
     * Instantiates a new AlpacaRequest.
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
