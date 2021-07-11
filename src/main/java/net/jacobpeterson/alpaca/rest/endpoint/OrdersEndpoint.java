package net.jacobpeterson.alpaca.rest.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import net.jacobpeterson.alpaca.model.endpoint.common.enums.SortDirection;
import net.jacobpeterson.alpaca.model.endpoint.order.CancelledOrder;
import net.jacobpeterson.alpaca.model.endpoint.order.Order;
import net.jacobpeterson.alpaca.model.endpoint.order.enums.*;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.util.format.FormatUtil;
import net.jacobpeterson.alpaca.util.okhttp.JSONBodyBuilder;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * {@link AbstractEndpoint} for <a href="https://alpaca.markets/docs/api-documentation/api-v2/orders/">Orders</a>.
 */
public class OrdersEndpoint extends AbstractEndpoint {

    /**
     * Instantiates a new {@link OrdersEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public OrdersEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "orders");
    }

    /**
     * Retrieves a list of {@link Order}s for the account, filtered by the supplied query parameters.
     *
     * @param status    {@link CurrentOrderStatus} to be queried. Defaults to {@link CurrentOrderStatus#OPEN}.
     * @param limit     the maximum number of orders in response. Defaults to 50 and max is 500.
     * @param after     the response will include only ones submitted after this timestamp (exclusive)
     * @param until     the response will include only ones submitted until this timestamp (exclusive)
     * @param direction the chronological order of response based on the submission time. Defaults to {@link
     *                  SortDirection#DESCENDING}.
     * @param nested    if true, the result will roll up multi-leg orders under the legs field of primary order.
     * @param symbols   a {@link List} of symbols to filter the result by (null for no filter).
     *
     * @return a {@link List} of {@link Order}s
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public List<Order> get(CurrentOrderStatus status, Integer limit, ZonedDateTime after, ZonedDateTime until,
            SortDirection direction, Boolean nested, List<String> symbols) throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);

        if (status != null) {
            urlBuilder.addQueryParameter("status", status.toString());
        }

        if (limit != null) {
            urlBuilder.addQueryParameter("limit", limit.toString());
        }

        if (after != null) {
            urlBuilder.addQueryParameter("after", after.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (until != null) {
            urlBuilder.addQueryParameter("until", until.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (direction != null) {
            urlBuilder.addQueryParameter("direction", direction.toString());
        }

        if (nested != null) {
            urlBuilder.addQueryParameter("nested", nested.toString());
        }

        if (symbols != null && !symbols.isEmpty()) {
            urlBuilder.addQueryParameter("symbols", String.join(",", symbols));
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, new TypeToken<ArrayList<Order>>() {}.getType());
    }

    /**
     * Places a new order for the given account. An order request may be rejected if the account is not authorized for
     * trading, or if the tradable balance is insufficient to fill the order. Note: many of the parameters for this
     * method can be set to <code>null</code> if they aren't required for the requested {@link OrderType}.
     *
     * @param symbol               symbol or asset ID to identify the asset to trade
     * @param quantity             number of shares to trade. Can be fractional.
     * @param notional             dollar amount to trade. Cannot work with <code>quantity</code>. Can only work for
     *                             {@link OrderType#MARKET} and {@link OrderTimeInForce#DAY}.
     * @param side                 the {@link OrderSide}
     * @param type                 the {@link OrderType}
     * @param timeInForce          the {@link OrderTimeInForce}
     * @param limitPrice           required if type is {@link OrderType#LIMIT} or {@link OrderType#STOP_LIMIT}
     * @param stopPrice            required if type is {@link OrderType#STOP} or {@link OrderType#STOP_LIMIT}
     * @param trailPrice           this or <code>trail_percent</code> is required if type is {@link
     *                             OrderType#TRAILING_STOP}
     * @param trailPercent         this or <code>trail_price</code> is required if type is {@link
     *                             OrderType#TRAILING_STOP}
     * @param extendedHours        (default) false. If true, order will be eligible to execute in premarket/afterhours.
     *                             Only works with type {@link OrderType#LIMIT} and {@link OrderTimeInForce#DAY}.
     * @param clientOrderId        a unique identifier for the order. Automatically generated if <code>null</code>.
     * @param orderClass           the {@link OrderClass}. For details of non-simple order classes, please see "Bracket
     *                             Order Overview" on the Alpaca Docs.
     * @param takeProfitLimitPrice additional parameter for take-profit leg of advanced orders. Required for {@link
     *                             OrderClass#BRACKET}.
     * @param stopLossStopPrice    additional parameters for stop-loss leg of advanced orders. Required for {@link
     *                             OrderClass#BRACKET}.
     * @param stopLossLimitPrice   additional parameters for stop-loss leg of advanced orders. The stop-loss order
     *                             becomes a stop-limit order if specified.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestOrder(String symbol, Double quantity, Double notional, OrderSide side, OrderType type,
            OrderTimeInForce timeInForce, Double limitPrice, Double stopPrice, Double trailPrice, Double trailPercent,
            Boolean extendedHours, String clientOrderId, OrderClass orderClass, Double takeProfitLimitPrice,
            Double stopLossStopPrice, Double stopLossLimitPrice)
            throws AlpacaClientException {
        checkNotNull(symbol);
        checkState(quantity != null ^ notional != null, "Either 'quantity' or 'notional' are required.");
        checkNotNull(side);
        checkNotNull(type);
        checkNotNull(timeInForce);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);

        JSONBodyBuilder jsonBodyBuilder = new JSONBodyBuilder();
        jsonBodyBuilder.appendJSONBodyProperty("symbol", symbol);
        jsonBodyBuilder.appendJSONBodyProperty("side", side.toString());
        jsonBodyBuilder.appendJSONBodyProperty("type", type.toString());
        jsonBodyBuilder.appendJSONBodyProperty("time_in_force", timeInForce.toString());

        if (quantity != null) {
            jsonBodyBuilder.appendJSONBodyProperty("qty", quantity.toString());
        } else {
            jsonBodyBuilder.appendJSONBodyProperty("notional", notional.toString());
        }

        if (limitPrice != null) {
            jsonBodyBuilder.appendJSONBodyProperty("limit_price", FormatUtil.toCurrencyFormat(limitPrice));
        }

        if (stopPrice != null) {
            jsonBodyBuilder.appendJSONBodyProperty("stop_price", FormatUtil.toCurrencyFormat(stopPrice));
        }

        if (trailPrice != null) {
            jsonBodyBuilder.appendJSONBodyProperty("trail_price", FormatUtil.toCurrencyFormat(trailPrice));
        }

        if (trailPercent != null) {
            jsonBodyBuilder.appendJSONBodyProperty("trail_percent", FormatUtil.toCurrencyFormat(trailPercent));
        }

        if (extendedHours != null) {
            jsonBodyBuilder.appendJSONBodyJSONProperty("extended_hours", new JsonPrimitive(extendedHours));
        }

        if (clientOrderId != null) {
            jsonBodyBuilder.appendJSONBodyProperty("client_order_id", clientOrderId);
        }

        if (orderClass != null) {
            jsonBodyBuilder.appendJSONBodyProperty("order_class", orderClass.toString());
        }

        if (takeProfitLimitPrice != null) {
            JsonObject takeProfitObject = new JsonObject();
            takeProfitObject.addProperty("limit_price", takeProfitLimitPrice);
            jsonBodyBuilder.appendJSONBodyJSONProperty("take_profit", takeProfitObject);
        }

        if (stopLossStopPrice != null || stopLossLimitPrice != null) {
            JsonObject stopLossObject = new JsonObject();

            if (stopLossStopPrice != null) {
                stopLossObject.addProperty("stop_price", stopLossStopPrice);
            }

            if (stopLossLimitPrice != null) {
                stopLossObject.addProperty("limit_price", stopLossLimitPrice);
            }

            jsonBodyBuilder.appendJSONBodyJSONProperty("stop_loss", stopLossObject);
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .post(jsonBodyBuilder.build())
                .build();
        return alpacaClient.requestObject(request, Order.class);
    }

    /**
     * A market order is a request to buy or sell a security at the currently available market price. It provides the
     * most likely method of filling an order. Market orders fill nearly instantaneously. This method calls {@link
     * #requestOrder(String, Double, Double, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double,
     * Boolean, String, OrderClass, Double, Double, Double)} with {@link OrderType#MARKET} so check the Javadoc for that
     * method for parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestMarketOrder(String symbol, Integer quantity, OrderSide side, OrderTimeInForce timeInForce)
            throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.MARKET, timeInForce, null, null,
                null, null, null, null, OrderClass.SIMPLE, null, null, null);
    }

    /**
     * A market order is a request to buy or sell a security at the currently available market price. It provides the
     * most likely method of filling an order. Market orders fill nearly instantaneously. This method calls {@link
     * #requestOrder(String, Double, Double, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double,
     * Boolean, String, OrderClass, Double, Double, Double)} with {@link OrderType#MARKET} and {@link
     * OrderTimeInForce#DAY} with a fractional quantity so check the Javadoc for that method for parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestFractionalMarketOrder(String symbol, Double quantity, OrderSide side)
            throws AlpacaClientException {
        return requestOrder(symbol, quantity, null, side, OrderType.MARKET, OrderTimeInForce.DAY, null, null, null,
                null, null, null, null, null, null, null);
    }

    /**
     * A market order is a request to buy or sell a security at the currently available market price. It provides the
     * most likely method of filling an order. Market orders fill nearly instantaneously. This method calls {@link
     * #requestOrder(String, Double, Double, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double,
     * Boolean, String, OrderClass, Double, Double, Double)} with {@link OrderType#MARKET} and {@link
     * OrderTimeInForce#DAY} with a notional dollar amount so check the Javadoc for that method for the parameter
     * details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestNotionalMarketOrder(String symbol, Double notional, OrderSide side)
            throws AlpacaClientException {
        return requestOrder(symbol, null, notional, side, OrderType.MARKET, OrderTimeInForce.DAY, null, null, null,
                null, null, null, null, null, null, null);
    }

    /**
     * A limit order is an order to buy or sell at a specified price or better. A buy limit order (a limit order to buy)
     * is executed at the specified limit price or lower (i.e., better). Conversely, a sell limit order (a limit order
     * to sell) is executed at the specified limit price or higher (better). Unlike a market order, you have to specify
     * the limit price parameter when submitting your order. This method calls {@link #requestOrder(String, Double,
     * Double, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass,
     * Double, Double, Double)} with {@link OrderType#LIMIT} so check the Javadoc for that method for the parameter
     * details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestLimitOrder(String symbol, Integer quantity, OrderSide side, OrderTimeInForce timeInForce,
            Double limitPrice, Boolean extendedHours) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.LIMIT, timeInForce, limitPrice,
                null, null, null, extendedHours, null, OrderClass.SIMPLE, null, null, null);
    }

    /**
     * A stop (market) order is an order to buy or sell a security when its price moves past a particular point,
     * ensuring a higher probability of achieving a predetermined entry or exit price. Once the market price crosses the
     * specified stop price, the stop order becomes a market order. Alpaca converts buy stop orders into stop limit
     * orders with a limit price that is 4% higher than a stop price &lt; $50 (or 2.5% higher than a stop price &gt;=
     * $50). Sell stop orders are not converted into stop limit orders. This method calls {@link #requestOrder(String,
     * Double, Double, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double, Boolean, String,
     * OrderClass, Double, Double, Double)} with {@link OrderType#STOP} so check the Javadoc for that method for
     * parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestStopOrder(String symbol, Integer quantity, OrderSide side, OrderTimeInForce timeInForce,
            Double stopPrice, Boolean extendedHours) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.STOP, timeInForce, null, stopPrice,
                null, null, extendedHours, null, OrderClass.SIMPLE, null, null, null);
    }

    /**
     * A stop-limit order is a conditional trade over a set time frame that combines the features of a stop order with
     * those of a limit order and is used to mitigate risk. The stop-limit order will be executed at a specified limit
     * price, or better, after a given stop price has been reached. Once the stop price is reached, the stop-limit order
     * becomes a limit order to buy or sell at the limit price or better. This method calls {@link #requestOrder(String,
     * Double, Double, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double, Boolean, String,
     * OrderClass, Double, Double, Double)} with {@link OrderType#STOP_LIMIT} so check the Javadoc for that method for
     * parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestStopLimitOrder(String symbol, Integer quantity, OrderSide side, OrderTimeInForce timeInForce,
            Double limitPrice, Double stopPrice, Boolean extendedHours) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.STOP_LIMIT, timeInForce,
                limitPrice, stopPrice, null, null, extendedHours, null, OrderClass.SIMPLE, null, null, null);
    }

    /**
     * A bracket order is a chain of three orders that can be used to manage your position entry and exit. It is a
     * common use case of an OTOCO (One Triggers OCO {One Cancels Other}) order. This method calls {@link
     * #requestOrder(String, Double, Double, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double,
     * Boolean, String, OrderClass, Double, Double, Double)} with {@link OrderType#MARKET} and with parameters for a
     * bracket order so check the Javadoc for that method for parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestMarketBracketOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double takeProfitLimitPrice, Double stopLossStopPrice,
            Double stopLossLimitPrice) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.MARKET, timeInForce, null, null,
                null, null, null, null, OrderClass.BRACKET, takeProfitLimitPrice, stopLossStopPrice,
                stopLossLimitPrice);
    }

    /**
     * A bracket order is a chain of three orders that can be used to manage your position entry and exit. It is a
     * common use case of an OTOCO (One Triggers OCO {One Cancels Other}) order. This method calls {@link
     * #requestOrder(String, Double, Double, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double,
     * Boolean, String, OrderClass, Double, Double, Double)} with {@link OrderType#LIMIT} and with parameters for a
     * bracket order so check the Javadoc for that method for parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestLimitBracketOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double limitPrice, Boolean extendedHours, Double takeProfitLimitPrice,
            Double stopLossStopPrice, Double stopLossLimitPrice) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.LIMIT, timeInForce, limitPrice,
                null, null, null, extendedHours, null, OrderClass.BRACKET, takeProfitLimitPrice, stopLossStopPrice,
                stopLossLimitPrice);
    }

    /**
     * OCO (One-Cancels-Other) is another type of advanced order type. This is a set of two orders with the same side
     * (buy/buy or sell/sell) and currently only exit order is supported. In other words, this is the second part of the
     * bracket orders where the entry order is already filled, and you can submit the take-profit and stop-loss in one
     * order submission. This method calls {@link #requestOrder(String, Double, Double, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * parameters for a {@link OrderClass#ONE_CANCELS_OTHER} so check the Javadoc for that method for the parameter
     * details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestOCOOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Boolean extendedHours, Double takeProfitLimitPrice,
            Double stopLossStopPrice, Double stopLossLimitPrice) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.LIMIT, timeInForce, null, null,
                null, null, extendedHours, null, OrderClass.ONE_CANCELS_OTHER, takeProfitLimitPrice, stopLossStopPrice,
                stopLossLimitPrice);
    }

    /**
     * OTO (One-Triggers-Other) is a variant of bracket order. It takes one of the take-profit or stop-loss order in
     * addition to the entry order. This method calls {@link #requestOrder(String, Double, Double, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#LIMIT} and with parameters for a {@link OrderClass#ONE_CANCELS_OTHER} so check the Javadoc for
     * that method for parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestOTOMarketOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double takeProfitLimitPrice, Double stopLossStopPrice,
            Double stopLossLimitPrice) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.MARKET, timeInForce, null, null,
                null, null, null, null, OrderClass.ONE_CANCELS_OTHER, takeProfitLimitPrice, stopLossStopPrice,
                stopLossLimitPrice);
    }

    /**
     * OTO (One-Triggers-Other) is a variant of bracket order. It takes one of the take-profit or stop-loss order in
     * addition to the entry order. This method calls {@link #requestOrder(String, Double, Double, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#LIMIT} and with parameters for a {@link OrderClass#ONE_CANCELS_OTHER} so check the Javadoc for
     * that method for parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestOTOLimitOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double limitPrice, Boolean extendedHours, Double takeProfitLimitPrice,
            Double stopLossStopPrice, Double stopLossLimitPrice) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.LIMIT, timeInForce, limitPrice,
                null, null, null, extendedHours, null, OrderClass.ONE_CANCELS_OTHER, takeProfitLimitPrice,
                stopLossStopPrice, stopLossLimitPrice);
    }

    /**
     * OTO (One-Triggers-Other) is a variant of bracket order. It takes one of the take-profit or stop-loss order in
     * addition to the entry order. This method calls {@link #requestOrder(String, Double, Double, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#STOP} and with parameters for a {@link OrderClass#ONE_CANCELS_OTHER} so check the Javadoc for
     * that method for parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestOTOStopOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double stopPrice, Boolean extendedHours, Double takeProfitLimitPrice,
            Double stopLossStopPrice, Double stopLossLimitPrice) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.STOP, timeInForce, null, stopPrice,
                null, null, extendedHours, null, OrderClass.ONE_CANCELS_OTHER, takeProfitLimitPrice, stopLossStopPrice,
                stopLossLimitPrice);
    }

    /**
     * OTO (One-Triggers-Other) is a variant of bracket order. It takes one of the take-profit or stop-loss order in
     * addition to the entry order. This method calls {@link #requestOrder(String, Double, Double, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#STOP_LIMIT} and with parameters for a {@link OrderClass#ONE_CANCELS_OTHER} so check the Javadoc
     * for that method for parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestOTOStopLimitOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double limitPrice, Double stopPrice, Boolean extendedHours,
            Double takeProfitLimitPrice, Double stopLossStopPrice, Double stopLossLimitPrice)
            throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.STOP_LIMIT, timeInForce,
                limitPrice, stopPrice, null, null, extendedHours, null, OrderClass.ONE_CANCELS_OTHER,
                takeProfitLimitPrice, stopLossStopPrice, stopLossLimitPrice);
    }

    /**
     * Trailing stop orders allow you to continuously and automatically keep updating the stop price threshold based on
     * the stock price movement. This method calls {@link #requestOrder(String, Double, Double, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#TRAILING_STOP} and with parameters for a {@link OrderType#TRAILING_STOP} so check the Javadoc
     * for that method for parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestTrailingStopPriceOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double trailPrice, Boolean extendedHours) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.TRAILING_STOP, timeInForce, null,
                null, trailPrice, null, extendedHours, null, null, null, null, null);
    }

    /**
     * Trailing stop orders allow you to continuously and automatically keep updating the stop price threshold based on
     * the stock price movement. This method calls {@link #requestOrder(String, Double, Double, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#TRAILING_STOP} and with parameters for a {@link OrderType#TRAILING_STOP} so check the Javadoc
     * for that method for parameter details.
     *
     * @return the requested {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order requestTrailingStopPercentOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double trailPercent, Boolean extendedHours) throws AlpacaClientException {
        return requestOrder(symbol, quantity.doubleValue(), null, side, OrderType.TRAILING_STOP, timeInForce, null,
                null, null, trailPercent, extendedHours, null, null, null, null, null);
    }

    /**
     * Retrieves a single {@link Order} for the given <code>orderID</code>.
     *
     * @param orderID the {@link Order#getId()}
     * @param nested  if true, the result will roll up multi-leg orders under the legs field of primary order.
     *
     * @return the {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order get(String orderID, Boolean nested) throws AlpacaClientException {
        checkNotNull(orderID);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(orderID);

        if (nested != null) {
            urlBuilder.addQueryParameter("nested", nested.toString());
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, Order.class);
    }

    /**
     * Retrieves a single {@link Order} for the given <code>clientOrderId</code>.
     *
     * @param clientOrderID the {@link Order#getClientOrderId()}
     *
     * @return the {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order getByClientID(String clientOrderID) throws AlpacaClientException {
        checkNotNull(clientOrderID);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment("orders:by_client_order_id");
        urlBuilder.addQueryParameter("client_order_id", clientOrderID);

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, Order.class);
    }

    /**
     * Replaces a single {@link Order} with updated parameters. Each parameter overrides the corresponding attribute of
     * the existing {@link Order}. The other attributes remain the same as the existing {@link Order}.
     *
     * @param orderID       the {@link Order#getId()}
     * @param quantity      number of shares to trade. Can be fractional.
     * @param timeInForce   the {@link OrderTimeInForce}
     * @param limitPrice    required if type is {@link OrderType#LIMIT} or {@link OrderType#STOP_LIMIT}
     * @param stopPrice     required if type is {@link OrderType#STOP} or {@link OrderType#STOP_LIMIT}
     * @param trail         the new value of the <code>trail_price</code> or <code>trail_percent</code> value (works
     *                      only for {@link Order#getType()} <code>==</code> {@link OrderType#TRAILING_STOP})
     * @param clientOrderId a unique identifier for the order. Automatically generated if <code>null</code>.
     *
     * @return a new {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order replace(String orderID, Integer quantity, OrderTimeInForce timeInForce, Double limitPrice,
            Double stopPrice, Double trail, String clientOrderId) throws AlpacaClientException {
        checkNotNull(orderID);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(orderID);

        JSONBodyBuilder jsonBodyBuilder = new JSONBodyBuilder();

        if (quantity != null) {
            jsonBodyBuilder.appendJSONBodyProperty("qty", quantity.toString());
        }

        if (timeInForce != null) {
            jsonBodyBuilder.appendJSONBodyProperty("time_in_force", timeInForce.toString());
        }

        if (limitPrice != null) {
            jsonBodyBuilder.appendJSONBodyProperty("limit_price", FormatUtil.toCurrencyFormat(limitPrice));
        }

        if (stopPrice != null) {
            jsonBodyBuilder.appendJSONBodyProperty("stop_price", FormatUtil.toCurrencyFormat(stopPrice));
        }

        if (trail != null) {
            jsonBodyBuilder.appendJSONBodyProperty("trail", FormatUtil.toCurrencyFormat(trail));
        }

        if (clientOrderId != null) {
            jsonBodyBuilder.appendJSONBodyProperty("client_order_id", clientOrderId);
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .patch(jsonBodyBuilder.build())
                .build();
        return alpacaClient.requestObject(request, Order.class);
    }

    /**
     * Attempts to cancel all open {@link Order}s. A response will be provided for each {@link Order} that is attempted
     * to be cancelled. If an {@link Order} is no longer cancelable, the server will respond with status
     * <code>500</code> and reject the request.
     *
     * @return a {@link List} of {@link CancelledOrder}s
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public ArrayList<CancelledOrder> cancelAll() throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .delete()
                .build();
        return alpacaClient.requestObject(request, (code) -> code == 200 || code == 207,
                new TypeToken<ArrayList<CancelledOrder>>() {}.getType());
    }

    /**
     * Attempts to cancel an open {@link Order}. If the {@link Order} is no longer cancelable (example: {@link
     * Order#getStatus()} <code>==</code> {@link OrderStatus#FILLED}), the server will respond with status
     * <code>422</code>, and reject the request.
     *
     * @param orderID the {@link Order#getId()}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public void cancel(String orderID) throws AlpacaClientException {
        checkNotNull(orderID);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(orderID);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .delete()
                .build();
        alpacaClient.requestVoid(request, (code) -> code == 200 || code == 204);
    }
}
