package io.github.mainstringargs.alpaca.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.JsonObject;
import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.websocket.WebsocketClientEndpoint.MessageHandler;

/**
 * The Class WebsocketClient.
 */
public class WebsocketClient implements MessageHandler {

  /** The base account url. */
  private String baseAccountUrl;

  /** The key id. */
  private String keyId;

  /** The secret. */
  private String secret;

  /** The observers. */
  private List<WebsocketObserver> observers = new ArrayList<WebsocketObserver>();

  /** The client end point. */
  private WebsocketClientEndpoint clientEndPoint = null;

  /** The logger. */
  private static Logger LOGGER = LogManager.getLogger(WebsocketClient.class);

  /** The authorized object. */
  private JsonObject authorizedObject = new JsonObject();
  {
    authorizedObject.addProperty("stream", "authorization");
    JsonObject dataObject = new JsonObject();
    dataObject.addProperty("status", "authorized");
    dataObject.addProperty("action", "authenticate");
    authorizedObject.add("data", dataObject);
  }

  /**
   * Instantiates a new websocket client.
   *
   * @param keyId the key id
   * @param secret the secret
   * @param baseAccountUrl the base account url
   */
  public WebsocketClient(String keyId, String secret, String baseAccountUrl) {
    this.keyId = keyId;
    this.secret = secret;
    this.baseAccountUrl = baseAccountUrl.replace("https", "wss") + "/stream";
  }

  /**
   * Adds the observer.
   *
   * @param observer the observer
   */
  public void addObserver(WebsocketObserver observer) {
    if (observers.isEmpty()) {
      connect();
    }
    observers.add(observer);
  }



  /**
   * Removes the observer.
   *
   * @param observer the observer
   */
  public void removeObserver(WebsocketObserver observer) {

    observers.remove(observer);

    if (observers.isEmpty()) {
      disconnect();
    }
  }

  /**
   * Connect.
   */
  private void connect() {


    try {
      clientEndPoint = new WebsocketClientEndpoint(new URI(baseAccountUrl));

      clientEndPoint.addMessageHandler(this);

    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    /**
     * Format of message is:
     * 
     * { "action": "authenticate", "data": { "key_id": "{YOUR_API_KEY_ID}", "secret_key":
     * "{YOUR_API_SECRET_KEY}" } }
     */

    JsonObject authRequest = new JsonObject();
    authRequest.addProperty("action", "authenticate");
    JsonObject payload = new JsonObject();
    payload.addProperty("key_id", keyId);
    payload.addProperty("secret_key", secret);
    authRequest.add("data", payload);

    clientEndPoint.sendMessage(authRequest.toString());

  }

  /**
   * Disconnect.
   */
  private void disconnect() {
    try {
      clientEndPoint.getUserSession().close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * io.github.mainstringargs.alpaca.websocket.WebsocketClientEndpoint.MessageHandler#handleMessage(
   * com.google.gson.JsonObject)
   */
  @Override
  public void handleMessage(JsonObject message) {

    // {
    // "stream": "authorization",
    // "data": {
    // "status": "authorized",
    // "action": "authenticate"
    // }
    // }

    if (authorizedObject.equals(message)) {
      LOGGER.info("Authorized by Alpaca");

//      {
//        "action": "listen",
//        "data": {
//            "streams": ["account_updates", "trade_updates"]
//        }
//      }

    }

  }

  /**
   * Gets the registered message types.
   *
   * @return the registered message types
   */
  public Set<MessageType> getRegisteredMessageTypes() {

    Set<MessageType> registeredMessageTypes = new HashSet<MessageType>();

    for (WebsocketObserver observer : observers) {

      // if its empty, assume they want everything
      if (observer.getMessageTypes().isEmpty()) {
        registeredMessageTypes.addAll(Arrays.asList(MessageType.values()));
        break;
      }

      registeredMessageTypes.addAll(observer.getMessageTypes());
    }

    return registeredMessageTypes;

  }


}
