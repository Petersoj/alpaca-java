package io.github.mainstringargs.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.time.LocalDateTime;

public class GsonUtil {

    /** The constant REQUEST_GSON which include ISO date time -> LocalDateTime objects */
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setLenient()
            .create();

    /** The constant JSON_PARSER. */
    public static final JsonParser JSON_PARSER = new JsonParser();

}
