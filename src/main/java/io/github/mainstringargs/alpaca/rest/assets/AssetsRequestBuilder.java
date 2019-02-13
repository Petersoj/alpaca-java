package io.github.mainstringargs.alpaca.rest.assets;

import io.github.mainstringargs.alpaca.rest.AlpacaRequestBuilder;

/**
 * The Class AssetsUrlBuilder.
 */
public abstract class AssetsRequestBuilder extends AlpacaRequestBuilder {


  /** The Constant ASSETS_ENDPOINT. */
  public final static String ASSETS_ENDPOINT = "assets";



  /**
   * Instantiates a new assets url builder.
   *
   * @param baseUrl the base url
   */
  public AssetsRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public String getEndpoint() {
    return ASSETS_ENDPOINT;
  }

}
