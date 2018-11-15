package io.github.mainstringargs.yahooFinance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.mainstringargs.yahooFinance.domain.FinanceData;

// TODO: Auto-generated Javadoc
/**
 * The Class YahooFinanceRequest.
 */
public class YahooFinanceRequest {

  /** The Constant USER_AGENT_KEY. */
  private static final String USER_AGENT_KEY = "user-agent";
  
  /** The user agent value. */
  private String userAgentValue =
      "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36";

  /**
   * Gets the user agent value.
   *
   * @return the user agent value
   */
  public String getUserAgentValue() {
    return userAgentValue;
  }

  /**
   * Sets the user agent value.
   *
   * @param userAgentValue the new user agent value
   */
  public void setUserAgentValue(String userAgentValue) {
    this.userAgentValue = userAgentValue;
  }

  /**
   * Invoke.
   *
   * @param builder the builder
   * @return the http response
   */
  public HttpResponse<JsonNode> invoke(YahooFinanceUrlBuilder builder) {
    HttpResponse<JsonNode> getRequest = null;
    try {
      getRequest = Unirest.get(builder.getURL()).header(USER_AGENT_KEY, userAgentValue).asJson();
    } catch (UnirestException e) {
      e.printStackTrace();
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


  /**
   * Gets the finance data.
   *
   * @param jsonNode the json node
   * @return the finance data
   */
  public YahooFinanceData getFinanceData(HttpResponse<JsonNode> jsonNode) {

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setLenient();
    Gson gson = gsonBuilder.create();

    FinanceData rawFinanceData;
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(jsonNode.getRawBody()));
      rawFinanceData = gson.fromJson(br, FinanceData.class);
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
      rawFinanceData = new FinanceData();
    }


    return new YahooFinanceData(rawFinanceData);
  }
}
