package io.github.mainstringargs.alpaca.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;

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

  /** The key id. */
  private String keyId;

  /** The secret. */
  private String secret;

  /**
   * Instantiates a new alpaca request.
   *
   * @param keyId the key id
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
   * @return the http response
   */
  public HttpResponse<JsonNode> invokeGet(AlpacaRequestBuilder builder) {
    HttpResponse<JsonNode> response = null;
    try {

      LOGGER.info("Get URL " + builder.getURL());

      response =
          Unirest.get(builder.getURL()).header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE)
              .header(API_KEY_ID, keyId).header(API_SECRET_KEY, secret).asJson();


    } catch (UnirestException e) {
      LOGGER.info("UnirestException", e);
    }

    return response;
  }

  /**
   * Invoke post.
   *
   * @param builder the builder
   * @return the http response
   */
  public HttpResponse<JsonNode> invokePost(AlpacaRequestBuilder builder) {
    HttpResponse<JsonNode> response = null;
    try {

      LOGGER.info("Post URL " + builder.getURL());
      LOGGER.debug("Post Body " + builder.getBodyAsJSON());

      response = Unirest.post(builder.getURL())
          .header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE).header(API_KEY_ID, keyId)
          .header(API_SECRET_KEY, secret).body(builder.getBodyAsJSON()).asJson();


    } catch (UnirestException e) {
      LOGGER.info("UnirestException", e);
    }

    return response;
  }

  /**
   * Invoke delete.
   *
   * @param builder the builder
   * @return the http response
   */
  public HttpResponse<JsonNode> invokeDelete(AlpacaRequestBuilder builder) {
    HttpResponse<JsonNode> response = null;
    try {

      LOGGER.info("Delete URL " + builder.getURL());

      response =
          Unirest.delete(builder.getURL()).header(USER_AGENT_KEY, AlpacaProperties.USER_AGENT_VALUE)
              .header(API_KEY_ID, keyId).header(API_SECRET_KEY, secret).asJson();


    } catch (UnirestException e) {
      LOGGER.info("UnirestException", e);
    }

    return response;
  }


  /**
   * Gets the response object.
   *
   * @param <T> the generic type
   * @param httpResponse the http response
   * @param type the type
   * @return the response object
   */
  public <T> T getResponseObject(HttpResponse<JsonNode> httpResponse, Type type) {

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setLenient();
    Gson gson = gsonBuilder.create();

    T responseObjectFromJson = null;

    BufferedReader br = null;

    // System.out.println(httpResponse.getStatus() + " " + httpResponse.getStatusText() + " "
    // + httpResponse.getHeaders().entrySet());
    // System.out.println(httpResponse.getBody());

    try {
      br = new BufferedReader(new InputStreamReader(httpResponse.getRawBody()));
      responseObjectFromJson = gson.fromJson(br, type);
    } catch (Exception e) {
      LOGGER.info("Exception", e);
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          LOGGER.info("IOException", e);
        }
      }
    }

    return responseObjectFromJson;

  }

}
