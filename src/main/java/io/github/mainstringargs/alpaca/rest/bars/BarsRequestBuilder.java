package io.github.mainstringargs.alpaca.rest.bars;

import io.github.mainstringargs.alpaca.rest.AlpacaRequestBuilder;


/**
 * The Class BarsRequestBuilder.
 */
public abstract class BarsRequestBuilder extends AlpacaRequestBuilder {


  /** The Constant BARS_ENDPOINT. */
  public final static String BARS_ENDPOINT = "bars";

  /**
   * Instantiates a new bars request builder.
   *
   * @param baseUrl the base url
   */
  public BarsRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaRequestBuilder#getEndpoint()
   */
  @Override
  public String getEndpoint() {
    return BARS_ENDPOINT;
  }

}
