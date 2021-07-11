package net.jacobpeterson.alpaca.util.okhttp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * {@link JSONBodyBuilder} is builder HTTP bodies.
 */
public class JSONBodyBuilder {

    /** The UTF-8 JSON {@link MediaType}. */
    public static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");

    private String bodyJSON;
    private JsonObject bodyJsonObject;

    /**
     * Instantiates a new {@link JSONBodyBuilder}.
     */
    public JSONBodyBuilder() {
        bodyJsonObject = new JsonObject();
    }

    /**
     * Instantiates a new {@link JSONBodyBuilder}.
     *
     * @param bodyJSON a JSON body {@link String} that is used instead of the internal {@link #bodyJsonObject}
     */
    public JSONBodyBuilder(String bodyJSON) {
        this.bodyJSON = bodyJSON;
    }

    /**
     * Appends a {@link String} to {@link #bodyJsonObject}.
     *
     * @param key   the key
     * @param value the value
     */
    public void appendJSONBodyProperty(String key, String value) {
        bodyJsonObject.addProperty(key, value);
    }

    /**
     * Appends a {@link JsonElement} property to {@link #bodyJsonObject}.
     *
     * @param key   the key
     * @param value the {@link JsonElement} value
     */
    public void appendJSONBodyJSONProperty(String key, JsonElement value) {
        bodyJsonObject.add(key, value);
    }

    /**
     * Builds a {@link RequestBody}.
     *
     * @return a {@link RequestBody}
     */
    public RequestBody build() {
        return RequestBody.create(bodyJSON != null ? bodyJSON : bodyJsonObject.toString(), JSON_MEDIA_TYPE);
    }
}
