package io.github.mainstringargs.abstracts.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Abstract request builder.
 */
public abstract class AbstractRequestBuilder {

    /** The Constant URL_SEPARATOR. */
    private final static String URL_SEPARATOR = "/";

    /** The appended endpoints. */
    private final List<String> appendedEndpoints = new ArrayList<>();

    /** The url parameters. */
    private final Map<String, String> urlParameters = new LinkedHashMap<>();

    /** The Body json object. */
    private final JsonObject bodyJsonObject = new JsonObject();

    /** The base url. */
    private final String baseUrl;

    /** The custom body. */
    private String customBody;

    /**
     * Instantiates a new abstract request builder.
     *
     * @param baseUrl the base url
     */
    public AbstractRequestBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Append endpoint.
     *
     * @param endpoint the endpoint
     */
    public void appendEndpoint(String endpoint) {
        if (endpoint != null) {
            appendedEndpoints.add(endpoint);
        }
    }

    /**
     * Append URL parameter.
     *
     * @param urlKey   the url key
     * @param urlValue the url value
     */
    public void appendURLParameter(String urlKey, String urlValue) {
        if (urlValue != null) {
            urlParameters.put(urlKey, urlValue);
        }
    }

    /**
     * Append json body property.
     *
     * @param bodyKey   the body key
     * @param bodyValue the body value
     */
    public void appendJSONBodyProperty(String bodyKey, String bodyValue) {
        if (bodyValue != null) {
            bodyJsonObject.addProperty(bodyKey, bodyValue);
        }
    }

    /**
     * Append body json property.
     *
     * @param bodyKey   the body key
     * @param bodyValue the body value
     */
    public void appendJSONBodyJSONProperty(String bodyKey, JsonElement bodyValue) {
        if (bodyValue != null) {
            bodyJsonObject.add(bodyKey, bodyValue);
        }
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getURL() {
        StringBuilder builder = new StringBuilder(baseUrl);

        for (String appendedEndpoint : appendedEndpoints) {
            builder.append(URL_SEPARATOR);
            builder.append(appendedEndpoint);
        }

        if (!urlParameters.isEmpty()) {
            builder.append('?');

            for (Map.Entry<String, String> entry : urlParameters.entrySet()) {
                builder.append(entry.getKey().trim());
                builder.append('=');
                builder.append(entry.getValue().trim());
                builder.append('&');
            }
            // removes last &
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    /**
     * Gets the body.
     *
     * @return the body
     */
    public String getBody() {
        if (customBody != null) {
            return customBody;
        } else if (bodyJsonObject.size() > 0) {
            return bodyJsonObject.toString();
        } else {
            return null;
        }
    }

    /**
     * Sets custom body.
     *
     * @param customBody the custom body
     */
    public void setCustomBody(String customBody) {
        this.customBody = customBody;
    }
}
