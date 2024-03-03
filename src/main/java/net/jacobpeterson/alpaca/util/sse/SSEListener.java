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
     * Called on SSE request open.
     */
    void onOpen();

    /**
     * Called on SSE request close.
     */
    void onClose();

    /**
     * Called on SSE request failure.
     *
     * @param throwable the {@link Throwable}
     * @param response  the {@link Response}
     */
    void onFailure(@Nullable Throwable throwable, @Nullable Response response);

    /**
     * Called on SSE request data receive event.
     *
     * @param data the {@link T} data
     */
    void onEvent(@NotNull T data);
}
