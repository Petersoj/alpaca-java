package io.github.mainstringargs.alpaca;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The Class Utilities.
 */
public class Utilities {

  /** The formatter. */
  private static DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"));

  private static NumberFormat numberFormatter = new DecimalFormat("#0.00");

  /**
   * To date time string.
   *
   * @param ldt the ldt
   * @return the string
   */
  public static String toDateTimeString(LocalDateTime ldt) {
    return formatter.format(ldt);
  }

  public static String toDecimalFormat(Number numerToFormat) {
    return numberFormatter.format(numerToFormat);
  }
}
