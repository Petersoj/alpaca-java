package io.github.mainstringargs.alpaca.rest.calendar;

import java.time.LocalDate;
import io.github.mainstringargs.alpaca.Utilities;

public class GetCalendarRequestBuilder extends CalendarRequestBuilder {


  public GetCalendarRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  public GetCalendarRequestBuilder start(LocalDate start) {
    if (start != null) {
      super.appendURLParameter("start", Utilities.toDateString(start));
    }

    return this;
  }


  public GetCalendarRequestBuilder end(LocalDate end) {
    if (end != null) {
      super.appendURLParameter("end", Utilities.toDateString(end));
    }

    return this;
  }


}
