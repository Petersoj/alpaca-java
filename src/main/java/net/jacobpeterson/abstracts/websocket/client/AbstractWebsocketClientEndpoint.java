package net.jacobpeterson.abstracts.websocket.client;

import net.jacobpeterson.util.concurrency.ExecutorTracer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;

/**
 * The type Abstract websocket client endpoint. You must annotate a subclass with {@link javax.websocket.ClientEndpoint}
 * and the appropriate websocket subprotocols because websocket annotations don't work with inheritance. The subclass
 * must also contain separate methods with the following annotations: {@link javax.websocket.OnOpen}, {@link
 * javax.websocket.OnClose}, and {@link javax.websocket.OnMessage}.
 */
public abstract class AbstractWebsocketClientEndpoint {

    /** The constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger(AbstractWebsocketClientEndpoint.class);

    /** The Websocket client. */
    private final WebsocketClient websocketClient;

    /** The Endpoint uri. */
    private final URI endpointURI;

    /** The Executor service. */
    private final ExecutorService executorService;

    /** The User session. */
    private Session userSession;

    /** The Retry attempts. */
    private int retryAttempts = 0;

    private String subscription;

    /**
     * Instantiates a new Abstract websocket client endpoint.
     *
     * @param websocketClient   the websocket client
     * @param endpointURI       the endpoint uri
     * @param messageThreadName the message thread name
     */
    public AbstractWebsocketClientEndpoint(WebsocketClient websocketClient, URI endpointURI,
            String messageThreadName) {
        this.websocketClient = websocketClient;
        this.endpointURI = endpointURI;
        this.executorService = ExecutorTracer.newSingleThreadExecutor(r -> new Thread(r, messageThreadName));
    }

    /**
     * Connect.
     *
     * @throws DeploymentException the deployment exception
     * @throws IOException         Signals that an I/O exception has occurred.
     */
    public void connect() throws DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        LOGGER.info("Connecting to {}", endpointURI);

        container.connectToServer(this, endpointURI);
    }

    /**
     * Disconnect.
     *
     * @throws IOException the io exception
     */
    public void disconnect() throws IOException {
        if (userSession != null) {
            userSession.close();
        }
    }

    /**
     * On open.
     *
     * @param userSession the user session
     */
    protected void onOpen(Session userSession) {
        this.userSession = userSession;

        LOGGER.debug("onOpen {}", userSession);
        LOGGER.info("Websocket opened... Authenticating...");
        websocketClient.sendAuthenticationMessage();
    }

    /**
     * On close.
     *
     * @param userSession the user session
     * @param reason      the reason
     */
    protected void onClose(Session userSession, CloseReason reason) {

        LOGGER.debug("onClose {}", userSession);

        if (!reason.getCloseCode().equals(CloseReason.CloseCodes.NORMAL_CLOSURE)) {
            if (retryAttempts > 5) {
                LOGGER.error("More than 5 attempts to reconnect were made.  Bailing out.");
                return;
            }

            LOGGER.info("Attempting a reconnect in 3 seconds.");
            retryAttempts++;

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            LOGGER.info("Reconnecting due to closure: {}",
                    CloseReason.CloseCodes.getCloseCode(reason.getCloseCode().getCode()));

            try {
                reconnectAndResubscribe();
                retryAttempts = 0; // hopefully we are connected by this time, so reset the retry counter
            } catch (Exception e) {
                LOGGER.catching(e);
            }
        } else {
            this.userSession = null;
            LOGGER.info("Websocket closed");
        }
    }

    private void reconnectAndResubscribe() throws IOException, DeploymentException {
        LOGGER.info("Attempting to re-connect");
        connect();
        LOGGER.info("Resending subscriptions: {}", subscription);
        sendSubscription(subscription);
    }

    /**
     * On message.
     *
     * @param message the message
     */
    protected void onMessage(String message) {
        executorService.execute(() -> websocketClient.handleWebsocketMessage(message));
    }

    /**
     * Send a message.
     *
     * @param message the message
     */
    private void sendMessage(String message) {
        LOGGER.debug("sendMessage {}", message);
        userSession.getAsyncRemote().sendText(message);
    }

    public void sendAuthentication(String message) {
        sendMessage(message);
    }

    public void sendSubscription(String message) {
        this.subscription = message;
        sendMessage(message);
    }

    /**
     * Gets user session.
     *
     * @return the user session
     */
    public Session getUserSession() {
        return userSession;
    }
}
