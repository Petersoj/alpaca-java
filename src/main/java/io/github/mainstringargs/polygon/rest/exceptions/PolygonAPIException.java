package io.github.mainstringargs.polygon.rest.exceptions;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

/**
 * The Class PolygonAPIException.
 */
public class PolygonAPIException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The http response. */
  private transient HttpResponse<JsonNode> httpResponse;

  /** The http response code. */
  private int httpResponseCode = -1;

  /** The http response message. */
  private String httpResponseMessage;

  /** The polygon response code. */
  private int polygonResponseCode = -1;

  /** The polygon response message. */
  private String polygonResponseMessage;

  /**
   * Instantiates a new polygon API exception.
   *
   * @param httpResponse the http response
   */
  public PolygonAPIException(HttpResponse<JsonNode> httpResponse) {
    httpResponseCode = httpResponse.getStatus();
    httpResponseMessage = httpResponse.getStatusText();

    JsonNode jsonNode = httpResponse.getBody();

    if (jsonNode != null) {
      if (jsonNode.getObject().has("code")) {
        polygonResponseCode = jsonNode.getObject().getInt("code");
      }

      if (jsonNode.getObject().has("message")) {
        polygonResponseMessage = jsonNode.getObject().getString("message");
      } else {
        // if all else fails, just use the json
        polygonResponseMessage = jsonNode.getObject().toString();
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
   * Gets the polygon response code.
   *
   * @return the polygon response code
   */
  public int getPolygonResponseCode() {
    return polygonResponseCode;
  }


  /**
   * Gets the polygon response message.
   *
   * @return the polygon response message
   */
  public String getPolygonResponseMessage() {
    return polygonResponseMessage;
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public String getMessage() {
    String message = "Generic Polygon Exception";

    if (polygonResponseCode != -1) {

      message = polygonResponseCode + "";

      if (polygonResponseMessage != null && !polygonResponseMessage.isEmpty()) {
        message += ": " + polygonResponseMessage;
      }

    } else if (httpResponseMessage != null && !httpResponseMessage.isEmpty()) {

      message = httpResponseCode + ": " + httpResponseMessage;

    }

    return message;
  }

}
