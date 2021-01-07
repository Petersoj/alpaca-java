package net.jacobpeterson.abstracts.websocket.client;

import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * {@link AbstractWebsocketClientEndpoint} is used for handling a Websocket directly.
 * <br>
 * NOTES: You MUST annotate a subclass with @see and the appropriate websocket sub-protocols because Websocket
 * annotations don't work with inheritance. The subclass must also contain separate methods with the following
 * annotations: {@link javax.websocket.OnOpen}, {@link javax.websocket.OnClose}, {@link javax.websocket.OnMessage}, and
 * {@link javax.websocket.OnError}.
 */
public abstract class AbstractWebsocketClientEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWebsocketClientEndpoint.class);

    private final WebsocketClient websocketClient;
    private final URI endpointURI;
    private final String messageThreadName;

    /**
     * {@link ExecutorService} passes message handlers to a different thread, which will prevent overflow of server
     * buffers which may cause a disconnect from not consuming data fast enough on the client end.
     */
    private ExecutorService executorService;
    private boolean automaticallyReconnect;
    private Session userSession;

    /**
     * Instantiates a new {@link AbstractWebsocketClientEndpoint}.
     *
     * @param websocketClient   the {@link WebsocketClient}
     * @param endpointURI       the endpoint {@link URI}
     * @param messageThreadName the message thread name
     */
    public AbstractWebsocketClientEndpoint(WebsocketClient websocketClient, URI endpointURI, String messageThreadName) {
        this.websocketClient = websocketClient;
        this.endpointURI = endpointURI;
        this.messageThreadName = messageThreadName;

        automaticallyReconnect = true;
    }

    /**
     * Connects this Websocket.
     *
     * @throws DeploymentException throw for {@link DeploymentException}s
     * @throws IOException         throw for {@link IOException}s
     */
    public void connect() throws DeploymentException, IOException {
        executorService = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, messageThreadName));

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        LOGGER.info("Connecting to {}", endpointURI);
        container.connectToServer(this, endpointURI);
    }

    /**
     * Disconnects this Websocket.
     *
     * @throws IOException throw for {@link IOException}s
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
     * Handles the Websocket open.
     *
     * @param userSession the user {@link Session}
     */
    protected void onOpen(Session userSession) {
        this.userSession = userSession;

        LOGGER.debug("onOpen {}", userSession);
        LOGGER.info("Websocket opened... Authenticating...");
        websocketClient.sendAuthenticationMessage();
    }

    /**
     * Handles the Websocket close.
     *
     * @param userSession the user {@link Session}
     * @param reason      the {@link CloseReason}
     */
    protected void onClose(Session userSession, CloseReason reason) {
        LOGGER.debug("onClose {}", userSession);

        if (!reason.getCloseCode().equals(CloseReason.CloseCodes.NORMAL_CLOSURE) && automaticallyReconnect) {

            LOGGER.info("Reconnecting due to closure: {}",
                    CloseReason.CloseCodes.getCloseCode(reason.getCloseCode().getCode()));

            try {
                connect();
                websocketClient.handleResubscribing();
            } catch (Exception exception) {
                LOGGER.error("Could not reconnect!", exception);
            }
        } else {
            this.userSession = null;
            LOGGER.info("Websocket closed");
        }
    }

    /**
     * Called when the Websocket message is received.
     *
     * @param message the message
     */
    protected void onMessage(String message) {
        executorService.execute(() -> websocketClient.handleWebsocketMessage(message));
    }

    /**
     * Handles the Websocket error.
     *
     * @param throwable the {@link Throwable}
     */
    protected void onError(Throwable throwable) {
        LOGGER.error("Websocket Error!", throwable);
    }

    /**
     * Sends a message through the Websocket.
     *
     * @param message the message
     */
    public void sendMessage(String message) {
        LOGGER.debug("sendMessage {}", message);
        userSession.getAsyncRemote().sendText(message);
    }

    /**
     * Gets user {@link Session}.
     *
     * @return the user {@link Session}
     */
    public Session getUserSession() {
        return userSession;
    }

    /**
     * Returns true if the Websocket is automatically reconnected except when {@link CloseReason} is {@link
     * CloseReason.CloseCodes#NORMAL_CLOSURE}.
     *
     * @return true if the Websocket is automatically reconnected except when {@link CloseReason} is {@link
     * CloseReason.CloseCodes#NORMAL_CLOSURE}.
     */
    public boolean doesAutomaticallyReconnect() {
        return automaticallyReconnect;
    }

    /**
     * Sets the Websocket to automatically reconnected except when {@link CloseReason} is {@link
     * CloseReason.CloseCodes#NORMAL_CLOSURE}.
     *
     * @param automaticallyReconnect true to automatically reconnect
     */
    public void setAutomaticallyReconnect(boolean automaticallyReconnect) {
        this.automaticallyReconnect = automaticallyReconnect;
    }
}
