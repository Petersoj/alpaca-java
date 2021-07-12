package net.jacobpeterson.alpaca.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * {@link LocalTimeAdapter} is a {@link Gson} adapter for {@link LocalTime}s.
 */
public class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    @Override
    public JsonElement serialize(LocalTime localTime, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(localTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
    }

    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_LOCAL_TIME);
    }
}
