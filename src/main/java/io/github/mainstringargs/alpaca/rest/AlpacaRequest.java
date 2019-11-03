package io.github.mainstringargs.alpaca.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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

    /** The constant JSON_PARSER. */
    private static final JsonParser JSON_PARSER = new JsonParser();

    /** The constant annotationClassCache. */
    private static final HashMap<Class, ArrayList<SerializedName>> classAnnotationCache = new HashMap<>();

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
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokeGet(AlpacaRequestBuilder builder) {
        HttpResponse<InputStream> response = null;

        try {
            LOGGER.debug("Get URL " + builder.getURL());

            response = Unirest.get(builder.getURL())
                    .header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE)
                    .header(API_KEY_ID, keyId).header(API_SECRET_KEY, secret).asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return response;
    }

    /**
     * Invoke post.
     *
     * @param builder the builder
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokePost(AlpacaRequestBuilder builder) {
        HttpResponse<InputStream> response = null;

        try {
            LOGGER.debug("Post URL " + builder.getURL());
            LOGGER.debug("Post Body " + builder.getBodyAsJSON());

            response = Unirest.post(builder.getURL())
                    .header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE).header(API_KEY_ID, keyId)
                    .header(API_SECRET_KEY, secret).body(builder.getBodyAsJSON()).asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return response;
    }

    /**
     * Invoke delete.
     *
     * @param builder the builder
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokeDelete(AlpacaRequestBuilder builder) {
        HttpResponse<InputStream> response = null;
        try {
            LOGGER.debug("Delete URL " + builder.getURL());

            response = Unirest.delete(builder.getURL())
                    .header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE)
                    .header(API_KEY_ID, keyId).header(API_SECRET_KEY, secret).asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return response;
    }

    /**
     * Invoke patch.
     *
     * @param builder the builder
     *
     * @return the http response
     */
    public HttpResponse<InputStream> invokePatch(AlpacaRequestBuilder builder) {
        HttpResponse<InputStream> response = null;
        try {
            LOGGER.debug("Patch URL " + builder.getURL());

            response = Unirest.patch(builder.getURL())
                    .header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE)
                    .header(API_KEY_ID, keyId).header(API_SECRET_KEY, secret).asBinary();
        } catch (UnirestException e) {
            LOGGER.error("UnirestException", e);
        }

        return response;
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
            responseObjectFromJson = GSON.fromJson(jsonReader, type);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }

        return responseObjectFromJson;
    }

    public JsonElement getResponseJSON(HttpResponse<InputStream> httpResponse) {
        JsonElement responseJsonElement = null;

        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(httpResponse.getRawBody()))) {
            responseJsonElement = JSON_PARSER.parse(jsonReader);
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
