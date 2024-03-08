package net.jacobpeterson.alpaca.websocket.updates;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.jacobpeterson.alpaca.model.util.apitype.TraderAPIEndpointType;
import net.jacobpeterson.alpaca.model.websocket.updates.model.UpdatesMessageType;
import net.jacobpeterson.alpaca.model.websocket.updates.model.authorization.AuthorizationMessage;
import net.jacobpeterson.alpaca.model.websocket.updates.model.tradeupdate.TradeUpdateMessage;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocket;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.gson.JsonParser.parseString;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jacobpeterson.alpaca.model.websocket.updates.model.UpdatesMessageType.TRADE_UPDATES;
import static net.jacobpeterson.alpaca.openapi.trader.JSON.getGson;

/**
 * {@link UpdatesWebsocket} is an {@link AlpacaWebsocket} implementation and provides the
 * {@link UpdatesWebsocketInterface} interface for
 * <a href="https://docs.alpaca.markets/docs/websocket-streaming">Updates Streaming</a>.
 */
public class UpdatesWebsocket extends AlpacaWebsocket implements UpdatesWebsocketInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatesWebsocket.class);

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

    protected final String keyID;
    protected final String secretKey;
    protected final String oAuthToken;
    protected UpdatesListener listener;
    protected boolean listenToTradeUpdates;

    /**
     * Instantiates a new {@link UpdatesWebsocket}.
     *
     * @param okHttpClient          the {@link OkHttpClient}
     * @param traderAPIEndpointType the {@link TraderAPIEndpointType}
     * @param keyID                 the key ID
     * @param secretKey             the secret key
     * @param oAuthToken            the OAuth token
     */
    public UpdatesWebsocket(OkHttpClient okHttpClient, TraderAPIEndpointType traderAPIEndpointType,
            String keyID, String secretKey, String oAuthToken) {
        super(okHttpClient, createWebsocketURL(traderAPIEndpointType), "Trades Stream");
        this.keyID = keyID;
        this.secretKey = secretKey;
        this.oAuthToken = oAuthToken;
    }

    @Override
    protected void cleanupState() {
        super.cleanupState();
    }

    @Override
    protected void onConnection() {
        sendAuthenticationMessage();
    }

    @Override
    protected void onReconnection() {
        sendAuthenticationMessage();
        if (waitForAuthorization(5, SECONDS) && listenToTradeUpdates) {
            sendTradeUpdatesListenMessage();
        }
    }

    @Override
    protected void sendAuthenticationMessage() {
        // Ensure that the authorization Future exists
        getAuthorizationFuture();

        final JsonObject authObject = new JsonObject();
        authObject.addProperty("action", "authenticate");
        final JsonObject authData = new JsonObject();
        if (oAuthToken != null) {
            authData.addProperty("oauth_token", oAuthToken);
        } else {
            authData.addProperty("key_id", keyID);
            authData.addProperty("secret_key", secretKey);
        }
        authObject.add("data", authData);

        LOGGER.info("{} websocket sending authentication message...", websocketName);
        sendWebsocketMessage(authObject.toString());
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString byteString) { // Binary framing
        final String messageString = byteString.utf8();
        LOGGER.trace("Websocket message received: message={}", messageString);

        // Parse JSON string and identify 'messageType'
        final JsonObject messageObject = parseString(messageString).getAsJsonObject();
        final UpdatesMessageType messageType =
                getGson().fromJson(messageObject.get("stream"), UpdatesMessageType.class);

        // Deserialize message based on 'messageType' and call listener
        switch (messageType) {
            case AUTHORIZATION:
                final AuthorizationMessage authorizationMessage =
                        getGson().fromJson(messageObject, AuthorizationMessage.class);
                authenticated = authorizationMessage.getData().getAction().equalsIgnoreCase("authenticate") &&
                        authorizationMessage.getData().getStatus().equalsIgnoreCase("authorized");
                if (authenticationMessageFuture != null) {
                    authenticationMessageFuture.complete(authenticated);
                }
                if (!authenticated) {
                    throw new RuntimeException(websocketName + " websocket authentication failed!");
                } else {
                    LOGGER.info("{} websocket authenticated.", websocketName);
                }
                break;
            case LISTENING:
                break;
            case TRADE_UPDATES:
                if (listener != null) {
                    listener.onTradeUpdate(getGson().fromJson(messageObject, TradeUpdateMessage.class));
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setListener(UpdatesListener listener) {
        this.listener = listener;
    }

    @Override
    public void subscribeToTradeUpdates(boolean subscribe) {
        listenToTradeUpdates = subscribe;
        sendTradeUpdatesListenMessage();
    }

    private void sendTradeUpdatesListenMessage() {
        final JsonObject requestObject = new JsonObject();
        requestObject.addProperty("action", "listen");
        final JsonArray streamsArray = new JsonArray();
        if (listenToTradeUpdates) {
            streamsArray.add(TRADE_UPDATES.toString());
        }
        final JsonObject dataObject = new JsonObject();
        dataObject.add("streams", streamsArray);
        requestObject.add("data", dataObject);

        sendWebsocketMessage(requestObject.toString());
        LOGGER.info("Requested streams: streams={}.", streamsArray);
    }
}
