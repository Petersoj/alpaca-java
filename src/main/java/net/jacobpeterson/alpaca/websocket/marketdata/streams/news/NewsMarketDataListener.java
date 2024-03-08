package net.jacobpeterson.alpaca.websocket.marketdata.streams.news;

import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.news.model.news.NewsMessage;

/**
 * {@link NewsMarketDataListener} defines a listener interface for {@link NewsMarketDataWebsocketInterface} messages.
 */
@FunctionalInterface
public interface NewsMarketDataListener {

    /**
     * Called when a {@link NewsMessage} is received.
     *
     * @param news the {@link NewsMessage}
     */
    void onNews(NewsMessage news);
}
