package net.jacobpeterson.alpaca.websocket.marketdata.crypto;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.realtime.bar.CryptoBarMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.realtime.quote.CryptoQuoteMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.realtime.trade.CryptoTradeMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataWebsocket;
import okhttp3.OkHttpClient;

/**
 * {@link CryptoMarketDataWebsocket} is a {@link MarketDataWebsocket} for
 * <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-crypto-data/real-time/">Realtime
 * Crypto Market Data</a>
 */
public class CryptoMarketDataWebsocket extends MarketDataWebsocket {

    /**
     * Instantiates a new {@link CryptoMarketDataWebsocket}.
     *
     * @param okHttpClient the {@link OkHttpClient}
     * @param keyID        the key ID
     * @param secretKey    the secret key
     */
    public CryptoMarketDataWebsocket(OkHttpClient okHttpClient, String keyID, String secretKey) {
        super(okHttpClient, "v1beta1/crypto", "Crypto", keyID, secretKey, CryptoTradeMessage.class,
                CryptoQuoteMessage.class, CryptoBarMessage.class);
    }
}
