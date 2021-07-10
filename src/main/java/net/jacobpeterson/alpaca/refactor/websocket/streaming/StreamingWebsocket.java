package net.jacobpeterson.alpaca.refactor.websocket.streaming;

import com.google.gson.*;
import net.jacobpeterson.alpaca.model.endpoint.streaming.StreamingMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.authorization.AuthorizationData;
import net.jacobpeterson.alpaca.model.endpoint.streaming.authorization.AuthorizationMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.enums.StreamingMessageType;
import net.jacobpeterson.alpaca.model.endpoint.streaming.listening.ListeningMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.trade.TradeUpdateMessage;
import net.jacobpeterson.alpaca.refactor.websocket.AlpacaWebsocket;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static net.jacobpeterson.alpaca.refactor.util.gson.GsonUtil.GSON;

public class StreamingWebsocket extends AlpacaWebsocket implements StreamingWebsocketInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingWebsocket.class);
    private static final String STREAM_ELEMENT_KEY = "stream";

    /**
     * Creates a {@link HttpUrl} for {@link StreamingWebsocket} for the given <code>alpacaSubdomain</code>.
     *
     * @param alpacaSubdomain the Alpaca subdomain
     *
     * @return a {@link HttpUrl}
     */
    private static HttpUrl createWebsocketURL(String alpacaSubdomain) {
        return new HttpUrl.Builder()
                .scheme("wss")
                .host(alpacaSubdomain + ".alpaca.markets")
                .addPathSegment("stream")
                .build();
    }

    private final List<StreamingListener> listeners;
    private final Object listenersLock;
    private final List<StreamingListener> listenersToAdd;
    private final List<StreamingListener> listenersToRemove;

    /**
     * Instantiates a new {@link StreamingWebsocket}.
     *
     * @param okHttpClient    the {@link OkHttpClient}
     * @param alpacaSubdomain the Alpaca subdomain
     * @param keyID           the key ID
     * @param secretKey       the secret key
     * @param oAuthToken      the OAuth token
     */
    public StreamingWebsocket(OkHttpClient okHttpClient, String alpacaSubdomain,
            String keyID, String secretKey, String oAuthToken) {
        super(okHttpClient, createWebsocketURL(alpacaSubdomain), "Streaming", keyID, secretKey, oAuthToken);

        listeners = new ArrayList<>();
        listenersLock = new Object();
        listenersToAdd = new ArrayList<>();
        listenersToRemove = new ArrayList<>();
    }

    @Override
    protected void onConnection() {
        sendAuthenticationMessage();
    }

    @Override
    protected void onReconnection() {
        sendAuthenticationMessage();
    }

    @Override
    protected void sendAuthenticationMessage() {
        /* Authentication message format:
         *  {
         *      "action": "authenticate",
         *      "data": {
         *          "key_id": "{YOUR_API_KEY_ID}",
         *          "secret_key": "{YOUR_API_SECRET_KEY}"
         *      }
         *  }
         */

        JsonObject authObject = new JsonObject();
        authObject.addProperty("action", "authenticate");

        JsonObject authData = new JsonObject();

        if (useOAuth) {
            authData.addProperty("oauth_token", oAuthToken);
        } else {
            authData.addProperty("key_id", keyID);
            authData.addProperty("secret_key", secretKey);
        }

        authObject.add("data", authData);

        LOGGER.info("{} websocket sending authentication message...", websocketName);
        websocket.send(authObject.toString());
    }

    // This websocket uses binary frames and not text frames.
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString byteString) {
        String message = byteString.utf8();

        JsonElement messageElement = JsonParser.parseString(message);
        checkState(messageElement instanceof JsonObject, "Message must be a JsonObject! Received: %s", messageElement);

        JsonObject messageObject = messageElement.getAsJsonObject();
        JsonElement streamElement = messageObject.get(STREAM_ELEMENT_KEY);
        checkState(streamElement instanceof JsonPrimitive,
                "Message must contain %s element! Received: %s", STREAM_ELEMENT_KEY, messageElement);

        StreamingMessageType streamingMessageType = GSON.fromJson(streamElement, StreamingMessageType.class);
        switch (streamingMessageType) {
            case AUTHORIZATION:
                AuthorizationMessage authorizationMessage = GSON.fromJson(messageObject, AuthorizationMessage.class);
                callListeners(streamingMessageType, authorizationMessage);

                authenticated = isAuthorizationMessageSuccess(authorizationMessage);
                if (!authenticated) {
                    LOGGER.error("{} websocket not authenticated! Received: {}. Disconnecting...",
                            websocketName, authorizationMessage);
                    disconnect();
                } else {
                    LOGGER.info("{} websocket authenticated.", websocketName);
                }
                break;
            case LISTENING:
                ListeningMessage listeningMessage = GSON.fromJson(messageObject, ListeningMessage.class);
                callListeners(streamingMessageType, listeningMessage);
                LOGGER.debug("{}", listeningMessage);
                break;
            case TRADE_UPDATES:
                TradeUpdateMessage tradeUpdateMessage = GSON.fromJson(messageObject, TradeUpdateMessage.class);
                callListeners(streamingMessageType, tradeUpdateMessage);
                LOGGER.debug("{}", tradeUpdateMessage);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns true if {@link AuthorizationMessage} states success.
     *
     * @param authorizationMessage the {@link AuthorizationMessage}
     *
     * @return a boolean
     */
    private boolean isAuthorizationMessageSuccess(AuthorizationMessage authorizationMessage) {
        AuthorizationData authorizationData = authorizationMessage.getData();
        return authorizationData.getStatus().equalsIgnoreCase("authorized") &&
                authorizationData.getAction().equalsIgnoreCase("authenticate");
    }

    /**
     * Calls the {@link StreamingListener}s in {@link #listeners}.
     *
     * @param streamingMessageType the {@link StreamingMessageType}
     * @param streamingMessage     the {@link StreamingMessage}
     */
    private void callListeners(StreamingMessageType streamingMessageType, StreamingMessage streamingMessage) {
        // Remove all 'StreamingListener's that were request to be removed then add all 'StreamingListener's
        // that were request to be added on the last call to the listeners.
        synchronized (listenersLock) {
            listeners.removeAll(listenersToRemove);
            listenersToRemove.clear();

            listeners.addAll(listenersToAdd);
            listenersToAdd.clear();
        }

        // Note that this doesn't need to acquire 'listenersLock' since 'listeners' is isolated
        // to this instance and is not modified outside a lock.
        for (StreamingListener streamingListener : listeners) {
            streamingListener.onMessage(streamingMessageType, streamingMessage);
        }
    }

    @Override
    public void subscriptions(StreamingMessageType... streamingMessageTypes) {
        checkNotNull(streamingMessageTypes);
        checkArgument(streamingMessageTypes.length > 0, "'streamingMessageTypes' must be have at least one element.");

        // Stream request format:
        // {
        //     "action": "listen",
        //     "data": {
        //         "streams": ["account_updates", "trade_updates"]
        //     }
        // }

        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("action", "listen");

        JsonArray streamsArray = new JsonArray();
        for (StreamingMessageType streamingMessageType : streamingMessageTypes) {
            streamsArray.add(streamingMessageType.toString());
        }

        JsonObject dataObject = new JsonObject();
        dataObject.add("streams", streamsArray);

        requestObject.add("data", dataObject);

        websocket.send(requestObject.toString());
        LOGGER.info("Requested subscriptions: {}.", streamsArray);
    }

    @Override
    public void addListener(StreamingListener streamingListener) {
        synchronized (listenersLock) {
            listenersToAdd.add(streamingListener);
        }
    }

    @Override
    public void removeListener(StreamingListener streamingListener) {
        synchronized (listenersLock) {
            listenersToRemove.add(streamingListener);
        }
    }
}
