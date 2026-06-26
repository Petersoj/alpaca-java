package net.jacobpeterson.alpacajava.common.sse;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;

@Slf4j
@NullMarked
public class SseListenerAdapter<T> implements SseListener<T> {

    @Override
    public void onData(final T data) {
        LOGGER.info("SSE data: {}", data);
    }

    @Override
    public void onComment(final String comment) {
        LOGGER.debug("SSE comment: {}", comment);
    }

    @Override
    public void onClose() {
        LOGGER.info("SSE closed");
    }

    @Override
    public void onError(final Throwable throwable) {
        LOGGER.error("SSE error", throwable);
    }
}
