package io.github.mainstringargs.polygon.nats;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.websocket.message.AccountUpdateMessage;
import io.github.mainstringargs.alpaca.websocket.message.OrderUpdateMessage;
import io.github.mainstringargs.alpaca.websocket.message.UpdateMessage;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.nats.client.Options.Builder;

/**
 * The Class WebsocketClient.
 */
public class PolygonNatsClient {



  /** The observers. */
  private List<PolygonStreamListener> listeners = new ArrayList<PolygonStreamListener>();


  /** The logger. */
  private static Logger LOGGER = LogManager.getLogger(PolygonNatsClient.class);


  private Options polygonOptions;

  private Connection polygonConnection;

  private Dispatcher polygonDispatcher;


  public PolygonNatsClient(String keyId, String... polygonNatsServers) {
    this(keyId, -1, polygonNatsServers);

  }

  public PolygonNatsClient(String keyId, int maxReconnects, String... polygonNatsServers) {
    Builder optionsBuilder = new Options.Builder();

    for (String serverUrl : polygonNatsServers) {
      optionsBuilder.server("nats://" + keyId + "@" + serverUrl);
    }

    optionsBuilder.maxReconnects(maxReconnects);

    polygonOptions = optionsBuilder.verbose().build();

    LOGGER.info("Polygon Options set to " + polygonOptions);
  }



  /**
   * Adds the listener.
   *
   * @param listener the listener
   */
  public void addListener(PolygonStreamListener listener) {

    if (listeners.isEmpty()) {
      connect();
    }

    listeners.add(listener);

//    updateSubscriptions();

  }



  /**
   * Removes the listener.
   *
   * @param listener the listener
   */
  public void removeListener(PolygonStreamListener listener) {

    listeners.remove(listener);

    if (listeners.isEmpty()) {
      disconnect();
    }

//    updateSubscriptions();

  }

  /**
   * Connect.
   */
  private void connect() {


    try {
      polygonConnection = Nats.connect(polygonOptions);


      polygonDispatcher = polygonConnection.createDispatcher((msg) -> {
        String response = new String(msg.getData(), StandardCharsets.UTF_8);

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("onMessage " + new String(response));

        }

        handleMessage(response);

      });

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }


  }

  /**
   * Disconnect.
   */
  private void disconnect() {

    if (polygonConnection != null) {
      try {
        polygonConnection.close();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }


  }

  public JsonObject getAsJsonObject(String message) {
    JsonElement jelement = new JsonParser().parse(new String(message));
    JsonObject jobject = jelement.getAsJsonObject();

    return jobject;
  }


  public void handleMessage(String response) {

    // if (response.has("stream")) {
    //
    // String streamType = response.get("stream").getAsString();
    //
    // switch (streamType) {
    // case "authorization":
    // if (authorizedObject.equals(response)) {
    // LOGGER.debug("Authorized by Alpaca " + response);
    // submitStreamRequest();
    // }
    // break;
    // case "listening":
    // LOGGER.debug("Listening response " + response);
    // break;
    // case "trade_updates":
    //
    // sendStreamMessageToObservers(MessageType.ORDER_UPDATES, response);
    //
    // break;
    // case "account_updates":
    //
    // sendStreamMessageToObservers(MessageType.ACCOUNT_UPDATES, response);
    //
    // break;
    // }
    //
    // } else {
    // LOGGER.error("Invalid message received " + response);
    // }


  }

  /**
   * Send stream message to observers.
   *
   * @param messageType the message type
   * @param message the message
   */
  private synchronized void sendStreamMessageToObservers(MessageType messageType,
      JsonObject message) {

    for (PolygonStreamListener observer : listeners) {

      // UpdateMessage messageObject = getMessageToObject(messageType, message);
      //
      // if (observer.getMessageTypes() == null || observer.getMessageTypes().isEmpty()
      // || observer.getMessageTypes().contains(messageType)) {
      // observer.streamUpdate(messageType, messageObject);
      // }


    }


  }

  /**
   * Gets the message to object.
   *
   * @param messageType the message type
   * @param message the message
   * @return the message to object
   */
  private UpdateMessage getMessageToObject(MessageType messageType, JsonObject message) {

    if (message.has("data")) {
      JsonObject data = message.getAsJsonObject("data");

      switch (messageType) {
        case ACCOUNT_UPDATES:
          AccountUpdateMessage accountUpdateMessage = new AccountUpdateMessage(data);

          return accountUpdateMessage;

        case ORDER_UPDATES:
          OrderUpdateMessage orderUpdateMessage = new OrderUpdateMessage(data);

          return orderUpdateMessage;

      }

    }
    return null;
  }

  /**
   * Submit stream request.
   */
  private void submitStreamRequest() {

  }


  /**
   * Gets the registered stock channel types.
   *
   * @return the registered stock channel types
   */
  public Map<String, Set<ChannelType>> getRegisteredStockChannelTypes() {

    Map<String, Set<ChannelType>> stockChannelTypes = new HashMap<>();

    for (PolygonStreamListener observer : listeners) {

      Map<String, Set<ChannelType>> listenerStockChannelType = observer.getStockChannelTypes();

      if (listenerStockChannelType != null) {
        for (Entry<String, Set<ChannelType>> entry : listenerStockChannelType.entrySet()) {

          if (!stockChannelTypes.containsKey(entry.getKey())) {
            stockChannelTypes.put(entry.getKey(), entry.getValue());
          } else {
            stockChannelTypes.get(entry.getKey()).addAll(entry.getValue());
          }

        }
      }

    }

    return stockChannelTypes;

  }


}
