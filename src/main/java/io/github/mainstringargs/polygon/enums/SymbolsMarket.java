package io.github.mainstringargs.polygon.enums;

/**
 * The Enum SymbolsMarket.
 */
public enum SymbolsMarket {

  /** The stocks. */
  STOCKS("stocks"),

  /** The indices. */
  INDICES("indices");

  /** The api name. */
  String apiName;

  /**
   * Instantiates a new order type.
   *
   * @param apiName the api name
   */
  SymbolsMarket(String apiName) {
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
  public static SymbolsMarket fromAPIName(String apiName) {
    String apiNameString = apiName.trim();

    for (SymbolsMarket cType : SymbolsMarket.values()) {
      if (apiNameString.equals(cType.apiName)) {
        return cType;
      }
    }

    return null;
  }
}
