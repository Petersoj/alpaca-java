package net.jacobpeterson.alpaca.websocket.marketdata.client;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.abstracts.websocket.client.WebsocketClient;
import net.jacobpeterson.alpaca.abstracts.websocket.client.WebsocketStateListener;
import net.jacobpeterson.alpaca.abstracts.websocket.exception.WebsocketException;
import net.jacobpeterson.alpaca.domain.marketdata.realtime.MarketDataMessage;
import net.jacobpeterson.alpaca.domain.marketdata.realtime.SymbolMessage;
import net.jacobpeterson.alpaca.domain.marketdata.realtime.bar.BarMessage;
import net.jacobpeterson.alpaca.domain.marketdata.realtime.control.ErrorMessage;
import net.jacobpeterson.alpaca.domain.marketdata.realtime.control.SubscriptionsMessage;
import net.jacobpeterson.alpaca.domain.marketdata.realtime.control.SuccessMessage;
import net.jacobpeterson.alpaca.domain.marketdata.realtime.quote.QuoteMessage;
import net.jacobpeterson.alpaca.domain.marketdata.realtime.trade.TradeMessage;
import net.jacobpeterson.alpaca.enums.api.DataAPIType;
import net.jacobpeterson.alpaca.util.gson.GsonUtil;
import net.jacobpeterson.alpaca.websocket.broker.client.AlpacaWebsocketClient;
import net.jacobpeterson.alpaca.websocket.marketdata.listener.MarketDataListener;
import net.jacobpeterson.alpaca.websocket.marketdata.message.MarketDataMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link MarketDataWebsocketClient} represents a client for the {@link AlpacaAPI} market data Websocket stream.
 */
public class MarketDataWebsocketClient implements WebsocketClient<MarketDataListener, MarketDataMessageType,
        MarketDataMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataWebsocketClient.class);
    private static final String ALL_TICKERS = "*";
    private static final String MESSAGE_TYPE_KEY = "T";

    private final String keyID;
    private final String secret;
    private final String oAuthToken;
    private final String websocketAPIURL;
    private final List<MarketDataListener> listeners;

    private MarketDataWebsocketClientEndpoint marketDataWebsocketClientEndpoint;
    private WebsocketStateListener websocketStateListener;
    private boolean authenticated;

    /**
     * Instantiates a new {@link AlpacaWebsocketClient}.
     *
     * @param keyID       the key ID
     * @param secret      the secret
     * @param dataAPIType the {@link DataAPIType}
     */
    public MarketDataWebsocketClient(String keyID, String secret, DataAPIType dataAPIType) {
        this(keyID, secret, null, dataAPIType);
    }

    /**
     * Instantiates a new {@link AlpacaWebsocketClient}.
     *
     * @param oAuthToken  the OAuth token
     * @param dataAPIType the {@link DataAPIType}
     */
    public MarketDataWebsocketClient(String oAuthToken, DataAPIType dataAPIType) {
        this(null, null, oAuthToken, dataAPIType);
    }

    /**
     * Instantiates a new {@link AlpacaWebsocketClient}.
     *
     * @param keyID       the key ID
     * @param secret      the secret
     * @param oAuthToken  the OAuth token
     * @param dataAPIType the {@link DataAPIType}
     */
    private MarketDataWebsocketClient(String keyID, String secret, String oAuthToken, DataAPIType dataAPIType) {
        this.keyID = keyID;
        this.secret = secret;
        this.oAuthToken = oAuthToken;
        this.websocketAPIURL = dataAPIType.getURL();

        listeners = new ArrayList<>();
    }

    @Override
    public void addListener(MarketDataListener streamListener) throws WebsocketException {
        if (!isConnected()) {
            try {
                connect();
            } catch (Exception exception) {
                throw new WebsocketException(exception);
            }
        }

        listeners.add(streamListener);

        submitRequest(streamListener, false);
    }

    @Override
    public void removeListener(MarketDataListener streamListener) throws WebsocketException {
        listeners.remove(streamListener);

        if (listeners.isEmpty() && isConnected()) {
            try {
                disconnect();
            } catch (Exception exception) {
                throw new WebsocketException(exception);
            }
        } else {
            submitRequest(streamListener, true);
        }
    }

    @Override
    public void connect() throws URISyntaxException, IOException, DeploymentException {
        LOGGER.info("Connecting...");

        marketDataWebsocketClientEndpoint = new MarketDataWebsocketClientEndpoint(this, new URI(websocketAPIURL));
        marketDataWebsocketClientEndpoint.setWebsocketStateListener(websocketStateListener);
        marketDataWebsocketClientEndpoint.setAutomaticallyReconnect(true);
        marketDataWebsocketClientEndpoint.connect();

        LOGGER.info("Connected.");
    }

    @Override
    public void disconnect() throws Exception {
        LOGGER.info("Disconnecting...");

        if (marketDataWebsocketClientEndpoint != null) {
            marketDataWebsocketClientEndpoint.disconnect();
        }

        LOGGER.info("Disconnected.");
    }

    @Override
    public void sendAuthenticationMessage() {
        /* Format of message is:
         * {"action": "auth", "key": "{APCA-API-KEY-ID}", "secret": "{APCA-API-SECRET-KEY}"}
         */

        JsonObject authRequest = new JsonObject();
        authRequest.addProperty("action", "auth");

        if (oAuthToken != null) {
            throw new UnsupportedOperationException("OAuth isn't currently implemented!");
        } else {
            authRequest.addProperty("key", keyID);
            authRequest.addProperty("secret", secret);
        }

        marketDataWebsocketClientEndpoint.sendMessage(authRequest.toString());
    }

    @Override
    public void handleResubscribing() {
        for (MarketDataListener marketDataListener : listeners) {
            submitRequest(marketDataListener, false);
        }
    }

    @Override
    public void handleWebsocketMessage(String message) {
        JsonElement messageJsonElement = GsonUtil.JSON_PARSER.parse(message);
        Preconditions.checkState(messageJsonElement instanceof JsonArray);

        JsonArray messageJsonArray = messageJsonElement.getAsJsonArray();

        for (JsonElement arrayJsonElement : messageJsonArray) {
            if (!(arrayJsonElement instanceof JsonObject)) {
                continue;
            }

            JsonObject messageJsonObject = arrayJsonElement.getAsJsonObject();
            MarketDataMessageType marketDataMessageType = GsonUtil.GSON.fromJson(
                    messageJsonObject.get(MESSAGE_TYPE_KEY), MarketDataMessageType.class);
            if (marketDataMessageType == null) {
                LOGGER.error("Message type not found in message: {}", messageJsonObject);
                continue;
            }

            MarketDataMessage marketDataMessage;
            switch (marketDataMessageType) {
                case SUCCESS:
                    marketDataMessage = GsonUtil.GSON.fromJson(messageJsonObject, SuccessMessage.class);
                    authenticated = isAuthorizationMessageSuccess((SuccessMessage) marketDataMessage);
                    break;
                case ERROR:
                    marketDataMessage = GsonUtil.GSON.fromJson(messageJsonObject, ErrorMessage.class);
                    LOGGER.error("{}", marketDataMessage);
                    break;
                case SUBSCRIPTION:
                    marketDataMessage = GsonUtil.GSON.fromJson(messageJsonObject, SubscriptionsMessage.class);
                    break;
                case TRADE:
                    marketDataMessage = GsonUtil.GSON.fromJson(messageJsonObject, TradeMessage.class);
                    break;
                case QUOTE:
                    marketDataMessage = GsonUtil.GSON.fromJson(messageJsonObject, QuoteMessage.class);
                    break;
                case BAR:
                    marketDataMessage = GsonUtil.GSON.fromJson(messageJsonObject, BarMessage.class);
                    break;
                default:
                    LOGGER.error("Message type {} not implemented!", marketDataMessageType);
                    continue;
            }

            LOGGER.debug("{}", marketDataMessage);
            sendStreamMessageToListeners(marketDataMessageType, marketDataMessage);
        }
    }

    @Override
    public void sendStreamMessageToListeners(MarketDataMessageType marketDataMessageType,
            MarketDataMessage marketDataMessage) {
        for (MarketDataListener marketDataListener : new ArrayList<>(listeners)) {
            boolean sendToStreamListener = false;

            if (marketDataListener.getDataStreams() == null || marketDataListener.getDataStreams().isEmpty()) {
                sendToStreamListener = true;
            } else if (!(marketDataMessage instanceof SymbolMessage) && marketDataListener.getDataStreams()
                    .values().stream().anyMatch(types -> types.contains(marketDataMessageType))) {
                sendToStreamListener = true;
            } else if (marketDataMessage instanceof SymbolMessage) {
                SymbolMessage symbolMessage = (SymbolMessage) marketDataMessage;
                Set<MarketDataMessageType> symbolMessageTypes = marketDataListener.getDataStreams()
                        .getOrDefault(symbolMessage.getSymbol(), null);
                Set<MarketDataMessageType> allMessageTypes = marketDataListener.getDataStreams()
                        .getOrDefault(ALL_TICKERS, null);

                if (symbolMessageTypes != null) {
                    sendToStreamListener = symbolMessageTypes.contains(marketDataMessageType);
                } else if (allMessageTypes != null) {
                    sendToStreamListener = allMessageTypes.contains(marketDataMessageType);
                }
            }

            if (sendToStreamListener) {
                marketDataListener.onStreamUpdate(marketDataMessageType, marketDataMessage);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return marketDataWebsocketClientEndpoint != null &&
                marketDataWebsocketClientEndpoint.getUserSession() != null &&
                marketDataWebsocketClientEndpoint.getUserSession().isOpen();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setWebsocketStateListener(WebsocketStateListener websocketStateListener) {
        this.websocketStateListener = websocketStateListener;
    }

    /**
     * Returns true if {@link SuccessMessage} states authentication.
     *
     * @param successMessage the {@link SuccessMessage}
     *
     * @return true if {@link SuccessMessage} states authentication
     */
    private boolean isAuthorizationMessageSuccess(SuccessMessage successMessage) {
        return successMessage.getMessage().equals("authenticated");
    }

    /**
     * Submits a (un)subscribe request.
     *
     * @param marketDataListener the {@link MarketDataListener}s
     * @param unsubscribe        true to unsubscribe, false to subscribe
     */
    private void submitRequest(MarketDataListener marketDataListener, boolean unsubscribe) {
        JsonObject streamRequestJsonObject = new JsonObject();
        streamRequestJsonObject.addProperty("action", unsubscribe ? "unsubscribe" : "subscribe");

        JsonArray tradesJsonArray = new JsonArray();
        JsonArray quotesJsonArray = new JsonArray();
        JsonArray barsJsonArray = new JsonArray();

        Map<String, Set<MarketDataMessageType>> flattenedDataStreams = listeners.stream()
                .filter(listener -> listener != marketDataListener) // Filter out listener being added
                .flatMap(listener -> listener.getDataStreams().entrySet().stream()) // Flat map all data streams
                .collect(HashMap::new, // Collect all data streams into one map
                        (cumulativeMap, listenerEntry) -> {
                            Set<MarketDataMessageType> listenerMessageTypes =
                                    cumulativeMap.getOrDefault(listenerEntry.getKey(), null);
                            if (listenerMessageTypes != null) {
                                listenerMessageTypes.addAll(listenerEntry.getValue());
                            } else {
                                cumulativeMap.put(listenerEntry.getKey(), listenerEntry.getValue());
                            }
                        },
                        HashMap::putAll);

        for (Map.Entry<String, Set<MarketDataMessageType>> listenerEntry : marketDataListener
                .getDataStreams().entrySet()) {
            Set<MarketDataMessageType> currentListenedTypes = flattenedDataStreams
                    .getOrDefault(listenerEntry.getKey(), null);

            if (listenerEntry.getValue().contains(MarketDataMessageType.TRADE) &&
                    (currentListenedTypes == null || !currentListenedTypes.contains(MarketDataMessageType.TRADE))) {
                tradesJsonArray.add(listenerEntry.getKey());
            }
            if (listenerEntry.getValue().contains(MarketDataMessageType.QUOTE) &&
                    (currentListenedTypes == null || !currentListenedTypes.contains(MarketDataMessageType.QUOTE))) {
                quotesJsonArray.add(listenerEntry.getKey());
            }
            if (listenerEntry.getValue().contains(MarketDataMessageType.BAR) &&
                    (currentListenedTypes == null || !currentListenedTypes.contains(MarketDataMessageType.BAR))) {
                barsJsonArray.add(listenerEntry.getKey());
            }
        }

        streamRequestJsonObject.add("trades", tradesJsonArray);
        streamRequestJsonObject.add("quotes", quotesJsonArray);
        streamRequestJsonObject.add("bars", barsJsonArray);

        marketDataWebsocketClientEndpoint.sendMessage(streamRequestJsonObject.toString());

        LOGGER.info("Requested subscriptions to update to {}", streamRequestJsonObject);
    }
}
