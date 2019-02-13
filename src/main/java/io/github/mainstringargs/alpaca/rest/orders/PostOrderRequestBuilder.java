package io.github.mainstringargs.alpaca.rest.orders;

import io.github.mainstringargs.alpaca.Utilities;
import io.github.mainstringargs.alpaca.enums.OrderSide;
import io.github.mainstringargs.alpaca.enums.OrderTimeInForce;
import io.github.mainstringargs.alpaca.enums.OrderType;

public class PostOrderRequestBuilder extends OrdersRequestBuilder {

  public PostOrderRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  /**
   * Symbol.
   *
   * @param symbol the symbol
   * @return the orders url builder
   */
  public PostOrderRequestBuilder symbol(String symbol) {
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
  public PostOrderRequestBuilder quantity(Integer qty) {
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
  public PostOrderRequestBuilder side(OrderSide side) {
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
  public PostOrderRequestBuilder type(OrderType type) {
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
  public PostOrderRequestBuilder timeInForce(OrderTimeInForce tif) {
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
  public PostOrderRequestBuilder limitPrice(Number limitPrice) {
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
  public PostOrderRequestBuilder stopPrice(Number stopPrice) {
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
  public PostOrderRequestBuilder clientOrderId(String clientOrderId) {
    if (clientOrderId != null) {
      super.appendBodyProperty("client_order_id", clientOrderId);
    }
    return this;
  }
}
