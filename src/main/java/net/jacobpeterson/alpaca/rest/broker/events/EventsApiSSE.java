package net.jacobpeterson.alpaca.rest.broker.events;

import com.google.gson.reflect.TypeToken;
import net.jacobpeterson.alpaca.openapi.broker.ApiClient;
import net.jacobpeterson.alpaca.openapi.broker.ApiException;
import net.jacobpeterson.alpaca.openapi.broker.api.EventsApi;
import net.jacobpeterson.alpaca.openapi.broker.model.AccountStatusEvent;
import net.jacobpeterson.alpaca.openapi.broker.model.JournalStatusEvent;
import net.jacobpeterson.alpaca.openapi.broker.model.SubscribeToAdminActionSSE200ResponseInner;
import net.jacobpeterson.alpaca.openapi.broker.model.TradeUpdateEvent;
import net.jacobpeterson.alpaca.openapi.broker.model.TradeUpdateEventV2;
import net.jacobpeterson.alpaca.openapi.broker.model.TransferStatusEvent;
import net.jacobpeterson.alpaca.util.sse.SSEListener;
import net.jacobpeterson.alpaca.util.sse.SSERequest;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jacobpeterson.alpaca.openapi.broker.JSON.getGson;

/**
 * {@link EventsApiSSE} add SSE support to {@link EventsApi}.
 */
public class EventsApiSSE {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsApiSSE.class);

    private final EventsApi eventsAPI;
    private final EventSource.Factory eventSourceFactory;

    /**
     * Instantiates a new {@link EventsApiSSE}.
     *
     * @param apiClient the api client
     */
    public EventsApiSSE(ApiClient apiClient) {
        eventsAPI = new EventsApi(apiClient);
        eventSourceFactory = EventSources.createFactory(apiClient.getHttpClient().newBuilder() // Shallow clone
                .readTimeout(0, SECONDS)
                .writeTimeout(0, SECONDS)
                .build());
    }

    /**
     * See {@link EventsApi#getV1EventsNta(String, String, String, Integer, Integer, String, String, Boolean)}.
     *
     * @return a {@link SSERequest}
     */
    public SSERequest subscribeToNonTradingActivitiesEvents(String id, String since, String until, Integer sinceId,
            Integer untilId, String sinceUlid, String untilUlid, Boolean includePreprocessing,
            SSEListener<Object> sseListener) throws ApiException { // TODO OpenAPI response type is broken
        final Request request = eventsAPI.getV1EventsNtaCall(id, since, until, sinceId, untilId, sinceUlid, untilUlid,
                includePreprocessing, null).request();
        return new SSERequest(eventSourceFactory.newEventSource(request, createEventSourceListener(sseListener,
                new TypeToken<Object>() {}.getType()))); // TODO OpenAPI response type is broken
    }

    /**
     * See {@link EventsApi#subscribeToAdminActionSSE(OffsetDateTime, OffsetDateTime, String, String)}.
     *
     * @return a {@link SSERequest}
     */
    public SSERequest subscribeToAdminAction(OffsetDateTime since, OffsetDateTime until, String sinceId,
            String untilId, SSEListener<AccountStatusEvent> sseListener) throws ApiException {
        final Request request = eventsAPI.subscribeToAdminActionSSECall(since, until, sinceId, untilId, null).request();
        return new SSERequest(eventSourceFactory.newEventSource(request, createEventSourceListener(sseListener,
                new TypeToken<SubscribeToAdminActionSSE200ResponseInner>() {}.getType())));
    }

    /**
     * See
     * {@link EventsApi#subscribeToJournalStatusSSE(OffsetDateTime, OffsetDateTime, Integer, Integer, String, String,
     * String)}.
     *
     * @return a {@link SSERequest}
     */
    public SSERequest subscribeToJournalStatus(OffsetDateTime since, OffsetDateTime until, Integer sinceId,
            Integer untilId, String sinceUlid, String untilUlid, String id,
            SSEListener<AccountStatusEvent> sseListener) throws ApiException {
        final Request request = eventsAPI.subscribeToJournalStatusSSECall(since, until, sinceId, untilId, sinceUlid,
                untilUlid, id, null).request();
        return new SSERequest(eventSourceFactory.newEventSource(request, createEventSourceListener(sseListener,
                new TypeToken<JournalStatusEvent>() {}.getType())));
    }

    /**
     * See {@link EventsApi#subscribeToTradeSSE(OffsetDateTime, OffsetDateTime, Integer, Integer, String, String)}.
     *
     * @return a {@link SSERequest}
     */
    public SSERequest subscribeToTrade(OffsetDateTime since, OffsetDateTime until, Integer sinceId,
            Integer untilId, String sinceUlid, String untilUlid, SSEListener<AccountStatusEvent> sseListener)
            throws ApiException {
        final Request request = eventsAPI.subscribeToTradeSSECall(since, until, sinceId, untilId, sinceUlid, untilUlid,
                null).request();
        return new SSERequest(eventSourceFactory.newEventSource(request, createEventSourceListener(sseListener,
                new TypeToken<TradeUpdateEvent>() {}.getType())));
    }

    /**
     * See {@link EventsApi#subscribeToTradeV2SSE(OffsetDateTime, OffsetDateTime, String, String)}.
     *
     * @return a {@link SSERequest}
     */
    public SSERequest subscribeToTradeV2(OffsetDateTime since, OffsetDateTime until, String sinceId,
            String untilId, SSEListener<AccountStatusEvent> sseListener) throws ApiException {
        final Request request = eventsAPI.subscribeToTradeV2SSECall(since, until, sinceId, untilId, null).request();
        return new SSERequest(eventSourceFactory.newEventSource(request, createEventSourceListener(sseListener,
                new TypeToken<TradeUpdateEventV2>() {}.getType())));
    }

    /**
     * See
     * {@link EventsApi#subscribeToTransferStatusSSE(OffsetDateTime, OffsetDateTime, Integer, Integer, String,
     * String)}.
     *
     * @return a {@link SSERequest}
     */
    public SSERequest subscribeToTransferStatus(OffsetDateTime since, OffsetDateTime until, Integer sinceId,
            Integer untilId, String sinceUlid, String untilUlid, SSEListener<AccountStatusEvent> sseListener)
            throws ApiException {
        final Request request = eventsAPI.subscribeToTransferStatusSSECall(since, until, sinceId, untilId, sinceUlid,
                untilUlid, null).request();
        return new SSERequest(eventSourceFactory.newEventSource(request, createEventSourceListener(sseListener,
                new TypeToken<TransferStatusEvent>() {}.getType())));
    }

    /**
     * See
     * {@link EventsApi#suscribeToAccountStatusSSE(LocalDate, LocalDate, Integer, Integer, String, String, String)}.
     *
     * @return a {@link SSERequest}
     */
    public SSERequest subscribeToAccountStatus(LocalDate since, LocalDate until, Integer sinceId, Integer untilId,
            String sinceUlid, String untilUlid, String id, SSEListener<AccountStatusEvent> sseListener)
            throws ApiException {
        final Request request = eventsAPI.suscribeToAccountStatusSSECall(since, until, sinceId, untilId, sinceUlid,
                untilUlid, id, null).request();
        return new SSERequest(eventSourceFactory.newEventSource(request, createEventSourceListener(sseListener,
                new TypeToken<AccountStatusEvent>() {}.getType())));
    }

    private <T> EventSourceListener createEventSourceListener(SSEListener<T> sseListener, Type responseTypeToken) {
        return new EventSourceListener() {
            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                LOGGER.info("Event source closed: eventSource={}", eventSource);
                sseListener.onClose();
            }

            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type,
                    @NotNull String data) {
                LOGGER.trace("Event source event: eventSource={} id={}, type={}, data={}", eventSource, id, type, data);
                sseListener.onMessage(getGson().fromJson(data, responseTypeToken));
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable throwable,
                    @Nullable Response response) {
                if (throwable != null && throwable.getMessage().equals("canceled")) {
                    sseListener.onClose();
                    return;
                }
                LOGGER.error("Event source failure: eventSource={} throwable={}, response={}",
                        eventSource, throwable, response);
                sseListener.onError(throwable, response);
            }

            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                LOGGER.info("Event source opened: {}", eventSource);
                sseListener.onOpen();
            }
        };
    }
}
