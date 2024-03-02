package net.jacobpeterson.alpaca.websocket.streaming;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.jacobpeterson.alpaca.model.endpoint.streaming.StreamingMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.authorization.AuthorizationData;
import net.jacobpeterson.alpaca.model.endpoint.streaming.authorization.AuthorizationMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.enums.StreamingMessageType;
import net.jacobpeterson.alpaca.model.endpoint.streaming.listening.ListeningMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.trade.TradeUpdateMessage;
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocket;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.not;
import static net.jacobpeterson.alpaca.util.gson.GsonUtil.GSON;

/**
 * {@link StreamingWebsocket} is an {@link AlpacaWebsocket} implementation and provides the
 * {@link StreamingWebsocketInterface} interface for
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
     * @param endpointAPIType the {@link EndpointAPIType}
     *
     * @return a {@link HttpUrl}
     */
    @SuppressWarnings("UnnecessaryDefault")
    private static HttpUrl createWebsocketURL(EndpointAPIType endpointAPIType) {
        return new HttpUrl.Builder()
                .scheme("https") // HttpUrl.Builder doesn't recognize "wss" scheme, but "https" works fine
                .host((switch (endpointAPIType) {
                    case LIVE -> "api";
                    case PAPER -> "paper-api";
                    default -> throw new UnsupportedOperationException();
                }) + ".alpaca.markets")
                .addPathSegment("stream")
                .build();
    }

    private final Set<StreamingMessageType> listenedStreamMessageTypes;

    /**
     * Instantiates a new {@link StreamingWebsocket}.
     *
     * @param okHttpClient    the {@link OkHttpClient}
     * @param endpointAPIType the {@link EndpointAPIType}
     * @param keyID           the key ID
     * @param secretKey       the secret key
     * @param oAuthToken      the OAuth token
     */
    public StreamingWebsocket(OkHttpClient okHttpClient, EndpointAPIType endpointAPIType,
            String keyID, String secretKey, String oAuthToken) {
        super(okHttpClient, createWebsocketURL(endpointAPIType), "Streaming", keyID, secretKey, oAuthToken);

        listenedStreamMessageTypes = new HashSet<>();
    }

    @Override
    protected void cleanupState() {
        super.cleanupState();

        listenedStreamMessageTypes.clear();
    }

    @Override
    protected void onConnection() {
        sendAuthenticationMessage();
    }

    @Override
    protected void onReconnection() {
        sendAuthenticationMessage();
        if (waitForAuthorization(5, TimeUnit.SECONDS)) {
            streams(Iterables.toArray(listenedStreamMessageTypes, StreamingMessageType.class));
        }
    }

    @Override
    protected void sendAuthenticationMessage() {
        // Ensures that 'authenticationMessageFuture' exists
        getAuthorizationFuture();

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
        final String message = byteString.utf8();
        LOGGER.trace("{}", message);

        JsonElement messageElement = JsonParser.parseString(message);
        checkState(messageElement instanceof JsonObject, "Message must be a JsonObject! Received: %s", messageElement);

        JsonObject messageObject = messageElement.getAsJsonObject();
        JsonElement streamElement = messageObject.get(STREAM_ELEMENT_KEY);
        checkState(streamElement instanceof JsonPrimitive,
                "Message must contain %s element! Received: %s", STREAM_ELEMENT_KEY, messageElement);

        StreamingMessageType streamingMessageType = GSON.fromJson(streamElement, StreamingMessageType.class);
        checkNotNull(streamingMessageType, "StreamingMessageType not found in message: %s", messageObject);

        StreamingMessage streamingMessage;
        switch (streamingMessageType) {
            case AUTHORIZATION:
                streamingMessage = GSON.fromJson(messageObject, AuthorizationMessage.class);

                authenticated = isAuthorizationMessageSuccess((AuthorizationMessage) streamingMessage);

                if (!authenticated) {
                    LOGGER.error("{} websocket not authenticated! Received: {}.", websocketName, streamingMessage);
                } else {
                    LOGGER.info("{} websocket authenticated.", websocketName);
                }

                if (authenticationMessageFuture != null) {
                    authenticationMessageFuture.complete(authenticated);
                }
                break;
            case LISTENING:
                streamingMessage = GSON.fromJson(messageObject, ListeningMessage.class);

                // Remove all 'StreamingMessageType's that are no longer listened to and add new ones
                List<StreamingMessageType> currentTypes = ((ListeningMessage) streamingMessage).getData().getStreams();
                currentTypes.stream()
                        .filter(not(listenedStreamMessageTypes::contains))
                        .forEach(listenedStreamMessageTypes::remove);
                listenedStreamMessageTypes.addAll(currentTypes);
                break;
            case TRADE_UPDATES:
                streamingMessage = GSON.fromJson(messageObject, TradeUpdateMessage.class);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        if (listenedStreamMessageTypes.contains(streamingMessageType)) {
            callListener(streamingMessageType, streamingMessage);
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
    public void streams(StreamingMessageType... streamingMessageTypes) {
        if (streamingMessageTypes == null || streamingMessageTypes.length == 0) {
            return;
        }

        // Add all non-subscribable 'streamingMessageTypes' before connecting or sending websocket message
        Arrays.stream(streamingMessageTypes)
                .filter(not(SUBSCRIBABLE_STREAMING_MESSAGE_TYPES::contains))
                .forEach(listenedStreamMessageTypes::add);

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
        } else if (!isConnected()) {
            throw new IllegalStateException("This websocket must be connected before subscribing to streams!");
        }

        JsonObject dataObject = new JsonObject();
        dataObject.add("streams", streamsArray);

        requestObject.add("data", dataObject);

        websocket.send(requestObject.toString());
        LOGGER.info("Requested streams: {}.", streamsArray);
    }

    @Override
    public Collection<StreamingMessageType> streams() {
        return new HashSet<>(listenedStreamMessageTypes);
    }
}
