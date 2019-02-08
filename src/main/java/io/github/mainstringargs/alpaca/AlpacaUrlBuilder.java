package io.github.mainstringargs.alpaca;

/**
 * The Class AlpacaUrlBuilder.
 */
public class AlpacaUrlBuilder {

  /** The builder. */
  private StringBuilder builder;
  /** The base url. */
  private final static String BASE_URL = AlpacaProperties.BASE_URL_VALUE;

  /** The Constant VERSION. */
  private final static String VERSION = "v1";

  /** The Constant URL_SEPARATOR. */
  private final static String URL_SEPARATOR = "/";

  /** The Constant ACCOUNT_ENDPOINT. */
  private final static String ACCOUNT_ENDPOINT = "account";

  /** The Constant ORDERS_ENDPOINT. */
  private final static String ORDERS_ENDPOINT = "orders";

  /** The Constant POSITIONS_ENDPOINT. */
  private final static String POSITIONS_ENDPOINT = "positions";

  /** The Constant ASSETS_ENDPOINT. */
  private final static String ASSETS_ENDPOINT = "assets";

  /** The Constant CLOCK_ENDPOINT. */
  private final static String CLOCK_ENDPOINT = "clock";

  /** The Constant CALENDAR_ENDPOINT. */
  private final static String CALENDAR_ENDPOINT = "calendar";

  /**
   * Instantiates a new alpaca url builder.
   */
  public AlpacaUrlBuilder() {
    builder = new StringBuilder(BASE_URL);
    builder.append(URL_SEPARATOR);
    builder.append(VERSION);
    builder.append(URL_SEPARATOR);
  }

  /**
   * Account.
   *
   * @return the alpaca url builder
   */
  public AlpacaUrlBuilder account() {
    builder.append(ACCOUNT_ENDPOINT);
    return this;
  }

  /**
   * Orders.
   *
   * @return the alpaca url builder
   */
  public AlpacaUrlBuilder orders() {
    builder.append(ORDERS_ENDPOINT);
    return this;
  }

  /**
   * Positions.
   *
   * @return the alpaca url builder
   */
  public AlpacaUrlBuilder positions() {
    builder.append(POSITIONS_ENDPOINT);
    return this;
  }

  /**
   * Clock.
   *
   * @return the alpaca url builder
   */
  public AlpacaUrlBuilder clock() {
    builder.append(CLOCK_ENDPOINT);
    return this;
  }

  /**
   * Calendar.
   *
   * @return the alpaca url builder
   */
  public AlpacaUrlBuilder calendar() {
    builder.append(ASSETS_ENDPOINT);
    return this;
  }

  /**
   * Gets the url.
   *
   * @return the url
   */
  public String getURL() {
    return builder.toString();
  }


}
