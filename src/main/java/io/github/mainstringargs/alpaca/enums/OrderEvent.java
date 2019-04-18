package io.github.mainstringargs.alpaca.enums;

/**
 * The Enum OrderEvent.
 */
public enum OrderEvent {

  /** The new. */
  NEW("new"),

  /** The fill. */
  FILL("fill"),

  /** The partially filled. */
  PARTIALLY_FILLED("partially_filled"),

  /** The filled. */
  FILLED("filled"),

  /** The done for day. */
  DONE_FOR_DAY("done_for_day"),

  /** The canceled. */
  CANCELED("canceled"),

  /** The expired. */
  EXPIRED("expired"),

  /** The pending cancel. */
  PENDING_CANCEL("pending_cancel"),

  /** The stopped. */
  STOPPED("stopped"),

  /** The rejected. */
  REJECTED("rejected"),

  /** The suspended. */
  SUSPENDED("suspended"),

  /** The pending new. */
  PENDING_NEW("pending_new"),

  /** The calculated. */
  CALCULATED("calculated");

  /** The api name. */
  private String apiName;

  /**
   * Instantiates a new order event.
   *
   * @param apiName the api name
   */
  OrderEvent(String apiName) {
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


  /**
   * Gets the API name.
   *
   * @param apiName the api name
   * @return the API name
   */
  public static OrderEvent fromAPIName(String apiName) {

    for (OrderEvent event : values()) {
      if (event.getAPIName().equals(apiName.trim())) {
        return event;
      }
    }

    return null;
  }
}
