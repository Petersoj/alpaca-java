package net.jacobpeterson.alpaca.websocket.broker.client;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.jacobpeterson.abstracts.websocket.client.WebsocketClient;
import net.jacobpeterson.abstracts.websocket.exception.WebsocketException;
import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.abstracts.websocket.message.StreamMessage;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;
import net.jacobpeterson.alpaca.websocket.broker.listener.AlpacaStreamListener;
import net.jacobpeterson.alpaca.websocket.broker.message.AlpacaStreamMessageType;
import net.jacobpeterson.domain.alpaca.streaming.AlpacaStreamMessage;
import net.jacobpeterson.domain.alpaca.streaming.account.AccountUpdateMessage;
import net.jacobpeterson.domain.alpaca.streaming.authorization.AuthorizationMessage;
import net.jacobpeterson.domain.alpaca.streaming.listening.ListeningMessage;
import net.jacobpeterson.domain.alpaca.streaming.trade.TradeUpdateMessage;
import net.jacobpeterson.util.gson.GsonUtil;
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
public class AlpacaWebsocketClient implements WebsocketClient {

    /** The logger. */
    private static final Logger LOGGER = LogManager.getLogger(AlpacaWebsocketClient.class);

    /** The constant STREAM_KEY. */
    private static final String STREAM_KEY = "stream";

    /** The key id. */
    private final String keyId;

    /** The secret. */
    private final String secret;

    /** The Base api url. */
    private final String streamAPIURL;

    /** The observers. */
    private final List<AlpacaStreamListener> listeners;

    /** The client end point. */
    private AlpacaWebsocketClientEndpoint alpacaWebsocketClientEndpoint;

    /** The Authenticated. */
    private boolean authenticated;

    /**
     * Instantiates a new Alpaca websocket client.
     *
     * @param keyId      the key id
     * @param secret     the secret
     * @param baseAPIURL the base API URL
     */
    public AlpacaWebsocketClient(String keyId, String secret, String baseAPIURL) {
        this.keyId = keyId;
        this.secret = secret;
        this.streamAPIURL = baseAPIURL.replace("https", "wss") + "/stream";

        this.listeners = new ArrayList<>();
    }

    @Override
    public void addListener(StreamListener<?, ?> streamListener) throws WebsocketException {
        Preconditions.checkState(streamListener instanceof AlpacaStreamListener);

        if (listeners.isEmpty()) {
            try {
                connect();
            } catch (IOException | URISyntaxException | DeploymentException exception) {
                throw new WebsocketException(exception);
            }
        }

        listeners.add((AlpacaStreamListener) streamListener);

        submitStreamRequestUpdate();
    }

    @Override
    public void removeListener(StreamListener<?, ?> streamListener) throws WebsocketException {
        Preconditions.checkState(streamListener instanceof AlpacaStreamListener);

        listeners.remove(streamListener);

        if (listeners.isEmpty()) {
            try {
                disconnect();
            } catch (Exception exception) {
                throw new WebsocketException(exception);
            }
        } else {
            submitStreamRequestUpdate();
        }
    }

    @Override
    public void connect() throws URISyntaxException, IOException, DeploymentException {
        LOGGER.info("Connecting...");

        alpacaWebsocketClientEndpoint = new AlpacaWebsocketClientEndpoint(this, new URI(streamAPIURL));
        alpacaWebsocketClientEndpoint.setAutomaticallyReconnect(true);
        alpacaWebsocketClientEndpoint.connect();

        LOGGER.info("Connected.");
    }

    @Override
    public void disconnect() throws Exception {
        LOGGER.info("Disconnecting...");

        alpacaWebsocketClientEndpoint.disconnect();

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

        JsonObject payload = new JsonObject();
        payload.addProperty("key_id", keyId);
        payload.addProperty("secret_key", secret);

        authRequest.add("data", payload);

        alpacaWebsocketClientEndpoint.sendMessage(authRequest.toString());
    }

    @Override
    public void handleResubscribing() {
        submitStreamRequestUpdate();
    }

    @Override
    public void handleWebsocketMessage(String message) {
        JsonElement messageJsonElement = GsonUtil.JSON_PARSER.parse(message);

        Preconditions.checkState(messageJsonElement instanceof JsonObject);

        JsonObject messageJsonObject = messageJsonElement.getAsJsonObject();
        JsonElement streamJsonElement = messageJsonObject.get(STREAM_KEY);

        if (streamJsonElement instanceof JsonPrimitive) {
            try {
                AlpacaStreamMessageType alpacaStreamMessageType = GsonUtil.GSON.fromJson(streamJsonElement,
                        AlpacaStreamMessageType.class);

                switch (alpacaStreamMessageType) {
                    case AUTHORIZATION:
                        AuthorizationMessage authorizationMessage = GsonUtil.GSON.fromJson(messageJsonObject,
                                AuthorizationMessage.class);
                        sendStreamMessageToListeners(alpacaStreamMessageType, authorizationMessage);

                        authenticated = isAuthorizationMessageSuccess(authorizationMessage);

                        LOGGER.debug(authorizationMessage);
                        break;
                    case LISTENING:
                        ListeningMessage listeningMessage = GsonUtil.GSON.fromJson(messageJsonObject,
                                ListeningMessage.class);
                        sendStreamMessageToListeners(alpacaStreamMessageType, listeningMessage);

                        LOGGER.debug(listeningMessage);
                        break;
                    case TRADE_UPDATES:
                        sendStreamMessageToListeners(alpacaStreamMessageType, GsonUtil.GSON.fromJson(messageJsonObject,
                                TradeUpdateMessage.class));
                        break;
                    case ACCOUNT_UPDATES:
                        sendStreamMessageToListeners(alpacaStreamMessageType, GsonUtil.GSON.fromJson(messageJsonObject,
                                AccountUpdateMessage.class));
                        break;
                    default:
                        LOGGER.error("Unhandled stream type: " + alpacaStreamMessageType);
                }
            } catch (JsonSyntaxException exception) {
                LOGGER.error("Could not parse message: " + messageJsonObject, exception);
            }
        } else {
            LOGGER.error("Unknown stream message: " + messageJsonObject);
        }
    }

    @Override
    public void sendStreamMessageToListeners(StreamMessageType streamMessageType, StreamMessage streamMessage) {
        Preconditions.checkState(streamMessageType instanceof AlpacaStreamMessageType);
        Preconditions.checkState(streamMessage instanceof AlpacaStreamMessage);

        AlpacaStreamMessageType alpacaStreamMessageType = (AlpacaStreamMessageType) streamMessageType;
        AlpacaStreamMessage alpacaStreamMessage = (AlpacaStreamMessage) streamMessage;

        for (AlpacaStreamListener alpacaStreamListener : new ArrayList<>(listeners)) {
            if (alpacaStreamListener.getStreamMessageTypes() == null ||
                    alpacaStreamListener.getStreamMessageTypes().isEmpty() ||
                    alpacaStreamListener.getStreamMessageTypes().contains(alpacaStreamMessageType)) {
                alpacaStreamListener.onStreamUpdate(alpacaStreamMessageType, alpacaStreamMessage);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return alpacaWebsocketClientEndpoint.getUserSession().isOpen();
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
    private void submitStreamRequestUpdate() {
        // Stream request example:
        // {
        //     "action": "listen",
        //     "data": {
        //         "streams": ["account_updates", "trade_updates"]
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

        alpacaWebsocketClientEndpoint.sendMessage(streamRequestJsonObject.toString());

        LOGGER.info("Requested subscriptions to update to " + streamsJsonArray);
    }

    /**
     * Gets the registered message types.
     *
     * @return the registered message types
     */
    public Set<AlpacaStreamMessageType> getRegisteredMessageTypes() {
        Set<AlpacaStreamMessageType> registeredStreamMessageTypes = new HashSet<>();

        for (AlpacaStreamListener alpacaStreamListener : new ArrayList<>(listeners)) {
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
