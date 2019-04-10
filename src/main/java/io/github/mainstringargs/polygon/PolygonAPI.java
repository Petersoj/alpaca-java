package io.github.mainstringargs.polygon;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.mainstringargs.polygon.nats.PolygonNatsClient;
import io.github.mainstringargs.polygon.nats.PolygonStreamListener;
import io.github.mainstringargs.polygon.properties.PolygonProperties;

/**
 * The Class PolygonAPI.
 */
public class PolygonAPI {

  /** The logger. */
  private static Logger LOGGER = LogManager.getLogger(PolygonAPI.class);

  /** The polygon nats client. */
  private final PolygonNatsClient polygonNatsClient;


  /**
   * Instantiates a new polygon API.
   */
  public PolygonAPI() {

    this(PolygonProperties.KEY_ID_VALUE);
  }

  /**
   * Instantiates a new polygon API.
   *
   * @param keyId the key id
   */
  public PolygonAPI(String keyId) {
    this(PolygonProperties.KEY_ID_VALUE, PolygonProperties.POLYGON_NATS_SERVERS_VALUE);

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
