package io.github.mainstringargs.util.time;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The Class Utilities.
 */
public class TimeUtil {

    /** The constant PREV_UNIX_EPOCH_NANO_TIME. */
    private static final long PREV_UNIX_EPOCH_NANO_TIME = System.currentTimeMillis() * 1000000L;
    /** The constant PREV_UNIX_EPOCH_NANO_TIME. */
    private static final long PREV_UNIX_EPOCH_MICRO_TIME = PREV_UNIX_EPOCH_NANO_TIME / 1000L;
    /** The date time formatter. */
    private static DateTimeFormatter inputDateTimeFormatter = DateTimeFormatter.ofPattern(
            "[yyyyMMdd][yyyy-MM-dd][yyyy-DDD]['T'[HHmmss][HHmm][HH:mm:ss][HH:mm][.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSS][.SS][.S]][OOOO][O][z][XXXXX][XXXX]['['VV']']");
    /** The output date time formatter. */
    private static DateTimeFormatter outputDateTimeFormatter =
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
        ZonedDateTime ldtZoned = ldt.atZone(ZoneId.systemDefault());

        ZonedDateTime localTimeZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));

        return outputDateTimeFormatter.format(localTimeZoned);
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

    /**
     * From date time string.
     *
     * @param dateTimeString the date time string
     * @return the local date time using the system time zone
     */
    public static LocalDateTime fromDateTimeString(String dateTimeString) {
        LocalDateTime ldt = LocalDateTime.parse(dateTimeString, inputDateTimeFormatter);

        ZonedDateTime ldtZoned;

        if (dateTimeString.endsWith("Z")) {
            ldtZoned = ldt.atZone(ZoneId.of("UTC"));
        } else {
            ldtZoned = ldt.atZone(ZoneId.of("America/New_York"));
        }

        ZonedDateTime localTimeZoned = ldtZoned.withZoneSameInstant(ZoneId.systemDefault());

        return localTimeZoned.toLocalDateTime();
    }

    /**
     * From date string.
     *
     * @param dateString the date string
     * @return the local date
     */
    public static LocalDate fromDateString(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
    }

    /**
     * Converts a given unix epoch time in nanoseconds or microseconds to milliseconds if necessary.
     *
     * @param time the time
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
