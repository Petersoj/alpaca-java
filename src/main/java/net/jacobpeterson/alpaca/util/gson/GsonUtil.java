package net.jacobpeterson.alpaca.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * {@link GsonUtil} contains utility methods relating to {@link Gson}.
 */
public class GsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(GsonUtil.class);
    private static final HashMap<Class<?>, ArrayList<SerializedName>> CLASS_ANNOTATION_CACHE = new HashMap<>();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .enableComplexMapKeySerialization()
            .setLenient()
            .create();

    /**
     * Checks if all {@link Gson} {@link SerializedName} annotation values (including inherited ones) in the JSON POJO
     * are present in the <strong>immediate</strong> JSON object.
     *
     * @param jsonPOJOClass the JSON POJO class
     * @param jsonObject    the JSON object
     *
     * @return a boolean
     */
    public static boolean doesGsonPOJOMatch(Class<?> jsonPOJOClass, JsonObject jsonObject) {
        List<SerializedName> gsonSerializedNameAnnotations = getGsonSerializedNameAnnotations(jsonPOJOClass);
        Set<String> jsonObjectKeys = jsonObject.keySet();

        List<String> classKeys = new ArrayList<>();
        gsonSerializedNameAnnotations.forEach((c) -> classKeys.add(c.value()));

        for (String key : jsonObjectKeys) {
            if (!classKeys.contains(key)) {
                LOGGER.trace("Class '{}' did not contain key '{}'", jsonPOJOClass, key);
                return false;
            }
        }

        return true;
    }

    /**
     * Gets {@link Gson} {@link SerializedName} annotations from a given {@link Class}
     *
     * @param clazz the {@link Class}
     *
     * @return a {@link List} of {@link SerializedName}s
     */
    private static synchronized List<SerializedName> getGsonSerializedNameAnnotations(Class<?> clazz) {
        // Use a caching system because Reflection can be quite expensive
        if (CLASS_ANNOTATION_CACHE.containsKey(clazz)) {
            return CLASS_ANNOTATION_CACHE.get(clazz);
        }

        LOGGER.debug("Caching Gson @SerializedName annotations for {}", clazz.getName());

        // Below is expensive, but it only has to be done once per class.

        List<SerializedName> serializedNameAnnotations = new ArrayList<>();

        Class<?> currentClass = clazz;
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

        CLASS_ANNOTATION_CACHE.put(clazz, serializedNameAnnotations);

        return serializedNameAnnotations;
    }
}
