package io.github.mainstringargs.polygon.rest.meta;

import io.github.mainstringargs.alpaca.rest.AlpacaRequestBuilder;

/**
 * The Class MetaRequestBuilder.
 */
public abstract class MetaRequestBuilder extends AlpacaRequestBuilder {


  /** The Constant META_ENDPOINT. */
  public final static String META_ENDPOINT = "meta";



  /**
   * Instantiates a new meta request builder.
   *
   * @param baseUrl the base url
   */
  public MetaRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public String getEndpoint() {
    return META_ENDPOINT;
  }

}
