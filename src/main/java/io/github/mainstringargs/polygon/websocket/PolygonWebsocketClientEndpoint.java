package io.github.mainstringargs.polygon.websocket;

import com.google.gson.JsonArray;
import io.github.mainstringargs.abstracts.websocket.MessageHandler;
import io.github.mainstringargs.util.concurrency.ExecutorTracer;
import io.github.mainstringargs.util.gson.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;

/**
 * The Class WebsocketClientEndpoint.
 */
@ClientEndpoint
public class PolygonWebsocketClientEndpoint {

    /** The executor. */
    private static final ExecutorService executor = ExecutorTracer.newSingleThreadExecutor(
            r -> new Thread(r, "PolygonWebsocketThread"));

    /** The logger. */
    private static final Logger LOGGER = LogManager.getLogger(PolygonWebsocketClientEndpoint.class);

    /** The user session. */
    private Session userSession = null;

    /** The message handler. */
    private MessageHandler<JsonArray> messageHandler;

    /** The endpoint URI. */
    private URI endpointURI;

    /** The Retry attempts. */
    private int retryAttempts = 0;

    /**
     * Instantiates a new Polygon websocket client endpoint.
     *
     * @param endpointURI the endpoint URI
     */
    public PolygonWebsocketClientEndpoint(URI endpointURI) {
        this.endpointURI = endpointURI;

        try {
            connect(endpointURI);
        } catch (Exception e) {
            LOGGER.throwing(e);
        }
    }

    /**
     * Connect.
     *
     * @param endpointURI the endpoint URI
     *
     * @throws DeploymentException the deployment exception
     * @throws IOException         Signals that an I/O exception has occurred.
     */
    private void connect(URI endpointURI) throws DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        LOGGER.info("Connecting to " + endpointURI);
        container.connectToServer(this, endpointURI);
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;

        LOGGER.debug("onOpen " + userSession);
        LOGGER.info("The websocket has opened");
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason      the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;

        LOGGER.debug("onClose " + userSession + " " + reason + " " + reason.getReasonPhrase());

        if (!reason.getCloseCode().equals(CloseCodes.NORMAL_CLOSURE)) {
            if (retryAttempts > 5) {
                LOGGER.error("More than 5 attempts to reconnect were made.");
                return;
            }

            LOGGER.info("Attempting a reconnect in 10 seconds.");
            retryAttempts++;

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            LOGGER.info("Reconnecting due to closure " + CloseCodes.getCloseCode(reason.getCloseCode().getCode()));

            try {
                connect(endpointURI);
            } catch (Exception e) {
                LOGGER.catching(e);
            }
        } else {
            LOGGER.info("The websocket has closed");
        }
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        executor.execute(() -> {
            if (messageHandler != null) {
                JsonArray jsonArray = GsonUtil.JSON_PARSER.parse(message).getAsJsonArray();

                messageHandler.handleMessage(jsonArray);
            }
        });
    }

    /**
     * Sets message handler.
     *
     * @param messageHandler the message handler
     */
    public void setMessageHandler(MessageHandler<JsonArray> messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Send a message.
     *
     * @param message the message
     */
    public void sendMessage(String message) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sendMessage " + (message));
        }

        this.userSession.getAsyncRemote().sendText(message);
    }

    /**
     * Gets the user session.
     *
     * @return the user session
     */
    public Session getUserSession() {
        return this.userSession;
    }
}
