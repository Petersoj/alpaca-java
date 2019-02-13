package io.github.mainstringargs.alpaca.rest.assets;


/**
 * The Class GetAssetBySymbolRequestBuilder.
 */
public class GetAssetBySymbolRequestBuilder extends AssetsRequestBuilder {


  /**
   * Instantiates a new gets the open position request builder.
   *
   * @param baseUrl the base url
   */
  public GetAssetBySymbolRequestBuilder(String baseUrl) {
    super(baseUrl);
  }



  /**
   * Symbol.
   *
   * @param symbol the symbol
   * @return the gets the open position request builder
   */
  public GetAssetBySymbolRequestBuilder symbol(String symbol) {
    if (symbol != null) {
      super.appendEndpoint(symbol.trim());
    }

    return this;
  }

}
