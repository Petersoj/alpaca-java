package net.jacobpeterson.alpacajava.common;

import com.google.common.net.HttpHeaders;
import org.jspecify.annotations.NullMarked;

/**
 * {@link ApiHeader} contains API header {@link String} constants.
 */
@NullMarked
public final class ApiHeader {

    /**
     * The API Key ID header {@link String}: <code>"APCA-API-KEY-ID"</code>
     */
    public static final String API_KEY_ID = "APCA-API-KEY-ID";

    /**
     * The API Secret Key header {@link String}: <code>"APCA-API-SECRET-KEY"</code>
     */
    public static final String API_SECRET_KEY = "APCA-API-SECRET-KEY";

    /**
     * The {@link HttpHeaders#AUTHORIZATION} basic prefix: <code>"Basic "</code>
     */
    public static final String AUTHORIZATION_BASIC_PREFIX = "Basic ";

    /**
     * The {@link HttpHeaders#AUTHORIZATION} bearer prefix: <code>"Bearer "</code>
     */
    public static final String AUTHORIZATION_BEARER_PREFIX = "Bearer ";

    private ApiHeader() {}
}
