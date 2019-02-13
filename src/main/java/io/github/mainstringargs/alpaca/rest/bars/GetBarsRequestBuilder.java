package io.github.mainstringargs.alpaca.rest.bars;

import java.time.LocalDate;
import java.time.LocalDateTime;
import io.github.mainstringargs.alpaca.Utilities;
import io.github.mainstringargs.alpaca.enums.BarsTimeFrame;

/**
 * The Class GetBarsRequestBuilder.
 */
public class GetBarsRequestBuilder extends BarsRequestBuilder {

  /**
   * Instantiates a new gets the bars request builder.
   *
   * @param baseUrl the base url
   */
  public GetBarsRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  public GetBarsRequestBuilder timeframe(BarsTimeFrame timeframe) {
    if (timeframe != null) {
      super.appendEndpoint(timeframe.getAPIName());
    }

    return this;
  }

  /**
   * Start.
   *
   * @param start the start
   * @return the gets the calendar request builder
   */
  public GetBarsRequestBuilder start(LocalDateTime start) {
    if (start != null) {
      super.appendURLParameter("start", Utilities.toDateTimeString(start));
    }

    return this;
  }


  /**
   * End.
   *
   * @param end the end
   * @return the gets the calendar request builder
   */
  public GetBarsRequestBuilder end(LocalDateTime end) {
    if (end != null) {
      super.appendURLParameter("end", Utilities.toDateTimeString(end));
    }

    return this;
  }

  /**
   * After.
   *
   * @param after the after
   * @return the gets the bars request builder
   */
  public GetBarsRequestBuilder after(LocalDateTime after) {
    if (after != null) {
      super.appendURLParameter("after", Utilities.toDateTimeString(after));
    }

    return this;
  }


  /**
   * Until.
   *
   * @param until the until
   * @return the gets the bars request builder
   */
  public GetBarsRequestBuilder until(LocalDateTime until) {
    if (until != null) {
      super.appendURLParameter("until", Utilities.toDateTimeString(until));
    }

    return this;
  }

  /**
   * Limit.
   *
   * @param limit the limit
   * @return the gets the bars request builder
   */
  public GetBarsRequestBuilder limit(Integer limit) {
    if (limit != null) {
      super.appendURLParameter("limit", limit.toString());
    }

    return this;
  }

  /**
   * Symbols.
   *
   * @param symbols the symbols
   * @return the gets the bars request builder
   */
  public GetBarsRequestBuilder symbols(String... symbols) {
    if (symbols != null) {
      super.appendURLParameter("symbols", String.join(",", symbols));
    }

    return this;
  }


}
