package net.jacobpeterson.alpaca.abstracts.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import net.jacobpeterson.alpaca.util.gson.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link AbstractRequest} contains methods for HTTP requests.
 */
public abstract class AbstractRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRequest.class);

    protected final Map<String, String> headers;

    /**
     * Instantiates a new {@link AbstractRequest}.
     */
    public AbstractRequest() {
        headers = new HashMap<>();
    }

    /**
     * Invoke GET request.
     *
     * @param abstractRequestBuilder the {@link AbstractRequestBuilder}
     *
     * @return the {@link HttpResponse}
     *
     * @throws UnirestException thrown for {@link UnirestException}s
     */
    public HttpResponse<InputStream> invokeGet(AbstractRequestBuilder abstractRequestBuilder) throws UnirestException {
        String url = abstractRequestBuilder.buildURL();

        LOGGER.debug("GET URL {}", url);

        GetRequest request = Unirest.get(url);

        if (!headers.isEmpty()) {
            request.headers(headers);

            LOGGER.debug("GET Headers: {}", headers);
        }

        return request.asBinary();
    }

    /**
     * Invoke HEAD request.
     *
     * @param abstractRequestBuilder the {@link AbstractRequestBuilder}
     *
     * @return the {@link HttpResponse}
     *
     * @throws UnirestException thrown for {@link UnirestException}s
     */
    public HttpResponse<InputStream> invokeHead(AbstractRequestBuilder abstractRequestBuilder) throws UnirestException {
        String url = abstractRequestBuilder.buildURL();

        LOGGER.debug("HEAD URL {}", url);

        GetRequest request = Unirest.head(url);

        if (!headers.isEmpty()) {
            request.headers(headers);

            LOGGER.debug("HEAD Headers: {}", headers);
        }

        return request.asBinary();
    }

    /**
     * Invoke POST request.
     *
     * @param abstractRequestBuilder the {@link AbstractRequestBuilder}
     *
     * @return the {@link HttpResponse}
     *
     * @throws UnirestException thrown for {@link UnirestException}s
     */
    public HttpResponse<InputStream> invokePost(AbstractRequestBuilder abstractRequestBuilder) throws UnirestException {
        String url = abstractRequestBuilder.buildURL();

        LOGGER.debug("POST URL: {}", url);

        HttpRequestWithBody request = Unirest.post(url);

        if (!headers.isEmpty()) {
            request.headers(headers);

            LOGGER.debug("POST Headers: {}", headers);
        }

        String body = abstractRequestBuilder.buildBody();
        if (body != null) {
            request.body(body);

            LOGGER.debug("POST Body: {}", body);
        }

        return request.asBinary();
    }

    /**
     * Invoke PATCH request.
     *
     * @param abstractRequestBuilder the {@link AbstractRequestBuilder}
     *
     * @return the {@link HttpResponse}
     *
     * @throws UnirestException thrown for {@link UnirestException}s
     */
    public HttpResponse<InputStream> invokePatch(AbstractRequestBuilder abstractRequestBuilder)
            throws UnirestException {
        String url = abstractRequestBuilder.buildURL();

        LOGGER.debug("PATCH URL {}", url);

        HttpRequestWithBody request = Unirest.patch(url);

        if (!headers.isEmpty()) {
            request.headers(headers);

            LOGGER.debug("PATCH Headers: {}", headers);
        }

        String body = abstractRequestBuilder.buildBody();
        if (body != null) {
            request.body(body);

            LOGGER.debug("PATCH Body: {}", body);
        }

        return request.asBinary();
    }

    /**
     * Invoke PUT request.
     *
     * @param abstractRequestBuilder the {@link AbstractRequestBuilder}
     *
     * @return the {@link HttpResponse}
     *
     * @throws UnirestException thrown for {@link UnirestException}s
     */
    public HttpResponse<InputStream> invokePut(AbstractRequestBuilder abstractRequestBuilder) throws UnirestException {
        String url = abstractRequestBuilder.buildURL();

        LOGGER.debug("PUT URL {}", url);

        HttpRequestWithBody request = Unirest.put(url);

        if (!headers.isEmpty()) {
            request.headers(headers);

            LOGGER.debug("PUT Headers: {}", headers);
        }

        String body = abstractRequestBuilder.buildBody();
        if (body != null) {
            request.body(body);

            LOGGER.debug("PUT Body: " + body);
        }

        return request.asBinary();
    }

    /**
     * Invoke DELETE request.
     *
     * @param abstractRequestBuilder the {@link AbstractRequestBuilder}
     *
     * @return the {@link HttpResponse}
     *
     * @throws UnirestException thrown for {@link UnirestException}s
     */
    public HttpResponse<InputStream> invokeDelete(AbstractRequestBuilder abstractRequestBuilder)
            throws UnirestException {
        String url = abstractRequestBuilder.buildURL();

        LOGGER.debug("DELETE URL {}", url);

        HttpRequestWithBody request = Unirest.delete(url);

        if (!headers.isEmpty()) {
            request.headers(headers);

            LOGGER.debug("DELETE Headers: {}", headers);
        }

        String body = abstractRequestBuilder.buildBody();
        if (body != null) {
            request.body(body);

            LOGGER.debug("DELETE Body: {}", body);
        }

        return request.asBinary();
    }

    /**
     * Invoke OPTIONS request.
     *
     * @param abstractRequestBuilder the {@link AbstractRequestBuilder}
     *
     * @return the {@link HttpResponse}
     *
     * @throws UnirestException thrown for {@link UnirestException}s
     */
    public HttpResponse<InputStream> invokeOptions(AbstractRequestBuilder abstractRequestBuilder)
            throws UnirestException {
        String url = abstractRequestBuilder.buildURL();

        LOGGER.debug("OPTIONS URL {}", url);

        HttpRequestWithBody request = Unirest.options(url);

        if (!headers.isEmpty()) {
            request.headers(headers);

            LOGGER.debug("OPTIONS Headers: {}", headers);
        }

        String body = abstractRequestBuilder.buildBody();
        if (body != null) {
            request.body(body);

            LOGGER.debug("OPTIONS Body: {}", body);
        }

        return request.asBinary();
    }

    /**
     * Gets a parsed Object with {@link GsonUtil#GSON} given {@link HttpResponse#getRawBody()} JSON.
     *
     * @param <T>          the generic type
     * @param httpResponse the {@link HttpResponse}
     * @param type         the type of object
     *
     * @return the {@link HttpResponse#getRawBody()} parsed Object
     */
    public <T> T getResponseObject(HttpResponse<InputStream> httpResponse, Type type) {
        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(httpResponse.getRawBody()))) {
            return GsonUtil.GSON.fromJson(jsonReader, type);
        } catch (IOException ioException) {
            LOGGER.error("Could not parse response JSON object", ioException);
            return null;
        }
    }

    /**
     * Gets a {@link JsonElement} with {@link GsonUtil#GSON} given {@link HttpResponse#getRawBody()} JSON.
     *
     * @param httpResponse the {@link HttpResponse}
     *
     * @return the {@link HttpResponse#getRawBody()} {@link JsonElement}
     */
    public JsonElement getResponseJSON(HttpResponse<InputStream> httpResponse) {
        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(httpResponse.getRawBody()))) {
            return JsonParser.parseReader(jsonReader);
        } catch (IOException ioException) {
            LOGGER.error("Could not parse response JSON object", ioException);
            return null;
        }
    }
}
