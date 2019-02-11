package io.github.mainstringargs.alpaca.enums;

public enum OrderStatus {
  OPEN("open"), CLOSED("closed"), ALL("all");

  String apiName;

  OrderStatus(String apiName) {
    this.apiName = apiName;
  }

  public String getAPIName() {
    return apiName;
  }
}
