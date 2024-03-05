package net.jacobpeterson.alpaca.websocket.marketdata.streams.stock;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.bar.StockBarMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.quote.StockQuoteMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.realtime.trade.StockTradeMessage;
import net.jacobpeterson.alpaca.model.properties.DataAPIType;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataWebsocket;
import okhttp3.OkHttpClient;

/**
 * {@link StockMarketDataWebsocket} is a {@link MarketDataWebsocket} for
 * <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/real-time/">Realtime
 * Stock Market Data</a>
 */
public class StockMarketDataWebsocket extends MarketDataWebsocket {

    /**
     * Instantiates a new {@link StockMarketDataWebsocket}.
     *
     * @param okHttpClient the {@link OkHttpClient}
     * @param dataAPIType  the {@link DataAPIType}
     * @param keyID        the key ID
     * @param secretKey    the secret key
     */
    public StockMarketDataWebsocket(OkHttpClient okHttpClient, DataAPIType dataAPIType,
            String keyID, String secretKey) {
        super(okHttpClient, "v2/" + dataAPIType.toString(), "Stock", keyID, secretKey, StockTradeMessage.class,
                StockQuoteMessage.class, StockBarMessage.class);
    }
}
