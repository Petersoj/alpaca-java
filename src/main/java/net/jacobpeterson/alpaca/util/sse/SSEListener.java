package net.jacobpeterson.alpaca.util.sse;

import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link SSEListener} is a listener interface for server-sent events (SSE).
 *
 * @param <T> the data type
 */
public interface SSEListener<T> {

    /**
     * Called on SSE open.
     */
    void onOpen();

    /**
     * Called on SSE close.
     */
    void onClose();

    /**
     * Called on SSE error.
     *
     * @param throwable the {@link Throwable}
     * @param response  the {@link Response}
     */
    void onError(@Nullable Throwable throwable, @Nullable Response response);

    /**
     * Called on SSE message received.
     *
     * @param message the {@link T} message
     */
    void onMessage(@NotNull T message);
}
