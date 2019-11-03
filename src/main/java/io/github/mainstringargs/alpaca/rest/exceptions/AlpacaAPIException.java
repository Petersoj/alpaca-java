package io.github.mainstringargs.alpaca.rest.exceptions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mashape.unirest.http.HttpResponse;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The Class AlpacaAPIException.
 */
public class AlpacaAPIException extends Exception {

    /** The constant JSON_PARSER. */
    private static final JsonParser JSON_PARSER = new JsonParser();

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The http response. */
    private transient HttpResponse<InputStream> httpResponse;

    /** The http response code. */
    private int httpResponseCode = -1;

    /** The http response message. */
    private String httpResponseMessage;

    /** The alpaca response code. */
    private int alpacaResponseCode = -1;

    /** The alpaca response message. */
    private String alpacaResponseMessage;

    /**
     * Instantiates a new alpaca API exception.
     *
     * @param httpResponse the http response
     */
    public AlpacaAPIException(HttpResponse<InputStream> httpResponse) {
        this.httpResponse = httpResponse;
        httpResponseCode = httpResponse.getStatus();
        httpResponseMessage = httpResponse.getStatusText();

        JsonElement jsonElement = JSON_PARSER.parse(new JsonReader(new InputStreamReader(httpResponse.getRawBody())));

        if (jsonElement instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            if (jsonObject.has("code")) {
                alpacaResponseCode = jsonObject.get("code").getAsInt();
            }

            if (jsonObject.has("message")) {
                alpacaResponseMessage = jsonObject.get("message").getAsString();
            } else {
                // if all else fails, just use the json
                alpacaResponseMessage = jsonObject.toString();
            }
        }
    }

    /**
     * Gets the http response.
     *
     * @return the http response
     */
    public HttpResponse<InputStream> getHttpResponse() {
        return httpResponse;
    }

    /**
     * Gets the http response code.
     *
     * @return the http response code
     */
    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    /**
     * Gets the http response message.
     *
     * @return the http response message
     */
    public String getHttpResponseMessage() {
        return httpResponseMessage;
    }

    /**
     * Gets the alpaca response code.
     *
     * @return the alpaca response code
     */
    public int getAlpacaResponseCode() {
        return alpacaResponseCode;
    }

    /**
     * Gets the alpaca response message.
     *
     * @return the alpaca response message
     */
    public String getAlpacaResponseMessage() {
        return alpacaResponseMessage;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        String message = "Generic Alpaca Exception";

        if (alpacaResponseCode != -1) {
            message = alpacaResponseCode + "";

            if (alpacaResponseMessage != null && !alpacaResponseMessage.isEmpty()) {
                message += ": " + alpacaResponseMessage;
            }

        } else if (httpResponseMessage != null && !httpResponseMessage.isEmpty()) {
            message = httpResponseCode + ": " + httpResponseMessage;
        }

        return message;
    }
}
