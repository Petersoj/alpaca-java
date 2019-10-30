package io.github.mainstringargs.util.time;

/**
 * The type Time util.
 */
public class TimeUtil {

    /** The constant PREV_UNIX_EPOCH_NANO_TIME. */
    private static final long PREV_UNIX_EPOCH_NANO_TIME = System.currentTimeMillis() * 1000000L;

    /** The constant PREV_UNIX_EPOCH_NANO_TIME. */
    private static final long PREV_UNIX_EPOCH_MICRO_TIME = PREV_UNIX_EPOCH_NANO_TIME / 1000L;

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
