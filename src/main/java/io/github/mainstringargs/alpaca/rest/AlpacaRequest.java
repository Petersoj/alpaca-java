package io.github.mainstringargs.alpaca.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * The Class AlpacaRequest.
 */
public class AlpacaRequest {

    /** The Constant USER_AGENT_KEY. */
    private static final String USER_AGENT_KEY = "user-agent";

    /** The Constant API_KEY_ID. */
    private static final String API_KEY_ID = "APCA-API-KEY-ID";

    /** The Constant API_SECRET_KEY. */
    private static final String API_SECRET_KEY = "APCA-API-SECRET-KEY";

    /** The logger. */
    private static final Logger LOGGER = LogManager.getLogger(AlpacaRequest.class);

    /** The Gson */
    private static final Gson GSON = new GsonBuilder().setLenient().create();

    /** The key id. */
    private String keyId;

    /** The secret. */
    private String secret;

    /**
     * Instantiates a new alpaca request.
     *
     * @param keyId  the key id
     * @param secret the secret
     */
    public AlpacaRequest(String keyId, String secret) {
        this.keyId = keyId;
        this.secret = secret;
    }

    /**
     * Invoke get.
     *
     * @param builder the builder
     * @return the http response
     */
    public HttpResponse<JsonNode> invokeGet(AlpacaRequestBuilder builder) {
        HttpResponse<JsonNode> response = null;

        try {
            LOGGER.debug("Get URL " + builder.getURL());

            response = Unirest.get(builder.getURL())
                    .header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE)
                    .header(API_KEY_ID, keyId).header(API_SECRET_KEY, secret).asJson();

            LOGGER.debug("GET status: " + response.getStatus() + "\n\t\t\t\t\tstatusText: "
                    + response.getStatusText() + "\n\t\t\t\t\tBody: " + response.getBody());
        } catch (UnirestException e) {
            LOGGER.info("UnirestException", e);
        }

        return response;
    }

    /**
     * Invoke post.
     *
     * @param builder the builder
     * @return the http response
     */
    public HttpResponse<JsonNode> invokePost(AlpacaRequestBuilder builder) {
        HttpResponse<JsonNode> response = null;

        try {
            LOGGER.debug("Post URL " + builder.getURL());
            LOGGER.debug("Post Body " + builder.getBodyAsJSON());

            response = Unirest.post(builder.getURL())
                    .header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE).header(API_KEY_ID, keyId)
                    .header(API_SECRET_KEY, secret).body(builder.getBodyAsJSON()).asJson();

            LOGGER.debug("POST status: " + response.getStatus() + "\n\t\t\t\t\tstatusText: "
                    + response.getStatusText() + "\n\t\t\t\t\tBody: " + response.getBody());
        } catch (UnirestException e) {
            LOGGER.info("UnirestException", e);
        }

        return response;
    }

    /**
     * Invoke delete.
     *
     * @param builder the builder
     * @return the http response
     */
    public HttpResponse<JsonNode> invokeDelete(AlpacaRequestBuilder builder) {
        HttpResponse<JsonNode> response = null;
        try {
            LOGGER.debug("Delete URL " + builder.getURL());

            response = Unirest.delete(builder.getURL())
                    .header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE)
                    .header(API_KEY_ID, keyId).header(API_SECRET_KEY, secret).asJson();

            LOGGER.debug("DELETE status: " + response.getStatus() + "\n\t\t\t\t\tstatusText: "
                    + response.getStatusText() + "\n\t\t\t\t\tBody: " + response.getBody());
        } catch (UnirestException e) {
            LOGGER.info("UnirestException", e);
        }

        return response;
    }

    /**
     * Invoke patch.
     *
     * @param builder the builder
     * @return the http response
     */
    public HttpResponse<JsonNode> invokePatch(AlpacaRequestBuilder builder) {
        HttpResponse<JsonNode> response = null;
        try {
            LOGGER.debug("Patch URL " + builder.getURL());

            response = Unirest.patch(builder.getURL())
                    .header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE)
                    .header(API_KEY_ID, keyId).header(API_SECRET_KEY, secret).asJson();

            LOGGER.debug("Patch status: " + response.getStatus() + "\n\t\t\t\t\tstatusText: "
                    + response.getStatusText() + "\n\t\t\t\t\tBody: " + response.getBody());
        } catch (UnirestException e) {
            LOGGER.info("UnirestException", e);
        }

        return response;
    }

    /**
     * Gets the response object.
     *
     * @param <T>          the generic type
     * @param httpResponse the http response
     * @param type         the type
     * @return the response object
     */
    public <T> T getResponseObject(HttpResponse<JsonNode> httpResponse, Type type) {
        T responseObjectFromJson = null;

        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(httpResponse.getRawBody()));
            responseObjectFromJson = GSON.fromJson(br, type);
        } catch (Exception e) {
            LOGGER.info("Exception", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.info("IOException", e);
                }
            }
        }

        return responseObjectFromJson;
    }
}
