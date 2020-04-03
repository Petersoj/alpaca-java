package io.github.mainstringargs.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GsonUtil {

    /** The logger. */
    private static final Logger LOGGER = LogManager.getLogger(GsonUtil.class);

    /** The constant CLASS_ANNOTATION_CACHE. */
    private static final HashMap<Class, ArrayList<SerializedName>> CLASS_ANNOTATION_CACHE = new HashMap<>();

    /** The constant GSON which include ISO date time to ZonedDateTime objects */
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .enableComplexMapKeySerialization()
            .setLenient()
            .create();

    /** The constant JSON_PARSER. */
    public static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * Checks if all Gson {@link SerializedName} annotation values (including inherited ones) in the JSON POJO are
     * present in the <strong>immediate</strong> JSON object.
     *
     * @param jsonPOJOClass the json pojo class
     * @param jsonObject    the json object
     *
     * @return the boolean
     */
    public static boolean doesGsonPOJOMatch(Class jsonPOJOClass, JsonObject jsonObject) {
        ArrayList<SerializedName> gsonSerializedNameAnnotations = getGsonSerializedNameAnnotations(jsonPOJOClass);
        Set<String> jsonObjectKeys = jsonObject.keySet();

        List<String> classKeys = new ArrayList<>();
        gsonSerializedNameAnnotations.forEach((c) -> classKeys.add(c.value()));

        for (String key : jsonObjectKeys){
            if (!classKeys.contains(key)){
                return false;
            }
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
    private static synchronized ArrayList<SerializedName> getGsonSerializedNameAnnotations(Class theClass) {
        // Use a caching system because Reflection can be quite expensive
        if (CLASS_ANNOTATION_CACHE.containsKey(theClass)) {
            return CLASS_ANNOTATION_CACHE.get(theClass);
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

        CLASS_ANNOTATION_CACHE.put(theClass, serializedNameAnnotations);

        return serializedNameAnnotations;
    }
}
