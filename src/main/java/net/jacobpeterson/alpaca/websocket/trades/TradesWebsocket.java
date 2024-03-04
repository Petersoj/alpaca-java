package net.jacobpeterson.alpaca.websocket.trades;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.jacobpeterson.alpaca.model.util.apitype.TraderAPIEndpointType;
import net.jacobpeterson.alpaca.model.websocket.trades.model.TradesStreamMessage;
import net.jacobpeterson.alpaca.model.websocket.trades.model.TradesStreamMessageType;
import net.jacobpeterson.alpaca.model.websocket.trades.model.authorization.AuthorizationData;
import net.jacobpeterson.alpaca.model.websocket.trades.model.authorization.AuthorizationMessage;
import net.jacobpeterson.alpaca.model.websocket.trades.model.listening.ListeningMessage;
import net.jacobpeterson.alpaca.model.websocket.trades.model.tradeupdate.TradeUpdateMessage;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.toArray;
import static com.google.gson.JsonParser.parseString;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jacobpeterson.alpaca.model.websocket.trades.model.TradesStreamMessageType.TRADE_UPDATES;
import static net.jacobpeterson.alpaca.openapi.trader.JSON.getGson;

/**
 * {@link TradesWebsocket} is an {@link AlpacaWebsocket} implementation and provides the
 * {@link TradesWebsocketInterface} interface for
 * <a href="https://docs.alpaca.markets/docs/websocket-streaming">Trades Streaming</a>.
 */
public class TradesWebsocket extends AlpacaWebsocket<TradesStreamMessageType, TradesStreamMessage, TradesListener>
        implements TradesWebsocketInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradesWebsocket.class);
    private static final String STREAM_ELEMENT_KEY = "stream";
    private static final List<TradesStreamMessageType> SUBSCRIBABLE_STREAMING_MESSAGE_TYPES =
            singletonList(TRADE_UPDATES);

    /**
     * Creates a {@link HttpUrl} for {@link TradesWebsocket}.
     *
     * @param traderAPIEndpointType the {@link TraderAPIEndpointType}
     *
     * @return a {@link HttpUrl}
     */
    @SuppressWarnings("UnnecessaryDefault")
    private static HttpUrl createWebsocketURL(TraderAPIEndpointType traderAPIEndpointType) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host((switch (traderAPIEndpointType) {
                    case LIVE -> "api";
                    case PAPER -> "paper-api";
                    default -> throw new UnsupportedOperationException();
                }) + ".alpaca.markets")
                .addPathSegment("stream")
                .build();
    }

    private final Set<TradesStreamMessageType> listenedMessageTypes;

    /**
     * Instantiates a new {@link TradesWebsocket}.
     *
     * @param okHttpClient          the {@link OkHttpClient}
     * @param traderAPIEndpointType the {@link TraderAPIEndpointType}
     * @param keyID                 the key ID
     * @param secretKey             the secret key
     * @param oAuthToken            the OAuth token
     */
    public TradesWebsocket(OkHttpClient okHttpClient, TraderAPIEndpointType traderAPIEndpointType,
            String keyID, String secretKey, String oAuthToken) {
        super(okHttpClient, createWebsocketURL(traderAPIEndpointType), "Trades Stream", keyID, secretKey, oAuthToken);
        listenedMessageTypes = new HashSet<>();
    }

    @Override
    protected void cleanupState() {
        super.cleanupState();
        listenedMessageTypes.clear();
    }

    @Override
    protected void onConnection() {
        sendAuthenticationMessage();
    }

    @Override
    protected void onReconnection() {
        sendAuthenticationMessage();
        if (waitForAuthorization(5, SECONDS)) {
            subscribe(toArray(listenedMessageTypes, TradesStreamMessageType.class));
        }
    }

    @Override
    protected void sendAuthenticationMessage() {
        // Ensure that the authorization Future exists
        getAuthorizationFuture();

        final JsonObject authObject = new JsonObject();
        authObject.addProperty("action", "authenticate");
        final JsonObject authData = new JsonObject();
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

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString byteString) {
        final String messageString = byteString.utf8();
        LOGGER.trace("Websocket message received: {}", messageString);

        // Parse JSON string and identify 'messageType'
        final JsonElement messageElement = parseString(messageString);
        checkState(messageElement instanceof JsonObject, "Message must be a JsonObject! Received: %s", messageElement);
        final JsonObject messageObject = messageElement.getAsJsonObject();
        final JsonElement streamElement = messageObject.get(STREAM_ELEMENT_KEY);
        checkState(streamElement instanceof JsonPrimitive,
                "Message must contain %s element! Received: %s", STREAM_ELEMENT_KEY, messageElement);
        final TradesStreamMessageType messageType = getGson().fromJson(streamElement, TradesStreamMessageType.class);
        checkNotNull(messageType, "TradesStreamMessageType not found in message: %s", messageObject);

        // Deserialize message based on 'messageType'
        TradesStreamMessage message;
        switch (messageType) {
            case AUTHORIZATION:
                message = getGson().fromJson(messageObject, AuthorizationMessage.class);
                final AuthorizationData authorizationData = ((AuthorizationMessage) message).getData();
                authenticated = authorizationData.getStatus().equalsIgnoreCase("authorized") &&
                        authorizationData.getAction().equalsIgnoreCase("authenticate");
                if (!authenticated) {
                    throw new RuntimeException(format("%s websocket not authenticated! Received: %s.",
                            websocketName, message));
                } else {
                    LOGGER.info("{} websocket authenticated.", websocketName);
                }
                if (authenticationMessageFuture != null) {
                    authenticationMessageFuture.complete(authenticated);
                }
                break;
            case LISTENING:
                message = getGson().fromJson(messageObject, ListeningMessage.class);
                // Remove all 'TradeStreamMessageType's that are no longer listened to and add new ones
                final List<TradesStreamMessageType> currentTypes = ((ListeningMessage) message).getData().getStreams();
                currentTypes.stream()
                        .filter(not(listenedMessageTypes::contains))
                        .forEach(listenedMessageTypes::remove);
                listenedMessageTypes.addAll(currentTypes);
                break;
            case TRADE_UPDATES:
                message = getGson().fromJson(messageObject, TradeUpdateMessage.class);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        // Call listener
        if (listenedMessageTypes.contains(messageType)) {
            callListener(messageType, message);
        }
    }

    @Override
    public void subscribe(TradesStreamMessageType... messageTypes) {
        if (messageTypes == null || messageTypes.length == 0) {
            return;
        }

        // Add all non-subscribable 'messageTypes' before connecting or sending websocket message
        Arrays.stream(messageTypes)
                .filter(not(SUBSCRIBABLE_STREAMING_MESSAGE_TYPES::contains))
                .forEach(listenedMessageTypes::add);

        // Create streams subscription JSON message
        final JsonObject requestObject = new JsonObject();
        requestObject.addProperty("action", "listen");
        final JsonArray streamsArray = new JsonArray();
        Arrays.stream(messageTypes)
                .filter(SUBSCRIBABLE_STREAMING_MESSAGE_TYPES::contains)
                .forEach((type) -> streamsArray.add(type.toString()));
        if (streamsArray.isEmpty()) {
            return;
        } else if (!isConnected()) {
            throw new IllegalStateException("This websocket must be connected before subscribing to streams!");
        }
        final JsonObject dataObject = new JsonObject();
        dataObject.add("streams", streamsArray);
        requestObject.add("data", dataObject);

        websocket.send(requestObject.toString());
        LOGGER.info("Requested streams: {}.", streamsArray);
    }

    @Override
    public Collection<TradesStreamMessageType> subscriptions() {
        return new HashSet<>(listenedMessageTypes);
    }
}
