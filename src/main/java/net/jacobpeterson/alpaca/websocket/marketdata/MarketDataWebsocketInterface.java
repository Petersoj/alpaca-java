package net.jacobpeterson.alpaca.websocket.marketdata;

import net.jacobpeterson.alpaca.websocket.AlpacaWebsocketInterface;

import java.util.List;

/**
 * {@link MarketDataWebsocketInterface} is an {@link AlpacaWebsocketInterface} for a {@link MarketDataWebsocket}.
 */
public interface MarketDataWebsocketInterface extends AlpacaWebsocketInterface<MarketDataListener> {

    /**
     * Subscribes to trades, quotes, or bars according to the given {@link List} of symbols.
     * <br>
     * Note that any one of the given {@link List}s can contain the wildcard character e.g. "*" to subscribe to ALL
     * available symbols.
     *
     * @param tradeSymbols a {@link List} of symbols to subscribe to trades or <code>null</code> for no change
     * @param quoteSymbols a {@link List} of symbols to subscribe to quotes or <code>null</code> for no change
     * @param barSymbols   a {@link List} of symbols to subscribe to bars or <code>null</code> for no change
     *
     * @see #unsubscribe(List, List, List)
     */
    void subscribe(List<String> tradeSymbols, List<String> quoteSymbols, List<String> barSymbols);

    /**
     * Unsubscribes from trades, quotes, or bars according to the given {@link List} of symbols.
     * <br>
     * Note that any one of the given {@link List}s can contain the wildcard character e.g. "*" to unsubscribe from a
     * previously subscribed wildcard.
     *
     * @param tradeSymbols a {@link List} of symbols to unsubscribe from trades or <code>null</code> for no change
     * @param quoteSymbols a {@link List} of symbols to unsubscribe from quotes or <code>null</code> for no change
     * @param barSymbols   a {@link List} of symbols to unsubscribe from bars or <code>null</code> for no change
     *
     * @see #subscribe(List, List, List)
     */
    void unsubscribe(List<String> tradeSymbols, List<String> quoteSymbols, List<String> barSymbols);
}
