package net.jacobpeterson.alpaca.websocket.marketdata.news;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.bar.BarMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.quote.QuoteMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.trade.TradeMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataWebsocket;
import okhttp3.OkHttpClient;

public class NewsMarketDataWebsocket extends MarketDataWebsocket {
    public NewsMarketDataWebsocket(OkHttpClient okHttpClient,
            String keyID, String secretKey) {
        super(okHttpClient, "v1beta1/news", "News", keyID, secretKey, TradeMessage.class,
                QuoteMessage.class, BarMessage.class);
    }
}
