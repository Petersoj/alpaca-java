package io.github.mainstringargs.polygon;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;
import io.github.mainstringargs.polygon.nats.PolygonNatsClient;
import io.github.mainstringargs.polygon.nats.PolygonStreamListener;

/**
 * The Class PolygonAPI.
 */
public class PolygonAPI {

  /** The logger. */
  private static Logger LOGGER = LogManager.getLogger(PolygonAPI.class);

  /** The polygon nats client. */
  private final PolygonNatsClient polygonNatsClient;

  /** The Constant POLYGON_NATS_SERVERS. */
  public final static String[] POLYGON_NATS_SERVERS =
      new String[] {"nats1.polygon.io:31101", "nats2.polygon.io:31102", "nats3.polygon.io:31103"};

  /**
   * Instantiates a new polygon API.
   */
  public PolygonAPI() {

    this(AlpacaProperties.KEY_ID_VALUE);
  }

  /**
   * Instantiates a new polygon API.
   *
   * @param keyId the key id
   */
  public PolygonAPI(String keyId) {
    this(AlpacaProperties.KEY_ID_VALUE, POLYGON_NATS_SERVERS);

  }

  /**
   * Instantiates a new polygon API.
   *
   * @param keyId the key id
   * @param polygonNatsServers the polygon nats servers
   */
  public PolygonAPI(String keyId, String... polygonNatsServers) {

    LOGGER.info("PolygonAPI is using the following properties: \nkeyId: " + keyId
        + "\npolygonNatsServers " + Arrays.toString(polygonNatsServers));

    polygonNatsClient = new PolygonNatsClient(keyId, polygonNatsServers);

  }

  /**
   * Adds the polygon stream listener.
   *
   * @param streamListener the stream listener
   */
  public void addPolygonStreamListener(PolygonStreamListener streamListener) {
    polygonNatsClient.addListener(streamListener);
  }


  /**
   * Removes the polygon stream listener.
   *
   * @param streamListener the stream listener
   */
  public void removePolygonStreamListener(PolygonStreamListener streamListener) {
    polygonNatsClient.removeListener(streamListener);
  }

}
