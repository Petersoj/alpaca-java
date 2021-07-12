package net.jacobpeterson.alpaca.rest.endpoint;

import com.google.gson.reflect.TypeToken;
import net.jacobpeterson.alpaca.model.endpoint.asset.Asset;
import net.jacobpeterson.alpaca.model.endpoint.order.Order;
import net.jacobpeterson.alpaca.model.endpoint.position.ClosePositionOrder;
import net.jacobpeterson.alpaca.model.endpoint.position.Position;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AlpacaEndpoint} for <a href="https://alpaca.markets/docs/api-documentation/api-v2/positions/">Positions</a>.
 */
public class PositionsEndpoint extends AlpacaEndpoint {

    /**
     * Instantiates a new {@link PositionsEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public PositionsEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "positions");
    }

    /**
     * Retrieves a list of the account's open {@link Position}s.
     *
     * @return a {@link List} of open {@link Position}s
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public List<Position> get() throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, new TypeToken<ArrayList<Position>>() {}.getType());
    }

    /**
     * Retrieves the account's open {@link Position}s for the given symbol or {@link Asset#getId()}.
     *
     * @param symbolOrAssetID the symbol or {@link Asset#getId()} (required)
     *
     * @return the open {@link Position}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Position getBySymbol(String symbolOrAssetID) throws AlpacaClientException {
        checkNotNull(symbolOrAssetID);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbolOrAssetID);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, Position.class);
    }

    /**
     * Closes (liquidates) all of the account’s open long and short {@link Position}s. A response will be provided for
     * each order that is attempted to be cancelled. If an order is no longer cancelable, the server will respond with
     * status 500 and reject the request.
     *
     * @param cancelOrders if true is specified, cancel all open orders before liquidating all positions
     *
     * @return a {@link List} of open {@link ClosePositionOrder}s
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public List<ClosePositionOrder> closeAll(Boolean cancelOrders) throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);

        if (cancelOrders != null) {
            urlBuilder.addQueryParameter("cancel_orders", cancelOrders.toString());
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .delete()
                .build();
        // TODO response code might be 207 instead of 200
        return alpacaClient.requestObject(request, new TypeToken<ArrayList<ClosePositionOrder>>() {}.getType());
    }

    /**
     * Closes (liquidates) the account’s open position for the given <code>symbol</code>. Works for both long and short
     * positions.
     *
     * @param symbolOrAssetID the symbol or {@link Asset#getId()}
     *
     * @return a closing {@link Position} {@link Order}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Order close(String symbolOrAssetID, Integer quantity, Double percentage) throws AlpacaClientException {
        checkNotNull(symbolOrAssetID);
        checkArgument(quantity != null ^ percentage != null, "Either 'quantity' or 'percentage' are required.");

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbolOrAssetID);

        if (quantity != null) {
            urlBuilder.addQueryParameter("qty", quantity.toString());
        }

        if (percentage != null) {
            urlBuilder.addQueryParameter("percentage", percentage.toString());
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .delete()
                .build();
        return alpacaClient.requestObject(request, Order.class);
    }
}
