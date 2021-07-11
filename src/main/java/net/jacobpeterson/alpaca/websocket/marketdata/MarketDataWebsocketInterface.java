package net.jacobpeterson.alpaca.websocket.marketdata;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.realtime.MarketDataMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.realtime.control.ErrorMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.realtime.control.SubscriptionsMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.realtime.control.SuccessMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocketInterface;

import java.util.Collection;

/**
 * {@link MarketDataWebsocketInterface} is an {@link AlpacaWebsocketInterface} for a {@link MarketDataWebsocket}.
 */
public interface MarketDataWebsocketInterface extends AlpacaWebsocketInterface<MarketDataListener> {

    /**
     * Subscribe to a specific {@link MarketDataMessage}. That is, {@link ErrorMessage}, {@link SubscriptionsMessage},
     * or {@link SuccessMessage}.
     *
     * @param marketDataMessageTypes array of any of: {@link MarketDataMessageType#SUCCESS}, {@link
     *                               MarketDataMessageType#ERROR}, or {@link MarketDataMessageType#SUBSCRIPTION}
     */
    void subscribeToTypes(MarketDataMessageType... marketDataMessageTypes);

    /**
     * Subscribes to trades, quotes, or bars according to the given {@link Collection} of symbols.
     * <br>
     * Note that any one of the given {@link Collection}s can contain the wildcard character e.g. "*" to subscribe to
     * ALL available symbols.
     * <br>
     * Note that this will call {@link MarketDataWebsocketInterface#connect()} and {@link
     * MarketDataWebsocketInterface#waitForAuthorization()} if {@link MarketDataWebsocketInterface#isConnected()}
     * returns false.
     *
     * @param tradeSymbols a {@link Collection} of symbols to subscribe to trades or <code>null</code> for no change
     * @param quoteSymbols a {@link Collection} of symbols to subscribe to quotes or <code>null</code> for no change
     * @param barSymbols   a {@link Collection} of symbols to subscribe to bars or <code>null</code> for no change
     *
     * @see #unsubscribe(Collection, Collection, Collection)
     */
    void subscribe(Collection<String> tradeSymbols, Collection<String> quoteSymbols, Collection<String> barSymbols);

    /**
     * Unsubscribes from trades, quotes, or bars according to the given {@link Collection} of symbols.
     * <br>
     * Note that any one of the given {@link Collection}s can contain the wildcard character e.g. "*" to unsubscribe
     * from a previously subscribed wildcard.
     *
     * @param tradeSymbols a {@link Collection} of symbols to unsubscribe from trades or <code>null</code> for no
     *                     change
     * @param quoteSymbols a {@link Collection} of symbols to unsubscribe from quotes or <code>null</code> for no
     *                     change
     * @param barSymbols   a {@link Collection} of symbols to unsubscribe from bars or <code>null</code> for no change
     *
     * @see #subscribe(Collection, Collection, Collection)
     */
    void unsubscribe(Collection<String> tradeSymbols, Collection<String> quoteSymbols, Collection<String> barSymbols);

    /**
     * Gets all of the currently subscribed {@link MarketDataMessageType}s.
     *
     * @return a {@link Collection} of {@link MarketDataMessageType}s
     */
    Collection<MarketDataMessageType> subscribedTypes();

    /**
     * Gets all of the currently subscribed symbols for trade updates.
     *
     * @return a {@link Collection} of {@link String}s
     */
    Collection<String> subscribedTrades();

    /**
     * Gets all of the currently subscribed symbols for quote updates.
     *
     * @return a {@link Collection} of {@link String}s
     */
    Collection<String> subscribedQuotes();

    /**
     * Gets all of the currently subscribed symbols for bar updates.
     *
     * @return a {@link Collection} of {@link String}s
     */
    Collection<String> subscribedBars();
}
