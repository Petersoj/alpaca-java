package io.github.mainstringargs.alpaca.rest;

/**
 * The Class AlpacaUrlBuilder.
 */
public abstract class AlpacaUrlBuilder {

  /** The builder. */
  protected StringBuilder builder;

  /** The Constant VERSION. */
  private final static String VERSION = "v1";

  /** The Constant URL_SEPARATOR. */
  public final static String URL_SEPARATOR = "/";


  /**
   * Instantiates a new alpaca url builder.
   *
   * @param baseUrl the base url
   */
  public AlpacaUrlBuilder(String baseUrl) {
    builder = new StringBuilder(baseUrl);
    builder.append(URL_SEPARATOR);
    builder.append(VERSION);
    builder.append(URL_SEPARATOR);
    builder.append(getEndpoint());

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
    return builder.toString();
  }


}
