package io.github.mainstringargs.polygon.websocket;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.websocket.message.AggregatePerMinuteMessage;
import io.github.mainstringargs.polygon.websocket.message.AggregatePerSecondMessage;
import io.github.mainstringargs.polygon.websocket.message.ChannelMessage;
import io.github.mainstringargs.polygon.websocket.message.QuotesMessage;
import io.github.mainstringargs.polygon.websocket.message.StatusMessage;
import io.github.mainstringargs.polygon.websocket.message.TradesMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 * The Class WebsocketClient.
 */
public class PolygonWebsocketClient implements PolygonWebsocketClientEndpoint.MessageHandler {

    /** The all tickers. */
    private static final String ALL_TICKERS = "*";

    /** The logger. */
    private static Logger LOGGER = LogManager.getLogger(PolygonWebsocketClient.class);

    /** The key id. */
    private String keyId;

    /** The Websocket url. */
    private String websocketURL;

    /** The client end point. */
    private PolygonWebsocketClientEndpoint clientEndPoint = null;

    /** The listeners. */
    private List<PolygonStreamListener> listeners = new ArrayList<>();

    /**
     * Instantiates a new Polygon websocket client.
     *
     * @param keyId        the key id
     * @param websocketURL the websocket url
     */
    public PolygonWebsocketClient(String keyId, String websocketURL) {
        this.keyId = keyId;
        this.websocketURL = websocketURL;
    }

    /**
     * Adds the listener.
     *
     * @param listener the listener
     */
    public synchronized void addListener(PolygonStreamListener listener) {
        if (listeners.isEmpty()) {
            connect();
        }
        listeners.add(listener);

        submitStreamRequest(StreamAction.SUBSCRIBE, listener);
    }

    /**
     * Removes the listener.
     *
     * @param listener the listener
     */
    public synchronized void removeListener(PolygonStreamListener listener) {
        if (listener != null) {
            listeners.remove(listener);

            submitStreamRequest(StreamAction.UNSUBSCRIBE, listener);
        }

        if (listeners.isEmpty()) {
            disconnect();
        }
    }

    /**
     * Connect.
     */
    private void connect() {
        LOGGER.info("Connecting...");

        try {
            clientEndPoint = new PolygonWebsocketClientEndpoint(new URI(websocketURL));
            clientEndPoint.setMessageHandler(this);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        LOGGER.info("Connected.");

        // Format of message is: {"action":"auth","params":"{API_KEY}"}

        JsonObject authRequest = new JsonObject();
        authRequest.addProperty("action", "auth");
        authRequest.addProperty("params", keyId);

        clientEndPoint.sendMessage(authRequest.toString());
    }

    /**
     * Disconnect.
     */
    private void disconnect() {
        LOGGER.info("Disconnecting...");

        try {
            clientEndPoint.getUserSession().close();
            LOGGER.info("Disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(JsonArray array) {
        for (JsonElement jsonElement : array) {
            JsonObject messageJsonObject = jsonElement.getAsJsonObject();

            String eventTypeKey = "ev";

            if (messageJsonObject.has(eventTypeKey)) {
                String eventType = messageJsonObject.get(eventTypeKey).getAsString();

                switch (eventType) {
                    case "status":
                        StatusMessage statusMessage = new StatusMessage(messageJsonObject);
                        LOGGER.debug("Channel status: " + statusMessage.getChannelStatus());
                        break;
                    case "T":
                        sendStreamMessageToListeners(ChannelType.TRADES,
                                new TradesMessage(messageJsonObject));
                        break;
                    case "Q":
                        sendStreamMessageToListeners(ChannelType.QUOTES,
                                new QuotesMessage(messageJsonObject));
                        break;
                    case "A":
                        sendStreamMessageToListeners(ChannelType.AGGREGATE_PER_SECOND,
                                new AggregatePerSecondMessage(messageJsonObject));
                        break;
                    case "AM":
                        sendStreamMessageToListeners(ChannelType.AGGREGATE_PER_MINUTE,
                                new AggregatePerMinuteMessage(messageJsonObject));
                        break;
                    default:
                        LOGGER.error("Unknown event type: " + eventType);
                }
            } else {
                LOGGER.error("Unknown channel message: " + messageJsonObject);
            }
        }
    }

    /**
     * Send stream message to listeners.
     *
     * @param channelType the channel type
     * @param message     the message
     */
    private synchronized void sendStreamMessageToListeners(ChannelType channelType,
            ChannelMessage message) {
        for (PolygonStreamListener streamListener : listeners) {
            boolean sendToStreamListener = false;

            if (streamListener.getStockChannelTypes().containsKey(message.getTicker())) {
                if (streamListener.getStockChannelTypes().get(message.getTicker()).contains(channelType)) {
                    sendToStreamListener = true;
                }
            } else if (streamListener.getStockChannelTypes().containsKey(ALL_TICKERS)) {
                if (streamListener.getStockChannelTypes().get(ALL_TICKERS).contains(channelType)) {
                    sendToStreamListener = true;
                }
            }

            if (sendToStreamListener) {
                streamListener.streamUpdate(message.getTicker(), channelType, message);
            }
        }
    }

    /**
     * Submit stream request.
     *
     * @param streamAction the stream request action
     * @param listener     the listener
     */
    private void submitStreamRequest(StreamAction streamAction,
            PolygonStreamListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        // Format: {"action":"(un)subscribe","params":"T.AAPL,T.MSFT,T.TSLA"}

        ArrayList<String> actionTickerList = new ArrayList<>();

        Map<String, Set<ChannelType>> registeredTickerChannels = getRegisteredTickerChannels(listener);
        Map<String, Set<ChannelType>> listenerTickerChannels = listener.getStockChannelTypes();

        for (String ticker : listenerTickerChannels.keySet()) {
            Set<ChannelType> registeredChannels = registeredTickerChannels.get(ticker);
            Set<ChannelType> listenerChannelTypes = listenerTickerChannels.get(ticker);

            if (registeredTickerChannels.containsKey(ticker)) {
                for (ChannelType listenerChannelType : listenerChannelTypes) {
                    boolean isTickerChannelRegistered =
                            registeredChannels.contains(listenerChannelType);
                    String formattedWebsocketTicker =
                            formatWebsocketTicker(listenerChannelType, ticker);

                    if (isTickerChannelRegistered) {
                        if (streamAction == StreamAction.UNSUBSCRIBE) {
                            LOGGER.warn("Cannot unsubscribe from " + ticker +
                                    " for channel " + listenerChannelType.name() +
                                    " because it is being used by another stream listener!");
                        } else if (streamAction == StreamAction.SUBSCRIBE) {
                            LOGGER.warn("Already subscribed to " + ticker +
                                    " for channel " + listenerChannelType.name() + "!");
                        }
                    } else { // Not a registered channel by other stream listeners
                        actionTickerList.add(formattedWebsocketTicker);

                        if (streamAction == StreamAction.UNSUBSCRIBE) {
                            LOGGER.info(("Unsubscribing from " + formattedWebsocketTicker));
                        } else if (streamAction == StreamAction.SUBSCRIBE) {
                            LOGGER.info(("Subscribing to " + formattedWebsocketTicker));
                        }
                    }
                }
            } else { // Not a registered ticker by other stream listeners
                for (ChannelType listenerChannelType : listenerChannelTypes) {
                    String formattedWebsocketTicker =
                            formatWebsocketTicker(listenerChannelType, ticker);

                    actionTickerList.add(formattedWebsocketTicker);

                    if (streamAction == StreamAction.UNSUBSCRIBE) {
                        LOGGER.info(("Unsubscribing from " + formattedWebsocketTicker));
                    } else if (streamAction == StreamAction.SUBSCRIBE) {
                        LOGGER.info(("Subscribing to " + formattedWebsocketTicker));
                    }
                }
            }
        }

        if (actionTickerList.isEmpty()) {
            LOGGER.info("Did not change any channel listeners");
        } else {
            StringJoiner commaActionTickers = new StringJoiner(",");
            actionTickerList.forEach(commaActionTickers::add);

            JsonObject actionJsonObject = new JsonObject();
            actionJsonObject.addProperty("action", streamAction.getAPIName());
            actionJsonObject.addProperty("params", commaActionTickers.toString());

            clientEndPoint.sendMessage(actionJsonObject.toString());

            LOGGER.info(("Subscriptions updated to " + getRegisteredTickerChannels(null)));
        }
    }

    /**
     * Format websocket ticker string. (e.g. "T.MSFT")
     *
     * @param channelType the channel type
     * @param ticker      the ticker
     *
     * @return the string
     */
    private String formatWebsocketTicker(ChannelType channelType, String ticker) {
        return channelType.getAPIName() + "." + ticker;
    }

    /**
     * Gets registered ticker channels.
     *
     * @param exclude listener to exclude from the final map
     *
     * @return the registered ticker channels
     */
    public synchronized Map<String, Set<ChannelType>> getRegisteredTickerChannels(PolygonStreamListener exclude) {
        HashMap<String, Set<ChannelType>> registeredTickerChannels = new HashMap<>();

        for (PolygonStreamListener streamListener : listeners) {
            if (streamListener.equals(exclude)) {
                continue;
            }

            Map<String, Set<ChannelType>> stockChannelTypes = streamListener.getStockChannelTypes();

            for (String ticker : stockChannelTypes.keySet()) {
                Set<ChannelType> streamListenerChannelTypes = stockChannelTypes.get(ticker);

                if (!registeredTickerChannels.containsKey(ticker)) {
                    registeredTickerChannels.put(ticker, streamListenerChannelTypes == null ?
                            new HashSet<>() : streamListenerChannelTypes);
                } else {
                    registeredTickerChannels.get(ticker).addAll(streamListenerChannelTypes);
                }
            }
        }

        return registeredTickerChannels;
    }

    /**
     * The enum Stream action.
     */
    private enum StreamAction {
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
        StreamAction(String apiName) {
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
