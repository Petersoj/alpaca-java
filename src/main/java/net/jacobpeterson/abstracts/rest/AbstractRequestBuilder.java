package net.jacobpeterson.abstracts.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link AbstractRequestBuilder} is a builder for URL Strings.
 */
public abstract class AbstractRequestBuilder {

    private static final String URL_SEPARATOR = "/";

    private final String baseURL;
    private final List<String> appendedEndpoints;
    private final Map<String, String> urlParameters;
    private final JsonObject bodyJsonObject;

    private String customBody;

    /**
     * Instantiates a new {@link AbstractRequestBuilder}.
     *
     * @param baseURL the base URL
     */
    public AbstractRequestBuilder(String baseURL) {
        this.baseURL = baseURL;

        appendedEndpoints = new ArrayList<>();
        urlParameters = new LinkedHashMap<>();
        bodyJsonObject = new JsonObject();
    }

    /**
     * Appends an endpoint.
     *
     * @param endpoint the endpoint
     */
    public void appendEndpoint(String endpoint) {
        appendedEndpoints.add(endpoint);
    }

    /**
     * Appends a URL parameter.
     *
     * @param urlKey   the URL key
     * @param urlValue the URL value
     */
    public void appendURLParameter(String urlKey, String urlValue) {
        urlParameters.put(urlKey, urlValue);
    }

    /**
     * Appends a JSON string property to {@link #buildBody()}.
     *
     * @param bodyKey   the body key
     * @param bodyValue the body value
     */
    public void appendJSONBodyProperty(String bodyKey, String bodyValue) {
        bodyJsonObject.addProperty(bodyKey, bodyValue);
    }

    /**
     * Appends a {@link JsonElement} property to {@link #buildBody()}.
     *
     * @param bodyKey   the body key
     * @param bodyValue the {@link JsonElement} body value
     */
    public void appendJSONBodyJSONProperty(String bodyKey, JsonElement bodyValue) {
        bodyJsonObject.add(bodyKey, bodyValue);
    }

    /**
     * Builds the URL string {@link AbstractRequestBuilder}.
     *
     * @return the built URL string
     */
    public String buildURL() {
        StringBuilder builder = new StringBuilder(baseURL);

        for (String appendedEndpoint : appendedEndpoints) {
            builder.append(URL_SEPARATOR).append(appendedEndpoint);
        }

        if (!urlParameters.isEmpty()) {
            builder.append('?');

            for (Map.Entry<String, String> entry : urlParameters.entrySet()) {
                builder.append(entry.getKey().trim());
                builder.append('=');
                builder.append(entry.getValue().trim());
                builder.append('&');
            }

            // removes last '&'
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    /**
     * Builds the body string of this {@link AbstractRequestBuilder}.
     *
     * @return the body string
     */
    public String buildBody() {
        if (customBody != null) {
            return customBody;
        } else if (bodyJsonObject.size() > 0) {
            return bodyJsonObject.toString();
        } else {
            return null;
        }
    }

    /**
     * Sets a custom body string for {@link #buildBody()}.
     *
     * @param customBody the custom body string
     */
    public void setCustomBody(String customBody) {
        this.customBody = customBody;
    }
}
