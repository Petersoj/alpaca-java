package io.github.mainstringargs.alpaca.rest;

/**
 * The Class PositionsUrlBuilder.
 */
public class PositionsUrlBuilder extends AlpacaUrlBuilder {

  /**
   * Instantiates a new positions url builder.
   *
   * @param baseUrl the base url
   */
  public PositionsUrlBuilder(String baseUrl) {
    super(baseUrl);
  }

  /* (non-Javadoc)
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public AlpacaUrlBuilder endpoint() {
    builder.append(POSITIONS_ENDPOINT);
    return this;
  }

}
