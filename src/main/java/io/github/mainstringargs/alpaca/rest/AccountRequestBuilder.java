package io.github.mainstringargs.alpaca.rest;

/**
 * The Class AccountUrlBuilder.
 */
public class AccountUrlBuilder extends AlpacaUrlBuilder {


  /** The Constant ACCOUNT_ENDPOINT. */
  public final static String ACCOUNT_ENDPOINT = "account";

  /**
   * Instantiates a new account url builder.
   *
   * @param baseUrl the base url
   */
  public AccountUrlBuilder(String baseUrl) {
    super(baseUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public String getEndpoint() {
    return ACCOUNT_ENDPOINT;
  }

}
