package io.github.mainstringargs.alpaca.rest;

import java.time.LocalDateTime;
import io.github.mainstringargs.alpaca.Utilities;
import io.github.mainstringargs.alpaca.enums.Direction;
import io.github.mainstringargs.alpaca.enums.OrderStatus;

/**
 * The Class OrdersUrlBuilder.
 */
public class OrdersUrlBuilder extends AlpacaUrlBuilder {

  /** The Constant ORDERS_ENDPOINT. */
  public final static String ORDERS_ENDPOINT = "orders";



  /**
   * Instantiates a new orders url builder.
   *
   * @param baseUrl the base url
   */
  public OrdersUrlBuilder(String baseUrl) {
    super(baseUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public String getEndpoint() {
    return ORDERS_ENDPOINT;
  }


  /**
   * Status.
   *
   * @param status the status
   * @return the orders url builder
   */
  public OrdersUrlBuilder status(OrderStatus status) {
    if (status != null) {
      super.appendURLParameters("status", status.getAPIName());
    }

    return this;
  }

  /**
   * Limit.
   *
   * @param limit the limit
   * @return the orders url builder
   */
  public OrdersUrlBuilder limit(Integer limit) {
    if (limit != null) {
      super.appendURLParameters("limit", limit.toString());
    }

    return this;
  }

  /**
   * After.
   *
   * @param after the after
   * @return the orders url builder
   */
  public OrdersUrlBuilder after(LocalDateTime after) {
    if (after != null) {



      super.appendURLParameters("after", Utilities.toDateTimeString(after));
    }

    return this;
  }

  /**
   * Until.
   *
   * @param until the until
   * @return the orders url builder
   */
  public OrdersUrlBuilder until(LocalDateTime until) {
    if (until != null) {


      super.appendURLParameters("until", Utilities.toDateTimeString(until));
    }

    return this;
  }

  /**
   * Direction.
   *
   * @param direction the direction
   * @return the orders url builder
   */
  public OrdersUrlBuilder direction(Direction direction) {
    if (direction != null) {
      super.appendURLParameters("direction", direction.getAPIName());
    }

    return this;
  }
}
