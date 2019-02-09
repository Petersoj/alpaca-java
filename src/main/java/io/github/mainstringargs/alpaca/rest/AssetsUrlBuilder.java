package io.github.mainstringargs.alpaca.rest;

/**
 * The Class AssetsUrlBuilder.
 */
public class AssetsUrlBuilder extends AlpacaUrlBuilder {

  /**
   * Instantiates a new assets url builder.
   *
   * @param baseUrl the base url
   */
  public AssetsUrlBuilder(String baseUrl) {
    super(baseUrl);
  }

  /* (non-Javadoc)
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public AlpacaUrlBuilder endpoint() {
    builder.append(ASSETS_ENDPOINT);
    return this;
  }

}
