package io.github.mainstringargs.alpaca.rest;

/**
 * The Class ClockUrlBuilder.
 */
public class ClockRequestBuilder extends AlpacaRequestBuilder {


  /** The Constant CLOCK_ENDPOINT. */
  public final static String CLOCK_ENDPOINT = "clock";

  /**
   * Instantiates a new clock url builder.
   *
   * @param baseUrl the base url
   */
  public ClockRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public String getEndpoint() {
    return CLOCK_ENDPOINT;
  }

}
