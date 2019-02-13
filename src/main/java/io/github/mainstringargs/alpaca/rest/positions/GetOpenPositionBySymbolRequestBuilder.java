package io.github.mainstringargs.alpaca.rest.positions;

/**
 * The Class GetOpenPositionRequestBuilder.
 */
public class GetOpenPositionBySymbolRequestBuilder extends PositionsRequestBuilder {


  /**
   * Instantiates a new gets the open position request builder.
   *
   * @param baseUrl the base url
   */
  public GetOpenPositionBySymbolRequestBuilder(String baseUrl) {
    super(baseUrl);
  }



  /**
   * Symbol.
   *
   * @param symbol the symbol
   * @return the gets the open position request builder
   */
  public GetOpenPositionBySymbolRequestBuilder symbol(String symbol) {
    if (symbol != null) {
      super.appendEndpoint(symbol.trim());
    }

    return this;
  }

}
