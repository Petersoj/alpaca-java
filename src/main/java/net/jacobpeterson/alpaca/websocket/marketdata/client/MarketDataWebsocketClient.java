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
import net.jacobpeterson.alpaca.websocket.marketdata.listener.MarketDataStreamListener;
import net.jacobpeterson.alpaca.websocket.marketdata.message.MarketDataStreamMessageType;
import net.jacobpeterson.domain.alpaca.marketdata.streaming.MarketDataStreamMessage;
import net.jacobpeterson.domain.alpaca.marketdata.streaming.abstracts.MarketDataStreamDataMessage;
import net.jacobpeterson.domain.alpaca.marketdata.streaming.abstracts.MarketDataStreamStatusMessage;
import net.jacobpeterson.domain.alpaca.marketdata.streaming.aggregate.AggregateMinuteMessage;
import net.jacobpeterson.domain.alpaca.marketdata.streaming.authorization.AuthorizationMessage;
import net.jacobpeterson.domain.alpaca.marketdata.streaming.listening.ListeningMessage;
import net.jacobpeterson.domain.alpaca.marketdata.streaming.quote.QuoteMessage;
import net.jacobpeterson.domain.alpaca.marketdata.streaming.trade.TradeMessage;
import net.jacobpeterson.util.gson.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * The type {@link MarketDataWebsocketClient}.
 */
public class MarketDataWebsocketClient implements WebsocketClient {

    /** The logger. */
    private static final Logger LOGGER = LogManager.getLogger(MarketDataWebsocketClient.class);

    /** The constant STREAM_KEY. */
    private static final String STREAM_KEY = "stream";

    /** The constant EVENT_TYPE_KEY. */
    private static final String EVENT_TYPE_KEY = "ev";

    /** The constant EVENT_TYPE_KEY. */
    private static final String DATA_KEY = "data";

    /** The constant ALL_TICKERS. */
    private static final String ALL_TICKERS = "*";

    /** The constant TRADES_STREAM_PREFIX. */
    private static final String TRADES_STREAM_PREFIX =
            GsonUtil.GSON.toJson(MarketDataStreamMessageType.TRADES).replace("\"", "") + ".";

    /** The constant QUOTES_STREAM_PREFIX. */
    private static final String QUOTES_STREAM_PREFIX =
            GsonUtil.GSON.toJson(MarketDataStreamMessageType.QUOTES).replace("\"", "") + ".";

    /** The constant AGGREGATE_MINUTE_STREAM_PREFIX. */
    private static final String AGGREGATE_MINUTE_STREAM_PREFIX =
            GsonUtil.GSON.toJson(MarketDataStreamMessageType.AGGREGATE_MINUTE).replace("\"", "") + ".";

    /** The key id. */
    private final String keyId;

    /** The secret. */
    private final String secret;

    /** The OAuth token. */
    private final String oAuthToken;

    /** The Stream API URL. */
    private final String streamAPIURL;

    /** The observers. */
    private final List<MarketDataStreamListener> listeners;

    /** The client end point. */
    private MarketDataWebsocketClientEndpoint marketDataWebsocketClientEndpoint;

    /** The Authenticated. */
    private boolean authenticated;

    /**
     * Instantiates a new {@link MarketDataWebsocketClient}.
     *
     * @param keyId       the key id
     * @param secret      the secret
     * @param baseDataUrl the base data url
     */
    public MarketDataWebsocketClient(String keyId, String secret, String baseDataUrl) {
        this.keyId = keyId;
        this.secret = secret;
        this.oAuthToken = null;
        this.streamAPIURL = baseDataUrl.replace("https", "wss") + "/stream";

        this.listeners = new ArrayList<>();
    }

    /**
     * Instantiates a new {@link MarketDataWebsocketClient}.
     *
     * @param oAuthToken  the OAuth token
     * @param baseDataUrl the base data url
     */
    public MarketDataWebsocketClient(String oAuthToken, String baseDataUrl) {
        this.keyId = null;
        this.secret = null;
        this.oAuthToken = oAuthToken;
        this.streamAPIURL = baseDataUrl.replace("https", "wss") + "/stream";

        this.listeners = new ArrayList<>();
    }

    @Override
    public void addListener(StreamListener<?, ?> streamListener) throws WebsocketException {
        Preconditions.checkState(streamListener instanceof MarketDataStreamListener);

        if (listeners.isEmpty()) {
            try {
                connect();
            } catch (IOException | URISyntaxException | DeploymentException exception) {
                throw new WebsocketException(exception);
            }
        }

        listeners.add((MarketDataStreamListener) streamListener);

        submitStreamRequest();
    }

    @Override
    public void removeListener(StreamListener<?, ?> streamListener) throws WebsocketException {
        Preconditions.checkState(streamListener instanceof MarketDataStreamListener);

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

        marketDataWebsocketClientEndpoint = new MarketDataWebsocketClientEndpoint(this, new URI(streamAPIURL));
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
            dataJson.addProperty("key_id", keyId);
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

                    MarketDataStreamMessageType marketDataStreamMessageType;

                    // Check if the "stream" is a data stream, that is, Q, T, or AM
                    if (streamString.startsWith(TRADES_STREAM_PREFIX) ||
                            streamString.startsWith(QUOTES_STREAM_PREFIX) ||
                            streamString.startsWith(AGGREGATE_MINUTE_STREAM_PREFIX)) {

                        // Set the stream message type to the parsed EVENT_TYPE_KEY
                        marketDataStreamMessageType = GsonUtil.GSON.fromJson(
                                messageJsonObject.get(DATA_KEY).getAsJsonObject().get(EVENT_TYPE_KEY),
                                MarketDataStreamMessageType.class);
                    } else {
                        marketDataStreamMessageType = GsonUtil.GSON.fromJson(streamJsonElement,
                                MarketDataStreamMessageType.class);
                    }

                    switch (marketDataStreamMessageType) {
                        case AUTHORIZATION:
                            AuthorizationMessage authorizationMessage = GsonUtil.GSON.fromJson(messageJsonObject,
                                    AuthorizationMessage.class);
                            sendStreamMessageToListeners(marketDataStreamMessageType, authorizationMessage);

                            authenticated = isAuthorizationMessageSuccess(authorizationMessage);

                            LOGGER.debug(authorizationMessage);
                            break;
                        case LISTENING:
                            ListeningMessage listeningMessage = GsonUtil.GSON.fromJson(messageJsonObject,
                                    ListeningMessage.class);
                            sendStreamMessageToListeners(marketDataStreamMessageType, listeningMessage);

                            LOGGER.debug(listeningMessage);
                            break;
                        case TRADES:
                            TradeMessage tradeMessage = GsonUtil.GSON.fromJson(messageJsonObject.get(DATA_KEY),
                                    TradeMessage.class);
                            sendStreamMessageToListeners(marketDataStreamMessageType, tradeMessage);

                            LOGGER.debug(tradeMessage);
                            break;
                        case QUOTES:
                            QuoteMessage quoteMessage = GsonUtil.GSON.fromJson(messageJsonObject.get(DATA_KEY),
                                    QuoteMessage.class);
                            sendStreamMessageToListeners(marketDataStreamMessageType, quoteMessage);

                            LOGGER.debug(quoteMessage);
                            break;
                        case AGGREGATE_MINUTE:
                            AggregateMinuteMessage aggregateMinuteMessage = GsonUtil.GSON.fromJson(
                                    messageJsonObject.get(DATA_KEY), AggregateMinuteMessage.class);
                            sendStreamMessageToListeners(marketDataStreamMessageType, aggregateMinuteMessage);

                            LOGGER.debug(aggregateMinuteMessage);
                            break;
                        default:
                            LOGGER.error("Unhandled stream type: " + marketDataStreamMessageType);
                    }
                } catch (JsonParseException exception) {
                    LOGGER.error("Could not parse message: " + messageJsonObject, exception);
                }
            } else {
                LOGGER.error("Unknown stream message: " + messageJsonObject);
            }
        }
    }

    @Override
    public void sendStreamMessageToListeners(StreamMessageType streamMessageType, StreamMessage streamMessage) {
        Preconditions.checkState(streamMessageType instanceof MarketDataStreamMessageType);
        Preconditions.checkState(streamMessage instanceof MarketDataStreamMessage);

        MarketDataStreamMessageType marketDataStreamMessageType = (MarketDataStreamMessageType) streamMessageType;
        MarketDataStreamMessage marketDataStreamMessage = (MarketDataStreamMessage) streamMessage;

        for (MarketDataStreamListener streamListener : new ArrayList<>(listeners)) {
            boolean sendToStreamListener = false;

            if (marketDataStreamMessage instanceof MarketDataStreamStatusMessage) {
                if (streamListener.getDataStreams() != null && !streamListener.getDataStreams().isEmpty()) {
                    // Send the message if any message type registered in the listener for
                    // any ticker is a status message
                    sendToStreamListener = streamListener.getDataStreams().values().stream().anyMatch(types ->
                            types.contains(MarketDataStreamMessageType.LISTENING) ||
                                    types.contains(MarketDataStreamMessageType.AUTHORIZATION));
                } else { // If the data streams list is empty or null, send the message
                    sendToStreamListener = true;
                }
            } else if (marketDataStreamMessage instanceof MarketDataStreamDataMessage) {
                MarketDataStreamDataMessage marketDataStreamDataMessage = (MarketDataStreamDataMessage)
                        marketDataStreamMessage;

                if (streamListener.getDataStreams() != null && !streamListener.getDataStreams().isEmpty()) {
                    Set<MarketDataStreamMessageType> tickerMessageTypes =
                            streamListener.getDataStreams().getOrDefault(marketDataStreamDataMessage.getTicker(), null);
                    Set<MarketDataStreamMessageType> allTickerMessageTypes =
                            streamListener.getDataStreams().getOrDefault(ALL_TICKERS, null);

                    if (tickerMessageTypes != null) {
                        sendToStreamListener = tickerMessageTypes.contains(marketDataStreamMessageType);
                    } else if (allTickerMessageTypes != null) {
                        sendToStreamListener = allTickerMessageTypes.contains(marketDataStreamMessageType);
                    }
                } else { // If the data streams list is empty or null, send the message
                    sendToStreamListener = true;
                }
            }

            if (sendToStreamListener) {
                streamListener.onStreamUpdate(marketDataStreamMessageType, marketDataStreamMessage);
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
     * Is authorization message success boolean.
     *
     * @param authorizationMessage the authorization message
     *
     * @return the boolean
     */
    private boolean isAuthorizationMessageSuccess(AuthorizationMessage authorizationMessage) {
        return authorizationMessage.getData().getStatus().equalsIgnoreCase("authorized") &&
                authorizationMessage.getData().getAction().equalsIgnoreCase("authenticate");
    }

    /**
     * Submit stream request.
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

        LOGGER.info("Requested subscriptions to update to " + streamsJsonArray);
    }

    /**
     * Gets the registered message types. May contain duplicates.
     *
     * @return the registered message types
     */
    public HashMap<String, Set<MarketDataStreamMessageType>> getRegisteredMessageTypes() {
        HashMap<String, Set<MarketDataStreamMessageType>> allMarketDataStreamMessageTypes = new HashMap<>();

        for (MarketDataStreamListener streamListener : new ArrayList<>(listeners)) {
            if (streamListener.getDataStreams() == null || streamListener.getDataStreams().isEmpty()) {
                continue; // Skip over empty/null data stream listeners
            }

            streamListener.getDataStreams().forEach((dataStreamTicker, marketDataStreamMessageTypes) -> {
                // Filter to only subscribable message types and use HashSet to prevent duplicates
                HashSet<MarketDataStreamMessageType> subscribableMessageTypes = marketDataStreamMessageTypes.stream()
                        .filter(MarketDataStreamMessageType::isAPISubscribable)
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
