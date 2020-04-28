package io.github.mainstringargs.alpaca.websocket.marketdata.client;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import io.github.mainstringargs.abstracts.websocket.client.WebsocketClient;
import io.github.mainstringargs.abstracts.websocket.listener.StreamListener;
import io.github.mainstringargs.abstracts.websocket.message.StreamMessage;
import io.github.mainstringargs.abstracts.websocket.message.StreamMessageType;
import io.github.mainstringargs.alpaca.websocket.broker.listener.AlpacaStreamListener;
import io.github.mainstringargs.alpaca.websocket.marketdata.listener.MarketDataStreamListener;
import io.github.mainstringargs.alpaca.websocket.marketdata.message.MarketDataStreamMessageType;
import io.github.mainstringargs.domain.alpaca.marketdata.streaming.MarketDataStreamDataMessage;
import io.github.mainstringargs.domain.alpaca.marketdata.streaming.MarketDataStreamMessage;
import io.github.mainstringargs.domain.alpaca.marketdata.streaming.MarketDataStreamStatusMessage;
import io.github.mainstringargs.domain.alpaca.marketdata.streaming.aggregate.AggregateMinuteMessage;
import io.github.mainstringargs.domain.alpaca.marketdata.streaming.authorization.AuthorizationMessage;
import io.github.mainstringargs.domain.alpaca.marketdata.streaming.listening.ListeningMessage;
import io.github.mainstringargs.domain.alpaca.marketdata.streaming.quote.QuoteMessage;
import io.github.mainstringargs.domain.alpaca.marketdata.streaming.trade.TradeMessage;
import io.github.mainstringargs.util.gson.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Alpaca websocket client.
 */
public class MarketDataWebsocketClient implements WebsocketClient {

    /** The logger. */
    private static final Logger LOGGER = LogManager.getLogger(MarketDataWebsocketClient.class);

    /** The constant STREAM_KEY. */
    private static final String STREAM_KEY = "stream";

    /** The constant EVENT_TYPE_KEY. */
    private static final String EVENT_TYPE_KEY = "ev";

    /** The key id. */
    private final String keyId;

    /** The secret. */
    private final String secret;

    /** The Stream API URL. */
    private final String streamAPIURL;

    /** The observers. */
    private final List<MarketDataStreamListener> listeners;

    /** The client end point. */
    private MarketDataWebsocketClientEndpoint marketDataWebsocketClientEndpoint;

    /** The Authenticated. */
    private boolean authenticated;

    /**
     * Instantiates a new Market data websocket client.
     *
     * @param keyId       the key id
     * @param secret      the secret
     * @param baseDataUrl the base data url
     */
    public MarketDataWebsocketClient(String keyId, String secret, String baseDataUrl) {
        this.keyId = keyId;
        this.secret = secret;
        this.streamAPIURL = baseDataUrl.replace("https", "wss") + "/stream";

        this.listeners = new ArrayList<>();
    }

    @Override
    public void addListener(StreamListener streamListener) {
        Preconditions.checkState(streamListener instanceof MarketDataStreamListener);

        if (listeners.isEmpty()) {
            connect();
        }

        listeners.add((MarketDataStreamListener) streamListener);

        submitStreamRequest();
    }

    @Override
    public void removeListener(StreamListener streamListener) {
        Preconditions.checkState(streamListener instanceof MarketDataStreamListener);

        listeners.remove(streamListener);

        submitStreamRequest();

        if (listeners.isEmpty()) {
            disconnect();
        }
    }

    @Override
    public void connect() {
        LOGGER.info("Connecting...");

        try {
            marketDataWebsocketClientEndpoint = new MarketDataWebsocketClientEndpoint(this, new URI(streamAPIURL));
            marketDataWebsocketClientEndpoint.connect();

            LOGGER.info("Connected.");
        } catch (URISyntaxException | DeploymentException | IOException e) {
            LOGGER.throwing(e);
        }
    }

    @Override
    public void disconnect() {
        LOGGER.info("Disconnecting...");

        try {
            marketDataWebsocketClientEndpoint.getUserSession().close();

            LOGGER.info("Disconnected.");
        } catch (IOException e) {
            LOGGER.throwing(e);
        }
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
        dataJson.addProperty("key_id", keyId);
        dataJson.addProperty("secret_key", secret);

        authRequest.add("data", dataJson);

        marketDataWebsocketClientEndpoint.sendMessage(authRequest.toString());
    }

    @Override
    public void handleWebsocketMessage(String message) {
        JsonElement messageJsonElement = GsonUtil.JSON_PARSER.parse(message);

        Preconditions.checkState(messageJsonElement instanceof JsonObject);
        JsonObject messageJsonObject = messageJsonElement.getAsJsonObject();

        if (messageJsonObject.has(STREAM_KEY)) {
            JsonElement streamJsonElement = messageJsonObject.get(STREAM_KEY);

            if (streamJsonElement instanceof JsonPrimitive) {
                try {
                    MarketDataStreamMessageType marketDataStreamMessageType = GsonUtil.GSON.fromJson(streamJsonElement,
                            MarketDataStreamMessageType.class);

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
                        default:
                            LOGGER.error("Unhandled stream type: " + marketDataStreamMessageType);
                    }
                } catch (JsonParseException exception) {
                    LOGGER.error("Could not parse message: " + messageJsonObject, exception);
                }
            } else {
                LOGGER.error("Unknown stream message: " + messageJsonObject);
            }
        } else { // Must be a data object with "ev" key
            JsonElement eventTypeJsonElement = messageJsonObject.get(EVENT_TYPE_KEY);

            if (eventTypeJsonElement instanceof JsonPrimitive) {
                try {
                    MarketDataStreamMessageType marketDataStreamMessageType =
                            GsonUtil.GSON.fromJson(eventTypeJsonElement, MarketDataStreamMessageType.class);

                    switch (marketDataStreamMessageType) {
                        case TRADES:
                            TradeMessage tradeMessage = GsonUtil.GSON.fromJson(messageJsonObject, TradeMessage.class);
                            sendStreamMessageToListeners(marketDataStreamMessageType, tradeMessage);
                            break;
                        case QUOTES:
                            QuoteMessage quoteMessage = GsonUtil.GSON.fromJson(messageJsonObject, QuoteMessage.class);
                            quoteMessage.setSym(quoteMessage.getTicker());
                            sendStreamMessageToListeners(marketDataStreamMessageType, quoteMessage);
                            break;
                        case AGGREGATE_MINUTE:
                            AggregateMinuteMessage aggregateMinuteMessage = GsonUtil.GSON.fromJson(messageJsonObject,
                                    AggregateMinuteMessage.class);
                            sendStreamMessageToListeners(marketDataStreamMessageType, aggregateMinuteMessage);
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
        MarketDataStreamMessageType marketDataStreamMessageType = (MarketDataStreamMessageType) streamMessageType;

        if (streamMessage instanceof MarketDataStreamStatusMessage) {
            for (MarketDataStreamListener marketDataStreamListener : listeners) {
                if (marketDataStreamListener.getDataStreams() == null ||
                        marketDataStreamListener.getDataStreams().isEmpty() ||
                        marketDataStreamListener.getDataStreams().values().stream()
                                .anyMatch(types -> types.contains(MarketDataStreamMessageType.LISTENING) ||
                                        types.contains(MarketDataStreamMessageType.AUTHORIZATION))) {
                    marketDataStreamListener.onStreamUpdate(marketDataStreamMessageType,
                            (MarketDataStreamMessage) streamMessage);
                }
            }
        } else if (streamMessage instanceof MarketDataStreamDataMessage) {
            MarketDataStreamDataMessage marketDataStreamDataMessage = (MarketDataStreamDataMessage) streamMessage;
            for (MarketDataStreamListener marketDataStreamListener : listeners) {
                if (marketDataStreamListener.getDataStreams().containsKey(marketDataStreamDataMessage.getSym())) {
                    if (marketDataStreamListener.getDataStreams().get(marketDataStreamDataMessage.getSym())
                            .contains(marketDataStreamMessageType)) {
                        marketDataStreamListener.onStreamUpdate(marketDataStreamMessageType,
                                marketDataStreamDataMessage);
                    }
                }
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
        // Stream request example:
        // {
        //     "action": "listen",
        //     "data": {
        //         "streams": ["T.SPY", "Q.SPY", "AM.SPY"]
        //     }
        // }

        JsonObject streamRequestJsonObject = new JsonObject();
        streamRequestJsonObject.addProperty("action", "listen");

        JsonArray streamsJsonArray = new JsonArray();
        getRegisteredMessageTypes().forEach(alpacaStreamMessageType ->
                streamsJsonArray.add(alpacaStreamMessageType.getAPIName()));

        JsonObject dataJsonObject = new JsonObject();
        dataJsonObject.add("streams", streamsJsonArray);

        streamRequestJsonObject.add("data", dataJsonObject);

        marketDataWebsocketClientEndpoint.sendMessage(streamRequestJsonObject.toString());

        LOGGER.info("Requested subscriptions to update to " + streamsJsonArray);
    }

    /**
     * Gets the registered message types.
     *
     * @return the registered message types
     */
    public Set<AlpacaStreamMessageType> getRegisteredMessageTypes() {
        Set<AlpacaStreamMessageType> registeredStreamMessageTypes = new HashSet<>();

        for (AlpacaStreamListener alpacaStreamListener : listeners) {
            Set<AlpacaStreamMessageType> alpacaStreamMessageTypes = alpacaStreamListener.getStreamMessageTypes();

            // if its empty, assume they want everything
            Set<AlpacaStreamMessageType> streamMessageTypesToAdd = alpacaStreamMessageTypes == null ? new HashSet<>() :
                    alpacaStreamMessageTypes.stream().filter(AlpacaStreamMessageType::isAPISubscribable)
                            .collect(Collectors.toSet());

            registeredStreamMessageTypes.addAll(streamMessageTypesToAdd);
        }

        return registeredStreamMessageTypes;
    }
}
