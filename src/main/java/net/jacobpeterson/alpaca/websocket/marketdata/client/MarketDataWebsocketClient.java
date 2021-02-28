package net.jacobpeterson.alpaca.websocket.marketdata.client;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.jacobpeterson.abstracts.websocket.client.WebsocketClient;
import net.jacobpeterson.abstracts.websocket.exception.WebsocketException;
import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.abstracts.websocket.message.StreamMessage;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;
import net.jacobpeterson.alpaca.enums.api.DataAPIType;
import net.jacobpeterson.alpaca.websocket.broker.client.AlpacaWebsocketClient;
import net.jacobpeterson.alpaca.websocket.marketdata.listener.MarketDataListener;
import net.jacobpeterson.alpaca.websocket.marketdata.message.MarketDataMessageType;
import net.jacobpeterson.util.gson.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link MarketDataWebsocketClient} represents a client for the {@link net.jacobpeterson.alpaca.AlpacaAPI} market data
 * Websocket stream.
 */
public class MarketDataWebsocketClient implements WebsocketClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataWebsocketClient.class);

    private static final String STREAM_KEY = "stream";
    private static final String EVENT_TYPE_KEY = "ev";
    private static final String DATA_KEY = "data";
    private static final String ALL_TICKERS = "*";
    private static final String TRADES_STREAM_PREFIX =
            GsonUtil.GSON.toJson(MarketDataMessageType.TRADES).replace("\"", "") + ".";
    private static final String QUOTES_STREAM_PREFIX =
            GsonUtil.GSON.toJson(MarketDataMessageType.QUOTES).replace("\"", "") + ".";
    private static final String AGGREGATE_MINUTE_STREAM_PREFIX =
            GsonUtil.GSON.toJson(MarketDataMessageType.AGGREGATE_MINUTE).replace("\"", "") + ".";

    private final String keyID;
    private final String secret;
    private final String oAuthToken;
    private final String websocketAPIURL;
    private final List<MarketDataListener> listeners;

    private MarketDataWebsocketClientEndpoint marketDataWebsocketClientEndpoint;
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
    public void addListener(StreamListener<?, ?> streamListener) throws WebsocketException {
        Preconditions.checkState(streamListener instanceof MarketDataListener);

        if (!isConnected()) {
            try {
                connect();
            } catch (Exception exception) {
                throw new WebsocketException(exception);
            }
        }

        listeners.add((MarketDataListener) streamListener);

        submitStreamRequest();
    }

    @Override
    public void removeListener(StreamListener<?, ?> streamListener) throws WebsocketException {
        Preconditions.checkState(streamListener instanceof MarketDataListener);

        listeners.remove(streamListener);

        if (listeners.isEmpty()) {
            try {
                disconnect();
            } catch (Exception exception) {
                throw new WebsocketException(exception);
            }
        } else {
            submitStreamRequest();
        }
    }

    @Override
    public void connect() throws URISyntaxException, IOException, DeploymentException {
        LOGGER.info("Connecting...");

        marketDataWebsocketClientEndpoint = new MarketDataWebsocketClientEndpoint(this, new URI(websocketAPIURL));
        marketDataWebsocketClientEndpoint.setAutomaticallyReconnect(true);
        marketDataWebsocketClientEndpoint.connect();

        LOGGER.info("Connected.");
    }

    @Override
    public void disconnect() throws Exception {
        LOGGER.info("Disconnecting...");

        marketDataWebsocketClientEndpoint.disconnect();

        LOGGER.info("Disconnected.");
    }

    @Override
    public void sendAuthenticationMessage() {
        /* Format of message is:
         *  {
         *      "action": "authenticate",
         *      "data": {
         *          "key_id": "{YOUR_API_KEY_ID}",
         *          "secret_key": "{YOUR_API_SECRET_KEY}"
         *      }
         *  }
         */

        JsonObject authRequest = new JsonObject();
        authRequest.addProperty("action", "authenticate");

        JsonObject dataJson = new JsonObject();
        if (oAuthToken != null) {
            dataJson.addProperty("oauth_token", oAuthToken);
        } else {
            dataJson.addProperty("key_id", keyID);
            dataJson.addProperty("secret_key", secret);
        }

        authRequest.add("data", dataJson);

        marketDataWebsocketClientEndpoint.sendMessage(authRequest.toString());
    }

    @Override
    public void handleResubscribing() {
        submitStreamRequest();
    }

    @Override
    public void handleWebsocketMessage(String message) {
        /* Format of authorization message:
         * {"stream":"authorization","data":{"action":"authenticate","status":"authorized"}}
         *
         * Format of listened streams message:
         * {"stream":"listening","data":{"streams":["Q.AAPL"]}}
         *
         * Format of stream data message:
         * {"stream":"Q.AAPL","data":{"ev":"Q","T":"AAPL","x":17,"p":112.12,"s":1,"X":3,"P":112.98,"S":1,
         * "c":[0],"t":1601645053026000000}}
         */

        JsonElement messageJsonElement = GsonUtil.JSON_PARSER.parse(message);
        Preconditions.checkState(messageJsonElement instanceof JsonObject);

        JsonObject messageJsonObject = messageJsonElement.getAsJsonObject();

        if (messageJsonObject.has(STREAM_KEY)) {
            JsonElement streamJsonElement = messageJsonObject.get(STREAM_KEY);

            if (streamJsonElement instanceof JsonPrimitive) {
                try {
                    String streamString = streamJsonElement.getAsString();

                    MarketDataMessageType marketDataMessageType;

                    // Check if the "stream" is a data stream, that is, Q, T, or AM
                    if (streamString.startsWith(TRADES_STREAM_PREFIX) ||
                            streamString.startsWith(QUOTES_STREAM_PREFIX) ||
                            streamString.startsWith(AGGREGATE_MINUTE_STREAM_PREFIX)) {

                        // Set the stream message type to the parsed EVENT_TYPE_KEY
                        marketDataMessageType = GsonUtil.GSON.fromJson(
                                messageJsonObject.get(DATA_KEY).getAsJsonObject().get(EVENT_TYPE_KEY),
                                MarketDataMessageType.class);
                    } else {
                        marketDataMessageType = GsonUtil.GSON.fromJson(streamJsonElement,
                                MarketDataMessageType.class);
                    }

                    switch (marketDataMessageType) {
                        case AUTHORIZATION:
                            AuthorizationMessage authorizationMessage = GsonUtil.GSON.fromJson(messageJsonObject,
                                    AuthorizationMessage.class);
                            sendStreamMessageToListeners(marketDataMessageType, authorizationMessage);

                            authenticated = isAuthorizationMessageSuccess(authorizationMessage);

                            LOGGER.debug("{}", authorizationMessage);
                            break;
                        case LISTENING:
                            ListeningMessage listeningMessage = GsonUtil.GSON.fromJson(messageJsonObject,
                                    ListeningMessage.class);
                            sendStreamMessageToListeners(marketDataMessageType, listeningMessage);

                            LOGGER.debug("{}", listeningMessage);
                            break;
                        case TRADES:
                            TradeMessage tradeMessage = GsonUtil.GSON.fromJson(messageJsonObject.get(DATA_KEY),
                                    TradeMessage.class);
                            sendStreamMessageToListeners(marketDataMessageType, tradeMessage);

                            LOGGER.debug("{}", tradeMessage);
                            break;
                        case QUOTES:
                            QuoteMessage quoteMessage = GsonUtil.GSON.fromJson(messageJsonObject.get(DATA_KEY),
                                    QuoteMessage.class);
                            sendStreamMessageToListeners(marketDataMessageType, quoteMessage);

                            LOGGER.debug("{}", quoteMessage);
                            break;
                        case AGGREGATE_MINUTE:
                            AggregateMinuteMessage aggregateMinuteMessage = GsonUtil.GSON.fromJson(
                                    messageJsonObject.get(DATA_KEY), AggregateMinuteMessage.class);
                            sendStreamMessageToListeners(marketDataMessageType, aggregateMinuteMessage);

                            LOGGER.debug("{}", aggregateMinuteMessage);
                            break;
                        default:
                            LOGGER.error("Unhandled stream type: {}", marketDataMessageType);
                    }
                } catch (JsonParseException exception) {
                    LOGGER.error("Could not parse message: {}\n{}", messageJsonObject, exception);
                }
            } else {
                LOGGER.error("Unknown stream message: {}", messageJsonObject);
            }
        }
    }

    @Override
    public void sendStreamMessageToListeners(StreamMessageType streamMessageType, StreamMessage streamMessage) {
        Preconditions.checkState(streamMessageType instanceof MarketDataMessageType);
        Preconditions.checkState(streamMessage instanceof MarketDataStreamMessage);

        MarketDataMessageType marketDataMessageType = (MarketDataMessageType) streamMessageType;
        MarketDataStreamMessage marketDataStreamMessage = (MarketDataStreamMessage) streamMessage;

        for (MarketDataListener streamListener : new ArrayList<>(listeners)) {
            boolean sendToStreamListener = false;

            if (marketDataStreamMessage instanceof MarketDataStreamStatusMessage) {
                if (streamListener.getDataStreams() != null && !streamListener.getDataStreams().isEmpty()) {
                    // Send the message if any message type registered in the listener for
                    // any ticker is a status message
                    sendToStreamListener = streamListener.getDataStreams().values().stream().anyMatch(types ->
                            types.contains(MarketDataMessageType.LISTENING) ||
                                    types.contains(MarketDataMessageType.AUTHORIZATION));
                } else { // If the data streams list is empty or null, send the message
                    sendToStreamListener = true;
                }
            } else if (marketDataStreamMessage instanceof MarketDataStreamDataMessage) {
                MarketDataStreamDataMessage marketDataStreamDataMessage = (MarketDataStreamDataMessage)
                        marketDataStreamMessage;

                if (streamListener.getDataStreams() != null && !streamListener.getDataStreams().isEmpty()) {
                    Set<MarketDataMessageType> tickerMessageTypes =
                            streamListener.getDataStreams().getOrDefault(marketDataStreamDataMessage.getTicker(), null);
                    Set<MarketDataMessageType> allTickerMessageTypes =
                            streamListener.getDataStreams().getOrDefault(ALL_TICKERS, null);

                    if (tickerMessageTypes != null) {
                        sendToStreamListener = tickerMessageTypes.contains(marketDataMessageType);
                    } else if (allTickerMessageTypes != null) {
                        sendToStreamListener = allTickerMessageTypes.contains(marketDataMessageType);
                    }
                } else { // If the data streams list is empty or null, send the message
                    sendToStreamListener = true;
                }
            }

            if (sendToStreamListener) {
                streamListener.onStreamUpdate(marketDataMessageType, marketDataStreamMessage);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return marketDataWebsocketClientEndpoint.getUserSession().isOpen();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Returns true if {@link AuthorizationMessage} states success.
     *
     * @param authorizationMessage the {@link AuthorizationMessage}
     *
     * @return true if {@link AuthorizationMessage} states success
     */
    private boolean isAuthorizationMessageSuccess(AuthorizationMessage authorizationMessage) {
        return authorizationMessage.getData().getStatus().equalsIgnoreCase("authorized") &&
                authorizationMessage.getData().getAction().equalsIgnoreCase("authenticate");
    }

    /**
     * Submits a stream request.
     */
    private void submitStreamRequest() {
        /* Stream request example:
         * {
         *     "action": "listen",
         *     "data": {
         *         "streams": ["T.SPY", "Q.SPY", "AM.SPY"]
         *     }
         * }
         */

        JsonObject streamRequestJsonObject = new JsonObject();
        streamRequestJsonObject.addProperty("action", "listen");

        JsonArray streamsJsonArray = new JsonArray();
        getRegisteredMessageTypes().forEach((s, m) -> m.forEach(i -> streamsJsonArray.add(i.getAPIName() + "." + s)));

        JsonObject dataJsonObject = new JsonObject();
        dataJsonObject.add("streams", streamsJsonArray);

        streamRequestJsonObject.add("data", dataJsonObject);

        marketDataWebsocketClientEndpoint.sendMessage(streamRequestJsonObject.toString());

        LOGGER.info("Requested subscriptions to update to {}", streamsJsonArray);
    }

    /**
     * Gets the registered {@link MarketDataMessageType}s of tickers. May contain duplicates.
     *
     * @return the registered {@link MarketDataMessageType}s of tickers
     */
    public HashMap<String, Set<MarketDataMessageType>> getRegisteredMessageTypes() {
        HashMap<String, Set<MarketDataMessageType>> allMarketDataStreamMessageTypes = new HashMap<>();

        for (MarketDataListener streamListener : new ArrayList<>(listeners)) {
            if (streamListener.getDataStreams() == null || streamListener.getDataStreams().isEmpty()) {
                continue; // Skip over empty/null data stream listeners
            }

            streamListener.getDataStreams().forEach((dataStreamTicker, marketDataStreamMessageTypes) -> {
                // Filter to only subscribable message types and use HashSet to prevent duplicates
                HashSet<MarketDataMessageType> subscribableMessageTypes = marketDataStreamMessageTypes.stream()
                        .filter(MarketDataMessageType::isAPISubscribable)
                        .collect(Collectors.toCollection(HashSet::new));

                // Add the subscribableMessageTypes if the ticker already exists, otherwise insert a new pair
                if (allMarketDataStreamMessageTypes.containsKey(dataStreamTicker)) {
                    allMarketDataStreamMessageTypes.get(dataStreamTicker).addAll(subscribableMessageTypes);
                } else {
                    allMarketDataStreamMessageTypes.put(dataStreamTicker, subscribableMessageTypes);
                }
            });
        }

        return allMarketDataStreamMessageTypes;
    }
}
