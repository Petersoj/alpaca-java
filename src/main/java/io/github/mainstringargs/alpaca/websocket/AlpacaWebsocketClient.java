package io.github.mainstringargs.alpaca.websocket;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.websocket.AlpacaWebsocketClientEndpoint.MessageHandler;
import io.github.mainstringargs.alpaca.websocket.message.AccountUpdateMessage;
import io.github.mainstringargs.alpaca.websocket.message.OrderUpdateMessage;
import io.github.mainstringargs.alpaca.websocket.message.UpdateMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Class WebsocketClient.
 */
public class AlpacaWebsocketClient implements MessageHandler {

    /** The logger. */
    private static Logger LOGGER = LogManager.getLogger(AlpacaWebsocketClient.class);

    /** The base API URL. */
    private String baseAPIURL;

    /** The key id. */
    private String keyId;

    /** The secret. */
    private String secret;

    /** The observers. */
    private List<AlpacaStreamListener> listeners = new ArrayList<>();

    /** The client end point. */
    private AlpacaWebsocketClientEndpoint clientEndPoint = null;

    /** The authorized object. */
    private JsonObject authorizedObject = new JsonObject();

    {
        authorizedObject.addProperty("stream", "authorization");
        JsonObject dataObject = new JsonObject();
        dataObject.addProperty("status", "authorized");
        dataObject.addProperty("action", "authenticate");
        authorizedObject.add("data", dataObject);
    }

    /**
     * Instantiates a new websocket client.
     *
     * @param keyId      the key id
     * @param secret     the secret
     * @param baseAPIURL the base api url
     */
    public AlpacaWebsocketClient(String keyId, String secret, String baseAPIURL) {
        this.keyId = keyId;
        this.secret = secret;
        this.baseAPIURL = baseAPIURL.replace("https", "wss") + "/stream";
    }

    /**
     * Adds the listener.
     *
     * @param listener the listener
     */
    public synchronized void addListener(AlpacaStreamListener listener) {
        if (listeners.isEmpty()) {
            connect();
        }
        listeners.add(listener);

        submitStreamRequest();
    }

    /**
     * Removes the listener.
     *
     * @param listener the listener
     */
    public synchronized void removeListener(AlpacaStreamListener listener) {
        if (listener != null) {
            listeners.remove(listener);

            submitStreamRequest();
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
            clientEndPoint = new AlpacaWebsocketClientEndpoint(new URI(baseAPIURL));
            clientEndPoint.addMessageHandler(this);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        LOGGER.info("Connected.");

        /**
         * Format of message is:
         *
         * { "action": "authenticate", "data": { "key_id": "{YOUR_API_KEY_ID}", "secret_key":
         * "{YOUR_API_SECRET_KEY}" } }
         */

        JsonObject authRequest = new JsonObject();
        authRequest.addProperty("action", "authenticate");
        JsonObject payload = new JsonObject();
        payload.addProperty("key_id", keyId);
        payload.addProperty("secret_key", secret);
        authRequest.add("data", payload);

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

    /*
     * (non-Javadoc)
     *
     * @see
     * io.github.mainstringargs.alpaca.websocket.WebsocketClientEndpoint.MessageHandler#handleMessage(
     * com.google.gson.JsonObject)
     */
    @Override
    public void handleMessage(JsonObject message) {
        if (message.has("stream")) {
            String streamType = message.get("stream").getAsString();

            switch (streamType) {
                case "authorization":
                    if (authorizedObject.equals(message)) {
                        LOGGER.debug("Authorized by Alpaca " + message);
                    }
                    break;
                case "listening":
                    LOGGER.debug("Listening response " + message);
                    break;
                case "trade_updates":
                    sendStreamMessageToObservers(MessageType.ORDER_UPDATES, message);
                    break;
                case "account_updates":
                    sendStreamMessageToObservers(MessageType.ACCOUNT_UPDATES, message);
                    break;
            }
        } else {
            LOGGER.error("Invalid message received " + message);
        }
    }

    /**
     * Send stream message to observers.
     *
     * @param messageType the message type
     * @param message     the message
     */
    private synchronized void sendStreamMessageToObservers(MessageType messageType,
            JsonObject message) {
        for (AlpacaStreamListener observer : listeners) {
            UpdateMessage messageObject = getMessageToObject(messageType, message);

            if (observer.getMessageTypes() == null || observer.getMessageTypes().isEmpty()
                    || observer.getMessageTypes().contains(messageType)) {
                observer.streamUpdate(messageType, messageObject);
            }
        }
    }

    /**
     * Gets the message to object.
     *
     * @param messageType the message type
     * @param message     the message
     *
     * @return the message to object
     */
    private UpdateMessage getMessageToObject(MessageType messageType, JsonObject message) {
        if (message.has("data")) {
            JsonObject data = message.getAsJsonObject("data");

            switch (messageType) {
                case ACCOUNT_UPDATES:
                    return new AccountUpdateMessage(data);
                case ORDER_UPDATES:
                    return new OrderUpdateMessage(data);
            }
        }
        return null;
    }

    /**
     * Submit stream request.
     */
    private void submitStreamRequest() {
        // Stream request example:
        // {
        //     "action": "listen",
        //     "data": {
        //         "streams": ["account_updates", "trade_updates"]
        //     }
        // }

        JsonObject streamRequest = new JsonObject();

        JsonArray streamsArray = new JsonArray();

        for (MessageType mType : getRegisteredMessageTypes()) {
            JsonPrimitive apiNameJson = new JsonPrimitive(mType.getAPIName());

            if (!streamsArray.contains(apiNameJson)) {
                streamsArray.add(mType.getAPIName());
            }
        }

        streamRequest.addProperty("action", "listen");

        JsonObject dataObject = new JsonObject();
        dataObject.add("streams", streamsArray);

        streamRequest.add("data", dataObject);

        clientEndPoint.sendMessage(streamRequest.toString());

        LOGGER.info("Updating streams to " + streamsArray);
    }

    /**
     * Gets the registered message types.
     *
     * @return the registered message types
     */
    public synchronized Set<MessageType> getRegisteredMessageTypes() {
        Set<MessageType> registeredMessageTypes = new HashSet<>();

        for (AlpacaStreamListener observer : listeners) {
            // if its empty, assume they want everything
            if (observer.getMessageTypes() == null || observer.getMessageTypes().isEmpty()) {
                registeredMessageTypes.addAll(Arrays.asList(MessageType.values()));
                break;
            }
            registeredMessageTypes.addAll(observer.getMessageTypes());
        }

        return registeredMessageTypes;
    }
}
