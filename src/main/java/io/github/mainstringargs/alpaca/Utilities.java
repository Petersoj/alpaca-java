package io.github.mainstringargs.alpaca;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The Class Utilities.
 */
public class Utilities {

  /** The formatter. */
  static DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"));

  /**
   * To date time string.
   *
   * @param ldt the ldt
   * @return the string
   */
  public static String toDateTimeString(LocalDateTime ldt) {
    return formatter.format(ldt);
  }
}
