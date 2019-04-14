package io.github.mainstringargs.polygon.rest.reference;

import io.github.mainstringargs.polygon.rest.PolygonRequestBuilder;

/**
 * The Class ReferenceRequestBuilder.
 */
public abstract class ReferenceRequestBuilder extends PolygonRequestBuilder {


  /** The Constant REFERENCE_ENDPOINT. */
  public final static String REFERENCE_ENDPOINT = "reference";



  /**
   * Instantiates a new symbols request builder.
   *
   * @param baseUrl the base url
   */
  public ReferenceRequestBuilder(String baseUrl) {
    super(baseUrl);
    super.setVersion("v2");
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public String getEndpoint() {
    return REFERENCE_ENDPOINT;
  }

}
