package io.github.mainstringargs.polygon.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;

/**
 * The Class AlpacaProperties.
 */
public class PolygonProperties {

  /** The property file. */
  private static LinkedHashSet<Properties> propertyFiles = new LinkedHashSet<>();


  static {

    Enumeration<URL> urls = null;
    try {
      urls = ClassLoader.getSystemClassLoader().getResources("polygon.properties");
    } catch (IOException e2) {
      e2.printStackTrace();
    }

    while (urls.hasMoreElements()) {
      InputStream is = null;
      try {
        URL url = urls.nextElement();
        is = (url.openStream());
      } catch (IOException e1) {
        e1.printStackTrace();
      }

      Properties propertyFile = new Properties();
      try {
        propertyFile.load(is);
      } catch (IOException e) {
        e.printStackTrace();
      }

      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      propertyFiles.add(propertyFile);
    }
  }


  /** The Constant KEY_ID_KEY. */
  private static final String KEY_ID_KEY = "key_id";

  /** The Constant BASE_DATA_URL_KEY. */
  private static final String BASE_DATA_URL_KEY = "base_data_url";

  /** The Constant USER_AGENT_KEY. */
  private static final String USER_AGENT_KEY = "user_agent";

  /** The Constant DEFAULT_USER_AGENT. */
  private static final String DEFAULT_USER_AGENT =
      "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36";

  /** The Constant DEFAULT_DATA_URL. */
  private static final String DEFAULT_DATA_URL = "https://api.polygon.io";

  /** The Constant INVALID_VALUE. */
  public static final String INVALID_VALUE = "<PLACEHOLDER>";

  /** The Constant KEY_ID_VALUE. */
  public static final String KEY_ID_VALUE = getProperty(KEY_ID_KEY, AlpacaProperties.KEY_ID_VALUE);

  /** The base data url value. */
  public static String BASE_DATA_URL_VALUE = getProperty(BASE_DATA_URL_KEY, DEFAULT_DATA_URL);

  /** The polygon nats servers key. */
  private static String POLYGON_NATS_SERVERS_KEY = "nats_urls";

  /** The default polygon nats servers. */
  private static String DEFAULT_POLYGON_NATS_SERVERS =
      "nats1.polygon.io:31101,nats2.polygon.io:31102,nats3.polygon.io:31103";

  /** The polygon nats servers value. */
  public static String[] POLYGON_NATS_SERVERS_VALUE =
      getProperty(POLYGON_NATS_SERVERS_KEY, DEFAULT_POLYGON_NATS_SERVERS).split(",");

  /** The Constant USER_AGENT_VALUE. */
  public static final String USER_AGENT_VALUE = getProperty(USER_AGENT_KEY, DEFAULT_USER_AGENT);

  /**
   * Gets the property.
   *
   * @param key the key
   * @param defaultValue the default value
   * @return the property
   */
  public static String getProperty(String key, String defaultValue) {

    for (Properties prop : propertyFiles) {

      if (prop.containsKey(key)) {
        String propertyVal = prop.getProperty(key);
        if (!propertyVal.equals(INVALID_VALUE)) {
          return propertyVal;
        }
      }
    }

    return defaultValue;
  }


}
