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

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.ForkJoinPool.commonPool;

/**
 * {@link AlpacaWebsocket} represents an abstract websocket for Alpaca.
 */
public abstract class AlpacaWebsocket extends WebSocketListener implements AlpacaWebsocketInterface {

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
     * Defines the sleep interval {@link Duration} between reconnection attempts made by an {@link AlpacaWebsocket}.
     */
    public static Duration RECONNECT_SLEEP_INTERVAL = Duration.ofSeconds(1);

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaWebsocket.class);

    protected final OkHttpClient okHttpClient;
    protected final HttpUrl websocketURL;
    protected final String websocketName;
    protected AlpacaWebsocketStateListener alpacaWebsocketStateListener;
    private WebSocket websocket;
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
     */
    protected AlpacaWebsocket(OkHttpClient okHttpClient, HttpUrl websocketURL, String websocketName) {
        checkNotNull(okHttpClient);
        checkNotNull(websocketURL);
        checkNotNull(websocketName);

        this.okHttpClient = okHttpClient;
        this.websocketURL = websocketURL;
        this.websocketName = websocketName;

        automaticallyReconnect = true;
    }

    @Override
    public void connect() {
        if (!isConnected()) {
            final Request websocketRequest = new Request.Builder()
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
        LOGGER.info("{} websocket response: response={}", websocketName, response);
        // Call 'onConnection' or 'onReconnection' async to avoid any potential deadlocking since this is called
        // in sync with 'onMessage' in OkHttp's 'WebSocketListener'
        commonPool().execute(() -> {
            if (reconnectAttempts > 0) {
                onReconnection();
            } else {
                onConnection();
            }
        });
        if (alpacaWebsocketStateListener != null) {
            alpacaWebsocketStateListener.onOpen(response);
        }
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        connected = false;
        if (intentionalClose) {
            LOGGER.info("{} websocket closed. code={}, reason={}", websocketName, code, reason);
            cleanupState();
        } else {
            LOGGER.error("{} websocket closed unintentionally! code={}, reason={}", websocketName, code, reason);
            handleReconnectionAttempt();
        }
        if (alpacaWebsocketStateListener != null) {
            alpacaWebsocketStateListener.onClosed(code, reason);
        }
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable cause, @Nullable Response response) {
        if (intentionalClose) {
            onClosed(webSocket, WEBSOCKET_NORMAL_CLOSURE_CODE, WEBSOCKET_NORMAL_CLOSURE_MESSAGE);
            return;
        }

        LOGGER.error("{} websocket failure!", websocketName, cause);
        // A websocket failure occurs when either there is a connection failure or when the client throws
        // an exception when receiving a message. In either case, OkHttp will close the websocket connection,
        // so try to reopen it.
        connected = false;
        handleReconnectionAttempt();
        if (alpacaWebsocketStateListener != null) {
            alpacaWebsocketStateListener.onFailure(cause);
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
            LOGGER.info("Attempting to reconnect {} websocket in {} seconds... (attempt {} of {})",
                    websocketName, RECONNECT_SLEEP_INTERVAL.toSeconds(), reconnectAttempts + 1, MAX_RECONNECT_ATTEMPTS);
            reconnectAttempts++;
            commonPool().execute(() -> {
                try {
                    Thread.sleep(RECONNECT_SLEEP_INTERVAL.toMillis());
                } catch (InterruptedException interruptedException) {
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
     * Sends a message to the underlying {@link #websocket}.
     *
     * @param message the message
     */
    protected void sendWebsocketMessage(String message) {
        if (!isConnected()) {
            throw new IllegalStateException("This websocket must be connected before send a message!");
        }
        LOGGER.trace("Websocket message sent: {}", message);
        websocket.send(message);
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

    @Override
    public void setAlpacaWebsocketStateListener(AlpacaWebsocketStateListener alpacaWebsocketStateListener) {
        this.alpacaWebsocketStateListener = alpacaWebsocketStateListener;
    }

    @Override
    public boolean doesAutomaticallyReconnect() {
        return automaticallyReconnect;
    }

    @Override
    public void setAutomaticallyReconnect(boolean automaticallyReconnect) {
        this.automaticallyReconnect = automaticallyReconnect;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public HttpUrl getWebsocketURL() {
        return websocketURL;
    }

    public WebSocket getWebsocket() {
        return websocket;
    }
}
