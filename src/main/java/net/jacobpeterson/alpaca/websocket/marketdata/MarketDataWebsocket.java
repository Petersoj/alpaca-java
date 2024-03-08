package net.jacobpeterson.alpaca.websocket.marketdata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.jacobpeterson.alpaca.model.websocket.marketdata.model.MarketDataMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.model.control.ErrorMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.model.control.SuccessMessage;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocket;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Function;

import static com.google.common.collect.Sets.difference;
import static com.google.gson.JsonParser.parseString;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jacobpeterson.alpaca.model.websocket.marketdata.model.control.SuccessMessageType.AUTHENTICATED;
import static net.jacobpeterson.alpaca.openapi.marketdata.JSON.getGson;

/**
 * {@link MarketDataWebsocket} is an abstract {@link AlpacaWebsocket} implementation for
 * <a href="https://docs.alpaca.markets/docs/streaming-market-data">Market Data Streaming</a>.
 *
 * @param <T> the 'message type' type
 * @param <S> the 'subscription message' type
 * @param <L> the 'listener' type
 */
public abstract class MarketDataWebsocket<T, S extends MarketDataMessage, L> extends AlpacaWebsocket
        implements MarketDataWebsocketInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataWebsocket.class);
    private static final Set<String> AUTH_FAILURE_MESSAGES = Set.of("auth failed", "auth timeout", "not authenticated");

    protected final String authKey;
    protected final String authSecret;
    protected final Class<T> messageTypeClass;
    protected final Class<S> subscriptionsMessageClass;
    protected S subscriptionsMessage;
    protected L listener;

    /**
     * Instantiates a new {@link MarketDataWebsocket}.
     *
     * @param okHttpClient                the {@link OkHttpClient}
     * @param websocketURL                the websocket {@link HttpUrl}
     * @param websocketMarketDataTypeName the websocket market data type name {@link String}
     * @param traderKeyID                 the trader key ID
     * @param traderSecretKey             the trader secret key
     * @param brokerAPIKey                the broker API key
     * @param brokerAPISecret             the broker API secret
     * @param messageTypeClass            the {@link T} message type {@link Class}
     * @param subscriptionsMessageClass   the {@link S} subscription message {@link Class}
     */
    protected MarketDataWebsocket(OkHttpClient okHttpClient, HttpUrl websocketURL, String websocketMarketDataTypeName,
            String traderKeyID, String traderSecretKey, String brokerAPIKey, String brokerAPISecret,
            Class<T> messageTypeClass, Class<S> subscriptionsMessageClass) {
        super(okHttpClient, websocketURL, websocketMarketDataTypeName + " Market Data");
        final boolean traderKeysGiven = traderKeyID != null && traderSecretKey != null;
        this.authKey = traderKeysGiven ? traderKeyID : brokerAPIKey;
        this.authSecret = traderKeysGiven ? traderSecretKey : brokerAPISecret;
        this.messageTypeClass = messageTypeClass;
        this.subscriptionsMessageClass = subscriptionsMessageClass;
    }

    @Override
    protected void cleanupState() {
        super.cleanupState();
        subscriptionsMessage = null;
    }

    @Override
    protected void onConnection() {
        sendAuthenticationMessage();
    }

    @Override
    protected void onReconnection() {
        sendAuthenticationMessage();
        if (waitForAuthorization(5, SECONDS) && subscriptionsMessage != null) {
            sendSubscriptionMessage(subscriptionsMessage, true);
        }
    }

    @Override
    protected void sendAuthenticationMessage() {
        // Ensure that the authorization Future exists
        getAuthorizationFuture();

        final JsonObject authObject = new JsonObject();
        authObject.addProperty("action", "auth");
        authObject.addProperty("key", authKey);
        authObject.addProperty("secret", authSecret);

        LOGGER.info("{} websocket sending authentication message...", websocketName);
        sendWebsocketMessage(authObject.toString());
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String message) { // Text framing
        LOGGER.trace("Websocket message received: {}", message);

        // Loop through message array and handle each message according to its type
        for (JsonElement arrayElement : parseString(message).getAsJsonArray()) {
            final JsonObject messageObject = arrayElement.getAsJsonObject();
            final T messageType = getGson().fromJson(messageObject.get("T"), messageTypeClass);
            if (isSuccessMessageType(messageType)) {
                final SuccessMessage successMessage = getGson().fromJson(messageObject, SuccessMessage.class);
                if (successMessage.getMessageType() == AUTHENTICATED) {
                    LOGGER.info("{} websocket authenticated.", websocketName);
                    authenticated = true;
                    if (authenticationMessageFuture != null) {
                        authenticationMessageFuture.complete(true);
                    }
                }
            } else if (isErrorMessageType(messageType)) {
                final ErrorMessage errorMessage = getGson().fromJson(messageObject, ErrorMessage.class);
                if (AUTH_FAILURE_MESSAGES.contains(errorMessage.getMessage()) && authenticationMessageFuture != null) {
                    authenticated = false;
                    authenticationMessageFuture.complete(false);
                    throw new RuntimeException(websocketName + " websocket authentication failed!");
                } else {
                    throw new RuntimeException(websocketName + " websocket error! Message: " + errorMessage);
                }
            } else if (isSubscriptionMessageType(messageType)) {
                subscriptionsMessage = getGson().fromJson(messageObject, subscriptionsMessageClass);
            } else if (listener != null) {
                callListenerWithMessage(messageType, messageObject);
            }
        }
    }

    /**
     * Sets the websocket stream's subscriptions for a specific message type.
     *
     * @param previousSubscriptions           the previous subscriptions symbol {@link Set}
     * @param newSubscriptions                the new subscriptions symbol {@link Set}
     * @param subscriptionUpdateObjectCreator the subscription update object creator {@link Function}
     */
    protected void setSubscriptions(@NotNull Set<String> previousSubscriptions,
            @NotNull Set<String> newSubscriptions, @NotNull Function<Set<String>, S> subscriptionUpdateObjectCreator) {
        // Unsubscribe from previous subscriptions
        final Set<String> unsubscribeSet = difference(previousSubscriptions, newSubscriptions);
        if (!unsubscribeSet.isEmpty()) {
            sendSubscriptionMessage(subscriptionUpdateObjectCreator.apply(unsubscribeSet), false);
        }
        // Subscribe to new subscriptions
        final Set<String> subscribeSet = difference(newSubscriptions, previousSubscriptions);
        if (!subscribeSet.isEmpty()) {
            sendSubscriptionMessage(subscriptionUpdateObjectCreator.apply(subscribeSet), true);
        }
    }

    private void sendSubscriptionMessage(S subscriptionsMessage, boolean subscribe) {
        final JsonObject subscribeObject = getGson().toJsonTree(subscriptionsMessage).getAsJsonObject();
        subscribeObject.addProperty("action", subscribe ? "subscribe" : "unsubscribe");
        sendWebsocketMessage(getGson().toJson(subscribeObject));
    }

    /**
     * Whether the given <code>messageType</code> is "success".
     *
     * @param messageType the message type
     *
     * @return a boolean
     */
    protected abstract boolean isSuccessMessageType(T messageType);

    /**
     * Whether the given <code>messageType</code> is "error".
     *
     * @param messageType the message type
     *
     * @return a boolean
     */
    protected abstract boolean isErrorMessageType(T messageType);

    /**
     * Whether the given <code>messageType</code> is "subscription".
     *
     * @param messageType the message type
     *
     * @return a boolean
     */
    protected abstract boolean isSubscriptionMessageType(T messageType);

    /**
     * Calls the {@link #listener} with a {@link MarketDataMessage} from a {@link JsonObject}.
     *
     * @param messageType   the message type
     * @param messageObject the message {@link JsonObject}
     */
    protected abstract void callListenerWithMessage(T messageType, JsonObject messageObject);
}
