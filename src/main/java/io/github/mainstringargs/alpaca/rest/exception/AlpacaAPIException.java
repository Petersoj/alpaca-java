package io.github.mainstringargs.alpaca.rest.exception;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

public class AlpacaAPIException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private HttpResponse<JsonNode> httpResponse;

  private int httpResponseCode = -1;
  private String httpResponseMessage;

  private int alpacaResponseCode = -1;
  private String alpacaResponseMessage;

  public AlpacaAPIException(HttpResponse<JsonNode> httpResponse) {
    httpResponseCode = httpResponse.getStatus();
    httpResponseMessage = httpResponse.getStatusText();

    JsonNode jsonNode = httpResponse.getBody();

    if (jsonNode != null) {
      if (jsonNode.getObject().has("code")) {
        alpacaResponseCode = jsonNode.getObject().getInt("code");
      }

      if (jsonNode.getObject().has("message")) {
        alpacaResponseMessage = jsonNode.getObject().getString("message");
      } else {
        // if all else fails, just use the json
        alpacaResponseMessage = jsonNode.getObject().toString();
      }


    }


  }

  public HttpResponse<JsonNode> getHttpResponse() {
    return httpResponse;
  }


  public int getHttpResponseCode() {
    return httpResponseCode;
  }


  public String getHttpResponseMessage() {
    return httpResponseMessage;
  }


  public int getAlpacaResponseCode() {
    return alpacaResponseCode;
  }


  public String getAlpacaResponseMessage() {
    return alpacaResponseMessage;
  }


  @Override
  public String getMessage() {
    String message = "Generic Alpaca Exception";

    if (alpacaResponseCode != -1) {

      message = alpacaResponseCode + "";

      if (alpacaResponseMessage != null && !alpacaResponseMessage.isEmpty()) {
        message += ": " + alpacaResponseMessage;
      }

    } else if (httpResponseMessage != null && !httpResponseMessage.isEmpty()) {

      message = httpResponseCode + ": " + httpResponseMessage;

    }

    return message;
  }

}
