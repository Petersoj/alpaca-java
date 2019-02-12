package io.github.mainstringargs.alpaca.rest;

import java.time.LocalDateTime;
import io.github.mainstringargs.alpaca.Utilities;
import io.github.mainstringargs.alpaca.enums.Direction;
import io.github.mainstringargs.alpaca.enums.OrderSide;
import io.github.mainstringargs.alpaca.enums.OrderStatus;
import io.github.mainstringargs.alpaca.enums.OrderTimeInForce;
import io.github.mainstringargs.alpaca.enums.OrderType;

/**
 * The Class OrdersUrlBuilder.
 */
public class OrdersRequestBuilder extends AlpacaRequestBuilder {

  /** The Constant ORDERS_ENDPOINT. */
  public final static String ORDERS_ENDPOINT = "orders";



  /**
   * Instantiates a new orders url builder.
   *
   * @param baseUrl the base url
   */
  public OrdersRequestBuilder(String baseUrl) {
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
  public OrdersRequestBuilder status(OrderStatus status) {
    if (status != null) {
      super.appendURLParameter("status", status.getAPIName());
    }

    return this;
  }

  /**
   * Limit.
   *
   * @param limit the limit
   * @return the orders url builder
   */
  public OrdersRequestBuilder limit(Integer limit) {
    if (limit != null) {
      super.appendURLParameter("limit", limit.toString());
    }

    return this;
  }

  /**
   * After.
   *
   * @param after the after
   * @return the orders url builder
   */
  public OrdersRequestBuilder after(LocalDateTime after) {
    if (after != null) {



      super.appendURLParameter("after", Utilities.toDateTimeString(after));
    }

    return this;
  }

  /**
   * Until.
   *
   * @param until the until
   * @return the orders url builder
   */
  public OrdersRequestBuilder until(LocalDateTime until) {
    if (until != null) {


      super.appendURLParameter("until", Utilities.toDateTimeString(until));
    }

    return this;
  }

  /**
   * Direction.
   *
   * @param direction the direction
   * @return the orders url builder
   */
  public OrdersRequestBuilder direction(Direction direction) {
    if (direction != null) {
      super.appendURLParameter("direction", direction.getAPIName());
    }

    return this;
  }

  /**
   * Order id.
   *
   * @param orderId the order id
   * @return the orders url builder
   */
  public OrdersRequestBuilder orderId(String orderId) {
    if (orderId != null) {
      super.appendEndpoint(orderId);
    }

    return this;

  }

  /**
   * Symbol.
   *
   * @param symbol the symbol
   * @return the orders url builder
   */
  public OrdersRequestBuilder symbol(String symbol) {
    if (symbol != null) {
      super.appendBodyProperty("symbol", symbol);
    }
    return this;
  }

  /**
   * Quantity.
   *
   * @param qty the qty
   * @return the orders url builder
   */
  public OrdersRequestBuilder quantity(Integer qty) {
    if (qty != null) {
      super.appendBodyProperty("qty", qty.toString());
    }
    return this;
  }

  /**
   * Side.
   *
   * @param side the side
   * @return the orders url builder
   */
  public OrdersRequestBuilder side(OrderSide side) {
    if (side != null) {
      super.appendBodyProperty("side", side.getAPIName());
    }
    return this;
  }

  /**
   * Type.
   *
   * @param type the type
   * @return the orders url builder
   */
  public OrdersRequestBuilder type(OrderType type) {
    if (type != null) {
      super.appendBodyProperty("type", type.getAPIName());
    }
    return this;
  }

  /**
   * Time in force.
   *
   * @param tif the tif
   * @return the orders url builder
   */
  public OrdersRequestBuilder timeInForce(OrderTimeInForce tif) {
    if (tif != null) {
      super.appendBodyProperty("time_in_force", tif.getAPIName());
    }
    return this;
  }

  /**
   * Limit price.
   *
   * @param limitPrice the limit price
   * @return the orders url builder
   */
  public OrdersRequestBuilder limitPrice(Number limitPrice) {
    if (limitPrice != null) {
      super.appendBodyProperty("limit_price", Utilities.toDecimalFormat(limitPrice));
    }
    return this;
  }

  /**
   * Stop price.
   *
   * @param stopPrice the stop price
   * @return the orders url builder
   */
  public OrdersRequestBuilder stopPrice(Number stopPrice) {
    if (stopPrice != null) {
      super.appendBodyProperty("stop_price", Utilities.toDecimalFormat(stopPrice));
    }
    return this;
  }

  /**
   * Client order id.
   *
   * @param clientOrderId the client order id
   * @return the orders url builder
   */
  public OrdersRequestBuilder clientOrderId(String clientOrderId) {
    if (clientOrderId != null) {
      super.appendBodyProperty("client_order_id", clientOrderId);
    }
    return this;
  }

  /**
   * Orders by client order id.
   *
   * @param clientOrderId the client order id
   * @return the orders request builder
   */
  public OrdersRequestBuilder ordersByClientOrderId(String clientOrderId) {
    if (clientOrderId != null) {
      super.setDefaultEndpoint(false);
      super.appendEndpoint("orders:by_client_order_id");
      super.appendURLParameter("client_order_id", clientOrderId);
    }
    return this;

  }
}
