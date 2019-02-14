package io.github.mainstringargs.alpaca.enums;

/**
 * The Enum Direction.
 */
public enum Direction {

  /** The asc. */
  ASC("asc"),

  /** The desc. */
  DESC("desc");

  /** The api name. */
  String apiName;

  /**
   * Instantiates a new direction.
   *
   * @param apiName the api name
   */
  Direction(String apiName) {
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
