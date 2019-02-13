package io.github.mainstringargs.alpaca.rest.calendar;

import io.github.mainstringargs.alpaca.rest.AlpacaRequestBuilder;

/**
 * The Class CalendarUrlBuilder.
 */
public abstract class CalendarRequestBuilder extends AlpacaRequestBuilder {



  /** The Constant CALENDAR_ENDPOINT. */
  public final static String CALENDAR_ENDPOINT = "calendar";



  /**
   * Instantiates a new calendar url builder.
   *
   * @param baseUrl the base url
   */
  public CalendarRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public String getEndpoint() {
    return CALENDAR_ENDPOINT;
  }


}
