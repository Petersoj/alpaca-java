package io.github.mainstringargs.alpaca.enums;

public enum Direction {
  ASC("asc"), DESC("desc");

  String apiName;

  Direction(String apiName) {
    this.apiName = apiName;
  }

  public String getAPIName() {
    return apiName;
  }
}
