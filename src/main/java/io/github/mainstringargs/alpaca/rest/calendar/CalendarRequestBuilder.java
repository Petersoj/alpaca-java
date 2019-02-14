package io.github.mainstringargs.alpaca.rest.calendar;

import io.github.mainstringargs.alpaca.rest.AlpacaRequestBuilder;

/**
 * The Class CalendarRequestBuilder.
 */
public abstract class CalendarRequestBuilder extends AlpacaRequestBuilder {



  /** The Constant CALENDAR_ENDPOINT. */
  public final static String CALENDAR_ENDPOINT = "calendar";



  /**
   * Instantiates a new calendar request builder.
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
