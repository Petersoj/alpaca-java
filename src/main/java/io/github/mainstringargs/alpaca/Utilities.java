package io.github.mainstringargs.alpaca;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The Class Utilities.
 */
public class Utilities {

  /** The formatter. */
  private static DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"));


  /** The number formatter. */
  private static NumberFormat numberFormatter = new DecimalFormat("#0.00");

  /**
   * To date time string.
   *
   * @param ldt the ldt
   * @return the string
   */
  public static String toDateTimeString(LocalDateTime ldt) {
    return dateTimeFormatter.format(ldt);
  }

  /**
   * To decimal format.
   *
   * @param numberToFormat the number to format
   * @return the string
   */
  public static String toDecimalFormat(Number numberToFormat) {
    return numberFormatter.format(numberToFormat);
  }

  /**
   * To date string.
   *
   * @param ld the ld
   * @return the string
   */
  public static String toDateString(LocalDate ld) {

    return ld.format(DateTimeFormatter.ISO_DATE);
  }
}
