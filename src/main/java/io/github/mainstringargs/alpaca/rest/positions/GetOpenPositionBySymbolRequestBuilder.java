package io.github.mainstringargs.alpaca.rest.positions;

/**
 * The Class GetOpenPositionBySymbolRequestBuilder.
 */
public class GetOpenPositionBySymbolRequestBuilder extends PositionsRequestBuilder {


  /**
   * Instantiates a new gets the open position by symbol request builder.
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
   * @return the gets the open position by symbol request builder
   */
  public GetOpenPositionBySymbolRequestBuilder symbol(String symbol) {
    if (symbol != null) {
      super.appendEndpoint(symbol.trim());
    }

    return this;
  }

}
