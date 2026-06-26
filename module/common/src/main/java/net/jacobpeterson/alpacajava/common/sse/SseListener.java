package net.jacobpeterson.alpacajava.common.sse;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SseListener<T> {

    void onData(final T data);

    void onComment(final String comment);

    void onClose();

    void onError(final Throwable throwable);
}
