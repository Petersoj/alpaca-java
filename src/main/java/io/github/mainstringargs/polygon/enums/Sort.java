package io.github.mainstringargs.polygon.enums;

/**
 * The Enum SymbolsSort.
 */
public enum Sort {

  /** The ticker. */
  TICKER("ticker"),

  /** The type. */
  TYPE("type");

  /** The api name. */
  String apiName;

  /**
   * Instantiates a new order type.
   *
   * @param apiName the api name
   */
  Sort(String apiName) {
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
   * From API name.
   *
   * @param apiName the api name
   * @return the channel type
   */
  public static Sort fromAPIName(String apiName) {
    String apiNameString = apiName.trim();

    for (Sort cType : Sort.values()) {
      if (apiNameString.equals(cType.apiName)) {
        return cType;
      }
    }

    return null;
  }
}
