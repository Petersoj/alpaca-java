package io.github.mainstringargs.alpaca.rest;

/**
 * The Class AccountUrlBuilder.
 */
public class AccountUrlBuilder extends AlpacaUrlBuilder {

  /**
   * Instantiates a new account url builder.
   *
   * @param baseUrl the base url
   */
  public AccountUrlBuilder(String baseUrl) {
    super(baseUrl);
  }

  /* (non-Javadoc)
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public AlpacaUrlBuilder endpoint() {
    builder.append(ACCOUNT_ENDPOINT);
    return this;
  }

}
