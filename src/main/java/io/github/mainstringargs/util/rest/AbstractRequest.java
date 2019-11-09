package io.github.mainstringargs.util.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The type Abstract request.
 */
public abstract class AbstractRequest {

    /** The logger. */
    private static final Logger LOGGER = LogManager.getLogger(AbstractRequest.class);

    /** The constant annotationClassCache. */
    private final HashMap<Class, ArrayList<SerializedName>> classAnnotationCache = new HashMap<>();

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

            LOGGER.debug("POST URL " + url);

            HttpRequestWithBody request = Unirest.post(url);

            if (!headers.isEmpty()) {
                request.headers(headers);
            }

            String body = abstractRequestBuilder.getBody();
            if (body != null) {
                request.body(body);
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
            }

            String body = abstractRequestBuilder.getBody();
            if (body != null) {
                request.body(body);
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
            }

            String body = abstractRequestBuilder.getBody();
            if (body != null) {
                request.body(body);
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
            }

            String body = abstractRequestBuilder.getBody();
            if (body != null) {
                request.body(body);
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
            }

            String body = abstractRequestBuilder.getBody();
            if (body != null) {
                request.body(body);
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

    /**
     * Checks if all Gson {@link SerializedName} annotation values (including inherited ones) in the JSON POJO are
     * present in the <strong>immediate</strong> JSON object.
     *
     * @param jsonPOJOClass the json pojo class
     * @param jsonObject    the json object
     *
     * @return the boolean
     */
    public boolean doesGSONPOJOMatch(Class jsonPOJOClass, JsonObject jsonObject) {
        ArrayList<SerializedName> gsonSerializedNameAnnotations = getGSONSerializedNameAnnotations(jsonPOJOClass);
        Set<String> jsonObjectKeys = jsonObject.keySet();

        for (SerializedName serializedName : gsonSerializedNameAnnotations) {
            // Check main serialized name
            if (!jsonObjectKeys.contains(serializedName.value())) {
                // Check alternate serialized names
                String[] alternates = serializedName.alternate();
                boolean match = false;

                for (String alternate : alternates) { // Loop through all alternates
                    if (jsonObjectKeys.contains(alternate)) {
                        match = true;
                        break;
                    }
                }
                if (!match) { // Check if we didn't find the name at all
                    return false;
                }
                // Found main serialized name so continue through loop
            }
            // Found main serialized name so continue through loop
        }

        return true;
    }

    /**
     * Gets gson serialized name annotations.
     *
     * @param theClass the the class
     *
     * @return the gson serialized name annotations
     */
    private synchronized ArrayList<SerializedName> getGSONSerializedNameAnnotations(Class theClass) {
        // Use a caching system because Reflection can be somewhat expensive
        if (classAnnotationCache.containsKey(theClass)) {
            return classAnnotationCache.get(theClass);
        }

        LOGGER.debug("Caching Gson @SerializedName annotations for " + theClass.getName());

        // Below is expensive, but it only has to be done once per class.

        ArrayList<SerializedName> serializedNameAnnotations = new ArrayList<>();

        Class currentClass = theClass;
        do {
            for (Field field : currentClass.getDeclaredFields()) { // Loop through all the fields
                for (Annotation annotation : field.getDeclaredAnnotations()) { // Loop through all the field annotations
                    if (annotation instanceof SerializedName) {
                        serializedNameAnnotations.add((SerializedName) annotation);
                    }
                }
            }

            currentClass = currentClass.getSuperclass();
        } while (currentClass != null); // Loop through all inherited classes

        classAnnotationCache.put(theClass, serializedNameAnnotations);

        return serializedNameAnnotations;
    }
}
