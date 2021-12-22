package net.jacobpeterson.alpaca.rest.endpoint.clock;

import net.jacobpeterson.alpaca.model.endpoint.clock.Clock;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.AlpacaEndpoint;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * {@link AlpacaEndpoint} for <a href="https://docs.alpaca.markets/api-documentation/api-v2/clock/">Clock</a>.
 */
public class ClockEndpoint extends AlpacaEndpoint {

    /**
     * Instantiates a new {@link ClockEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public ClockEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "clock");
    }

    /**
     * Returns the market {@link Clock}.
     *
     * @return the market {@link Clock}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Clock get() throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, Clock.class);
    }
}
