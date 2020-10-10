package net.jacobpeterson.polygon.websocket.client;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.jacobpeterson.abstracts.websocket.client.WebsocketClient;
import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.abstracts.websocket.message.StreamMessage;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;
import net.jacobpeterson.domain.polygon.websocket.PolygonStreamMessage;
import net.jacobpeterson.domain.polygon.websocket.aggregate.AggregatePerMinuteMessage;
import net.jacobpeterson.domain.polygon.websocket.aggregate.AggregatePerSecondMessage;
import net.jacobpeterson.domain.polygon.websocket.quote.QuoteMessage;
import net.jacobpeterson.domain.polygon.websocket.status.StatusMessage;
import net.jacobpeterson.domain.polygon.websocket.trade.TradeMessage;
import net.jacobpeterson.polygon.websocket.listener.PolygonStreamListener;
import net.jacobpeterson.polygon.websocket.message.PolygonStreamMessageType;
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
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * The Class WebsocketClient.
 */
public class PolygonWebsocketClient implements WebsocketClient {

    /** The logger. */
    private static final Logger LOGGER = LogManager.getLogger(PolygonWebsocketClient.class);

    /** The all tickers. */
    private static final String ALL_TICKERS = "*";

    /** The constant EVENT_TYPE_KEY. */
    private static final String EVENT_TYPE_KEY = "ev";

    /** The key id. */
    private final String keyId;

    /** The Websocket url. */
    private final String websocketURL;

    /** The listeners. */
    private final List<PolygonStreamListener> listeners;

    /** The client end point. */
    private PolygonWebsocketClientEndpoint polygonWebsocketClientEndpoint;

    /** The Authenticated. */
    private boolean authenticated;

    /**
     * Instantiates a new Polygon websocket client.
     *
     * @param keyId        the key id
     * @param websocketURL the websocket url
     */
    public PolygonWebsocketClient(String keyId, String websocketURL) {
        this.keyId = keyId;
        this.websocketURL = websocketURL;

        this.listeners = new ArrayList<>();
    }

    @Override
    public void addListener(StreamListener listener) {
        Preconditions.checkState(listener instanceof PolygonStreamListener);

        if (listeners.isEmpty()) {
            connect();
        }

        listeners.add((PolygonStreamListener) listener);

        submitStreamRequest(PolygonStreamAction.SUBSCRIBE, (PolygonStreamListener) listener);
    }

    @Override
    public void removeListener(StreamListener listener) {
        Preconditions.checkState(listener instanceof PolygonStreamListener);

        listeners.remove(listener);

        submitStreamRequest(PolygonStreamAction.UNSUBSCRIBE, (PolygonStreamListener) listener);

        if (listeners.isEmpty()) {
            disconnect();
        }
    }

    @Override
    public void connect() {
        LOGGER.info("Connecting...");

        try {
            polygonWebsocketClientEndpoint = new PolygonWebsocketClientEndpoint(this, new URI(websocketURL));
            polygonWebsocketClientEndpoint.connect();

            LOGGER.info("Connected.");
        } catch (URISyntaxException | DeploymentException | IOException e) {
            LOGGER.throwing(e);
        }
    }

    @Override
    public void disconnect() {
        LOGGER.info("Disconnecting...");

        try {
            polygonWebsocketClientEndpoint.disconnect();
            LOGGER.info("Disconnected.");
        } catch (IOException e) {
            LOGGER.throwing(e);
        }
    }

    @Override
    public void sendAuthenticationMessage() {
        // Format of message is: {"action":"auth","params":"{API_KEY}"}

        JsonObject authRequest = new JsonObject();
        authRequest.addProperty("action", "auth");
        authRequest.addProperty("params", keyId);

        polygonWebsocketClientEndpoint.sendMessage(authRequest.toString());
    }

    @Override
    public void handleResubscribing() {
        listeners.forEach(polygonStreamListener ->
                submitStreamRequest(PolygonStreamAction.SUBSCRIBE, polygonStreamListener));
    }

    @Override
    public void handleWebsocketMessage(String message) {
        JsonElement messageJsonElement = GsonUtil.JSON_PARSER.parse(message);

        Preconditions.checkState(messageJsonElement instanceof JsonArray);

        for (JsonElement jsonElement : messageJsonElement.getAsJsonArray()) {
            JsonObject messageJsonObject = jsonElement.getAsJsonObject();
            JsonElement eventTypeJsonElement = messageJsonObject.get(EVENT_TYPE_KEY);

            if (eventTypeJsonElement instanceof JsonPrimitive) {
                try {
                    PolygonStreamMessageType polygonStreamMessageType = GsonUtil.GSON.fromJson(eventTypeJsonElement,
                            PolygonStreamMessageType.class);

                    switch (polygonStreamMessageType) {
                        case STATUS:
                            StatusMessage statusMessage = GsonUtil.GSON.fromJson(messageJsonObject,
                                    StatusMessage.class);
                            sendStreamMessageToListeners(polygonStreamMessageType, statusMessage);

                            authenticated = isAuthenticatedStatusMessage(statusMessage);

                            LOGGER.debug(statusMessage);
                            break;
                        case TRADE:
                            sendStreamMessageToListeners(polygonStreamMessageType,
                                    GsonUtil.GSON.fromJson(messageJsonObject, TradeMessage.class));
                            break;
                        case QUOTE:
                            sendStreamMessageToListeners(polygonStreamMessageType,
                                    GsonUtil.GSON.fromJson(messageJsonObject, QuoteMessage.class));
                            break;
                        case AGGREGATE_PER_SECOND:
                            sendStreamMessageToListeners(polygonStreamMessageType,
                                    GsonUtil.GSON.fromJson(messageJsonObject, AggregatePerSecondMessage.class));
                            break;
                        case AGGREGATE_PER_MINUTE:
                            sendStreamMessageToListeners(polygonStreamMessageType,
                                    GsonUtil.GSON.fromJson(messageJsonObject, AggregatePerMinuteMessage.class));
                            break;
                        default:
                            LOGGER.error("Unknown stream object: {}", messageJsonObject);
                    }
                } catch (JsonSyntaxException e) {
                    LOGGER.throwing(e);
                }
            } else {
                LOGGER.error("Unknown stream message: {}", messageJsonObject);
            }
        }
    }

    @Override
    public void sendStreamMessageToListeners(StreamMessageType streamMessageType, StreamMessage streamMessage) {
        Preconditions.checkState(streamMessageType instanceof PolygonStreamMessageType);
        Preconditions.checkState(streamMessage instanceof PolygonStreamMessage);

        PolygonStreamMessageType polygonStreamMessageType = (PolygonStreamMessageType) streamMessageType;
        PolygonStreamMessage polygonStreamMessage = (PolygonStreamMessage) streamMessage;

        for (PolygonStreamListener streamListener : listeners) {
            boolean sendToStreamListener = false;

            if (streamListener.getStockChannels().containsKey(polygonStreamMessage.getSym())) {
                if (streamListener.getStockChannels().get(polygonStreamMessage.getSym())
                        .contains(polygonStreamMessageType)) {
                    sendToStreamListener = true;
                }
            } else if (streamListener.getStockChannels().containsKey(ALL_TICKERS)) {
                if (streamListener.getStockChannels().get(ALL_TICKERS).contains(polygonStreamMessageType)) {
                    sendToStreamListener = true;
                }
            }

            if (sendToStreamListener) {
                streamListener.onStreamUpdate(polygonStreamMessageType, polygonStreamMessage);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return polygonWebsocketClientEndpoint.getUserSession().isOpen();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Is authenticated status message boolean.
     *
     * @param statusMessage the status message
     *
     * @return the boolean
     */
    private boolean isAuthenticatedStatusMessage(StatusMessage statusMessage) {
        return statusMessage.getStatus().equalsIgnoreCase("success") &&
                statusMessage.getMessage().equalsIgnoreCase("authenticated");
    }

    /**
     * Submit stream request.
     *
     * @param polygonStreamAction the polygon stream action
     * @param newListener         the listener
     */
    private void submitStreamRequest(PolygonStreamAction polygonStreamAction, PolygonStreamListener newListener) {
        Preconditions.checkNotNull(polygonStreamAction);
        Preconditions.checkNotNull(newListener);

        ArrayList<String> actionTickerList = new ArrayList<>();

        Map<String, Set<PolygonStreamMessageType>> registeredTickerChannels = getRegisteredTickerChannels(newListener);
        Map<String, Set<PolygonStreamMessageType>> newListenerTickerChannels = newListener.getStockChannels();

        for (String ticker : newListenerTickerChannels.keySet()) {
            Set<PolygonStreamMessageType> registeredChannels = registeredTickerChannels.get(ticker);
            Set<PolygonStreamMessageType> newListenerChannelTypes = newListenerTickerChannels.get(ticker);

            if (registeredTickerChannels.containsKey(ticker)) {
                for (PolygonStreamMessageType listenerChannelType : newListenerChannelTypes) {
                    if (!listenerChannelType.isAPISubscribable()) {
                        continue;
                    }

                    boolean isTickerChannelRegistered = registeredChannels.contains(listenerChannelType);
                    String formattedWebsocketTicker = formatWebsocketTicker(listenerChannelType, ticker);

                    if (isTickerChannelRegistered) {
                        if (polygonStreamAction == PolygonStreamAction.UNSUBSCRIBE) {
                            LOGGER.warn("Cannot unsubscribe from {} for channel {} because it is being used by " +
                                    "another stream listener!", ticker, listenerChannelType.name());
                        } else if (polygonStreamAction == PolygonStreamAction.SUBSCRIBE) {
                            LOGGER.warn("Already subscribed to {} for channel {}", ticker, listenerChannelType.name());
                        }
                    } else { // Not a registered channel by other stream listeners
                        actionTickerList.add(formattedWebsocketTicker);

                        if (polygonStreamAction == PolygonStreamAction.UNSUBSCRIBE) {
                            LOGGER.info("Unsubscribing from {}", formattedWebsocketTicker);
                        } else if (polygonStreamAction == PolygonStreamAction.SUBSCRIBE) {
                            LOGGER.info("Subscribing to {}", formattedWebsocketTicker);
                        }
                    }
                }
            } else { // Not a registered ticker by other stream listeners
                for (PolygonStreamMessageType listenerChannelType : newListenerChannelTypes) {
                    if (!listenerChannelType.isAPISubscribable()) {
                        continue;
                    }

                    String formattedWebsocketTicker = formatWebsocketTicker(listenerChannelType, ticker);

                    actionTickerList.add(formattedWebsocketTicker);

                    if (polygonStreamAction == PolygonStreamAction.UNSUBSCRIBE) {
                        LOGGER.info("Unsubscribing from: {}", formattedWebsocketTicker);
                    } else if (polygonStreamAction == PolygonStreamAction.SUBSCRIBE) {
                        LOGGER.info("Subscribing to:  {}", formattedWebsocketTicker);
                    }
                }
            }
        }

        if (actionTickerList.isEmpty()) {
            LOGGER.info("Did not change any channel listeners");
        } else {
            // Format: {"action":"(un)subscribe","params":"T.AAPL,T.MSFT,T.TSLA"}

            StringJoiner commaActionTickers = new StringJoiner(",");
            actionTickerList.forEach(commaActionTickers::add);

            JsonObject actionJsonObject = new JsonObject();
            actionJsonObject.addProperty("action", polygonStreamAction.getAPIName());
            actionJsonObject.addProperty("params", commaActionTickers.toString());

            polygonWebsocketClientEndpoint.sendMessage(actionJsonObject.toString());

            LOGGER.info("Requested subscriptions to update for: {}", getRegisteredTickerChannels(null));
        }
    }

    /**
     * Format websocket ticker string.
     *
     * @param polygonStreamMessageType the polygon stream message type
     * @param ticker                   the ticker
     *
     * @return the string
     */
    private String formatWebsocketTicker(PolygonStreamMessageType polygonStreamMessageType, String ticker) {
        return polygonStreamMessageType.getAPIName() + "." + ticker;
    }

    /**
     * Gets registered ticker channels.
     *
     * @param exclude listener to exclude from the final map
     *
     * @return the registered ticker channels
     */
    private Map<String, Set<PolygonStreamMessageType>> getRegisteredTickerChannels(PolygonStreamListener exclude) {
        HashMap<String, Set<PolygonStreamMessageType>> registeredTickerChannels = new HashMap<>();

        for (PolygonStreamListener streamListener : listeners) {
            if (streamListener.equals(exclude)) {
                continue;
            }

            Map<String, Set<PolygonStreamMessageType>> stockChannelTypes = streamListener.getStockChannels();

            for (String ticker : stockChannelTypes.keySet()) {
                Set<PolygonStreamMessageType> streamMessageTypes = stockChannelTypes.get(ticker);
                Set<PolygonStreamMessageType> subscribableStreamMessageTypes = streamMessageTypes == null ?
                        new HashSet<>() : streamMessageTypes.stream()
                        .filter(PolygonStreamMessageType::isAPISubscribable)
                        .collect(Collectors.toSet());

                if (!registeredTickerChannels.containsKey(ticker)) {
                    registeredTickerChannels.put(ticker, subscribableStreamMessageTypes);
                } else {
                    registeredTickerChannels.get(ticker).addAll(subscribableStreamMessageTypes);
                }
            }
        }

        return registeredTickerChannels;
    }

    /**
     * The enum Polygon stream action.
     */
    private enum PolygonStreamAction {
        /** The subscribe. */
        SUBSCRIBE("subscribe"),

        /** The unsubscribe. */
        UNSUBSCRIBE("unsubscribe");

        /** The api name. */
        String apiName;

        /**
         * Instantiates a new stream action.
         *
         * @param apiName the api name
         */
        PolygonStreamAction(String apiName) {
            this.apiName = apiName;
        }

        /**
         * Gets api name.
         *
         * @return the api name
         */
        public String getAPIName() {
            return apiName;
        }
    }
}
