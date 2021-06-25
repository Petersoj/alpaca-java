package net.jacobpeterson.alpaca.rest;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.abstracts.rest.AbstractRequest;
import net.jacobpeterson.alpaca.properties.AlpacaProperties;
import net.jacobpeterson.alpaca.rest.exception.AlpacaAPIRequestException;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * {@link AlpacaRequest} contains methods for HTTP requests for {@link AlpacaAPI}.
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

    /**
     * Invokes a GET request and handles/returns the response object.
     *
     * @param <T>        the type
     * @param urlBuilder the {@link AlpacaRequestBuilder}
     * @param type       the type to return
     *
     * @return the response object type
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    public <T> T get(AlpacaRequestBuilder urlBuilder, Type type) throws AlpacaAPIRequestException {
        try {
            HttpResponse<InputStream> response = invokeGet(urlBuilder);

            if (response.getStatus() != 200) {
                throw new AlpacaAPIRequestException(response);
            }

            return getResponseObject(response, type);
        } catch (UnirestException exception) {
            throw new AlpacaAPIRequestException(exception);
        }
    }

    /**
     * Invokes a HEAD request and handles/returns the response object.
     *
     * @param <T>        the type
     * @param urlBuilder the {@link AlpacaRequestBuilder}
     * @param type       the type to return
     *
     * @return the response object type
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    public <T> T head(AlpacaRequestBuilder urlBuilder, Type type) throws AlpacaAPIRequestException {
        try {
            HttpResponse<InputStream> response = invokeHead(urlBuilder);

            if (response.getStatus() != 200) {
                throw new AlpacaAPIRequestException(response);
            }

            return getResponseObject(response, type);
        } catch (UnirestException exception) {
            throw new AlpacaAPIRequestException(exception);
        }
    }

    /**
     * Invokes a POST request and handles/returns the response object.
     *
     * @param <T>        the type
     * @param urlBuilder the {@link AlpacaRequestBuilder}
     * @param type       the type to return
     *
     * @return the response object type
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    public <T> T post(AlpacaRequestBuilder urlBuilder, Type type) throws AlpacaAPIRequestException {
        try {
            HttpResponse<InputStream> response = invokePost(urlBuilder);

            if (response.getStatus() != 200) {
                throw new AlpacaAPIRequestException(response);
            }

            return getResponseObject(response, type);
        } catch (UnirestException exception) {
            throw new AlpacaAPIRequestException(exception);
        }
    }

    /**
     * Invokes a PATCH request and handles/returns the response object.
     *
     * @param <T>        the type
     * @param urlBuilder the {@link AlpacaRequestBuilder}
     * @param type       the type to return
     *
     * @return the response object type
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    public <T> T patch(AlpacaRequestBuilder urlBuilder, Type type) throws AlpacaAPIRequestException {
        try {
            HttpResponse<InputStream> response = invokePatch(urlBuilder);

            if (response.getStatus() != 200) {
                throw new AlpacaAPIRequestException(response);
            }

            return getResponseObject(response, type);
        } catch (UnirestException exception) {
            throw new AlpacaAPIRequestException(exception);
        }
    }

    /**
     * Invokes a POST request and handles/returns the response object.
     *
     * @param <T>        the type
     * @param urlBuilder the {@link AlpacaRequestBuilder}
     * @param type       the type to return
     *
     * @return the response object type
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    public <T> T put(AlpacaRequestBuilder urlBuilder, Type type) throws AlpacaAPIRequestException {
        try {
            HttpResponse<InputStream> response = invokePut(urlBuilder);

            if (response.getStatus() != 200) {
                throw new AlpacaAPIRequestException(response);
            }

            return getResponseObject(response, type);
        } catch (UnirestException exception) {
            throw new AlpacaAPIRequestException(exception);
        }
    }

    /**
     * Invokes a DELETE request and handles/returns the response object.
     *
     * @param <T>        the type
     * @param urlBuilder the {@link AlpacaRequestBuilder}
     * @param type       the type to return
     *
     * @return the response object type
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    public <T> T delete(AlpacaRequestBuilder urlBuilder, Type type) throws AlpacaAPIRequestException {
        try {
            HttpResponse<InputStream> response = invokeDelete(urlBuilder);

            if (response.getStatus() != 200) {
                throw new AlpacaAPIRequestException(response);
            }

            return getResponseObject(response, type);
        } catch (UnirestException exception) {
            throw new AlpacaAPIRequestException(exception);
        }
    }

    /**
     * Invokes a OPTIONS request and handles/returns the response object.
     *
     * @param <T>        the type
     * @param urlBuilder the {@link AlpacaRequestBuilder}
     * @param type       the type to return
     *
     * @return the response object type
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    public <T> T options(AlpacaRequestBuilder urlBuilder, Type type) throws AlpacaAPIRequestException {
        try {
            HttpResponse<InputStream> response = invokeOptions(urlBuilder);

            if (response.getStatus() != 200) {
                throw new AlpacaAPIRequestException(response);
            }

            return getResponseObject(response, type);
        } catch (UnirestException exception) {
            throw new AlpacaAPIRequestException(exception);
        }
    }
}
