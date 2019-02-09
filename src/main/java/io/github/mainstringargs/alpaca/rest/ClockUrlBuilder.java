package io.github.mainstringargs.alpaca.rest;

/**
 * The Class ClockUrlBuilder.
 */
public class ClockUrlBuilder extends AlpacaUrlBuilder {

  /**
   * Instantiates a new clock url builder.
   *
   * @param baseUrl the base url
   */
  public ClockUrlBuilder(String baseUrl) {
    super(baseUrl);
  }

  /* (non-Javadoc)
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public AlpacaUrlBuilder endpoint() {
    builder.append(CLOCK_ENDPOINT);
    return this;
  }

}
