package net.jacobpeterson.alpaca.websocket;

import net.jacobpeterson.alpaca.util.okhttp.WebsocketStateListener;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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

    /** Defines the maximum number of reconnection attempts to be made by an {@link AlpacaWebsocket}. */
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
    protected final List<L> listeners;
    protected final Object listenersLock;
    protected final List<L> listenersToAdd;
    protected final List<L> listenersToRemove;

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
        listeners = new ArrayList<>();
        listenersLock = new Object();
        listenersToAdd = new ArrayList<>();
        listenersToRemove = new ArrayList<>();

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
        if (isConnected() && websocket != null) {
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
        LOGGER.info("{} websocket opened.", websocketName);
        LOGGER.debug("{} websocket response: {}", websocketName, response);

        connected = true;

        if (reconnectAttempts > 0) {
            reconnectAttempts = 0;
            onReconnection();
        } else {
            onConnection();
        }

        if (websocketStateListener != null) {
            websocketStateListener.onOpen(response);
        }
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
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
        LOGGER.error("{} websocket failure! Response: {}", websocketName, response, cause);
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
     * Cleans up this instances state variables.
     */
    protected void cleanupState() {
        listeners.clear();
        listenersToAdd.clear();
        listenersToRemove.clear();

        websocket = null;
        connected = false;
        authenticated = false;
        intentionalClose = false;
        reconnectAttempts = 0;
    }

    /**
     * Called when a websocket connection is made.
     */
    protected abstract void onConnection();

    /**
     * Called when a websocket reconnection is made after unintentional disconnection.
     */
    protected abstract void onReconnection();

    /**
     * Sends an authentication message to authenticate this websocket stream.
     */
    protected abstract void sendAuthenticationMessage();

    @Override
    public Future<Boolean> getAuthorizationFuture() {
        if (authenticationMessageFuture == null || authenticationMessageFuture.isDone()) {
            authenticationMessageFuture = new CompletableFuture<>();
        }

        return authenticationMessageFuture;
    }

    /**
     * Calls the {@link AlpacaWebsocketMessageListener}s in {@link #listeners}.
     *
     * @param messageType the message type
     * @param message     the message
     */
    protected void callListeners(T messageType, M message) {
        // Remove all listeners that were request to be removed then add all listeners that were request to be added
        // on the last 'onMessage' call to the listeners.
        synchronized (listenersLock) {
            listeners.removeAll(listenersToRemove);
            listenersToRemove.clear();

            listeners.addAll(listenersToAdd);
            listenersToAdd.clear();
        }

        // Note that this doesn't need to acquire 'listenersLock' since 'listeners' is isolated
        // to this instance and is not modified outside a lock.
        for (L listener : listeners) {
            listener.onMessage(messageType, message);
        }
    }

    @Override
    public void addListener(L listener) {
        synchronized (listenersLock) {
            listenersToAdd.add(listener);
        }
    }

    @Override
    public void removeListener(L listener) {
        if (listeners.size() == 1 && listeners.contains(listener)) {
            disconnect();
            return;
        }

        synchronized (listenersLock) {
            listenersToRemove.add(listener);
        }
    }

    /**
     * Gets {@link #websocketStateListener}.
     *
     * @return the {@link WebsocketStateListener}
     */
    public WebsocketStateListener getWebsocketStateListener() {
        return websocketStateListener;
    }

    /**
     * Sets {@link #websocketStateListener}.
     *
     * @param websocketStateListener an {@link WebsocketStateListener}
     */
    public void setWebsocketStateListener(WebsocketStateListener websocketStateListener) {
        this.websocketStateListener = websocketStateListener;
    }

    /**
     * Gets {@link #automaticallyReconnect}.
     *
     * @return a boolean
     */
    public boolean doesAutomaticallyReconnect() {
        return automaticallyReconnect;
    }

    /**
     * Sets {@link #automaticallyReconnect}.
     *
     * @param automaticallyReconnect the boolean
     */
    public void setAutomaticallyReconnect(boolean automaticallyReconnect) {
        this.automaticallyReconnect = automaticallyReconnect;
    }
}
