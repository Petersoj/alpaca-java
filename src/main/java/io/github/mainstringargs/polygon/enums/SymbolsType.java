package io.github.mainstringargs.polygon.enums;

/**
 * The Enum SymbolsType.
 */
public enum SymbolsType {

  /** The etfs. */
  ETFS("etp"),

  /** The common stocks. */
  COMMON_STOCKS("cs");

  /** The api name. */
  String apiName;

  /**
   * Instantiates a new order type.
   *
   * @param apiName the api name
   */
  SymbolsType(String apiName) {
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
  public static SymbolsType fromAPIName(String apiName) {
    String apiNameString = apiName.trim();

    for (SymbolsType cType : SymbolsType.values()) {
      if (apiNameString.equals(cType.apiName)) {
        return cType;
      }
    }

    return null;
  }
}
