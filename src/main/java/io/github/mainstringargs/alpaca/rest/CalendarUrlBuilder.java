package io.github.mainstringargs.alpaca.rest;

/**
 * The Class CalendarUrlBuilder.
 */
public class CalendarUrlBuilder extends AlpacaUrlBuilder {

  /**
   * Instantiates a new calendar url builder.
   *
   * @param baseUrl the base url
   */
  public CalendarUrlBuilder(String baseUrl) {
    super(baseUrl);
  }

  /* (non-Javadoc)
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public AlpacaUrlBuilder endpoint() {
    builder.append(CALENDAR_ENDPOINT);
    return this;
  }


}
