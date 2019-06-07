package io.github.mainstringargs.polygon.nats;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.nats.message.AggregatePerMinuteMessage;
import io.github.mainstringargs.polygon.nats.message.AggregatePerSecondMessage;
import io.github.mainstringargs.polygon.nats.message.ChannelMessage;
import io.github.mainstringargs.polygon.nats.message.QuotesMessage;
import io.github.mainstringargs.polygon.nats.message.TradesMessage;
import io.github.mainstringargs.util.concurrency.ExecutorTracer;
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


  /** The polygon options. */
  private Options polygonOptions;

  /** The polygon connection. */
  private Connection polygonConnection;

  /** The polygon dispatcher. */
  private Dispatcher polygonDispatcher;

  /** The current subscriptions. */
  private Map<String, Set<ChannelType>> currentSubscriptions = new HashMap<>();

  /** The all tickers. */
  private static final String ALL_TICKERS = "*";

  /** The Constant executor. */
  private static final ExecutorService executor =
      ExecutorTracer.newSingleThreadExecutor(new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
          return new Thread(r, "PolygonNatsThread");
        }
      });



  /**
   * Instantiates a new polygon nats client.
   *
   * @param keyId the key id
   * @param polygonNatsServers the polygon nats servers
   */
  public PolygonNatsClient(String keyId, String... polygonNatsServers) {
    this(keyId, -1, polygonNatsServers);

  }

  /**
   * Instantiates a new polygon nats client.
   *
   * @param keyId the key id
   * @param maxReconnects the max reconnects
   * @param polygonNatsServers the polygon nats servers
   */
  public PolygonNatsClient(String keyId, int maxReconnects, String... polygonNatsServers) {
    Builder optionsBuilder = new Options.Builder();

    for (String serverUrl : polygonNatsServers) {
      optionsBuilder.server("nats://" + keyId + "@" + serverUrl);
    }

    optionsBuilder.maxReconnects(maxReconnects);

    polygonOptions = optionsBuilder.verbose().build();

    LOGGER.info(
        "Polygon Server set to " + Arrays.toString(polygonNatsServers) + " " + polygonOptions);
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

    updateSubscriptions(listener, true);

  }


  /**
   * Removes the listener.
   *
   * @param listener the listener
   */
  public void removeListener(PolygonStreamListener listener) {


    listeners.remove(listener);

    updateSubscriptions(listener, false);


    if (listeners.isEmpty()) {
      disconnect();
    }

  }


  /**
   * Update subscriptions.
   *
   * @param listener the listener
   * @param isAdd the is add
   */
  private void updateSubscriptions(PolygonStreamListener listener, boolean isAdd) {

    if (listener != null) {
      Map<String, Set<ChannelType>> toRemove = listener.getStockChannelTypes().entrySet().stream()
          .collect(Collectors.toMap(e -> e.getKey(), e -> new HashSet<ChannelType>(e.getValue())));
      Map<String, Set<ChannelType>> toAdd = listener.getStockChannelTypes().entrySet().stream()
          .collect(Collectors.toMap(e -> e.getKey(), e -> new HashSet<ChannelType>(e.getValue())));



      if (isAdd) {

        for (Entry<String, Set<ChannelType>> entry : toAdd.entrySet()) {

          Set<ChannelType> currentSubbedChannels = currentSubscriptions.get(entry.getKey());

          if (currentSubbedChannels == null) {
            currentSubbedChannels = new HashSet<>();
            currentSubscriptions.put(entry.getKey(), currentSubbedChannels);
          }

          for (ChannelType channel : entry.getValue()) {
            String subscriptionName = channel.getAPIName() + '.' + entry.getKey();
            if (!currentSubbedChannels.contains(channel)) {
              currentSubbedChannels.add(channel);

              LOGGER.info(("Subscribing to " + subscriptionName));
              polygonDispatcher.subscribe(subscriptionName);
            } else {
              LOGGER.info(("Already subscribed to " + subscriptionName));
            }


          }

        }


      } else {


        for (PolygonStreamListener polyListener : listeners) {
          for (Entry<String, Set<ChannelType>> entry : polyListener.getStockChannelTypes()
              .entrySet()) {

            if (toRemove.containsKey(entry.getKey())) {

              for (ChannelType cType : entry.getValue()) {
                toRemove.get(entry.getKey()).remove(cType);
              }

            }

          }
        }

        for (Entry<String, Set<ChannelType>> entry : toRemove.entrySet()) {
          for (ChannelType channel : entry.getValue()) {

            if (currentSubscriptions.containsKey(entry.getKey())) {
              currentSubscriptions.get(entry.getKey()).remove(channel);
            }

            String subscriptionName = channel.getAPIName() + "." + entry.getKey();
            LOGGER.info(("Unsubscribing from " + subscriptionName));
            polygonDispatcher.unsubscribe(subscriptionName);


          }
        }
      }
    }
    LOGGER.info(("Subscriptions updated to " + currentSubscriptions));

  }


  /**
   * Connect.
   */
  private void connect() {

    LOGGER.info("Connecting...");

    try {
      polygonConnection = Nats.connect(polygonOptions);


      LOGGER.info("Connected.");

      polygonDispatcher = polygonConnection.createDispatcher((msg) -> {

        executor.execute(new Runnable() {

          @Override
          public void run() {
            String response = new String(msg.getData(), StandardCharsets.UTF_8);

            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("onMessage " + new String(response));

            }

            handleMessage(msg.getSubject(), response);
          }
        });



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

    LOGGER.info("Disconnecting...");


    if (polygonConnection != null) {
      try {
        polygonConnection.close();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      LOGGER.info("Disconnected.");
    }


  }

  /**
   * Gets the as json object.
   *
   * @param message the message
   * @return the as json object
   */
  public JsonObject getAsJsonObject(String message) {
    JsonElement jelement = new JsonParser().parse(new String(message));
    JsonObject jobject = jelement.getAsJsonObject();

    return jobject;
  }


  /**
   * Handle message.
   *
   * @param subject the subject
   * @param response the response
   */
  public void handleMessage(String subject, String response) {



    String[] subjectSplit = subject.split("\\.");
    String apiNameString = subjectSplit[0].trim();
    String tickerString = subjectSplit[1].trim();

    ChannelType cType = ChannelType.fromAPIName(apiNameString);


    ChannelMessage cMessage = getMessageToObject(cType, tickerString, getAsJsonObject(response));

    sendStreamMessageToObservers(tickerString, cType, cMessage);



  }

  /**
   * Send stream message to observers.
   *
   * @param ticker the ticker
   * @param channelType the channel type
   * @param cMessage the c message
   */
  private synchronized void sendStreamMessageToObservers(String ticker, ChannelType channelType,
      ChannelMessage cMessage) {

    for (PolygonStreamListener observer : listeners) {

      boolean sendToObserver = false;

      if (observer.getStockChannelTypes().containsKey(ticker)) {
        if (observer.getStockChannelTypes().get(ticker).contains(channelType)) {
          sendToObserver = true;
        }
      } else if (observer.getStockChannelTypes().containsKey(ALL_TICKERS)) {
        if (observer.getStockChannelTypes().get(ALL_TICKERS).contains(channelType)) {
          sendToObserver = true;
        }
      }

      if (sendToObserver) {
        observer.streamUpdate(ticker, channelType, cMessage);
      }

    }


  }



  /**
   * Gets the message to object.
   *
   * @param cType the c type
   * @param tickerString the ticker string
   * @param asJsonObject the as json object
   * @return the message to object
   */
  private ChannelMessage getMessageToObject(ChannelType cType, String tickerString,
      JsonObject asJsonObject) {
    switch (cType) {
      case QUOTES:
        return new QuotesMessage(cType, tickerString, asJsonObject);

      case TRADES:
        return new TradesMessage(cType, tickerString, asJsonObject);

      case AGGREGATE_PER_MINUTE:
        return new AggregatePerMinuteMessage(cType, tickerString, asJsonObject);

      case AGGREGATE_PER_SECOND:
        return new AggregatePerSecondMessage(cType, tickerString, asJsonObject);

    }
    return null;
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
