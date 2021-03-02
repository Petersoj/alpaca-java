package net.jacobpeterson.util.format;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * {@link FormatUtil} is a utility class for various formatting.
 */
public class FormatUtil {

    private static final NumberFormat CURRENCY_FORMATTER = new DecimalFormat("#0.00");
    private static final DateTimeFormatter RFC_3339_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("America/New_York"));
    private static final long PREV_UNIX_EPOCH_NANO_TIME = System.currentTimeMillis() * 1000000L;
    private static final long PREV_UNIX_EPOCH_MICRO_TIME = PREV_UNIX_EPOCH_NANO_TIME / 1000L;

    /**
     * Formats an arbitrary number to a currency format. e.g. $123.45
     *
     * @param numberToFormat the {@link Number} to format
     *
     * @return the formatted string
     */
    public static String toCurrencyFormat(Number numberToFormat) {
        return CURRENCY_FORMATTER.format(numberToFormat);
    }

    /**
     * Formats a {@link TemporalAccessor} using {@link #RFC_3339_FORMATTER} {@link DateTimeFormatter}.
     *
     * @param zonedDateTime the {@link ZonedDateTime}
     *
     * @return the formatted string
     */
    public static String toRFC3339Format(ZonedDateTime zonedDateTime) {
        return RFC_3339_FORMATTER.format(zonedDateTime);
    }

    /**
     * Converts a given unix epoch time in nanoseconds or microseconds to milliseconds if necessary.
     *
     * @param time the time
     *
     * @return the long
     */
    public static long convertToMilli(long time) {
        if (time > PREV_UNIX_EPOCH_NANO_TIME) {
            time /= 1000000L;
        } else if (time > PREV_UNIX_EPOCH_MICRO_TIME) {
            time /= 1000L;
        }
        return time;
    }
}
