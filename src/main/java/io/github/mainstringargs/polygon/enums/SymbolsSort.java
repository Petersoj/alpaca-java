package io.github.mainstringargs.polygon.enums;

/**
 * The Enum SymbolsSort.
 */
public enum SymbolsSort {

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
  SymbolsSort(String apiName) {
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
  public static SymbolsSort fromAPIName(String apiName) {
    String apiNameString = apiName.trim();

    for (SymbolsSort cType : SymbolsSort.values()) {
      if (apiNameString.equals(cType.apiName)) {
        return cType;
      }
    }

    return null;
  }
}
