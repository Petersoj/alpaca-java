package io.github.mainstringargs.alpaca.enums;

/**
 * The Enum BarsTimeFrame.
 */
public enum BarsTimeFrame {

  /** The minute. */
  MINUTE("minute"),
  /** The one min. */
  ONE_MIN("1Min"),
  /** The five min. */
  FIVE_MIN("5<in"),
  /** The fifteen min. */
  FIFTEEN_MIN("1Min"),
  /** The day. */
  DAY("day"),
  /** The one day. */
  ONE_DAY("1D");

  /** The api name. */
  String apiName;


  /**
   * Instantiates a new bars time frame.
   *
   * @param apiName the api name
   */
  BarsTimeFrame(String apiName) {
    this.apiName = apiName;
  }

  /**
   * Gets the API name.
   *
   * @return the API name
   */
  public String getAPIName() {
    return apiName;
  }
}
