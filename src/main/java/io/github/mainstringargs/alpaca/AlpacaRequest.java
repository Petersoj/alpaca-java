package io.github.mainstringargs.alpaca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * The Class AlpacaRequest.
 */
public class AlpacaRequest {

  /** The logger. */
  private static Logger LOGGER = LogManager.getLogger(AlpacaRequest.class);

  /** The Constant USER_AGENT_KEY. */
  private static final String USER_AGENT_KEY = "user-agent";

  /** The Constant API_KEY_ID. */
  private static final String API_KEY_ID = "APCA-API-KEY-ID";

  /** The Constant API_SECRET_KEY. */
  private static final String API_SECRET_KEY = "APCA-API-SECRET-KEY";

  /**
   * Invoke.
   *
   * @param builder the builder
   * @return the http response
   */
  public static HttpResponse<JsonNode> invokeGet(AlpacaUrlBuilder builder) {
    HttpResponse<JsonNode> response = null;
    try {

      LOGGER.info("Get URL " + builder.getURL());

      response =
          Unirest.get(builder.getURL()).header(USER_AGENT_KEY, AlpacaProperties.UESR_AGENT_VALUE)
              .header(API_KEY_ID, AlpacaProperties.KEY_ID_VALUE)
              .header(API_SECRET_KEY, AlpacaProperties.SECRET_VALUE).asJson();


    } catch (UnirestException e) {
      LOGGER.debug("UnirestException", e);
    }

    return response;
  }



  /**
   * Gets the response object.
   *
   * @param <T> the generic type
   * @param httpResponse the http response
   * @param clazz the clazz
   * @return the response object
   */
  public static <T> T getResponseObject(HttpResponse<JsonNode> httpResponse, Class<T> clazz) {

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setLenient();
    Gson gson = gsonBuilder.create();

    T responseObjectFromJson = null;

    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(httpResponse.getRawBody()));
      responseObjectFromJson = gson.fromJson(br, clazz);
    } catch (Exception e) {
      LOGGER.debug("Exception", e);
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          LOGGER.debug("IOException", e);
        }
      }
    }

    return responseObjectFromJson;

  }

}
