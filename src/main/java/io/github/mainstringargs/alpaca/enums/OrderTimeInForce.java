package io.github.mainstringargs.alpaca.enums;

/**
 * The Enum OrderTimeInForce.
 */
public enum OrderTimeInForce {

  /** The day. */
  DAY("day"),

  /** The gtc. */
  GTC("gtc"),

  /** The opg. */
  OPG("opg");

  /** The api name. */
  String apiName;

  /**
   * Instantiates a new order time in force.
   *
   * @param apiName the api name
   */
  OrderTimeInForce(String apiName) {
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
