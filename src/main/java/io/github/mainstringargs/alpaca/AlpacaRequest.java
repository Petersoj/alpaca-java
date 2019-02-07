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

public class AlpacaRequest {

  private static Logger LOGGER = LogManager.getLogger(AlpacaRequest.class);

  /** The Constant USER_AGENT_KEY. */
  private static final String USER_AGENT_KEY = "user-agent";
  private static final String API_KEY_ID = "APCA-API-KEY-ID";
  private static final String API_SECRET_KEY = "APCA-API-SECRET-KEY";

  /**
   * Invoke.
   *
   * @param builder the builder
   * @return the http response
   */
  public HttpResponse<JsonNode> invoke(AlpacaUrlBuilder builder) {
    HttpResponse<JsonNode> getRequest = null;
    try {
      getRequest =
          Unirest.get(builder.getURL())
              .header(USER_AGENT_KEY, AlpacaProperties.UESR_AGENT_VALUE)
              .header(API_KEY_ID, AlpacaProperties.KEY_ID_VALUE)
              .header(API_SECRET_KEY, AlpacaProperties.SECRET_VALUE).asJson();
    } catch (UnirestException e) {
      LOGGER.debug("UnirestException", e);
    }

    return getRequest;
  }

  /**
   * Gets the json.
   *
   * @param jsonNode the json node
   * @return the json
   */
  public JsonNode getJson(HttpResponse<JsonNode> jsonNode) {
    return jsonNode.getBody();
  }


//  /**
//   * Gets the finance data.
//   *
//   * @param jsonNode the json node
//   * @return the finance data
//   */
//  public YahooFinanceData getFinanceData(HttpResponse<JsonNode> jsonNode) {
//
//    GsonBuilder gsonBuilder = new GsonBuilder();
//    gsonBuilder.setLenient();
//    Gson gson = gsonBuilder.create();
//
//    FinanceData rawFinanceData;
//
//    BufferedReader br = null;
//    try {
//      br = new BufferedReader(new InputStreamReader(jsonNode.getRawBody()));
//      rawFinanceData = gson.fromJson(br, FinanceData.class);
//    } catch (Exception e) {
//      LOGGER.debug("Exception", e);
//      rawFinanceData = new FinanceData();
//    } finally {
//      if (br != null) {
//        try {
//          br.close();
//        } catch (IOException e) {
//          LOGGER.debug("IOException", e);
//        }
//      }
//    }
//
//
//    return new YahooFinanceData(rawFinanceData);
//  }
}
