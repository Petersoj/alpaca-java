package net.jacobpeterson.alpaca.util.sse;

import okhttp3.sse.EventSource;

/**
 * {@link SSERequest} is a {@link EventSource} wrapper class.
 */
public class SSERequest {

    private final EventSource eventSource;

    /**
     * Instantiates a new {@link SSERequest}.
     *
     * @param eventSource the {@link EventSource}
     */
    public SSERequest(EventSource eventSource) {
        this.eventSource = eventSource;
    }

    /**
     * Closes the internal {@link EventSource}.
     */
    public void close() {
        eventSource.cancel();
    }

    /**
     * Gets the internal {@link EventSource}.
     *
     * @return the {@link EventSource}
     */
    public EventSource getInternalEventSource() {
        return eventSource;
    }
}
