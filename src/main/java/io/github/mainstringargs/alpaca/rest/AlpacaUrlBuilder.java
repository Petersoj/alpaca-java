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

  /** The Constant ACCOUNT_ENDPOINT. */
  public final static String ACCOUNT_ENDPOINT = "account";

  /** The Constant ORDERS_ENDPOINT. */
  public final static String ORDERS_ENDPOINT = "orders";

  /** The Constant POSITIONS_ENDPOINT. */
  public final static String POSITIONS_ENDPOINT = "positions";

  /** The Constant ASSETS_ENDPOINT. */
  public final static String ASSETS_ENDPOINT = "assets";

  /** The Constant CLOCK_ENDPOINT. */
  public final static String CLOCK_ENDPOINT = "clock";

  /** The Constant CALENDAR_ENDPOINT. */
  public final static String CALENDAR_ENDPOINT = "calendar";

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
  }

  /**
   * Clock.
   *
   * @return the alpaca url builder
   */
  public abstract AlpacaUrlBuilder endpoint();
  /**
   * Gets the url.
   *
   * @return the url
   */
  public String getURL() {
    return builder.toString();
  }


}
