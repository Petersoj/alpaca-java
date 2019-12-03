package io.github.mainstringargs.alpaca.rest;

import io.github.mainstringargs.alpaca.properties.AlpacaProperties;
import io.github.mainstringargs.abstracts.rest.AbstractRequest;

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

    /**
     * Instantiates a new alpaca request.
     *
     * @param keyID     the key id
     * @param secretKey the secret key
     */
    public AlpacaRequest(String keyID, String secretKey) {
        super();

        headers.put(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE);
        headers.put(API_KEY_ID, keyID);
        headers.put(API_SECRET_KEY, secretKey);
    }
}
