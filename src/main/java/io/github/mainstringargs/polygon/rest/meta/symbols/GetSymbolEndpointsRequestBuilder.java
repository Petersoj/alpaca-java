package io.github.mainstringargs.polygon.rest.meta.symbols;

/**
 * The Class GetSymbolEndpointsRequestBuilder.
 */
public class GetSymbolEndpointsRequestBuilder extends SymbolsRequestBuilder {

  /**
   * Instantiates a new gets the symbol endpoints request builder.
   *
   * @param baseDataUrl the base data url
   */
  public GetSymbolEndpointsRequestBuilder(String baseDataUrl) {
    super(baseDataUrl);
  }

  /**
   * Symbol.
   *
   * @param symbol the symbol
   */
  public void symbol(String symbol) {
    super.appendEndpoint(symbol);

  }


  /**
   * Endpoint.
   *
   * @param endpoint the endpoint
   */
  public void symbolEndpoint(String endpoint) {
    super.appendEndpoint(endpoint);

  }


}
