package io.github.mainstringargs.abstracts.websocket.client;

import io.github.mainstringargs.util.concurrency.ExecutorTracer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

/**
 * The type Abstract websocket client endpoint.
 */
@ClientEndpoint
public abstract class AbstractWebsocketClientEndpoint {

    /** The constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger(AbstractWebsocketClientEndpoint.class);

    /** The Websocket client. */
    private WebsocketClient websocketClient;

    /** The Endpoint uri. */
    private final URI endpointURI;

    /** The Executor service. */
    private final ExecutorService executorService;

    /** The User session. */
    private Session userSession;

    /** The Retry attempts. */
    private int retryAttempts = 0;

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

        LOGGER.info("Connecting to " + endpointURI);

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
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;

        LOGGER.debug("onOpen " + userSession);
        LOGGER.info("Websocket opened");
    }

    /**
     * On close.
     *
     * @param userSession the user session
     * @param reason      the reason
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;

        LOGGER.debug("onClose " + userSession);

        if (!reason.getCloseCode().equals(CloseReason.CloseCodes.NORMAL_CLOSURE)) {
            if (retryAttempts > 5) {
                LOGGER.error("More than 5 attempts to reconnect were made.");
                return;
            }

            LOGGER.info("Attempting a reconnect in 3 seconds.");
            retryAttempts++;

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            LOGGER.info("Reconnecting due to closure " +
                    CloseReason.CloseCodes.getCloseCode(reason.getCloseCode().getCode()));

            try {
                connect();
            } catch (Exception e) {
                LOGGER.catching(e);
            }
        } else {
            LOGGER.info("Websocket closed");
        }
    }

    /**
     * On message.
     *
     * @param message the message
     */
    @OnMessage
    public void onMessage(byte[] message) {
        executorService.execute(() -> websocketClient.handleWebsocketMessage(new String(message,
                StandardCharsets.UTF_8)));
    }

    /**
     * Send a message.
     *
     * @param message the message
     */
    public void sendMessage(String message) {
        LOGGER.debug("sendMessage " + message);

        this.userSession.getAsyncRemote().sendText(message);
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
