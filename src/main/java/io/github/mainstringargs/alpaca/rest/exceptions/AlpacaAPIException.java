package io.github.mainstringargs.alpaca.rest.exceptions;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

/**
 * The Class AlpacaAPIException.
 */
public class AlpacaAPIException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The http response. */
  private transient HttpResponse<JsonNode> httpResponse;

  /** The http response code. */
  private int httpResponseCode = -1;

  /** The http response message. */
  private String httpResponseMessage;

  /** The alpaca response code. */
  private int alpacaResponseCode = -1;

  /** The alpaca response message. */
  private String alpacaResponseMessage;

  /**
   * Instantiates a new alpaca API exception.
   *
   * @param httpResponse the http response
   */
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

  /**
   * Gets the http response.
   *
   * @return the http response
   */
  public HttpResponse<JsonNode> getHttpResponse() {
    return httpResponse;
  }


  /**
   * Gets the http response code.
   *
   * @return the http response code
   */
  public int getHttpResponseCode() {
    return httpResponseCode;
  }


  /**
   * Gets the http response message.
   *
   * @return the http response message
   */
  public String getHttpResponseMessage() {
    return httpResponseMessage;
  }


  /**
   * Gets the alpaca response code.
   *
   * @return the alpaca response code
   */
  public int getAlpacaResponseCode() {
    return alpacaResponseCode;
  }


  /**
   * Gets the alpaca response message.
   *
   * @return the alpaca response message
   */
  public String getAlpacaResponseMessage() {
    return alpacaResponseMessage;
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Throwable#getMessage()
   */
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
