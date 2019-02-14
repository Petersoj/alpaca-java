package io.github.mainstringargs.alpaca.rest.orders;

import java.time.LocalDateTime;
import io.github.mainstringargs.alpaca.Utilities;
import io.github.mainstringargs.alpaca.enums.Direction;
import io.github.mainstringargs.alpaca.enums.OrderStatus;

/**
 * The Class GetListOfOrdersRequestBuilder.
 */
public class GetListOfOrdersRequestBuilder extends OrdersRequestBuilder {

  /**
   * Instantiates a new gets the list of orders request builder.
   *
   * @param baseUrl the base url
   */
  public GetListOfOrdersRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  /**
   * Status.
   *
   * @param status the status
   * @return the gets the list of orders request builder
   */
  public GetListOfOrdersRequestBuilder status(OrderStatus status) {
    if (status != null) {
      super.appendURLParameter("status", status.getAPIName());
    }

    return this;
  }

  /**
   * Limit.
   *
   * @param limit the limit
   * @return the gets the list of orders request builder
   */
  public GetListOfOrdersRequestBuilder limit(Integer limit) {
    if (limit != null) {
      super.appendURLParameter("limit", limit.toString());
    }

    return this;
  }

  /**
   * After.
   *
   * @param after the after
   * @return the gets the list of orders request builder
   */
  public GetListOfOrdersRequestBuilder after(LocalDateTime after) {
    if (after != null) {
      super.appendURLParameter("after", Utilities.toDateTimeString(after));
    }

    return this;
  }

  /**
   * Until.
   *
   * @param until the until
   * @return the gets the list of orders request builder
   */
  public GetListOfOrdersRequestBuilder until(LocalDateTime until) {
    if (until != null) {
      super.appendURLParameter("until", Utilities.toDateTimeString(until));
    }

    return this;
  }

  /**
   * Direction.
   *
   * @param direction the direction
   * @return the gets the list of orders request builder
   */
  public GetListOfOrdersRequestBuilder direction(Direction direction) {
    if (direction != null) {
      super.appendURLParameter("direction", direction.getAPIName());
    }

    return this;
  }

}
