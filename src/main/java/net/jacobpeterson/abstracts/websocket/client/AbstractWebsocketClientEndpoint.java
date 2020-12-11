package net.jacobpeterson.abstracts.websocket.client;

import net.jacobpeterson.util.concurrency.ExecutorTracer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.component.LifeCycle;

import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;

/**
 * The type {@link AbstractWebsocketClientEndpoint}.
 * <br>
 * NOTES: You MUST annotate a subclass with {@link javax.websocket.ClientEndpoint} and the appropriate websocket
 * subprotocols because websocket annotations don't work with inheritance. The subclass must also contain separate
 * methods with the following annotations: {@link javax.websocket.OnOpen}, {@link javax.websocket.OnClose}, {@link
 * javax.websocket.OnMessage}, and {@link javax.websocket.OnError}.
 */
public abstract class AbstractWebsocketClientEndpoint {

    /** The constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger(AbstractWebsocketClientEndpoint.class);

    /** The Websocket client. */
    private final WebsocketClient websocketClient;

    /** The Endpoint uri. */
    private final URI endpointURI;

    /** The Message thread name. */
    private final String messageThreadName;

    /**
     * The Executor service, which passes message handlers to a different thread, will prevent overflow of server
     * buffers (causing a disconnect) from not consuming data fast enough on the client end.
     */
    private ExecutorService executorService;

    /** The automatically reconnect boolean. */
    private boolean automaticallyReconnect;

    /** The User session. */
    private Session userSession;

    /**
     * Instantiates a new {@link AbstractWebsocketClientEndpoint}.
     *
     * @param websocketClient   the websocket client
     * @param endpointURI       the endpoint uri
     * @param messageThreadName the message thread name
     */
    public AbstractWebsocketClientEndpoint(WebsocketClient websocketClient, URI endpointURI,
            String messageThreadName) {
        this.websocketClient = websocketClient;
        this.endpointURI = endpointURI;
        this.messageThreadName = messageThreadName;
        this.automaticallyReconnect = true;
    }

    /**
     * Connect.
     *
     * @throws DeploymentException the deployment exception
     * @throws IOException         Signals that an I/O exception has occurred.
     */
    public void connect() throws DeploymentException, IOException {
        executorService = ExecutorTracer.newSingleThreadExecutor(r -> new Thread(r, messageThreadName));

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        LOGGER.info("Connecting to {}", endpointURI);
        container.connectToServer(this, endpointURI);
    }

    /**
     * Disconnect.
     *
     * @throws IOException the io exception
     */
    public void disconnect() throws Exception {
        automaticallyReconnect = false;

        if (userSession != null) {
            userSession.close();

            WebSocketContainer webSocketContainer = userSession.getContainer();
            if (webSocketContainer instanceof LifeCycle) {
                ((LifeCycle) webSocketContainer).stop(); // Closes all websocket-related threads
            }
        }

        if (executorService != null) {
            executorService.shutdown();
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

        if (!reason.getCloseCode().equals(CloseReason.CloseCodes.NORMAL_CLOSURE) && automaticallyReconnect) {

            LOGGER.info("Reconnecting due to closure: {}",
                    CloseReason.CloseCodes.getCloseCode(reason.getCloseCode().getCode()));

            try {
                connect();
                websocketClient.handleResubscribing();
            } catch (Exception e) {
                LOGGER.catching(e);
            }
        } else {
            this.userSession = null;
            LOGGER.info("Websocket closed");
        }
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
     * On error.
     *
     * @param throwable the throwable
     */
    protected void onError(Throwable throwable) {
        LOGGER.error(throwable);
    }

    /**
     * Send a message.
     *
     * @param message the message
     */
    public void sendMessage(String message) {
        LOGGER.debug("sendMessage {}", message);
        userSession.getAsyncRemote().sendText(message);
    }

    /**
     * Gets user session.
     *
     * @return the user session
     */
    public Session getUserSession() {
        return userSession;
    }

    /**
     * Does automatically reconnect boolean.
     *
     * @return the boolean
     */
    public boolean doesAutomaticallyReconnect() {
        return automaticallyReconnect;
    }

    /**
     * Sets automatically reconnect.
     *
     * @param automaticallyReconnect the automatically reconnect
     */
    public void setAutomaticallyReconnect(boolean automaticallyReconnect) {
        this.automaticallyReconnect = automaticallyReconnect;
    }
}
