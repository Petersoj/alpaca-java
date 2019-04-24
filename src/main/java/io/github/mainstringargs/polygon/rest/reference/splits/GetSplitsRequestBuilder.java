package io.github.mainstringargs.polygon.rest.reference.splits;

import io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder;

/**
 * The Class GetSplitsRequestBuilder.
 */
public class GetSplitsRequestBuilder extends ReferenceRequestBuilder {

  /** The Constant SPLITS_REQUEST_ENDPOINT. */
  public final static String SPLITS_REQUEST_ENDPOINT = "reference/splits";



  /**
   * Instantiates a new gets the splits request builder.
   *
   * @param baseUrl the base url
   */
  public GetSplitsRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder#getEndpoint()
   */
  @Override
  public String getEndpoint() {
    return SPLITS_REQUEST_ENDPOINT;
  }

  /**
   * Symbol.
   *
   * @param symbol the symbol
   */
  public void symbol(String symbol) {
    super.appendEndpoint(symbol);

  }


}
