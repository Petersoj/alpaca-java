package io.github.mainstringargs.polygon.rest.reference.types;

import io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder;

/**
 * The Class GetTypesRequestBuilder.
 */
public class GetTypesRequestBuilder extends ReferenceRequestBuilder {

  /** The Constant TYPES_REQUEST_ENDPOINT. */
  public final static String TYPES_REQUEST_ENDPOINT = "reference/types";



  /**
   * Instantiates a new gets the types request builder.
   *
   * @param baseUrl the base url
   */
  public GetTypesRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder#getEndpoint()
   */
  @Override
  public String getEndpoint() {
    return TYPES_REQUEST_ENDPOINT;
  }


}
