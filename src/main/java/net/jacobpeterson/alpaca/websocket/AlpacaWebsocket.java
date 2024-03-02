package net.jacobpeterson.alpaca.websocket;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AlpacaWebsocket} represents an abstract websocket for Alpaca.
 *
 * @param <L> the {@link AlpacaWebsocketMessageListener} type parameter
 * @param <T> the 'message type' type parameter
 * @param <M> the 'message' type parameter
 */
public abstract class AlpacaWebsocket<T, M, L extends AlpacaWebsocketMessageListener<T, M>> extends WebSocketListener
        implements AlpacaWebsocketInterface<L> {

    /**
     * Defines a websocket normal closure code.
     *
     * @see WebSocket#close(int, String)
     */
    public static final int WEBSOCKET_NORMAL_CLOSURE_CODE = 1000;

    /**
     * Defines a websocket normal closure message.
     *
     * @see WebSocket#close(int, String)
     */
    public static final String WEBSOCKET_NORMAL_CLOSURE_MESSAGE = "Normal closure";

    /**
     * Defines the maximum number of reconnection attempts to be made by an {@link AlpacaWebsocket}.
     */
    public static int MAX_RECONNECT_ATTEMPTS = 5;

    /**
     * Defines the millisecond sleep interval between reconnection attempts made by an {@link AlpacaWebsocket}.
     */
    public static int RECONNECT_SLEEP_INTERVAL = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaWebsocket.class);

    protected final OkHttpClient okHttpClient;
    protected final HttpUrl websocketURL;
    protected final String websocketName;
    protected final String keyID;
    protected final String secretKey;
    protected final String oAuthToken;
    protected final boolean useOAuth;

    protected L listener;
    protected WebsocketStateListener websocketStateListener;
    protected WebSocket websocket;
    protected boolean connected;
    protected boolean authenticated;
    protected CompletableFuture<Boolean> authenticationMessageFuture;
    protected boolean intentionalClose;
    protected int reconnectAttempts;
    protected boolean automaticallyReconnect;

    /**
     * Instantiates a {@link AlpacaWebsocket}.
     *
     * @param okHttpClient  the {@link OkHttpClient}
     * @param websocketURL  the websocket {@link HttpUrl}
     * @param websocketName the websocket name
     * @param keyID         the key ID
     * @param secretKey     the secret key
     * @param oAuthToken    the OAuth token
     */
    public AlpacaWebsocket(OkHttpClient okHttpClient, HttpUrl websocketURL, String websocketName,
            String keyID, String secretKey, String oAuthToken) {
        checkNotNull(okHttpClient);
        checkNotNull(websocketURL);
        checkNotNull(websocketName);

        this.okHttpClient = okHttpClient;
        this.websocketURL = websocketURL;
        this.websocketName = websocketName;
        this.keyID = keyID;
        this.secretKey = secretKey;
        this.oAuthToken = oAuthToken;
        useOAuth = oAuthToken != null;

        automaticallyReconnect = true;
    }

    @Override
    public void connect() {
        if (!isConnected()) {
            Request websocketRequest = new Request.Builder()
                    .url(websocketURL)
                    .get()
                    .build();
            websocket = okHttpClient.newWebSocket(websocketRequest, this);
        }
    }

    @Override
    public void disconnect() {
        if (websocket != null && isConnected()) {
            intentionalClose = true;
            websocket.close(WEBSOCKET_NORMAL_CLOSURE_CODE, WEBSOCKET_NORMAL_CLOSURE_MESSAGE);
        } else {
            cleanupState();
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        connected = true;

        LOGGER.info("{} websocket opened.", websocketName);
        LOGGER.debug("{} websocket response: {}", websocketName, response);

        // Call 'onConnection' or 'onReconnection' async to avoid any potential dead-locking since this is called
        // in sync with 'onMessage' in OkHttp's 'WebSocketListener'
        ForkJoinPool.commonPool().execute(() -> {
            if (reconnectAttempts > 0) {
                reconnectAttempts = 0;
                onReconnection();
            } else {
                onConnection();
            }
        });

        if (websocketStateListener != null) {
            websocketStateListener.onOpen(response);
        }
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        connected = false;

        if (intentionalClose) {
            LOGGER.info("{} websocket closed.", websocketName);
            LOGGER.debug("Close code: {}, Reason: {}", code, reason);
            cleanupState();
        } else {
            LOGGER.error("{} websocket closed unintentionally! Code: {}, Reason: {}", websocketName, code, reason);
            handleReconnectionAttempt();
        }

        if (websocketStateListener != null) {
            websocketStateListener.onClosed(code, reason);
        }
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable cause, @Nullable Response response) {
        LOGGER.error("{} websocket failure!", websocketName, cause);

        // A websocket failure occurs when either there is a connection failure or when the client throws
        // an Exception when receiving a message. In either case, OkHttp will silently close the websocket
        // connection, so try to reopen it.
        connected = false;
        handleReconnectionAttempt();

        if (websocketStateListener != null) {
            websocketStateListener.onFailure(cause);
        }
    }

    /**
     * Attempts to reconnect the disconnected {@link #websocket} asynchronously.
     */
    private void handleReconnectionAttempt() {
        if (!automaticallyReconnect) {
            return;
        }

        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            LOGGER.info("Attempting to reconnect {} websocket in {} milliseconds...",
                    websocketName, RECONNECT_SLEEP_INTERVAL);

            reconnectAttempts++;

            ForkJoinPool.commonPool().execute(() -> {
                try {
                    Thread.sleep(RECONNECT_SLEEP_INTERVAL);
                } catch (InterruptedException ignored) {
                    return;
                }

                connect();
            });
        } else {
            LOGGER.error("Exhausted {} reconnection attempts. Not attempting to reconnect.", MAX_RECONNECT_ATTEMPTS);
            cleanupState();
        }
    }

    /**
     * Cleans up this instance's state variables.
     */
    protected void cleanupState() {
        websocket = null;
        connected = false;
        authenticated = false;
        if (authenticationMessageFuture != null && !authenticationMessageFuture.isDone()) {
            authenticationMessageFuture.complete(false);
        }
        authenticationMessageFuture = null;
        intentionalClose = false;
        reconnectAttempts = 0;
    }

    /**
     * Called asynchronously when a websocket connection is made.
     */
    protected abstract void onConnection();

    /**
     * Called asynchronously when a websocket reconnection is made after unintentional disconnection.
     */
    protected abstract void onReconnection();

    /**
     * Sends an authentication message to authenticate this websocket stream.
     */
    protected abstract void sendAuthenticationMessage();

    @Override
    public Future<Boolean> getAuthorizationFuture() {
        if (authenticationMessageFuture == null) {
            authenticationMessageFuture = new CompletableFuture<>();
        }

        return authenticationMessageFuture;
    }

    /**
     * Calls the {@link AlpacaWebsocketMessageListener}.
     *
     * @param messageType the message type
     * @param message     the message
     */
    protected void callListener(T messageType, M message) {
        if (listener != null) {
            try {
                listener.onMessage(messageType, message);
            } catch (Exception exception) {
                LOGGER.error("{} listener threw exception!", websocketName, exception);
            }
        }
    }

    @Override
    public void setListener(L listener) {
        this.listener = listener;
    }

    public WebsocketStateListener getWebsocketStateListener() {
        return websocketStateListener;
    }

    public void setWebsocketStateListener(WebsocketStateListener websocketStateListener) {
        this.websocketStateListener = websocketStateListener;
    }

    public boolean doesAutomaticallyReconnect() {
        return automaticallyReconnect;
    }

    public void setAutomaticallyReconnect(boolean automaticallyReconnect) {
        this.automaticallyReconnect = automaticallyReconnect;
    }
}
