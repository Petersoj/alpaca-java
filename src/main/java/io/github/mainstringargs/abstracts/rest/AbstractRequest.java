package io.github.mainstringargs.abstracts.rest;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import io.github.mainstringargs.util.gson.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Abstract request.
 */
public abstract class AbstractRequest {

    /** The logger. */
    private static final Logger LOGGER = LogManager.getLogger(AbstractRequest.class);

    /** The headers. */
    protected final Map<String, String> headers = new HashMap<>();

    /**
     * Invoke get.
     *
     * @param abstractRequestBuilder the abstract request builder
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokeGet(AbstractRequestBuilder abstractRequestBuilder) {
        try {
            String url = abstractRequestBuilder.getURL();

            LOGGER.debug("GET URL " + url);

            GetRequest request = Unirest.get(url);

            if (!headers.isEmpty()) {
                request.headers(headers);

                LOGGER.debug("GET Headers: " + headers);
            }

            return request.asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return null;
    }

    /**
     * Invoke head.
     *
     * @param abstractRequestBuilder the abstract request builder
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokeHead(AbstractRequestBuilder abstractRequestBuilder) {
        try {
            String url = abstractRequestBuilder.getURL();

            LOGGER.debug("HEAD URL " + url);

            GetRequest request = Unirest.head(url);

            if (!headers.isEmpty()) {
                request.headers(headers);

                LOGGER.debug("HEAD Headers: " + headers);
            }

            return request.asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return null;
    }

    /**
     * Invoke post.
     *
     * @param abstractRequestBuilder the abstract request builder
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokePost(AbstractRequestBuilder abstractRequestBuilder) {
        try {
            String url = abstractRequestBuilder.getURL();

            LOGGER.debug("POST URL: " + url);

            HttpRequestWithBody request = Unirest.post(url);

            if (!headers.isEmpty()) {
                request.headers(headers);

                LOGGER.debug("POST Headers: " + headers);
            }

            String body = abstractRequestBuilder.getBody();
            if (body != null) {
                request.body(body);

                LOGGER.debug("POST Body: " + body);
            }

            return request.asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return null;
    }

    /**
     * Invoke patch.
     *
     * @param abstractRequestBuilder the abstract request builder
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokePatch(AbstractRequestBuilder abstractRequestBuilder) {
        try {
            String url = abstractRequestBuilder.getURL();

            LOGGER.debug("PATCH URL " + url);

            HttpRequestWithBody request = Unirest.patch(url);

            if (!headers.isEmpty()) {
                request.headers(headers);

                LOGGER.debug("PATCH Headers: " + headers);
            }

            String body = abstractRequestBuilder.getBody();
            if (body != null) {
                request.body(body);

                LOGGER.debug("PATCH Body: " + body);
            }

            return request.asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return null;
    }

    /**
     * Invoke put http response.
     *
     * @param abstractRequestBuilder the abstract request builder
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokePut(AbstractRequestBuilder abstractRequestBuilder) {
        try {
            String url = abstractRequestBuilder.getURL();

            LOGGER.debug("PUT URL " + url);

            HttpRequestWithBody request = Unirest.put(url);

            if (!headers.isEmpty()) {
                request.headers(headers);

                LOGGER.debug("PUT Headers: " + headers);
            }

            String body = abstractRequestBuilder.getBody();
            if (body != null) {
                request.body(body);

                LOGGER.debug("PUT Body: " + body);
            }

            return request.asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return null;
    }

    /**
     * Invoke delete.
     *
     * @param abstractRequestBuilder the abstract request builder
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokeDelete(AbstractRequestBuilder abstractRequestBuilder) {
        try {
            String url = abstractRequestBuilder.getURL();

            LOGGER.debug("DELETE URL " + url);

            HttpRequestWithBody request = Unirest.delete(url);

            if (!headers.isEmpty()) {
                request.headers(headers);

                LOGGER.debug("DELETE Headers: " + headers);
            }

            String body = abstractRequestBuilder.getBody();
            if (body != null) {
                request.body(body);

                LOGGER.debug("DELETE Body: " + body);
            }

            return request.asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return null;
    }

    /**
     * Invoke options.
     *
     * @param abstractRequestBuilder the abstract request builder
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokeOptions(AbstractRequestBuilder abstractRequestBuilder) {
        try {
            String url = abstractRequestBuilder.getURL();

            LOGGER.debug("OPTIONS URL " + url);

            HttpRequestWithBody request = Unirest.options(url);

            if (!headers.isEmpty()) {
                request.headers(headers);

                LOGGER.debug("OPTIONS Headers: " + headers);
            }

            String body = abstractRequestBuilder.getBody();
            if (body != null) {
                request.body(body);

                LOGGER.debug("OPTIONS Body: " + body);
            }

            return request.asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return null;
    }

    /**
     * Gets the response object.
     *
     * @param <T>          the generic type
     * @param httpResponse the http response
     * @param type         the type
     *
     * @return the response object
     */
    public <T> T getResponseObject(HttpResponse<InputStream> httpResponse, Type type) {
        T responseObjectFromJson = null;

        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(httpResponse.getRawBody()))) {
            responseObjectFromJson = GsonUtil.GSON.fromJson(jsonReader, type);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }

        return responseObjectFromJson;
    }

    /**
     * Gets response json.
     *
     * @param httpResponse the http response
     *
     * @return the response json
     */
    public JsonElement getResponseJSON(HttpResponse<InputStream> httpResponse) {
        JsonElement responseJsonElement = null;

        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(httpResponse.getRawBody()))) {
            responseJsonElement = GsonUtil.JSON_PARSER.parse(jsonReader);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }

        return responseJsonElement;
    }
}
