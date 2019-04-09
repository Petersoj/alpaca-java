package io.github.mainstringargs.polygon.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;

/**
 * The Class AlpacaProperties.
 */
public class PolygonProperties {

  /** The property file. */
  private static Properties propertyFile;


  static {
    InputStream is = PolygonProperties.class.getResourceAsStream("/polygon.properties");
    propertyFile = new Properties();
    try {
      propertyFile.load(is);
    } catch (IOException e) {
      e.printStackTrace();
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
  public static final String INVALID_VALUE = "INVALID";

  /** The Constant KEY_ID_VALUE. */
  public static final String KEY_ID_VALUE = getProperty(KEY_ID_KEY, AlpacaProperties.KEY_ID_VALUE);


  /** The base data url value. */
  public static String BASE_DATA_URL_VALUE = getProperty(BASE_DATA_URL_KEY, DEFAULT_DATA_URL);

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
    return propertyFile.getProperty(key, defaultValue).trim();
  }

}
