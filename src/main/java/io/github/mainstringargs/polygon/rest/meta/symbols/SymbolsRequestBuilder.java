package io.github.mainstringargs.polygon.rest.meta.symbols;

import io.github.mainstringargs.polygon.rest.meta.MetaRequestBuilder;

/**
 * The Class SymbolsRequestBuilder.
 */
public abstract class SymbolsRequestBuilder extends MetaRequestBuilder {


  /** The Constant SYMBOLS_ENDPOINT. */
  public final static String SYMBOLS_ENDPOINT = "symbols";



  /**
   * Instantiates a new symbols request builder.
   *
   * @param baseUrl the base url
   */
  public SymbolsRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public String getEndpoint() {
    return SYMBOLS_ENDPOINT;
  }

}
