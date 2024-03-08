package net.jacobpeterson.alpaca.websocket.marketdata.streams.news;

import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.news.model.news.NewsMessage;

/**
 * {@link NewsMarketDataListenerAdapter} is an adapter for {@link NewsMarketDataListener}.
 */
public class NewsMarketDataListenerAdapter implements NewsMarketDataListener {

    @Override
    public void onNews(NewsMessage news) {}
}
