package net.jacobpeterson.alpaca.rest.endpoint.watchlist;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import net.jacobpeterson.alpaca.model.endpoint.assets.Asset;
import net.jacobpeterson.alpaca.model.endpoint.watchlist.Watchlist;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.AlpacaEndpoint;
import net.jacobpeterson.alpaca.util.okhttp.JSONBodyBuilder;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.jacobpeterson.alpaca.rest.AlpacaClient.STATUS_CODE_200_OR_204;

/**
 * {@link AlpacaEndpoint} for <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/">Watchlists</a>
 * .
 */
public class WatchlistEndpoint extends AlpacaEndpoint {

    private static final Type WATCHLIST_ARRAYLIST_TYPE = new TypeToken<ArrayList<Watchlist>>() {}.getType();

    /**
     * Instantiates a new {@link WatchlistEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public WatchlistEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "watchlists");
    }

    /**
     * Returns the list of {@link Watchlist}s registered under the account.
     *
     * @return a {@link List} of {@link Watchlist}s
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public List<Watchlist> get() throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, WATCHLIST_ARRAYLIST_TYPE);
    }

    /**
     * Creates a new {@link Watchlist} with initial set of {@link Asset}s.
     *
     * @param name    arbitrary name string, up to 64 characters
     * @param symbols set of symbols {@link String}s
     *
     * @return the created {@link Watchlist}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Watchlist create(String name, String... symbols) throws AlpacaClientException {
        checkNotNull(name);
        checkArgument(name.length() <= 64, "'name' cannot be longer than 64 characters!");

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);

        JSONBodyBuilder jsonBodyBuilder = new JSONBodyBuilder();
        jsonBodyBuilder.appendJSONBodyProperty("name", name);

        if (symbols != null && symbols.length != 0) {
            JsonArray symbolsArray = new JsonArray();
            Arrays.stream(symbols).forEach(symbolsArray::add);
            jsonBodyBuilder.appendJSONBodyJSONProperty("symbols", symbolsArray);
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .post(jsonBodyBuilder.build())
                .build();
        return alpacaClient.requestObject(request, Watchlist.class);
    }

    /**
     * Returns a {@link Watchlist} identified by the ID.
     *
     * @param watchlistID the {@link Watchlist#getId()}
     *
     * @return the {@link Watchlist}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Watchlist get(String watchlistID) throws AlpacaClientException {
        checkNotNull(watchlistID);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(watchlistID);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, Watchlist.class);
    }

    /**
     * Updates the name and/or content of a {@link Watchlist}.
     *
     * @param watchlistID the {@link Watchlist#getId()}
     * @param name        the new {@link Watchlist} name
     * @param symbols     the new list of symbol names to replace the {@link Watchlist} content
     *
     * @return the updated {@link Watchlist}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Watchlist update(String watchlistID, String name, String... symbols)
            throws AlpacaClientException {
        checkNotNull(watchlistID);
        checkArgument(name == null || name.length() <= 64, "'name' cannot be longer than 64 characters!");

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(watchlistID);

        JSONBodyBuilder jsonBodyBuilder = new JSONBodyBuilder();

        if (name != null) {
            jsonBodyBuilder.appendJSONBodyProperty("name", name);
        }

        if (symbols != null && symbols.length != 0) {
            JsonArray symbolsArray = new JsonArray();
            Arrays.stream(symbols).forEach(symbolsArray::add);
            jsonBodyBuilder.appendJSONBodyJSONProperty("symbols", symbolsArray);
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .put(jsonBodyBuilder.build())
                .build();
        return alpacaClient.requestObject(request, Watchlist.class);
    }

    /**
     * Append an asset for the symbol to the end of {@link Watchlist} asset list.
     *
     * @param watchlistID the {@link Watchlist#getId()}
     * @param symbol      the symbol name to add to the {@link Watchlist}
     *
     * @return the updated {@link Watchlist}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Watchlist addAsset(String watchlistID, String symbol) throws AlpacaClientException {
        checkNotNull(watchlistID);
        checkNotNull(symbol);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(watchlistID);

        JSONBodyBuilder jsonBodyBuilder = new JSONBodyBuilder();
        jsonBodyBuilder.appendJSONBodyProperty("symbol", symbol);

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .post(jsonBodyBuilder.build())
                .build();
        return alpacaClient.requestObject(request, Watchlist.class);
    }

    /**
     * Deletes a {@link Watchlist}. This is a permanent deletion.
     *
     * @param watchlistID the {@link Watchlist#getId()}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public void delete(String watchlistID) throws AlpacaClientException {
        checkNotNull(watchlistID);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(watchlistID);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .delete()
                .build();
        alpacaClient.requestVoid(request, STATUS_CODE_200_OR_204);
    }

    /**
     * Delete one entry for an asset by symbol name.
     *
     * @param watchlistID the {@link Watchlist#getId()}
     * @param symbol      symbol name to remove from the {@link Watchlist} content
     *
     * @return the updated {@link Watchlist}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Watchlist removeSymbol(String watchlistID, String symbol) throws AlpacaClientException {
        checkNotNull(watchlistID);
        checkNotNull(symbol);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(watchlistID)
                .addPathSegment(symbol);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .delete()
                .build();
        return alpacaClient.requestObject(request, STATUS_CODE_200_OR_204, Watchlist.class);
    }
}
