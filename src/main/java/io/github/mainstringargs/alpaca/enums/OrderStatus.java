package io.github.mainstringargs.alpaca.enums;

/**
 * The Enum OrderStatus.
 */
public enum OrderStatus {

  /** The open. */
  OPEN("open"),

  /** The closed. */
  CLOSED("closed"),

  /** The all. */
  ALL("all");

  /** The api name. */
  String apiName;

  /**
   * Instantiates a new order status.
   *
   * @param apiName the api name
   */
  OrderStatus(String apiName) {
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
