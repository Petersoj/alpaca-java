package net.jacobpeterson.alpacajava.common.sse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@NullMarked
public class SseResponse implements AutoCloseable {

    private static final String DATA_PREFIX = "data: ";
    private static final String COMMENT_PREFIX = ": ";

    private final @Getter int statusCode;
    private final @Getter HttpHeaders headers;
    private final InputStream body;
    private volatile boolean close;

    public <T> SseResponse(final HttpClient httpClient, final HttpResponse<InputStream> response,
            final SseListener<T> sseListener, final Function<String, T> dataFromJson) {
        statusCode = response.statusCode();
        headers = response.headers();
        body = response.body();
        httpClient.executor().orElse(Thread::startVirtualThread).execute(() -> {
            try (final var reader = new BufferedReader(new InputStreamReader(body, UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(DATA_PREFIX)) {
                        sseListener.onData(dataFromJson.apply(line.substring(DATA_PREFIX.length())));
                    } else if (!line.isBlank()) {
                        sseListener.onComment(line.startsWith(COMMENT_PREFIX) ?
                                line.substring(COMMENT_PREFIX.length()) : line);
                    }
                }
            } catch (final Throwable throwable) {
                if (!close && !(throwable instanceof IOException)) {
                    sseListener.onError(throwable);
                }
            } finally {
                sseListener.onClose();
            }
        });
    }

    @Override
    public void close() {
        close = true;
        try {
            body.close();
        } catch (final IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }
}
