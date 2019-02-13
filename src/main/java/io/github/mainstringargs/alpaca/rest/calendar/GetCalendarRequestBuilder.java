package io.github.mainstringargs.alpaca.rest.calendar;

import java.time.LocalDate;
import io.github.mainstringargs.alpaca.Utilities;

/**
 * The Class GetCalendarRequestBuilder.
 */
public class GetCalendarRequestBuilder extends CalendarRequestBuilder {


  /**
   * Instantiates a new gets the calendar request builder.
   *
   * @param baseUrl the base url
   */
  public GetCalendarRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  /**
   * Start.
   *
   * @param start the start
   * @return the gets the calendar request builder
   */
  public GetCalendarRequestBuilder start(LocalDate start) {
    if (start != null) {
      super.appendURLParameter("start", Utilities.toDateString(start));
    }

    return this;
  }


  /**
   * End.
   *
   * @param end the end
   * @return the gets the calendar request builder
   */
  public GetCalendarRequestBuilder end(LocalDate end) {
    if (end != null) {
      super.appendURLParameter("end", Utilities.toDateString(end));
    }

    return this;
  }


}
