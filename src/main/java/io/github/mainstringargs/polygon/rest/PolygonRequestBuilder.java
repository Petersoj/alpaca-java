package io.github.mainstringargs.polygon.rest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.google.gson.JsonObject;

/**
 * The Class PolygonRequestBuilder.
 */
public abstract class PolygonRequestBuilder {


  /** The version. */
  private String version = "v1";

  /** The Constant URL_SEPARATOR. */
  public final static String URL_SEPARATOR = "/";

  /** The url parameters. */
  public final Map<String, String> urlParameters = new LinkedHashMap<String, String>();

  /** The body properties. */
  public final Map<String, String> bodyProperties = new LinkedHashMap<String, String>();

  /** The base url. */
  private String baseUrl;

  /** The appended endpoints. */
  private List<String> appendedEndpoints = new ArrayList<String>();

  /** The default endpoint. */
  private boolean defaultEndpoint = true;


  /**
   * Instantiates a new polygon request builder.
   *
   * @param baseUrl the base url
   */
  public PolygonRequestBuilder(String baseUrl) {
    this.baseUrl = baseUrl;

  }


  /**
   * Sets the version.
   *
   * @param version the new version
   */
  public void setVersion(String version) {
    this.version = version;

  }

  /**
   * Append URL parameter.
   *
   * @param parameterKey the parameter key
   * @param parameterValue the parameter value
   */
  public void appendURLParameter(String parameterKey, String parameterValue) {
    urlParameters.put(parameterKey, parameterValue);
  }

  /**
   * Append body property.
   *
   * @param parameterKey the parameter key
   * @param parameterValue the parameter value
   */
  public void appendBodyProperty(String parameterKey, String parameterValue) {

    bodyProperties.put(parameterKey, parameterValue);
  }

  /**
   * Checks if is default endpoint.
   *
   * @return true, if is default endpoint
   */
  public boolean isDefaultEndpoint() {
    return defaultEndpoint;
  }

  /**
   * Sets the default endpoint.
   *
   * @param defaultEndpoint the new default endpoint
   */
  public void setDefaultEndpoint(boolean defaultEndpoint) {
    this.defaultEndpoint = defaultEndpoint;
  }

  /**
   * Gets the body as JSON.
   *
   * @return the body as JSON
   */
  public String getBodyAsJSON() {
    JsonObject jsonBody = new JsonObject();

    for (Entry<String, String> entry : bodyProperties.entrySet()) {
      jsonBody.addProperty(entry.getKey(), entry.getValue());
    }

    return jsonBody.toString();

  }


  /**
   * Append endpoint.
   *
   * @param endpoint the endpoint
   */
  public void appendEndpoint(String endpoint) {
    appendedEndpoints.add(endpoint);

  }


  /**
   * Gets the endpoint.
   *
   * @return the endpoint
   */
  public abstract String getEndpoint();

  /**
   * Gets the url.
   *
   * @return the url
   */
  public String getURL() {

    StringBuilder builder = new StringBuilder(baseUrl);
    builder.append(URL_SEPARATOR);
    builder.append(version);

    if (defaultEndpoint) {
      builder.append(URL_SEPARATOR);
      builder.append(getEndpoint());
    }

    for (String endpoint : appendedEndpoints) {
      builder.append(URL_SEPARATOR);
      builder.append(endpoint);
    }


    if (!urlParameters.isEmpty()) {
      builder.append('?');

      for (Entry<String, String> entry : urlParameters.entrySet()) {
        builder.append(entry.getKey().trim());
        builder.append('=');
        builder.append(entry.getValue().trim());
        builder.append('&');
      }

      // removes last &
      builder.deleteCharAt(builder.length() - 1);
    }

    return builder.toString();
  }



}
