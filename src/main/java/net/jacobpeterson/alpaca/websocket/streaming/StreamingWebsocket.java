package net.jacobpeterson.alpaca.websocket.streaming;

import com.google.common.collect.Iterables;
import com.google.gson.*;
import net.jacobpeterson.alpaca.model.endpoint.streaming.StreamingMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.authorization.AuthorizationData;
import net.jacobpeterson.alpaca.model.endpoint.streaming.authorization.AuthorizationMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.enums.StreamingMessageType;
import net.jacobpeterson.alpaca.model.endpoint.streaming.listening.ListeningMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.trade.TradeUpdateMessage;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocket;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static net.jacobpeterson.alpaca.util.gson.GsonUtil.GSON;

/**
 * {@link StreamingWebsocket} is an {@link AlpacaWebsocket} implementation and provides the {@link
 * StreamingWebsocketInterface} interface for
 * <a href="https://alpaca.markets/docs/api-documentation/api-v2/streaming/">Streaming</a>
 */
public class StreamingWebsocket extends AlpacaWebsocket<StreamingMessageType, StreamingMessage, StreamingListener>
        implements StreamingWebsocketInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingWebsocket.class);
    private static final String STREAM_ELEMENT_KEY = "stream";
    private static final List<StreamingMessageType> SUBSCRIBABLE_STREAMING_MESSAGE_TYPES = Collections.singletonList(
            StreamingMessageType.TRADE_UPDATES);

    /**
     * Creates a {@link HttpUrl} for {@link StreamingWebsocket} with the given <code>alpacaSubdomain</code>.
     *
     * @param alpacaSubdomain the Alpaca subdomain
     *
     * @return a {@link HttpUrl}
     */
    private static HttpUrl createWebsocketURL(String alpacaSubdomain) {
        return new HttpUrl.Builder()
                .scheme("https") // HttpUrl.Builder doesn't recognize "wss" scheme, so "https" works fine
                .host(alpacaSubdomain + ".alpaca.markets")
                .addPathSegment("stream")
                .build();
    }

    private final List<StreamingMessageType> listenedStreamMessageTypes;

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

        listenedStreamMessageTypes = new ArrayList<>();
    }

    @Override
    protected void onConnection() {
        sendAuthenticationMessage();
    }

    @Override
    protected void onReconnection() {
        sendAuthenticationMessage();
        if (waitForAuthorization()) {
            subscriptions(Iterables.toArray(listenedStreamMessageTypes, StreamingMessageType.class));
        }
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

                authenticated = isAuthorizationMessageSuccess(authorizationMessage);

                if (authenticationMessageFuture != null) {
                    authenticationMessageFuture.complete(authenticated);
                }

                if (!authenticated) {
                    LOGGER.error("{} websocket not authenticated! Received: {}.", websocketName, authorizationMessage);
                } else {
                    LOGGER.info("{} websocket authenticated.", websocketName);
                    LOGGER.debug("{}", authorizationMessage);
                }

                callListeners(streamingMessageType, authorizationMessage);
                break;
            case LISTENING:
                ListeningMessage listeningMessage = GSON.fromJson(messageObject, ListeningMessage.class);
                LOGGER.debug("{}", listeningMessage);

                listenedStreamMessageTypes.clear();
                listenedStreamMessageTypes.addAll(listeningMessage.getData().getStreams());

                callListeners(streamingMessageType, listeningMessage);
                break;
            case TRADE_UPDATES:
                TradeUpdateMessage tradeUpdateMessage = GSON.fromJson(messageObject, TradeUpdateMessage.class);
                LOGGER.debug("{}", tradeUpdateMessage);
                callListeners(streamingMessageType, tradeUpdateMessage);
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

    @Override
    public void subscriptions(StreamingMessageType... streamingMessageTypes) {
        checkState(isConnected(), "Websocket must be connected before requesting subscriptions!");
        checkNotNull(streamingMessageTypes);

        if (streamingMessageTypes.length == 0) {
            return;
        }

        // Stream request format:
        // {
        //     "action": "listen",
        //     "data": {
        //         "streams": ["trade_updates"]
        //     }
        // }

        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("action", "listen");

        JsonArray streamsArray = new JsonArray();
        Arrays.stream(streamingMessageTypes)
                .filter(SUBSCRIBABLE_STREAMING_MESSAGE_TYPES::contains)
                .forEach((type) -> streamsArray.add(type.toString()));

        if (streamsArray.isEmpty()) {
            return;
        }

        JsonObject dataObject = new JsonObject();
        dataObject.add("streams", streamsArray);

        requestObject.add("data", dataObject);

        websocket.send(requestObject.toString());
        LOGGER.info("Requested subscriptions: {}.", streamsArray);
    }
}
