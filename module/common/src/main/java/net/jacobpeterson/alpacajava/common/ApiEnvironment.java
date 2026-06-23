package net.jacobpeterson.alpacajava.common;

import org.jspecify.annotations.NullMarked;

/**
 * {@link ApiEnvironment} is an enum that represents the API environment to use.
 */
@NullMarked
public enum ApiEnvironment {

    /**
     * Production/live environment.
     */
    PRODUCTION,

    /**
     * Development/sandbox/paper environment.
     */
    DEVELOPMENT
}
