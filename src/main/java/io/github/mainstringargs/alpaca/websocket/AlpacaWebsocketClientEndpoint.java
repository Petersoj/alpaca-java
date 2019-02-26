package io.github.mainstringargs.alpaca.websocket;

import java.io.IOException;
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The Class WebsocketClientEndpoint.
 */
@ClientEndpoint
public class AlpacaWebsocketClientEndpoint {

  /** The user session. */
  Session userSession = null;

  /** The message handler. */
  private MessageHandler messageHandler;

  /** The endpoint URI. */
  private URI endpointURI;

  /** The logger. */
  private static Logger LOGGER = LogManager.getLogger(AlpacaWebsocketClientEndpoint.class);

  /**
   * Instantiates a new websocket client endpoint.
   *
   * @param endpointURI the endpoint URI
   */
  public AlpacaWebsocketClientEndpoint(URI endpointURI) {

    this.endpointURI = endpointURI;

    try {
      connect(endpointURI);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Connect.
   *
   * @param endpointURI the endpoint URI
   * @throws DeploymentException the deployment exception
   * @throws IOException Signals that an I/O exception has occurred.
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

    LOGGER.info("onOpen " + userSession);
  }

  /**
   * Callback hook for Connection close events.
   *
   * @param userSession the userSession which is getting closed.
   * @param reason the reason for connection close
   */
  @OnClose
  public void onClose(Session userSession, CloseReason reason) {
    this.userSession = null;

    LOGGER.info("onClose " + userSession + " " + reason);

    try {
      connect(endpointURI);
    } catch (Exception e) {
      LOGGER.catching(e);
    }
  }

  /**
   * Callback hook for Message Events. This method will be invoked when a client send a message.
   *
   * @param message The text message
   */
  @OnMessage
  public void onMessage(byte[] message) {

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("onMessage " + new String(message));

    if (this.messageHandler != null) {
      JsonElement jelement = new JsonParser().parse(new String(message));
      JsonObject jobject = jelement.getAsJsonObject();

      this.messageHandler.handleMessage(jobject);
    }
  }

  /**
   * register message handler.
   *
   * @param msgHandler the msg handler
   */
  public void addMessageHandler(MessageHandler msgHandler) {
    this.messageHandler = msgHandler;
  }

  /**
   * Send a message.
   *
   * @param message the message
   */
  public void sendMessage(String message) {

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("sendMessage " + (message));

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

  /**
   * Message handler.
   */
  public static interface MessageHandler {

    /**
     * Handle message.
     *
     * @param message the message
     */
    public void handleMessage(JsonObject message);
  }

}
