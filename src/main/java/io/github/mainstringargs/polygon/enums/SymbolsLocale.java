package io.github.mainstringargs.polygon.enums;

/**
 * The Enum SymbolsLocale.
 */
public enum SymbolsLocale {

  /** The us. */
  US("us"),

  /** The global. */
  GLOBAL("g");

  /** The api name. */
  String apiName;

  /**
   * Instantiates a new order type.
   *
   * @param apiName the api name
   */
  SymbolsLocale(String apiName) {
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
  public static SymbolsLocale fromAPIName(String apiName) {
    String apiNameString = apiName.trim();

    for (SymbolsLocale cType : SymbolsLocale.values()) {
      if (apiNameString.equals(cType.apiName)) {
        return cType;
      }
    }

    return null;
  }
}
