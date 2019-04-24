package io.github.mainstringargs.polygon.rest.reference.locales;

import io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder;

/**
 * The Class GetLocalesRequestBuilder.
 */
public class GetLocalesRequestBuilder extends ReferenceRequestBuilder {

  /** The Constant LOCALES_REQUEST_ENDPOINT. */
  public final static String LOCALES_REQUEST_ENDPOINT = "reference/locales";



  /**
   * Instantiates a new gets the locales request builder.
   *
   * @param baseUrl the base url
   */
  public GetLocalesRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder#getEndpoint()
   */
  @Override
  public String getEndpoint() {
    return LOCALES_REQUEST_ENDPOINT;
  }


}
