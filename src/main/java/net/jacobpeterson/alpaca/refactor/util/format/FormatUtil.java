package net.jacobpeterson.alpaca.refactor.util.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * {@link FormatUtil} is a utility class for various formatting.
 */
public class FormatUtil {

    // Alpaca uses the following rounding mechanics with respect to buy orders: (1) rounded down to two decimal
    // places if the last trade price is over $1.00; otherwise, rounded down to four decimal places, hence the '#' in
    // the 3rd and 4th least significant decimal digits.
    private static final NumberFormat CURRENCY_FORMATTER = new DecimalFormat("#0.00##",
            DecimalFormatSymbols.getInstance(Locale.US));
    private static final DateTimeFormatter RFC_3339_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("America/New_York"));

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
}
