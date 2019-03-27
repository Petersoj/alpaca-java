package io.github.mainstringargs.polygon.enums;

/**
 * The Enum ChannelType.
 */
public enum ChannelType {

  /** The trades. */
  TRADES("T"),

  /** The quotes. */
  QUOTES("Q"),

  /** The aggregate per second. */
  AGGREGATE_PER_SECOND("A"),

  /** The aggregate per minute. */
  AGGREGATE_PER_MINUTE("AM");

  /** The api name. */
  String apiName;

  /**
   * Instantiates a new order type.
   *
   * @param apiName the api name
   */
  ChannelType(String apiName) {
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
