package net.jacobpeterson.alpaca.websocket.broker.client;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import net.jacobpeterson.abstracts.websocket.client.WebsocketClient;
import net.jacobpeterson.abstracts.websocket.exception.WebsocketException;
import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.abstracts.websocket.message.StreamMessage;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;
import net.jacobpeterson.alpaca.enums.api.EndpointAPIType;
import net.jacobpeterson.alpaca.websocket.broker.listener.AlpacaStreamListener;
import net.jacobpeterson.alpaca.websocket.broker.message.AlpacaStreamMessageType;
import net.jacobpeterson.domain.alpaca.streaming.AlpacaStreamMessage;
import net.jacobpeterson.domain.alpaca.streaming.account.AccountUpdateMessage;
import net.jacobpeterson.domain.alpaca.streaming.authorization.AuthorizationMessage;
import net.jacobpeterson.domain.alpaca.streaming.listening.ListeningMessage;
import net.jacobpeterson.domain.alpaca.streaming.trade.TradeUpdateMessage;
import net.jacobpeterson.util.gson.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * {@link AlpacaWebsocketClient} represents a client for the {@link net.jacobpeterson.alpaca.AlpacaAPI} Websocket
 * stream.
 */
public class AlpacaWebsocketClient implements WebsocketClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaWebsocketClient.class);
    private static final String STREAM_KEY = "stream";

    private final String keyID;
    private final String secret;
    private final String oAuthToken;
    private final String streamAPIURL;
    private final List<AlpacaStreamListener> listeners;

    private AlpacaWebsocketClientEndpoint alpacaWebsocketClientEndpoint;
    private boolean authenticated;

    /**
     * Instantiates a new {@link AlpacaWebsocketClient}.
     *
     * @param keyID           the key ID
     * @param secret          the secret
     * @param endpointAPIType the {@link EndpointAPIType}
     */
    public AlpacaWebsocketClient(String keyID, String secret, EndpointAPIType endpointAPIType) {
        this(keyID, secret, null, endpointAPIType);
    }

    /**
     * Instantiates a new {@link AlpacaWebsocketClient}.
     *
     * @param oAuthToken      the OAuth token
     * @param endpointAPIType the {@link EndpointAPIType}
     */
    public AlpacaWebsocketClient(String oAuthToken, EndpointAPIType endpointAPIType) {
        this(null, null, oAuthToken, endpointAPIType);
    }

    /**
     * Instantiates a new {@link AlpacaWebsocketClient}.
     *
     * @param keyID      the key ID
     * @param secret     the secret
     * @param oAuthToken the OAuth token
     */
    private AlpacaWebsocketClient(String keyID, String secret, String oAuthToken, EndpointAPIType endpointAPIType) {
        this.keyID = keyID;
        this.secret = secret;
        this.oAuthToken = oAuthToken;
        this.streamAPIURL = endpointAPIType.getURL().replace("https", "wss") + "/stream";

        listeners = new ArrayList<>();
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

        if (oAuthToken != null) {
            payload.addProperty("oauth_token", oAuthToken);
        } else {
            payload.addProperty("key_id", keyID);
            payload.addProperty("secret_key", secret);
        }

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

                        LOGGER.debug("{}", authorizationMessage);
                        break;
                    case LISTENING:
                        ListeningMessage listeningMessage = GsonUtil.GSON.fromJson(messageJsonObject,
                                ListeningMessage.class);
                        sendStreamMessageToListeners(alpacaStreamMessageType, listeningMessage);

                        LOGGER.debug("{}", listeningMessage);
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
                        LOGGER.error("Unhandled stream type: {}", alpacaStreamMessageType);
                }
            } catch (JsonSyntaxException exception) {
                LOGGER.error("Could not parse message: {}\n{}", messageJsonObject, exception);
            }
        } else {
            LOGGER.error("Unknown stream message: {}", messageJsonObject);
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

        LOGGER.info("Requested subscriptions to update to {}", streamsJsonArray);
    }

    /**
     * Gets the registered {@link AlpacaStreamMessageType}.
     *
     * @return the registered {@link AlpacaStreamMessageType}
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
